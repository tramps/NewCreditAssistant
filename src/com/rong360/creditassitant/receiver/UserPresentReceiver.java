package com.rong360.creditassitant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rong360.creditassitant.service.PhoneNoticeService;

public class UserPresentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("receiver", Intent.ACTION_USER_PRESENT);
		if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())) {
			Intent service = new Intent(context, PhoneNoticeService.class);
			context.startService(service);
		}
	}

}
