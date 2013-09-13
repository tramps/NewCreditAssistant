package com.rong360.creditassitant.receiver;

import com.rong360.creditassitant.util.GlobalValue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CommunicationChangedReceiver extends BroadcastReceiver {
    public static final String CHANGE_ACTION = "changed_action";
    @Override
    public void onReceive(Context context, Intent intent) {
	GlobalValue.getIns().setNeedUpdateCommunication(true);
    }

}
