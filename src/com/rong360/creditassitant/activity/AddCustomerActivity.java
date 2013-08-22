package com.rong360.creditassitant.activity;

import static com.rong360.creditassitant.activity.ChooseOptionActivity.EXTRA_RESULT_ID;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.EXTRA_RESULT_TEXT;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_BANK;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_CAR;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_CASH;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_CREDI_RECORD;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_HOUSE;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_ID;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_PROGRESS;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_SOURCE;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.model.GlobalValue;
import com.rong360.creditassitant.model.NoticeIgnore;
import com.rong360.creditassitant.model.NoticeIgnoreHandler;
import com.rong360.creditassitant.model.TelHelper;
import com.rong360.creditassitant.util.DialogUtil;
import com.rong360.creditassitant.util.DialogUtil.ITimePicker;
import com.rong360.creditassitant.util.MyToast;
import com.rong360.creditassitant.widget.MySrollview;

public class AddCustomerActivity extends BaseActionBar implements
	OnClickListener {
    private static final String TAG = "AddCustomerActivity";
    private static final int REQUEST_CODE = 10000;

    private static final String phoneCharacters = "*#,()/; -+"; // phones

    private MySrollview mySrollview;

    private RelativeLayout rlName;
    private RelativeLayout rlTel;
    private RelativeLayout rlLoan;
    private RelativeLayout rlSource;
    private RelativeLayout rlProgress;
    private RelativeLayout rlAlarm;
    private RelativeLayout rlComment;
    private RelativeLayout rlBank;
    private RelativeLayout rlCash;
    private RelativeLayout rlId;
    private RelativeLayout rlCreditRecord;
    private RelativeLayout rlCar;
    private RelativeLayout rlHouse;

    private EditText etName;
    private EditText etTel;
    private EditText etLoan;
    private TextView tvSource;
    private TextView tvProgress;
    private TextView tvAlarm;
    private EditText etComment;
    private TextView tvBank;
    private TextView tvCash;
    private TextView tvId;
    private TextView tvCreditRecord;
    private TextView tvCar;
    private TextView tvHouse;

    private Button btnDelete;

    private HashMap<RelativeLayout, TextView> mChooseMap;
    private HashMap<RelativeLayout, EditText> mInputMap;
    private HashMap<RelativeLayout, String> mTitleMap;

    private TextView mCurrentEditV;
    private HashMap<View, Integer> mValues;

    public static final String EXTRA_CUSTOMER_ID = "extra_customer_id";
    private static final int MAX_NAME_LENGTH = 6;
    private static final int MAX_TEL_LENGTH = 15;
    private int mCustomerId = -1;
    private Customer mCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	mChooseMap = new HashMap<RelativeLayout, TextView>();
	mInputMap = new HashMap<RelativeLayout, EditText>();
	mTitleMap = new HashMap<RelativeLayout, String>();
	mValues = new HashMap<View, Integer>();

	super.onCreate(savedInstanceState);
	mCustomerId = getIntent().getIntExtra(EXTRA_CUSTOMER_ID, -1);
	if (mCustomerId == -1) {
	    getSupportActionBar().setTitle("添加客户");
	} else {
	    getSupportActionBar().setTitle("编辑客户");
	    mCustomer = GlobalValue.getIns().getCusmer(mCustomerId);
	}
    }

    @Override
    protected void initElements() {
	mySrollview = (MySrollview) findViewById(R.id.myScrollView);

	rlName = (RelativeLayout) findViewById(R.id.rlName);
	rlTel = (RelativeLayout) findViewById(R.id.rlTel);
	rlLoan = (RelativeLayout) findViewById(R.id.rlLoan);
	rlSource = (RelativeLayout) findViewById(R.id.rlSource);
	rlProgress = (RelativeLayout) findViewById(R.id.rlProgress);
	rlAlarm = (RelativeLayout) findViewById(R.id.rlAlarm);
	rlComment = (RelativeLayout) findViewById(R.id.rlComment);
	rlBank = (RelativeLayout) findViewById(R.id.rlBank);
	rlCash = (RelativeLayout) findViewById(R.id.rlCash);
	rlId = (RelativeLayout) findViewById(R.id.rlId);
	rlCreditRecord = (RelativeLayout) findViewById(R.id.rlCreditRecord);
	rlCar = (RelativeLayout) findViewById(R.id.rlCar);
	rlHouse = (RelativeLayout) findViewById(R.id.rlHouse);

	rlAlarm.setOnClickListener(this);

	etName = (EditText) findViewById(R.id.etName);
	etTel = (EditText) findViewById(R.id.etTel);
	etLoan = (EditText) findViewById(R.id.etLoan);
	tvSource = (TextView) findViewById(R.id.tvCSource);
	tvProgress = (TextView) findViewById(R.id.tvCProgress);
	tvAlarm = (TextView) findViewById(R.id.tvCAlarm);
	etComment = (EditText) findViewById(R.id.etComment);
	tvBank = (TextView) findViewById(R.id.tvCBank);
	tvCash = (TextView) findViewById(R.id.tvCCash);
	tvId = (TextView) findViewById(R.id.tvCIdentity);
	tvCreditRecord = (TextView) findViewById(R.id.tvCCreditRecord);
	tvHouse = (TextView) findViewById(R.id.tvCHouse);
	tvCar = (TextView) findViewById(R.id.tvCCar);

	btnDelete = (Button) findViewById(R.id.btnDelete);
	btnDelete.setOnClickListener(this);

	initMap();
	setListener();

	ShowButton();
    }

    private void ShowButton() {
	if (mCustomerId == -1) {
	    btnDelete.setVisibility(View.GONE);
	} else {
	    btnDelete.setVisibility(View.VISIBLE);
	}
    }

    private void initMap() {
	mChooseMap.put(rlSource, tvSource);
	mChooseMap.put(rlProgress, tvProgress);
	mChooseMap.put(rlCash, tvCash);
	mChooseMap.put(rlBank, tvBank);
	mChooseMap.put(rlId, tvId);
	mChooseMap.put(rlCreditRecord, tvCreditRecord);
	mChooseMap.put(rlHouse, tvHouse);
	mChooseMap.put(rlCar, tvCar);

	mTitleMap.put(rlSource, TITLE_SOURCE);
	mTitleMap.put(rlProgress, TITLE_PROGRESS);
	mTitleMap.put(rlCash, TITLE_CASH);
	mTitleMap.put(rlBank, TITLE_BANK);
	mTitleMap.put(rlId, TITLE_ID);
	mTitleMap.put(rlCreditRecord, TITLE_CREDI_RECORD);
	mTitleMap.put(rlHouse, TITLE_HOUSE);
	mTitleMap.put(rlCar, TITLE_CAR);

	mInputMap.put(rlName, etName);
	mInputMap.put(rlTel, etTel);
	mInputMap.put(rlLoan, etLoan);
	mInputMap.put(rlComment, etComment);
    }

    private void setListener() {
	for (RelativeLayout rl : mChooseMap.keySet()) {
	    rl.setOnClickListener(this);
	}

	for (RelativeLayout rl : mInputMap.keySet()) {
	    rl.setOnClickListener(this);
	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	menu.add(0, R.id.finish, 0, "完成");
	return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	if (item.getItemId() == R.id.finish) {

	    if (!validateInput()) {
		return false;
	    }

	    save2Db();
	    go2Detail();
	    finish();
	    return true;

	}
	return super.onOptionsItemSelected(item);
    }

    private boolean validateInput() {
	boolean valid = true;

	String name = etName.getText().toString().trim();
	if (name.length() > MAX_NAME_LENGTH || name.length() == 0) {
	    mySrollview.scrollTo(0, etName.getTop());
	    if (name.length() == 0) {
		MyToast.makeText(this, "姓名必须填写", Toast.LENGTH_SHORT).show();
	    } else {
		MyToast.makeText(this, "姓名长度不能超过" + MAX_NAME_LENGTH,
			Toast.LENGTH_SHORT).show();
	    }
	    etName.requestFocus();
	    valid = false;
	    return valid;
	}

	char[] tel = etTel.getText().toString().trim().toCharArray();
	if (tel.length == 0 || tel.length > MAX_TEL_LENGTH) {
	    mySrollview.scrollTo(0, etTel.getTop());
	    if (tel.length == 0) {
		MyToast.makeText(this, "联系电话必须填写", Toast.LENGTH_SHORT).show();
	    } else {
		MyToast.makeText(this, "联系电话不能超过" + MAX_TEL_LENGTH,
			Toast.LENGTH_SHORT).show();
	    }
	    etTel.requestFocus();
	    valid = false;
	    return valid;
	}

	for (int i = 0; i < tel.length; i++) {
	    if (!Character.isDigit(tel[i])
		    && phoneCharacters.indexOf(tel[i]) == -1) {
		mySrollview.scrollTo(0, etTel.getTop());
		MyToast.makeText(this, "非法字符", Toast.LENGTH_SHORT);
		etTel.requestFocus();
		etTel.setSelection(i + 1);
		valid = false;
		return valid;
	    }
	}

	if (mCustomerId == 0
		&& GlobalValue
			.getIns()
			.getCustomerHandler(this)
			.getCustomerByTel(
				TelHelper.getPureTel(String.valueOf(tel))) != null) {
	    mySrollview.scrollTo(0, etTel.getTop());
	    MyToast.makeText(this, "该号码已存在", Toast.LENGTH_LONG).show();
	    etTel.requestFocus();
	    valid = false;
	    return valid;
	}
	return valid;
    }

    private void go2Detail() {
	Intent intent = new Intent(this, CustomerDetailActivity.class);
	intent.putExtra(EXTRA_CUSTOMER_ID, mCustomer.getId());
	startActivity(intent);
	finish();
    }

    private boolean save2Db() {
	if (mCustomer == null) {
	    mCustomer = new Customer();
	}

	mCustomer.setName(etName.getText().toString());
	mCustomer.setTel(etTel.getText().toString());
	mCustomer.setLoan(getEditText(etLoan));
	mCustomer.setSource(tvSource.getText().toString());
	mCustomer.setProgress(tvProgress.getText().toString());
	mCustomer.setAlarmTime(getValueFromMap(tvAlarm));
	// TODO
	mCustomer.setLastFollowComment(etComment.getText().toString().trim());
	mCustomer.setBank(getValueFromMap(tvBank));
	mCustomer.setCash(getValueFromMap(tvCash));
	mCustomer.setIdentity(getValueFromMap(tvId));
	mCustomer.setCreditRecord(getValueFromMap(tvCreditRecord));
	mCustomer.setHouse(getValueFromMap(tvHouse));
	mCustomer.setCar(getValueFromMap(tvCar));

	boolean isSuccess = false;
	if (mCustomer.getId() == 0) {
	    isSuccess =
		    GlobalValue.getIns().getCustomerHandler(this)
			    .insertCustomer(mCustomer);
	    mCustomer =
		    GlobalValue
			    .getIns()
			    .getCustomerHandler(this)
			    .getCustomerByNameAndTel(mCustomer.getName(),
				    mCustomer.getTel());

	    // remove from blacklist
//	    NoticeIgnoreHandler handler =
//		    GlobalValue.getIns().getNoticeHandler(this);
//	    NoticeIgnore ignore = handler.getIgnoreByTel(mCustomer.getTel());
//
//	    if (ignore != null) {
//		handler.remove(ignore);
//	    }

	} else {
	    isSuccess =
		    GlobalValue.getIns().getCustomerHandler(this)
			    .updateCustomer(mCustomer);
	}

	if (isSuccess) {
	    GlobalValue.getIns().putCustomer(mCustomer);
	}

	return isSuccess;
    }
    
    private long getValueFromMap(TextView v) {
	int selected = -1;
	if (mValues.containsKey(v)) {
	    selected = mValues.get(v); 
	}
	
	return selected;
    }

    private long getEditText(EditText et) {
	String text = et.getText().toString().trim();
	if (text.length() > 8) {
	    text = text.substring(0, 8);
	}

	long value = 0;
	if (text.length() > 0) {
	    // XXX
	    try {
		value = Long.valueOf(text);
	    } catch (Exception e) {
		value = Long.valueOf(text.trim().substring(0, 8));
	    }
	}

	return value;
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_add_customer;
    }

    @Override
    public void onClick(View v) {
	// View child =
	if (mChooseMap.containsKey(v)) {
	    Log.i(TAG, "choose");
	    mCurrentEditV = mChooseMap.get(v);
	    Intent intent = new Intent(this, ChooseOptionActivity.class);
	    intent.putExtra(ChooseOptionActivity.EXTRA_TITLE, mTitleMap.get(v));
	    intent.putExtra(ChooseOptionActivity.EXTRA_SELECTED_INDEX,
		    mValues.get(mChooseMap.get(v)));
	    startActivityForResult(intent, REQUEST_CODE);
	} else if (mInputMap.containsKey(v)) {
	    Log.i(TAG, "input");
	    EditText et = mInputMap.get(v);
	    et.requestFocus();
	    et.setSelection(0);
	    InputMethodManager im =
		    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    im.showSoftInput(et, InputMethodManager.RESULT_SHOWN);
	} else if (v == rlAlarm) {
	    InputMethodManager imm =
		    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    mySrollview.closeInput(mySrollview, imm);
	    DialogUtil.showTimePicker(this, mTimePickListener);
	}
    }

    private ITimePicker mTimePickListener = new ITimePicker() {

	@Override
	public void onTimePicked(String time) {
	    tvAlarm.setText(time);
	}

    };

    @Override
    protected void
	    onActivityResult(int requestCode, int resultCode, Intent data) {
	if (resultCode == RESULT_OK) {
	    int res = data.getIntExtra(EXTRA_RESULT_ID, -1);
	    String title = data.getStringExtra(EXTRA_RESULT_TEXT);
	    mValues.put(mCurrentEditV, res);
	    mCurrentEditV.setText(title);
	}
    }

}
