package com.rong360.creditassitant.activity;

import static com.rong360.creditassitant.activity.ChooseOptionActivity.EXTRA_RESULT_ID;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.EXTRA_RESULT_TEXT;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_BANK;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_CAR;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_CASH;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_CREDI_RECORD;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_HOUSE;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_ID;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_PROGRESS;
import static com.rong360.creditassitant.activity.ChooseOptionActivity.TITLE_SOURCE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.CustomerHandler;
import com.rong360.creditassitant.util.RongStats;
import com.umeng.analytics.MobclickAgent;

public class AdvancedFilterActiviy extends BaseActionBar implements
	OnClickListener {
    public static final String EXTRA_QUERY = "extra_query";
    private static final String TAG = "AdvancedFilterActiviy";
    private RelativeLayout rlStar;
    private RelativeLayout rlTime;
    private RelativeLayout rlProgress;
    private RelativeLayout rlSource;
    private RelativeLayout rlBank;
    private RelativeLayout rlCash;
    private RelativeLayout rlId;
    private RelativeLayout rlCredit;
    private RelativeLayout rlHouse;
    private RelativeLayout rlCar;

    private TextView tvSource;
    private TextView tvProgress;
    private TextView tvBank;
    private TextView tvCash;
    private TextView tvId;
    private TextView tvCreditRecord;
    private TextView tvCar;
    private TextView tvHouse;
    private TextView tvStar;
    private TextView tvTime;

    private HashMap<RelativeLayout, TextView> mChooseMap;
    private HashMap<RelativeLayout, EditText> mInputMap;
    private HashMap<RelativeLayout, Integer> mQueryIndexMap;
    private HashMap<RelativeLayout, String> mTitleMap;

    private int mCurrentIndex;
    private RelativeLayout rlCurrent;
    private ArrayList<String> mValues;
    private ArrayList<Integer> mIndexer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	mChooseMap = new HashMap<RelativeLayout, TextView>();
	mInputMap = new HashMap<RelativeLayout, EditText>();
	mQueryIndexMap = new HashMap<RelativeLayout, Integer>();
	mTitleMap = new HashMap<RelativeLayout, String>();
	mValues = new ArrayList<String>();
	mIndexer = new ArrayList<Integer>();

	super.onCreate(savedInstanceState);
	getSupportActionBar().setTitle("高级筛选");

	ArrayList<String> query =
		getIntent().getStringArrayListExtra(
			AdvancedFilterActiviy.EXTRA_QUERY);
	if (query != null) {
	    mValues.addAll(query);
	    initContent();
	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	menu.add(0, R.id.finish, 0, "确定");
	return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	if (item.getItemId() == R.id.finish) {
	    Intent intent = new Intent();
	    Bundle b = new Bundle();
	    b.putStringArrayList(EXTRA_QUERY, mValues);
	    intent.putExtras(b);
	    setResult(RESULT_OK, intent);
	    finish();
	    return true;

	}
	return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initElements() {
	rlStar = (RelativeLayout) findViewById(R.id.rlStar);
	rlTime = (RelativeLayout) findViewById(R.id.rlInputTime);
	rlSource = (RelativeLayout) findViewById(R.id.rlSource);
	rlProgress = (RelativeLayout) findViewById(R.id.rlProgress);
	rlBank = (RelativeLayout) findViewById(R.id.rlBank);
	rlCash = (RelativeLayout) findViewById(R.id.rlCash);
	rlId = (RelativeLayout) findViewById(R.id.rlId);
	rlCredit = (RelativeLayout) findViewById(R.id.rlCreditRecord);
	rlCar = (RelativeLayout) findViewById(R.id.rlCar);
	rlHouse = (RelativeLayout) findViewById(R.id.rlHouse);

	tvSource = (TextView) findViewById(R.id.tvCSource);
	tvProgress = (TextView) findViewById(R.id.tvCProgress);
	tvBank = (TextView) findViewById(R.id.tvCBank);
	tvCash = (TextView) findViewById(R.id.tvCCash);
	tvId = (TextView) findViewById(R.id.tvCIdentity);
	tvCreditRecord = (TextView) findViewById(R.id.tvCCreditRecord);
	tvHouse = (TextView) findViewById(R.id.tvCHouse);
	tvCar = (TextView) findViewById(R.id.tvCCar);
	tvTime = (TextView) findViewById(R.id.tvCTime);
	tvStar = (TextView) findViewById(R.id.tvCStar);

	initMap();
	setListener();
    }

    private void initMap() {
	mChooseMap.put(rlSource, tvSource);
	mChooseMap.put(rlProgress, tvProgress);
	mChooseMap.put(rlCash, tvCash);
	mChooseMap.put(rlBank, tvBank);
	mChooseMap.put(rlId, tvId);
	mChooseMap.put(rlCredit, tvCreditRecord);
	mChooseMap.put(rlHouse, tvHouse);
	mChooseMap.put(rlCar, tvCar);
	mChooseMap.put(rlTime, tvTime);
	;
	mChooseMap.put(rlStar, tvStar);

	mQueryIndexMap.put(rlStar, QueryIndexer.STAR);
	mQueryIndexMap.put(rlTime, QueryIndexer.TIME);
	mQueryIndexMap.put(rlSource, QueryIndexer.SOURCE);
	mQueryIndexMap.put(rlProgress, QueryIndexer.PROGRESS);
	mQueryIndexMap.put(rlCash, QueryIndexer.CASH);
	mQueryIndexMap.put(rlBank, QueryIndexer.BANK);
	mQueryIndexMap.put(rlId, QueryIndexer.IDENTITY);
	mQueryIndexMap.put(rlCredit, QueryIndexer.CREDIT);
	mQueryIndexMap.put(rlHouse, QueryIndexer.HOUSE);
	mQueryIndexMap.put(rlCar, QueryIndexer.CAR);

	mTitleMap.put(rlStar, ChooseOptionActivity.TITLE_STAR);
	mTitleMap.put(rlTime, ChooseOptionActivity.TITLE_TIME);
	mTitleMap.put(rlSource, TITLE_SOURCE);
	mTitleMap.put(rlProgress, TITLE_PROGRESS);
	mTitleMap.put(rlCash, TITLE_CASH);
	mTitleMap.put(rlBank, TITLE_BANK);
	mTitleMap.put(rlId, TITLE_ID);
	mTitleMap.put(rlCredit, TITLE_CREDI_RECORD);
	mTitleMap.put(rlHouse, TITLE_HOUSE);
	mTitleMap.put(rlCar, TITLE_CAR);
    }

    private void setListener() {
	for (RelativeLayout rl : mChooseMap.keySet()) {
	    rl.setOnClickListener(this);
	}

	for (RelativeLayout rl : mInputMap.keySet()) {
	    rl.setOnClickListener(this);
	}
    }

    private void initContent() {
	for (String query : mValues) {
	    String[] qqqs = query.split(",");
	    int index = Integer.parseInt(qqqs[0]);
	    if (index == QueryIndexer.STAR) {
		mIndexer.add(Integer.valueOf(qqqs[0]));
		tvStar.setText(qqqs[2].replaceAll("#", ","));
	    } else if (index == QueryIndexer.TIME) {
		mIndexer.add(Integer.valueOf(qqqs[0]));
		tvTime.setText(qqqs[2].replaceAll("#", ","));
	    } else if (index == QueryIndexer.PROGRESS) {
		mIndexer.add(Integer.valueOf(qqqs[0]));
		tvProgress.setText(qqqs[2].replaceAll("#", ","));
	    } else if (index == QueryIndexer.SOURCE) {
		mIndexer.add(Integer.valueOf(qqqs[0]));
		tvSource.setText(qqqs[2].replaceAll("#", ","));
	    } else if (index == QueryIndexer.BANK) {
		mIndexer.add(Integer.valueOf(qqqs[0]));
		tvBank.setText(qqqs[2].replaceAll("#", ","));
	    } else if (index == QueryIndexer.CASH) {
		mIndexer.add(Integer.valueOf(qqqs[0]));
		tvCash.setText(qqqs[2].replaceAll("#", ","));
	    } else if (index == QueryIndexer.IDENTITY) {
		mIndexer.add(Integer.valueOf(qqqs[0]));
		tvId.setText(qqqs[2].replaceAll("#", ","));
	    } else if (index == QueryIndexer.CREDIT) {
		mIndexer.add(Integer.valueOf(qqqs[0]));
		tvCreditRecord.setText(qqqs[2].replaceAll("#", ","));
	    } else if (index == QueryIndexer.HOUSE) {
		mIndexer.add(Integer.valueOf(qqqs[0]));
		tvHouse.setText(qqqs[2].replaceAll("#", ","));
	    } else if (index == QueryIndexer.CAR) {
		mIndexer.add(Integer.valueOf(qqqs[0]));
		tvCar.setText(qqqs[2].replaceAll("#", ","));
	    }
	}
    }

    @Override
    protected int getLayout() {
	return R.layout.activity_advanced_filter;
    }

    @Override
    public void onClick(View v) {
	Intent intent = new Intent(this, ChooseOptionActivity.class);
	// if (v == rlSource) {
	intent.putExtra(ChooseOptionActivity.EXTRA_CHOOSE_TYPE,
		ChooseOptionActivity.TYPE_CHECKBOX);
	// }
	intent.putExtra(ChooseOptionActivity.EXTRA_TITLE, mTitleMap.get(v));
	HashMap<String, String> evenMap = new HashMap<String, String>();
	evenMap.put("操作项", mTitleMap.get(v));
	MobclickAgent.onEvent(this, RongStats.ADV_ITEM, evenMap);
	int index = mQueryIndexMap.get(v);
	for (int i = 0; i < mIndexer.size(); i++) {
	    if (mIndexer.get(i) == index) {
		String key = mValues.get(i);
		Log.i(TAG, key);
		String[] segs = key.split(",");
		if (segs.length > 2)
		    if (index != QueryIndexer.SOURCE) {
			// intent.putExtra(ChooseOptionActivity.EXTRA_SELECTED_INDEX,
			// Integer.parseInt(segs[1]));
			intent.putExtra(
				ChooseOptionActivity.EXTRA_SELECTED_IDS,
				segs[1]);
		    } else {
			intent.putExtra(
				ChooseOptionActivity.EXTRA_SELECTED_IDS,
				segs[1]);
		    }
	    }
	}
	mCurrentIndex = mQueryIndexMap.get(v);
	if (v instanceof RelativeLayout) {
	    rlCurrent = (RelativeLayout) v;
	}
	startActivityForResult(intent, 10001);
    }

    @Override
    protected void
	    onActivityResult(int requestCode, int resultCode, Intent data) {
	if (resultCode == RESULT_OK) {
	    for (int i = 0; i < mIndexer.size(); i++) {
		Integer va = mIndexer.get(i);
		if (va.intValue() == mCurrentIndex) {
		    mValues.remove(i);
		    mIndexer.remove(va);
		    break;
		}
	    }
	    String[] res = new String[3];
	    res[0] = mCurrentIndex + "";
	    // if (mCurrentIndex == QueryIndexer.SOURCE) {
	    int[] ids = data.getIntArrayExtra(EXTRA_RESULT_ID);
	    String[] tis = data.getStringArrayExtra(EXTRA_RESULT_TEXT);
	    String title = "";
	    String id = "";
	    if (ids.length > 0) {
		for (int idd : ids) {
		    id += idd + "#";
		}
		id = id.substring(0, id.length() - 1);
		for (String t : tis) {
		    title += t + "#";
		}
		title.substring(0, title.length() - 1);
	    }
	    res[1] = id;
	    res[2] = title;

	    // } else {
	    // res[1] = "" + data.getIntExtra(EXTRA_RESULT_ID, -1);
	    // res[2] = data.getStringExtra(EXTRA_RESULT_TEXT);
	    // }
	    mIndexer.add(mCurrentIndex);
	    mValues.add(res[0] + "," + res[1] + "," + res[2]);
	    // if (mCurrentIndex == QueryIndexer.SOURCE) {
	    String source = res[2].replace("#", ", ");
	    if (source.length() > 2) {
		mChooseMap.get(rlCurrent).setText(
			source.substring(0, source.length() - 2));
	    } else {
		mChooseMap.get(rlCurrent).setText("");
	    }
	    // } else {
	    // mChooseMap.get(rlCurrent).setText(res[2]);
	    // }
	    Log.i(TAG, "index size:" + mIndexer.size());
	    Log.i(TAG, "value size:" + mValues.size());
	}
    }

    public static class QueryIndexer {
	public static final int STAR = 0;
	public static final int TIME = 1;
	public static final int PROGRESS = 2;
	public static final int SOURCE = 3;
	public static final int BANK = 4;
	public static final int CASH = 5;
	public static final int IDENTITY = 6;
	public static final int CREDIT = 7;
	public static final int HOUSE = 8;
	public static final int CAR = 9;
    }

}
