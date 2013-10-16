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
import com.rong360.creditassitant.util.RongStats;
import com.umeng.analytics.MobclickAgent;

public class CustomerLockActivity extends BaseActionBar implements OnClickListener{
    private RelativeLayout rlOpenPass;
    private RelativeLayout rlModifyPass;
    
    private TextView tvOPass;
    private TextView tvMPass;
    
    private boolean mCanClose = false;
    
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
	    tvOPass.setText("关闭密码锁");
	    mCanClose = true;
	} else {
	    mCanClose = false;
	    tvOPass.setText("打开密码锁");
	    tvOPass.setTextColor(Color.BLACK);
	    rlModifyPass.setClickable(false);
	    tvMPass.setTextColor(getResources().getColor(R.color.customer_label));
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
	    if (mCanClose) {
		intent.putExtra(SetPassActivity.EXTRA_MODE, SetPassActivity.MODE_CLOSE);
		MobclickAgent.onEvent(this, RongStats.LOCK_CLOSE);
	    } else {
		MobclickAgent.onEvent(this, RongStats.LOCK_OPEN);
	    }
	} else if (v == rlModifyPass) {
	    intent.putExtra(SetPassActivity.EXTRA_MODE, SetPassActivity.MODE_MODIFY);
	    MobclickAgent.onEvent(this, RongStats.LOCK_MODIFY);
	}
	IntentUtil.startActivity(this, intent);
    }
}
