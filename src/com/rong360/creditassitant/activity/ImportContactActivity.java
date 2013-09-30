package com.rong360.creditassitant.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.model.Action;
import com.rong360.creditassitant.model.ActionHandler;
import com.rong360.creditassitant.model.CommuHandler;
import com.rong360.creditassitant.model.Contact;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.model.CustomerHandler;
import com.rong360.creditassitant.model.TelHelper;
import com.rong360.creditassitant.util.CloudHelper;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.ModelHeler;
import com.rong360.creditassitant.util.MyToast;
import com.rong360.creditassitant.widget.IndexableListView;

public class ImportContactActivity extends BaseActionBar implements
	OnClickListener {
    private IndexableListView lvIndex;
    private TextView tvHint;

    private static final int TYPE_CONTACT = 0;
    private static final int TYPE_ALPHA = 1;
    public static final String TAG = "ImportContactActivity";

    private final String importText = "导入";

    private HashMap<Contact, Boolean> mCheckMap;
    private Button btnSelect;
    private Button btnImport;

    private ArrayList<Contact> mContacts;
    private ContactAdapter maAdapter;
    private ArrayList<Item> mItems;

    private ArrayList<Customer> mCustomers;

    private boolean mIsPorted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	mContacts = new ArrayList<Contact>();
	super.onCreate(savedInstanceState);
	getSupportActionBar().setTitle("通讯录导入客户");

    }

    @Override
    protected int getLayout() {
	return R.layout.activity_import_contact;
    }

    @Override
    protected void onResume() {
	super.onResume();
	initContent();
    }

    private void initContent() {
	ArrayList<Contact> contacts = CommuHandler.getAllContacts(this);
	HashMap<String, Customer> phoneNameMap =
		GlobalValue.getIns().getPhoneNameMap();
	mContacts.clear();
	for (Contact c : contacts) {
	    if (phoneNameMap.get(TelHelper.getPureTel(c.getTel())) == null) {
		mContacts.add(c);
	    }
	}
	mCheckMap = new HashMap<Contact, Boolean>();
	transfer2Items(mContacts);

	// Log.i(TAG, "contacts: " + mContacts.size() + " items: " +
	// mItems.size());
	if (mItems == null || mItems.size() == 0) {
	    tvHint.setVisibility(View.VISIBLE);
	    lvIndex.setVisibility(View.INVISIBLE);
	    if (contacts.size() != 0) {
		tvHint.setText("您已经导入了全部通讯录");
	    } else {
		tvHint.setText("通讯录没有记录");
	    }
	} else {
	    maAdapter = new ContactAdapter(this, mItems);
	    lvIndex.setAdapter(maAdapter);
	    lvIndex.setFastScrollEnabled(true);
	    lvIndex.setVisibility(View.VISIBLE);
	    lvIndex.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
			int position, long id) {
		    CheckBox cbChoose =
			    (CheckBox) view.findViewById(R.id.cb_choose);
		    if (mItems.get(position).type == TYPE_ALPHA) {
			return;
		    }
		    Contact c = (Contact) view.getTag();
		    if (mCheckMap.containsKey(c)) {
			mCheckMap.remove(c);
			cbChoose.setChecked(Boolean.FALSE);
		    } else {
			mCheckMap.put(c, Boolean.FALSE);
			cbChoose.setChecked(Boolean.TRUE);
		    }
		    btnImport.setText(importText + "(" + mCheckMap.size() + ")");
		}

	    });
	    tvHint.setVisibility(View.GONE);

	    btnImport.setText(importText);
	}

    }

    private ArrayList<Item> transfer2Items(ArrayList<Contact> contacts) {
	if (contacts == null || contacts.size() == 0) {
	    return null;
	}
	mItems = new ArrayList<ImportContactActivity.Item>();
	char lastAlpha = contacts.get(0).getSpell().charAt(0);
	mItems.add(new Item(lastAlpha, TYPE_ALPHA));
	mItems.add(new Item(contacts.get(0), TYPE_CONTACT));

	for (int i = 1; i < contacts.size(); i++) {
	    Contact c = contacts.get(i);
	    char firstChar = c.getSpell().charAt(0);
	    if (firstChar != lastAlpha) {
		lastAlpha = firstChar;
		mItems.add(new Item(lastAlpha, TYPE_ALPHA));
	    }
	    mItems.add(new Item(c, TYPE_CONTACT));
	}

	return mItems;
    }

    @Override
    protected void initElements() {
	lvIndex = (IndexableListView) findViewById(R.id.lv_index);
	tvHint = (TextView) findViewById(R.id.tv_hint);
	btnImport = (Button) findViewById(R.id.btn_import);
	btnSelect = (Button) findViewById(R.id.btn_select);
	btnImport.setOnClickListener(this);
	btnSelect.setOnClickListener(this);
    }

    private class ContactAdapter extends BaseAdapter implements SectionIndexer {
	private ArrayList<Item> mItems;
	private Context mContext;

	public ContactAdapter(Context context, ArrayList<Item> contacts) {
	    mContext = context;
	    mItems = contacts;
	}

	@Override
	public int getCount() {
	    return mItems.size();
	}

	@Override
	public Item getItem(int position) {
	    return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
	    return 0;
	}

	@Override
	public int getViewTypeCount() {
	    return 2;
	}

	@Override
	public int getItemViewType(int position) {
	    return getItem(position).type;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if (getItemViewType(position) == TYPE_ALPHA) {
		return initSection(position, convertView);
	    } else {
		return initContent(position, convertView);
	    }

	}

	private View initContent(int position, View convertView) {
	    // Log.i(TAG, "getViewContent");
	    if (convertView == null) {
		convertView =
			LayoutInflater.from(mContext).inflate(
				R.layout.list_item_contact, null);
	    }

	    final Contact c = getItem(position).contact;
	    TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
	    final CheckBox cbChoose =
		    (CheckBox) convertView.findViewById(R.id.cb_choose);
	    if (mCheckMap.containsKey(c)) {
		cbChoose.setChecked(Boolean.TRUE);
	    } else {
		cbChoose.setChecked(Boolean.FALSE);
	    }

	    convertView.setTag(c);
	    tvName.setText(c.getName());

	    return convertView;
	}

	private View initSection(int position, View convertView) {
	    if (convertView == null) {
		convertView =
			LayoutInflater.from(mContext).inflate(
				R.layout.list_item_head, null);
	    }

	    TextView tvHead = (TextView) convertView.findViewById(R.id.tvHead);
	    Item item = getItem(position);
	    tvHead.setText(item.alphaBeta + "");

	    return convertView;
	}

	private String mSections = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	@Override
	public int getPositionForSection(int section) {
	    int size = getCount();
	    for (int i = section; i >= 0; i--) {
		for (int j = 0; j < size; j++) {
		    Item it = getItem(j);
		    if (it.type == TYPE_ALPHA) {
			if (it.alphaBeta == mSections.charAt(i)
				|| it.alphaBeta == (mSections.charAt(i) + 32)) {
			    return j;
			}
		    }
		}
	    }
	    return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
	    return 0;
	}

	@Override
	public Object[] getSections() {
	    String[] sections = new String[mSections.length()];
	    for (int i = 0; i < mSections.length(); i++)
		sections[i] = String.valueOf(mSections.charAt(i));
	    return sections;
	}

    }

    private class Item {
	public Item(Contact con, int t) {
	    contact = con;
	    type = t;
	}

	public Item(char first, int t) {
	    alphaBeta = first;
	    type = t;
	}

	public Contact contact;
	public char alphaBeta;
	public int type; // 0 contact - 1 alphabeta;
    }

    @Override
    public void onClick(View v) {
	if (v == btnImport) {
	    if (mCheckMap.size() == 0) {
		MyToast.makeText(this, "请选择导入客户", Toast.LENGTH_SHORT).show();
		return;
	    }
	    if (!mIsPorted) {
		save2Customers();
	    } else {
		MyToast.makeText(this, "您已经导过了，亲", Toast.LENGTH_SHORT).show();
	    }

	} else if (v == btnSelect) {
	    if (mCheckMap.size() == mContacts.size()) {
		return;
	    }

	    for (Contact c : mContacts) {
		mCheckMap.put(c, Boolean.TRUE);
	    }

	    btnImport.setText(importText + "(" + mContacts.size() + ")");
	    maAdapter.notifyDataSetChanged();
	}
    }

    private void save2Customers() {
	CustomerHandler handler = GlobalValue.getIns().getCustomerHandler(this);
	if (mCustomers == null) {
	    mCustomers = GlobalValue.getIns().getAllCustomers();
	}
	ArrayList<Contact> removedContacts = new ArrayList<Contact>();
	int sucCount = 0;
	for (Contact c : mCheckMap.keySet()) {
	    if (containSameTel(mCustomers, c)) {
		removedContacts.add(c);
		// Log.i(TAG, "same tel: " + c.getName() + c.getTel());
		continue;
	    }
	    Customer customer = new Customer();
	     Log.i(TAG, "before pure: " + c.getTel());
	    customer.setTel(TelHelper.getPureTel(c.getTel()));
	     Log.i(TAG, "after pure: " + customer.getTel());
	    if (c.getCreateTime() != 0) {
		customer.setTime(c.getCreateTime());
	    } else {
		customer.setTime(System.currentTimeMillis());
	    }
	    // TODO name
	    if (c.getName().length() > AddCustomerActivity.MAX_NAME_LENGTH) {
		customer.setName(c.getName().substring(0,
			AddCustomerActivity.MAX_NAME_LENGTH));
	    } else {
		customer.setName(c.getName());
	    }
	    if (handler.insertCustomer(customer)) {
		customer = handler.getCustomerByTel(customer.getTel());
		removedContacts.add(c);
		if (customer == null) {
		    Log.e(TAG, "insert error");
		    continue;
		}
		Action a = new Action(customer.getId(), ActionHandler.TYPE_NEW);
		GlobalValue.getIns().getActionHandler(this).handleAction(a);
		customer.setIsImported(true);
		GlobalValue.getIns().putCustomer(customer);
		mCustomers.add(customer);
		sucCount++;
	    }
	}

	if (sucCount == mContacts.size()) {
	    mIsPorted = true;
	}

	// Log.i(TAG, "before: " + mContacts.size() + " " + mCheckMap.size() +
	// " " + removedContacts.size());
	// for (Contact c : removedContacts) {
	// mContacts.remove(c);
	// mCheckMap.remove(c);
	// }
	// Log.i(TAG, "after: " + mContacts.size() + " " + mCheckMap.size() +
	// " " + removedContacts.size());
	// mItems.clear();
	// transfer2Items(mContacts);
	// maAdapter.notifyDataSetChanged();
	MyToast.makeText(this, "成功导入" + sucCount + "个客户", Toast.LENGTH_LONG)
		.show();
	Log.i(TAG, "after insert: " + mCustomers.size());
	ModelHeler.orderCustomersByTime(mCustomers);
	finish();

	CloudHelper.back2Server(this, false);
    }

    private boolean containSameTel(ArrayList<Customer> customers, Contact c) {
	for (Customer cus : customers) {
	    if (ModelHeler.isTelEqual(cus.getTel(), c.getTel())) {
		return true;
	    }
	}
	return false;
    }
}
