package com.rong360.creditassitant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.exception.ECode;
import com.rong360.creditassitant.exception.JsonParseException;
import com.rong360.creditassitant.json.JsonHelper;
import com.rong360.creditassitant.model.result.SyncResult;
import com.rong360.creditassitant.task.BaseHttpsManager.RequestParam;
import com.rong360.creditassitant.task.DomainHelper;
import com.rong360.creditassitant.task.HandleMessageTask;
import com.rong360.creditassitant.task.HandleMessageTask.Callback;
import com.rong360.creditassitant.task.TransferDataTask;
import com.rong360.creditassitant.util.CloudHelper;
import com.rong360.creditassitant.util.DateUtil;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.util.MyToast;
import com.rong360.creditassitant.util.PreferenceHelper;
import com.rong360.creditassitant.util.CloudHelper.IOnFinished;

public class AuthorisePartnerActivity extends BaseActionBar {
    protected static final String TAG = "AuthorisePartnerActivity";
    public static final String PRE_KEY_LAST_SYNC = "pre_key_last_sync";
    private RelativeLayout rlAuthorize;

    private TextView tvHint;
    private ImageView ivHint;
    private ImageView ivSign;

    private ProgressBar pbCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getSupportActionBar(false).setTitle("合作伙伴方导入客户");
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_authorize_partner;
    }

    @Override
    protected void onResume() {
	super.onResume();
	PreferenceHelper helper =
		PreferenceHelper.getHelper(AuthorisePartnerActivity.this);
	String tel =
		helper.readPreference(ImportPartnerActivity.PRE_KEY_BD_TEL);
	String pass =
		helper.readPreference(ImportPartnerActivity.PRE_KEY_BD_PASS);
	if (tel != null && pass != null) {
	    ivSign.setImageResource(R.drawable.ic_green_circle);
	    pbCircle.setVisibility(View.VISIBLE);
	    tvHint.setVisibility(View.GONE);
	    CloudHelper.syncOrder(AuthorisePartnerActivity.this, onFinish);
//	    ivHint.setVisibility(View.GONE);
//	    String last = helper.readPreference(PRE_KEY_LAST_SYNC);
//	    if (last != null) {
//		tvHint.setText(DateUtil.getDisplayTimeForDetail(Long
//			.valueOf(last)));
//	    }
	} else {
	    pbCircle.setVisibility(View.GONE);
	    ivSign.setImageResource(R.drawable.ic_grey_circle);
	    tvHint.setText("未绑定");
	    tvHint.setVisibility(View.VISIBLE);
	}
	ivHint.setVisibility(View.GONE);
    }

    private IOnFinished onFinish = new IOnFinished() {

	@Override
	public void onSuccess(int count) {
	    ivSign.setImageResource(R.drawable.ic_green_circle);
	    ivHint.setImageResource(R.drawable.ic_sucseed);
	    ivHint.setVisibility(View.VISIBLE);
	    pbCircle.setVisibility(View.GONE);
	    tvHint.setText(DateUtil.getDisplayTimeForDetail(System
		    .currentTimeMillis()));
	    tvHint.setVisibility(View.VISIBLE);
	}

	@Override
	public void onFail() {
	    PreferenceHelper helper =
		    PreferenceHelper.getHelper(AuthorisePartnerActivity.this);
	    String tel =
		    helper.readPreference(ImportPartnerActivity.PRE_KEY_BD_TEL);
	    String pass =
		    helper.readPreference(ImportPartnerActivity.PRE_KEY_BD_PASS);

	    pbCircle.setVisibility(View.GONE);
	    ivSign.setImageResource(R.drawable.ic_grey_circle);
	    if (tel != null && pass != null) {
		tvHint.setText("更新失败，请稍后重试");
	    } else {
		tvHint.setText("未绑定");
	    }
	    tvHint.setVisibility(View.VISIBLE);
	    
	}
    };

    private void initContent() {
	PreferenceHelper helper =
		PreferenceHelper.getHelper(AuthorisePartnerActivity.this);
	String tel =
		helper.readPreference(ImportPartnerActivity.PRE_KEY_BD_TEL);
	String pass =
		helper.readPreference(ImportPartnerActivity.PRE_KEY_BD_PASS);
	if (tel != null && pass != null) {
	    ivSign.setVisibility(View.GONE);
	    tvHint.setVisibility(View.GONE);
	    pbCircle.setVisibility(View.VISIBLE);
	    CloudHelper.syncOrder(AuthorisePartnerActivity.this);
	} else {
	    Intent intent =
		    new Intent(AuthorisePartnerActivity.this,
			    ImportPartnerActivity.class);
	    startActivityForResult(intent, 10001);
	}
    }

    @Override
    protected void initElements() {
	rlAuthorize = (RelativeLayout) findViewById(R.id.rlAuthorize);
	rlAuthorize.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		initContent();
	    }
	});
	tvHint = (TextView) findViewById(R.id.tvHint);
	ivHint = (ImageView) findViewById(R.id.ivHint);
	ivSign = (ImageView) findViewById(R.id.ivSign);

	pbCircle = (ProgressBar) findViewById(R.id.pbCricle);
    }

    @Override
    protected void
	    onActivityResult(int requestCode, int resultCode, Intent data) {
	if (resultCode == RESULT_OK) {
	    PreferenceHelper helper =
		    PreferenceHelper.getHelper(AuthorisePartnerActivity.this);
	    String tel =
		    helper.readPreference(ImportPartnerActivity.PRE_KEY_BD_TEL);
	    String pass =
		    helper.readPreference(ImportPartnerActivity.PRE_KEY_BD_PASS);
	    if (tel != null && pass != null) {
		CloudHelper.syncOrder(AuthorisePartnerActivity.this, onFinish);
	    } else {
		tvHint.setText("未绑定");
		ivHint.setImageResource(R.drawable.ic_grey_circle);
	    }
	}
    }

}
