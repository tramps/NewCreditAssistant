package com.rong360.creditassitant.activity;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.exception.ECode;
import com.rong360.creditassitant.exception.JsonParseException;
import com.rong360.creditassitant.json.JsonHelper;
import com.rong360.creditassitant.model.result.AuthCode;
import com.rong360.creditassitant.model.result.Result;
import com.rong360.creditassitant.task.DomainHelper;
import com.rong360.creditassitant.task.HandleMessageTask;
import com.rong360.creditassitant.task.TransferDataTask;
import com.rong360.creditassitant.task.BaseHttpsManager.RequestParam;
import com.rong360.creditassitant.task.HandleMessageTask.Callback;
import com.rong360.creditassitant.util.AESUtil;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.util.MyToast;
import com.rong360.creditassitant.util.PreferenceHelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class LoginActivity extends BaseActionBar implements OnClickListener {
    protected static final String TAG = "LoginActivity";
    private EditText etPass;
    private EditText etTel;
    private Button btnLogin;
    private LinearLayout llForget;

    private String mTel;
    private String mPass;
    private String mEpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
	llForget.setOnClickListener(this);
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
		TransferDataTask tTask =
			new TransferDataTask(this, DomainHelper.getFullUrl(
				DomainHelper.SUFFIX_LOGIN, params));
		tTask.setCallback(new Callback() {

		    @Override
		    public void onSuccess(HandleMessageTask task, Object t) {
			try {
			    Result res =
				    JsonHelper.parseJSONToObject(Result.class,
					    task.getResult());
			    Log.i(TAG, "res:" + task.getResult());
			    if (res.getError() == (ECode.SUCCESS)) {
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
				finish();
			    } else if (res.getError() == 2) {
				MyToast.makeText(LoginActivity.this, "用户被封禁")
					.show();
			    } else if (res.getError() == 1) {
				MyToast.makeText(LoginActivity.this, "用户不存在或密码错误")
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
	    Intent intent = new Intent(LoginActivity.this, ForgetPwdActivity.class);
	    IntentUtil.startActivity(LoginActivity.this, intent);
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

	if (mPass.length() != 6) {
	    MyToast.makeText(this, "请输入六位数字密码", Toast.LENGTH_SHORT).show();
	    etPass.requestFocus();
	    return false;
	}

	return isValid;
    }
}
