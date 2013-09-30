package com.rong360.creditassitant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rong360.creditassitant.activity.AlarmActivity;
import com.rong360.creditassitant.activity.BaseActionBar;
import com.rong360.creditassitant.activity.LockActivity;
import com.rong360.creditassitant.util.IntentUtil;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String EXTRA_IDS = "extra_ids";
    public static final String TIME_TO_ALARM = "time_to_alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
	// String ids = intent.getAction();
	// Log.i("AlarmHelper", "action" + ids);
	Log.i("AlarmHelper", "started");
	if (TIME_TO_ALARM.equalsIgnoreCase(intent.getAction())) {
	    Intent displya = new Intent(context, AlarmActivity.class);
	    displya.putExtra(LockActivity.EXTRA_LOCK, false);
	    // displya.putExtra(AlarmActivity.EXTRA_IDS, ids);
	    displya.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    IntentUtil.startActivity(context.getApplicationContext(), displya);
	}
    }

}
