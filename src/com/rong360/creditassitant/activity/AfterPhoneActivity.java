package com.rong360.creditassitant.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.Action;
import com.rong360.creditassitant.model.ActionHandler;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.model.LocationHelper;
import com.rong360.creditassitant.service.PhoneNoticeService;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.PreferenceHelper;

public class AfterPhoneActivity extends Activity implements
	OnClickListener {
    private static final String TAG = AfterPhoneActivity.class.getSimpleName();
    private static final String PRE_KEY_SERIAL = "pre_key_serial";
    private String mTel;
    private Customer mCustomer;

    private Button btnClose;
    private Button btnAction;
    private TextView tvLast;
    private TextView tvProgress;

    private TextView tvTel;

    private TextView tvName;
    private TextView tvComment;

    private String mLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	mTel = getIntent().getStringExtra(PhoneNoticeService.EXTRA_CALL_NUMBER);
	mLast = getIntent().getStringExtra(PhoneNoticeService.EXTRA_LAST);
	Log.i(TAG, "number : " + mTel + " last :" + mLast);
	mCustomer =
		GlobalValue.getIns().getCustomerHandler(this)
			.getCustomerByTel(mTel);

	super.onCreate(savedInstanceState);
	
	setContentView(getLayout());
	initElements();
    }

    protected void initElements() {
	btnClose = (Button) findViewById(R.id.btnClose);
	btnAction = (Button) findViewById(R.id.btnAction);
	btnClose.setOnClickListener(this);
	btnAction.setOnClickListener(this);
	tvLast = (TextView) findViewById(R.id.tvLast);
	tvProgress = (TextView) findViewById(R.id.tvProgress);
	if (mCustomer == null) {
	    tvTel = (TextView) findViewById(R.id.tvTel);
	    tvTel.setText(mTel);
	    tvProgress.setText(LocationHelper.getAreaByNumber(this, mTel));
	} else {
	    tvName = (TextView) findViewById(R.id.tvName);
	    tvComment = (TextView) findViewById(R.id.tvComment);
	    if (mCustomer.getProgress() != null) {
		tvProgress.setText(mCustomer.getProgress());
	    } else {
		tvProgress.setText(LocationHelper.getAreaByNumber(this, mTel));
	    }
	    tvName.setText(mCustomer.getName());
	    String comment = mCustomer.getLastFollowComment();
	    if (comment != null && comment.length() > 0) {
		tvComment.setText(comment);
	    } else {
		tvComment.setText("暂无备注");
	    }
	}

	tvLast.setText(mLast);

    }

    protected int getLayout() {
	if (mCustomer == null) {
	    return R.layout.activity_after_phone_unknown;
	} else {
	    return R.layout.activity_after_phone;
	}
    }

    @Override
    public void onClick(View v) {
	if (v == btnAction) {
	    if (mCustomer == null) {
		mCustomer = new Customer();
		mCustomer.setTel(mTel);
		String serial =
			PreferenceHelper.getHelper(this).readPreference(
				PRE_KEY_SERIAL);
		int seNum = 1;
		if (serial != null && serial.length() > 0) {
		    seNum = Integer.valueOf(serial) + 1;
		}
		PreferenceHelper.getHelper(this).writePreference(
			PRE_KEY_SERIAL, String.valueOf(seNum));
		mCustomer.setName("客户" + seNum);
		mCustomer.setProgress("潜在客户");
		GlobalValue gv = GlobalValue.getIns();
		boolean isSuccess =
			gv.getCustomerHandler(this).insertCustomer(mCustomer);
		if (isSuccess) {
		    mCustomer =
			    gv.getCustomerHandler(this)
				    .getCustomerByNameAndTel(
					    mCustomer.getName(),
					    mCustomer.getTel());
		    GlobalValue.getIns().putCustomer(mCustomer);
		    Action a =
			    new Action(mCustomer.getId(),
				    ActionHandler.TYPE_NEW);
		    GlobalValue.getIns().getActionHandler(this).handleAction(a);
		} else {
		    Log.e(TAG, "insert new customer error");
		    finish();
		    return;
		}
	    }
	    
	    Intent intent = new Intent(this, CustomerDetailActivity.class);
	    intent.putExtra(AddCustomerActivity.EXTRA_CUSTOMER_ID, mCustomer.getId());
	    startActivity(intent);
	}

	finish();
    }

}
