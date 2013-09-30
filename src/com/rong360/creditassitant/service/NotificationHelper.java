package com.rong360.creditassitant.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.activity.MainTabHost;

public class NotificationHelper {
    public static final int NOTIFICATION_ID = 100023;
    
    public static void showNotification(Context context, int count) {
	Notification notification =
		new Notification(R.drawable.ic_launcher, "新增用户",
			System.currentTimeMillis());
	Intent notificationIntent = new Intent(context, MainTabHost.class);
	notificationIntent.putExtra(MainTabHost.EXTRA_INDEX_TAG,
		MainTabHost.TAG_FOLLOW);
	notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	PendingIntent pendingIntent =
		PendingIntent.getActivity(context, 0, notificationIntent, 0);

	notification.setLatestEventInfo(context, "融易记", "新增用户" + count + "个用户", pendingIntent);
	
	NotificationManager manager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    manager.notify(NOTIFICATION_ID, notification);
    }
    
    public static Notification getNotification(Context context, int count) {
	
    Notification notification =
		new Notification(R.drawable.ic_launcher, "新增用户",
			System.currentTimeMillis());
	Intent notificationIntent = new Intent(context, MainTabHost.class);
	notificationIntent.putExtra(MainTabHost.EXTRA_INDEX_TAG,
		MainTabHost.TAG_FOLLOW);
	notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
	PendingIntent pendingIntent =
		PendingIntent.getActivity(context, 0, notificationIntent, 0);

	notification.setLatestEventInfo(context, "融易记", "新增用户" + count + "个用户", pendingIntent);
	
	return notification;
    }
    
    public static void cancelNotification(Context context, int notifiId) {
	NotificationManager manager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    manager.cancel(notifiId);
    }
}
