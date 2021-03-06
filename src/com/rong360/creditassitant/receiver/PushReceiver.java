package com.rong360.creditassitant.receiver;

import com.rong360.creditassitant.activity.MainTabHost;
import com.rong360.creditassitant.service.NotificationHelper;
import com.rong360.creditassitant.util.CloudHelper;

import cn.jpush.android.api.JPushInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.util.Log;

public class PushReceiver extends BroadcastReceiver {
    private static final String TAG = "PushReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
	Bundle bundle = intent.getExtras();

	String action = intent.getAction();
	Log.d(TAG, "onreceiver : " + action);

	if (JPushInterface.ACTION_MESSAGE_RECEIVED.equalsIgnoreCase(action)) {
//	    Log.d(TAG, "msg: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
	    CloudHelper.syncOrder(context, true);
	} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equalsIgnoreCase(action)) {
	    String msg = bundle.getString("n_title");
	    if (msg != null) {
		int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
		Log.d(TAG, "id: " + notificationId);
		NotificationHelper.cancelNotification(context, notificationId);
	    } else {
		Log.d(TAG, "msg: " + msg);
	    }
	    CloudHelper.syncOrder(context, true);
	} else if (JPushInterface.ACTION_NOTIFICATION_OPENED
		.equalsIgnoreCase(action)) {
	    Log.d(TAG, "opened: ");
	    Intent notificationIntent = new Intent(context, MainTabHost.class);
	    notificationIntent.putExtra(MainTabHost.EXTRA_INDEX_TAG,
		    MainTabHost.TAG_CUSTOMER);
	    notificationIntent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
	    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	    context.startActivity(notificationIntent);
	}
    }

}
