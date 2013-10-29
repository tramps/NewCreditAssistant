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
import com.rong360.creditassitant.model.result.TResult;
import com.rong360.creditassitant.task.BaseHttpsManager.RequestParam;
import com.rong360.creditassitant.task.DomainHelper;
import com.rong360.creditassitant.task.HandleMessageTask;
import com.rong360.creditassitant.task.HandleMessageTask.Callback;
import com.rong360.creditassitant.task.TransferDataTask;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.util.MyToast;
import com.rong360.creditassitant.util.PreferenceHelper;

public class ImportPartnerActivity extends BaseActionBar implements
	OnClickListener {
    protected static final String TAG = "ImportPartnerActivity";
    public static final String EXTRA_MODE = "extra_mode";
    public static final int MODE_LOGIN = 1;
    public static final String PRE_KEY_BD_TEL = "pre_key_bd_tel";
    public static final String PRE_KEY_BD_PASS = "pre_key_bd_pwd";
    private EditText etTel;
    private EditText etPass;

    private Button btnOk;
    private String mTel;
    private String mPass;

    private int mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getSupportActionBar().setTitle("融360账号登录授权");
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_import_partner;
    }

    @Override
    protected void initElements() {
	etTel = (EditText) findViewById(R.id.etTel);
	etPass = (EditText) findViewById(R.id.etPass);
	btnOk = (Button) findViewById(R.id.btnLogin);
	btnOk.setOnClickListener(this);
	mMode = getIntent().getIntExtra(EXTRA_MODE, 0);
	if (mMode == MODE_LOGIN) {
	    btnOk.setText("登录");
	}
    }

    @Override
    public void onClick(View v) {
	if (validateInput()) {
	    RequestParam params = new RequestParam();
	    params.addNameValuePair("mobile", mTel);
	    params.addNameValuePair("password", mPass);
	    params.addNameValuePair("app_type", 2);
	    String ryj =
		    PreferenceHelper.getHelper(this).readPreference(
			    AuthCodeActivity.EXTRA_TEL);
	    if (ryj == null) {
		ryj = "";
	    }
	    params.addNameValuePair("ryj_account", ryj);
	    TransferDataTask tTask =
		    new TransferDataTask(this, DomainHelper.getFullUrl(
			    DomainHelper.SUFFIX_LOGIN, params));
	    tTask.setCallback(new Callback() {

		@Override
		public void onSuccess(HandleMessageTask task, Object t) {
		    try {
			TResult tResult =
				JsonHelper.parseJSONToObject(TResult.class,
					task.getResult());
			Log.i(TAG, "res:" + task.getResult());
			if (tResult.mResult.getError() == (ECode.SUCCESS)) {
			    PreferenceHelper.getHelper(
				    ImportPartnerActivity.this)
				    .writePreference(PRE_KEY_BD_TEL, mTel);
			    PreferenceHelper.getHelper(
				    ImportPartnerActivity.this)
				    .writePreference(PRE_KEY_BD_PASS, mPass);
			    if (mMode == MODE_LOGIN) {
				MyToast.displayFeedback(ImportPartnerActivity.this, R.drawable.ic_alert, "请使用融易记账号登录");
			    }
			    finish();
			    setResult(RESULT_OK);
			} else if (tResult.mResult.getError() == 1
				|| tResult.mResult.getError() == 110) {
			    MyToast.makeText(ImportPartnerActivity.this,
				    "用户不存在").show();
			} else if (tResult.mResult.getError() == 2) {
			    MyToast.makeText(ImportPartnerActivity.this,
				    "用户名或密码错误").show();
			} else if (tResult.mResult.getError() == 110) {
			    MyToast.makeText(ImportPartnerActivity.this,
				    "用户被封禁").show();
			} else if (tResult.mResult.getError() == 120) {
			    PreferenceHelper.getHelper(
				    ImportPartnerActivity.this)
				    .writePreference(PRE_KEY_BD_TEL, mTel);
			    PreferenceHelper.getHelper(
				    ImportPartnerActivity.this)
				    .writePreference(PRE_KEY_BD_PASS, mPass);

			    if (mMode == MODE_LOGIN) {
				Intent intent =
					new Intent(ImportPartnerActivity.this,
						ResetPwdActivity.class);
				intent.putExtra(AuthCodeActivity.EXTRA_TEL,
					mTel);
				intent.putExtra(ResetPwdActivity.EXTRA_FIRST, true);
				IntentUtil.startActivity(
					ImportPartnerActivity.this, intent);
			    } else {
				setResult(RESULT_OK);
			    }
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

	return isValid;
    }
}
