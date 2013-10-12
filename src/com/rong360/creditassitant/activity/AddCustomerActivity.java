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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.Action;
import com.rong360.creditassitant.model.ActionHandler;
import com.rong360.creditassitant.model.CommuHandler;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.model.TelHelper;
import com.rong360.creditassitant.util.AlarmHelper;
import com.rong360.creditassitant.util.CloudHelper;
import com.rong360.creditassitant.util.DateUtil;
import com.rong360.creditassitant.util.DialogUtil;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.PreferenceHelper;
import com.rong360.creditassitant.util.DialogUtil.ITimePicker;
import com.rong360.creditassitant.util.MyToast;
import com.rong360.creditassitant.util.NetUtil;
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

    private ImageButton ibDelete;

    private HashMap<RelativeLayout, TextView> mChooseMap;
    private HashMap<RelativeLayout, EditText> mInputMap;
    private HashMap<RelativeLayout, String> mTitleMap;

    private TextView mCurrentEditV;
    private HashMap<View, Integer> mValues;

    public static final String EXTRA_CUSTOMER_ID = "extra_customer_id";
    public static final String EXTRA_TEL = "extra_tel";
    public static final int MAX_NAME_LENGTH = 10;
    private static final int MAX_TEL_LENGTH = 15;
    private int mCustomerId = -1;
    private Customer mCustomer;
    private String mTel;

    private Calendar mAlarm;

    private ArrayList<Action> mActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	mChooseMap = new HashMap<RelativeLayout, TextView>();
	mInputMap = new HashMap<RelativeLayout, EditText>();
	mTitleMap = new HashMap<RelativeLayout, String>();
	mValues = new HashMap<View, Integer>();
	mCustomerId = getIntent().getIntExtra(EXTRA_CUSTOMER_ID, -1);
	super.onCreate(savedInstanceState);
	if (mCustomerId == -1) {
	    getSupportActionBar().setTitle("添加客户");
	    mTel = getIntent().getStringExtra(EXTRA_TEL);
	    Log.i(TAG, "mtel : " + mTel);
	} else {
	    getSupportActionBar().setTitle("编辑客户");
	    mCustomer = GlobalValue.getIns().getCusmer(mCustomerId);
	}

	mActions = new ArrayList<Action>();

	initContent();
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
	ibDelete = (ImageButton) findViewById(R.id.ibDelete);
	ibDelete.setOnClickListener(this);

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

    @Override
    protected void onResume() {
	super.onResume();
    }

    private void initContent() {
	Log.i(TAG, "initcontent");
	if (mCustomer == null) {
	    if (mTel != null) {
		etTel.setText(mTel);
	    }

	    ibDelete.setVisibility(View.GONE);
	    return;
	}
	etName.setText(mCustomer.getName());
	etName.setSelection(mCustomer.getName().length());
	etTel.setText(mCustomer.getTel());
	if (mCustomer.getLoan() != 0) {
	    etLoan.setText(mCustomer.getLoan() + "");
	}
	if (mCustomer.getAlarmTime() > System.currentTimeMillis()) {
	    tvAlarm.setText(DateUtil.yyyyMMddHHmm.format(new Date(mCustomer
		    .getAlarmTime())));
	    ibDelete.setVisibility(View.VISIBLE);
	} else {
	    ibDelete.setVisibility(View.GONE);
	}
	if (mCustomer.getLastFollowComment() != null) {
	    etComment.setText(mCustomer.getLastFollowComment());
	}
	if (mCustomer.getProgress() != null) {
	    String pro = mCustomer.getProgress();
	    tvProgress.setText(pro);
	    String[] p = getResources().getStringArray(R.array.progress);
	    int i = 0;
	    for (String pp : p) {
		if (pp.equalsIgnoreCase(pro)) {
		    mValues.put(tvProgress, i);
		}
		i++;
	    }
	}
	if (mCustomer.getSource() != null) {
	    // todo;
	    tvSource.setText(mCustomer.getSource());
	}
	setChooseValue(tvBank, mCustomer.getBank(), R.array.bankCash);
	setChooseValue(tvCash, mCustomer.getCash(), R.array.bankCash);
	setChooseValue(tvId, mCustomer.getIdentity(), R.array.identity);
	setChooseValue(tvCreditRecord, mCustomer.getCreditRecord(),
		R.array.credit);
	setChooseValue(tvHouse, mCustomer.getHouse(), R.array.house);
	setChooseValue(tvCar, mCustomer.getCar(), R.array.car);

	closeImm();
    }

    private void closeImm() {
	InputMethodManager imm =
		(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	mySrollview.closeInput(mySrollview, imm);
    }

    private void setChooseValue(TextView v, int selected, int arrayId) {
	String[] arr = getResources().getStringArray(arrayId);
	if (selected > 0) {
	    v.setText(arr[selected]);
	    mValues.put(v, selected);
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
	    // AlarmHelper.startAlarm(this, true);
	}
	
	if (mCustomer != null) {
	    go2Detail();
	}
	closeImm();
	finish();
	return true;
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
		MyToast.makeText(this, "非法字符", Toast.LENGTH_SHORT).show();
		etTel.requestFocus();
		etTel.setSelection(i + 1);
		valid = false;
		return valid;
	    }
	}

	if (mCustomerId == -1
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

	String loan = etLoan.getText().toString();
	if (loan != null && loan.length() > 0 && !loan.matches("^-?\\d+$")) {
	    mySrollview.scrollTo(0, etLoan.getTop());
	    MyToast.makeText(this, "非法字符", Toast.LENGTH_SHORT).show();
	    etLoan.requestFocus();
	    etLoan.setSelection(loan.length());
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
	String tel = etTel.getText().toString();
	mCustomer.setTel(TelHelper.getPureTel(tel));
	mCustomer.setLoan(getEditText(etLoan));
	mCustomer.setSource(tvSource.getText().toString());

	String progress = tvProgress.getText().toString();
	if (progress != null
		&& !progress.equalsIgnoreCase(mCustomer.getProgress())) {
	    Action action =
		    new Action(mCustomerId, ActionHandler.TYPE_PROGRESS);
	    action.setContent(progress);
	    mActions.add(action);
	}
	mCustomer.setProgress(progress);
	if (mAlarm != null) {
	    mCustomer.setAlarmTime(mAlarm.getTimeInMillis());
	}
	// TODO
	String comment = etComment.getText().toString().trim();
	if (comment.length() > 0
		&& !comment.equalsIgnoreCase(mCustomer.getLastFollowComment())) {
	    mCustomer.setLastFollowComment(comment);
	    Action action = new Action(mCustomerId, ActionHandler.TYPE_COMMENT);
	    action.setContent(mCustomer.getLastFollowComment());
	    mActions.add(action);
	}

	Resources res = getResources();
	int bank = getValueFromMap(tvBank);
	if (bank != -1 && mCustomer.getBank() != bank) {
	    mCustomer.setBank(bank);
	    Action action = new Action(mCustomerId, ActionHandler.TYPE_QUALITY);
	    String[] array = res.getStringArray(R.array.bankCash);
	    action.setContent(TITLE_BANK + "-" + array[bank]);
	    mActions.add(action);
	}

	int cash = getValueFromMap(tvCash);
	if (cash != -1 && mCustomer.getCash() != cash) {
	    mCustomer.setCash(cash);
	    Action action = new Action(mCustomerId, ActionHandler.TYPE_QUALITY);
	    String[] array = res.getStringArray(R.array.bankCash);
	    action.setContent(TITLE_CASH + "-" + array[cash]);
	    mActions.add(action);
	}

	int identity = getValueFromMap(tvId);
	if (identity != -1 && mCustomer.getIdentity() != identity) {
	    mCustomer.setIdentity(identity);
	    Action action = new Action(mCustomerId, ActionHandler.TYPE_QUALITY);
	    String[] array = res.getStringArray(R.array.identity);
	    action.setContent(TITLE_ID + "-" + array[identity]);
	    mActions.add(action);
	}

	int credit = getValueFromMap(tvCreditRecord);
	if (credit != -1 && mCustomer.getCreditRecord() != credit) {
	    mCustomer.setCreditRecord(credit);
	    Action action = new Action(mCustomerId, ActionHandler.TYPE_QUALITY);
	    String[] array = res.getStringArray(R.array.credit);
	    action.setContent(TITLE_CREDI_RECORD + "-" + array[credit]);
	    mActions.add(action);
	}

	int house = getValueFromMap(tvHouse);
	if (house != -1 && mCustomer.getHouse() != house) {
	    mCustomer.setHouse(house);
	    Action action = new Action(mCustomerId, ActionHandler.TYPE_QUALITY);
	    String[] array = res.getStringArray(R.array.house);
	    action.setContent(TITLE_HOUSE + "-" + array[house]);
	    mActions.add(action);
	}

	int car = getValueFromMap(tvCar);
	if (car != -1 && mCustomer.getCar() != car) {
	    mCustomer.setCar(car);
	    Action action = new Action(mCustomerId, ActionHandler.TYPE_QUALITY);
	    String[] array = res.getStringArray(R.array.car);
	    action.setContent(TITLE_CAR + "-" + array[car]);
	    mActions.add(action);
	}

	boolean isSuccess = false;
	if (mCustomer.getId() == 0) {
	    if (mCustomer.getProgress() == null
		    || mCustomer.getProgress().length() == 0) {
		mCustomer.setProgress("潜在客户");
	    }
	    isSuccess =
		    GlobalValue.getIns().getCustomerHandler(this)
			    .insertCustomer(mCustomer);
	    mCustomer =
		    GlobalValue
			    .getIns()
			    .getCustomerHandler(this)
			    .getCustomerByNameAndTel(mCustomer.getName(),
				    mCustomer.getTel());
	    // mCustomerId = mCustomer.getId();

	    Action a = new Action(mCustomer.getId(), ActionHandler.TYPE_NEW);
	    GlobalValue.getIns().getActionHandler(this).handleAction(a);

	    // remove from blacklist
	    // NoticeIgnoreHandler handler =
	    // GlobalValue.getIns().getNoticeHandler(this);
	    // NoticeIgnore ignore = handler.getIgnoreByTel(mCustomer.getTel());
	    //
	    // if (ignore != null) {
	    // handler.remove(ignore);
	    // }

	} else {
	    isSuccess =
		    GlobalValue.getIns().getCustomerHandler(this)
			    .updateCustomer(mCustomer);
	}

	if (isSuccess) {
	    GlobalValue.getIns().putCustomer(mCustomer);
	    ActionHandler handler = GlobalValue.getIns().getActionHandler(this);
	    for (Action a : mActions) {
		a.setCustomerId(mCustomer.getId());
		handler.handleAction(a);
	    }

	    CommuHandler.setNewAddName(mTel, this);

	    if (NetUtil.isNetworkAvailable(this)) {
		CloudHelper.back2Server(this, false);
	    }
	}

	return isSuccess;
    }

    private int getValueFromMap(TextView v) {
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
	    mCurrentEditV = mChooseMap.get(v);
	    Intent intent = new Intent(this, ChooseOptionActivity.class);
	    intent.putExtra(ChooseOptionActivity.EXTRA_TITLE, mTitleMap.get(v));
	    intent.putExtra(ChooseOptionActivity.EXTRA_SELECTED_INDEX,
		    mValues.get(mChooseMap.get(v)));
	    if (mCurrentEditV == tvSource) {
		intent.putExtra(ChooseOptionActivity.EXTRA_RESULT_TEXT,
			tvSource.getText());
	    }
	    startActivityForResult(intent, REQUEST_CODE);
	    closeImm();
	} else if (mInputMap.containsKey(v)) {
	    Log.i(TAG, "input");
	    EditText et = mInputMap.get(v);
	    et.requestFocus();
	    et.setSelection(et.getText().length());
	    InputMethodManager im =
		    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    im.showSoftInput(et, InputMethodManager.RESULT_SHOWN);
	} else if (v == rlAlarm) {
	    closeImm();
	    DialogUtil.showTimePicker(this, mTimePickListener);
	} else if (v == btnDelete) {
	    GlobalValue.getIns().getCustomerHandler(this)
		    .deleteCustomer(mCustomerId);
	    GlobalValue.getIns().removeCustomer(mCustomerId);
	    GlobalValue.getIns().getActionHandler(this)
		    .deleteAction(mCustomerId);
	    CommuHandler.removeNameByPhone(mCustomer.getTel(), this);
	    String ids =
		    PreferenceHelper.getHelper(this).readPreference(
			    CloudHelper.PRE_KEY_DELETE_IDS);
	    if (ids == null || ids.length() == 0) {
		ids = String.valueOf(mCustomerId);
	    } else {
		ids += ", " + mCustomerId;
	    }
	    PreferenceHelper.getHelper(this).writePreference(
		    CloudHelper.PRE_KEY_DELETE_IDS, ids);

	    CloudHelper.deleteCustomer(this);
	    finish();
	} else if (v == ibDelete) {
	    tvAlarm.setText("");
	    mCustomer.setAlarmTime(0);
	    mCustomer.setHasChecked(false);
	    mCustomer.setIsDisplayed(false);
	    GlobalValue.getIns().putCustomer(mCustomer);
	    GlobalValue.getIns().getCustomerHandler(getBaseContext())
		    .updateCustomer(mCustomer);

	    Action action =
		    new Action(mCustomer.getId(),
			    ActionHandler.TYPE_CANCEL_ALARM);
	    GlobalValue.getIns().getActionHandler(AddCustomerActivity.this)
		    .handleAction(action);
	    AlarmHelper.startAlarm(AddCustomerActivity.this, true);

	    ibDelete.setVisibility(View.GONE);
	}
    }

    private ITimePicker mTimePickListener = new ITimePicker() {

	@Override
	public void onTimePicked(String time, Calendar alarm) {
	    tvAlarm.setText(time);
	    mAlarm = alarm;

	    ibDelete.setVisibility(View.VISIBLE);

	    if (mCustomer == null) {
		mCustomer = new Customer();
	    }
	    mCustomer.setAlarmTime(alarm.getTimeInMillis());
	    mCustomer.setHasChecked(false);
	    mCustomer.setIsDisplayed(false);
	    if (mCustomerId != -1) {
		GlobalValue.getIns().putCustomer(mCustomer);
		GlobalValue.getIns().getCustomerHandler(getBaseContext())
			.updateCustomer(mCustomer);

		AlarmHelper.startAlarm(AddCustomerActivity.this, true);
	    }

	    Action action =
		    new Action(mCustomer.getId(), ActionHandler.TYPE_SET_ALARM);
	    action.setContent(DateUtil.yyyyMMddHHmm.format(alarm.getTime()));
	    if (mCustomerId == -1) {
		mActions.add(action);
		return;
	    } else {
		GlobalValue.getIns().getActionHandler(AddCustomerActivity.this)
			.handleAction(action);
	    }
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
