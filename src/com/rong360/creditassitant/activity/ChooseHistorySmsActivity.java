package com.rong360.creditassitant.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.HistoryMsg;
import com.rong360.creditassitant.model.HistoryMsgHandler;
import com.rong360.creditassitant.util.GlobalValue;

public class ChooseHistorySmsActivity extends BaseActionBar {
    public static final String EXTRA_SMS = "extra_sms";
    private ListView lvHistorySms;
    
    private HistoryMsgHandler mHandler;
    private ArrayList<HistoryMsg> mMsgs;
    
    private HistoryAdapter mAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar(false).setTitle("选择历史短信");
        
        mHandler = new HistoryMsgHandler(this);
        mMsgs = GlobalValue.getIns().getHistoryMsgs(mHandler);
        mAdapter = new HistoryAdapter(this, mMsgs);
        
        initContent();
    }
    
    private void initContent(){
	lvHistorySms.setAdapter(mAdapter);
    }
    
    @Override
    protected int getLayout() {
	return R.layout.activity_choose_history_sms;
    }
    
    @Override
    protected void initElements() {
	lvHistorySms = (ListView) findViewById(R.id.lvHistorySms);
	lvHistorySms.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view,
		    int position, long id) {
		Intent intent = new Intent();
		HistoryMsg msg = (HistoryMsg) view.getTag();
		intent.putExtra(EXTRA_SMS, msg.getMsg());
		setResult(RESULT_OK, intent);
		finish();
	    }
	});
    }
    
    private class HistoryAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<HistoryMsg> mMsgs;

	public HistoryAdapter(
		Context context,
		ArrayList<HistoryMsg> msgs) {
	    mContext = context;
	    mMsgs = msgs;
	}

	@Override
	public int getCount() {
	    return mMsgs.size();
	}

	@Override
	public HistoryMsg getItem(int position) {
	    return mMsgs.get(position);
	}

	@Override
	public long getItemId(int position) {
	    return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if (convertView == null) {
		convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_history_msg, null);
	    }
	    
	    HistoryMsg msg = getItem(position);
	    TextView tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
	    tvMsg.setText(msg.getMsg());
	    convertView.setTag(msg);
	    return convertView;
	}
	
    }
}
