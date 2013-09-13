package com.rong360.creditassitant.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.CommuHandler;
import com.rong360.creditassitant.model.Communication;
import com.rong360.creditassitant.model.LocationHelper;
import com.rong360.creditassitant.util.DateUtil;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.IntentUtil;
import com.rong360.creditassitant.widget.TitleBarCenter;

public class ComunicationHistoryFragment extends BaseFragment {
    private static final String TAG = ComunicationHistoryFragment.class
	    .getSimpleName();

    private ListView mList;
    private TextView tvEmpty;

    private ArrayList<Communication> mHistory;
    private CommunicationAdapter mAdapter;

    public static final String PRE_KEK_LAST_POSITION = "pre_key_last_position";

    @Override
    public void onCreate(Bundle savedInstanceState) {
	setHasOptionsMenu(true);
	setReuseOldViewEnable(true);
	super.onCreate(savedInstanceState);

	TitleBarCenter center = getSupportActionBarCenter(Boolean.FALSE);
	center.hideLeft();
	center.setTitle("通讯历史");
	mHistory = new ArrayList<Communication>();
	mAdapter = new CommunicationAdapter(mContext, mHistory);
    }

    @Override
    public void onResume() {
	super.onResume();
	ArrayList<Communication> res;
	if (GlobalValue.getIns().isNeedUpdateCommunication()) {
	    Log.i(TAG, "reloaded");
	    res = CommuHandler.getAllCallLog(mContext);
	    ArrayList<Communication> coms = GlobalValue.getIns().getAllComunication(mContext);
	    coms.clear();
	    coms.addAll(res);
	} else {
	    Log.i(TAG, "cached");
	    res = GlobalValue.getIns().getAllComunication(mContext);
	}
	// LocationHelper.setAllMobileLoc(mContext, res);
	mHistory.clear();
	mHistory.addAll(res);
	initContent();
    }

    @Override
    public void onPause() {
	super.onPause();
	// savePosition();
    }

    // private void savePosition() {
    // if (mList.getChildCount() > 0) {
    // int position = mList.getFirstVisiblePosition();
    // int top = mList.getChildAt(0).getTop();
    // Log.i(TAG, position + " " + top);
    // PreferenceHelper.getHelper(mContext).writePreference(
    // PRE_KEK_LAST_POSITION, position + "-" + top);
    // }
    // }

    // private void restorePosition() {
    // String lastPostion =
    // PreferenceHelper.getHelper(mContext).readPreference(
    // PRE_KEK_LAST_POSITION);
    // int pos = 0, top = 0;
    // if (lastPostion != null) {
    // try {
    // String[] memPos = lastPostion.split("-");
    // pos = Integer.valueOf(memPos[0]);
    // top = Integer.valueOf(memPos[1]);
    // Log.i(TAG, pos + " " + top);
    // } catch (Exception e) {
    // Log.e(TAG, e.toString());
    // }
    // }
    // mList.setSelectionFromTop(pos, top);
    // }

    private void initContent() {
	if (mHistory.size() > 0) {
	    mList.setVisibility(View.VISIBLE);
	    tvEmpty.setVisibility(View.GONE);
	    mAdapter.notifyDataSetChanged();
	} else {
	    mList.setVisibility(View.GONE);
	    tvEmpty.setVisibility(View.VISIBLE);
	}
    }

    @Override
    protected void initElement() {
	mList = (ListView) findViewById(android.R.id.list);
	tvEmpty = (TextView) findViewById(R.id.tvHint);

	mList.setAdapter(mAdapter);
    }

    @Override
    protected int getLayout() {
	return R.layout.fragment_history;
    }

    private class CommunicationAdapter extends BaseAdapter {
	private ArrayList<Communication> mComms;
	private Context mContext;

	public CommunicationAdapter(Context context,
		ArrayList<Communication> comms) {
	    mContext = context;
	    mComms = comms;
	}

	@Override
	public int getCount() {
	    return mComms.size();
	}

	@Override
	public Communication getItem(int position) {
	    return mComms.get(position);
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
				R.layout.list_item_communication, null);
	    }

	    TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
	    TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
	    TextView tvDuration =
		    (TextView) convertView.findViewById(R.id.tv_duration);
	    ImageView ivType =
		    (ImageView) convertView.findViewById(R.id.iv_type);
	    TextView tvProgress =
		    (TextView) convertView.findViewById(R.id.tvProgress);

	    RelativeLayout rlCustomer =
		    (RelativeLayout) convertView.findViewById(R.id.rl_customer);
	    LinearLayout llTel =
		    (LinearLayout) convertView.findViewById(R.id.ll_tel);

	    final Communication c = getItem(position);
	    convertView.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		    Intent intent =
			    new Intent(mContext,
				    CustomerComuDetailActivity.class);
		    if (c.getId() != -1) {
			intent.putExtra(AddCustomerActivity.EXTRA_CUSTOMER_ID,
				c.getId());
		    } else {
			intent.putExtra(AddCustomerActivity.EXTRA_TEL,
				c.getTel());
		    }
		    mContext.startActivity(intent);

		}
	    });

	    llTel.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
		    IntentUtil.startTel(mContext, c.getTel());
		}
	    });

	    if (c.getName() != null && c.getName().length() > 0) {
		tvName.setText(c.getName());
	    } else {
		tvName.setText(c.getTel());
	    }

	    if (c.getType() == Communication.TYPE_INCOMING) {
		ivType.setBackgroundResource(R.drawable.ic_call_in);
	    } else if (c.getType() == Communication.TYPE_OUTCOMING) {
		ivType.setBackgroundResource(R.drawable.ic_call_out);
	    } else {
		ivType.setBackgroundResource(R.drawable.ic_call_missed);
	    }

	    if (c.getProgress() != null) {
		tvProgress.setText(c.getProgress());
	    } else {
		String location = c.getLocation();
		if (location == null) {
		    location = LocationHelper.getAreaByNumber(mContext, c.getTel());
		    c.setLocation(location);
//		    Log.i(TAG, c.getTel() + " " + c.getLocation());
		}
		tvProgress.setText(location);
	    }

	    tvTime.setText(DateUtil.getDisplayTime(c.getTime()));
	    tvDuration.setText(DateUtil.getDisplayForDuration(c.getDuration()));
	    return convertView;
	}
    }
}
