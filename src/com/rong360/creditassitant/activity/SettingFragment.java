package com.rong360.creditassitant.activity;

import android.os.Bundle;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.widget.TitleBarCenter;

public class SettingFragment extends BaseFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBarCenter center = getSupportActionBarCenter(Boolean.FALSE);
		center.hideLeft();
		center.setTitle("设置");
	}
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_setting;
	}
}
