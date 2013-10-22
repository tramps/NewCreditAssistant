package com.rong360.creditassitant.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.CommuHandler;
import com.rong360.creditassitant.model.HistoryMsg;
import com.rong360.creditassitant.model.HistoryMsgHandler;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.MyToast;
import com.rong360.creditassitant.util.RongStats;
import com.umeng.analytics.MobclickAgent;

public class SendGroupSmsActivity extends BaseActionBar implements
	OnClickListener {
    private static final String TAG = "SendGroupSmsActivity";
    public static final String EXTRA_CUSTOMER = "extra_customer";
    public static final String EXTRA_UNKNOWN = "extra_unknown";

    private TextView tvPeaple;
    private Button btnAdd;
    private EditText etMsg;
    private Button btnHistory;
    private Button btnSend;

    private ArrayList<String[]> mReceiver;
    private StringBuilder mBuilder;
    private String mCustomerInfo = "";
    private int mFilterIndex;
    private ArrayList<String> mQueryIndex;

    private String mHistoryMsg;

    private HistoryMsgHandler mHandler;
    private String mUnknown = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getSupportActionBar().setTitle("群发短信");
	setReciver(getIntent());

	mHandler = new HistoryMsgHandler(this);
	mFilterIndex =
		getIntent().getIntExtra(ChooseCustomerActivity.EXTRA_INDEX, 0);
	mQueryIndex =
		getIntent().getStringArrayListExtra(
			AdvancedFilterActiviy.EXTRA_QUERY);
	Log.i(TAG,
		"mquery size: "
			+ (mQueryIndex == null ? 0 : mQueryIndex.size()));
    }

    private void closeImm() {
	InputMethodManager imm =
		(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	imm.hideSoftInputFromWindow(etMsg.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	closeImm();
	return super.onOptionsItemSelected(item);
    }

    private void setReciver(Intent intent) {
	boolean isUnknonwn = intent.getBooleanExtra(EXTRA_UNKNOWN, false);
	mCustomerInfo = intent.getStringExtra(EXTRA_CUSTOMER);
	if (isUnknonwn) {
	    String[] cuses = mCustomerInfo.split("%");
	    for (String c : cuses) {
		String[] singel = c.split("#");
		if (singel[0].equalsIgnoreCase(singel[1])) {
		    mUnknown += c + "%";
		}
	    }
	} else {
	    mCustomerInfo = mUnknown + mCustomerInfo;
	}
	Log.i(TAG, "customerinfo: " + mCustomerInfo);
	// Log.i(TAG, mCustomerInfo);
	mReceiver = new ArrayList<String[]>();
	String[] cuses = mCustomerInfo.split("%");
	Log.i(TAG, "peaples: " + cuses.length);
	mBuilder = new StringBuilder();
	int i = 0;
	for (String c : cuses) {
	    String[] singel = c.split("#");
	    mReceiver.add(singel);
	    if (i < 3) {
		mBuilder.append(singel[0]);
		mBuilder.append(",");
	    } else if (i == 3) {
		mBuilder.append(singel[0]);
	    }
	    i++;
	}

	if (i > 3) {
	    mBuilder.append(" 等");
	}

	mBuilder.append(cuses.length + "人");
    }

    @Override
    protected void initElements() {
	tvPeaple = (TextView) findViewById(R.id.tvPeaple);
	btnAdd = (Button) findViewById(R.id.btnAdd);
	etMsg = (EditText) findViewById(R.id.etMsg);
	btnHistory = (Button) findViewById(R.id.btnHistory);
	btnSend = (Button) findViewById(R.id.btnSend);
	btnAdd.setOnClickListener(this);
	btnSend.setOnClickListener(this);
	btnHistory.setOnClickListener(this);
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_send_group_sms;
    }

    @Override
    protected void onResume() {
	super.onResume();
	initContent();
    }

    private void initContent() {
	if (mBuilder.length() > 0) {
	    tvPeaple.setText(mBuilder.toString());
	}
	ArrayList<HistoryMsg> msgs =
		GlobalValue.getIns().getHistoryMsgs(mHandler);
	Log.i(TAG, "history msg: " + msgs.size());
	if (msgs.size() > 0) {
	    btnHistory.setVisibility(View.VISIBLE);
	} else {
	    btnHistory.setVisibility(View.INVISIBLE);
	}
    }

    @Override
    public void onClick(View v) {
	closeImm();
	if (v == btnAdd) {
	    MobclickAgent.onEvent(this, RongStats.SEND_ADD);
	    addMoreCustomer();
	} else if (v == btnHistory) {
	    MobclickAgent.onEvent(this, RongStats.SEND_HTY_SMS);
	    chooseHistory();
	} else if (v == btnSend) {
	    MobclickAgent.onEvent(this, RongStats.SEND_SMS);
	    if (etMsg.getText().toString().trim().length() == 0) {
		MyToast.makeText(this, "请输入短信内容~", Toast.LENGTH_SHORT).show();
		return;
	    }
	    sendMsg();
	    insert2History();
	    finish();
	}
    }

    private void insert2History() {
	String msg = etMsg.getText().toString();
	if (mHistoryMsg == null || !mHistoryMsg.equalsIgnoreCase(msg)) {
	    mHandler.insertSms(msg);
	    GlobalValue.getIns().setMsgDirty();
	}
    }

    private void chooseHistory() {
	Intent intent = new Intent(this, ChooseHistorySmsActivity.class);
	startActivityForResult(intent, 10002);
    }

    @Override
    protected void
	    onActivityResult(int requestCode, int resultCode, Intent data) {
	if (resultCode != RESULT_OK) {
	    return;
	}

	if (requestCode == 10002) {
	    mHistoryMsg =
		    data.getStringExtra(ChooseHistorySmsActivity.EXTRA_SMS);
	    if (mHistoryMsg != null) {
		Log.i(TAG, mHistoryMsg);
		etMsg.setText(mHistoryMsg);
		etMsg.setSelection(mHistoryMsg.length());
	    }
	} else if (requestCode == 10001) {
	    setReciver(data);
	    initContent();
	    mFilterIndex =
		    data.getIntExtra(ChooseCustomerActivity.EXTRA_INDEX, 0);
	    mQueryIndex =
		    data.getStringArrayListExtra(AdvancedFilterActiviy.EXTRA_QUERY);
	    Log.i(TAG,
		    "mquery size: "
			    + (mQueryIndex == null ? 0 : mQueryIndex.size()));
	}
    }

    private void addMoreCustomer() {
	Intent intent = new Intent(this, ChooseCustomerActivity.class);
	intent.putExtra(ChooseCustomerActivity.EXTRA_INDEX, mFilterIndex);
	intent.putExtra(EXTRA_CUSTOMER, mCustomerInfo);
	intent.putExtra(ChooseCustomerActivity.EXTRA_NEW, false);
	intent.putExtra(AdvancedFilterActiviy.EXTRA_QUERY, mQueryIndex);
	Log.i(TAG,
		"mquery size: "
			+ (mQueryIndex == null ? 0 : mQueryIndex.size()));
	startActivityForResult(intent, 10001);
    }

    private void sendMsg() {
	SmsManager manager = SmsManager.getDefault();
	String msg = etMsg.getText().toString();
	ArrayList<String> dMsg = manager.divideMessage(msg);
	StringBuilder recErs = new StringBuilder();
	for (String[] re : mReceiver) {
	    manager.sendMultipartTextMessage(re[1], null, dMsg, null, null);
	    recErs.append(re[1]).append(",");
	    
	}
	if (recErs.length() > 1) {
	    String dest = recErs.substring(0, recErs.length() - 1);
	    Log.i(TAG, "des: " + dest);
	    CommuHandler.insertSms(this, msg, dest);
	}
	MyToast.makeText(this, "已给" + mReceiver.size() + "人发送消息",
		Toast.LENGTH_LONG).show();
    }

}
