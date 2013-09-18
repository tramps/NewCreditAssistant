package com.rong360.creditassitant.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.AESUtil;
import com.rong360.creditassitant.util.MyToast;
import com.rong360.creditassitant.util.PreferenceHelper;

public class ResetPwdActivity extends BaseActionBar implements OnClickListener {
    protected static final String TAG = "ResetPwdActivity";
    private EditText etPass;
    private Button btnOk;

    private String mTel;
    private String mPass;
    private String mEpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getSupportActionBar().setTitle("重置密码");
	mTel = getIntent().getStringExtra(AuthCodeActivity.EXTRA_TEL);
    }

    @Override
    protected void initElements() {
	etPass = (EditText) findViewById(R.id.etPass);
	btnOk = (Button) findViewById(R.id.btnOk);
	btnOk.setOnClickListener(this);
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_reset_pwd;
    }

    @Override
    public void onClick(View v) {
	if (validateInput()) {
	    mEpass = AESUtil.encryptHex(mTel, mPass);
	    if (mEpass == null) {
		mEpass = mPass;
		Log.w(TAG, "encrypt failed");
	    }
	    PreferenceHelper helper =
		    PreferenceHelper.getHelper(ResetPwdActivity.this);
	    helper.writePreference(
			CustomerSafeActivity.PRE_KEY_IS_SAFED,
			Boolean.TRUE.toString());
	    helper.writePreference(CustomerSafeActivity.PRE_KEY_IS_LOGINED,
		    Boolean.TRUE.toString());
	    helper.writePreference(AuthCodeActivity.EXTRA_TEL, mTel);
	    helper.writePreference(AuthCodeActivity.EXTRA_PASS, mEpass);
	    finish();
	}
    }

    private boolean validateInput() {
	boolean isValid = true;
	mPass = etPass.getText().toString();
	if (mPass.length() != 6) {
	    MyToast.makeText(this, "请输入六位数字密码", Toast.LENGTH_SHORT).show();
	    etPass.requestFocus();
	    return false;
	}

	return isValid;
    }
}
