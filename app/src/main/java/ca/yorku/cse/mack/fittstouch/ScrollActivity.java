package ca.yorku.cse.mack.fittstouch;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class ScrollActivity extends Activity implements View.OnTouchListener {

    SeekBar longHorizontal, shortHorizontal, verticalRight, verticalLeft;
    TextView text;
    ScrollPanel panel;
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
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
//        xCenter = screenWidth / 2f;
//        yCenter = screenHeight / 2f;

//        panel.panelWidth = screenWidth;
//        panel.panelHeight = screenHeight;
//        panel.waitStartCircleSelect = true;
//        panel.setStartTarget();

        text = (TextView) findViewById(R.id.horizontalNum);

        longHorizontal = (SeekBar) findViewById(R.id.horizontalLongBar);
        longHorizontal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        verticalRight = (SeekBar)findViewById(R.id.verticalBar_Right);
        verticalRight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        verticalLeft = (SeekBar)findViewById(R.id.verticalBar_Left);
        verticalLeft.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        verticalLeft.setVisibility(View.INVISIBLE);
        verticalRight.setVisibility(View.INVISIBLE);
        longHorizontal.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent me) {
        float x = me.getX();
        float y = me.getY();

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

    }

    public void radioRightButtonClick(View v) {
        verticalLeft.setVisibility(View.INVISIBLE);
        verticalRight.setVisibility(View.VISIBLE);
        longHorizontal.setVisibility(View.INVISIBLE);
    }

    public void radioLeftButtonClick(View v) {
        verticalLeft.setVisibility(View.VISIBLE);
        verticalRight.setVisibility(View.INVISIBLE);
        longHorizontal.setVisibility(View.INVISIBLE);
    }

    public void radioLongButtonClick(View v) {
        longHorizontal.getLayoutParams().width = 1720;
        longHorizontal.requestLayout();
        verticalLeft.setVisibility(View.INVISIBLE);
        verticalRight.setVisibility(View.INVISIBLE);
        longHorizontal.setVisibility(View.VISIBLE);
    }

    public void radioShortButtonClick(View v) {
        longHorizontal.getLayoutParams().width = 860;
        longHorizontal.requestLayout();
        verticalLeft.setVisibility(View.INVISIBLE);
        verticalRight.setVisibility(View.INVISIBLE);
        longHorizontal.setVisibility(View.VISIBLE);
    }
}
