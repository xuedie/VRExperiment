package ca.yorku.cse.mack.fittstouch;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * <h1>FittsTouch</h1>
 *
 * <h3>Summary</h3>
 *
 * <ul>
 * <li>
 * Android application for evaluating touch-based target selection using Fitts' law.
 * <p>
 *
 * <li>Implements both the two-dimensional (2D) and one-dimensional (1D) Fitts' law tasks described
 * in ISO 9241-9 (updated in 2012 as ISO/TC 9241-411).
 * <p>
 *
 * <li>User performance data gathered and saved in output files for follow-up analyses.
 * <p>
 * </ul>
 *
 * <h3>Related References</h3>
 *
 * The following publication presents research where this software was used.
 * <p>
 *
 * <ul>
 * <li>Fitts' throughput and the remarkable case of touch-based target selection, by MacKenzie (in
 * press)
 * </ul>
 * <p>
 *
 * The following publications provide background information on Fitts' law and experimental testing
 * using the Fitts' paradigm.
 * <p>
 *
 * <ul>
 * <li><a href="http://www.yorku.ca/mack/ijhcs2004.pdf">Towards a standard for pointing device
 * evaluation: Perspectives on 27 years of Fitts' law research in HCI</a>, by Soukoreff and
 * MacKenzie (<i>IJHCS 2004</i>).
 * <p>
 *
 * <li><a href="http://www.yorku.ca/mack/HCI.html">Fitts' law as a research and design tool in
 * human-computer interaction</a>, by MacKenzie (<i>HCI 1992</i>).
 * <p>
 * </ul>
 * <p>
 *
 * <h3>Setup Parameters</h3>
 *
 * Upon launching, the program presents a setup dialog:
 * <p>
 * <center><a href="FittsTouch-1.jpg"><img src="FittsTouch-1.jpg" width="200"></a></center>
 * <p>
 * </center>
 *
 * The parameters are embedded in the application. The default settings (shown) may be changed by
 * selecting the corresponding spinner. Changes may be saved. Saved changes become the default
 * settings when the application is next launched.
 * <p>
 *
 * The setup parameters are as follows:
 * <p>
 *
 * <blockquote>
 * <table border="1" cellspacing="0" cellpadding="6">
 * <tr bgcolor="#cccccc">
 * <th>Parameter
 * <th>Description
 *
 * <tr>
 * <td valign="top">Participant code
 * <td>Identifies the current participant.
 * <p>
 *
 * <tr>
 * <td valign="top">Session code
 * <td>Identifies the session. This code is useful if testing proceeds over multiple sessions to
 * gauge the progression of learning.
 * <p>
 *
 * <tr>
 * <td valign="top">Block code (auto)
 * <td>Identifies the block of testing. This code is generated automatically. The first block of
 * testing is "B01", then "B02", and so on. Output data files include the block code in the
 * filename. The first available block code is used in opening data files for output. This prevents
 * overwriting data from an earlier block of testing.
 * <p>
 *
 * <tr>
 * <td valign="top">Group code
 * <td>Identifies the group to which the participant was assigned. This code is needed if
 * counterbalancing was used (i.e., participants were assigned to groups to offset order effects).
 * This is common practice for testing the levels of a within-subjects independent variable.
 * <p>
 *
 * <tr>
 * <td valign="top">Condition code
 * <td>An arbitrary code to associate a test condition with a block of trials. This parameter might
 * be useful if the user study includes conditions that are not inherently part of the application
 * (e.g., Gender &rarr; male, female; User stance &rarr; sitting, standing, walking).
 * <p>
 *
 * <tr>
 * <td valign="top">Mode
 * <td>Set to either "1D" or "2D" to control whether the task is one-dimensional or two-dimensional.
 * <p>
 *
 * <tr>
 * <td colspan=2 valign=center>
 * NOTE: The setup parameters above appear in the filename for the output data files (e.g.,
 * <code>FittsTouch-P01-S01-B01-G01-C01-1D.sd1</code>). They also appear as data columns in the
 * output data files.
 *
 * <tr>
 * <td valign="top">Number of trials (1D)
 * <td>Specifies the number of back-and-forth selections in a block of trials. This setup parameter
 * is only relevant if Mode = 1D.
 * <p>
 *
 * <tr>
 * <td valign="top">Number of targets (2D)
 * <td>Specifies the number of targets that appear in the layout circle. This setup parameter is
 * only relevant if Mode = 2D.
 * <p>
 *
 * <tr>
 * <td valign="top">Target amplitude (A)
 * <td>Specifies either the diameter of the layout circle (2D) or the center-to-center distance
 * between targets (1D). The spinner offers three choices: "120, 240, 480", "250, 500", or "500"
 * (but see note 2 below).
 * <p>
 *
 * <tr>
 * <td valign="top">Target width (W)
 * <td>Specifies the width of targets. This is either the diameter of the target circles (2D) or the
 * width of the rectangles (1D). The spinner offers three choices: "60, 100", "30, 60, 120", or
 * "100",
 * <p>
 *
 * Notes:<br>
 * 1. The total number of <i>A-W</i> conditions (sequences) in a block is <i>n &times; m</i>, where
 * <i>n</i> is the number of target amplitudes and <i>m</i> is the number of target widths.<br>
 * 2. The <i>A-W</i> values are scaled such that the widest condition (largest A, largest W) spans
 * the device's display with minus 10 pixels on each side.
 * <p>
 *
 * <tr>
 * <td valign="top">Vibrotactile feedback
 * <td>A checkbox parameter. If checked, a 10 ms vibrotactile pulse is emitted if a target is
 * selected in error (i.e., the finger tap is outside the target).
 * <p>
 *
 * <tr>
 * <td valign="top">Audio feedback
 * <td>A checkbox parameter. If checked, an auditory beep is heard if a target is selected in error
 * (i.e., the finger tap is outside the target).
 * <p>
 * </table>
 * </blockquote>
 *
 * <h3>Operation</h3>
 *
 * Once the setup parameters are chosen, the testing begins by tapping "OK". The first screen to
 * appear is a transition screen to ensure the participant is ready (below, left). Once the blue
 * circle is tapped, the first test condition appears. Example are shown below for the 2D task
 * (below, center) and the 1D task (below, right).
 * <p>
 *
 * <center> <a href="FittsTouch-2.jpg"><img src="FittsTouch-2.jpg"
 * height=300></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="FittsTouch-3.jpg"><img
 * src="FittsTouch-3.jpg" height=300></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="FittsTouch-4.jpg"><img src="FittsTouch-4.jpg" height=300></a> </center>
 * <p>
 *
 * The participant selects targets by tapping with a finger. The target to tap is highlighted. Upon
 * selection, the highlight moves to a target on the opposite side of the layout circle (2D) or to
 * the opposing target (1D). Tapping continues until all targets are selected (2D) or until the
 * specified number of trials is completed (1D).
 * <p>
 *
 * The first tap to start a sequence must be correctly on the initial highlighted target. After a
 * sequence is started, errors are permitted. If the user attempts to tap a target and the selection
 * coordinate on finger-lift is outside the target, an error occurs. Vibrotactile or auditory
 * feedback is emitted if the corresponding setup parameter was checked. Whether the target was hit
 * or missed, the highlight moves to the next target.
 * <p>
 *
 * A series of trials for a single <i>A-W</i> condition is called a "sequence". At the end of each
 * sequence, results appear on the display. See below:
 * <p>
 *
 * <center><a href="FittsTouch-5.jpg"><img src="FittsTouch-5.jpg" height="400"></a></center>
 * <p>
 *
 * Once all the <i>A-W</i> conditions in a block are finished, the application terminates. User
 * performance data are saved files for follow-up analyses. The data files are located in the
 * device's public storage directory in a folder named <code>FittsTouchData</code>.
 * <p>
 *
 *
 * <h3>Output Data Files</h3>
 *
 * For each block of testing, two output data files are created: sd1 and sd2. ("sd" is for
 * "summary data".) The data are comma delimited for easy importing into a spreadsheet or statistics
 * program.
 * <p>
 *
 * <h4>sd1 Output File</h4>
 *
 * The sd1 file contains the following summary data for each trial:
 * <p>
 *
 * <pre>
 *      Participant - participant code
 *      Session - session code
 *      Block - block code
 *      Group - group code
 *      Condition - condition code
 *      Mode - mode code (Tap or Drag)
 *      Trial - trial number
 *      A - target amplitude (N/A)
 *      W - target width
 *      FromX - x coordinate of center of from-target
 *      FromY - y coordinate of center of from-target
 *      TargetX - x coordinate of center of to-target
 *      TargetY - y coordinate of center of to-target
 *      FingerDownX - x coordinate of finger-down event at end of trial
 *      FingerDownY - y coordinate of finger-down event at end of trial
 *      FingerUpX - x coordinate of finger-up event at end of trial (target selection)
 *      FingerUpY - y coordinate of finger-up event at end of trial (target selection)
 *      xDelta - (see below)
 *      FingerDownUpDelta - Pythagorean distance between finger-down and finger-up events
 *      FingerDownUpTime - time in ms between finger-down and finger-up events
 *      DistanceFromTargetCenter - Pythagorean distance from selection coordinate to target center
 *      Miss - 0 = target selected, 1 = target missed
 *      MT - movement time in ms for the trial
 * </pre>
 *
 * Note: All sizes, distances, and coordinates are in pixel units for the test device.
 * <p>
 *
 * <code>xDelta</code> is the <i>x</i>-distance from the selection coordinate to the center of the
 * target. It is normalized relative to the center of the target and to the task axis. For example,
 * <code>xDelta</code> = 1 is the equivalent of a one-pixel overshoot while <code>xDelta</code> =
 * &minus;1 is the equivalent of a one-pixel undershoot. Note that <code>xDelta</code> = 0 does not
 * mean selection was precisely at the centre of the target. It means the selection was on a line
 * orthogonal to the task axis going through the centre of the target. This is consistent with the
 * inherently one-dimensional nature of Fitts' law.
 * <p>
 *
 * <code>xDelta</code> is important for calculating Fitts' throughput. The standard deviation in the
 * <code>xDelta</code> values collected over a sequence of trials is <i>SD</i><sub>x</sub>. This is
 * used in the calculation of throughput (<i>TP</i>) as follows:
 * <p>
 *
 * <blockquote> <i>W</i><sub>e</sub> = 4.133 &times; <i>SD</i><sub>x</sub>
 * <p>
 *
 * <i>ID</i><sub>e</sub> = log<sub>2</sub>(<i>A</i><sub>e</sub> / <i>W</i><sub>e</sub> + 1)
 * <p>
 *
 * <i>TP</i> = <i>ID</i><sub>e</sub> / <i>MT</i>
 * <p>
 * </blockquote>
 *
 * <h4>sd2 Output File</h4>
 *
 * The sd2 file contains summary data for a sequence of trials:
 *
 * <pre>
 *      Participant - participant code
 *      Session - session code
 *      Block - block code
 *      Group - group code
 *      Condition - condition code
 *      Mode - mode code (Tap or Drag)
 *      Trials - number of trials in the sequence
 *      SequenceRepeatCount - number of times the sequence was repeated (see below)
 *      A - specified target amplitude
 *      W - specified target width
 *      ID - specified index of difficulty
 *      Ae - actual or effective movement amplitude
 *      We - actual or effective target width
 *      IDe - actual or effective index of difficulty
 *      MT - mean movement time in ms over all trials in the sequence
 *      ErrorRate - error rate (%)
 *      TP - Fitts' throughput in bits per second
 * </pre>
 *
 *
 * The <code>SequenceRepeatCount</code> is used in conjunction with an outlier criterion. Any trial
 * where the actual distance traversed is less the &frac12; the specified amplitude is deemed an
 * outlier. The most likely cause of an outlier is either an inadvertent double-tap or a missed tap.
 * A sequence with 1 or more outliers is an outlier sequence and is repeated. If this occurs, an
 * alert popup appears at the end of the sequence to inform the participant. No data are saved for
 * an outlier sequence. However, the <code>SequenceRepeatCount</code> entry in the sd2 file
 * indicates the number of times the sequence was repeated due to the outlier criterion. Usually,
 * <code>SequenceRepeatCount</code> = 0 (hopefully!).
 * <p>
 *
 * The last six entries in the sd2 file are user performance measures. These reflect how the user
 * actually performed while doing the sequence of trials. The measures most commonly used as
 * dependent variables in Fitts' law experiments are the last three: movement time, error rate, and
 * throughput.
 * <p>
 *
 * The following are examples of "sd" (summary data) files:
 * <p>
 *
 * <ul>
 * <li><a href="FittsTouch-sd1-example.txt">sd1 example</a>
 * <li><a href="FittsTouch-sd2-example.txt">sd2 example</a>
 * </ul>
 * <p>
 *
 * Actual output files use "FittsTouch" as the base filename. This is followed by the participant
 * code, the session code, the block code, the group code, the condition code, and the mode, for
 * example, <code>FittsTouch-P01-S01-B01-G01-C01-1D.sd1</code>.
 * <p>
 *
 * In most cases, the sd2 data files are the primary files used for data analyses in an experimental
 * evaluation. The data in the sd2 files are full-precision, comma-delimited, to facilitate
 * importing into a spreadsheet or statistics application. Below is an example for the sd2 file
 * above, after importing into Microsoft <i>Excel</i>: (click to enlarge)
 * <p>
 *
 * <center> <a href="FittsTouch-6.jpg"><img src="FittsTouch-6.jpg" width=1000></a> </center>
 * <p>
 *
 * When using this application in an experiment, it is a good idea to terminate all other
 * applications and to disable the system's WiFi and Bluetooth transceivers. This will maintain the
 * integrity of the data collected and ensure that the application runs without hesitations.
 * <p>
 *
 * @author (c) Scott MacKenzie, 2013-2015
 */

