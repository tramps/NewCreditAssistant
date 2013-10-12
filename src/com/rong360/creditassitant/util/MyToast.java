package com.rong360.creditassitant.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rong360.creditassitant.R;

/**
 * custom toast util.
 */
public class MyToast extends Toast {
	private static final String TAG = "MyToast";
	private static MyToast mLastToast;
	private static int TOAST_BACKGROUND = android.R.drawable.toast_frame;

	public MyToast(Context context) {
		super(context);
	}

	public static void setBackgroundRes(int res) {
		TOAST_BACKGROUND = res;
	}

	public static MyToast makeText(Context context, String text, int duration) {
		MyToast toast = new MyToast(context);
		TextView tv = new TextView(context);
		tv.setBackgroundResource(TOAST_BACKGROUND);
		tv.setTextAppearance(context,
				android.R.style.TextAppearance_Small_Inverse);
		tv.setTextColor(Color.WHITE);
		tv.setGravity(Gravity.CENTER);
		tv.setShadowLayer(2.75f, 0.1f, 0.1f, 0xBB000000);
		tv.setText(text);

		toast.setView(tv);
		toast.setDuration(duration);
		return toast;
	}

	public static MyToast makeText(Context context, String text) {
		if (mLastToast == null
				|| mLastToast.getView().getWindowVisibility() != View.VISIBLE) {
			mLastToast = makeText(context, text, Toast.LENGTH_SHORT);
		} else {
			((TextView) (mLastToast.getView())).setText(text);
		}
		return mLastToast;
	}

	public static MyToast makeText(Context context, int resId) {
		return makeText(context, context.getResources().getString(resId));
	}
	
	public static void displayFeedback(Context context, int resId, String hint) {
	    Toast t = new Toast(context);
	    View v = LayoutInflater.from(context).inflate(R.layout.toast, null);
	    ImageView ivHint = (ImageView) v.findViewById(R.id.ivHint);
	    TextView tvHint = (TextView) v.findViewById(R.id.tvHint);
	    ivHint.setImageResource(resId);
	    tvHint.setText(hint);
	    t.setView(v);
	    t.setGravity(Gravity.CENTER, 0, 0);
	    t.setDuration(Toast.LENGTH_SHORT);
	    t.show();
	}
	
	public static void displayFeedback(Context context, int resId, String hint, View loV) {
	    Toast t = new Toast(context);
	    View v = LayoutInflater.from(context).inflate(R.layout.toast, null);
	    ImageView ivHint = (ImageView) v.findViewById(R.id.ivHint);
	    TextView tvHint = (TextView) v.findViewById(R.id.tvHint);
	    ivHint.setImageResource(resId);
	    tvHint.setText(hint);
	    t.setView(v);
	    v.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
		    LayoutParams.WRAP_CONTENT));
	    v.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    int[] location = new int[2];
	    int xPos = 0, rootWidth;

		loV.getLocationOnScreen(location);

		Rect anchorRect =
			new Rect(location[0], location[1], location[0]
				+ loV.getWidth(), location[1] + loV.getHeight());


		int screenWidth = DisplayUtils.getScreenHeight(context);
		int screenHeight = DisplayUtils.getScreenHeight(context);
		rootWidth = loV.getMeasuredWidth();
		// automatically get X coord of popup (top left)
		if ((anchorRect.left + rootWidth) > screenWidth) {
		    xPos = anchorRect.left - (rootWidth - loV.getWidth());
		    xPos = (xPos < 0) ? 0 : xPos;

		} else {
		    // if (loV.getWidth() > rootWidth) {
		     xPos = anchorRect.centerX() - (rootWidth / 2);
		    // } else {
//		    xPos = anchorRect.left;
		    // }

		}

		Log.i(TAG, "xpos:" + xPos + " y:" + location[1]);
	    t.setGravity(Gravity.LEFT|Gravity.TOP, anchorRect.centerX() - v.getMeasuredWidth() / 2,
		    location[1] - v.getMeasuredHeight() / 2);
	    t.setDuration(Toast.LENGTH_SHORT);
	    t.show();
	}

}
