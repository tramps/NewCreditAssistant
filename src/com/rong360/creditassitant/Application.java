package com.rong360.creditassitant;

import android.content.Intent;
import android.util.Log;

import com.rong360.creditassitant.activity.ComunicationHistoryFragment;
import com.rong360.creditassitant.activity.CustomerManagementFragment;
import com.rong360.creditassitant.util.AlarmHelper;
import com.rong360.creditassitant.util.DisplayUtils;
import com.rong360.creditassitant.util.GlobalValue;

public class Application extends android.app.Application {
    private static final String TAG = "Application";

    @Override
    public void onCreate() {
	super.onCreate();
	onAppStart();
    }

    protected void onAppStart() {
	Log.i(TAG, "Application onAppStart");
	GlobalValue.getIns().init(getApplicationContext());
	AlarmHelper.startAlarm(getApplicationContext());
	removePreference();
//	Intent service =
//		new Intent(getApplicationContext(), PhoneNoticeService.class);
//	startService(service);
    }

    private void removePreference() {
//	PreferenceHelper.getHelper(this).removePreference(
//		PhoneNoticeService.PRE_KEY_STARTED);
//	PreferenceHelper.getHelper(this).removePreference(
//		ComunicationHistoryFragment.PRE_KEK_LAST_POSITION);
//	PreferenceHelper.getHelper(this).removePreference(
//		CustomerManagementFragment.PRE_KEY_PAGE_NUM);
    }
}
