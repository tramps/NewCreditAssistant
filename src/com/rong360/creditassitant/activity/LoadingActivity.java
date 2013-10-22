package com.rong360.creditassitant.activity;

import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.PreferenceHelper;

public class LoadingActivity extends BaseActionBar implements Runnable {
    private ImageView ivLoading;

    // private boolean mHasGuided = false;

    private Handler mHandler = new Handler();

    @Override
    protected int getLayout() {
	return R.layout.activity_loading;
    }

    @Override
    protected void onResume() {
	super.onResume();
	// String first =
	// PreferenceHelper.getHelper(this).readPreference(PRE_KEY_FIRST);
	// if (first == null && !mHasGuided) {
	// Intent intent = new Intent(this, GuideActivity.class);
	// startActivityForResult(intent, 10001);
	// } else {
	setLoadingView();
	mHandler.postDelayed(this, 2000);
	// }

    }

    // @Override
    // protected void
    // onActivityResult(int requestCode, int resultCode, Intent data) {
    // if (resultCode == RESULT_OK) {
    // PreferenceHelper.getHelper(this).writePreference(PRE_KEY_FIRST, "first");
    // mHasGuided = true;
    // }
    // }

    @Override
    protected void initElements() {
	ivLoading = (ImageView) findViewById(R.id.loading);
    }

    private void setLoadingView() {
	ivLoading.setImageResource(R.drawable.ic_main);
    }

    @Override
    public void run() {
	Intent intent = new Intent(this, GuideActivity.class);
	startActivity(intent);
	finish();
    }
}
