package com.rong360.creditassitant.service;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.CommuHandler;
import com.rong360.creditassitant.model.Communication;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.model.LocationHelper;
import com.rong360.creditassitant.util.DateUtil;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.RongStats;
import com.umeng.analytics.MobclickAgent;

public class WindowManagerHelper {
    private static final String TAG = "PhoneNoticeService";
    
    private static WindowManager mWindowManager;
    private static DesktopLayout mDesktopLayout;
    private static WindowManager.LayoutParams mParams;
    
    private static final String TIME_SUFFIX = "联系过";
    private static final String NO_CONTENT = "暂无备注";
    
    public static boolean mShallShow = true;
    
    public static DesktopLayout createDesktopLayout(Context context) {
	if (mDesktopLayout == null) {
	    mDesktopLayout = new DesktopLayout(context);
	}
	return mDesktopLayout;
    }

    public static WindowManager createWindowManager(Context context) {
	if (mWindowManager == null) {
	    mWindowManager =
		    (WindowManager) context.getSystemService("window");
	}
	return mWindowManager;
    }

    public static WindowManager.LayoutParams createLayoutParams() {
	if (mParams == null) {
	    mParams = new WindowManager.LayoutParams();
	    mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
	    mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
	    mParams.format = PixelFormat.RGBA_8888;
	    mParams.gravity = Gravity.TOP | Gravity.LEFT;
	    mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
	    mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

	    mParams.x = 0;
	    mParams.y = 80;
	}
	return mParams;
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
    
    public static boolean showContent(Context context, String tel) {
	initContent(context, tel);
	try {
	    createWindowManager(context).addView(createDesktopLayout(context), createLayoutParams());
	    MobclickAgent.onEvent(context, RongStats.PHONE_POPUP);
	    Log.i(TAG, "showed:" + tel);
	    return true;
	} catch (Exception e) {
	    Log.e(TAG, e.toString());
	    return false;
	}
    }
    
    public static boolean hideContent(Context context) {
	try {
	    createWindowManager(context).removeView(createDesktopLayout(context));
	    Log.i(TAG, "hided:");
	} catch (Exception e) {
	    Log.e(TAG, e.toString());
	}
	return false;
    }
    
    
    public static String initContent(Context context, String tel) {
	View parent = createDesktopLayout(context);
	TextView tvName = (TextView) parent.findViewById(R.id.tv_name);
	TextView tvProgress = (TextView) parent.findViewById(R.id.tv_progress);
	TextView tvComment = (TextView) parent.findViewById(R.id.tv_comment);
	TextView tvTime = (TextView) parent.findViewById(R.id.tv_time);

	Customer c =
		GlobalValue.getIns()
			.getCustomerHandler(context)
			.getCustomerByTel(tel);

	if (null == c) {
	    tvName.setText(tel);
	    tvComment.setText("");
	    tvProgress.setText(LocationHelper.getAreaByNumber(
		    context, tel));
	} else {
	    GlobalValue.getIns().putCustomer(c);
	    tvName.setText(c.getName());
	    if (c.getProgress() != null) {
		tvProgress.setText(c.getProgress());
	    } else {
		tvProgress.setText(LocationHelper.getAreaByNumber(
			context, tel));
	    }
	    tvProgress.setVisibility(View.VISIBLE);
	    String comment = c.getLastFollowComment();
	    if (comment != null && comment.length() > 0) {
		tvComment.setText(comment);
	    } else {
		tvComment.setText(NO_CONTENT);
	    }
	}

	Communication com = CommuHandler.getLastCallOfTel(context, tel);
	if (null == com) {
	    tvTime.setText("第一次联系");
	} else {
	    tvTime.setText(DateUtil.getDisplayTime(com.getTime()) + TIME_SUFFIX);
	}

	return PhoneNoticeService.mLast = tvTime.getText().toString();
    }

}
