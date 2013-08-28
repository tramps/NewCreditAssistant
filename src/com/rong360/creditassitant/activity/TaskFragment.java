package com.rong360.creditassitant.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
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
import com.rong360.creditassitant.model.GlobalValue;
import com.rong360.creditassitant.util.DateUtil;
import com.rong360.creditassitant.widget.TitleBarCenter;

public class TaskFragment extends BaseFragment {
    private ArrayList<Customer> mAlarmCustomers;
    private AlarmAdapter mAdapter;
    
    private ListView lvAlarm;
    private TextView tvHint;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	TitleBarCenter center = getSupportActionBarCenter(Boolean.FALSE);
	center.hideLeft();
	center.setTitle("提醒");
	mAlarmCustomers = new ArrayList<Customer>();
	mAdapter = new AlarmAdapter(mContext, mAlarmCustomers);
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
        initContent();
    }

    private void initContent() {
	mAlarmCustomers.clear();
	ArrayList<Customer> allCustomers = GlobalValue.getIns().getAllCustomers();
	
	for (Customer c : allCustomers) {
	    if (c.getAlarmTime() != 0) {
		mAlarmCustomers.add(c);
	    }
	}
	
	if (mAlarmCustomers.size() > 0) {
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
	
	public AlarmAdapter(Context context, ArrayList<Customer> customers) {
	    mContext = context;
	    mCustomers = customers;
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
		convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_alarm, null);
	    }
	    
	    TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
	    TextView tvComment = (TextView) convertView.findViewById(R.id.tvComment);
	    TextView tvProgress = (TextView) convertView.findViewById(R.id.tvProgress);
	    TextView tvDay = (TextView) convertView.findViewById(R.id.tvDay);
	    TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
	    final Customer c = getItem(position);
	    tvName.setText(c.getName());
	    tvComment.setText(c.getLastFollowComment());
	    tvProgress.setText(c.getProgress());
	    tvDay.setText(DateUtil.getDisplayTime(c.getAlarmTime()));
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
	    
	    return convertView;
	}
    }

}
