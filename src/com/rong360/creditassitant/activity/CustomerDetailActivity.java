package com.rong360.creditassitant.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.Action;
import com.rong360.creditassitant.model.ActionHandler;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.model.CustomerAction;
import com.rong360.creditassitant.model.CustomerHandler;
import com.rong360.creditassitant.model.GlobalValue;
import com.rong360.creditassitant.util.DateUtil;
import com.rong360.creditassitant.util.DialogUtil;
import com.rong360.creditassitant.util.DialogUtil.ITimePicker;
import com.rong360.creditassitant.util.DisplayUtils;
import com.rong360.creditassitant.widget.HorizontalListView;
import com.rong360.creditassitant.widget.MovingBarView;
import com.rong360.creditassitant.widget.MovingBarView.IProgressChanged;

public class CustomerDetailActivity extends BaseActionBar implements
	OnClickListener {
    private static final String TAG = "CustomerDetailActivity";
    private static final int REQUEST_CODE = 10001;
    private int mCustomerId;
    private String[] mState;
    private Customer mCustomer;

    private TextView tvName;
    private TextView tvTel;
    private TextView tvLoan;
    private TextView tvSource;

    private LinearLayout llTel;
    private LinearLayout llMsg;
    private LinearLayout llComHistory;
    private LinearLayout llStar;
    private ImageView ivStar;

    private HorizontalListView hlv;
    private MovingBarView mbv;

    private RelativeLayout rlAlarm;
    private RelativeLayout rlComment;
    private TextView tvAlarm;
    private TextView tvComment;

    private LinearLayout llQuality;
    private LinearLayout llDetail;
    private LinearLayout llHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getSupportActionBar(false).setTitle("客户详情");
	mState = getResources().getStringArray(R.array.progress);
	mCustomerId =
		getIntent().getIntExtra(AddCustomerActivity.EXTRA_CUSTOMER_ID,
			-1);
	mCustomer = GlobalValue.getIns().getCusmer(mCustomerId);

	if (mCustomer == null) {
	    finish();
	    Log.e(TAG, "customer can't be null");
	}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	menu.add(0, R.id.edit, 0, "编辑");
	return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	if (item.getItemId() == R.id.edit) {
	    Intent intent = new Intent(this, AddCustomerActivity.class);
	    intent.putExtra(AddCustomerActivity.EXTRA_CUSTOMER_ID, mCustomerId);
	    startActivity(intent);
	    // finish();
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
	super.onResume();
	initContent();
    }

    private IProgressChanged mOnProgressChange = new IProgressChanged() {

	@Override
	public void onProgressChanged(int index) {
	    if (index >= 0 && index < mState.length) {
		mCustomer.setProgress(mState[index]);
		Log.i(TAG, "progress changed: " + mCustomer.getName()
			+ mCustomer.getProgress());
		CustomerHandler handler =
			GlobalValue.getIns().getCustomerHandler(
				CustomerDetailActivity.this);
		handler.updateCustomer(mCustomer);
		GlobalValue.getIns().putCustomer(mCustomer);
		Action a = new Action(mCustomerId, ActionHandler.TYPE_PROGRESS);
		a.setContent(mCustomer.getProgress());
		GlobalValue.getIns()
			.getActionHandler(CustomerDetailActivity.this)
			.handleAction(a);
	    } else {
		Log.e(TAG, "wrong index" + index);
	    }
	}
    };

    private void initContent() {
	CustomerState state = new CustomerState(this, mState);
	hlv.setAdapter(state);
	mbv.setHorizontalListview(hlv);
	hlv.setMovingBar(mbv);
	String progress = mCustomer.getProgress();
	if (progress != null && progress.length() > 0) {
	    for (int i = 0; i < mState.length; i++) {
		if (mState[i].equalsIgnoreCase(progress)) {
		    mbv.setIndex(i);
		    break;
		}
	    }
	}

	tvName.setText(mCustomer.getName());
	tvTel.setText(mCustomer.getTel());
	tvLoan.setText(mCustomer.getLoan() + "万");
	tvSource.setText(mCustomer.getSource());

	if (mCustomer.getAlarmTime() != -1) {
	    Calendar instance = Calendar.getInstance();
	    instance.setTimeInMillis(mCustomer.getAlarmTime());
	    tvAlarm.setText(DateUtil.yyyyMMddHHmm.format(instance.getTime()));
	} else {
	    tvAlarm.setText("点击设置提醒");
	}

	tvComment.setText(mCustomer.getLastFollowComment());

	initDetail();

	initAction();
    }

    private void initAction() {
	llHistory.removeAllViewsInLayout();
	ArrayList<Action> actions =
		GlobalValue.getIns().getActionHandler(this)
			.getAllActionById(mCustomerId);
	Log.i(TAG, "action size:" + actions.size());
	ActionAdapter adapter = new ActionAdapter(this, actions);
	for (int i = 0; i < adapter.getCount(); i++) {
	    llHistory.addView(adapter.getView(i, null, null));
	}
    }

    private void initDetail() {
	HashMap<String, String> detailMap = new HashMap<String, String>();
	String[] items = null;
	Resources res = getResources();
	if (mCustomer.getBank() >= 0) {
	    items = res.getStringArray(R.array.bankCash);
	    detailMap.put("银行流水(元)", items[mCustomer.getBank()]);
	}
	if (mCustomer.getCash() >= 0) {
	    if (items == null) {
		items = res.getStringArray(R.array.bankCash);
	    }
	    detailMap.put("现金流水", items[mCustomer.getCash()]);
	}
	if (mCustomer.getIdentity() >= 0) {
	    items = res.getStringArray(R.array.identity);
	    detailMap.put("身份", items[mCustomer.getIdentity()]);
	}
	if (mCustomer.getCreditRecord() >= 0) {
	    items = res.getStringArray(R.array.credit);
	    detailMap.put("信用记录", items[mCustomer.getCreditRecord()]);
	}
	if (mCustomer.getHouse() >= 0) {
	    items = res.getStringArray(R.array.house);
	    detailMap.put("房产", items[mCustomer.getHouse()]);
	}
	if (mCustomer.getCar() >= 0) {
	    items = res.getStringArray(R.array.car);
	    detailMap.put("车辆", items[mCustomer.getCar()]);
	}

	llDetail.removeAllViewsInLayout();
	if (detailMap.size() > 0) {
	    DetailAdapter adapter = new DetailAdapter(this, detailMap);
	    for (int i = 0; i < adapter.getCount(); i++) {
		llDetail.addView(adapter.getView(i, null, null));
	    }
	    llQuality.setVisibility(View.VISIBLE);
	} else {
	    llQuality.setVisibility(View.GONE);
	}

    }

    @Override
    protected void initElements() {
	hlv = (HorizontalListView) findViewById(R.id.hlv);
	mbv = (MovingBarView) findViewById(R.id.movingBar);
	mbv.setOnProgressChangedListener(mOnProgressChange);

	tvName = (TextView) findViewById(R.id.tvName);
	tvTel = (TextView) findViewById(R.id.tvTel);
	tvLoan = (TextView) findViewById(R.id.tvLoan);
	tvSource = (TextView) findViewById(R.id.tvSource);

	llTel = (LinearLayout) findViewById(R.id.llTel);
	llMsg = (LinearLayout) findViewById(R.id.llMsg);
	llComHistory = (LinearLayout) findViewById(R.id.llComHistory);
	llStar = (LinearLayout) findViewById(R.id.llStar);
	ivStar = (ImageView) findViewById(R.id.ivStar);

	rlAlarm = (RelativeLayout) findViewById(R.id.rlAlarm);
	rlComment = (RelativeLayout) findViewById(R.id.rlComment);
	tvAlarm = (TextView) findViewById(R.id.tvCAlarm);
	tvComment = (TextView) findViewById(R.id.tvCCommment);

	rlAlarm.setOnClickListener(this);
	rlComment.setOnClickListener(this);

	llQuality = (LinearLayout) findViewById(R.id.llQuality);
	llDetail = (LinearLayout) findViewById(R.id.llDetail);
	llHistory = (LinearLayout) findViewById(R.id.llHistory);
    }

    @Override
    public void onClick(View v) {
	if (v == rlAlarm) {
	    DialogUtil.showTimePicker(this, mTimePickListener);
	} else if (v == rlComment) {
	    Intent intent = new Intent(this, CommentActivity.class);
	    intent.putExtra(CommentActivity.EXTRA_ID, mCustomerId);
	    if (mCustomer.getLastFollowComment() != null
		    && mCustomer.getLastFollowComment().length() > 0) {
		intent.putExtra(CommentActivity.EXTRA_COMMENT,
			mCustomer.getLastFollowComment());
	    }
	    startActivityForResult(intent, REQUEST_CODE);
	}
    }

    @Override
    protected void
	    onActivityResult(int requestCode, int resultCode, Intent data) {
	if (resultCode == RESULT_OK) {
	    String comment =
		    data.getStringExtra(CommentActivity.RESULT_COMMENT);
	    if (comment.length() > 0
		    && !comment.equalsIgnoreCase(mCustomer
			    .getLastFollowComment())) {
		mCustomer.setLastFollowComment(comment);
		Action action =
			new Action(mCustomerId, ActionHandler.TYPE_COMMENT);
		action.setContent(mCustomer.getLastFollowComment());
		GlobalValue.getIns().getActionHandler(this)
			.handleAction(action);
		tvComment.setText(comment);
	    }
	}
    }

    private ITimePicker mTimePickListener = new ITimePicker() {

	@Override
	public void onTimePicked(String time, Calendar alarm) {
	    tvAlarm.setText(time);
	    mCustomer.setAlarmTime(alarm.getTimeInMillis());
	    GlobalValue.getIns().putCustomer(mCustomer);
	    GlobalValue.getIns().getCustomerHandler(getBaseContext())
		    .updateCustomer(mCustomer);

	    Action action =
		    new Action(mCustomer.getId(), ActionHandler.TYPE_SET_ALARM);
	    action.setContent(DateUtil.yyyyMMddHHmm.format(alarm.getTime()));
	    GlobalValue.getIns().getActionHandler(CustomerDetailActivity.this)
		    .handleAction(action);
	}

    };

    @Override
    protected int getLayout() {
	return R.layout.activity_customer_detail;
    }

    private class DetailAdapter extends BaseAdapter {
	private Context mContext;
	private HashMap<String, String> mDetailMap;

	public DetailAdapter(Context context, HashMap<String, String> detailMap) {
	    mContext = context;
	    mDetailMap = detailMap;
	}

	@Override
	public int getCount() {
	    return mDetailMap.size();
	}

	@Override
	public String getItem(int position) {
	    int i = 0;
	    for (String key : mDetailMap.keySet()) {
		if (i == position) {
		    return key;
		}

		i++;
	    }

	    return "out if range";
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
				R.layout.list_item_detail, null);
	    }

	    TextView tvTitle =
		    (TextView) convertView.findViewById(R.id.tvTitle);
	    TextView tvContent =
		    (TextView) convertView.findViewById(R.id.tvContent);

	    String title = getItem(position);
	    tvTitle.setText(title + ": ");
	    tvContent.setText(mDetailMap.get(title));
	    return convertView;
	}

    }

    private class ActionAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Action> mActions;

	public ActionAdapter(Context context, ArrayList<Action> actions) {
	    mContext = context;
	    mActions = actions;
	}

	@Override
	public int getCount() {
	    return mActions.size();
	}

	@Override
	public Action getItem(int position) {
	    return mActions.get(position);
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
				R.layout.list_item_action, null);
	    }

	    TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
	    TextView tvContent =
		    (TextView) convertView.findViewById(R.id.tvContent);

	    Action a = getItem(position);
	    // Calendar calc = Calendar.getInstance();
	    // calc.setTimeInMillis(a.getTime());
	    tvTime.setText(DateUtil.getDisplayTimeForDetail(a.getTime()));
	    tvContent.setText(a.getContent());

	    return convertView;
	}

    }

    private class CustomerState extends BaseAdapter {
	private Context mContext;
	private String[] mStates;

	public CustomerState(Context context, String[] states) {
	    mContext = context;
	    mStates = new String[states.length + 2];
	    mStates[0] = "";
	    mStates[states.length - 1] = "";
	    for (int i = 0; i < states.length; i++) {
		mStates[i + 1] = states[i];
	    }
	}

	@Override
	public int getCount() {
	    return mStates.length;
	}

	@Override
	public String getItem(int position) {
	    return mStates[position];
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
				R.layout.list_item_state, null);
		// LayoutParams params = convertView.getLayoutParams();
		// Log.i(TAG, "width: " + DisplayUtils.getScreenWidth(mContext)
		// + " height: " + DisplayUtils.getScreenHeight(mContext));
		// Log.i(TAG,
		// "width: " + hlv.getWidth() + " height: "
		// + hlv.getHeight());
		// if (params == null) {
		// params =
		// new LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.MATCH_PARENT);
		// }
		// // params.width = DisplayUtils.getScreenWidth(mContext) / 3 -
		// 15;
		// convertView.setLayoutParams(params);
	    }

	    TextView tvState =
		    (TextView) convertView.findViewById(R.id.tv_state);
	    String state = getItem(position);
	    LayoutParams p = tvState.getLayoutParams();
	    p.width = DisplayUtils.getScreenWidth(mContext) / 3 - 10;
	    tvState.setText(state);

	    return convertView;
	}

    }

}
