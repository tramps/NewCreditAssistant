package com.rong360.creditassitant.activity;

import com.rong360.creditassitant.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AuthCodeActivity extends BaseActionBar implements OnClickListener{
    private Button btnOk;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("输入验证码");
    }
    
    @Override
    protected int getLayout() {
	return R.layout.activity_auth_code;
    }
    
    @Override
    protected void initElements() {
	btnOk = (Button) findViewById(R.id.btnOk);
	btnOk.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
	if (v == btnOk) {
//	    Intent intent = new Intent(this, SetPassActivity.class);
//	    in
	}
    }

}
