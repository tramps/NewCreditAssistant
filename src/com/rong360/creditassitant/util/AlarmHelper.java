package com.rong360.creditassitant.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rong360.creditassitant.activity.AlarmActivity;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.receiver.AlarmReceiver;

public class AlarmHelper {
    private static final String TAG = "AlarmHelper";
    
    private static Timer mTimer = new Timer();
    private static AlarmTask mTask;

    public static ArrayList<Customer> getCurrentAlarmCustomer(boolean shallNext) {
	ArrayList<Customer> customers = GlobalValue.getIns().getAllCustomers();
	ArrayList<Customer> alarmCustomers = new ArrayList<Customer>();
	long now = System.currentTimeMillis();
	for (Customer c : customers) {
	    if (c.getAlarmTime() > now && !c.isHasChecked()
		    && !c.IsDisplayed()) {
		alarmCustomers.add(c);
	    }
	}
	ArrayList<Customer> currentAlarms = new ArrayList<Customer>();
	if (alarmCustomers.size() == 0) {
	    return currentAlarms;
	}

	// Log.i(TAG, "alarm size:" + alarmCustomers.size());
	Collections.sort(alarmCustomers, new Comparator<Customer>() {

	    @Override
	    public int compare(Customer lhs, Customer rhs) {
		return lhs.getAlarmTime() - rhs.getAlarmTime() > 0 ? 1 : -1;
	    }
	});
	// Log.i(TAG, "alarm size:" + alarmCustomers.size());

	Customer c = alarmCustomers.get(0);
	currentAlarms.add(c);
//	long now = System.currentTimeMillis();
	if (c.getAlarmTime() < now) {
	    for (int i = 1; i < alarmCustomers.size(); i++) {
		Customer next = alarmCustomers.get(i);
		if (next.getAlarmTime() <= now) {
		    currentAlarms.add(next);
		} else {
		    if (shallNext) {
			currentAlarms.add(next);
		    }
		    break;
		}
	    }
	} else {
	    long max = c.getAlarmTime() + 60;
	    long min = c.getAlarmTime() - 60;
	    for (int i = 1; i < alarmCustomers.size(); i++) {
		Customer nextCustomer = alarmCustomers.get(i);
		if (max > nextCustomer.getAlarmTime() && min < nextCustomer.getAlarmTime()) {
		    currentAlarms.add(nextCustomer);
		} else {
		    break;
		}
	    }
	}
	// Log.i(TAG, "alarm size:" + currentAlarms.size());
	return currentAlarms;
    }
    
    public static int[] startAlarm(Context context) {
	return startAlarm(context, false);
    }

    public static int[] startAlarm(Context context, boolean shallNext) {
	ArrayList<Customer> alarms = GlobalValue.getIns().getAlarms();
	if (alarms.size() > 0) {
	    alarms.clear();
	}

	alarms.addAll(getCurrentAlarmCustomer(shallNext));
	Log.i(TAG, "start alarm: " + alarms.size());
	if (alarms.size() == 0) {
	    return null;
	}

	// StringBuilder sb = new StringBuilder();
	// for (int i = 0; i < customers.size(); i++) {
	// sb.append(customers.get(i).getId());
	// sb.append(",");
	// }
//	AlarmManager manager =
//		(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	Customer c = alarms.get(alarms.size() -1);
	long leftTime = c.getAlarmTime() - System.currentTimeMillis();
	Log.i(TAG, c.getId() + "-" + c.getName() + "next time: " + leftTime);
	// receiver.setAction(sb.toString());
	// Log.i(TAG, sb.toString());
//	Intent receiver = new Intent(context, AlarmReceiver.class);
//	PendingIntent intent =
//		PendingIntent.getBroadcast(context, 10001, receiver, 0);
//	manager.set(AlarmManager.RTC_WAKEUP, c.getAlarmTime(), intent);
	if (mTimer != null) {
	    mTimer.cancel();
	    mTimer.purge();
	    mTimer = new Timer();
	} else {
	    mTimer = new Timer();
	}
	
	mTask = new AlarmTask(context);
	
	if (leftTime < 0) {
	    leftTime = 10000;
	}
	
	mTimer.schedule(mTask, leftTime);
	return null;
    }
    
    private static class AlarmTask extends TimerTask {
	private Context mContext;
	public AlarmTask(Context context) {
	    mContext = context;
	}
	@Override
	public void run() {
	    mContext.sendBroadcast(new Intent(AlarmReceiver.TIME_TO_ALARM));
	    Log.i(TAG, "sended");
//	    Intent displya = new Intent(mContext, AlarmActivity.class);
//	    displya.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	    IntentUtil.startActivity(mContext, displya);
	}
    }
}
