package com.rong360.creditassitant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.DataBackupHelper;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.util.PreferenceHelper;
import com.rong360.creditassitant.util.RongStats;
import com.rong360.creditassitant.widget.TitleBarCenter;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

public class SettingFragment extends BaseFragment implements OnClickListener {
    private Button btnSafe;
    private Button btnLock;

    private RelativeLayout rlImportContact;
    private RelativeLayout rlImportPartner;
    private RelativeLayout rlExport;
    private RelativeLayout rlSource;
    private RelativeLayout rlFeedback;
    private RelativeLayout rlAbout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	TitleBarCenter center = getSupportActionBarCenter(Boolean.FALSE);
	center.hideLeft();
	center.setTitle("设置");
	
	MobclickAgent.onEvent(mContext, RongStats.SETTING);
    }

    @Override
    protected int getLayout() {
	return R.layout.fragment_setting;
    }

    @Override
    public void onResume() {
	super.onResume();
	initContent();
    }

    private void initContent() {
	boolean isSafe =
		Boolean.valueOf(PreferenceHelper.getHelper(mContext)
			.readPreference(CustomerSafeActivity.PRE_KEY_IS_SAFED));
	if (isSafe) {
	    btnSafe.setBackgroundResource(R.drawable.ic_safe);
	} else {
	    btnSafe.setBackgroundResource(R.drawable.ic_safe_no);
	}

	String pass =PreferenceHelper.getHelper(mContext).readPreference(SetPassActivity.PRE_KEY_PASS);
	if (pass != null) {
	    btnLock.setBackgroundResource(R.drawable.ic_lock);
	} else {
	    btnLock.setBackgroundResource(R.drawable.ic_lock_no);
	}
    }

    @Override
    protected void initElement() {
	btnSafe = (Button) findViewById(R.id.btnSafe);
	btnLock = (Button) findViewById(R.id.btnLock);
	btnSafe.setOnClickListener(this);
	btnLock.setOnClickListener(this);
	rlImportContact = (RelativeLayout) findViewById(R.id.rlImportContact);
	rlImportPartner = (RelativeLayout) findViewById(R.id.rlImportPartner);
	rlExport = (RelativeLayout) findViewById(R.id.rlExport);
	rlSource = (RelativeLayout) findViewById(R.id.rlSource);
	rlFeedback = (RelativeLayout) findViewById(R.id.rlFeedback);
	rlAbout = (RelativeLayout) findViewById(R.id.rlAbout);
	rlImportContact.setOnClickListener(this);
	rlImportPartner.setOnClickListener(this);
	rlExport.setOnClickListener(this);
	rlSource.setOnClickListener(this);
	rlFeedback.setOnClickListener(this);
	rlAbout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
	if (v == btnSafe) {
	    MobclickAgent.onEvent(mContext, RongStats.SET_SAFE);
	    Intent intent = new Intent(mContext, CustomerSafeActivity.class);
	    IntentUtil.startActivity(mContext, intent);
	} else if (v == btnLock) {
	    MobclickAgent.onEvent(mContext, RongStats.SET_LOCK);
	    Intent intent = new Intent(mContext, CustomerLockActivity.class);
	    IntentUtil.startActivity(mContext, intent);
	} else if (v == rlSource) {
	    MobclickAgent.onEvent(mContext, RongStats.SET_SOURCE);
	    Intent intent = new Intent(mContext, SourceActivity.class);
	    IntentUtil.startActivity(mContext, intent);
	} else if (v == rlImportPartner) {
	    MobclickAgent.onEvent(mContext, RongStats.SET_IMP_PARTER);
	    Intent intent =
		    new Intent(mContext, AuthorisePartnerActivity.class);
	    IntentUtil.startActivity(mContext, intent);
	} else if (v == rlImportContact) {
	    MobclickAgent.onEvent(mContext, RongStats.SET_IMP_CTC);
	    Intent intent = new Intent(mContext, ImportContactActivity.class);
	    IntentUtil.startActivity(mContext, intent);
	} else if (v == rlExport) {
	    MobclickAgent.onEvent(mContext, RongStats.SET_EPT_SD);
	    DataBackupHelper.backupCustomers(mContext);
	} else if (v == rlFeedback) {
	    MobclickAgent.onEvent(mContext, RongStats.SET_FEEDBACK);
//	    CloudHelper.back2Server(mContext, true);
	    FeedbackAgent agent = new FeedbackAgent(mContext);
	    agent.startFeedbackActivity();
	} else if (v == rlAbout) {
	    MobclickAgent.onEvent(mContext, RongStats.SET_ABOUT);
	    Intent intent = new Intent(mContext, AboutUsActivity.class);
	    startActivity(intent);
	}
    }
}
