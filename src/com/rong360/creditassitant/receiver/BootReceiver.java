package com.rong360.creditassitant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rong360.creditassitant.service.PhoneNoticeService;
import com.rong360.creditassitant.service.TimingService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
	Log.i("receiver", Intent.ACTION_BOOT_COMPLETED);
	if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
	    Intent service = new Intent(context, PhoneNoticeService.class);
	    context.startService(service);

	    TimingService.startAlarm(context);
	    Intent timService = new Intent(context, TimingService.class);
	    context.startService(timService);
	}
    }
}
