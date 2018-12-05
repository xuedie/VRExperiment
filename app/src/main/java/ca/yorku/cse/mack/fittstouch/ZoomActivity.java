package ca.yorku.cse.mack.fittstouch;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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
 * </pre>
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
 * </pre>
 */

public class ZoomActivity extends Activity
{
    final String MYDEBUG = "MYDEBUG";
    final String DATA_DIRECTORY = "/ZoomData/";
    final String APP = "Zoom";
    // trial data
    final String SD1_HEADER = "Participant,Session,Group,Combination," +
            "CurrentCombination,TrialIdx,BlockIdx,TaskType,NumberOfScales,TrialTime(ms)\n";
    // scales per trial data
    final String SD2_HEADER = "Participant,Session,Group,Combination,CurrentCombination,TrialIdx,BlockIdx," +
            "ScaleNum,ScaleTime(ms),ScaleSpan,ScaleStartTime\n";
    // per touch data
    final String SD4_HEADER = "Participant,Session,Group,Combination,CurrentCombination,TrialIdx,BlockIdx," +
            "ScaleNum,PointerA_X,PointerA_Y,PointerB_X,PointerB_Y,TimeStamp\n";
    final int REST_TIME = 10;
    final int RECORD_DATA_TIME_MS = 50;

    StringBuilder sb1, sb2, sb3, sb4, results;
    String participantCode, sessionCode, blockCode, groupCode;
    BufferedWriter sd1, sd2, sd3, sd4;
    File f1, f2, f3, f4;

    ZoomPanel zoomPanel;
    String moveMode;
    float xCenter, yCenter;
    float scaleFactor;
    float scaleValue;
    int scaleCount;
    int inValue, outValue;
    int[] trialValues;
    int trialIdx, combinationIdx, blockIdx;
    int numberOfCombinations = 4;
    int[] combinationOrders;
    int conditionCode, numberOfSessions, numberOfTrials;
    long waitSec0, waitSec1;
    String[] trialOrders;
    ScaleGestureDetector mScaleGestureDetector;
    Timer t;
    MediaPlayer changeFingerSound, completeSound;

    boolean firstScale,firstTap,needRecordData;
    // start first scale of the trial
    long trialStartTime,tapStartTime;
    long currentStartTime;
    float startSpan;

