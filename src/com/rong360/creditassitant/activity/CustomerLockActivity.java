package com.rong360.creditassitant.activity;

import com.rong360.creditassitant.R;

import android.os.Bundle;

public class CustomerLockActivity extends BaseActionBar {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar(false).setTitle("客户密码锁");
    }
    
    @Override
    protected int getLayout() {
	return R.layout.activity_lock_customer;
    }
}
