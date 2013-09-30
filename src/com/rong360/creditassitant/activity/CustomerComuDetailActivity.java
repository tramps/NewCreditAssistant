package com.rong360.creditassitant.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.CommuHandler;
import com.rong360.creditassitant.model.Communication;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.util.DateUtil;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.IntentUtil;

public class CustomerComuDetailActivity extends BaseActionBar implements
	OnClickListener {
    private static final String TAG = "CustomerComuDetailActivity";
    public static final String EXTRA_TEL = "extra_tel";
    public static final String EXTRA_MODE = "extra_shall_show";

    private int mCustomerId;
    private Customer mCustomer;
    private String mTel;

    private TextView tvName;
    private TextView tvTel;
    private LinearLayout llTel;
    private LinearLayout llMsg;
    private LinearLayout llCustomer;
    private ImageView ivCustomer;
    private TextView tvCusomter;
    private TableLayout tlAction;

    private ListView lvHistory;
    private TextView tvHint;

    private CommunicationAdapter mAdapter;
    private List<Communication> mHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	mCustomerId =
		getIntent().getIntExtra(AddCustomerActivity.EXTRA_CUSTOMER_ID,
			-1);
	mCustomer = GlobalValue.getIns().getCusmer(mCustomerId);

	if (mCustomer == null) {
	    mTel = getIntent().getStringExtra(EXTRA_TEL);
	} else {
	    mTel = mCustomer.getTel();
	}
	mHistory = new ArrayList<Communication>();

	if (mTel != null && mTel.length() > 0) {
	    mHistory.addAll(CommuHandler.getCallLogByTel(this, mTel));
	    Log.i(TAG, "tel:" + mHistory.size());
	    mHistory.addAll(CommuHandler.getAllSmsByTel(this, mTel));
	    Log.i(TAG, "all:" + mHistory.size());
	}
	Log.i(TAG, "all:" + mHistory.size());

	Collections.sort(mHistory, new Comparator<Communication>() {
	    @Override
	    public int compare(Communication lhs, Communication rhs) {
		return (rhs.getTime() - lhs.getTime() >= 0 ? 1 : -1);
	    }
	});
	mAdapter = new CommunicationAdapter(this, mHistory);

	super.onCreate(savedInstanceState);
	getSupportActionBar(false).setTitle("通讯详情");
    }

    private void initContent() {
	if (mCustomer == null) {
	    tvName.setText(mTel);
	    tvTel.setVisibility(View.GONE);
	    tvCusomter.setText("添加客户");
	} else {
	    tvName.setText(mCustomer.getName());
	    tvTel.setText(mCustomer.getTel());
	    tvCusomter.setText("查看客户");
	}

	if (mHistory.size() > 0) {
	    lvHistory.setVisibility(View.VISIBLE);
	    tvHint.setVisibility(View.GONE);
	    mAdapter.notifyDataSetChanged();
	} else {
	    lvHistory.setVisibility(View.GONE);
	    tvHint.setVisibility(View.VISIBLE);
	}

	boolean shallShowTable = getIntent().getBooleanExtra(EXTRA_MODE, true);
	if (!shallShowTable) {
	    tlAction.setVisibility(View.GONE);
	}

    }

    @Override
    protected void onResume() {
	super.onResume();
	if (GlobalValue.getIns().getCusmer(mCustomerId) == null
		&& getIntent().getStringExtra(EXTRA_TEL) == null) {
	    finish();
	    return;
	}
	initContent();
    }

    @Override
    protected void initElements() {
	tvName = (TextView) findViewById(R.id.tvName);
	tvTel = (TextView) findViewById(R.id.tvTel);
	llTel = (LinearLayout) findViewById(R.id.llTel);
	llMsg = (LinearLayout) findViewById(R.id.llMsg);
	llCustomer = (LinearLayout) findViewById(R.id.llCustomer);
	ivCustomer = (ImageView) findViewById(R.id.ivCustomer);
	tvCusomter = (TextView) findViewById(R.id.tvCustomer);
	lvHistory = (ListView) findViewById(android.R.id.list);
	tvHint = (TextView) findViewById(R.id.tvHint);
	llCustomer.setOnClickListener(this);
	llTel.setOnClickListener(this);
	llMsg.setOnClickListener(this);
	lvHistory.setAdapter(mAdapter);

	tlAction = (TableLayout) findViewById(R.id.tlAction);
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_customer_comu_detail;
    }

    @Override
    public void onClick(View v) {
	if (v == llCustomer) {
	    Intent intent;
	    if (mCustomer == null) {
		Log.i(TAG, "tel:" + mTel);
		intent = new Intent(this, AddCustomerActivity.class);
		intent.putExtra(AddCustomerActivity.EXTRA_TEL, mTel);
		finish();
	    } else {
		intent = new Intent(this, CustomerDetailActivity.class);
		intent.putExtra(AddCustomerActivity.EXTRA_CUSTOMER_ID,
			mCustomerId);
	    }
	    IntentUtil.startActivity(this, intent);
	} else if (v == llTel) {
	    Log.i(TAG, "tel:" + mTel);
	    IntentUtil.startTel(this, mTel);
	} else if (v == llMsg) {
	    String customerInfo;
	    if (mCustomer == null) {
		customerInfo = mTel + "#" + mTel;
	    } else {
		customerInfo = mCustomer.getName() + "#" + mCustomer.getTel();
	    }
	    Log.i(TAG, "customerinfo: " + customerInfo);
	    Intent intent = new Intent(this, SendGroupSmsActivity.class);
	    intent.putExtra(SendGroupSmsActivity.EXTRA_CUSTOMER, customerInfo);
	    IntentUtil.startActivity(this, intent);
	}
    }

    private class CommunicationAdapter extends ArrayAdapter<Communication> {
	private static final int TYPE_SMS = 0;
	private static final int TYPE_CALL = 1;
	private static final int TOTAL_TYPE_COUNT = 2;

	private List<Communication> mHistory;
	private Context mContext;

	public CommunicationAdapter(Context context, List<Communication> comms) {
	    super(context, 0, comms);
	    mContext = context;
	    mHistory = comms;
	}

	@Override
	public int getCount() {
	    return mHistory.size();
	}

	@Override
	public Communication getItem(int position) {
	    return mHistory.get(position);
	}

	@Override
	public int getViewTypeCount() {
	    return TOTAL_TYPE_COUNT;
	}

	@Override
	public int getItemViewType(int position) {
	    Communication com = getItem(position);
	    if (com.getContent() != null) {
		return TYPE_SMS;
	    } else {
		return TYPE_CALL;
	    }
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if (getItemViewType(position) == TYPE_CALL) {
		return initCall(position, convertView);
	    } else {
		return initSms(position, convertView);
	    }
	}

	private View initSms(int position, View convertView) {
	    if (convertView == null) {
		convertView =
			LayoutInflater.from(mContext).inflate(
				R.layout.list_item_sms, null);
	    }

	    ImageView ivType =
		    (ImageView) convertView.findViewById(R.id.iv_type);
	    TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
	    TextView tvContent =
		    (TextView) convertView.findViewById(R.id.tv_content);
//	    LinearLayout llMsg =
//		    (LinearLayout) convertView.findViewById(R.id.ll_msg);
	    final Communication c = getItem(position);

//	    llMsg.setOnClickListener(new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//		    IntentUtil.sendMessage(mContext, c.getTel(), "",
//			    c.getThreadId());
//		}
//	    });
	    if (c.getType() == Communication.TYPE_INCOMING) {
		ivType.setBackgroundResource(R.drawable.ic_sms_in);
	    } else {
		ivType.setBackgroundResource(R.drawable.ic_sms_out);
	    }

	    tvTime.setText(DateUtil.getDisplayTimeForDetail(c.getTime()));
	    tvContent.setText(c.getContent());

	    return convertView;
	}

	private View initCall(int position, View convertView) {
	    if (convertView == null) {
		convertView =
			LayoutInflater.from(mContext).inflate(
				R.layout.list_item_call, null);
	    }

	    ImageView ivType =
		    (ImageView) convertView.findViewById(R.id.iv_type);
	    TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
	    TextView tvDuration =
		    (TextView) convertView.findViewById(R.id.tv_duration);
	    LinearLayout llTel =
		    (LinearLayout) convertView.findViewById(R.id.ll_tel);
	    final Communication c = getItem(position);
//	    llTel.setOnClickListener(new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//		    IntentUtil.startTel(mContext, c.getTel());
//		}
//	    });

	    if (c.getType() == Communication.TYPE_INCOMING) {
		ivType.setBackgroundResource(R.drawable.ic_call_in);
	    } else if (c.getType() == Communication.TYPE_OUTCOMING) {
		ivType.setBackgroundResource(R.drawable.ic_call_out);
	    } else {
		ivType.setBackgroundResource(R.drawable.ic_call_missed);
	    }

	    tvTime.setText(DateUtil.getDisplayTimeForDetail(c.getTime()));
	    tvDuration.setText(DateUtil.getDisplayForDuration(c.getDuration()));

	    return convertView;
	}

    }

}
