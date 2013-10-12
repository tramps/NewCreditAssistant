package com.rong360.creditassitant.activity;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.PreferenceHelper;

public class ChooseOptionActivity extends BaseActionBar {
    protected static final String TAG = "ChooseOptionActivity";

    public static final String EXTRA_CHOOSE_TYPE = "pre_key_choose";
    public static final String EXTRA_SELECTED_INDEX = "pre_key_selected";
    public static final String EXTRA_SELECTED_IDS = "pre_key_selected_ids";
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

    public static final String TITLE_STAR = "是否加星";
    public static final String TITLE_TIME = "录入时间";

    public static final HashMap<String, Integer> mOpRsMap;

    private OptionAdapter mAdapter;
    private String[] mItems;
    private int mType;
    private int mSelectedIndex;
    private int[] mSelectedIds;
    private HashMap<Integer, String> mSelected;

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
	mOpRsMap.put(TITLE_STAR, R.array.star);
	mOpRsMap.put(TITLE_TIME, R.array.time);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	mType = getIntent().getIntExtra(EXTRA_CHOOSE_TYPE, TYPE_RADIO);
	if (mType == TYPE_CHECKBOX) {
	    mSelected = new HashMap<Integer, String>();
	}
	super.onCreate(savedInstanceState);
	String title = getIntent().getStringExtra(EXTRA_TITLE);
	getSupportActionBar(false).setTitle(title);
	mSelectedIndex = getIntent().getIntExtra(EXTRA_SELECTED_INDEX, -1);
	if (mType == TYPE_CHECKBOX) {
	    String ids = getIntent().getStringExtra(EXTRA_SELECTED_IDS);
	    if (ids != null && ids.length() > 0) {
		String[] idDs = ids.split("#");
		mSelectedIds = new int[idDs.length];
		int i = 0;
		for (String d : idDs) {
		    mSelectedIds[i] = Integer.parseInt(d);
		}
	    }
	}
	initContent(title);
    }

    private void initContent(String title) {
	if (title.equalsIgnoreCase(TITLE_SOURCE)) {
	    String current = getIntent().getStringExtra(EXTRA_RESULT_TEXT);
	    PreferenceHelper helper = PreferenceHelper.getHelper(this);
	    String source =
		    helper.readPreference(AddSourceActivity.PRE_KEY_SOURCES);
	    if (source != null && source.length() > 0) {
		String[] sources = source.split(";");
		mItems = new String[sources.length + 2];
		mItems[0] = "自有客户";
		mItems[1] = "融360导入客户";
		for (int i = 0; i < sources.length; i++) {
		    if (sources[i] != null) {
			mItems[i + 2] = sources[i];
		    } else {
			break;
		    }
		}
		for (int i = 0; i < mItems.length; i++) {
		    if (mItems[i].equalsIgnoreCase(current)) {
			mSelectedIndex = i;
		    }
		}
	    } else {
		mItems = new String[] { "自有客户", "融360导入客户" };
		for (int i = 0; i < mItems.length; i++) {
		    if (mItems[i].equalsIgnoreCase(current)) {
			mSelectedIndex = i;
		    }
		}
	    }
	} else {
	    mItems = getResources().getStringArray(mOpRsMap.get(title));
	}

	if (mSelectedIds != null)
	    for (int id : mSelectedIds) {
		mSelected.put(id, mItems[id]);
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
		handleClick(position);
	    }

	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	if (mType == TYPE_CHECKBOX) {
	    menu.add(0, R.id.finish, 0, "确定");
	}
	return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	if (item.getItemId() == R.id.finish) {
	    Intent intent = new Intent();
	    Integer[] ids = new Integer[mSelected.size()];
	    mSelected.keySet().toArray(ids);
	    int[] index = new int[ids.length];
	    int i = 0;
	    for (Integer id : ids) {
		index[i++] = id;
	    }
	    String[] titls = new String[mSelected.size()];
	    mSelected.values().toArray(titls);
	    intent.putExtra(EXTRA_RESULT_ID, index);
	    intent.putExtra(EXTRA_RESULT_TEXT, titls);
	    setResult(RESULT_OK, intent);
	    finish();
	    return true;

	}
	return super.onOptionsItemSelected(item);
    }

    private void handleClick(int pos) {
	if (mType == TYPE_RADIO) {
	    if (mSelectedIndex == pos) {
		mSelectedIndex = -1;
	    } else {
		mSelectedIndex = pos;
		Intent intent = new Intent();
		intent.putExtra(EXTRA_RESULT_ID, mSelectedIndex);
		intent.putExtra(EXTRA_RESULT_TEXT, mItems[pos]);
		setResult(RESULT_OK, intent);
		finish();
	    }
	    Log.i(TAG, "selected: " + mSelectedIndex);
	} else {
	    if (mSelected.containsKey(pos)) {
		mSelected.remove(pos);
	    } else {
		mSelected.put(pos, mItems[pos]);
	    }
	}

	mAdapter.notifyDataSetChanged();
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
	public View getView(final int position, View convertView,
		ViewGroup parent) {
	    if (convertView == null) {
		int layout = R.layout.list_item_option;
		if (type == TYPE_CHECKBOX) {
		    layout = R.layout.list_item_option_check;
		}

		convertView =
			LayoutInflater.from(mContext).inflate(layout, null);
	    }

	    CompoundButton check =
		    (CompoundButton) convertView.findViewById(R.id.ivChoose);
	    TextView tvOption =
		    (TextView) convertView.findViewById(R.id.tvOption);

	    if (mSelected == null) {
		if (position == mSelectedIndex) {
		    Log.i(TAG, "one equal");
		    check.setChecked(true);
		} else {
		    check.setChecked(false);
		}
	    } else {
		boolean isContained = false;
		for (int id : mSelected.keySet()) {
		    if (position == id) {
			check.setChecked(true);
			isContained = true;
			break;
		    }
		}
		if (!isContained) {
		    check.setChecked(false);
		}
	    }
	    // check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	    //
	    // @Override
	    // public void onCheckedChanged(CompoundButton buttonView, boolean
	    // isChecked) {
	    // handleClick(position);
	    //
	    // }
	    // });
	    tvOption.setText(getItem(position));

	    return convertView;
	}

    }

}
