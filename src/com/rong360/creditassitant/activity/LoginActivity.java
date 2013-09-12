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
import com.rong360.creditassitant.model.result.AuthCode;
import com.rong360.creditassitant.model.result.Result;
import com.rong360.creditassitant.task.BaseHttpsManager.RequestParam;
import com.rong360.creditassitant.task.DomainHelper;
import com.rong360.creditassitant.task.HandleMessageTask;
import com.rong360.creditassitant.task.HandleMessageTask.Callback;
import com.rong360.creditassitant.task.TransferDataTask;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.util.MyToast;

public class LoginActivity extends BaseActionBar implements OnClickListener {
    protected static final String TAG = "LoginActivity";
    private EditText etTel;
    private EditText etPass;
    private Button btnLogin;
    private LinearLayout llForget;

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
		params.addNameValuePair("action", "register");
		params.addNameValuePair("mobile", etTel.getText().toString());
		TransferDataTask tTask =
			new TransferDataTask(this, DomainHelper.getFullUrl(
				DomainHelper.SUFFIX_LOGIN, params));
		tTask.setCallback(new Callback() {

		    @Override
		    public void onSuccess(HandleMessageTask task, Object t) {
			try {
			    AuthCode authCode =
				    JsonHelper.parseJSONToObject(
					    AuthCode.class, task.getResult());
			    Log.i(TAG, "res:" + task.getResult());
			    if (authCode.getResult().getError()
				    .equals(ECode.SUCCESS)) {
				Intent intent =
					new Intent(LoginActivity.this,
						AuthCodeActivity.class);
				IntentUtil.startActivity(LoginActivity.this,
					intent);
			    }
			} catch (JsonParseException e) {
			    Log.e(TAG, e.toString());
			}

		    }

		    @Override
		    public void onFail(HandleMessageTask task, Object t) {
			Log.e(TAG, task.getResult());
			// Intent intent =
			// new Intent(LoginActivity.this,
			// AuthCodeActivity.class);
			// IntentUtil.startActivity(LoginActivity.this, intent);
		    }
		});

		tTask.execute();
	    }
	} else if (v == llForget) {
	    Intent intent = new Intent(this, ForgetPwdActivity.class);
	    IntentUtil.startActivity(this, intent);
	}
    }

    private boolean validateInput() {
	boolean isValid = true;
	String tel = etTel.getText().toString().trim();
	String pass = etPass.getText().toString().trim();
	if (tel.length() != 11) {
	    MyToast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
	    etTel.requestFocus();
	    return false;
	}

	if (pass.length() != 6) {
	    MyToast.makeText(this, "请输入六位数字密码", Toast.LENGTH_SHORT).show();
	    etPass.requestFocus();
	    return false;
	}

	return isValid;
    }

}
