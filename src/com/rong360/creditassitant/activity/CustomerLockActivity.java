package com.rong360.creditassitant.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.util.PreferenceHelper;

public class CustomerLockActivity extends BaseActionBar implements OnClickListener{
    private RelativeLayout rlOpenPass;
    private RelativeLayout rlModifyPass;
    
    private TextView tvOPass;
    private TextView tvMPass;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar(false).setTitle("客户密码锁");
    }
    
    @Override
    protected int getLayout() {
	return R.layout.activity_lock_customer;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        initContent();
    }
    
    private void initContent() {
	String pass =PreferenceHelper.getHelper(this).readPreference(SetPassActivity.PRE_KEY_PASS);
	if (pass != null && pass.length() > 0) {
	    rlModifyPass.setOnClickListener(this);
	    tvMPass.setTextColor(Color.BLACK);
	    rlOpenPass.setClickable(false);
	} else {
	    rlOpenPass.setOnClickListener(this);
	    tvOPass.setTextColor(Color.BLACK);
	    rlModifyPass.setClickable(false);
	}
    }

    @Override
    protected void initElements() {
	rlOpenPass = (RelativeLayout) findViewById(R.id.rlOpenPass);
	rlModifyPass = (RelativeLayout) findViewById(R.id.rlModifyPass);
	tvOPass = (TextView) findViewById(R.id.tvOpenPass);
	tvMPass = (TextView) findViewById(R.id.tvMPass);
	
	rlOpenPass.setOnClickListener(this);
	rlModifyPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
	Intent intent = new Intent(this, SetPassActivity.class);
	if (v == rlOpenPass) {
	} else if (v == rlModifyPass) {
	    intent.putExtra(SetPassActivity.EXTRA_MODE, SetPassActivity.MODE_MODIFY);
	}
	IntentUtil.startActivity(this, intent);
    }
}
