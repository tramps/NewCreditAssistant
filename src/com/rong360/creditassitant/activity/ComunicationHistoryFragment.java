package com.rong360.creditassitant.activity;

import android.os.Bundle;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.widget.TitleBarCenter;

public class ComunicationHistoryFragment extends BaseFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBarCenter center = getSupportActionBarCenter(Boolean.FALSE);
		center.hideLeft();
		center.setTitle("通讯历史");
	}
	
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_history;
	}
}
