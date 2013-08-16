package com.rong360.creditassitant.activity;

import android.os.Bundle;

import com.rong360.creditassitant.R;

public class AddCustomerActivity extends BaseActionBar {
	
	public static final String CUSTOMER_ID = "customer_id";
	
	private int mId = -1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mId = getIntent().getIntExtra(CUSTOMER_ID, -1);
		if (mId == -1) {
			getSupportActionBar().setTitle("添加客户");
		} else {
			getSupportActionBar().setTitle("编辑客户");
		}
		
	}
	
	@Override
	protected int getLayout() {
		return R.layout.activity_add_customer;
	}

}
