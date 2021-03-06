package com.rong360.creditassitant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.exception.ECode;
import com.rong360.creditassitant.exception.JsonParseException;
import com.rong360.creditassitant.json.JsonHelper;
import com.rong360.creditassitant.model.result.AuthCode;
import com.rong360.creditassitant.model.result.Result;
import com.rong360.creditassitant.model.result.TResult;
import com.rong360.creditassitant.task.BaseHttpsManager.RequestParam;
import com.rong360.creditassitant.task.DomainHelper;
import com.rong360.creditassitant.task.HandleMessageTask;
import com.rong360.creditassitant.task.HandleMessageTask.Callback;
import com.rong360.creditassitant.task.TransferDataTask;
import com.rong360.creditassitant.util.AESUtil;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.util.MyToast;
import com.rong360.creditassitant.util.PackageUtils;
import com.rong360.creditassitant.util.PreferenceHelper;

public class AuthCodeActivity extends BaseActionBar implements OnClickListener {
    private static final String TAG = "AuthCodeActivity";
    public static final String EXTRA_AUTH_CODE = "extra_auth_code";
    public static final String EXTRA_TEL = "extra_tel";
    public static final String EXTRA_PASS = "extra_pass";
    public static final String EXTRA_IS_FORGET = "extra_is_forget";
    private Button btnOk;
    private Button btnReget;
    private EditText etAuth;
    private String mAuthCode;
    private String mPass;
    private String mTel;
    private String mEpass;
    
    private TextView tvHint;
    
    private String hint = "2/2  已向您的手机号";
    private String suffix = "发送了一条验证码，请输入验证码";
    
    private boolean isForget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getSupportActionBar().setTitle("输入验证码");
	mAuthCode = getIntent().getStringExtra(EXTRA_AUTH_CODE);
	mTel = getIntent().getStringExtra(EXTRA_TEL);
	mPass = getIntent().getStringExtra(EXTRA_PASS);
	
	isForget = getIntent().getBooleanExtra(EXTRA_IS_FORGET, false);
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_auth_code;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        if (!isForget) {
            tvHint.setText(hint + mTel + suffix + "完成注册");
        } else {
            tvHint.setText(hint + mTel + suffix + "找回密码");
        }
    }

    @Override
    protected void initElements() {
	btnOk = (Button) findViewById(R.id.btnOk);
	btnOk.setOnClickListener(this);
	btnReget = (Button) findViewById(R.id.btnReget);
	btnReget.setOnClickListener(this);
	etAuth = (EditText) findViewById(R.id.etAuthcode);
	
	tvHint = (TextView) findViewById(R.id.tvHint);
    }

    @Override
    public void onClick(View v) {
	if (v == btnOk) {
	    handleOk();
	} else if (v == btnReget) {
	    handleReget();
	}
    }

    private void handleReget() {
	RequestParam params = new RequestParam();
	params.addNameValuePair("mobile", mTel);
	params.addNameValuePair("action", "register");
	TransferDataTask tTask =
		new TransferDataTask(this, DomainHelper.getFullUrl(
			DomainHelper.SUFFIX_GET_AUTH_CODE, params));
	tTask.setCallback(new Callback() {

	    @Override
	    public void onSuccess(HandleMessageTask task, Object t) {
		try {
		    AuthCode res =
			    JsonHelper.parseJSONToObject(AuthCode.class,
				    task.getResult());
		    Log.i(TAG, "res:" + task.getResult());
		    if (res.getResult().getError() == (ECode.SUCCESS)) {
			mAuthCode = res.getAuth_Code();
			MyToast.makeText(AuthCodeActivity.this, "发送成功，请稍候！").show();
		    } else {
			MyToast.makeText(AuthCodeActivity.this, "网络较差，请过后再试试吧！").show();
			Log.e(TAG, "auth code resend error " + mAuthCode);
		    }
		} catch (JsonParseException e) {
		    Log.e(TAG, e.toString());
		}
	    }

	    @Override
	    public void onFail(HandleMessageTask task, Object t) {
		Log.e(TAG, "auth code resend failed ");
	    }
	});

	tTask.execute();
    }

    private void handleOk() {
	String auth = etAuth.getText().toString().trim();
	if (mAuthCode.equalsIgnoreCase(auth)) {
	    TransferDataTask tTask;
	    if (!isForget) {
		RequestParam params = new RequestParam();
		params.addNameValuePair("mobile", mTel);
		mEpass = AESUtil.encryptHex(mTel, mPass);
		if (mEpass == null) {
		    mEpass = mPass;
		    Log.w(TAG, "encrypt failed");
		}
		params.addNameValuePair("password", mEpass);
		params.addNameValuePair("mobile_IMEI",
			PackageUtils.getCustomIMEI(this));
		tTask =
			new TransferDataTask(this, DomainHelper.getFullUrl(
				DomainHelper.SUFFIX_REGISTER, params));
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
						.getHelper(AuthCodeActivity.this);
				helper.writePreference(
					CustomerSafeActivity.PRE_KEY_IS_SAFED,
					Boolean.TRUE.toString());
				helper.writePreference(
					CustomerSafeActivity.PRE_KEY_IS_LOGINED,
					Boolean.TRUE.toString());
				helper.writePreference(EXTRA_TEL, mTel);
				helper.writePreference(EXTRA_PASS, mEpass);
				finish();
			    } else if (res.mResult.getError() == 2) {
				MyToast.makeText(AuthCodeActivity.this, "验证码错误")
					.show();
			    } else if (res.mResult.getError() == 4) {
				MyToast.makeText(AuthCodeActivity.this, "用户已存在")
					.show();
				Intent intent =
					new Intent(AuthCodeActivity.this,
						LoginActivity.class);
				IntentUtil.startActivity(AuthCodeActivity.this,
					intent);
				finish();
			    }
			} catch (JsonParseException e) {
			    Log.e(TAG, e.toString());
			}
		    }

		    @Override
		    public void onFail(HandleMessageTask task, Object t) {

		    }
		});

		tTask.execute();
	    } else {
		Intent intent =
			new Intent(AuthCodeActivity.this,
				ResetPwdActivity.class);
		intent.putExtra(AuthCodeActivity.EXTRA_TEL, mTel);
		IntentUtil.startActivity(AuthCodeActivity.this, intent);
		finish();
	    }
	} else {
	    MyToast.makeText(AuthCodeActivity.this, "验证码错误").show();
	}
    }
}
