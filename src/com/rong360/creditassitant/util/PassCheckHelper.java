package com.rong360.creditassitant.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

public class PassCheckHelper {
    private static final String TAG = "PassCheckHelper";

    private static long INIT_TIME = 0;
    private static PassCheckHelper mhelper = null;
    private static SharedPreferences mSharePref = null;
    private static final long THRESH_HOLD = 6000;

    private PassCheckHelper(Context context) {
	mSharePref =
		context.getSharedPreferences("PassCheck", Context.MODE_PRIVATE);
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

    public boolean shouldLock() {
	long period = SystemClock.uptimeMillis() - PassCheckHelper.INIT_TIME;
	Log.i(TAG, "period" + period);

	if (period > THRESH_HOLD) {
	    return true;
	}
	return false;
    }
}