public class FittsActivity extends Activity
{
    final String MYDEBUG = "MYDEBUG";

    final String DATA_DIRECTORY = "/FittsTouchData/";
    final String APP = "FittsTouch";

    final String SD1_HEADER = "Participant,Session,Block,Group,Condition,CurrentCombination," +
            "TrialIdx,BlockIdx,FromX,FromY,TargetX,TargetY," +
            "TargetZoneNumber,FingerDownX,FingerDownY,FingerUpX,FingerUpY,FingerDownUpTime(ms)," +
            "DistanceFromTargetCenter,IsMiss,IsFingerDownMiss,IsFingerUpMiss,TrialTime(ms)\n";
    final String SD2_HEADER = "App,Participant,Session,Block,Group,Condition,Mode,Trials,SequenceRepeatCount,A,W,ID," +
            "Ae,We,IDe,MT(ms),ErrorRate(%),TP(bps)\n";
    final float TWO_TIMES_PI = 6.283185307f;
    final int VIBRATION_PULSE_DURATION = 10;

    ExperimentPanel expPanel;
    String participantCode, sessionCode, blockCode, groupCode;
    int conditionCode;
    String moveMode;
    boolean vibrotactileFeedback, auditoryFeedback;
    int numberOfTrials, numberOfTargets, outlierSequenceCount, trialMissCount;
    int numberOfSessions, numberOfTargetColumns, numberOfTargetRows;
    BufferedWriter sd1, sd2;
    File f1, f2;
    int screenOrientation;
    String sd2Leader;

