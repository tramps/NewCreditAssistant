package com.rong360.creditassitant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.LocCache;
import com.rong360.creditassitant.util.PreferenceHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * the main tab host and the main activity of this app.
 */
public class MainTabHost extends BaseTabHost {
    private static final String TAG = MainTabHost.class.getSimpleName();

    public static final String TAG_COMUNICATION = "tag_comunication";
    public static final String TAG_CUSTOMER = "tag_custom";
    public static final String TAG_FOLLOW = "tag_task";
    public static final String TAG_SETTING = "tag_setting";

    public static final Class<? extends Fragment> TAG_SUB_TASK =
	    TaskFragment.class;
    public static final Class<? extends Fragment> TAG_SUB_CUSTOMER =
	    CustomerManagementFragment.class;
    public static final Class<? extends Fragment> TAG_SUB_COMUNICATION =
	    ComunicationHistoryFragment.class;
    public static final Class<? extends Fragment> TAG_SUB_SETTING =
	    SettingFragment.class;

    private ComunicationHistoryTab mComunicationTab;
    private CustomersTab mCustomersTab;
    private FollowTab mTaskTab;
    private SettingTab mSettingTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mBackCount = 0;
	com.umeng.common.Log.LOG = true;
	MobclickAgent.updateOnlineConfig(this);
	UmengUpdateAgent.update(this);
    }

    // private UmengUpdateListener mUpdateCallback = new UmengUpdateListener() {
    // @Override
    // public void onUpdateReturned(int updateStatus,
    // UpdateResponse updateInfo) {
    // switch (updateStatus) {
    // case 0: // has update
    // UmengUpdateAgent.showUpdateDialog(MainTabHost.this, updateInfo);
    // break;
    // }
    //
    // }
    // };

    @Override
    protected void onDestroy() {
	// InitialUtil.unregisterOnExitListener(mOnExitListener);
	super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
	super.onNewIntent(intent);
	setIntent(intent);
    }

    @Override
    public void onResume() {
	super.onResume();
	
	if (PreferenceHelper.getHelper(this).readPreference("shortcut") == null) {
	    createShortCut();
	    PreferenceHelper.getHelper(this).writePreference("shortcut", "cuted");
	}
	// MobclickAgent.onResume(this);
    }
    
    private void createShortCut() {
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");   
        shortcutintent.putExtra("duplicate", false); 
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "融易记");             
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_launcher);      
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon); 
        Intent pIntent = new Intent(Intent.ACTION_MAIN);  
        pIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pIntent.setClassName(this, LoadingActivity.class.getName());  
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, pIntent);  
        sendBroadcast(shortcutintent);
    }

    @Override
    public void onPause() {
	super.onPause();
	// MobclickAgent.onPause(this);
    }

    // private OnExitListener mOnExitListener = new OnExitListener() {
    // @Override
    // public void onExit() {
    // finish();
    // }
    // };
    
    @Override
    public void onBackPressed() {
	if (mBackCount < 1) {
	    mBackCount++;
	    Toast.makeText(this, "再按一次退出融易记", Toast.LENGTH_SHORT).show();
	} else {
	    LocCache.getInstance().saveCache(this);
	    super.onBackPressed();
	}
    }

    public void finish() {
	mTabManager.detachCurrentTab();
	super.finish();
    };

    @Override
    protected int getLayout() {
	return R.layout.bottom_tab_host;
    }

    @Override
    protected TabInfo[] getTabInfos() {
	TabInfo[] infos = new TabInfo[4];
	infos[0] = mCustomersTab = new CustomersTab();
	infos[1] = mTaskTab = new FollowTab();
	infos[2] = mComunicationTab = new ComunicationHistoryTab();
	infos[3] = mSettingTab = new SettingTab();
	return infos;
    }

    class ComunicationHistoryTab extends TabInfo {
	public ComunicationHistoryTab() {
	    mTabSpec =
		    makeTabSpec(1, TAG_COMUNICATION, "历史通讯",
			    R.drawable.ic_history);
	    setAlwaysInMemory(true);
	}

	@Override
	protected void initFragments() {
	    addFragment(TAG_SUB_COMUNICATION);
	}

	@Override
	public Class<? extends Fragment> getCurrentClass() {
	    setCurrentClass(TAG_SUB_COMUNICATION);
	    return super.getCurrentClass();
	}
    }

    class CustomersTab extends TabInfo {
	public CustomersTab() {
	    mTabSpec =
		    makeTabSpec(2, TAG_CUSTOMER, "客户管理",
			    R.drawable.ic_customers);
	    setAlwaysInMemory(true);
	}

	@Override
	protected void initFragments() {
	    addFragment(TAG_SUB_CUSTOMER);
	}
    }

    public class FollowTab extends TabInfo {

	public FollowTab() {
	    mTabSpec = makeTabSpec(3, TAG_FOLLOW, "提醒", R.drawable.ic_follow);
	    setAlwaysInMemory(true);
	}

	@Override
	protected void initFragments() {
	    addFragment(TAG_SUB_TASK);
	}

    }

    public class SettingTab extends TabInfo {

	public SettingTab() {
	    mTabSpec = makeTabSpec(4, TAG_SETTING, "设置", R.drawable.ic_setting);
	}

	@Override
	protected void initFragments() {
	    addFragment(TAG_SUB_SETTING);
	}
    }

    @Override
    protected int getBackgroundRes() {
	return R.drawable.tab_indicator;
    }

}
