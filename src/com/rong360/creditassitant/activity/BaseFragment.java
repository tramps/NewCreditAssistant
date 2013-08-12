package com.rong360.creditassitant.activity;

import android.app.ActionBar.LayoutParams;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.widget.FitTextView.ITitle;
import com.rong360.creditassitant.widget.TitleBarCenter;
import com.rong360.creditassitant.widget.TitleBarLeft;

public abstract class BaseFragment extends Fragment implements IFragment {
	private static final String TAG = "BaseFragment";
	public static final String EXTRA_TITLE = "extra_title";
	public static final String EXTRA_LAYOUT = "extra_layout";
	public static final String EXTRA_SHOW_AS_BACK = "extra_show_as_back";
	public static final String EXTRA_SHOW_AS_HOME = "extra_show_as_home";
	protected View mView;
	protected FragmentActivity mContext;
	private ITitle mTitleBar;
	private TitleBarCenter mCenter;
	private TitleBarLeft mLeft;
	protected ViewGroup mContainer;
	private boolean mReuseOldViewEnable = false;
	private int mLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = getActivity();
		
//		if (mShowDialog) {
//			showDialog();
//		}
//		Bundle bundle = getArguments();
//		if (bundle != null) {
//			mLayout = bundle.getInt(EXTRA_LAYOUT, 0);
//			String title = bundle.getString(EXTRA_TITLE);
//			if (title != null) {
//				getSupportActionBar().setTitle(title);
//			}
//		}
		
	}

	@Override
	public void onResume() {
		super.onResume();
//		MobclickAgent.onResume(mContext);
	}
	
	@Override
	public void onPause() {
		super.onPause();
//		MobclickAgent.onPause(mContext);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		Bundle bundle = getArguments();
//		if (bundle != null) {
//			boolean showAsBack = bundle.getBoolean(EXTRA_SHOW_AS_BACK, false);
//			if (showAsBack) {
//				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//			}
//			boolean showAsHome = bundle.getBoolean(EXTRA_SHOW_AS_HOME, false);
//			if (showAsHome) {
//				getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//			}
//		}
	}

	protected void setReuseOldViewEnable(boolean b) {
		mReuseOldViewEnable = b;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mContainer == null || !mReuseOldViewEnable) {
			mContainer = new RelativeLayout(mContext);
			mView = inflater.inflate(getLayout(), null);
			mContainer.addView(mView, new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			
			if (mTitleBar != null) {
				View titleBar = mCenter;
				if (titleBar == null) {
					titleBar = mLeft;
				}
				if (titleBar.getParent() != null) {
					ViewGroup group = (ViewGroup) titleBar.getParent();
					group.removeView(titleBar);
				}
				mContainer.addView(titleBar);
			}
			initElement();
		} else {
			// reuse the old view.
			if (mContainer.getParent() != null) {
				((ViewGroup) mContainer.getParent()).removeView(mContainer);
			}
		}
		
		return mContainer;
	}

	/** get the layout resource id. */
	protected int getLayout() {
		return 0;
	}

	public void setLayout(int layout) {
		mLayout = layout;
	}

	/** find all elements and set listeners */
	protected void initElement() {
	}

	protected View findViewById(int id) {
		return mView.findViewById(id);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.back || item.getItemId() == R.id.home) {
			mContext.onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	public TitleBarCenter getSupportActionBarCenter(boolean isShow) {
		if (mCenter == null) {
			mCenter = new TitleBarCenter(mContext);
			if (mContainer != null) {
				mContainer.addView(mCenter);
			}
			mTitleBar = mCenter;
		} 
		
		mCenter.displayIndicator(isShow);
		return mCenter;
	}
	
	
	public TitleBarLeft getSupportActionBarLeft() {
		if (mLeft == null) {
			mLeft = new TitleBarLeft(mContext);
			if (mContainer != null) {
				mContainer.addView(mLeft);
			}
			mTitleBar = mLeft;
		}
		
		return mLeft;
	}

	public View getMView() {
		return mView;
	}

	@Override
	public View getMContainer() {
		return mContainer;
	}

}