    float xCenter, yCenter, screenHeight;
    float xFingerDown, yFingerDown;
    long fingerDownTime, trialStartTime, now, sequenceStartTime;
    boolean even, sequenceStarted, waitTargetSelected;
    int blockIdx, selectionCount, trialMiss, fingerUpMiss, fingerDownMiss;
    int[] targetOrders;
    int numberOfCombinations = 3;
    int combinationInd;
    int[] combinationOrders;
    String[] trialOrders;
    Vibrator vib;
    MediaPlayer missSound, changeFingerSound, completeSound;
    StringBuilder sb1, sb2, results;

    // new stuff to streamline calculation of Throughput
    PointF[] from;
    PointF[] to;
    PointF[] select;
    float[] mtArray;
    // -----

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        setContentView(R.layout.main);

        // init study parameters
        Bundle b = getIntent().getExtras();
        participantCode = b.getString("participantCode");
        sessionCode = b.getString("sessionCode");
        blockCode = "B01"; // always start here
        groupCode = b.getString("groupCode");
        conditionCode = b.getInt("conditionCode") + 1;
        moveMode = b.getString("mode");
        numberOfSessions = b.getInt("numberOfTrials") + 1;
        numberOfTargetColumns = b.getInt("numberOfTargetColumns");
        numberOfTargetRows = b.getInt("numberOfTargetRows");
        numberOfTargets = numberOfTargetColumns * numberOfTargetRows;
        vibrotactileFeedback = b.getBoolean("vibrotactileFeedback");
        auditoryFeedback = b.getBoolean("auditoryFeedback");
        screenOrientation = b.getInt("screenOrientation");

