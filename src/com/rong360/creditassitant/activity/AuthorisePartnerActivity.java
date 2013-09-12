package com.rong360.creditassitant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.IntentUtil;

public class AuthorisePartnerActivity extends BaseActionBar {
    private RelativeLayout rlAuthorize;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar(false).setTitle("合作伙伴方导入客户");
    }
    
    @Override
    protected int getLayout() {
	return R.layout.activity_authorize_partner;
    }
    
    @Override
    protected void initElements() {
	rlAuthorize = (RelativeLayout) findViewById(R.id.rlAuthorize);
	rlAuthorize.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		Intent intent = new Intent(AuthorisePartnerActivity.this, ImportPartnerActivity.class);
		IntentUtil.startActivity(AuthorisePartnerActivity.this, intent);
		
	    }
	});
    }

}
