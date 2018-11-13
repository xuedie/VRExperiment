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
public class ExperimentPanel extends View
{
    final int START_TEXT_SIZE = 14; // may need to fiddle with this, depending on device
    final int START_CICLE_DIAMETER = 53; // x pixelDensity = one-third inch
    final int GAP_BETWEEN_LINES = 6;

    Target[] targetSet;
    Target toTarget; // the target to select
    Target fromTarget; // the source target from where the trial began
    Target startCircle;
    Target destTarget;
    Target draggingTarget;

    float panelWidth;
    float panelHeight;

    int panelColumns, panelRows;

    float d; // diameter of start circle (also used for positioning circle and text)
    float textSize;
    float gap;

    boolean waitStartCircleSelect, done, isDragging, showFingerCombination;
    String moveMode;
    Paint targetPaint, targetRimPaint, normalPaint, startPaint, gridPaint, destRimPaint, seletedPaint, draggingTargetRimPaint;
    Paint crossPaint, fingerPaint;
    String instructionString = "Tap to continue";
    String[] resultsString = {""};
    String[] combination = {"Right Index Finger", "Left Index Finger", "Both Thumbs"};
    String combinationString = "Right Index Finger";

    public ExperimentPanel(Context contextArg)
    {
        super(contextArg);
        initialize(contextArg);
    }

    public ExperimentPanel(Context contextArg, AttributeSet attrs)
    {
        super(contextArg, attrs);
        initialize(contextArg);
    }

    public ExperimentPanel(Context contextArg, AttributeSet attrs, int defStyle)
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
        draggingTarget = new Target(Target.CIRCLE, d, d, d, d, Target.NORMAL);
        textSize = START_TEXT_SIZE * pixelDensity;
        gap = GAP_BETWEEN_LINES * pixelDensity;

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

        fingerPaint = new Paint();
        fingerPaint.setColor(0xff0000ff);
        fingerPaint.setStyle(Paint.Style.FILL);
        fingerPaint.setAntiAlias(true);
        fingerPaint.setTextSize(textSize * 3);

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
        crossPaint.setColor(Color.parseColor("#FFE7E0"));
        crossPaint.setStyle(Paint.Style.STROKE);
        crossPaint.setStrokeWidth(10);
        crossPaint.setAntiAlias(true);
    }

    public void setStartTarget() {
        startCircle = new Target(Target.CIRCLE, d, panelHeight - d, d, d, Target.NORMAL);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (waitStartCircleSelect) // draw start circle and prompt/results string
        {
            canvas.drawCircle(startCircle.xCenter, startCircle.yCenter, startCircle.width / 2f,
                    startPaint);
            canvas.drawText(instructionString, d / 2, panelHeight - d - 2 * (textSize + gap), startPaint);
            for (int i = 0; i < resultsString.length; ++i)
                canvas.drawText(resultsString[i], d / 2, d / 2 + (i + 1)
                        * (textSize + gap), startPaint);
        } else if (!done) // draw task targets
        {
            for (Target value : targetSet)
            {
                //canvas.drawOval(value.r, normalPaint);
                crossPaint.setColor(Color.parseColor("#FFE7E0"));
                canvas.drawLine(value.xCenter - value.displayWidth / 2,
                        value.yCenter,
                        value.xCenter + value.displayWidth / 2,
                        value.yCenter, crossPaint);

                canvas.drawLine(value.xCenter,
                        value.yCenter - value.displayWidth / 2,
                        value.xCenter,
                        value.yCenter + value.displayWidth / 2, crossPaint);
            }

            // draw target to select last (so it is on top of any overlapping targets)
            crossPaint.setColor(Color.BLACK);
            canvas.drawLine(toTarget.xCenter - toTarget.displayWidth / 2,
                    toTarget.yCenter,
                    toTarget.xCenter + toTarget.displayWidth / 2,
                    toTarget.yCenter, crossPaint);

            canvas.drawLine(toTarget.xCenter,
                    toTarget.yCenter - toTarget.displayWidth / 2,
                    toTarget.xCenter,
                    toTarget.yCenter + toTarget.displayWidth / 2, crossPaint);
        }
        if (showFingerCombination) {
            float w = fingerPaint.measureText(combinationString, 0, combinationString.length());
            canvas.drawText(combinationString, (panelWidth - w) / 2, panelHeight / 2 , fingerPaint);
        }
        invalidate(); // will cause onDraw to run again immediately
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension((int) panelWidth, (int) panelHeight);
    }
}