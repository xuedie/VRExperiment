package ca.yorku.cse.mack.fittstouch;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class ScrollActivity extends Activity {

    float xCenter, yCenter;
    SeekBar longHorizontal, shortHorizontal, verticalRight, verticalLeft;
    TextView text;
    ScrollPanel panel;
    String participantCode, sessionCode, groupCode, moveMode;
    int conditionCode, numberOfSessions, numberOfTrials;

    // 50-75, 50-25, 50-95, 50-5, 0-50, 0-95, 100-50, 100-5
    int[] startVal = {50, 50, 50, 50, 0, 0, 100, 100};
    int[] endVal = {75, 25, 95, 5, 50, 95, 50, 5};
    int curVal;
    int destVal;
    int taskIdx = 0;

    int[] barOrder = {3, 1, 4};
    int barOrderIdx = 0;

    int numberOfCombinations = 3;
    int[] combinationOrders;
    int combinationIdx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scroll);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(decorView.SYSTEM_UI_FLAG_IMMERSIVE | decorView.SYSTEM_UI_FLAG_FULLSCREEN
                | decorView.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        panel = (ScrollPanel) findViewById(R.id.scrollpanel);
        // determine screen width and height
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = 1920;
        int screenHeight = 1080;
        xCenter = screenWidth / 2f;
        yCenter = screenHeight / 2f;

        panel.panelWidth = screenWidth;
        panel.panelHeight = screenHeight;
        panel.waitStartCircleSelect = true;
        panel.isVisibilityTest = true;
        panel.isResting = false;
        panel.setStartTarget();

        // get values from setting
        Bundle b = getIntent().getExtras();
        participantCode = b.getString("participantCode");
        sessionCode = b.getString("sessionCode");
        groupCode = b.getString("groupCode");
        conditionCode = b.getInt("conditionCode") + 1;
        moveMode = b.getString("mode");
        numberOfSessions = b.getInt("numberOfTrials") + 1;
        numberOfTrials = b.getInt("numberOfTasks");

        text = (TextView) findViewById(R.id.horizontalNum);

        longHorizontal = (SeekBar) findViewById(R.id.horizontalLongBar);
        longHorizontal.setOnSeekBarChangeListener(new ScrollBarListener());

        verticalRight = (SeekBar)findViewById(R.id.verticalBar_Right);
        verticalRight.setOnSeekBarChangeListener(new ScrollBarListener());

        verticalLeft = (SeekBar)findViewById(R.id.verticalBar_Left);
        verticalLeft.setOnSeekBarChangeListener(new ScrollBarListener());

        verticalLeft.setVisibility(View.INVISIBLE);
        verticalRight.setVisibility(View.INVISIBLE);
        longHorizontal.setVisibility(View.INVISIBLE);

        configureFingerCombination();
        panel.combinationString = panel.combination[combinationOrders[combinationIdx]];
    }

    class ScrollBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                curVal = progress;
                text.setText(String.valueOf(progress));
                panel.valueString[0] = String.valueOf(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (curVal == destVal) {
                onTaskEnd();
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

    public boolean onTouchEvent(MotionEvent me) {
        float x = me.getX();
        float y = me.getY();

        if (panel.isVisibilityTest) {
            panel.isVisibilityTest = false;
            panel.showFingerCombination = true;
            return true;
        }

        if (panel.waitStartCircleSelect && me.getAction() == MotionEvent.ACTION_UP)
        {
            if (!panel.startCircle.inTarget(x, y))
                return true;
            else
                doStartCircleSelected();
            return true;
        }

        return true;
    }

    void doStartCircleSelected() {
        if (panel.done) // start circle displayed after last sequence, select to finish
            doEndExperiment();

        curVal = startVal[taskIdx];
        destVal = endVal[taskIdx];
        panel.valueString[0] = String.valueOf(curVal);
        panel.valueString[1] = String.valueOf(destVal);
        setVisibleScrollBar(barOrder[barOrderIdx], curVal);
        panel.waitStartCircleSelect = false;
        panel.showFingerCombination = false;
    }

    void onTaskEnd() {
        taskIdx++;
        if (taskIdx == startVal.length) {
            onEndOfBlock();
        } else {
            curVal = startVal[taskIdx];
            destVal = endVal[taskIdx];
            panel.valueString[0] = String.valueOf(curVal);
            panel.valueString[1] = String.valueOf(destVal);
            setVisibleScrollBar(barOrder[barOrderIdx], curVal);
        }
    }

    void onEndOfBlock() {
        barOrderIdx++;
        if (barOrderIdx == barOrder.length) {
            onEndOfFinger();
        }
        taskIdx = 0;
        panel.waitStartCircleSelect = true;
        setVisibleScrollBar(-1, -1);
    }

    void onEndOfFinger() {
            combinationIdx++;
            // change to next finger combination
            if (combinationIdx < numberOfCombinations) {
//                changeFingerSound.start();
//                restTimerCountdown(REST_TIME);
                panel.showFingerCombination = true;
                panel.combinationString = panel.combination[combinationOrders[combinationIdx]];
            } else {
                panel.done = true;
                panel.showFingerCombination = true;
                panel.combinationString = "End of Scroll Experiment";
//                completeSound.start();
            }

    }

    void setVisibleScrollBar(int idx, int val) {
        switch (idx) {
            case -1:
                verticalLeft.setVisibility(View.INVISIBLE);
                verticalRight.setVisibility(View.INVISIBLE);
                longHorizontal.setVisibility(View.INVISIBLE);
                break;
            case 0:
                // Left vertical
                verticalLeft.setVisibility(View.VISIBLE);
                verticalLeft.setProgress(val);
                verticalRight.setVisibility(View.INVISIBLE);
                longHorizontal.setVisibility(View.INVISIBLE);
                break;
            case 1:
                // Right vertical
                verticalLeft.setVisibility(View.INVISIBLE);
                verticalRight.setVisibility(View.VISIBLE);
                verticalRight.setProgress(val);
                longHorizontal.setVisibility(View.INVISIBLE);
                break;
            case 2:
                // Left horizontal
                longHorizontal.getLayoutParams().width = 860;
                longHorizontal.requestLayout();
                verticalLeft.setVisibility(View.INVISIBLE);
                verticalRight.setVisibility(View.INVISIBLE);
                longHorizontal.setVisibility(View.VISIBLE);
                longHorizontal.setProgress(val);
                break;
            case 3:
                // Right horizontal
                // TODO:
                longHorizontal.getLayoutParams().width = 860;
                longHorizontal.getLayoutParams(). = 860;
//                LayoutParams params = longHorizontal.getLayoutParams();
//                params.width = 200; params.leftMargin = 100; params.topMargin = 200;
                longHorizontal.requestLayout();
                verticalLeft.setVisibility(View.INVISIBLE);
                verticalRight.setVisibility(View.INVISIBLE);
                longHorizontal.setVisibility(View.VISIBLE);
                longHorizontal.setProgress(val);
                break;
            case 4:
                // Long horizontal
                longHorizontal.getLayoutParams().width = 1720;
                longHorizontal.requestLayout();
                verticalLeft.setVisibility(View.INVISIBLE);
                verticalRight.setVisibility(View.INVISIBLE);
                longHorizontal.setVisibility(View.VISIBLE);
                longHorizontal.setProgress(val);
                break;
        }
    }

    void doEndExperiment() {
        Intent i = new Intent(getApplicationContext(), FittsTouchSetup.class);
        startActivity(i);
        finish();
    }
}
