package com.rong360.creditassitant.activity;

import android.os.Bundle;

import com.rong360.creditassitant.R;

public class ComunicationHistoryFragment extends BaseFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBarCenter(Boolean.FALSE).setTitle("通讯历史");
	}
	
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_history;
	}
}
