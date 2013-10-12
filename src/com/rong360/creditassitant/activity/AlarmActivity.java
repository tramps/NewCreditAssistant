package com.rong360.creditassitant.activity;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.Action;
import com.rong360.creditassitant.model.ActionHandler;
import com.rong360.creditassitant.model.CommuHandler;
import com.rong360.creditassitant.model.Communication;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.util.AlarmHelper;
import com.rong360.creditassitant.util.DateUtil;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.util.MPlayHelper;

public class AlarmActivity extends BaseActionBar implements OnClickListener {
    public static final String EXTRA_IDS = "extra_alarm_tels";
    private static final String TAG = "AlarmActivity";

    private static final int LAYOUT_SINGLE = R.layout.activity_alarm_single;
    private static final int LAYOUT_MORE = R.layout.activity_alarm_more;

    private Button btnClose;
    private Button btnView;
    private ImageButton btnSlient;
    private View parent;

    // for single;
    private TextView tvTime;
    private TextView tvName;
    private TextView tvComment;
    private TextView tvProgress;
    private TextView tvLast;
    private Button btnContact;

    // for more
    private TextView tvNumber;
    private ListView lvAlarm;

    private ArrayList<Customer> mAlarmCustomers;
    private boolean mIsSliented = false;
    private AlarmAdatper mAdapter;

    private Handler mHandler = new Handler();
    
    private WakeLock mLock;

    // private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	mAlarmCustomers = new ArrayList<Customer>();
	mAdapter = new AlarmAdatper(this, mAlarmCustomers);
	setAlarm();
	super.onCreate(savedInstanceState);
	
	PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
	mLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP|PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"alarm");
    }

    private void setAlarm() {
	Log.i(TAG, "cleared");
	// String cIds = getIntent().getStringExtra(EXTRA_IDS);
	// if (cIds == null) {
	// Log.e(TAG, "ids can't be null");
	// finish();
	// }
	// Log.i(TAG, "id: " + cIds);
	// String[] ids = cIds.split(",");
	GlobalValue global = GlobalValue.getIns();
	ArrayList<Customer> alarms = global.getAlarms();
	for (Customer c : alarms) {
	    c.setIsDisplayed(true);
	    global.putCustomer(c);
	    mAlarmCustomers.add(c);
	    Log.i(TAG, "new id:" + c.getId() + c.getName());
	}
	// if (count > 5) {
	// return;
	// }
	// count++;
	AlarmHelper.startAlarm(this, true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
	super.onNewIntent(intent);
	setAlarm();
	Log.i(TAG, "new intent");
    }

    @Override
    protected int getLayout() {
	if (mAlarmCustomers.size() == 1) {
	    return LAYOUT_SINGLE;
	} else {
	    return LAYOUT_MORE;
	}
    }

    @Override
    protected void onResume() {
	super.onResume();
	if (mLock != null) {
	    mLock.acquire();
	}
	if (!mIsSliented) {
	    MPlayHelper.playSound(this);
	}
	mHandler.removeCallbacks(mSilentThread);
	mHandler.postDelayed(mSilentThread, MPlayHelper.MAXIMIUM_DURATION);
	Log.i(TAG, "resumed, alarm");
	initContent();
    }
    
    @Override
    protected void onDestroy() {
	if (null != mLock) {
	    mLock.release();
	}
        super.onDestroy();
    }

    private void initContent() {
	if (mAlarmCustomers.size() == 1) {
	    Customer c = mAlarmCustomers.get(0);
	    tvTime.setText(DateUtil.getExactTime(c.getAlarmTime()));
	    tvName.setText(c.getName());
	    tvProgress.setText(c.getProgress());
	    tvComment.setText(c.getLastFollowComment());
	    Communication com = CommuHandler.getLastCallOfTel(this, c.getTel());
	    if (com != null) {
		tvLast.setText(DateUtil.getDisplayTime(com.getTime()) + "联系过");
	    }

	} else {
	    tvNumber.setText("您有" + mAlarmCustomers.size() + "条跟进提醒");
	    mAdapter.notifyDataSetChanged();
	}
    }

    private Runnable mSilentThread = new Runnable() {
	@Override
	public void run() {
	    Log.i(TAG, "silented");
	    clearDisplay();
	    MPlayHelper.silentAlarm(AlarmActivity.this);
	}
    };

    @Override
    protected void initElements() {
	btnClose = (Button) findViewById(R.id.btnClose);
	btnView = (Button) findViewById(R.id.btnView);
	btnSlient = (ImageButton) findViewById(R.id.btnSound);
	parent = findViewById(R.id.pop_parent);
	parent.setOnClickListener(this);
	btnSlient.setOnClickListener(this);
	btnClose.setOnClickListener(this);
	btnView.setOnClickListener(this);
	if (mAlarmCustomers.size() == 1) {
	    tvTime = (TextView) findViewById(R.id.tvTime);
	    tvName = (TextView) findViewById(R.id.tvName);
	    tvComment = (TextView) findViewById(R.id.tvComment);
	    tvProgress = (TextView) findViewById(R.id.tvProgress);
	    tvLast = (TextView) findViewById(R.id.tvLast);
	    btnContact = (Button) findViewById(R.id.btnContact);
	    btnContact.setOnClickListener(this);
	} else {
	    tvNumber = (TextView) findViewById(R.id.tvNumber);
	    lvAlarm = (ListView) findViewById(R.id.lvAlarm);
	    lvAlarm.setAdapter(mAdapter);
	}
    }

    @Override
    public void onClick(View v) {
	if (v == btnClose) {
	    finish();
	} else if (v == btnView) {
	    Intent intent;
	    if (mAlarmCustomers.size() == 1) {
		intent = new Intent(this, CustomerDetailActivity.class);
		intent.putExtra(AddCustomerActivity.EXTRA_CUSTOMER_ID,
			mAlarmCustomers.get(0).getId());
	    } else {
		intent = new Intent(this, MainTabHost.class);
		intent.putExtra(BaseTabHost.EXTRA_INDEX_TAG,
			MainTabHost.TAG_FOLLOW);
	    }
	    IntentUtil.startActivity(this, intent);
	    clearStatus();
	    finish();
	} else if (v == btnContact) {
	    IntentUtil.startTel(this, mAlarmCustomers.get(0).getTel());
	    clearStatus();
	    finish();
	} else if (v == btnSlient) {
//	    if (mIsSliented) {
//		return;
//	    }
	    mIsSliented = true;
	    btnSlient.setBackgroundResource(R.drawable.ic_silented);
	    clearDisplay();
	} else if (v == parent) {
	    // for (Customer c : mAlarmCustomers) {
	    // c.setIsDisplayed(false);
	    // GlobalValue.getIns().putCustomer(c);
	    // }
	    // mHandler.post(mSilentThread);
	    // finish();
	}

	mHandler.removeCallbacks(mSilentThread);
	mHandler.post(mSilentThread);
    }

    private void clearDisplay() {
	for (Customer c : mAlarmCustomers) {
	    c.setIsDisplayed(false);
	    GlobalValue.getIns().putCustomer(c);
	}
    }

    private void clearStatus() {
	for (Customer c : mAlarmCustomers) {
	    c.setIsDisplayed(false);
	    c.setHasChecked(true);
	    GlobalValue.getIns().getCustomerHandler(this).updateCustomer(c);
	    GlobalValue.getIns().putCustomer(c);

	    Action action =
		    new Action(c.getId(), ActionHandler.TYPE_FINISH_ALARM);
	    action.setContent(DateUtil.yyyyMMddHHmm.format(c.getAlarmTime()));
	    GlobalValue.getIns().getActionHandler(AlarmActivity.this)
		    .handleAction(action);
	}
	mAlarmCustomers.clear();
    }

    private class AlarmAdatper extends BaseAdapter {
	private Context mContext;
	private ArrayList<Customer> cList;

	public AlarmAdatper(Context context, ArrayList<Customer> cList) {
	    mContext = context;
	    this.cList = cList;
	}

	@Override
	public int getCount() {
	    return cList.size();
	}

	@Override
	public Customer getItem(int position) {
	    return cList.get(position);
	}

	@Override
	public long getItemId(int position) {
	    return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if (convertView == null) {
		convertView =
			LayoutInflater.from(mContext).inflate(
				R.layout.list_item_alarm_popup, null);
	    }
	    TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
	    TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);

	    Customer c = getItem(position);
	    tvName.setText(c.getName());
	    Calendar calc = Calendar.getInstance();
	    calc.setTimeInMillis(c.getAlarmTime());
	    tvTime.setText(DateUtil.yyyyMMddHHmm.format(calc.getTime()));
	    return convertView;
	}

    }

}
