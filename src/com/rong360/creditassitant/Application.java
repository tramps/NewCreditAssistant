package com.rong360.creditassitant;

import java.util.ArrayList;

import android.content.Intent;
import android.util.Log;

import com.rong360.creditassitant.model.CommuHandler;
import com.rong360.creditassitant.model.Communication;
import com.rong360.creditassitant.model.LocationHelper;
import com.rong360.creditassitant.service.PhoneNoticeService;
import com.rong360.creditassitant.task.BaseHttpsManager;
import com.rong360.creditassitant.util.AlarmHelper;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.LocCache;
import com.rong360.creditassitant.util.PreferenceHelper;

public class Application extends android.app.Application {
    private static final String TAG = "Application";

    @Override
    public void onCreate() {
	super.onCreate();
	onAppStart();
    }

    protected void onAppStart() {
	Log.i(TAG, "Application onAppStart");
	String isBacked = PreferenceHelper.getHelper(this).readPreference(LocationHelper.PRE_KEY_DB);
	if (isBacked == null || !isBacked.equalsIgnoreCase(LocationHelper.BACKED)) {
	    LocationHelper.back2SdCard(getApplicationContext());
	} else {
	    LocationHelper.initHomeMap(getApplicationContext());
	}
	
	GlobalValue.getIns().init(getApplicationContext());
	
	ArrayList<Communication> comms = GlobalValue.getIns().getAllComunication(getApplicationContext());
	ArrayList<Communication> hisComs = CommuHandler.getAllCallLog(getApplicationContext());
	comms.addAll(hisComs);
	LocationHelper.setAllMobileLoc(getApplicationContext(), comms);
	
	LocCache.getInstance().restoreCache(getApplicationContext());
	
	AlarmHelper.startAlarm(getApplicationContext());
	
	BaseHttpsManager.init(getApplicationContext());
	
	removePreference();
	
	Intent service =
		new Intent(getApplicationContext(), PhoneNoticeService.class);
	startService(service);
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
