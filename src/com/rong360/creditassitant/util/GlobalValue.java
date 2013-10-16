package com.rong360.creditassitant.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rong360.creditassitant.model.ActionHandler;
import com.rong360.creditassitant.model.Comment;
import com.rong360.creditassitant.model.CommentHandler;
import com.rong360.creditassitant.model.CommuHandler;
import com.rong360.creditassitant.model.Communication;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.model.CustomerHandler;
import com.rong360.creditassitant.model.HistoryMsg;
import com.rong360.creditassitant.model.HistoryMsgHandler;
import com.rong360.creditassitant.model.NoticeIgnoreHandler;


import android.content.Context;
import android.util.Log;

public class GlobalValue {
    private static final String TAG = "GlobalValue";
    private static GlobalValue mInstance;

    private static CustomerHandler mCustomerHander;
    private static CommentHandler mCommentHandler;
    private static NoticeIgnoreHandler mNoticeIgnoreHandler;
    private static ActionHandler mActionHandler;
    
    private ArrayList<Customer> mAlarmCustomers;
    
    public ArrayList<Customer> getAlarms() {
	if (mAlarmCustomers == null) {
	    mAlarmCustomers =  new ArrayList<Customer>();
	}
	
	return mAlarmCustomers;
    }

    public static GlobalValue getIns() {
	if (mInstance == null)
	    mInstance = new GlobalValue();
	return mInstance;
    }

    public void init(Context context) {
	ArrayList<Customer> customers = GlobalValue.getIns().getAllCustomers();
	customers.clear();
	GlobalValue.getIns().loadAllCustomerFromDb(customers, context);
    }

    public ActionHandler getActionHandler(Context context) {
	if (mActionHandler == null) {
	    mActionHandler = new ActionHandler(context);
	}

	return mActionHandler;
    }

    public CustomerHandler getCustomerHandler(Context context) {
	if (mCustomerHander == null) {
	    mCustomerHander = new CustomerHandler(context);
	}

	return mCustomerHander;
    }

    public NoticeIgnoreHandler getNoticeHandler(Context context) {
	if (mNoticeIgnoreHandler == null) {
	    mNoticeIgnoreHandler = new NoticeIgnoreHandler(context);
	}

	return mNoticeIgnoreHandler;
    }

    public CommentHandler getCommentHandler(Context context) {
	if (mCommentHandler == null) {
	    mCommentHandler = new CommentHandler(context);
	}

	return mCommentHandler;
    }

    public void
	    loadAllCustomerFromDb(List<Customer> customers, Context context) {
	CustomerHandler hander =
		GlobalValue.getIns().getCustomerHandler(context);
	List<Customer> cuses = hander.getAllCustomers();

	String maxOrderId = PreferenceHelper.getHelper(context).readPreference(CloudHelper.PRE_KEY_MAX_ID);
	int maxId = 0;
	if (maxOrderId != null && maxOrderId.length() > 0) {
	    maxId = Integer.valueOf(maxOrderId);
	}
	if (cuses.size() > 0) {
	    customers.addAll(cuses);
	    for (int i = 0; i < customers.size(); i++) {
		Customer c = customers.get(i);
		if (c.mOrderNo > maxId) {
		    c.setIsImported(true);
		}
		GlobalValue.getIns().putCustomer(c);
	    }
	}
    }

    private Map<Integer, Customer> mCustomers;
    private Map<Integer, List<Comment>> mCustomerComments;

    private ArrayList<Customer> mCustomerList;
    private ArrayList<Customer> mFavorList;

    private boolean mIsListDirty = true;
    private boolean mIsFavorDirty = true;

    private HashMap<String, Customer> mPhoneNameMap;

    private ArrayList<String> mPhones;
    private long mPhoneTime;
    private static final long READ_CONTACT_HOURLY = 60 * 60 * 1000;

    private ArrayList<HistoryMsg> mHistoryMsgs;
    private boolean mIsMsgDirty = true;
    
    private ArrayList<Communication> mComunications;
    public boolean mNeedUpdateCommunication;
    
    public boolean isNeedUpdateCommunication() {
        return mNeedUpdateCommunication;
    }

    public void setNeedUpdateCommunication(boolean mNeedUpdateCommunication) {
        this.mNeedUpdateCommunication = mNeedUpdateCommunication;
    }

    public ArrayList<Communication> getAllComunication(Context context) {
	if (mComunications == null) {
	    mComunications = new ArrayList<Communication>();
	}
	return mComunications;
    }

    public ArrayList<HistoryMsg> getHistoryMsgs(HistoryMsgHandler handler) {
	if (mHistoryMsgs == null) {
	    mHistoryMsgs = new ArrayList<HistoryMsg>();
	}
	if (mHistoryMsgs.size() == 0 || mIsMsgDirty) {
	    mHistoryMsgs.addAll(handler.getAllHistoryMsges());
	    mIsMsgDirty = false;
	}
	return mHistoryMsgs;
    }

    public void setMsgDirty() {
	mIsMsgDirty = true;
	mHistoryMsgs.clear();
    }

    public void clearOnLogOut() {
	clear();
    }

