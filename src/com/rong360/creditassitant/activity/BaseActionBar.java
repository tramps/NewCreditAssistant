package com.rong360.creditassitant.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.widget.FitTextView.ITitle;
import com.rong360.creditassitant.widget.TitleBarCenter;
import com.rong360.creditassitant.widget.TitleBarLeft;

public abstract class BaseActionBar extends Activity {
    private static final String TAG = BaseActionBar.class.getSimpleName();
    public static final String CANCEL_TITLE = "取消";

    private ITitle mTitleBar;
    private RelativeLayout mContainer;
    private TitleBarCenter mCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mContainer = new RelativeLayout(this);
	View view = LayoutInflater.from(this).inflate(getLayout(), null);
	mContainer.addView(view, new RelativeLayout.LayoutParams(
		LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	setContentView(mContainer);

	initElements();
    }

    protected void initElements() {

    }

    @Override
    protected void onPause() {
	super.onPause();
	// MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
	super.onResume();
	// MobclickAgent.onResume(this);
    }

    protected int getLayout() {
	return 0;
    }

    protected RelativeLayout getMContainer() {
	return mContainer;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	if (item.getItemId() == R.id.back) {
	    onBackPressed();
	}
	return super.onOptionsItemSelected(item);
    }

    public TitleBarCenter getSupportActionBar() {
	if (mCenter == null) {
	    mCenter = new TitleBarCenter(this);
	    mContainer.addView(mCenter);
	    mTitleBar = mCenter;
	    mCenter.setLeftButton(CANCEL_TITLE, R.drawable.bkg_blue);
	    mTitleBar = mCenter;
	}
	
	return mCenter;
    }
    
    public TitleBarCenter getSupportActionBarCenter(boolean isShow) {
	if (mCenter == null) {
	    mCenter = new TitleBarCenter(this);
	    if (mContainer != null) {
		mContainer.addView(mCenter);
	    }
	    mTitleBar = mCenter;
	}

	mCenter.setLeftButton(CANCEL_TITLE, R.drawable.bkg_blue);
	mCenter.displayIndicator(isShow);
	return mCenter;
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
}
