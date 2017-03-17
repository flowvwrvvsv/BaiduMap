package baidumapsdk.demo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import baidumapsdk.demo.R;

public class CircleScrollLayout extends ViewGroup
{

	private Bitmap imageOriginal, imageScaled;
	private Matrix matrix;
	private GestureDetector mGesuteDetetor;

	private int angle = 90;
	private int firstChildPos = 90;
	private int maxChildWidth;
	private int maxChildHeight;
	private int radius = 0;

	private int childWidth;
	private int childHeight;

	private boolean bRotate;
	private boolean bRotating;
	private int circleWidth;
	private int circleHeight;

	public CircleScrollLayout(Context context)
	{
		super(context);
	}

	public CircleScrollLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initAttr(attrs);
	}

	public CircleScrollLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initAttr(attrs);
	}

	private void initAttr(AttributeSet att)
	{
		mGesuteDetetor = new GestureDetector(null);
		if(att != null)
		{
			TypedArray a = getContext().obtainStyledAttributes(att, R.styleable.Circle);

			angle = a.getInt(R.styleable.Circle_firstChildPosition, 90);
			firstChildPos = angle;

			bRotate = a.getBoolean(R.styleable.Circle_rotateToCenter, true);
			bRotating = a.getBoolean(R.styleable.Circle_isRotating, true);

			if(!bRotating)
				bRotate = false;

			if(imageOriginal == null)
			{
				int pid = a.getResourceId(R.styleable.Circle_circleBackground, -1);

				if(pid != -1)
					imageOriginal = BitmapFactory.decodeResource(getResources(), pid);
			}

			a.recycle();

			if(matrix == null)
				matrix = new Matrix();
			else
				matrix.reset();

			setWillNotDraw(false);
		}

	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		circleWidth = getWidth();
		circleHeight = getHeight();

		if(imageOriginal != null)
		{
			if(imageScaled == null)
			{
				matrix = new Matrix();
				float sx = ((radius + childWidth / 4) * 2) / (float)(imageOriginal.getWidth());
				float sy = ((radius + childHeight / 4) * 2) / (float)(imageOriginal.getHeight());
				matrix.postScale(sx,sy);
				imageScaled = Bitmap.createBitmap(imageOriginal,0,0,imageOriginal.getWidth(),imageOriginal.getHeight(),matrix,false);
			}

			if(imageScaled != null)
			{
				int cx = (circleWidth - imageScaled.getWidth()) / 2;
				int cy = (circleHeight - imageScaled.getHeight()) / 2;

				Canvas g = canvas;
				g.rotate(0,circleWidth/2,circleHeight/2);
				g.drawBitmap(imageScaled,cx,cy,null);
			}
		}
	}

	@Override
	public void childHasTransientStateChanged(View child, boolean childHasTransientState)
	{
		super.childHasTransientStateChanged(child, childHasTransientState);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		maxChildWidth = 0;
		maxChildHeight = 0;

		int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
		int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST);

		final int count = getChildCount();
		for(int x = 0; x < count; x++)
		{
			final View child = getChildAt(x);
			if(child.getVisibility() == View.GONE)
				continue;

			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

			maxChildWidth = Math.max(maxChildWidth, child.getMeasuredWidth());
			maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
		}

		childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(maxChildWidth, MeasureSpec.EXACTLY);
		childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxChildHeight, MeasureSpec.EXACTLY);

		for(int x = 0; x < count; x++)
		{
			final View child = getChildAt(x);
			if(child.getVisibility() == View.GONE)
				continue;

			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
		}

		setMeasuredDimension(resolveSize(maxChildWidth, widthMeasureSpec), resolveSize(maxChildHeight, heightMeasureSpec));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		int layoutWidth = r - l;
		int layoutHeight = b - t;

		final int count = getChildCount();

		int left, top;

		radius = (layoutHeight > layoutWidth) ? layoutHeight / 3 : layoutWidth / 3;

		int angleDelay = 360 / count;

		childWidth = (int)(radius / 1.5);
		childHeight = (int)(radius / 1.5);

		for(int x = 0; x < count; x++)
		{
			final CircleImageView child = (CircleImageView)getChildAt(count);

			if(child.getVisibility() == View.GONE)
				continue;

			if(angle > 360)
				angle -= 360;
			else if(angle < 0)
				angle += 360;

			child.setAngle(angle);
			child.setPosition(x);

			left = (int)Math.round((float)(layoutWidth / 2 - childWidth / 2) + radius + Math.cos(Math.toRadians(angle)));
			top = (int)Math.round((float)(layoutHeight / 2 - childHeight / 2) + radius + Math.sin(Math.toRadians(angle)));

			child.layout(left, top, left + childWidth, top + childHeight);

			angle += angleDelay;
		}

	}

	@Override
	public boolean hasTransientState()
	{
		return super.hasTransientState();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		circleWidth = getWidth();
		circleHeight = getHeight();

		if(imageOriginal != null)
		{

		}

		return super.onTouchEvent(event);
	}

	private int getAngle(int x, int y)
	{
		int angle = 0;

		angle = (x - radius);


		return 0;
	}
}
