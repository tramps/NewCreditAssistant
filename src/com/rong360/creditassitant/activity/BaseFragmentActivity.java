package com.rong360.creditassitant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.PassCheckHelper;
import com.rong360.creditassitant.widget.FitTextView.ITitle;
import com.rong360.creditassitant.widget.TitleBarCenter;
import com.rong360.creditassitant.widget.TitleBarLeft;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragmentActivity extends FragmentActivity {
    private static final String TAG = "BaseFragmentActivity";
    private ITitle mTitleBar;
    private RelativeLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mContainer = new RelativeLayout(this);
	LayoutInflater inflater =
		(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	 MobclickAgent.onPause(this);
	Log.i(TAG, "base action fragemtn pause");
	PassCheckHelper.getInstance(this).init();
    }

    @Override
    protected void onResume() {
	super.onResume();
	 MobclickAgent.onResume(this);
	if (PassCheckHelper.getInstance(this).shouldLock(this)) {
	    Intent intent = new Intent(this, ShowPassAliasActivity.class);
	    startActivity(intent);
	}
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	Log.i(TAG, "base action fragemtn destroy");
	PassCheckHelper.getInstance(this).init();
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

    public ViewGroup getMContainer() {
	return mContainer;
    }
}
