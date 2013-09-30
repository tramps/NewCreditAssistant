package com.rong360.creditassitant.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.DisplayUtils;

public class MovingBarView extends View {
    private static final String TAG = "MovingBarView";

    // private int mWidth;
    // private int mHeight;
    // private int mColor;
    // private int mAlpha;

    private Paint mPaint;
    private Paint mWhitePain;
    private Rect mRect;
    private Drawable mSlider;
    private Rect mFixedRect;

    private int mScreenWidth;

    private float mMovingOffset = 0;
    private int mSingleViewWidth = 0;

    private boolean mIsDraging = false;
    private float mLastMotionX = 0;

    private HorizontalListView mHlv;

    private int mHlvOffset = 0;
    private int mIndex = 0;
    private String[] mProgress;
    private IProgressChanged onProgressChanged;
    private boolean mIsFirst = true;

    public MovingBarView(Context context) {
	this(context, null);
    }

    public MovingBarView(Context context, AttributeSet attrs) {
	this(context, attrs, R.attr.MyMovingBar);
    }

    @SuppressLint("NewApi")
    public MovingBarView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);

	// TypedArray ta = context.obtainStyledAttributes(attrs,
	// R.styleable.MovingBar, defStyle, 0);
	// mWidth = ta.getDimensionPixelOffset(R.styleable.MovingBar_width, 60);
	// mHeight = ta.getDimensionPixelOffset(R.styleable.MovingBar_height,
	// 60);
	// mColor = ta
	// .getDimensionPixelSize(R.styleable.MovingBar_color, 0xffff00);
	// mAlpha = ta.getDimensionPixelOffset(R.styleable.MovingBar_alpha,
	// 155);

	mPaint = new Paint();
	mPaint.setAntiAlias(true);
	mPaint.setAlpha(0);
	mPaint.setColor(Color.BLACK);
	mPaint.setStrokeWidth(2);

	mWhitePain = new Paint();
	mWhitePain.setAntiAlias(true);
	mWhitePain.setColor(Color.WHITE);
	mWhitePain.setTextSize(30);

	mScreenWidth = DisplayUtils.getScreenWidth(context);
	mSlider = context.getResources().getDrawable(R.drawable.ic_slide);
	// Log.i(TAG, "screen width: " + mScreenWidth);
	// mBarWidth = mWidth;
	// mInitialOffset = mRect.centerX();
	mProgress = context.getResources().getStringArray(R.array.progress);
    }

    public void setHorizontalListview(HorizontalListView hlv) {
	mHlv = hlv;
    }

    public void setOnProgressChangedListener(IProgressChanged iProgress) {
	onProgressChanged = iProgress;
    }

    public void initRect(int width, int height, int offY) {
	// Log.i(TAG, "width: " + width + " height: " + height);
	// width = 230;
	// height = 120;
	mSingleViewWidth = width;
	int w = mSlider.getIntrinsicWidth();
	int h = mSlider.getIntrinsicHeight();
	// Log.i(TAG, "w" + w + " h" + h);
	int margin = height - h;
	mRect = new Rect(0, margin / 2, w, h);
	// Log.i(TAG, mRect.toString());
	if (mRect.left < 10) {
	    // mRect = new RectF(0, 0, width, height);
	    int offset = (int) ((mScreenWidth - mRect.width()) / 2);
	    mRect.offset(offset, 0);
	    // mRect.offset(offset, 0);
	}
	// Log.i(TAG, mRect.toString());
	if (mFixedRect == null) {
	    mFixedRect = new Rect(mRect);
	    // Log.i(TAG, "init");
	}

	if (mIndex > 0 && mIsFirst) {
	    mHlvOffset = (mIndex) * mSingleViewWidth;
	    Log.i(TAG, "offset:" + mHlvOffset);
	    mHlv.setCurrentX(mHlvOffset);
	    mHlv.setHistoryOffset(mHlvOffset);
	    // invalidate();
	    mIsFirst = false;
	}

    }

    public void setIndex(int index) {
	mIndex = index;
    }

    public void setIsFirst(Boolean isFirst) {
	mIsFirst = isFirst;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	// if (mHlv != null) {
	// int[] position = new int[2];
	// mHlv.getLocationOnScreen(position);
	// Log.i(TAG, "left: " + position[0] + " top: " + position[1]);
	// mRect.offset(0, position[1]);
	// }
    }

    @Override
    protected void onDraw(Canvas canvas) {
	// Log.i(TAG, "ondraw:" + mRect.toString());
	mSlider.setBounds(mRect);
	mSlider.draw(canvas);
	// canvas.drawRect(mRect, mPaint);
	canvas.drawText(mProgress[mIndex], mRect.centerX() - 55,
		mRect.centerY() + 10, mWhitePain);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
	    int bottom) {
	super.onLayout(changed, left, top, right, bottom);
	// Log.i(TAG, "layout" + left + " " + top + " " + right + " " + bottom);
	// if (mMovingOffset == 0) {
	// int offset = (mScreenWidth - mBarWidth) / 2;
	// Log.i(TAG, "offset" + offset);
	// mRect.offset(offset, 0);
	// mMovingOffset = offset;
	// } else {
	// mRect.offset(mMovingOffset, 0);
	// }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
	int aciont = event.getAction();
	// Log.i(TAG, "action" + aciont + " motionX " + lastMotionX);
	switch (aciont) {
	case MotionEvent.ACTION_DOWN:
	    mIsDraging = isInRect((int) event.getX(), (int) event.getY());
	    // Log.i(TAG, "isdragging: " + mIsDraging);
	    if (mIsDraging) {
		mLastMotionX = event.getX();
	    } else {
		return false;
	    }
	    // invalidate();
	    break;
	case MotionEvent.ACTION_MOVE:
	    if (!mIsDraging) {
		return false;
	    }
	    // Log.i(TAG, "last:" + mLastMotionX + " now: " + event.getX());
	    mMovingOffset = event.getX() - mLastMotionX;
	    // Log.i(TAG, "move offset" + mMovingOffset);
	    mRect.offset((int) mMovingOffset, 0);
	    mLastMotionX = event.getX();
	    // Log.i(TAG, "lastMotionOffsetX" + mLastMotionX);
	    // int moving = (int) (mLastMotionX - mScreenWidth / 2);
	    // Log.i(TAG, "hlv scrollto:" + moving);
	    // mHlv.scrollTo(mHlvOffset + moving);
	    invalidate();
	    break;
	case MotionEvent.ACTION_UP:
	case MotionEvent.ACTION_CANCEL:
	    // float movingOffset = mLastMotionX;
	    boolean shallMove = false;
	    int recWidth = (int) mRect.width() / 3;
	    if (mLastMotionX > mScreenWidth / 2 + recWidth) {
		mHlvOffset += mSingleViewWidth;
		mIndex++;
		shallMove = true;
		onProgressChanged();
	    } else if (mLastMotionX < mScreenWidth / 2 - recWidth) {
		mHlvOffset -= mSingleViewWidth;
		mIndex--;
		shallMove = true;
		onProgressChanged();
	    }

	    if (mIndex == mProgress.length) {
		mIndex = mProgress.length - 1;
		mHlvOffset -= mSingleViewWidth;
		shallMove = false;
	    } else if (mIndex < 0) {
		mHlvOffset += mSingleViewWidth;
		mIndex = 0;
		shallMove = false;
	    }

	    if (shallMove) {
		moveBottom(mHlvOffset);
		Log.i(TAG, mIndex + "mhlvoffset:" + mHlvOffset);
	    }

	    post(new SlideBackAnim());
	    mIsDraging = false;
	    break;
	}
	return true;
    }

    private void onProgressChanged() {
	Log.i(TAG, "index: " + mIndex);

	if (mIndex < mProgress.length && mIndex >= 0) {
	    if (onProgressChanged != null) {
		Log.i(TAG, "onprogresschanged");
		onProgressChanged.onProgressChanged(mIndex);
	    }
	}
    }

    private class SlideBackAnim extends Thread {
	int mOffsetX;
	int mTime = 200;

	public SlideBackAnim() {
	    mRect.set(mFixedRect);
	    // Log.i(TAG, "offsetX:" + mOffsetX);
	}

	@Override
	public void run() {
	    // Log.i(TAG, "slide back anim" + mOffsetX);
	    // mRect.offset(mOffsetX, 0);
	    invalidate();
	}
    }

    private void moveBottom(float movingOffset) {
	int offset = (int) movingOffset;
	mHlv.scrollTo(offset);
	mHlv.setCurrentX(offset);
	mHlv.setHistoryOffset(offset);

//	setText();
    }

    private void setText() {
	int left = mIndex - 2;
	int center = mIndex - 1;
	if (left > 0) {
	    TextView tv = getChildTextView(mHlv.getChildAt(left));
	    if (tv != null) {
		tv.setText(mProgress[left]);
	    }
	}

	if (center > 0) {
	    TextView tv = getChildTextView(mHlv.getChildAt(center));
	    if (tv != null) {
		tv.setText(mProgress[center]);
	    }
	}

	TextView tv = getChildTextView(mHlv.getChildAt(mIndex-1));
	if (tv != null) {
	    tv.setText(mProgress[mIndex]);
	}
    }
    
    
    private TextView getChildTextView (View parent) {
	if (parent == null) {
	    return null;
	}
	if (parent instanceof ViewGroup) {
	    ViewGroup group = (ViewGroup) parent;
	    for (int i = 0; i < group.getChildCount(); i++) {
		View child = group.getChildAt(i);
		if (child instanceof TextView) {
		    return (TextView)child;
		} else if (child instanceof ViewGroup){
		    return getChildTextView(child);
		} else {
		    continue;
		}
	    }
	}
	
	return null;
    }

    private boolean isInRect(int x, int y) {
	return mRect.contains(x, y);
    }

    public static interface IProgressChanged {
	public void onProgressChanged(int index);
    }
}
