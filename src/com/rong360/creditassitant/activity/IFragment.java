package com.rong360.creditassitant.activity;

import com.rong360.creditassitant.widget.FitTextView.ITitle;

import android.support.v4.app.FragmentActivity;
import android.view.View;

public interface IFragment {
	public View getMView();
	
	public View getMContainer();
	
//	public ITitle getSupportActionBar(boolean isCenter);
	
	public FragmentActivity getActivity();

}
