package com.rong360.creditassitant.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.util.DateUtil;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.RongStats;
import com.rong360.creditassitant.widget.TitleBarCenter;
import com.umeng.analytics.MobclickAgent;

public class TaskFragment extends BaseFragment {
    private ArrayList<Customer> mAlarmCustomers;
    private AlarmAdapter mAdapter;

    private ListView lvAlarm;
    private TextView tvHint;

    private Calendar mToday;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	TitleBarCenter center = getSupportActionBarCenter(Boolean.FALSE);
	center.hideLeft();
	center.setTitle("提醒");
	mAlarmCustomers = new ArrayList<Customer>();
	mAdapter = new AlarmAdapter(mContext, mAlarmCustomers);
	
	MobclickAgent.onEvent(mContext, RongStats.TASK);
    }

    @Override
    protected void initElement() {
	lvAlarm = (ListView) findViewById(R.id.lvAlarm);
	tvHint = (TextView) findViewById(R.id.tvHint);
	lvAlarm.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
	super.onResume();

	mToday = Calendar.getInstance();
	mToday.set(Calendar.HOUR_OF_DAY, 0);
	mToday.set(Calendar.MINUTE, 0);
	mToday.set(Calendar.MILLISECOND, 0);
	initContent();
    }

    private void initContent() {
	mAlarmCustomers.clear();
	ArrayList<Customer> allCustomers =
		GlobalValue.getIns().getAllCustomers();

	for (Customer c : allCustomers) {
	    if (c.getAlarmTime() > mToday.getTimeInMillis()) {
		mAlarmCustomers.add(c);
	    }
	}

	if (mAlarmCustomers.size() > 0) {
	    Collections.sort(mAlarmCustomers, new Comparator<Customer>() {

		@Override
		public int compare(Customer lhs, Customer rhs) {
		    return rhs.getAlarmTime() - lhs.getAlarmTime() < 0 ? 1 : -1;
		}
	    });
	    lvAlarm.setVisibility(View.VISIBLE);
	    mAdapter.notifyDataSetChanged();
	    tvHint.setVisibility(View.INVISIBLE);
	} else {
	    lvAlarm.setVisibility(View.INVISIBLE);
	    tvHint.setVisibility(View.VISIBLE);
	}
    }

    @Override
    protected int getLayout() {
	return R.layout.fragment_task;
    }

    private class AlarmAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Customer> mCustomers;
	private String[] mProgress;
	
	private ColorStateList mGrey;
	private ColorStateList mBlack;
	private ColorStateList mLabel;

	public AlarmAdapter(Context context, ArrayList<Customer> customers) {
	    mContext = context;
	    mCustomers = customers;
	    
	    mProgress = mContext.getResources().getStringArray(R.array.progress);
	    mGrey = mContext.getResources().getColorStateList(R.color.text_grey);
	    mBlack = mContext.getResources().getColorStateList(R.color.text_black);
	    mLabel = mContext.getResources().getColorStateList(R.color.text_label);
	}

	@Override
	public int getCount() {
	    return mCustomers.size();
	}

	@Override
	public Customer getItem(int position) {
	    return mCustomers.get(position);
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
				R.layout.list_item_alarm, null);
	    }

	    TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
	    TextView tvComment =
		    (TextView) convertView.findViewById(R.id.tvComment);
	    TextView tvProgress =
		    (TextView) convertView.findViewById(R.id.tvProgress);
	    TextView tvDay = (TextView) convertView.findViewById(R.id.tvDay);
	    TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
	    final Customer c = getItem(position);
	    tvName.setText(c.getName());
	    String comment = c.getLastFollowComment();
	    if (comment != null && comment.length() > 0) {
		tvComment.setText(c.getLastFollowComment());
	    } else {
		tvComment.setText("暂无备注");
	    }
	    String progress = c.getProgress();
	    if (progress != null) {
		tvProgress.setText(c.getProgress());
		for (int i = 0; i < mProgress.length; i++) {
		    if (mProgress[i].equalsIgnoreCase(progress)) {
			tvProgress.setTextColor(getResources().getColorStateList(CustomerManagementFragment.progressColor[i]));
			break;
		    }
		}
	    }
	    tvDay.setText(DateUtil.getDisplayTimeForTask(c.getAlarmTime()));
	    tvTime.setText(DateUtil.getExactTime(c.getAlarmTime()));
	    convertView.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		    Intent intent =
			    new Intent(mContext, CustomerDetailActivity.class);
		    intent.putExtra(AddCustomerActivity.EXTRA_CUSTOMER_ID,
			    c.getId());
		    startActivity(intent);
		}
	    });
	    
	    if (c.getAlarmTime() < System.currentTimeMillis()) {
		tvName.setTextColor(mGrey);
		tvComment.setTextColor(mGrey);
		tvDay.setTextColor(mGrey);
		tvTime.setTextColor(mGrey);
		tvProgress.setTextColor(mGrey);
	    } else {
		tvName.setTextColor(mBlack);
		tvTime.setTextColor(mBlack);
		
		tvDay.setTextColor(mLabel);
		tvComment.setTextColor(mLabel);
	    }

	    return convertView;
	}
    }

}
