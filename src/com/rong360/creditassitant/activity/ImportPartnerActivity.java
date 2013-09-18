package com.rong360.creditassitant.activity;

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
import com.rong360.creditassitant.model.result.Result;
import com.rong360.creditassitant.task.BaseHttpsManager.RequestParam;
import com.rong360.creditassitant.task.DomainHelper;
import com.rong360.creditassitant.task.HandleMessageTask;
import com.rong360.creditassitant.task.HandleMessageTask.Callback;
import com.rong360.creditassitant.task.TransferDataTask;
import com.rong360.creditassitant.util.MyToast;
import com.rong360.creditassitant.util.PreferenceHelper;

public class ImportPartnerActivity extends BaseActionBar implements OnClickListener{
    protected static final String TAG = "ImportPartnerActivity";
    public static final String PRE_KEY_BD_TEL = "pre_key_bd_tel";
    public static final String PRE_KEY_BD_PASS = "pre_key_bd_pwd";
    private EditText etTel;
    private EditText etPass;
    
    private Button btnOk;
    private String mTel;
    private String mPass;
    
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
    }
    


    @Override
    public void onClick(View v) {
	if (validateInput()) {
	    RequestParam params = new RequestParam();
	    params.addNameValuePair("mobile", mTel);
		params.addNameValuePair("password", mPass);
		params.addNameValuePair("app_type", 2);
		TransferDataTask tTask =
			new TransferDataTask(this, DomainHelper.getFullUrl(
				DomainHelper.SUFFIX_LOGIN, params));
		tTask.setCallback(new Callback() {

		    @Override
		    public void onSuccess(HandleMessageTask task, Object t) {
			try {
			    Result authCode =
				    JsonHelper.parseJSONToObject(
					    Result.class, task.getResult());
			    Log.i(TAG, "res:" + task.getResult());
			    if (authCode.getError()
				    == (ECode.SUCCESS)) {
				PreferenceHelper.getHelper(ImportPartnerActivity.this).writePreference(PRE_KEY_BD_TEL, mTel);
				PreferenceHelper.getHelper(ImportPartnerActivity.this).writePreference(PRE_KEY_BD_PASS, mPass);
				finish();
				setResult(RESULT_OK);
			    } else if (authCode.getError() == 1) {
				MyToast.makeText(ImportPartnerActivity.this, "用户不存在").show();
			    } else if (authCode.getError() == 2) {
				MyToast.makeText(ImportPartnerActivity.this, "用户名或密码错误").show();
			    } else if (authCode.getError() == 3) {
				MyToast.makeText(ImportPartnerActivity.this, "用户被封禁").show();
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

   	if (mPass.length() != 6) {
   	    MyToast.makeText(this, "请输入六位数字密码", Toast.LENGTH_SHORT).show();
   	    etPass.requestFocus();
   	    return false;
   	}

   	return isValid;
       }
}
