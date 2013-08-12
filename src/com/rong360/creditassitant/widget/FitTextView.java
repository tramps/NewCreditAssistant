package com.rong360.creditassitant.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class FitTextView extends TextView {
	private static final String TAG = "FitTextView";
	private boolean mTextChanged = false;
	private CharSequence mText;

	public FitTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FitTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed || mTextChanged) {
			setFitText();
			mTextChanged = false;
		}
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
		if (mText == null || !mText.equals(text)) {
			mTextChanged = true;
			mText = text;
		}
	}

	private int mLines;
	private int mMaxLines;

	@Override
	public void setLines(int lines) {
		super.setLines(lines);
		mLines = lines;
	}

	@Override
	public void setMaxLines(int maxlines) {
		super.setMaxLines(maxlines);
		mMaxLines = maxlines;
	}

	private static final String SUBFIX = "...";

	private void setFitText() {
		if (mLines == 0 && mMaxLines == 0)
			return;
		float lineScale = 0;
		if (mLines == 0) {
			lineScale = mMaxLines;
		} else if (mMaxLines == 0) {
			lineScale = mLines;
		} else {
			lineScale = Math.min(mLines, mMaxLines);
		}

		if (lineScale > 1) {
			lineScale -= 0.3f;
		} else {
			lineScale -= 0.1f;
		}
		// float subFixWidth = getPaint().measureText(SUBFIX);
		int width = getWidth();
		String content = mText != null ? mText.toString() : "";
		int breakLength = getPaint().breakText(content, true,
				width * lineScale, null);
		if (breakLength < content.length()) {
			content = content.substring(0, breakLength);
			content += SUBFIX;
		}

		super.setText(content, BufferType.NORMAL);
	}
	
	public static interface ITitle {
		public void setTitle(String title);
	}

}
