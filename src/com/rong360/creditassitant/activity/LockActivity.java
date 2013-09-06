package com.rong360.creditassitant.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.PreferenceHelper;

public class LockActivity extends Activity {

    private static final String TAG = "LockActivity";
    private TextView tvLeftTime;
    private Button btnUnlock;

    private final int mTotalTime = 1 * 60;
    private final int mMinute = 60;

    private long mStartTime;

    private static final String PRE_KEY_START_LOCK_TIME = "pre_key_start_lock";

    private Handler mHandler = new Handler(new Callback() {
	@Override
	public boolean handleMessage(Message msg) {
	    long leftTime =
		    mTotalTime - (System.currentTimeMillis() - mStartTime)
			    / 1000;
	    if (leftTime <= 0) {
		PreferenceHelper.getHelper(LockActivity.this).removePreference(
			PRE_KEY_START_LOCK_TIME);
		finish();
	    }
	    Log.i(TAG, "refresh...");
	    tvLeftTime.setText(getDisplay(leftTime));
	    startTiming();
	    return true;
	}
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_lock);

	initContent();
	String start =
		PreferenceHelper.getHelper(this).readPreference(
			PRE_KEY_START_LOCK_TIME);
	if (start != null && start.length() > 0) {
	    mStartTime = Long.valueOf(start);
	} else {
	    mStartTime = System.currentTimeMillis();
	    PreferenceHelper.getHelper(this).writePreference(
		    PRE_KEY_START_LOCK_TIME, String.valueOf(mStartTime));
	}

	startTiming();
    }

    protected CharSequence getDisplay(long leftTime) {
	long min = leftTime / mMinute;
	long sec = leftTime - min * mMinute;
	StringBuilder sb = new StringBuilder();
	if (min < 10) {
	    sb.append("0");
	}
	sb.append(min);
	sb.append(":");
	sb.append(sec);

	return sb.toString();
    }

    private void startTiming() {
	Log.i(TAG, "start timing");
	mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private void initContent() {
	tvLeftTime = (TextView) findViewById(R.id.tvLeftTime);
	btnUnlock = (Button) findViewById(R.id.btnUnlock);
	btnUnlock.setOnClickListener(new OnClickListener() {
	    @Override
	    public void onClick(View v) {
		finish();
	    }
	});
    }

}