        initializeFile();

        expPanel = (ExperimentPanel)findViewById(R.id.experimentpanel);
        expPanel.waitStartCircleSelect = true;

        // determine screen width and height
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        xCenter = screenWidth / 2f;
        yCenter = screenHeight / 2f;

        expPanel.panelWidth = screenWidth;
        expPanel.panelHeight = screenHeight;
        expPanel.panelColumns = numberOfTargetColumns;
        expPanel.panelRows = numberOfTargetRows;
        expPanel.moveMode = moveMode;
        expPanel.waitStartCircleSelect = true;
        expPanel.showFingerCombination = true;
        expPanel.setStartTarget();

        vib = (Vibrator)

                getSystemService(Context.VIBRATOR_SERVICE);

        missSound = MediaPlayer.create(this, R.raw.miss);
        changeFingerSound = MediaPlayer.create(this, R.raw.changefinger);
        completeSound = MediaPlayer.create(this, R.raw.complete);

        numberOfTrials = numberOfTargets;

        targetOrders = new int[numberOfTrials];
        for (int i = 0; i < numberOfTrials; ++i)
            targetOrders[i] = i;

        configureFingerCombination();
        expPanel.combinationString = expPanel.combination[combinationOrders[0]];

        initializeTrialOrders();

        // arrays needed for Throughput calculation (done on a per-sequence basis)
        from = new PointF[numberOfTrials];
        to = new PointF[numberOfTrials];
        select = new PointF[numberOfTrials];
        mtArray = new float[numberOfTrials];
        blockIdx = 0;
        combinationInd = 0;
        waitTargetSelected = true;
    }

    void configureFingerCombination() {
        combinationOrders = new int[numberOfCombinations];
        switch (conditionCode) {
            case 1:
                combinationOrders[0] = 0;
                combinationOrders[1] = 1;
                combinationOrders[2] = 2;
                break;
            case 2:
                combinationOrders[0] = 1;
                combinationOrders[1] = 2;
                combinationOrders[2] = 0;
                break;
            case 3:
                combinationOrders[0] = 2;
                combinationOrders[1] = 0;
                combinationOrders[2] = 1;
                break;
        }
    }

    // convert the amplitude/width string in the spinners to float array
    private float[] getValues(String valuesArg)
    {
        StringTokenizer st = new StringTokenizer(valuesArg, ", ");
        int i = 0;
        float[] values = new float[st.countTokens()];
        while (st.hasMoreTokens())
            values[i++] = Float.parseFloat(st.nextToken());
        return values;
    }

    void initializeFile() {

        // ===================
        // File initialization
        // ===================

        // make a working directory (if necessary) to store data files
        File dataDirectory = new File(Environment.getExternalStorageDirectory() +
                DATA_DIRECTORY);
        if (!dataDirectory.exists() && !dataDirectory.mkdirs())
        {
            Log.e(MYDEBUG, "ERROR --> FAILED TO CREATE DIRECTORY: " + DATA_DIRECTORY);
            super.onDestroy(); // cleanup
            this.finish(); // terminate
        }

        /**
         * The following do-loop creates data files for output and a string sd2Leader to write to the sd2
         * output files.  Both the filenames and the sd2Leader are constructed by combining the setup parameters
         * so that the filenames and sd2Leader are unique and also reveal the conditions used for the block of input.
         *
         * The block code begins "B01" and is incremented on each loop iteration until an available
         * filename is found.  The goal, of course, is to ensure data files are not inadvertently overwritten.
         */
        int blockNumber = 0;
        do
        {
            ++blockNumber;
            String blockCode = String.format("B%02d", blockNumber);
            String baseFilename = String.format("%s-%s-%s-%s-%d-%s", APP, participantCode,
                    sessionCode, groupCode, conditionCode, moveMode);

            f1 = new File(dataDirectory, baseFilename + ".sd1");
            //f2 = new File(dataDirectory, baseFilename + ".sd2");

            // also make a comma-delimited leader that will begin each data line written to the sd2 file
            //sd2Leader = String.format("%s,%s,%s,%s,%s,%s", APP, participantCode, sessionCode,
            //        blockCode, groupCode, conditionCode);
        } while (f1.exists());

        try
        {
            sd1 = new BufferedWriter(new FileWriter(f1));
            //sd2 = new BufferedWriter(new FileWriter(f2));

            // output header in sd1 file
            sd1.write(SD1_HEADER, 0, SD1_HEADER.length());
            sd1.flush();

            // output header in sd2 file
            //sd2.write(SD2_HEADER, 0, SD2_HEADER.length());
            //sd2.flush();

        } catch (IOException e)
        {
            Log.e(MYDEBUG, "ERROR OPENING DATA FILES! e=" + e.toString());
            super.onDestroy();
            this.finish();

        } // end file initialization

    }

    public void configureTargets()
    {
        // only one size target
        float rowWidth = expPanel.panelWidth * 1.0f / numberOfTargetColumns;
        float columnWidth = expPanel.panelHeight * 1.0f / numberOfTargetRows;
        float targetWidth = rowWidth < columnWidth ? rowWidth : columnWidth;
        for (int i = 0; i < numberOfTargetColumns; ++i)
            for (int j = 0; j < numberOfTargetRows; ++j)
            {
                float x = (2 * i + 1) * rowWidth / 2.0f;
                float y = (2 * j + 1) * columnWidth / 2.0f;
                expPanel.targetSet[i + j * numberOfTargetColumns] = new Target(Target.CIRCLE, x, y, targetWidth, targetWidth,
                        Target.NORMAL);
            }
        // Don't set target yet. This is done when start circle is selected.
    }

    void initializeTrialOrders() {
        File dataDirectory = new File(Environment.getExternalStorageDirectory() +
                DATA_DIRECTORY);
        if (!dataDirectory.exists() && !dataDirectory.mkdirs())
        {
            Log.e(MYDEBUG, "ERROR --> FAILED TO CREATE DIRECTORY: " + DATA_DIRECTORY);
            super.onDestroy(); // cleanup
            this.finish(); // terminate
        }
        f2 = new File(dataDirectory, "randomTargetOrders.sd2");
        trialOrders = new String[numberOfSessions];
        // read file
        if (f2.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(f2));
                String line;
                for (int i = 0; i < numberOfSessions; ++i) {
                    line = br.readLine();
                    trialOrders[i] = line;
                }
                br.close();
            }
            catch (IOException e) {

            }
            return;
        }
        // first randomize
        // make a working directory (if necessary) to store data files
        try {
            sd2 = new BufferedWriter(new FileWriter(f2));
            sb2 = new StringBuilder();

            for (int i = 0; i < numberOfSessions; ++i) {
                int[] orders = new int[numberOfTrials];
                for (int j = 0; j < numberOfTrials; ++j)
                    orders[j] = j;

                // shuffle target orders
                Random r = new Random();
                for (int j = 0; j < numberOfTrials; ++j) {
                    int idx = r.nextInt(numberOfTrials);
                    int temp = orders[idx];
                    orders[idx] = orders[i];
                    orders[i] = temp;
                }
                String s = "";
                for (int j = 0; j < numberOfTrials; ++j) {
                    s += (String.valueOf(orders[j]));
                    if (j == numberOfTrials-1) {
                        trialOrders[i] = s;
                        s+= "\n";
                    }
                    else
                        s+= ",";
                }
                sb2.append(s);
            }
            sd2.write(sb2.toString(), 0, sb2.length());
            sd2.flush();
            sb2.delete(0, sb2.length());
        } catch (IOException e) {
            Log.e(MYDEBUG, "ERROR OPENING DATA FILES! e=" + e.toString());
            super.onDestroy();
            this.finish();
        }
    }

    public void configureTargetOrders()
    {

        String[] s = trialOrders[blockIdx].split(",");
        for (int i = 0; i < numberOfTrials; ++i)
            targetOrders[i] = Integer.parseInt(s[i]);
    }

