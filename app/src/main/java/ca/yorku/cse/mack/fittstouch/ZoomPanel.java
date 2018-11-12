package ca.yorku.cse.mack.fittstouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * ExperimentPanel -- panel to present and sequence the targets
 * <p>
 *
 * @author Scott MacKenzie
 */
public class ZoomPanel extends View
{
    final int START_TEXT_SIZE = 14; // may need to fiddle with this, depending on device
    final int START_CICLE_DIAMETER = 53; // x pixelDensity = one-third inch
    final int GAP_BETWEEN_LINES = 6;

    Target zoomTarget;
    Target startCircle;

    float panelWidth;
    float panelHeight;

    float d; // diameter of start circle (also used for positioning circle and text)
    float textSize;
    float gap;
    boolean freezing, waitStartCircleSelect, done, showFingerCombination, showNextValue;
    int waitSec;

    Paint targetPaint, targetRimPaint, normalPaint, startPaint, gridPaint, destRimPaint, seletedPaint, draggingTargetRimPaint;
    Paint crossPaint, freezingPaint, fingerPaint;

    String[] resultsString = {"Tap to continue"};
    String[] valueString = {"50", "50"};
    String[] combination = {"Index and Middle Fingers", "Index Finger and Thumb", "Both Thumbs", "Both Index Fingers"};
    String combinationString = "Right Index Finger";

    public ZoomPanel(Context contextArg)
    {
        super(contextArg);
        initialize(contextArg);
    }

    public ZoomPanel(Context contextArg, AttributeSet attrs)
    {
        super(contextArg, attrs);
        initialize(contextArg);
    }

    public ZoomPanel(Context contextArg, AttributeSet attrs, int defStyle)
    {
        super(contextArg, attrs, defStyle);
        initialize(contextArg);
    }

    // things that can be initialized from within this View
    private void initialize(Context c)
    {
        this.setBackgroundColor(Color.LTGRAY);

        float pixelDensity = c.getResources().getDisplayMetrics().density;
        d = START_CICLE_DIAMETER * pixelDensity;
        startCircle = new Target(Target.CIRCLE, d, d, d, d, Target.NORMAL);
        textSize = START_TEXT_SIZE * pixelDensity;
        gap = GAP_BETWEEN_LINES * pixelDensity;
        zoomTarget = new Target(Target.CIRCLE, 600, 600,
                d, d, Target.NORMAL);

        freezing = false;

        targetPaint = new Paint();
        targetPaint.setColor(0xffffaaaa);
        targetPaint.setStyle(Paint.Style.FILL);
        targetPaint.setAntiAlias(true);

        targetRimPaint = new Paint();
        targetRimPaint.setColor(Color.RED);
        targetRimPaint.setStyle(Paint.Style.STROKE);
        targetRimPaint.setStrokeWidth(2);
        targetRimPaint.setAntiAlias(true);

        draggingTargetRimPaint = new Paint();
        draggingTargetRimPaint.setColor(Color.MAGENTA);
        draggingTargetRimPaint.setStyle(Paint.Style.STROKE);
        draggingTargetRimPaint.setStrokeWidth(8);
        draggingTargetRimPaint.setAntiAlias(true);

        normalPaint = new Paint();
        normalPaint.setColor(0xffff9999); // lighter red (to minimize distraction)
        normalPaint.setStyle(Paint.Style.STROKE);
        normalPaint.setStrokeWidth(2);
        normalPaint.setAntiAlias(true);

        seletedPaint = new Paint();
        seletedPaint.setColor(Color.YELLOW); // lighter red (to minimize distraction)
        seletedPaint.setStyle(Paint.Style.STROKE);
        seletedPaint.setStrokeWidth(20);
        seletedPaint.setAntiAlias(true);

        startPaint = new Paint();
        startPaint.setColor(0xff0000ff);
        startPaint.setStyle(Paint.Style.FILL);
        startPaint.setAntiAlias(true);
        startPaint.setTextSize(textSize);

        freezingPaint = new Paint();
        freezingPaint.setColor(Color.DKGRAY);
        freezingPaint.setStyle(Paint.Style.FILL);
        freezingPaint.setAntiAlias(true);
        freezingPaint.setTextSize(textSize);

        gridPaint = new Paint();
        gridPaint.setColor(Color.GRAY); // lighter red (to minimize distraction)
        gridPaint.setStyle(Paint.Style.FILL);
        gridPaint.setAntiAlias(true);

        destRimPaint = new Paint();
        destRimPaint.setColor(Color.BLUE);
        destRimPaint.setStyle(Paint.Style.STROKE);
        destRimPaint.setStrokeWidth(8);
        destRimPaint.setAntiAlias(true);

        crossPaint = new Paint();
        crossPaint.setColor(Color.parseColor("#89B74E"));
        crossPaint.setStyle(Paint.Style.STROKE);
        crossPaint.setStrokeWidth(28);
        crossPaint.setAntiAlias(true);

        fingerPaint = new Paint();
        fingerPaint.setColor(0xff0000ff);
        fingerPaint.setStyle(Paint.Style.FILL);
        fingerPaint.setAntiAlias(true);
        fingerPaint.setTextSize(textSize * 3);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (waitStartCircleSelect) // draw start circle and prompt/results string
        {
            startPaint.setTextSize(textSize);
            canvas.drawCircle(startCircle.xCenter, startCircle.yCenter, startCircle.width / 2f,
                    startPaint);
            for (int i = 0; i < resultsString.length; ++i)
                canvas.drawText(resultsString[i], d / 2, d / 2 + 2 * startCircle.width / 2f + (i + 1)
                        * (textSize + gap), startPaint);
        } else if (showNextValue) {
            freezingPaint.setTextSize(textSize * 3);
            canvas.drawText("Next", panelWidth / 4, panelHeight / 4 * 3 - 100, freezingPaint);
            canvas.drawText(valueString[1], panelWidth / 2, panelHeight / 4 * 3 + gap, freezingPaint);
        } else {
            Paint p = freezing ? freezingPaint : startPaint;
            p.setTextSize(textSize * 3);
            canvas.drawText("Current", panelWidth / 4, panelHeight / 4 - 100, p);
            canvas.drawText(valueString[0], panelWidth / 2, panelHeight / 4 + gap, p);
            canvas.drawText("Zoom to", panelWidth / 4, panelHeight / 4 * 3 - 100, p);
            canvas.drawText(valueString[1], panelWidth / 2, panelHeight / 4 * 3 + gap, p);
            if (freezing) {
                canvas.drawLine(panelWidth / 4 * 3, panelHeight / 4,
                        panelWidth / 4 * 3 + 100, panelHeight / 4 + 100, crossPaint);

                canvas.drawLine(panelWidth / 4 * 3 + 90,
                        panelHeight / 4 + 95,
                        panelWidth / 4 * 3 + 220,
                        panelHeight / 4 - 100, crossPaint);
            }
        }

        if (showFingerCombination) {
            float w = fingerPaint.measureText(combinationString, 0, combinationString.length());
            canvas.drawText(combinationString, (panelWidth - w) / 2, panelHeight / 1.5f , fingerPaint);
        }
        invalidate(); // will cause onDraw to run again immediately
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension((int) panelWidth, (int) panelHeight);
    }
}