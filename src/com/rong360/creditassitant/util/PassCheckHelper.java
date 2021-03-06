package com.rong360.creditassitant.util;

import com.rong360.creditassitant.activity.SetPassActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

public class PassCheckHelper {
    private static final String TAG = "PassCheckHelper";

    private static long INIT_TIME = 0;
    private static PassCheckHelper mhelper = null;
    private static SharedPreferences mSharePref = null;
    private static final long THRESH_HOLD = 60000;
    
    private static boolean isLocked = false;

    private PassCheckHelper(Context context) {
	mSharePref =
		context.getSharedPreferences("PassCheck", Context.MODE_PRIVATE);
    }
    
    public static void clearLock() {
	isLocked = false;
    }

    public static synchronized PassCheckHelper getInstance(Context context) {
	if (mhelper == null) {
	    mhelper = new PassCheckHelper(context);
	}
	return mhelper;
    }

    public void init() {
	PassCheckHelper.INIT_TIME = SystemClock.uptimeMillis();
	Log.i(TAG, "init" + PassCheckHelper.INIT_TIME);
    }

    // public void forceLock() {
    // PassCheckHelper.INIT_TIME = 0;
    // }

    public boolean shouldLock(Context context) {
	String pass = PreferenceHelper.getHelper(context).readPreference(SetPassActivity.PRE_KEY_PASS);
	if (pass == null) {
	    return false;
	}
	long period = SystemClock.uptimeMillis() - PassCheckHelper.INIT_TIME;
	Log.i(TAG, "period" + period);

	if (period > THRESH_HOLD) {
	    if (isLocked) {
		return false;
	    } else {
		return isLocked = true;
	    }
	}
	return false;
    }
}