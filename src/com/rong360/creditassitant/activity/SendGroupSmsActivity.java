package com.rong360.creditassitant.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.HistoryMsg;
import com.rong360.creditassitant.model.HistoryMsgHandler;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.MyToast;

public class SendGroupSmsActivity extends BaseActionBar implements
	OnClickListener {
    private static final String TAG = "SendGroupSmsActivity";
    public static final String EXTRA_CUSTOMER = "extra_customer";

    private TextView tvPeaple;
    private Button btnAdd;
    private EditText etMsg;
    private Button btnHistory;
    private Button btnSend;

    private ArrayList<String[]> mReceiver;
    private StringBuilder mBuilder;
    private String mCustomerInfo;
    private int mFilterIndex;
    private ArrayList<String> mQueryIndex;

    private String mHistoryMsg;

    private HistoryMsgHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getSupportActionBar().setTitle("群发短信");
	setReciver(getIntent());

	mHandler = new HistoryMsgHandler(this);
	mFilterIndex = getIntent().getIntExtra(ChooseCustomerActivity.EXTRA_INDEX, 0);
	mQueryIndex = getIntent().getStringArrayListExtra(AdvancedFilterActiviy.EXTRA_QUERY);
	Log.i(TAG, "mquery size: " + (mQueryIndex == null? 0 : mQueryIndex.size()));
    }

    private void setReciver(Intent intent) {
	mCustomerInfo = intent.getStringExtra(EXTRA_CUSTOMER);
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
	if (v == btnAdd) {
	    addMoreCustomer();
	} else if (v == btnHistory) {
	    chooseHistory();
	} else if (v == btnSend) {
	    if (etMsg.getText().toString().trim().length() == 0) {
		MyToast.makeText(this, "请输入短信内容~", Toast.LENGTH_SHORT).show();
		return;
	    }
//	    sendMsg();
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
	    mFilterIndex = data.getIntExtra(ChooseCustomerActivity.EXTRA_INDEX, 0);
	    mQueryIndex = data.getStringArrayListExtra(AdvancedFilterActiviy.EXTRA_QUERY);
	    Log.i(TAG, "mquery size: " + (mQueryIndex == null? 0 : mQueryIndex.size()));
	}
    }

    private void addMoreCustomer() {
	Intent intent = new Intent(this, ChooseCustomerActivity.class);
	intent.putExtra(ChooseCustomerActivity.EXTRA_INDEX, mFilterIndex);
	intent.putExtra(EXTRA_CUSTOMER, mCustomerInfo);
	intent.putExtra(ChooseCustomerActivity.EXTRA_NEW, false);
	intent.putExtra(AdvancedFilterActiviy.EXTRA_QUERY, mQueryIndex);
	Log.i(TAG, "mquery size: " + (mQueryIndex == null? 0 : mQueryIndex.size()));
	startActivityForResult(intent, 10001);
    }

    private void sendMsg() {
	 SmsManager manager = SmsManager.getDefault();
	 for (String[] re : mReceiver) {
	 String msg = etMsg.getText().toString();
	 ArrayList<String> dMsg = manager.divideMessage(msg);
	 manager.sendMultipartTextMessage(re[1], null, dMsg, null, null);
	 }
	MyToast.makeText(this, "已给" + mReceiver.size() + "人发送消息",
		Toast.LENGTH_LONG).show();
    }

}