    public void clear() {
	mCustomers.clear();
	mCustomerComments.clear();
	mCustomerHander.close();
	mCommentHandler.close();
    }

    public ArrayList<String> getContactPhones(Context context) {
	if (mPhones == null) {
	    mPhones = new ArrayList<String>();
	    mPhoneTime = System.currentTimeMillis();
	}
	if (mPhones.size() == 0
		|| System.currentTimeMillis() - mPhoneTime > READ_CONTACT_HOURLY) {
	    List<String> contacts = CommuHandler.getAllContactsPhone(context);
	    if (contacts.size() > 0) {
		mPhones.clear();
		mPhones.addAll(contacts);
	    }
	}

	return mPhones;
    }

    public HashMap<String, Customer> getPhoneNameMap() {
	if (mPhoneNameMap == null) {
	    mPhoneNameMap = new HashMap<String, Customer>();
	}

	if (mIsListDirty || mPhoneNameMap.size() == 0
		|| mPhoneNameMap.size() != mCustomerList.size()) {
	    Log.i(TAG, "phone name map isdirty:" + mIsListDirty + " last size:"
		    + mPhoneNameMap.size());
	    mPhoneNameMap.clear();

	    List<Customer> customers = getAllCustomers();

	    Customer c;
	    int size = customers.size();
	    for (int i = 0; i < size; i++) {
		c = customers.get(i);
		mPhoneNameMap.put(c.getTel(), c);
	    }
	}

	Log.i(TAG, "phone2name map new size: " + mPhoneNameMap.size());
	return mPhoneNameMap;
    }

    public ArrayList<Customer> getAllCustomers() {
	if (mCustomerList == null) {
	    mCustomerList = new ArrayList<Customer>();
	    mFavorList = new ArrayList<Customer>();
	}

	if (mCustomers != null && mIsListDirty) {
	    Log.i(TAG, "new fav");
	    mCustomerList.clear();
	    mCustomerList.addAll(mCustomers.values());

	    Collections.sort(mCustomerList, new Comparator<Customer>() {

		@Override
		public int compare(Customer lhs, Customer rhs) {
		    return rhs.getTime() - lhs.getTime() > 0 ? 1 : -1;
		}
	    });
	}

	Log.i(TAG, "getAll : size " + mCustomerList.size());
	mIsListDirty = false;
	return mCustomerList;
    }

    public List<Customer> getAllFavoredCusmers() {
	if (mIsFavorDirty || mFavorList.size() == 0) {
	    Log.i(TAG, "new fav");
	    getAllCustomers();
	    mFavorList.clear();

	    Customer c;
	    for (int i = 0; i < mCustomerList.size(); i++) {
		c = mCustomerList.get(i);
		if (c.isIsFavored()) {
		    mFavorList.add(c);
		}
	    }
	}

	mIsFavorDirty = false;
	Log.i(TAG, "getfav : size " + mFavorList.size());
	return mFavorList;
    }

    public Customer getCusmer(int customerId) {
	if (mCustomers == null) {
	    mCustomers = new HashMap<Integer, Customer>();
	}

	return mCustomers.get(customerId);
    }

    public void putCustomer(Customer customer) {
	if (null == mCustomers) {
	    mCustomers = new HashMap<Integer, Customer>();
	}

	mCustomers.put(customer.getId(), customer);
	Log.i(TAG, "all customer: " + mCustomers.size());
	mIsListDirty = true;
	mIsFavorDirty = true;
    }

    public void removeCustomer(int id) {
	mCustomers.remove(id);
	mIsListDirty = true;
	for (int i = 0; i < mFavorList.size(); i++) {
	    if (mFavorList.get(i).getId() == id) {
		mIsFavorDirty = true;
		break;
	    }
	}
    }

    public void updateCustomerFavor(Customer customer) {
	if (null == mCustomers) {
	    mCustomers = new HashMap<Integer, Customer>();
	}

	mCustomers.put(customer.getId(), customer);
	Log.i(TAG, "all customer: " + mCustomers.size());
	mIsFavorDirty = true;
    }

    public List<Comment> getCommentsById(int customerId) {
	if (mCustomerComments == null) {
	    mCustomerComments = new HashMap<Integer, List<Comment>>();
	}

	List<Comment> res = mCustomerComments.get(customerId);

	if (null == res) {
	    res = new ArrayList<Comment>();
	    mCustomerComments.put(customerId, res);
	}

	return res;
    }

    public void putCommentsById(int id, List<Comment> coms) {
	if (mCustomerComments == null) {
	    mCustomerComments = new HashMap<Integer, List<Comment>>();
	}

	mCustomerComments.put(id, coms);
    }

    public void updateComments(Comment comment) {
	List<Comment> comList = getCommentsById(comment.getCustomerId());
	comList.add(0, comment);
	mCustomerComments.put(comment.getCustomerId(), comList);
    }

    public Comment getLastestComment(int customerId) {
	List<Comment> res = getCommentsById(customerId);
	if (res.size() == 0) {
	    return null;
	}

	return getCommentsById(customerId).get(0);
    }

    public static void onDestroy(Context context) {
	GlobalValue.getIns().clear();
    }
}
