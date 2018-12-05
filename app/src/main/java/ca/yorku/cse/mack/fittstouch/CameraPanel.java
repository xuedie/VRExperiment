package ca.yorku.cse.mack.fittstouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * ExperimentPanel -- panel to present and sequence the targets
 * <p>
 *
 * @author Scott MacKenzie
 */
public class CameraPanel extends Panel
{
    Target[] targetSet;
    Target startCircle;

    int panelColumns, panelRows;

    Paint targetPaint, targetRimPaint, normalPaint, startPaint, gridPaint, destRimPaint, seletedPaint, draggingTargetRimPaint;
    Paint crossPaint, fingerPaint, visibilityPaint;

    public CameraPanel(Context contextArg)
    {
        super(contextArg);
        initialize(contextArg);
    }

    public CameraPanel(Context contextArg, AttributeSet attrs)
    {
        super(contextArg, attrs);
        initialize(contextArg);
    }

    public CameraPanel(Context contextArg, AttributeSet attrs, int defStyle)
    {
        super(contextArg, attrs, defStyle);
        initialize(contextArg);
    }

    // things that can be initialized from within this View
    protected void initialize(Context c)
    {
        super.initialize(c);

        startCircle = new Target(Target.CIRCLE, d, d, d, d, Target.NORMAL);

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
        fingerPaint.setTextSize(textSize);

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

        visibilityPaint = new Paint();
        visibilityPaint.setColor(0xff0000ff);
        visibilityPaint.setStyle(Paint.Style.FILL);
        visibilityPaint.setAntiAlias(true);
        visibilityPaint.setTextSize(visibilitySize);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        {
            for (int i = 0; i < panelColumns * panelRows; ++i)
            {
                Target value = targetSet[i];
                //canvas.drawOval(value.r, normalPaint);
                crossPaint.setColor(Color.BLACK);
                canvas.drawLine(value.xCenter - value.displayWidth / 2,
                        value.yCenter,
                        value.xCenter + value.displayWidth / 2,
                        value.yCenter, crossPaint);

                canvas.drawLine(value.xCenter,
                        value.yCenter - value.displayWidth / 2,
                        value.xCenter,
                        value.yCenter + value.displayWidth / 2, crossPaint);

                canvas.drawText(String.valueOf(i+1), value.xCenter - value.displayWidth, value.yCenter - value.displayWidth / 2 , fingerPaint);
            }
        }
        invalidate(); // will cause onDraw to run again immediately
    }
}