package ca.yorku.cse.mack.fittstouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

public class ScrollPanel extends Panel
{
    Target startCircle;

    boolean freezing, waitStartCircleSelect, done, showFingerCombination;
    boolean isVisibilityTest;

    Paint startPaint;
    Paint checkPaint, freezingPaint, fingerPaint, confirmationPaint, arrowPaint, visibilityPaint;

    String[] visibilityWords = {"zh","kx","ov","hw","ui","eg","ut","gf"};
    String[] resultsString = {""};
    String instructionString = "Tap to continue";
    String[] valueString = {"50", "50"};
    String[] combination = {"Index and Middle Fingers", "Index Finger and Thumb", "Both Thumbs", "Both Index Fingers"};
    String combinationString = "Right Index Finger";

    public ScrollPanel(Context contextArg)
    {
        super(contextArg);
        initialize(contextArg);
    }

    public ScrollPanel(Context contextArg, AttributeSet attrs)
    {
        super(contextArg, attrs);
        initialize(contextArg);
    }

    public ScrollPanel(Context contextArg, AttributeSet attrs, int defStyle)
    {
        super(contextArg, attrs, defStyle);
        initialize(contextArg);
    }

    // things that can be initialized from within this View
    protected void initialize(Context c)
    {
        super.initialize(c);

        freezing = false;

        startPaint = new Paint();
        startPaint.setColor(0xff0000ff);
        startPaint.setStyle(Paint.Style.FILL);
        startPaint.setAntiAlias(true);
        startPaint.setTextSize(textSize);

        visibilityPaint = new Paint();
        visibilityPaint.setColor(0xff0000ff);
        visibilityPaint.setStyle(Paint.Style.FILL);
        visibilityPaint.setAntiAlias(true);
        visibilityPaint.setTextSize(visibilitySize);

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
        startCircle = new Target(Target.CIRCLE, panelWidth / 2, panelHeight - d, d, d, Target.NORMAL);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (isVisibilityTest) {
            drawVisibilityTest(canvas);
            invalidate();
            return;
        }

        if (isResting) {
            drawRestTimer(canvas);
            invalidate();
            return;
        }

        if (waitStartCircleSelect) // draw start circle and prompt/results string
        {
            startPaint.setTextSize(textSize);
            float tw = startPaint.measureText(instructionString, 0, instructionString.length()) / 2;
            canvas.drawCircle(startCircle.xCenter, startCircle.yCenter, startCircle.width / 2f,
                    startPaint);
            canvas.drawText(instructionString, panelWidth / 2 - tw, panelHeight - d - 2 * (textSize + gap), startPaint);
        }

        if (showFingerCombination) {
            float w = fingerPaint.measureText(combinationString, 0, combinationString.length());
            canvas.drawText(combinationString, (panelWidth - w) / 2, panelHeight / 2 , fingerPaint);
        }
        invalidate(); // will cause onDraw to run again immediately
    }

    void drawVisibilityTest(Canvas canvas) {
        float w = visibilityPaint.measureText(visibilityWords[0], 0, visibilityWords[0].length()) / 2;
        canvas.drawText(visibilityWords[0], gap, gap * 2, visibilityPaint);
        canvas.drawText(visibilityWords[1], panelWidth / 2 - w, gap * 2, visibilityPaint);
        canvas.drawText(visibilityWords[2], panelWidth - gap - w * 2, gap * 2, visibilityPaint);

        // middle
        canvas.drawText(visibilityWords[3], gap, panelHeight / 2, visibilityPaint);
        canvas.drawText(visibilityWords[4], panelWidth - gap - w * 2, panelHeight / 2, visibilityPaint);

        // bottom
        canvas.drawText(visibilityWords[5], gap, panelHeight - gap, visibilityPaint);
        canvas.drawText(visibilityWords[6], panelWidth / 2 - w, panelHeight - gap, visibilityPaint);
        canvas.drawText(visibilityWords[7], panelWidth - gap - w * 2, panelHeight - gap, visibilityPaint);
    }
}