package com.rong360.creditassitant.activity;

import android.os.Bundle;

import com.rong360.creditassitant.R;

public class AboutUsActivity extends BaseActionBar {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("关于");
	}
	
	@Override
	protected int getLayout() {
		return R.layout.activity_about_us;
	}

}
