package com.rong360.creditassitant.service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.activity.AfterPhoneActivity;
import com.rong360.creditassitant.activity.MainTabHost;
import com.rong360.creditassitant.model.CommuHandler;
import com.rong360.creditassitant.model.Communication;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.model.LocationHelper;
import com.rong360.creditassitant.model.TelHelper;
import com.rong360.creditassitant.service.WindowManagerHelper.DesktopLayout;
import com.rong360.creditassitant.util.DateUtil;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.PreferenceHelper;

public class PhoneNoticeService extends Service {
    public static final String EXTRA_CALL_NUMBER = "extra_call_number";
    public static final String EXTRA_LAST = "extra_last";
    private static final String TAG = PhoneNoticeService.class.getSimpleName();
    private static final int MIN_DURATION = 60000;

    public static final String PRE_KEY_STARTED = "pre_key_started";
    public static final String PRE_KEY_DATE = "pre_key_date";
    private static final long MAX_AVAILABLE_SERVICE_TIME = 3600 * 1000;
    private static final int notification_id = 132432;

    private String mIncomingNumber = "";
    private String mLastEffectiveNumber = "";
    private TelephonyManager mTelManager = null;
    private TeleListener mListener;
    private boolean mIsDestroyed = false;
    private long mLastChangeStateTime;
    private int mLastCallState = -1;

    private WindowManager mWindowManager;
    private DesktopLayout mContent;
    private WindowManager.LayoutParams mLayoutParams;
    private boolean mIsAttached;

    private static final String TIME_SUFFIX = "联系过";
    private static final String NO_CONTENT = "暂无备注";

    private ShowCustomerScreenThread mThread;
    private Handler mHandler;
    private boolean mShallShown = false;
    private boolean mIsOutGoing = false;

    private String mLast = "";

    @Override
    public void onCreate() {
	super.onCreate();
	Log.i(TAG, "**start service**");
	mWindowManager =
		WindowManagerHelper
			.createWindowManager(getApplicationContext());
	mContent =
		WindowManagerHelper
			.createDesktopLayout(getApplicationContext());
	mLayoutParams = WindowManagerHelper.createLayoutParams();
	mIsAttached = false;

    }

    // private BroadcastReceiver mTaskChangeReceiver = new BroadcastReceiver() {
    //
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // if (intent.getAction().equalsIgnoreCase(
    // NotificationHelper.TASK_CHANGED)) {
    // new UpdateTaskThread().start();
    // }
    // }
    //
    // };

