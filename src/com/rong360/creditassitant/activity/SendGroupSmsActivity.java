package com.rong360.creditassitant.activity;

import android.os.Bundle;

import com.rong360.creditassitant.R;

public class SendGroupSmsActivity extends BaseActionBar {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getSupportActionBar().setTitle("群发短信");
    }
    
    @Override
    protected int getLayout() {
	return R.layout.activity_send_group_sms;
    }

}
