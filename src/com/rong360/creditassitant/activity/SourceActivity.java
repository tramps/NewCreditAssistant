package com.rong360.creditassitant.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.model.CustomerHandler;
import com.rong360.creditassitant.util.ConfirmHelper;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.util.PreferenceHelper;
import com.rong360.creditassitant.util.ConfirmHelper.OnRefresh;

public class SourceActivity extends BaseActionBar {
    public static final String TAG = "SourceActivity";
    private ArrayList<String> mSources;
    private TextView tvHint;
    private ListView lvSource;
    private SourceAdapter mAdapter;
    private PreferenceHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	mSources = new ArrayList<String>();
	mAdapter = new SourceAdapter(this, mSources);
	super.onCreate(savedInstanceState);
	getSupportActionBar(false).setTitle("客户来源");
	mHelper = PreferenceHelper.getHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	menu.add(0, R.id.finish, 0, "添加");
	return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	if (item.getItemId() == R.id.finish) {
	    Intent intent = new Intent(this, AddSourceActivity.class);
	    IntentUtil.startActivity(this, intent);
	}
	return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initElements() {
	tvHint = (TextView) findViewById(R.id.tvHint);
	lvSource = (ListView) findViewById(R.id.lvSource);
	lvSource.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
	super.onResume();
	initContent();
    }

    private void initContent() {
	String sources =
		mHelper.readPreference(AddSourceActivity.PRE_KEY_SOURCES);
	mSources.clear();
	if (sources == null || sources.length() == 0) {
	    tvHint.setVisibility(View.VISIBLE);
	    lvSource.setVisibility(View.GONE);
	} else {
	    String[] cSource = sources.split(";");
	    for (String s : cSource) {
		mSources.add(s);
	    }
	    lvSource.setVisibility(View.VISIBLE);
	    tvHint.setVisibility(View.INVISIBLE);
	    mAdapter.notifyDataSetChanged();
	}
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_source;
    }

    private class SourceAdapter extends BaseAdapter {

	private ArrayList<String> mSrs;
	private Context mContext;

	public SourceAdapter(Context context, ArrayList<String> sources) {
	    mContext = context;
	    mSrs = sources;
	}

	@Override
	public int getCount() {
	    return mSrs.size();
	}

	@Override
	public String getItem(int position) {
	    return mSrs.get(position);
	}

	@Override
	public long getItemId(int position) {
	    return 0;
	}

	@Override
	public View getView(final int position, View convertView,
		ViewGroup parent) {
	    if (convertView == null) {
		convertView =
			LayoutInflater.from(mContext).inflate(
				R.layout.list_item_source, null);
	    }

	    TextView tvSource =
		    (TextView) convertView.findViewById(R.id.tvSource);
	    Button btnClose = (Button) convertView.findViewById(R.id.btnClose);
	    final String title = getItem(position);
	    btnClose.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		    ConfirmHelper.showFollowDialog(mContext, title, position, mOnDelete);
		}
	    });
	    tvSource.setText(title);
	    return convertView;
	}

    }

    protected void removeCustomerSource(int position) {
	String removed = mSources.remove(position);
	Log.i(TAG, "removed:" + removed);
	StringBuilder sb = new StringBuilder();
	for (String s : mSources) {
	    sb.append(s);
	    sb.append(";");
	}
	mHelper.writePreference(AddSourceActivity.PRE_KEY_SOURCES,
		sb.toString());
	CustomerHandler handler =
		GlobalValue.getIns().getCustomerHandler(SourceActivity.this);
	handler.removeSourceByName(removed);
	ArrayList<Customer> customers = GlobalValue.getIns().getAllCustomers();
	for (Customer c : customers) {
	    if (c.getSource() != null
		    && c.getSource().equalsIgnoreCase(removed)) {
		c.setSource("");
		GlobalValue.getIns().putCustomer(c);
	    }
	}
	initContent();
    }

    private OnRefresh mOnDelete = new OnRefresh() {

	@Override
	public void onRefresh(int pos) {
	    removeCustomerSource(pos);
	}
    };
}
