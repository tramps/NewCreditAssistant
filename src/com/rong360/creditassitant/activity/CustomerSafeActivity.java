package com.rong360.creditassitant.activity;

import com.rong360.creditassitant.R;

import android.os.Bundle;

public class CustomerSafeActivity extends BaseActionBar {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar(false).setTitle("客户保险箱");
    }
    
    @Override
    protected void initElements() {
	
    }
    
    @Override
    protected int getLayout() {
	return R.layout.activity_safe_customer;
    }

}
