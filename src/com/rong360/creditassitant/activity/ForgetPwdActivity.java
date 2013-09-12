package com.rong360.creditassitant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.IntentUtil;

public class ForgetPwdActivity extends BaseActionBar {
    private Button btnGetAuth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("忘记密码");
        
    }
    
    @Override
    protected void initElements() {
	btnGetAuth = (Button) findViewById(R.id.btnGetAuth);
	btnGetAuth.setOnClickListener(new OnClickListener() {
	    
	    @Override
	    public void onClick(View v) {
		Intent intent = new Intent(ForgetPwdActivity.this, AuthCodeActivity.class);
		IntentUtil.startActivity(ForgetPwdActivity.this, intent);
		
	    }
	});
    }
    
    @Override
    protected int getLayout() {
	return R.layout.activity_forget_pwd;
    }

}
