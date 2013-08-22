package com.rong360.creditassitant.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.model.GlobalValue;
import com.rong360.creditassitant.util.DisplayUtils;
import com.rong360.creditassitant.widget.HorizontalListView;
import com.rong360.creditassitant.widget.MovingBarView;

public class CustomerDetailActivity extends BaseActionBar {
    private static final String TAG = "CustomerDetailActivity";
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
	    return true;
	}
	return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
	super.onResume();
	initContent();
    }

    private void initContent() {
	CustomerState state = new CustomerState(this, mState);
	hlv.setAdapter(state);
	mbv.setHorizontalListview(hlv);
	hlv.setMovingBar(mbv);

	tvName.setText(mCustomer.getName());
	tvTel.setText(mCustomer.getTel());
	tvLoan.setText(mCustomer.getLoan() + "万");
	tvSource.setText(mCustomer.getSource());
    }

    @Override
    protected void initElements() {
	hlv = (HorizontalListView) findViewById(R.id.hlv);
	mbv = (MovingBarView) findViewById(R.id.movingBar);

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
	
	llQuality = (LinearLayout) findViewById(R.id.llQuality);
	llDetail = (LinearLayout) findViewById(R.id.llDetail);
	llComHistory = (LinearLayout) findViewById(R.id.llComHistory);
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_customer_detail;
    }

    private class CustomerState extends BaseAdapter {
	private Context mContext;
	private String[] mStates;

	public CustomerState(Context context, String[] states) {
	    mContext = context;
	    mStates = states;
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
//		LayoutParams params = convertView.getLayoutParams();
//		Log.i(TAG, "width: " + DisplayUtils.getScreenWidth(mContext)
//			+ " height: " + DisplayUtils.getScreenHeight(mContext));
//		Log.i(TAG,
//			"width: " + hlv.getWidth() + " height: "
//				+ hlv.getHeight());
//		if (params == null) {
//		    params =
//			    new LayoutParams(LayoutParams.WRAP_CONTENT,
//				    LayoutParams.MATCH_PARENT);
//		}
////		params.width = DisplayUtils.getScreenWidth(mContext) / 3 - 15;
//		convertView.setLayoutParams(params);
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
