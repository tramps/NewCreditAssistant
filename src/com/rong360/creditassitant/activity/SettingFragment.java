package com.rong360.creditassitant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.CloudHelper;
import com.rong360.creditassitant.util.DataBackupHelper;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.util.PreferenceHelper;
import com.rong360.creditassitant.widget.TitleBarCenter;

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
	    Intent intent = new Intent(mContext, CustomerSafeActivity.class);
	    IntentUtil.startActivity(mContext, intent);
	} else if (v == btnLock) {
	    Intent intent = new Intent(mContext, CustomerLockActivity.class);
	    IntentUtil.startActivity(mContext, intent);
	} else if (v == rlSource) {
	    Intent intent = new Intent(mContext, SourceActivity.class);
	    IntentUtil.startActivity(mContext, intent);
	} else if (v == rlImportPartner) {
	    Intent intent =
		    new Intent(mContext, AuthorisePartnerActivity.class);
	    IntentUtil.startActivity(mContext, intent);
	} else if (v == rlImportContact) {
	    Intent intent = new Intent(mContext, ImportContactActivity.class);
	    IntentUtil.startActivity(mContext, intent);
	} else if (v == rlExport) {
	    DataBackupHelper.backupCustomers(mContext);
	} else if (v == rlFeedback) {
	    CloudHelper.back2Server(mContext, true);
	}
    }
}
