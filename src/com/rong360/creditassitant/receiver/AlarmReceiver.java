package com.rong360.creditassitant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rong360.creditassitant.activity.AlarmActivity;
import com.rong360.creditassitant.util.IntentUtil;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String EXTRA_IDS = "extra_ids";
    @Override
    public void onReceive(Context context, Intent intent) {
//	String ids = intent.getAction();
//	Log.i("AlarmHelper", "action" + ids);
	Intent displya = new Intent(context, AlarmActivity.class);
//	displya.putExtra(AlarmActivity.EXTRA_IDS, ids);
	displya.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	IntentUtil.startActivity(context.getApplicationContext(), displya);
    }

}
