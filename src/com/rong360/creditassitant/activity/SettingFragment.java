package com.rong360.creditassitant.activity;

import android.os.Bundle;

import com.rong360.creditassitant.R;

public class SettingFragment extends BaseFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBarCenter(Boolean.FALSE).setTitle("设置");
	}
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_setting;
	}
}
