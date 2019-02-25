package ca.yorku.cse.mack.fittstouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Panel extends View
{
    final int START_TEXT_SIZE = 14; // may need to fiddle with this, depending on device
    final int START_CICLE_DIAMETER = 53; // x pixelDensity = one-third inch
    final int GAP_BETWEEN_LINES = 6;
    final int VISIBILITY_TEXT_SIZE = 13;

    Target startCircle;

    Paint restPaint;

    boolean isResting;

    float panelWidth;
    float panelHeight;

    float d; // diameter of start circle (also used for positioning circle and text)
    float textSize, visibilitySize;
    float gap;

    String[] restTimer = {"Break ends in", "0:30"};

    public Panel(Context contextArg)
    {
        super(contextArg);
        initialize(contextArg);
    }

    public Panel(Context contextArg, AttributeSet attrs)
    {
        super(contextArg, attrs);
        initialize(contextArg);
    }

    public Panel(Context contextArg, AttributeSet attrs, int defStyle)
    {
        super(contextArg, attrs, defStyle);
        initialize(contextArg);
    }

    // things that can be initialized from within this View
    protected void initialize(Context c)
    {
        setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        this.setBackgroundColor(Color.LTGRAY);

        float pixelDensity = c.getResources().getDisplayMetrics().density;
        d = START_CICLE_DIAMETER * pixelDensity;
        textSize = START_TEXT_SIZE * pixelDensity;
        visibilitySize = VISIBILITY_TEXT_SIZE * pixelDensity;
        gap = GAP_BETWEEN_LINES * pixelDensity;

        restPaint = new Paint();
        restPaint.setColor(0xff0000ff);
        restPaint.setStyle(Paint.Style.FILL);
        restPaint.setAntiAlias(true);
        restPaint.setTextSize(textSize * 3);
    }

    public void setStartTarget() {
        startCircle = new Target(Target.CIRCLE, panelWidth / 2, panelHeight - d, d, d, Target.NORMAL);
    }

    public void hideToolBar() {
        setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {

    }

    void drawRestTimer(Canvas canvas) {
        float w = restPaint.measureText(restTimer[0], 0, restTimer[0].length()) / 2;
        canvas.drawText(restTimer[0], panelWidth / 2 - w, panelHeight / 2 - gap, restPaint);
        w = restPaint.measureText(restTimer[1], 0, restTimer[1].length()) / 2;
        canvas.drawText(restTimer[1], panelWidth / 2 - w, panelHeight / 2 + textSize * 2 + gap * 2, restPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension((int) panelWidth, (int) panelHeight);
    }
}