    private BroadcastReceiver mOutgoingCallReceiver = new BroadcastReceiver() {

	@Override
	public void onReceive(Context context, Intent intent) {
	    if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
		mLastEffectiveNumber =
			intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
		mLastEffectiveNumber =
			TelHelper.getPureTel(mLastEffectiveNumber);
		Log.i(TAG, "out going" + mLastEffectiveNumber);
		mIsOutGoing = true;
		showCustomedScreen();
	    }
	}

    };

    private void showCustomedScreen() {
	mHandler.removeCallbacks(mThread);
	mHandler.postDelayed(mThread, 1000);
	closePopUp(15000);
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	mIsDestroyed = true;
	unregisterReceiver(mOutgoingCallReceiver);
	// unregisterReceiver(mTaskChangeReceiver);
	if (mTelManager != null && mListener != null) {
	    mTelManager.listen(mListener, PhoneStateListener.LISTEN_NONE);
	}
	PreferenceHelper.getHelper(this).removePreference(PRE_KEY_STARTED);
	Log.i(TAG, "destroy service******");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	Log.i(TAG, "onStartCommand******");

	if (mListener != null
		&& PreferenceHelper.getHelper(this).readPreferenceWithDate(
			PRE_KEY_STARTED, MAX_AVAILABLE_SERVICE_TIME) != null) {
	    Log.i(TAG, "sticky");

	    Calendar today = Calendar.getInstance();
	    String lastNoticeDate =
		    PreferenceHelper.getHelper(getApplicationContext())
			    .readPreference(PRE_KEY_DATE);
	    try {
		if (lastNoticeDate == null
			|| (lastNoticeDate != null && !DateUtil.isSameDay(
				today.getTime(),
				DateUtil.yyyy_MM_dd.parse(lastNoticeDate)))) {
		    // NotificationHelper
		    // .broadcastHasChanged(getApplicationContext());
		}

	    } catch (ParseException e) {
		Log.e(TAG, e.toString());
	    }

	    return START_STICKY;
	}

	Log.i(TAG, "notify...");
	Notification notification = getNotification();
	startForeground(notification_id, notification);

	mHandler = new Handler(getMainLooper());
	mThread = new ShowCustomerScreenThread();

	mIsDestroyed = false;
	if (mTelManager == null) {
	    mTelManager =
		    (TelephonyManager) this
			    .getSystemService(Context.TELEPHONY_SERVICE);
	    mListener = new TeleListener();

	    mTelManager.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	registerReceiver();

	PreferenceHelper.getHelper(this).writePreferenceWithDate(
		PRE_KEY_STARTED);

	return super.onStartCommand(intent, flags, startId);
    }

    private void registerReceiver() {
	IntentFilter filter = new IntentFilter();
	filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
	registerReceiver(mOutgoingCallReceiver, filter);

	// filter = new IntentFilter();
	// filter.addAction(NotificationHelper.TASK_CHANGED);
	// registerReceiver(mTaskChangeReceiver, filter);
    }

    private Notification getNotification() {
	Notification notification =
		new Notification(R.drawable.ic_launcher, "新增用户",
			System.currentTimeMillis());
	Intent notificationIntent = new Intent(this, MainTabHost.class);
	notificationIntent.putExtra(MainTabHost.EXTRA_INDEX_TAG,
		MainTabHost.TAG_FOLLOW);
	notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	PendingIntent pendingIntent =
		PendingIntent.getActivity(this, 0, notificationIntent, 0);
	List<Customer> cuses = GlobalValue.getIns().getAllCustomers();
	if (cuses.size() == 0) {
	    GlobalValue.getIns().loadAllCustomerFromDb(cuses,
		    getApplicationContext());
	}

	String[] spilt;
	Calendar today = Calendar.getInstance();
	PreferenceHelper.getHelper(getApplicationContext()).writePreference(
		PRE_KEY_DATE, DateUtil.yyyy_MM_dd.format(today.getTime()));
	Calendar date;
	int count = 0;
	int totalCount = 0;
	// for (Customer c : cuses) {
	// spilt =
	// c.getLastFollowComment().split(
	// AddCommentActivity.DEFAULT_SEPARATOR);
	// Log.i(TAG, Arrays.toString(spilt));
	// date = Calendar.getInstance();
	// long notifyTime = 0;
	// try {
	// notifyTime = Long.parseLong(spilt[spilt.length - 1]);
	// } catch (Exception e) {
	// Log.e(TAG, e.toString());
	// }
	// date.setTimeInMillis(notifyTime);
	//
	// if (DateUtil.isSameDay(today, date)) {
	// totalCount++;
	// if (!c.isHasChecked()) {
	// count++;
	// }
	// }
	// }

	if (totalCount == 0) {
	    notification.setLatestEventInfo(this, "新增用户", "0", pendingIntent);
	} else {
	    if (count == 0) {
		notification.setLatestEventInfo(this, "新增用户", "0",
			pendingIntent);
	    } else {
		notification.setLatestEventInfo(this, "新增用户", "新增用户" + count
			+ "个", pendingIntent);
	    }
	}
	return notification;
    }

    private class UpdateTaskThread extends Thread {

	@Override
	public void run() {
	    mHandler.post(new Runnable() {

		@Override
		public void run() {
		    Notification notification = getNotification();
		    NotificationManager manager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		    manager.notify(notification_id, notification);
		}
	    });
	}

    }

    class TeleListener extends PhoneStateListener {

	public void onCallStateChanged(int state, String incomingNumber) {
	    super.onCallStateChanged(state, incomingNumber);
	    GlobalValue.getIns().setNeedUpdateCommunication(true);
	    
	    Log.i(TAG, "call state: " + state + " number: " + incomingNumber
		    + " last: " + mLastEffectiveNumber);
	    if (mLastCallState == -1) {
		mLastCallState = state;
	    }

	    if (mLastCallState == TelephonyManager.CALL_STATE_OFFHOOK
		    && state == TelephonyManager.CALL_STATE_IDLE) {
		closePopUp(0);
		if (mLastEffectiveNumber.length() == 0) {
		    return;
		}
		Log.i(TAG, "call state changed: show dialog "
			+ mLastEffectiveNumber);
		if (!mShallShown) {
		    return;
		}
		mShallShown = false;
		// if (SystemClock.uptimeMillis() - mLastChangeStateTime >=
		// MIN_DURATION) {
		Log.i(TAG, "show option");
		Intent intent =
			new Intent(PhoneNoticeService.this,
				AfterPhoneActivity.class);
		intent.putExtra(EXTRA_CALL_NUMBER, mLastEffectiveNumber);
		intent.putExtra(EXTRA_LAST, mLast);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		// } else {
		// Log.i(TAG, "less then 1 minute");
		// }
	    }

	    mIncomingNumber = TelHelper.getPureTel(incomingNumber);
	    mLastCallState = state;
	    mLastChangeStateTime = SystemClock.uptimeMillis();
	    switch (state) {
	    case TelephonyManager.CALL_STATE_IDLE: {
		Log.i(TAG, "idle: now" + mIncomingNumber + " last:"
			+ mLastEffectiveNumber);
		if (mIncomingNumber.length() > 0) {
		    closePopUp(0);
		}
		break;
	    }
	    case TelephonyManager.CALL_STATE_OFFHOOK: {
		Log.i(TAG, "off hook:" + mIncomingNumber);
		if (mIsOutGoing) {
		    closePopUp(15000);
		} else {
		    closePopUp(0);
		}
		mIsOutGoing = false;
		break;
	    }
	    case TelephonyManager.CALL_STATE_RINGING: {
		mLastEffectiveNumber = mIncomingNumber;
		Log.i(TAG, "ring last:" + mLastEffectiveNumber);
		showCustomedScreen();
		break;
	    }
	    }
	}
    }

    private void closePopUp(long delay) {
	mHandler.removeCallbacks(mCloseWindowRunnable);
	mHandler.postDelayed(mCloseWindowRunnable, delay);
    }

    private class ShowCustomerScreenThread extends Thread {
	public void run() {
	    Log.i(TAG, "isattached: " + mIsAttached);
	    if (mIsDestroyed || mIsAttached) {
		return;
	    }

	    GlobalValue global = GlobalValue.getIns();

	    if (!global.getContactPhones(getBaseContext()).contains(
		    mLastEffectiveNumber)
		    || global.getCustomerHandler(getApplicationContext())
			    .getCustomerByTel(mLastEffectiveNumber) != null) {
		Log.i(TAG, "number: " + mLastEffectiveNumber
			+ "not containded in contacts");
		showCustomer(mLastEffectiveNumber);
	    }
	}
    }

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }

    private void showCustomer(String tel) {
	Log.i(TAG, "show customer tel~" + tel);
	if (tel.length() == 0) {
	    Log.w(TAG, "tel number abnomal");
	    return;
	}
	initContent(mContent, tel);
	try {
	    mWindowManager.addView(mContent, mLayoutParams);
	    mShallShown = true;
	} catch (Exception e) {
	    Log.e(TAG, e.toString());
	}
	mIsAttached = true;
    }

    private Runnable mCloseWindowRunnable = new Runnable() {
	@Override
	public void run() {
	    // TODO
	    Log.i(TAG, "thread close customer" + mIsAttached);
	    if (mIsAttached) {
		try {
		    mWindowManager.removeView(mContent);
		} catch (Exception e) {
		    Log.e(TAG, e.toString());
		}
		mIsAttached = false;
	    }
	}
    };

    private void initContent(View parent, String tel) {
	TextView tvName = (TextView) parent.findViewById(R.id.tv_name);
	TextView tvProgress = (TextView) parent.findViewById(R.id.tv_progress);
	TextView tvComment = (TextView) parent.findViewById(R.id.tv_comment);
	TextView tvTime = (TextView) parent.findViewById(R.id.tv_time);

	Customer c =
		GlobalValue.getIns()
			.getCustomerHandler(getApplicationContext())
			.getCustomerByTel(tel);

	if (null == c) {
	    tvName.setText(tel);
	    tvComment.setText("");
	    tvProgress.setText(LocationHelper.getAreaByNumber(
		    getApplicationContext(), tel));
	} else {
	    GlobalValue.getIns().putCustomer(c);
	    tvName.setText(c.getName());
	    if (c.getProgress() != null) {
		tvProgress.setText(c.getProgress());
	    } else {
		tvProgress.setText(LocationHelper.getAreaByNumber(
			getApplicationContext(), tel));
	    }
	    tvProgress.setVisibility(View.VISIBLE);
	    String comment = c.getLastFollowComment();
	    if (comment != null && comment.length() > 0) {
		tvComment.setText(comment);
	    } else {
		tvComment.setText(NO_CONTENT);
	    }
	}

	Communication com = CommuHandler.getLastCallOfTel(this, tel);
	if (null == com) {
	    tvTime.setText("第一次联系");
	} else {
	    tvTime.setText(DateUtil.getDisplayTime(com.getTime()) + TIME_SUFFIX);
	}

	mLast = tvTime.getText().toString();
    }

}
