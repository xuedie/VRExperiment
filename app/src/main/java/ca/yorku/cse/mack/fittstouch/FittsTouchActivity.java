package ca.yorku.cse.mack.fittstouch;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
 *      Mode - mode code (1D or 2D)
 *      Trial - trial number
 *      A - target amplitude
 *      W - target width
 *      FromX - x coordinate of center of from-target
 *      FromY - y coordinate of center of from-target
 *      TargetX - x coordinate of center of to-target
 *      TargetY - y coordinate of center of to-target
 *      FingerDownX - x coordinate of finger-down event at end of trial
 *      FingerDownY - y coordinate of finger-down event at end of trial
 *      SelectX - x coordinate of finger-up event at end of trial (target selection)
 *      SelectY - y coordinate of finger-up event at end of trial (target selection)
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
 *      Mode - mode code (1D or 2D)
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

public class FittsTouchActivity extends FittsActivity implements View.OnTouchListener
{

    boolean targetTapped;
    // -----
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        expPanel.setOnTouchListener(this);
        targetTapped = false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        float x = me.getX();
        float y = me.getY();

        if (expPanel.waitStartCircleSelect && me.getAction() == MotionEvent.ACTION_UP)
        {
            if (!expPanel.startCircle.inTarget(x, y))
                return true;
            else
                doStartCircleSelected();
            return true;
        }

        if (expPanel.waitStartCircleSelect)
            return true;

        // detect hit/miss when down/up
        if (me.getAction() == MotionEvent.ACTION_DOWN)
        {
            doFingerDown(x, y);
        } else if (me.getAction() == MotionEvent.ACTION_UP)
        {
            doFingerUp(x, y);
        } else if (me.getAction() == MotionEvent.ACTION_MOVE)
        {
            doFingerPressed(x, y);
        }
        return true;
    }

    public void doFingerDown(float xArg, float yArg)
    {
        xFingerDown = xArg;
        yFingerDown = yArg;
        fingerDownTime = System.nanoTime();

        if (expPanel.toTarget != null)
        {
            boolean isInTarget = expPanel.toTarget.inTarget(xArg, yArg);
            if (isInTarget) {
                targetTapped = true;
                fingerDownMiss = 0;
                expPanel.toTarget.status = Target.TAPPINGSELECTED;
            } else {
                expPanel.toTarget.status = Target.TARGET;
                fingerDownMiss = 1;
            }
        }
    }

    public void doFingerPressed(float xArg, float yArg)
    {
        if (expPanel.toTarget != null)
        {
            boolean isInTarget = expPanel.toTarget.inTarget(xArg, yArg);
            if (isInTarget) {
                targetTapped = true;
                expPanel.toTarget.status = Target.TAPPINGSELECTED;
            } else {
                expPanel.toTarget.status = Target.TARGET;
            }
        }
    }

    public void doFingerUp(float xSelect, float ySelect)
    {
        boolean isInTarget = expPanel.toTarget.inTarget(xSelect, ySelect);
        fingerUpMiss = isInTarget ? 0 : 1;

        now = System.nanoTime(); // current "now" value is end of trial

        // set back to normal status
        expPanel.toTarget.status = Target.TARGET;

        // hit or miss? (respond appropriately)
        trialMiss = isInTarget ? 0 : 1;

        if (trialMiss == 1)
        {
            // provide feedback (as per setup) but only if the user misses the target
            if (vibrotactileFeedback)
                vib.vibrate(VIBRATION_PULSE_DURATION);
            if (auditoryFeedback)
                missSound.start();
            trialMissCount++;
        }

        if (!sequenceStarted) // 1st target selection (beginning of sequence)
        {
            sequenceStarted = true;
            sequenceStartTime = now;
            expPanel.fromTarget = expPanel.targetSet[0];
            sb1 = new StringBuilder();
            sb2 = new StringBuilder();
            results = new StringBuilder();
        }

        calculateTrialData(xSelect, ySelect);

        Log.d("Miss", String.format("Trial Miss: %d, DownMiss: %d, UpMiss: %d", trialMiss, fingerDownMiss, fingerUpMiss));

        // prepare for next target selection
        ++selectionCount;
        targetTapped = false;
        fingerUpMiss = 1;
        fingerDownMiss = 1;

        if (selectionCount == numberOfTrials)
            doEndSequence();
        else
            advanceTarget();
    }

    // advance to the next target (a bit complicated for the 2D task; see comment below)
    private void advanceTarget()
    {
        /*
         * Find the current "target" then advance it to the circle on the opposite side of the
         * layout circle. This is a bit tricky since every second advance requires the target to be
         * beside the target directly opposite the last target. This is needed to get the sequence
         * of selections to advance around the layout circle. Of course, this only applies to
         * the 2D
         * task.
         */

        // find current target
        int i;
        for (i = 0; i < expPanel.targetSet.length; ++i)
            if (expPanel.targetSet[i].status == Target.TARGET)
                break; // i is index of current target

        // last target becomes "normal" again
        expPanel.targetSet[i].status = Target.NORMAL;

        // find next target
        int next = targetOrders[selectionCount];
        expPanel.targetSet[next].status = Target.TARGET;
        expPanel.fromTarget = expPanel.targetSet[i];
        expPanel.toTarget = expPanel.targetSet[next];
        even = !even;

        trialStartTime = System.nanoTime(); // last "now" value is start of trial
    }
}