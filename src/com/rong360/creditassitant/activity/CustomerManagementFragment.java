package com.rong360.creditassitant.activity;

import static com.rong360.creditassitant.widget.ActionItem.TITLE_ALL;
import static com.rong360.creditassitant.widget.ActionItem.TITLE_CONSISTENT;
import static com.rong360.creditassitant.widget.ActionItem.TITLE_FAIL;
import static com.rong360.creditassitant.widget.ActionItem.TITLE_POTENTIAL;
import static com.rong360.creditassitant.widget.ActionItem.TITLE_STAR;
import static com.rong360.creditassitant.widget.ActionItem.TITLE_SUCCEED;
import static com.rong360.creditassitant.widget.ActionItem.TITLE_UNCONSISTENT;
import static com.rong360.creditassitant.widget.ActionItem.TITLE_UPGRADE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.activity.AdvancedFilterActiviy.QueryIndexer;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.model.CustomerHandler;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.widget.ActionItem;
import com.rong360.creditassitant.widget.QuickAction;
import com.rong360.creditassitant.widget.QuickAction.OnActionItemClickListener;
import com.rong360.creditassitant.widget.TitleBarCenter;

public class CustomerManagementFragment extends BaseFragment implements
	OnClickListener {
    private static final int FILTER_INDEX = 100;

    private static final String TAG = "CustomerManagementFragment";

    private static final int TYPE_HEAD = 0;
    private static final int TYPE_CUSTOMER = 1;

    private String[] mFilter = new String[] { TITLE_ALL, TITLE_STAR,
	    TITLE_POTENTIAL, TITLE_CONSISTENT, TITLE_UPGRADE, TITLE_SUCCEED,
	    TITLE_FAIL, TITLE_UNCONSISTENT };

    private Button btnImport;
    private LinearLayout llNoCustomers;
    private ListView lvCustomers;
    private TextView tvHint;
    private ArrayList<Customer> mCustomers;
    private ArrayList<Customer> mFilteredCustomers;
    private int mFilterIndex;
    private ArrayList<Section> mSections;
    private CustomerAdapter mAdapter;
    private QuickAction mAction;

    private TitleBarCenter mTitleCenter;

    private ArrayList<String> mQueryIndex;
    private String mQueryHint = "";
    private View mQueryheader;

    private TextView tvHeadHint;
    private LinearLayout llHeader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mTitleCenter = getSupportActionBarCenter(Boolean.TRUE);
	mTitleCenter.showLeft();

	mFilterIndex = 0;
	initQuickAction();
	mTitleCenter.setTitleListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		mAction.showView(v);
	    }
	});
	setReuseOldViewEnable(true);
	setHasOptionsMenu(false);

	mFilteredCustomers = new ArrayList<Customer>();
	mSections = new ArrayList<CustomerManagementFragment.Section>();
	mAdapter = new CustomerAdapter(mContext, mSections);
	mQueryIndex = new ArrayList<String>();
    }

    private void initQuickAction() {
	mAction = new QuickAction(mContext);
	mAction.addActionItem(new ActionItem(ActionItem.ACTION_ALL,
		ActionItem.TITLE_ALL));
	mAction.addActionItem(new ActionItem(ActionItem.ACTION_STAR,
		ActionItem.TITLE_STAR));
	mAction.addActionItem(new ActionItem());
	mAction.addActionItem(new ActionItem(ActionItem.ACTION_POTENTIAL,
		ActionItem.TITLE_POTENTIAL));
	mAction.addActionItem(new ActionItem(ActionItem.ACTION_CONSISTENT,
		ActionItem.TITLE_CONSISTENT));
	mAction.addActionItem(new ActionItem(ActionItem.ACTION_UPGRADE,
		ActionItem.TITLE_UPGRADE));
	mAction.addActionItem(new ActionItem(ActionItem.ACTION_SUCCEED,
		ActionItem.TITLE_SUCCEED));
	mAction.addActionItem(new ActionItem(ActionItem.ACTION_FAIL,
		ActionItem.TITLE_FAIL));
	mAction.addActionItem(new ActionItem(ActionItem.ACTION_UNCONSISTENT,
		ActionItem.TITLE_UNCONSISTENT));

	mAction.setOnActionItemClickListener(new OnActionItemClickListener() {

	    @Override
	    public void onItemClick(QuickAction source, int pos, int actionId) {
		Log.i(TAG, "action id:" + actionId);
		mFilterIndex = actionId;
		mTitleCenter.setTitle(mFilter[mFilterIndex]);
		getFilteredCustomers();
		initContent();
	    }
	});

	RelativeLayout rlShift =
		(RelativeLayout) mAction.getRootView().findViewById(
			R.id.rlShift);
	rlShift.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		setAdvanceFilter();
		mAction.dismiss();
	    }
	});
    }

    private void setAdvanceFilter() {
	Intent intent = new Intent(mContext, AdvancedFilterActiviy.class);
	startActivityForResult(intent, 10002);
	mTitleCenter.setTitle("高级筛选");
	mFilterIndex = FILTER_INDEX;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (resultCode == Activity.RESULT_OK) {
	    mQueryIndex.clear();
	    ArrayList<String> query =
		    data.getStringArrayListExtra(AdvancedFilterActiviy.EXTRA_QUERY);
	    if (query != null) {
		mQueryIndex.addAll(query);
	    }

	    getFilteredCustomers();
	    initContent();
	}

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	menu.add(0, R.id.newCustomer, 0, "new customer").setIcon(
		R.drawable.ic_add);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	if (item.getItemId() == R.id.newCustomer) {
	    Intent intent = new Intent(mContext, AddCustomerActivity.class);
	    startActivity(intent);
	}
	return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
	super.onResume();
	if (mFilterIndex == FILTER_INDEX) {
	    mTitleCenter.setTitle("高级筛选");
	} else {
	    mTitleCenter.setTitle(mFilter[mFilterIndex]);
	}
	getFilteredCustomers();
	initContent();
    }

    @Override
    protected void initElement() {
	btnImport = (Button) findViewById(R.id.btnImport);
	btnImport.setOnClickListener(this);
	tvHint = (TextView) findViewById(R.id.tvHint);
	llNoCustomers = (LinearLayout) findViewById(R.id.ll_no_customer);
	lvCustomers = (ListView) findViewById(R.id.lv_customers);
	llHeader = (LinearLayout) findViewById(R.id.llHeader);
	lvCustomers.setAdapter(mAdapter);

	mQueryheader = findViewById(R.id.rlQuery);

	tvHeadHint = (TextView) findViewById(R.id.tvHeadHint);
	mQueryheader.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		setAdvanceFilter();
	    }
	});
    }

    private void initContent() {
	add2Sections(mFilteredCustomers, mSections);
	if (mSections.size() == 0) {
	    llNoCustomers.setVisibility(View.VISIBLE);
	    lvCustomers.setVisibility(View.GONE);
	    if (mFilterIndex == 0) {
		tvHint.setText("您未存入客户");
		btnImport.setVisibility(View.VISIBLE);
	    } else if (mFilterIndex == FILTER_INDEX) {
		tvHint.setText("没有满足查询条件的客户");
		btnImport.setVisibility(View.INVISIBLE);
	    } else {
		btnImport.setVisibility(View.GONE);
		if (mFilterIndex != FILTER_INDEX) {
		    tvHint.setText("尚无" + mFilter[mFilterIndex]);
		}
	    }

	} else {
	    llNoCustomers.setVisibility(View.GONE);
	    lvCustomers.setVisibility(View.VISIBLE);
	    mAdapter.notifyDataSetChanged();
	}

	if (mQueryHint.length() == 0 || mFilterIndex != FILTER_INDEX) {
	    llHeader.setVisibility(View.GONE);
	} else {
	    if (mQueryHint.length() > 0) {
		tvHeadHint.setText(mQueryHint);
	    }
	    llHeader.setVisibility(View.VISIBLE);
	}
    }

    private void getFilteredCustomers() {
	mFilteredCustomers.clear();
	if (mFilterIndex == FILTER_INDEX) {
	    StringBuilder filter = new StringBuilder();
	    StringBuilder head = new StringBuilder();
	    boolean isAdd = false;
	    for (String query : mQueryIndex) {
		String[] qqqs = query.split(",");
		int index = Integer.parseInt(qqqs[0]);
		if (index == QueryIndexer.STAR) {
		    head.append(qqqs[2]).append(", ");
		    if (isAdd) {
			filter.append(" and ");
		    }
		    isAdd = true;
		    filter.append(CustomerHandler.IS_FAVORED);
		    filter.append("=");
		    filter.append(qqqs[1]);
		    filter.append(" ");
		} else if (index == QueryIndexer.TIME) {
		    head.append(qqqs[2]).append(", ");
		    if (isAdd) {
			filter.append(" and ");
		    }
		    isAdd = true;
		    filter.append(CustomerHandler.TIME);
		    filter.append(">");
		    Calendar t = Calendar.getInstance();
		    int ix = Integer.parseInt(qqqs[1]);
		    int dn = 0;
		    if (ix == 0) {
			dn = -1;
		    } else if (ix == 1) {
			dn = -3;
		    } else if (ix == 2) {
			dn = -6;
		    } else if (ix == 3) {
			dn = -12;
		    }
		    t.roll(Calendar.MONTH, dn);
		    filter.append(t.getTimeInMillis());
		    filter.append(" ");
		} else if (index == QueryIndexer.PROGRESS) {
		    head.append(qqqs[2]).append(", ");
		    if (isAdd) {
			filter.append(" and ");
		    }
		    isAdd = true;
		    filter.append(CustomerHandler.PROGRESS);
		    filter.append("=");
		    filter.append("\"" + qqqs[2] + "\"");
		    filter.append(" ");
		} else if (index == QueryIndexer.SOURCE) {
		    head.append(qqqs[2].replace("#", ", ")).append(", ");
		    if (isAdd) {
			filter.append(" and ");
		    }
		    String[] sources = qqqs[2].split("#");
		    filter.append(" ( ");
		    boolean isOr = false;
		    for (String s : sources) {
			if (isOr) {
			    filter.append(" or ");
			}
			filter.append(CustomerHandler.SOURCE);
			filter.append("=");
			filter.append("\"" + s + "\"");
			filter.append(" ");
			isOr = true;
		    }
		    filter.append(" ) ");
		} else if (index == QueryIndexer.BANK) {
		    head.append(qqqs[2]).append(", ");
		    if (isAdd) {
			filter.append(" and ");
		    }
		    isAdd = true;
		    filter.append(CustomerHandler.BANK);
		    filter.append("=");
		    filter.append(qqqs[1]);
		    filter.append(" ");
		} else if (index == QueryIndexer.CASH) {
		    head.append(qqqs[2]).append(", ");
		    if (isAdd) {
			filter.append(" and ");
		    }
		    isAdd = true;
		    filter.append(CustomerHandler.CASH);
		    filter.append("=");
		    filter.append(qqqs[1]);
		    filter.append(" ");
		} else if (index == QueryIndexer.IDENTITY) {
		    head.append(qqqs[2]).append(", ");
		    if (isAdd) {
			filter.append(" and ");
		    }
		    isAdd = true;
		    filter.append(CustomerHandler.IDENTITY);
		    filter.append("=");
		    filter.append(qqqs[1]);
		    filter.append(" ");
		} else if (index == QueryIndexer.CREDIT) {
		    head.append(qqqs[2]).append(", ");
		    if (isAdd) {
			filter.append(" and ");
		    }
		    isAdd = true;
		    filter.append(CustomerHandler.CREDIT_RECORD);
		    filter.append("=");
		    filter.append(qqqs[1]);
		    filter.append(" ");
		} else if (index == QueryIndexer.HOUSE) {
		    head.append(qqqs[2]).append(", ");
		    if (isAdd) {
			filter.append(" and ");
		    }
		    isAdd = true;
		    filter.append(CustomerHandler.HOUSE);
		    filter.append("=");
		    filter.append(qqqs[1]);
		    filter.append(" ");
		} else if (index == QueryIndexer.CAR) {
		    head.append(qqqs[2]).append(", ");
		    if (isAdd) {
			filter.append(" and ");
		    }
		    isAdd = true;
		    filter.append(CustomerHandler.CAR);
		    filter.append("=");
		    filter.append(qqqs[1]);
		    filter.append(" ");
		}
	    }
	    Log.i(TAG, "filter:" + filter.toString());
	    Log.i(TAG, "head:" + head.toString());
	    if (filter.length() > 0) {
		ArrayList<Customer> quaCustomers =
			GlobalValue.getIns().getCustomerHandler(mContext)
				.getCustomerByFilter(filter.toString());
		mFilteredCustomers.addAll(quaCustomers);
	    } else {
		mFilteredCustomers.addAll(mCustomers);
	    }

	    if (head.length() > 2) {
		mQueryHint = head.toString().substring(0, head.length() - 2);
	    }

	    return;
	}
	
	mCustomers = GlobalValue.getIns().getAllCustomers();
	if (mCustomers.size() == 0) {
	    GlobalValue.getIns().loadAllCustomerFromDb(mCustomers, mContext);
	}
	String filter = mFilter[mFilterIndex];
	if (filter.equalsIgnoreCase(TITLE_ALL)) {
	    mFilteredCustomers.addAll(mCustomers);
	} else if (filter.equalsIgnoreCase(TITLE_STAR)) {
	    for (Customer c : mCustomers) {
		if (c.isIsFavored()) {
		    mFilteredCustomers.add(c);
		}
	    }
	} else {
	    for (Customer c : mCustomers) {
		if (filter.equalsIgnoreCase(c.getProgress())) {
		    mFilteredCustomers.add(c);
		}
	    }
	}
    }

    private void add2Sections(ArrayList<Customer> customers,
	    ArrayList<Section> sections) {
	mSections.clear();
	if (customers.size() == 0) {
	    return;
	}

	Log.i(TAG, "customer size: " + mCustomers.size());
	Calendar lastCalc = Calendar.getInstance(Locale.CHINA);
	Customer lastCustomer = customers.get(0);
	lastCalc.setTimeInMillis(lastCustomer.getTime());
	mSections.add(new Section(TYPE_HEAD, lastCalc.get(Calendar.MONTH)
		+ "month "));
	mSections.add(new Section(TYPE_CUSTOMER, lastCustomer));
	Calendar nextCalc = Calendar.getInstance();
	for (int i = 1; i < customers.size(); i++) {
	    Customer c = customers.get(i);
	    nextCalc.setTimeInMillis(c.getTime());
	    if (lastCalc.get(Calendar.MONTH) != nextCalc.get(Calendar.MONTH)) {
		mSections.add(new Section(TYPE_HEAD, nextCalc
			.get(Calendar.MONTH) + "month"));
		lastCalc = nextCalc;
	    }

	    mSections.add(new Section(TYPE_CUSTOMER, c));
	}

	Log.i(TAG, "sections size: " + mSections.size());
    }

    @Override
    protected int getLayout() {
	return R.layout.fragment_customers;
    }

    @Override
    public void onClick(View v) {
	if (v == btnImport) {
	    Intent intent = new Intent(mContext, ImportContactActivity.class);
	    startActivity(intent);
	}
    }

    private class CustomerAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Section> mSections;

	public CustomerAdapter(Context context, ArrayList<Section> sections) {
	    mContext = context;
	    mSections = sections;
	}

	@Override
	public int getCount() {
	    return mSections.size();
	}

	@Override
	public Section getItem(int position) {
	    return mSections.get(position);
	}

	@Override
	public long getItemId(int position) {
	    return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if (getItemViewType(position) == TYPE_CUSTOMER) {
		return initCustomer(position, convertView);
	    } else {
		return initTitle(position, convertView);
	    }
	}

	private View initTitle(int position, View convertView) {
	    if (convertView == null) {
		convertView =
			LayoutInflater.from(mContext).inflate(
				R.layout.list_item_customer_head, null);
	    }

	    TextView tvHead = (TextView) convertView.findViewById(R.id.tvHead);
	    tvHead.setText(getItem(position).title);
	    return convertView;
	}

	private View initCustomer(int position, View convertView) {
	    if (convertView == null) {
		convertView =
			LayoutInflater.from(mContext).inflate(
				R.layout.list_item_customer_content, null);
	    }

	    final Customer c = getItem(position).customer;
	    convertView.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
		    Intent intent =
			    new Intent(mContext, CustomerDetailActivity.class);
		    intent.putExtra(AddCustomerActivity.EXTRA_CUSTOMER_ID,
			    c.getId());
		    Log.i(TAG, c.getId() + c.getName());
		    startActivity(intent);
		}
	    });

	    TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
	    ImageView ivStar =
		    (ImageView) convertView.findViewById(R.id.ivStar);
	    TextView tvLoan = (TextView) convertView.findViewById(R.id.tvLoan);
	    TextView tvSource =
		    (TextView) convertView.findViewById(R.id.tvSource);
	    TextView tvProgress =
		    (TextView) convertView.findViewById(R.id.tvProgress);
	    tvName.setText(c.getName());
	    if (c.isIsFavored()) {
		ivStar.setVisibility(View.VISIBLE);
	    } else {
		ivStar.setVisibility(View.GONE);
	    }
	    if (c.getLoan() > 0) {
		tvLoan.setText(c.getLoan() + "");
	    } else {
		tvLoan.setText("");
	    }
	    tvSource.setText(c.getSource());
	    tvProgress.setText(c.getProgress());
	    return convertView;
	}

	@Override
	public int getItemViewType(int position) {
	    return getItem(position).type;
	}

	@Override
	public int getViewTypeCount() {
	    return 2;
	}

    }

    private class Section {
	public Section(int t, Customer c) {
	    type = t;
	    customer = c;
	}

	public Section(int t, String til) {
	    type = t;
	    title = til;
	}

	public int type;
	public Customer customer;
	public String title;
    }
}
