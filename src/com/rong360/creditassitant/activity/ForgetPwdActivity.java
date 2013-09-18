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
import com.rong360.creditassitant.task.DomainHelper;
import com.rong360.creditassitant.task.HandleMessageTask;
import com.rong360.creditassitant.task.TransferDataTask;
import com.rong360.creditassitant.task.BaseHttpsManager.RequestParam;
import com.rong360.creditassitant.task.HandleMessageTask.Callback;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.util.MyToast;

public class ForgetPwdActivity extends BaseActionBar {
    private static final String TAG = "ForgetPwdActivity";

    private Button btnGetAuth;
    private EditText etTel;
    private String mTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getSupportActionBar().setTitle("忘记密码");

    }

    @Override
    protected void initElements() {
	etTel = (EditText) findViewById(R.id.etTel);
	btnGetAuth = (Button) findViewById(R.id.btnGetAuth);
	btnGetAuth.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		if (validateInput()) {
		    RequestParam params = new RequestParam();
		    params.addNameValuePair("mobile", mTel);
		    params.addNameValuePair("action", "find_password");
		    TransferDataTask tTask =
			    new TransferDataTask(ForgetPwdActivity.this,
				    DomainHelper.getFullUrl(
					    DomainHelper.SUFFIX_GET_AUTH_CODE,
					    params));
		    tTask.setCallback(new Callback() {

			@Override
			public void onSuccess(HandleMessageTask task, Object t) {
			    try {
				AuthCode authCode =
					JsonHelper.parseJSONToObject(
						AuthCode.class,
						task.getResult());
				Log.i(TAG, "res:" + task.getResult());
				if (authCode.getResult().getError() == (ECode.SUCCESS)) {
				    Intent intent =
					    new Intent(ForgetPwdActivity.this,
						    AuthCodeActivity.class);
				    intent.putExtra(AuthCodeActivity.EXTRA_TEL,
					    mTel);
				    intent.putExtra(AuthCodeActivity.EXTRA_IS_FORGET, true);
				    intent.putExtra(AuthCodeActivity.EXTRA_AUTH_CODE, authCode.getAuth_Code());
				    IntentUtil.startActivity(
					    ForgetPwdActivity.this, intent);
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
			    // IntentUtil.startActivity(LoginActivity.this,
			    // intent);
			}
		    });

		    tTask.execute();
		}
	    }
	});
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_forget_pwd;
    }

    private boolean validateInput() {
	boolean isValid = true;
	mTel = etTel.getText().toString().trim();
	if (mTel.length() != 11) {
	    MyToast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
	    etTel.requestFocus();
	    return false;
	}

	return isValid;
    }

}
