package com.rong360.creditassitant.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

public class MySrollview extends ScrollView {
    private static final String TAG = MySrollview.class.getSimpleName();

    public MySrollview(Context context) {
	this(context, null);
    }

    public MySrollview(Context context, AttributeSet attrs) {
	super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
	switch (ev.getAction()) {
	case MotionEvent.ACTION_MOVE:
	    InputMethodManager imm =
		    (InputMethodManager) getContext().getSystemService(
			    Context.INPUT_METHOD_SERVICE);

	    if (!imm.isActive()) {
		break;
	    }

	    closeInput(this, imm);
	    break;
	}
	boolean isConsumerd = super.onTouchEvent(ev);
	return isConsumerd;
    }

    public void closeInput(ViewGroup parent, InputMethodManager imm) {
	View child;
	for (int i = 0; i < parent.getChildCount(); i++) {
	    child = parent.getChildAt(i);
	    if (child instanceof EditText) {
		imm.hideSoftInputFromWindow(child.getWindowToken(), 0);
	    } else if (child instanceof ViewGroup) {
		closeInput((ViewGroup) child, imm);
	    }
	}
    }

}