    float[] xP,yP;
    // -----
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom);
        zoomPanel = (ZoomPanel)findViewById(R.id.zoompanel);
        FittsActivity.verifyStoragePermissions(this);

        // get values from setting
        Bundle b = getIntent().getExtras();
        participantCode = b.getString("participantCode");
        sessionCode = b.getString("sessionCode");
        groupCode = b.getString("groupCode");
        conditionCode = b.getInt("conditionCode") + 1;
        moveMode = b.getString("mode");
        // + 2 practice blocks per finger
        // TODO:
        numberOfSessions = b.getInt("numberOfTrials") + 1;
        numberOfTrials = b.getInt("numberOfTasks");
        inValue = 75;
        outValue = 25;
        waitSec0 = 1;
        waitSec1 = (long).75;

        fileInitialize();

        // determine screen width and height
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        xCenter = screenWidth / 2f;
        yCenter = screenHeight / 2f;

        zoomPanel.panelWidth = screenWidth;
        zoomPanel.panelHeight = screenHeight;
        zoomPanel.waitStartCircleSelect = true;
        zoomPanel.showFingerCombination = true;
        zoomPanel.showNextValue = false;
        zoomPanel.setStartTarget();
        zoomPanel.isVisibilityTest = true;
        zoomPanel.isResting = false;

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        scaleFactor = 0.03f;
        scaleValue = 50f;
        xP = new float[2];
        yP = new float[2];

        trialValues = new int[numberOfTrials];
        scaleCount = 0;
        trialIdx = 0;
        blockIdx = 0;
        combinationIdx = 0;

        initializeTrialOrders();

        configureFingerCombination();
        zoomPanel.combinationString = zoomPanel.combination[combinationOrders[0]];

        changeFingerSound = MediaPlayer.create(this, R.raw.changefinger);
        completeSound = MediaPlayer.create(this, R.raw.complete);
    }

    void restTimerCountdown(int time) {
        zoomPanel.freezing = true;
        t = new Timer();
        t.scheduleAtFixedRate(new RestTask(time),  1000,  1000);
    }

    class RestTask extends  TimerTask {
        int cur;
        RestTask(int t) {
            zoomPanel.isResting = true;
            cur = t;
            zoomPanel.restTimer[1] = "0:" + cur;
            //Log.e(MYDEBUG, "resting start");
        }
        public void run() {
            cur--;
            zoomPanel.restTimer[1] = "0:" + cur;
            if (cur < 10)
                zoomPanel.restTimer[1] = "0:0" + cur;
            // Log.e(MYDEBUG, String.format("resting %d", cur));
            if (cur <= 0) {
                zoomPanel.freezing = false;
                zoomPanel.isResting = false;
                showCombination();
                t.cancel();
            }
        }
    }

    void freezeScreen() {
        zoomPanel.freezing = true;
        t = new Timer();
        if (trialIdx == 0) {
            t.schedule(new FirstTrialTask(), waitSec0 * 1000);
        } else if (trialIdx == numberOfTrials) {
            t.schedule(new LastTrialTask(), waitSec0 * 1000);
        } else {
            t.scheduleAtFixedRate(new UnfreezeTask(), waitSec0 * 1000, (waitSec0 + waitSec1) * 1000);
        }
    }

    class LastTrialTask extends  TimerTask {
        LastTrialTask() {

        }
        public void run() {
            zoomPanel.freezing = false;
            t.cancel();
            zoomPanel.valueString[0] = "" + Math.round(scaleValue);
            doEndSequence();
        }
    }

    class FirstTrialTask extends  TimerTask {
        FirstTrialTask() {
            zoomPanel.showNextValue = true;
            zoomPanel.valueString[0] = "" + Math.round(scaleValue);
            zoomPanel.valueString[1] = "" + trialValues[trialIdx];
            Log.e(MYDEBUG, "FirstTrialTask start");
        }
        public void run() {
            zoomPanel.showNextValue = false;
            zoomPanel.freezing = false;
            t.cancel();
            Log.e(MYDEBUG, "FirstTrialTask timeout");
        }
    }

    class UnfreezeTask extends TimerTask {
        boolean showNext;
        UnfreezeTask() {
            showNext = false;
            zoomPanel.showNextValue = false;
        }
        public void run() {
            if (!showNext) {
                showNext = true;
                zoomPanel.showNextValue = true;
                zoomPanel.valueString[0] = "" + Math.round(scaleValue);
                zoomPanel.valueString[1] = "" + trialValues[trialIdx];
                //Log.e(MYDEBUG, "First timeout");
            } else {
                zoomPanel.showNextValue = false;
                zoomPanel.freezing = false;
                t.cancel();
                //Log.e(MYDEBUG, "Second timeout");
            }
        }
    }

    void configureFingerCombination() {
        combinationOrders = new int[numberOfCombinations];
        switch (conditionCode) {
            case 1:
                combinationOrders[0] = 0;
                combinationOrders[1] = 1;
                combinationOrders[2] = 2;
                combinationOrders[3] = 3;
                break;
            case 2:
                combinationOrders[0] = 1;
                combinationOrders[1] = 0;
                combinationOrders[2] = 3;
                combinationOrders[3] = 2;
                break;
            case 3:
                combinationOrders[0] = 2;
                combinationOrders[1] = 3;
                combinationOrders[2] = 0;
                combinationOrders[3] = 1;
                break;
            case 4:
                combinationOrders[0] = 3;
                combinationOrders[1] = 2;
                combinationOrders[2] = 1;
                combinationOrders[3] = 0;
                break;
        }
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
        f3 = new File(dataDirectory, "Zoom-randomTargetOrders-" + numberOfSessions + "-" + numberOfTrials + ".sd3");
        trialOrders = new String[numberOfSessions];
        // read file
        if (f3.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(f3));
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
            sd3 = new BufferedWriter(new FileWriter(f3));
            sb3 = new StringBuilder();

            for (int i = 0; i < numberOfSessions; ++i) {
                int[] values = new int[numberOfTrials];
                for (int j = 0; j < numberOfTrials / 2; ++j) {
                    values[j] = inValue;
                }
                for (int j = numberOfTrials / 2; j < numberOfTrials; ++j) {
                    values[j] = outValue;
                }
                // randomize data trials
                Random r = new Random();
                for (int j = 0; j < numberOfTrials; ++j)
                {
                    int idx = r.nextInt(numberOfTrials);
                    int temp = values[idx];
                    values[idx] = values[j];
                    values[j] = temp;
                }
                String s = "";
                for (int j = 0; j < numberOfTrials; ++j) {
                    s += (String.valueOf(values[j]));
                    if (j == numberOfTrials-1) {
                        trialOrders[i] = s;
                        s+= "\n";
                    }
                    else
                        s+= ",";
                }
                sb3.append(s);
            }
            sd3.write(sb3.toString(), 0, sb3.length());
            sd3.flush();
            sb3.delete(0, sb3.length());
        } catch (IOException e) {
            Log.e(MYDEBUG, "ERROR OPENING DATA FILES! e=" + e.toString());
            super.onDestroy();
            this.finish();
        }
    }

    void setTrialValues() {

        String[] s = trialOrders[blockIdx].split(",");
        for (int i = 0; i < numberOfTrials; ++i)
            trialValues[i] = Integer.parseInt(s[i]);
    }

    void recordDataTimer() {
        t = new Timer();
        t.schedule(new RecordDataTask(), RECORD_DATA_TIME_MS, RECORD_DATA_TIME_MS);
    }

    class RecordDataTask extends TimerTask {
        RecordDataTask() {needRecordData = false;}
        public void  run() {
            Long tsLong = System.currentTimeMillis();
            String ts = tsLong.toString();
            sb4.append(String.format("%s,%s,%s,%d," +
                            "%s,%d,%d,%d," +
                            "%s,%s,%s,%s,%s\n",
                    participantCode, sessionCode, groupCode, conditionCode,
                    zoomPanel.combinationString.replaceAll("\\s+", ""),
                    trialIdx, blockIdx, scaleCount,
                    xP[0], yP[0], xP[1], yP[1],
                    ts));
        }
    }

    public boolean onTouchEvent(MotionEvent me) {
        float x = me.getX();
        float y = me.getY();

        if (zoomPanel.isVisibilityTest) {
            zoomPanel.isVisibilityTest = false;
            return true;
        }

        if (zoomPanel.waitStartCircleSelect && me.getAction() == MotionEvent.ACTION_UP)
        {
            if (!zoomPanel.startCircle.inTarget(x, y))
                return true;
            else
                doStartCircleSelected();
            return true;
        }

        if (zoomPanel.waitStartCircleSelect)
            return true;

        if (firstTap && me.getAction() == MotionEvent.ACTION_DOWN) {
            tapStartTime = System.nanoTime();
            firstTap = false;
        }


        int count = me.getPointerCount();
        if (count == 2) {
            xP[0] = me.getX(0);
            xP[1] = me.getX(1);
            yP[0] = me.getY(0);
            yP[1] = me.getY(1);
            //if (needRecordData) {
                //Log.d("Scale Pointers","record data");
                //Log.d("Scale Pointers", String.format("%d: %.2f, %.2f", i, me.getX(i), me.getY(i)));
//                Long tsLong = System.currentTimeMillis();
//                String ts = tsLong.toString();
//                sb4.append(String.format("%s,%s,%s,%d," +
//                                "%s,%d,%d,%d," +
//                                "%s,%s,%s,%s,%s\n",
//                        participantCode, sessionCode, groupCode, conditionCode,
//                        zoomPanel.combinationString.replaceAll("\\s+", ""),
//                        trialIdx, blockIdx, scaleCount,
//                        xP[0], yP[0], xP[1], yP[1],
//                        ts));
                //recordDataTimer();
            //}
        }

        mScaleGestureDetector.onTouchEvent(me);
        return true;
    }

    void doStartCircleSelected() {
        if (zoomPanel.done) // start circle displayed after last sequence, select to finish
            doEndBlock();
        if (trialIdx < numberOfSessions)
        {
            setTrialValues();
        }

        trialIdx = 0;
        zoomPanel.waitStartCircleSelect = false;
        zoomPanel.showFingerCombination = false;
        advanceTask();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (!zoomPanel.freezing) {
                //Log.d("ScaleStart", "Start of Scale:" + String.format ("%.2f", detector.getCurrentSpan()));
                if (firstScale) {
                    trialStartTime = System.nanoTime();
                    firstScale = false;
                }
                startSpan = detector.getCurrentSpan();
                currentStartTime = System.nanoTime();
                Long tsLong = System.currentTimeMillis();
                String ts = tsLong.toString();
                sb4.append(String.format("%s,%s,%s,%d," +
                                "%s,%d,%d,%d," +
                                "%s,%s,%s,%s,%s,Start\n",
                        participantCode, sessionCode, groupCode, conditionCode,
                        zoomPanel.combinationString.replaceAll("\\s+", ""),
                        trialIdx, blockIdx, scaleCount,
                        xP[0], yP[0], xP[1], yP[1],
                        ts));
                recordDataTimer();
            }
            return !zoomPanel.freezing;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            // check in target
            float currentSpan = scaleGestureDetector.getCurrentSpan();
            float previousSpan = scaleGestureDetector.getPreviousSpan();
            scaleValue += scaleFactor * (currentSpan - previousSpan);
            // show
            zoomPanel.valueString[0] = "" + Math.round(scaleValue);;
            //Log.d("Scaling",  String.format ("%.2f", currentSpan));
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (t != null) {
                t.cancel();
            }
            Long tsLong = System.currentTimeMillis();
            String ts = tsLong.toString();
            sb4.append(String.format("%s,%s,%s,%d," +
                            "%s,%d,%d,%d," +
                            "%s,%s,%s,%s,%s,End\n",
                    participantCode, sessionCode, groupCode, conditionCode,
                    zoomPanel.combinationString.replaceAll("\\s+", ""),
                    trialIdx, blockIdx, scaleCount,
                    xP[0], yP[0], xP[1], yP[1],
                    ts));
            scaleCount++;
            endOfScale(detector.getCurrentSpan()-startSpan);
            //Log.d("ScaleEnd", "End of Scale"+String.format ("%.2f", detector.getCurrentSpan()));
            if (trialValues[trialIdx] == Math.round(scaleValue)) {
                endOfTrial();
            }
        }
    }

    void endOfScale(float dis) {
        long now = System.nanoTime();
        // record if not practise
        String scaleTime = String.format("%.1f", (now - currentStartTime) / 1000000.0f);
        String span = String.format ("%.2f", dis);
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        sb2.append(String.format("%s,%s,%s,%s," +
                        "%s,%d,%d," +
                        "%d,%s,%s,%s\n",
                participantCode, sessionCode, groupCode, conditionCode,
                zoomPanel.combinationString.replaceAll("\\s+", ""),
                trialIdx, blockIdx,
                scaleCount, scaleTime, span, ts));
    }

    void fileInitialize() {
        // make a working directory (if necessary) to store data files
        File dataDirectory = new File(Environment.getExternalStorageDirectory() +
                DATA_DIRECTORY);
        if (!dataDirectory.exists() && !dataDirectory.mkdirs())
        {
            Log.e(MYDEBUG, "ERROR --> FAILED TO CREATE DIRECTORY: " + DATA_DIRECTORY);
            super.onDestroy(); // cleanup
            this.finish(); // terminate
        }
        int blockNumber = 0;
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        String baseFilename = String.format("%s-%s-%s-%s-%d-%s", APP, participantCode,
                    sessionCode, groupCode, conditionCode, ts);

        f1 = new File(dataDirectory, baseFilename + "-trial.sd1");
        f2 = new File(dataDirectory, baseFilename + "-scale.sd2");
        f4 = new File(dataDirectory, baseFilename + "-touch.sd4");

        try
        {
            sd1 = new BufferedWriter(new FileWriter(f1));

            // output header in sd1 file
            sd1.write(SD1_HEADER, 0, SD1_HEADER.length());
            sd1.flush();

            sd2 = new BufferedWriter(new FileWriter(f2));

            // output header in sd2 file
            sd2.write(SD2_HEADER, 0, SD2_HEADER.length());
            sd2.flush();

            sd4 = new BufferedWriter(new FileWriter(f4));

            // output header in sd2 file
            sd4.write(SD4_HEADER, 0, SD4_HEADER.length());
            sd4.flush();

        } catch (IOException e)
        {
            Log.e(MYDEBUG, "ERROR OPENING DATA FILES! e=" + e.toString());
            super.onDestroy();
            this.finish();

        }
        sb1 = new StringBuilder();
        sb2 = new StringBuilder();
        sb4 = new StringBuilder();
        results = new StringBuilder();
        // end file initialization
    }

    void endOfTrial() {
        // record data
        long now = System.nanoTime();
        // String trialTime = String.format("%.1f", (now - trialStartTime) / 1000000.0f);
        String trialTime = String.format("%.1f", (now - tapStartTime) / 1000000.0f);
        String taskType = (trialValues[trialIdx] == inValue) ? "In" : "Out";
        sb1.append(String.format("%s,%s,%s,%d," +
                        "%s,%d,%d,%s,%d,%s\n",
                participantCode, sessionCode, groupCode, conditionCode,
                zoomPanel.combinationString.replaceAll("\\s+", ""),
                trialIdx, blockIdx, taskType,
                scaleCount, trialTime));

        trialIdx++;
        if (trialIdx == numberOfTrials) {
            freezeScreen();
        }
        else
            advanceTask();
    }

    void advanceTask() {
        scaleCount = 0;
        scaleValue = 50f;
        freezeScreen();
        firstScale = true;
        firstTap = true;
        zoomPanel.isZoomIn = trialValues[trialIdx] - scaleValue > 0;
    }

    void doEndSequence()
    {
        // write data to files at end of each sequence
        try
            {
                sd1.write(sb1.toString(), 0, sb1.length());
                sd1.flush();
                sd2.write(sb2.toString(), 0, sb2.length());
                sd2.flush();
                sd4.write(sb4.toString(), 0, sb4.length());
                sd4.flush();
            } catch (IOException e)
            {
                Log.d("MYDEBUG", "ERROR WRITING TO DATA FILES: e = " + e);
            }
            sb1.delete(0, sb1.length());
            sb2.delete(0, sb2.length());
            sb4.delete(0, sb4.length());

            sb1 = new StringBuilder();
            sb2 = new StringBuilder();
            sb4 = new StringBuilder();
            results = new StringBuilder();

            // prepare results for output on display
            String s = "Combination " + (combinationIdx + 1) + ":";
            s += "Sequence " + (blockIdx + 1) + " of " + numberOfSessions + ":";
            s += "Number of trials = " + numberOfTrials + ":";
            results.append(s);
            zoomPanel.resultsString = results.toString().split(":");

            ++blockIdx;
            // next block
            if (blockIdx < numberOfSessions) {
                setTrialValues();
            }
            else {
                combinationIdx++;
                // change to next finger combination
                if (combinationIdx < numberOfCombinations) {
                    blockIdx = 0;
                    changeFingerSound.start();
                    restTimerCountdown(REST_TIME);
                } else {
                    zoomPanel.done = true;
                    zoomPanel.showFingerCombination = true;
                    zoomPanel.combinationString = "End of Zoom Experiment";
                    completeSound.start();
                }
            }
        zoomPanel.waitStartCircleSelect = true;
    }

    void showCombination() {
        zoomPanel.showFingerCombination = true;
        zoomPanel.combinationString = zoomPanel.combination[combinationOrders[combinationIdx]];
    }

    // Done! close data files and exit
    void doEndBlock()
    {
        try
        {
            sd1.close();
            sd2.close();

            /*
             * Make the saved data files visible in Windows Explorer. There seems to be bug doing
             * this with Android 4.4. I'm using the following code, instead of sendBroadcast.
             * See...
             *
             * http://code.google.com/p/android/issues/detail?id=38282
             */
            MediaScannerConnection.scanFile(this, new String[] {f1.getAbsolutePath(), f2.getAbsolutePath()}, null, null);
        } catch (IOException e)
        {
            Log.d("MYDEBUG", "FILE CLOSE ERROR! e = " + e);
        }
        completeSound.release();
        changeFingerSound.release();
        Intent i = new Intent(getApplicationContext(), FittsTouchSetup.class);
        startActivity(i);
        finish();
    }
}