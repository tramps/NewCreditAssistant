package com.rong360.creditassitant.activity;

import android.app.Activity;
import android.content.Intent;
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
import com.rong360.creditassitant.util.MyToast;
import com.rong360.creditassitant.util.PassCheckHelper;
import com.rong360.creditassitant.util.PreferenceHelper;
import com.rong360.creditassitant.util.RongStats;
import com.umeng.analytics.MobclickAgent;

public class LockActivity extends Activity {

    private static final String TAG = "LockActivity";
    public static final String EXTRA_LOCK = "extra_lock";
    private TextView tvLeftTime;
    private Button btnUnlock;

    private final int mTotalTime = 10 * 60;
    private final int mMinute = 60;

    private long mStartTime;
    private boolean mStop;

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
		Intent intent =
			new Intent(LockActivity.this,
				ShowPassAliasActivity.class);
		startActivity(intent);
		finish();
		mStop = true;
		return false;
	    }
	    Log.i(TAG, "refresh...");
	    tvLeftTime.setText(getDisplay(leftTime));
	    if (!mStop) {
		startTiming();
	    }
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
	mStop = false;
	startTiming();
    }

    @Override
    public void onBackPressed() {
	// stopTiming();
	// super.onBackPressed();
    }

    private void stopTiming() {
	mStop = true;
	mHandler.removeMessages(0);
    }

    @Override
    protected void onResume() {
	super.onResume();
	mStop = false;
	startTiming();
	MobclickAgent.onResume(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onResume(this);
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
	if (sec < 10) {
	    sb.append("0");
	}
	sb.append(sec);

	return sb.toString();
    }

    private void startTiming() {
	if (mStop) {
	    return;
	}
	Log.i(TAG, "start timing");
	mHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private void initContent() {
	tvLeftTime = (TextView) findViewById(R.id.tvLeftTime);
	btnUnlock = (Button) findViewById(R.id.btnUnlock);
	String tel =
		PreferenceHelper.getHelper(this).readPreference(
			AuthCodeActivity.EXTRA_TEL);
	if (tel != null && tel.length() > 0) {
	    btnUnlock.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		    MobclickAgent.onEvent(LockActivity.this, RongStats.LOCK_LOGIN);
		    Intent intent =
			    new Intent(LockActivity.this, LoginActivity.class);
		    intent.putExtra(EXTRA_LOCK, false);
		    startActivityForResult(intent, 10001);
		    stopTiming();
		}
	    });
	} else {
	    btnUnlock.setVisibility(View.GONE);
	}
    }

    @Override
    protected void
	    onActivityResult(int requestCode, int resultCode, Intent data) {
	if (resultCode == RESULT_OK) {
	    PassCheckHelper.clearLock();
	    stopTiming();
	    PreferenceHelper.getHelper(this).removePreference(
		    SetPassActivity.PRE_KEY_PASS);
	    PreferenceHelper.getHelper(this).removePreference(
		    PRE_KEY_START_LOCK_TIME);
	    MyToast.displayFeedback(this, R.drawable.ic_right, "密码已关闭");
	    new Handler().postDelayed(new Runnable() {
		@Override
		public void run() {
		    LockActivity.this.finish();
		}
	    }, 200);
	}
    }

}
