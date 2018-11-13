package ca.yorku.cse.mack.fittstouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
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

    Target startCircle;

    float panelWidth;
    float panelHeight;

    float d; // diameter of start circle (also used for positioning circle and text)
    float textSize;
    float gap;
    boolean freezing, waitStartCircleSelect, done, showFingerCombination, showNextValue, isZoomIn;

    Paint targetPaint, targetRimPaint, startPaint;
    Paint checkPaint, freezingPaint, fingerPaint, confirmationPaint, arrowPaint;

    String[] resultsString = {""};
    String instructionString = "Tap to continue";
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
        textSize = START_TEXT_SIZE * pixelDensity;
        gap = GAP_BETWEEN_LINES * pixelDensity;

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

        confirmationPaint = new Paint();
        confirmationPaint.setColor(Color.parseColor("#89B74E"));
        confirmationPaint.setStyle(Paint.Style.FILL);
        confirmationPaint.setAntiAlias(true);
        confirmationPaint.setTextSize(textSize);

        checkPaint = new Paint();
        checkPaint.setColor(Color.parseColor("#89B74E"));
        checkPaint.setStyle(Paint.Style.STROKE);
        checkPaint.setStrokeWidth(28);
        checkPaint.setAntiAlias(true);

        arrowPaint = new Paint();
        arrowPaint.setColor(Color.parseColor("#89B74E"));
        arrowPaint.setStyle(Paint.Style.STROKE);
        arrowPaint.setStrokeWidth(28);
        arrowPaint.setAntiAlias(true);

        fingerPaint = new Paint();
        fingerPaint.setColor(0xff0000ff);
        fingerPaint.setStyle(Paint.Style.FILL);
        fingerPaint.setAntiAlias(true);
        fingerPaint.setTextSize(textSize * 3);
    }

    public void setStartTarget() {
        startCircle = new Target(Target.CIRCLE, d, panelHeight - d, d, d, Target.NORMAL);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (waitStartCircleSelect) // draw start circle and prompt/results string
        {
            startPaint.setTextSize(textSize);
            canvas.drawCircle(startCircle.xCenter, startCircle.yCenter, startCircle.width / 2f,
                    startPaint);
            canvas.drawText(instructionString, d / 2, panelHeight - d - 2 * (textSize + gap), startPaint);
            for (int i = 0; i < resultsString.length; ++i)
                canvas.drawText(resultsString[i], d / 2, d / 2 + (i + 1)
                        * (textSize + gap), startPaint);
        } else {
            Paint p = freezing ? (showNextValue ? freezingPaint : confirmationPaint) : startPaint;
            p.setTextSize(textSize * 2);
            canvas.drawText("Current", panelWidth / 4, panelHeight / 5 * 2 - 100, p);
            p.setTextSize(textSize * 5);
            canvas.drawText(valueString[0], panelWidth / 2, panelHeight / 5 * 2, p);
            p.setTextSize(textSize * 2);
            canvas.drawText("Zoom to", panelWidth / 4, panelHeight / 5 * 4 - 100, p);
            p.setTextSize(textSize * 5);
            canvas.drawText(valueString[1], panelWidth / 2, panelHeight / 5 * 4, p);

            // draw direction arrows
            if (showNextValue || !freezing) {
                arrowPaint.setColor(p.getColor());
                drawDirectionArrows(canvas);
            }
        }

        if (freezing && !showNextValue) {
            canvas.drawLine(panelWidth / 4 * 3, panelHeight / 2,
                    panelWidth / 4 * 3 + 100, panelHeight / 2 + 100, checkPaint);

            canvas.drawLine(panelWidth / 4 * 3 + 90,
                    panelHeight / 2 + 95,
                    panelWidth / 4 * 3 + 220,
                    panelHeight / 2 - 100, checkPaint);
        }

        if (showFingerCombination) {
            float w = fingerPaint.measureText(combinationString, 0, combinationString.length());
            canvas.drawText(combinationString, (panelWidth - w) / 2, panelHeight / 2 , fingerPaint);
        }
        invalidate(); // will cause onDraw to run again immediately
    }

    void drawDirectionArrows(Canvas canvas) {
        canvas.drawLine(panelWidth / 4 * 3, panelHeight / 2 - 50,
                panelWidth / 4 * 3, panelHeight / 2 - 250, arrowPaint);

        canvas.drawLine(panelWidth / 4 * 3, panelHeight / 2 + 50,
                panelWidth / 4 * 3, panelHeight / 2 + 250, arrowPaint);
        if (isZoomIn) {
            canvas.drawLine(panelWidth / 4 * 3 + 7, panelHeight / 2 - 250,
                    panelWidth / 4 * 3 - 50, panelHeight / 2 - 200, arrowPaint);

            canvas.drawLine(panelWidth / 4 * 3 - 7, panelHeight / 2 - 250,
                    panelWidth / 4 * 3 + 50, panelHeight / 2 - 200, arrowPaint);

            canvas.drawLine(panelWidth / 4 * 3 + 7, panelHeight / 2 + 250,
                    panelWidth / 4 * 3 - 50, panelHeight / 2 + 200, arrowPaint);

            canvas.drawLine(panelWidth / 4 * 3 - 7, panelHeight / 2 + 250,
                    panelWidth / 4 * 3 + 50, panelHeight / 2 + 200, arrowPaint);
        } else {
            canvas.drawLine(panelWidth / 4 * 3 + 7, panelHeight / 2 - 50,
                    panelWidth / 4 * 3 - 50, panelHeight / 2 - 100, arrowPaint);

            canvas.drawLine(panelWidth / 4 * 3 - 7, panelHeight / 2 - 50,
                    panelWidth / 4 * 3 + 50, panelHeight / 2 - 100, arrowPaint);

            canvas.drawLine(panelWidth / 4 * 3 + 7, panelHeight / 2 + 50,
                    panelWidth / 4 * 3 - 50, panelHeight / 2 + 100, arrowPaint);

            canvas.drawLine(panelWidth / 4 * 3 - 7, panelHeight / 2 + 50,
                    panelWidth / 4 * 3 + 50, panelHeight / 2 + 100, arrowPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension((int) panelWidth, (int) panelHeight);
    }
}