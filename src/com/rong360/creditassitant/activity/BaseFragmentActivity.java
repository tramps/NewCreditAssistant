package com.rong360.creditassitant.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.widget.FitTextView.ITitle;
import com.rong360.creditassitant.widget.TitleBarCenter;
import com.rong360.creditassitant.widget.TitleBarLeft;

public abstract class BaseFragmentActivity extends FragmentActivity{
	private ITitle mTitleBar;
	private RelativeLayout mContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContainer = new RelativeLayout(this);
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(getLayout(), mContainer);
		setContentView(mContainer);
		initElement();
	}

	/** get the layout resource id. */
	protected int getLayout() {
		return 0;
	}

	/** find all elements and set listeners */
	protected void initElement() {
	}

	@Override
	protected void onPause() {
		super.onPause();
//		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		MobclickAgent.onResume(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.back) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	public ITitle getSupportActionBar(boolean isCenter) {
		if (mTitleBar == null) {
			if (isCenter) {
				TitleBarCenter view = new TitleBarCenter(this);
				mContainer.addView(view);
				mTitleBar = view;
			} else {
				TitleBarLeft view = new TitleBarLeft(this);
				mContainer.addView(view);
				mTitleBar = view;
			}
		}
		return mTitleBar;
	}
	
	public ViewGroup getMContainer(){
		return mContainer;
	}
}
