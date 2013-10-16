package com.rong360.creditassitant.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.umeng.analytics.MobclickAgent;

/**
 * base class for TabHost.
 */
public abstract class BaseTabHost extends FragmentActivity {
    private static final String TAG = "BaseTabHost";
    /** the start tab index */
    public static final String EXTRA_INDEX_TAG = "extra_index_tag";
    public static final String EXTRA_SUB_INDEX_TAG = "extra_sub_index_tag";
    protected TabHost mTabHost;
    protected TabManager mTabManager;

    protected Dialog mDialog;

    protected int mBackCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.setContentView(getLayout());
	initElements();
	initTabs();
    }

    protected void dismissDialog() {
	if (mDialog != null) {
	    mDialog.dismiss();
	}
    }

    @Override
    protected void onNewIntent(Intent intent) {
	super.onNewIntent(intent);
	setIntent(intent);
	// initCurrentTab();
    }

    @Override
    protected void onResume() {
	super.onResume();
	MobclickAgent.onResume(this);
	initCurrentTab();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private void initCurrentTab() {
	String tag = getIntent().getStringExtra(EXTRA_INDEX_TAG);
	String subTag = getIntent().getStringExtra(EXTRA_SUB_INDEX_TAG);
	if (tag != null && subTag != null) {
	    TabInfo info = mTabManager.getTabInfo(tag);
	    if (info != null) {
		try {
		    info.setCurrentClass((Class<? extends Fragment>) Class
			    .forName(subTag));
		} catch (ClassNotFoundException e) {
		    Log.e(TAG, "", e);
		}
	    }
	}
	if (tag != null) {
	    setCurrentTab(tag);
	} else {
	    setCurrentTab(getTabInfos()[0].getTabTag());
	}
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	mTabManager.removeAllTabs();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    public TabManager getTabManager() {
	return mTabManager;
    }

    public void refreshCurrentTab() {
	setCurrentTab(mTabHost.getCurrentTabTag());
    }

    protected void setCurrentTab(String tag) {
	if (mTabHost.getCurrentTabTag().equals(tag)) {
	    // if tag == currentTag, onTagChange() will not be invoked by
	    // TabHost.
	    mTabManager.onTabChanged(tag);
	} else
	    mTabHost.setCurrentTabByTag(tag);
    }

    protected int getLayout() {
	return R.layout.base_tab_host;
    }

    protected void initElements() {
	mTabHost = (TabHost) findViewById(android.R.id.tabhost);
	mTabHost.setup();
    }

    protected void initTabs() {
	mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);
	TabInfo[] tabInfos = getTabInfos();
	for (TabInfo info : tabInfos) {
	    mTabManager.addTab(info);
	}
	mTabHost.setOnTabChangedListener(mTabManager);
    }

    protected abstract TabInfo[] getTabInfos();

    public TabInfo getCurrentTabInfo() {
	return mTabManager.getCurentTab();
    }

    protected TabHost.TabSpec makeTabSpec(int i, String tag, String title,
	    int icon) {
	View v = getTabIndicatorView(i, title, icon);
	TabHost.TabSpec spec = mTabHost.newTabSpec(tag).setIndicator(v);
	return spec;
    }

    protected View getTabIndicatorView(int i, String title, int icon) {
	View v = getTabIndicatorView(i);
	TextView tvTitle = (TextView) v.findViewById(android.R.id.title);
	ImageView ivIcon = (ImageView) v.findViewById(android.R.id.icon);
	if (tvTitle != null && title != null)
	    tvTitle.setText(title);
	if (ivIcon != null && icon != 0) {
	    ivIcon.setImageResource(icon);
	}
	int bkgRes = getBackgroundRes();
	if (bkgRes != 0) {
	    v.setBackgroundResource(bkgRes);
	}
	return v;
    }

    protected View getTabIndicatorView(int i) {
	int res = R.layout.tab_indicator_bottom;
	View v = getLayoutInflater().inflate(res, null);
	return v;
    }

    protected abstract int getBackgroundRes();

    protected static abstract class TabInfo {
	protected TabHost.TabSpec mTabSpec;
	private Bundle mArgs;
	protected Map<Class<? extends Fragment>, Fragment> mFragMaps;
	protected Class<? extends Fragment> mCurrentClass;
	private boolean mAlwaysInMemory = true;

	public TabInfo() {
	    this(null);
	}

	public TabInfo(TabHost.TabSpec spec) {
	    this(spec, null);
	}

	public TabInfo(TabHost.TabSpec spec, Bundle arg) {
	    mTabSpec = spec;
	    mArgs = arg;
	    mFragMaps = new HashMap<Class<? extends Fragment>, Fragment>();
	    initFragments();
	}

	public void setAlwaysInMemory(boolean b) {
	    mAlwaysInMemory = b;
	}

	public boolean isAlwaysInMemory() {
	    return mAlwaysInMemory;
	}

	protected abstract void initFragments();

	public TabHost.TabSpec getTabSpec() {
	    return mTabSpec;
	}

	public String getTabTag() {
	    return mTabSpec.getTag();
	}

	public String getFragmentTag() {
	    return getCurrentClass().getSimpleName();
	}

	public List<String> getFragmentTags() {
	    List<String> res = new ArrayList<String>();
	    for (Class<? extends Fragment> c : mFragMaps.keySet()) {
		res.add(c.getSimpleName());
	    }
	    return res;
	}

	public Bundle getBundle() {
	    return mArgs;
	}

	public void addFragment(Class<? extends Fragment> clss) {
	    mFragMaps.put(clss, null);
	}

	private void removeFragment(Class<? extends Fragment> clss) {
	    mFragMaps.put(clss, null);
	}

	private void putFragment(Fragment fragment) {
	    // Put the Fragment only if the class of fragment already exists.
	    if (mFragMaps.containsKey(fragment.getClass())) {
		mFragMaps.put(fragment.getClass(), fragment);
	    }
	}

	public void setCurrentClass(Class<? extends Fragment> c) {
	    mCurrentClass = c;
	}

	public Class<? extends Fragment> getCurrentClass() {
	    if (mCurrentClass == null && mFragMaps.size() > 0)
		mCurrentClass = mFragMaps.keySet().iterator().next();
	    return mCurrentClass;
	}

	public Fragment getFragment(Class<? extends Fragment> clss) {
	    return mFragMaps.get(clss);
	}

	public Fragment getCurrentFragment() {
	    if (getCurrentClass() != null)
		return mFragMaps.get(getCurrentClass());
	    return null;
	}

    }

    protected class TabManager implements TabHost.OnTabChangeListener {
	private static final String TAG = "TabManager";
	private final FragmentActivity mActivity;
	private final TabHost mTabHost;
	private final int mContainerId;
	private final HashMap<String, TabInfo> mTabs =
		new HashMap<String, TabInfo>();
	private TabInfo mLastTab;

	class DummyTabFactory implements TabHost.TabContentFactory {
	    private final Context mContext;

	    public DummyTabFactory(Context context) {
		mContext = context;
	    }

	    @Override
	    public View createTabContent(String tag) {
		View v = new View(mContext);
		v.setMinimumWidth(0);
		v.setMinimumHeight(0);
		return v;
	    }
	}

	public TabManager(FragmentActivity activity, TabHost tabHost,
		int containerId) {
	    mActivity = activity;
	    mTabHost = tabHost;
	    mContainerId = containerId;
	}

	public TabInfo getTabInfo(String tabTag) {
	    return mTabs.get(tabTag);
	}

	public void removeAllTabs() {
	    for (TabInfo t : mTabs.values()) {
		detachAllFragment(t);
	    }
	}

	public void detachCurrentTab() {
	    detachAllFragment(mLastTab);
	}

	protected void detachAllFragment(TabInfo info) {
	    if (info == null)
		return;
	    FragmentManager fm = mActivity.getSupportFragmentManager();
	    List<String> tags = info.getFragmentTags();
	    boolean remove = !info.isAlwaysInMemory();
	    for (String tag : tags) {
		Fragment fragment =
			getSupportFragmentManager().findFragmentByTag(tag);
		if (fragment != null) {
		    FragmentTransaction ft = fm.beginTransaction();
		    info.putFragment(fragment);
		    if (remove) {
			ft.remove(fragment);
			info.removeFragment(fragment.getClass());
		    } else {
			ft.detach(fragment);
		    }
		    ft.commitAllowingStateLoss();
		}
	    }
	}

	public void addTab(TabInfo info) {
	    info.getTabSpec().setContent(new DummyTabFactory(mActivity));

	    // Check to see if we already have a fragment for this tab, probably
	    // from a previously saved state. If so, deactivate it, because our
	    // initial state is that a tab isn't shown.
	    detachAllFragment(info);

	    mTabs.put(info.getTabTag(), info);
	    mTabHost.addTab(info.getTabSpec());
	}

	@Override
	public void onTabChanged(String tabId) {
	    mBackCount = 0;

	    TabInfo newTab = mTabs.get(tabId);
	    FragmentManager fm = mActivity.getSupportFragmentManager();
	    FragmentTransaction ft = fm.beginTransaction();
	    // ft.setCustomAnimations(enter, exit);
	    detachAllFragment(mLastTab);
	    if (newTab != null) {
		Fragment f = newTab.getCurrentFragment();
		if (f == null) {
		    f =
			    Fragment.instantiate(mActivity, newTab
				    .getCurrentClass().getName(), newTab
				    .getBundle());
		    Log.i(TAG, "instantiate" + f.getClass().toString());
		    newTab.putFragment(f);
		    ft.add(mContainerId, f, newTab.getFragmentTag());
		} else {
		    ft.attach(f);
		}

		getIntent().putExtra(EXTRA_INDEX_TAG, newTab.getTabTag());
	    }

	    mLastTab = newTab;
	    ft.commitAllowingStateLoss();
	    try {
		fm.executePendingTransactions();
	    } catch (IllegalStateException e) {
		Log.w(TAG, "", e);
	    }

	}

	public TabInfo getCurentTab() {
	    return mLastTab;
	}
    }
}