//    private AmplitudeWidth[] getAmplitudeWidthArray(float[] aArray, float[] wArray)
//    {
//        AmplitudeWidth[] aw = new AmplitudeWidth[aArray.length * wArray.length];
//        for (int i = 0; i < aw.length; ++i)
//            aw[i] = new AmplitudeWidth(aArray[i / wArray.length], wArray[i % wArray.length]);
//
//        // shuffle
//        Random r = new Random();
//        for (int i = 0; i < aw.length; ++i)
//        {
//            int idx = r.nextInt(aw.length);
//            AmplitudeWidth temp = aw[idx];
//            aw[idx] = aw[i];
//            aw[i] = temp;
//        }
//        return aw;
//    }

    public float[] getBlockArray(float[] wArray)
    {
        // # of blocks = # of sessions * # of target sizes
        float[] blocks = new float[numberOfSessions * wArray.length];
        for (int i = 0; i < wArray.length; ++i)
            for (int j = 0; j < numberOfSessions; ++j)
            {
                blocks[i * numberOfSessions + j] = wArray[i];
            }

        // shuffle
        Random r = new Random();
        for (int i = 0; i < blocks.length; ++i)
        {
            int idx = r.nextInt(blocks.length);
            float temp = blocks[idx];
            blocks[idx] = blocks[i];
            blocks[i] = temp;
        }
        return blocks;
    }

    public void doFingerDown(float xArg, float yArg)
    {
        xFingerDown = xArg;
        yFingerDown = yArg;
        fingerDownTime = System.nanoTime();
    }

    public void doStartCircleSelected()
    {
        if (expPanel.done) // start circle displayed after last sequence, select to finish
            doEndBlock();
        even = true;
        if (blockIdx < numberOfSessions)
        {
            expPanel.targetSet = new Target[numberOfTargets];
            configureTargets();
            configureTargetOrders();
            trialStartTime = System.nanoTime(); // last "now" value is start of trial
        }

        expPanel.targetSet[targetOrders[0]].status = Target.TARGET;
        expPanel.toTarget = expPanel.targetSet[targetOrders[0]];
        selectionCount = 0;
        expPanel.waitStartCircleSelect = false;
        expPanel.showFingerCombination = false;
    }

    // Done! close data files and exit
    public void doEndBlock()
    {
        try
        {
            sd1.close();
            //sd2.close();

            /*
             * Make the saved data files visible in Windows Explorer. There seems to be bug doing
             * this with Android 4.4. I'm using the following code, instead of sendBroadcast.
             * See...
             *
             * http://code.google.com/p/android/issues/detail?id=38282
             */
            MediaScannerConnection.scanFile(this, new String[] {f1.getAbsolutePath()}, null, null);
        } catch (IOException e)
        {
            Log.d("MYDEBUG", "FILE CLOSE ERROR! e = " + e);
        }
        Intent i = new Intent(getApplicationContext(), FittsTouchSetup.class);
        startActivity(i);
        finish();
    }

    public void calculateTrialData(float xSelect, float ySelect)
    {
        /*
         * CALCULATION OF XDELTA: xDelta is the signed distance of the selection coordinate from the
         * target center, as projected on the task axis. The projection onto the task axis is
         * performed to respect the inherent one-dimensionality of Fitts' law. NOTE: xDelta is -ve
         * if the selection is on the near side of the target center, +ve otherwise.
         *
         * NOTE: Some of the calculations here are repeated in the Throughput class. They are also
         * needed here to compute xDelta and ae at the end of each trial. Recall that the
         * Throughput
         * class is only given data at the end of a sequence.
         */

        // a is the distance from the center of the "from" target to the center of the "to" target.
        float ax = expPanel.fromTarget.xCenter - expPanel.toTarget.xCenter;
        float ay = expPanel.fromTarget.yCenter - expPanel.toTarget.yCenter;
        float a = (float)Math.sqrt(ax * ax + ay * ay);

        // b is the distance from the selection coordinate to the center of the "to" target.
        float b = (float)Math.sqrt((xSelect - expPanel.toTarget.xCenter) * (xSelect - expPanel.toTarget
                .xCenter)
                + (ySelect - expPanel.toTarget.yCenter) * (ySelect - expPanel.toTarget.yCenter));

        // c is the distance from the center of the "from" target to the selection coordinate.
        float c = (float)Math.sqrt((xSelect - expPanel.fromTarget.xCenter) * (xSelect - expPanel.fromTarget
                .xCenter) + (ySelect - expPanel.fromTarget.yCenter) * (ySelect - expPanel.fromTarget.yCenter));

        // combine Pythagorean theorems to find xDelta
        float xDelta = -1.0f * (a * a + b * b - c * c) / (2 * a);

		/*
         * ae is the "effective target amplitude" (the actual distance moved, as projected on the
		 * task axis). The calculation here isn't quite the same as in the Throughput class, where
		 * there is also an adjustment for the xDelta from the previous trial (for all trials after
		 * the first). The calculation here is good enough, since we are only using ae to determine
		 * if a trial is an outlier. The actual ae used in calculating throughput is calculated in
		 * the Throughput class.

        float ae = a + xDelta;
        */
		/*
         * DEFINITION OF OUTLIER: Any trial where the actual distance moved is less the 1/2 the
		 * specified amplitude is deemed an outlier. Any sequence with 1 or more outliers will be
		 * repeated.


        if (ae < aw[awIdx].a / 2f)
            outlier = true;
        */

        String trialTime = String.format("%.1f", (now - trialStartTime) / 1000000.0f);
        String fingerDownUpTime = String.format("%.1f", (now - fingerDownTime) / 1000000.0f);
        float fingerDownUpDelta = (float)Math.sqrt((xSelect - xFingerDown) * (xSelect -
                xFingerDown) + (ySelect - yFingerDown) * (ySelect - yFingerDown));

        /*
         * CAUTION: Ensure this is consistent with the sd1 header line defined at the top of
         * FittsTouchActivity.
         */
        sb1.append(String.format(
                "%s,%s,%s,%s,%d," +
                "%s,%d,%d," +
                "%s,%s,%s,%s," +
                "%d,%s,%s,%s,%s," +
                "%s,%s,%d,%d,%d,%s\n",
                participantCode, sessionCode, blockCode, groupCode, conditionCode,
                expPanel.combinationString.replaceAll("\\s+", ""), selectionCount, blockIdx,
                expPanel.fromTarget.xCenter, expPanel.fromTarget.yCenter,
                expPanel.toTarget.xCenter, expPanel.toTarget.yCenter,
                targetOrders[selectionCount], xFingerDown, yFingerDown, xSelect, ySelect,
                fingerDownUpTime, b, trialMiss, fingerDownMiss, fingerUpMiss, trialTime));

        /*
         * These four arrays are added to at the end of each trial. At the end of a sequence, they
         * are passed to the Throughput constructor. The Throughput object will compute throughput
         * and other values based on the data in these arrays.
         */
        from[selectionCount] = new PointF(expPanel.fromTarget.xCenter, expPanel.fromTarget.yCenter);
        to[selectionCount] = new PointF(expPanel.toTarget.xCenter, expPanel.toTarget.yCenter);
        select[selectionCount] = new PointF(xSelect, ySelect);
        mtArray[selectionCount] = (now - trialStartTime) / 1000000.0f;
    }

    void doEndSequence()
    {

            /*
             * Give the relevant data to the Throughput object (where the serious calculations are
             * performed).
             */
            int taskType = Throughput.ONE_DIMENSIONAL;
            int responseType = Throughput.SERIAL;
            Throughput t = new Throughput("nocode", 0f, 0f, taskType,
                    responseType, from, to, select,
                    mtArray);

            /*
             * CAUTION: Ensure this is consistent with the sd2 header line defined at the top of
             * FittsTouchActivity.

            sb2.append(sd2Leader);
            sb2.append(String.format("%d,%d,%s,%s,%s,%s,%s,%s,%s,%s\n", t.getNumberOfTrials(),
                    outlierSequenceCount, t.getW(), t.getID(), t.getAe(), t.getWe(), t
                            .getIDe(), t.getMT(), t.getErrorRate(), t.getThroughput()));
            */

            // write data to files at end of each sequence
            try
            {
                sd1.write(sb1.toString(), 0, sb1.length());
                sd1.flush();
                //sd2.write(sb2.toString(), 0, sb2.length());
                //sd2.flush();
            } catch (IOException e)
            {
                Log.d("MYDEBUG", "ERROR WRITING TO DATA FILES: e = " + e);
            }
            sb1.delete(0, sb1.length());
            //sb2.delete(0, sb2.length());

            // prepare results for output on display
            String s = "Combination " + (combinationInd + 1) + ":";
            s += "Sequence " + (blockIdx + 1) + " of " + numberOfSessions + ":";
            s += "Number of trials = " + numberOfTrials + ":";
            s += "-----:";
            s += "Trial Misses = " + trialMissCount + ":";
            results.append(s);
            expPanel.resultsString = results.toString().split(":");

            ++blockIdx;
            // next block
            if (blockIdx < numberOfSessions)
                configureTargets();
            else {
                combinationInd++;
                // change to next finger combination
                if (combinationInd < numberOfCombinations) {
                    blockIdx = 0;
                    expPanel.showFingerCombination = true;
                    expPanel.combinationString = expPanel.combination[combinationOrders[combinationInd]];
                    changeFingerSound.start();
                } else {
                    expPanel.done = true;
                    expPanel.showFingerCombination = true;
                    expPanel.combinationString = "End of Tap Experiment";
                    completeSound.start();
                }
            }

        expPanel.waitStartCircleSelect = true;
        sequenceStarted = false;
        trialMissCount = 0;

    }
}