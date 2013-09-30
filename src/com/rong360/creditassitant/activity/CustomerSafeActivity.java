package com.rong360.creditassitant.activity;

import java.util.ArrayList;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.util.CloudHelper;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.util.PreferenceHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomerSafeActivity extends BaseActionBar implements
	OnClickListener {
    private ImageView ivSafe;
    private TextView tvTitle;
    private TextView tvDetail;
    private Button btnSafe;
    private LinearLayout llSafe;
    private LinearLayout llSafeNo;

    public static final String PRE_KEY_IS_LOGINED = "pre_key_is_logined";
    public static final String PRE_KEY_IS_SAFED = "pre_key_is_safed";
    public static final String PRE_KEY_HAS_HINTED = "pre_key_has_hinted";

    private boolean mIsSafed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getSupportActionBar(false).setTitle("客户保险箱");
    }

    @Override
    protected void initElements() {
	ivSafe = (ImageView) findViewById(R.id.ivSafe);
	tvTitle = (TextView) findViewById(R.id.tvSafeTitle);
	tvDetail = (TextView) findViewById(R.id.tvSafeDetail);
	btnSafe = (Button) findViewById(R.id.btnSafe);
	btnSafe.setOnClickListener(this);
	llSafe = (LinearLayout) findViewById(R.id.llSafe);
	llSafeNo = (LinearLayout) findViewById(R.id.llSafeNo);
    }

    @Override
    protected void onResume() {
	super.onResume();
	initContent();
    }

    private void initContent() {
	mIsSafed =
		Boolean.valueOf(PreferenceHelper.getHelper(this)
			.readPreference(PRE_KEY_IS_SAFED));
	if (mIsSafed) {
	    ivSafe.setImageResource(R.drawable.ic_safe_normal);
	    tvTitle.setText("已开启客户保险箱");
	    tvDetail.setText("您的客户资料正在保护中");
	    btnSafe.setBackgroundResource(R.drawable.ic_stop);
	    llSafe.setVisibility(View.VISIBLE);
	    llSafeNo.setVisibility(View.GONE);
	    
	    ArrayList<Customer> customers = GlobalValue.getIns().getAllCustomers();
	    if (customers.size() == 0) {
		CloudHelper.restoreFromCloud(this);
	    }
	} else {
	    ivSafe.setImageResource(R.drawable.ic_save_no_normal);
	    tvTitle.setText("尚未开启客户保险箱");
	    tvDetail.setText("立即开启，保护客户资料");
	    btnSafe.setBackgroundResource(R.drawable.ic_start);
	    llSafe.setVisibility(View.GONE);
	    llSafeNo.setVisibility(View.VISIBLE);
	}
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_safe_customer;
    }

    @Override
    public void onClick(View v) {
	if (v == btnSafe) {
	    if (mIsSafed) {
		mIsSafed = false;
		PreferenceHelper.getHelper(this).writePreference(
			PRE_KEY_IS_SAFED, Boolean.FALSE + "");
		initContent();
	    } else {
		boolean isLogined =
			Boolean.valueOf(PreferenceHelper.getHelper(this)
				.readPreference(PRE_KEY_IS_LOGINED));
		if (isLogined) {
		    mIsSafed = true;
		    PreferenceHelper.getHelper(this).writePreference(
			    PRE_KEY_IS_SAFED, Boolean.TRUE + "");
		    initContent();
		} else {
		    Intent intent = new Intent(this, RegisterActivity.class);
		    IntentUtil.startActivity(this, intent);
		}
	    }
	}

    }

}
