package com.rong360.creditassitant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.exception.ECode;
import com.rong360.creditassitant.exception.JsonParseException;
import com.rong360.creditassitant.json.JsonHelper;
import com.rong360.creditassitant.model.result.AuthCode;
import com.rong360.creditassitant.task.BaseHttpsManager.RequestParam;
import com.rong360.creditassitant.task.DomainHelper;
import com.rong360.creditassitant.task.HandleMessageTask;
import com.rong360.creditassitant.task.HandleMessageTask.Callback;
import com.rong360.creditassitant.task.TransferDataTask;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.util.MyToast;

public class RegisterActivity extends BaseActionBar implements OnClickListener {
    protected static final String TAG = "RegisterActivity";
    private EditText etTel;
    private EditText etPass;
    private Button btnLogin;
    private Button btnLoginCA;
    private Button btnLoginBD;
    
    private String mTel;
    private String mPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getSupportActionBar().setTitle("注册");
    }

    @Override
    protected void initElements() {
	etTel = (EditText) findViewById(R.id.etTel);
	etPass = (EditText) findViewById(R.id.etPass);
	btnLogin = (Button) findViewById(R.id.btnLogin);
	btnLogin.setOnClickListener(this);
	btnLoginBD = (Button) findViewById(R.id.btnLoginBD);
	btnLoginCA = (Button) findViewById(R.id.btnLoginCA);
	btnLoginBD.setOnClickListener(this);
	btnLoginCA.setOnClickListener(this);
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_register;
    }

    @Override
    public void onClick(View v) {
	if (v == btnLogin) {
	    if (validateInput()) {
		RequestParam params = new RequestParam();
		params.addNameValuePair("action", "register");
		params.addNameValuePair("mobile", mTel);
		TransferDataTask tTask =
			new TransferDataTask(this, DomainHelper.getFullUrl(
				DomainHelper.SUFFIX_GET_AUTH_CODE, params));
		tTask.setCallback(new Callback() {

		    @Override
		    public void onSuccess(HandleMessageTask task, Object t) {
			try {
			    Log.i(TAG, "res:" + task.getResult());
			    AuthCode authCode =
				    JsonHelper.parseJSONToObject(
					    AuthCode.class, task.getResult());
			    if (authCode.getResult().getError()
				    == (ECode.SUCCESS)) {
				Intent intent =
					new Intent(RegisterActivity.this,
						AuthCodeActivity.class);
				intent.putExtra(
					AuthCodeActivity.EXTRA_AUTH_CODE,
					authCode.getAuth_Code());
				intent.putExtra(AuthCodeActivity.EXTRA_TEL, mTel);
				intent.putExtra(AuthCodeActivity.EXTRA_PASS, mPass);
				IntentUtil.startActivity(RegisterActivity.this,
					intent);
				finish();
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
	} else if (v == btnLoginCA) {
	    Intent intent = new Intent(this, LoginActivity.class);
	    IntentUtil.startActivity(this, intent);
	    finish();
	} else if (v == btnLoginBD) {
	    Intent intent = new Intent(this, ImportPartnerActivity.class);
	    intent.putExtra(ImportPartnerActivity.EXTRA_MODE, ImportPartnerActivity.MODE_LOGIN);
	    IntentUtil.startActivity(this, intent);
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

	if (mPass.length() != 6) {
	    MyToast.makeText(this, "请输入六位数字密码", Toast.LENGTH_SHORT).show();
	    etPass.requestFocus();
	    return false;
	}

	return isValid;
    }

}
