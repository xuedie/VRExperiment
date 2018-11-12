package ca.yorku.cse.mack.fittstouch;

import android.graphics.RectF;
import android.util.Log;

@SuppressWarnings("unused")
public class Target
{
	final static int NORMAL = 1;
	final static int TARGET = 2;
	final static int ALREADY_SELECTED = 3;
	final static int OTHER = 4;
	final static int DRAGGING = 5;
	final static int TAPPINGSELECTED = 6;
	final static int DRAGGINGSELECTED = 7;

	final static int RECTANGLE = 0;
	final static int CIRCLE = 1;

	public float xCenter, yCenter, width, height;
	public float displayWidth = 60f;
	RectF r;
	int status;
	int type;

	Target(int typeArg, float xCenterArg, float yCenterArg, float widthArg, float heightArg, int statusArg)
	{
		type = typeArg;
		r = new RectF(xCenterArg - widthArg / 2f, yCenterArg - heightArg / 2f, xCenterArg + widthArg / 2f, yCenterArg
				+ heightArg / 2f);
		xCenter = xCenterArg;
		yCenter = yCenterArg;
		width = widthArg;
		height = heightArg;
		status = statusArg;
	}

	public void copyTarget(Target origin)
	{
		type = origin.type;
		xCenter = origin.xCenter;
		yCenter = origin.yCenter;
		width = origin.width;
		height = origin.height;
		status = origin.status;
		r = new RectF(xCenter - width / 2f, yCenter - height / 2f, xCenter + width / 2f, yCenter
				+ height / 2f);
	}

	public boolean moveTarget(float xCenterArg, float yCenterArg)
	{
		r = new RectF(xCenterArg - width / 2f, yCenterArg - height / 2f, xCenterArg + width / 2f, yCenterArg
				+ height / 2f);
		xCenter = xCenterArg;
		yCenter = yCenterArg;
		return true;
	}

	/**
	 * Returns true if the specified coordinate is inside the target.
	 */
	public boolean inTarget(float xTest, float yTest)
	{
		//Log.i("MYDEBUG", "type=" + type);
		if (type == CIRCLE)
			return distanceFromTargetCenter(xTest, yTest) <= (width / 2f);
		else
			return r.contains(xTest, yTest);
	}

	public boolean circleInTarget(float xTest, float yTest)
	{
		//Log.i("MYDEBUG", "type=" + type);
		// if the dragging target is in destination target
		return distanceFromTargetCenter(xTest, yTest) <= (width);
	}

	public float distanceFromTargetCenter(float xTest, float yTest)
	{
		return (float) Math.sqrt((xCenter - xTest) * (xCenter - xTest) + (yCenter - yTest) * (yCenter - yTest));
	}
}