package com.rong360.creditassitant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.exception.ECode;
import com.rong360.creditassitant.exception.JsonParseException;
import com.rong360.creditassitant.json.JsonHelper;
import com.rong360.creditassitant.model.result.TResult;
import com.rong360.creditassitant.task.BaseHttpsManager.RequestParam;
import com.rong360.creditassitant.task.DomainHelper;
import com.rong360.creditassitant.task.HandleMessageTask;
import com.rong360.creditassitant.task.HandleMessageTask.Callback;
import com.rong360.creditassitant.task.TransferDataTask;
import com.rong360.creditassitant.util.AESUtil;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.util.MyToast;
import com.rong360.creditassitant.util.PreferenceHelper;

public class LoginActivity extends BaseActionBar implements OnClickListener {
    protected static final String TAG = "LoginActivity";
    private EditText etPass;
    private EditText etTel;
    private Button btnLogin;
    private LinearLayout llForget;

    private String mTel;
    private String mPass;
    private String mEpass;

    private boolean mUnLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	mUnLock = getIntent().getBooleanExtra(LockActivity.EXTRA_LOCK, true);
	super.onCreate(savedInstanceState);
	getSupportActionBar().setTitle("融易记账号登录");
    }

    @Override
    protected void initElements() {
	etTel = (EditText) findViewById(R.id.etTel);
	etPass = (EditText) findViewById(R.id.etPass);
	btnLogin = (Button) findViewById(R.id.btnLogin);
	btnLogin.setOnClickListener(this);
	llForget = (LinearLayout) findViewById(R.id.llForget);
//	if (mUnLock) {
	    llForget.setOnClickListener(this);
//	} else {
//	    llForget.setVisibility(View.GONE);
//	}
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_login;
    }

    @Override
    public void onClick(View v) {
	if (v == btnLogin) {
	    if (validateInput()) {
		RequestParam params = new RequestParam();
		mEpass = AESUtil.encryptHex(mTel, mPass);
		if (mEpass == null) {
		    mEpass = mPass;
		    Log.w(TAG, "encrypt failed");
		}
		params.addNameValuePair("password", mEpass);
		params.addNameValuePair("app_type", 1);
		params.addNameValuePair("mobile", mTel);
		String bdAccount =
			PreferenceHelper.getHelper(this).readPreference(
				ImportPartnerActivity.PRE_KEY_BD_TEL);
		if (bdAccount == null) {
		    bdAccount = "";
		}
		params.addNameValuePair("bd_account", bdAccount);
		TransferDataTask tTask =
			new TransferDataTask(this, DomainHelper.getFullUrl(
				DomainHelper.SUFFIX_LOGIN, params));
		tTask.setCallback(new Callback() {

		    @Override
		    public void onSuccess(HandleMessageTask task, Object t) {
			try {
			    Log.i(TAG, "res:" + task.getResult());
			    TResult res =
				    JsonHelper.parseJSONToObject(TResult.class,
					    task.getResult());
			    if (res.mResult.getError() == (ECode.SUCCESS)) {
				PreferenceHelper helper =
					PreferenceHelper
						.getHelper(LoginActivity.this);
				helper.writePreference(
					CustomerSafeActivity.PRE_KEY_IS_SAFED,
					Boolean.TRUE.toString());
				helper.writePreference(
					CustomerSafeActivity.PRE_KEY_IS_LOGINED,
					Boolean.TRUE.toString());
				helper.writePreference(
					AuthCodeActivity.EXTRA_TEL, mTel);
				helper.writePreference(
					AuthCodeActivity.EXTRA_PASS, mEpass);
				setResult(RESULT_OK);
				finish();
			    } else if (res.mResult.getError() == 2) {
				MyToast.makeText(LoginActivity.this, "用户被封禁")
					.show();
			    } else if (res.mResult.getError() == 1
				    || res.mResult.getError() == 110) {
				MyToast.makeText(LoginActivity.this,
					"用户不存在或密码错误").show();
			    } else if (res.mResult.getError() == 4) {
				MyToast.makeText(LoginActivity.this, "用户已存在")
					.show();
			    }
			} catch (JsonParseException e) {
			    Log.e(TAG, e.toString());
			}

		    }

		    @Override
		    public void onFail(HandleMessageTask task, Object t) {
			Log.e(TAG, "failed");
			// Intent intent =
			// new Intent(LoginActivity.this,
			// AuthCodeActivity.class);
			// IntentUtil.startActivity(LoginActivity.this, intent);
		    }
		});

		tTask.execute();
	    }
	} else if (v == llForget) {
	    Intent intent =
		    new Intent(LoginActivity.this, ForgetPwdActivity.class);
	    IntentUtil.startActivity(LoginActivity.this, intent);
	    finish();
	}
    }

    private boolean validateInput() {
	boolean isValid = true;
	mTel = etTel.getText().toString().trim();
	mPass = etPass.getText().toString().trim();
	if (mTel.length() != 11) {
	    MyToast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
	    etTel.requestFocus();
	    return false;
	}

	if (!mUnLock) {
	    String existTel =
		    PreferenceHelper.getHelper(this).readPreference(
			    AuthCodeActivity.EXTRA_TEL);
	    if (!mTel.equalsIgnoreCase(existTel)) {
		MyToast.makeText(this, "请用之前登录账号解锁",
			Toast.LENGTH_SHORT).show();
		etTel.requestFocus();
		return false;
	    }
	}

	if (mPass.length() != 6) {
	    MyToast.makeText(this, "请输入六位密码", Toast.LENGTH_SHORT).show();
	    etPass.requestFocus();
	    return false;
	}

	return isValid;
    }
}
