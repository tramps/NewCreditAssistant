package com.rong360.creditassitant.service;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.rong360.creditassitant.R;

public class WindowManagerHelper {
    private static final String TAG = WindowManager.class.getSimpleName();

    public static DesktopLayout createDesktopLayout(Context context) {
	return new DesktopLayout(context);
    }

    public static WindowManager createWindowManager(Context context) {
	WindowManager windowManager =
		(WindowManager) context.getSystemService("window");
	return windowManager;
    }

    public static WindowManager.LayoutParams createLayoutParams() {
	WindowManager.LayoutParams LayoutParams =
		new WindowManager.LayoutParams();
	LayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
	LayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
	LayoutParams.format = PixelFormat.RGBA_8888;
	LayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
	LayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
	LayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

	LayoutParams.x = 0;
	LayoutParams.y = 0;

	return LayoutParams;
    }

    public static class DesktopLayout extends LinearLayout {
	public DesktopLayout(Context context) {
	    super(context);
	    setOrientation(LinearLayout.VERTICAL);
	    LayoutParams mLayoutParams =
		    new LayoutParams(LayoutParams.MATCH_PARENT,
			    LayoutParams.WRAP_CONTENT);
	    setLayoutParams(mLayoutParams);

	    View view =
		    LayoutInflater.from(context).inflate(
			    R.layout.activity_customed_phone_notice, null);
	    addView(view, mLayoutParams);
	}
    }

}
