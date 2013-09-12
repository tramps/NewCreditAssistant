package com.rong360.creditassitant.activity;

import com.rong360.creditassitant.R;

import android.os.Bundle;

public class ImportPartnerActivity extends BaseActionBar {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("融360账号登录授权");
    }

    
    @Override
    protected int getLayout() {
	return R.layout.activity_import_partner;
    }
}
