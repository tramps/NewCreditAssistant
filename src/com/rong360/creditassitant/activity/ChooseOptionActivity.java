package com.rong360.creditassitant.activity;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.rong360.creditassitant.R;

public class ChooseOptionActivity extends BaseActionBar {
    protected static final String TAG = "ChooseOptionActivity";
    
    public static final String EXTRA_CHOOSE_TYPE = "pre_key_choose";
    public static final String EXTRA_SELECTED_INDEX = "pre_key_selected";
    public static final int TYPE_CHECKBOX = 1;
    public static final int TYPE_RADIO = 0;
    
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_RESULT_ID = "extra_res_id";
    public static final String EXTRA_RESULT_TEXT = "extra_res_text";
    
    public static final String TITLE_SOURCE = "客户来源";
    public static final String TITLE_PROGRESS = "客户进度";
    public static final String TITLE_BANK = "银行流水";
    public static final String TITLE_CASH = "现金流水";
    public static final String TITLE_ID = "身份";
    public static final String TITLE_CREDI_RECORD = "信用记录";
    public static final String TITLE_HOUSE = "房产";
    public static final String TITLE_CAR = "车辆";
    
    public static final HashMap<String, Integer> mOpRsMap;
    
    private OptionAdapter mAdapter;
    private String[] mItems;
    private int mType;
    private int mSelectedIndex;
    
    private ListView lvOption;
    
    static {
	mOpRsMap = new HashMap<String, Integer>();
	mOpRsMap.put(TITLE_PROGRESS, R.array.progress);
	mOpRsMap.put(TITLE_BANK, R.array.bankCash);
	mOpRsMap.put(TITLE_CASH, R.array.bankCash);
	mOpRsMap.put(TITLE_CREDI_RECORD, R.array.credit);
	mOpRsMap.put(TITLE_ID, R.array.identity);
	mOpRsMap.put(TITLE_HOUSE, R.array.house);
	mOpRsMap.put(TITLE_CAR, R.array.car);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        getSupportActionBar(false).setTitle(title);
        mType = getIntent().getIntExtra(EXTRA_CHOOSE_TYPE, TYPE_RADIO);
        mSelectedIndex = getIntent().getIntExtra(EXTRA_SELECTED_INDEX, -1);
        initContent(title);
    }
    
    
    private void initContent(String title) {
	if (title.equalsIgnoreCase(TITLE_SOURCE)) {
	    mItems = new String[] {"自有客户", "其他自定义来源"};
	} else {
	    mItems = getResources().getStringArray(mOpRsMap.get(title));
	}
	
	mAdapter = new OptionAdapter(this, mType, mItems);
	lvOption.setAdapter(mAdapter);
    }
    
    @Override
    protected void initElements() {
	
	lvOption = (ListView) findViewById(R.id.lvOption);
	lvOption.setOnItemClickListener(new OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> parent, View view,
		    int position, long id) {
		if (mSelectedIndex == position) {
		    mSelectedIndex = -1;
		} else {
		    mSelectedIndex = position;
		    Intent intent = new Intent();
		    intent.putExtra(EXTRA_RESULT_ID, mSelectedIndex);
		    intent.putExtra(EXTRA_RESULT_TEXT, mItems[position]);
		    setResult(RESULT_OK, intent);
		    finish();
		}
		
		Log.i(TAG, "selected: " + mSelectedIndex);
		mAdapter.notifyDataSetChanged();
	    }
	    
	});
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_choose_option;
    }
    
    private class OptionAdapter extends BaseAdapter {
	private Context mContext;
	private int type;
	private String[] mOptions;
	
	public OptionAdapter(Context context, int t, String[] ops) {
	    mContext = context;
	    type = t;
	    mOptions = ops;
	}

	@Override
	public int getCount() {
	    return mOptions.length;
	}

	@Override
	public String getItem(int position) {
	    return mOptions[position];
	}

	@Override
	public long getItemId(int position) {
	    return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if (convertView == null) {
		int layout = R.layout.list_item_option;
		if (type == TYPE_CHECKBOX) {
		    layout = R.layout.list_item_option_check;
		}
		
		convertView = LayoutInflater.from(mContext).inflate(layout, null);
	    }
	    
	    CompoundButton check = (CompoundButton) convertView.findViewById(R.id.ivChoose);
	    TextView tvOption = (TextView) convertView.findViewById(R.id.tvOption);
	    
	    if (position == mSelectedIndex) {
		Log.i(TAG, "one equal");
		check.setChecked(true);
	    } else {
		check.setChecked(false);
	    }
	    tvOption.setText(getItem(position));
	    
	    return convertView;
	}
	
    }

}
