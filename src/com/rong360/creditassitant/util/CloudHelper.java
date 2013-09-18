package com.rong360.creditassitant.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.rong360.creditassitant.activity.AddCustomerActivity;
import com.rong360.creditassitant.activity.AuthCodeActivity;
import com.rong360.creditassitant.activity.AuthorisePartnerActivity;
import com.rong360.creditassitant.activity.ImportPartnerActivity;
import com.rong360.creditassitant.exception.ECode;
import com.rong360.creditassitant.exception.JsonParseException;
import com.rong360.creditassitant.json.JsonHelper;
import com.rong360.creditassitant.model.Action;
import com.rong360.creditassitant.model.ActionHandler;
import com.rong360.creditassitant.model.Customer;
import com.rong360.creditassitant.model.CustomerHandler;
import com.rong360.creditassitant.model.HistoryMsg;
import com.rong360.creditassitant.model.HistoryMsgHandler;
import com.rong360.creditassitant.model.TelHelper;
import com.rong360.creditassitant.model.result.BDCustomer;
import com.rong360.creditassitant.model.result.CustomerModel;
import com.rong360.creditassitant.model.result.Result;
import com.rong360.creditassitant.model.result.SyncResult;
import com.rong360.creditassitant.task.BaseHttpsManager.RequestParam;
import com.rong360.creditassitant.task.DomainHelper;
import com.rong360.creditassitant.task.HandleMessageTask;
import com.rong360.creditassitant.task.PostDataTask;
import com.rong360.creditassitant.task.HandleMessageTask.Callback;
import com.rong360.creditassitant.task.TransferDataTask;

public class CloudHelper {
    protected static final String TAG = "CloudHelper";
    public static final String PRE_KEY_LAST_BACK = "pre_key_last_back";
    private static final String PRE_KEY_MAX_ID = "pre_key_max_id";
    
    private static HashMap<Integer, String> mCodeMap;
    private static HashMap<Integer, String> mUseMap;
    
    static {
	mCodeMap = new HashMap<Integer, String>();
	mCodeMap.put(10, "新订单");
	mCodeMap.put(34, "无效电话");
	mCodeMap.put(130, "用户考虑中");
	mCodeMap.put(70, "不符合条件");
	mCodeMap.put(90, "用户放弃");
	mCodeMap.put(120, "待跟进用户（初满）");
	mCodeMap.put(150, "审批中");
	mCodeMap.put(155, "审批被拒");
	mCodeMap.put(170, "审批通过/成功放款");
	
	mUseMap = new HashMap<Integer, String>();
	mUseMap.put(9, "不限");
	mUseMap.put(1, "经营周转");
	mUseMap.put(2, "个人消费");
	mUseMap.put(3, "贷款买车");
	mUseMap.put(4, "按揭买房");
	mUseMap.put(5, "其他用途");
	
    }

    public static void back2Server(final Activity context, boolean isForce) {
	PreferenceHelper helper = PreferenceHelper.getHelper(context);
	String tel = helper.readPreference(AuthCodeActivity.EXTRA_TEL);
	String pass = helper.readPreference(AuthCodeActivity.EXTRA_PASS);
	// if (tel != null && pass != null) {
	String lastTime = helper.readPreference(PRE_KEY_LAST_BACK);
	long lastUpdateTime = 0;
	if (lastTime != null && lastTime.length() >= 0) {
	    lastUpdateTime = Long.valueOf(lastTime);
	}
	
	Log.i(TAG, "time :" + lastUpdateTime);
	lastUpdateTime = 0;
	ArrayList<Customer> allCustomers =
		GlobalValue.getIns().getAllCustomers();
	ArrayList<Customer> updateCustomers = new ArrayList<Customer>();
	for (Customer c : allCustomers) {
	    if (c.getTime() > lastUpdateTime) {
		updateCustomers.add(c);
	    }
	}
//	if (!isForce && DateUtil.getDaySpan(lastUpdateTime) < 3
//		&& updateCustomers.size() < 10) {
//	    return;
//	}

	JSONObject jArray = new JSONObject();

	try {
	    JSONArray objec =
		    JsonHelper.parseListToJsonArray(updateCustomers);
	    jArray.put("customer", objec);
	    Log.i(TAG, "update customer size :" + updateCustomers.size());
	    ArrayList<HistoryMsg> hisMsgs =
		    GlobalValue.getIns().getHistoryMsgs(
			    new HistoryMsgHandler(context));
	    ArrayList<HistoryMsg> updateMsgs = new ArrayList<HistoryMsg>();
	    for (HistoryMsg msg : hisMsgs) {
		if (msg.getTime() > lastUpdateTime) {
		    updateMsgs.add(msg);
		}
	    }
	    objec = JsonHelper.parseListToJsonArray(updateMsgs);
	    jArray.put("history_msg", objec);
	    Log.i(TAG, "update history msg size :" + updateMsgs.size());
	    
	    ArrayList<Action> updateActions =
		    GlobalValue.getIns().getActionHandler(context)
			    .getUpdateActions(lastUpdateTime);

	    objec = JsonHelper.parseListToJsonArray(updateActions);
	    jArray.put("action", objec);
	    Log.i(TAG, "action:" + objec.toString());
	    Log.i(TAG, "update action size :" + updateActions.size());
	} catch (JSONException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
//	Log.i(TAG, jArray.toString());
	RequestParam params = new RequestParam(DomainHelper.getApi(DomainHelper.SUFFIX_BACKUP));
	params.addNameValuePair("mobile", tel);
	params.addNameValuePair("password", pass);
	params.addNameValuePair("data", jArray.toString());
	String token = DomainHelper.getSecretToken(params);
	params.addNameValuePair("token", token);
	PostDataTask tTask =
		new PostDataTask(context, params);
	tTask.setCallback(new Callback() {

	    @Override
	    public void onSuccess(HandleMessageTask task, Object t) {
		try {
		    Log.i(TAG, "res:" + task.getResult());
		    Result res =
			    JsonHelper.parseJSONToObject(Result.class,
				    task.getResult());
		    if (res.getError() == (ECode.SUCCESS)) {
			PreferenceHelper helper =
				PreferenceHelper.getHelper(context);
			helper.writePreference(PRE_KEY_LAST_BACK,
				"" + System.currentTimeMillis());
		    } else if (res.getError() == 4) {
			Log.e(TAG, "解析数据失败");
		    }
		} catch (JsonParseException e) {
		    Log.e(TAG, e.toString());
		}
	    }

	    @Override
	    public void onFail(HandleMessageTask task, Object t) {

	    }
	});

	tTask.execute();
    }

    public static void syncOrder(final Context context) {
	PreferenceHelper helper = PreferenceHelper.getHelper(context);
	String tel = helper.readPreference(ImportPartnerActivity.PRE_KEY_BD_TEL);
	String pass = helper.readPreference(ImportPartnerActivity.PRE_KEY_BD_PASS);
	if (tel != null && pass != null) {
	    String maxOrderId = helper.readPreference(PRE_KEY_MAX_ID);
	    Log.i(TAG, "max order id" + maxOrderId);
	    RequestParam params = new RequestParam();
		params.addNameValuePair("mobile", tel);
		params.addNameValuePair("password", pass);
		params.addNameValuePair("max_order_id", maxOrderId);
		TransferDataTask tTask =
			new TransferDataTask(context, DomainHelper.getFullUrl(
				DomainHelper.SUFFIX_SYNC_ORDER, params));
		tTask.setShowProgressDialog(false);
		tTask.setCallback(new Callback() {
		    
		    @Override
		    public void onSuccess(HandleMessageTask task, Object t) {
			try {
			    Log.i(TAG, "res:" + task.getResult());
			    SyncResult res =
				    JsonHelper.parseJSONToObject(
					    SyncResult.class, task.getResult());
			    if (res.mResult.getError()
				    == (ECode.SUCCESS)) {
				PreferenceHelper helper = PreferenceHelper.getHelper(context);
				Log.i(TAG, "total count: " + res.mRes_total);
				transfer2Customers(res, context);
				helper.writePreference(AuthorisePartnerActivity.PRE_KEY_LAST_SYNC, "" + System.currentTimeMillis());
				MyToast.makeText(context, "已同步" + res.mRes_total + "个订单").show();
//				finish();
			    } else if (res.mResult.getError() == 1) {
				MyToast.makeText(context, "后端不可用").show();
			    } 
			} catch (JsonParseException e) {
			    Log.e(TAG, e.toString());
			}
		    }
		    
		    @Override
		    public void onFail(HandleMessageTask task, Object t) {
			
		    }
		});
		
		tTask.execute();
	}
    }
    
    public static void restoreFromCloud(final Context context) {
	PreferenceHelper helper = PreferenceHelper.getHelper(context);
	String tel = helper.readPreference(AuthCodeActivity.EXTRA_TEL);
	String pass = helper.readPreference(AuthCodeActivity.EXTRA_PASS);
	// if (tel != null && pass != null) {
	RequestParam params = new RequestParam();
	params.addNameValuePair("mobile", tel);
	params.addNameValuePair("password", pass);

	TransferDataTask tTask =
		new TransferDataTask(context, DomainHelper.getFullUrl(
			DomainHelper.SUFFIX_RECOVER, params));
	tTask.setCallback(new Callback() {

	    @Override
	    public void onSuccess(HandleMessageTask task, Object t) {
		try {
		    Log.i(TAG, "res:" + task.getResult());
		    CustomerModel res =
			    JsonHelper.parseJSONToObject(CustomerModel.class,
				    task.getResult());
		    if (res.mResult.getError() == (ECode.SUCCESS)) {
			PreferenceHelper helper =
				PreferenceHelper.getHelper(context);
			Log.i(TAG, "total customer: " + res.mData.mCustomer.length);
			save2db(res, context);
//			helper.writePreference(PRE_KEY_BACK_UP,
//				"" + System.currentTimeMillis());
			MyToast.makeText(context,
				"已恢复" + res.mData.mCustomer.length + "个客户").show();
			// context.finish();
		    } else if (res.mResult.getError() == 1) {
			MyToast.makeText(context, "后端不可用").show();
		    }
		} catch (JsonParseException e) {
		    Log.e(TAG, e.toString());
		}
	    }

	    @Override
	    public void onFail(HandleMessageTask task, Object t) {

	    }
	});

	tTask.execute();
    }

    protected static void transfer2Customers(SyncResult res, Context context) {
//	ArrayList<Customer> allCustomers = GlobalValue.getIns().getAllCustomers();
	HashMap<String, Customer> phoneMap = GlobalValue.getIns().getPhoneNameMap();
	CustomerHandler handler = GlobalValue.getIns().getCustomerHandler(context);
	int i = 0;
	int maxId = -1;
	for (BDCustomer bd : res.mData) {
//	    Log.i(TAG, "mobile :" + bd.mUser_mobile);
	    String mobile = TelHelper.getPureTel(bd.mUser_mobile);
	    Customer c = phoneMap.get(mobile);
	    if (c != null) {
		Log.w(TAG, "bd mobile exists" + c.getName());
		continue;
	    }
	    
	    c = new Customer();
	    String name = bd.mUser_name;
	    if (name != null && name.length() > AddCustomerActivity.MAX_NAME_LENGTH) {
		name = name.substring(0, AddCustomerActivity.MAX_NAME_LENGTH);
	    }
	    c.setName(name);
	    c.setTel(mobile);
	    c.setTime(bd.mCreate_time * 1000);
	    c.setLoan(bd.mLoan_limit);
	    c.setSource("融360");
	    c.setOrderNo(bd.mId);
	    if (bd.mId > maxId) {
		maxId = bd.mId;
	    }
	    c.setLastFollowComment(bd.mProduct_name + "-" + mUseMap.get(bd.mApplication_type) + "-" + mCodeMap.get(bd.mStatus));
	    handler.insertCustomer(c);
	    i++;
	}
	
	Log.i(TAG, "import " + i + " orders");
	PreferenceHelper.getHelper(context).writePreference(PRE_KEY_MAX_ID, String.valueOf(maxId));
	//TODO;
	GlobalValue.getIns().init(context);
    }
    
    protected static void save2db(CustomerModel cModel, Context context) {
	CustomerHandler cHandler = GlobalValue.getIns().getCustomerHandler(context);
	for (Customer c : cModel.mData.mCustomer) {
	    cHandler.insertCustomer(c);
	}
	GlobalValue.getIns().init(context);
	ActionHandler aHandler = GlobalValue.getIns().getActionHandler(context);
	for (Action a : cModel.mData.mAction) {
	    aHandler.updateAction(a);
	}
	HistoryMsgHandler mHandler = new HistoryMsgHandler(context);
	for (HistoryMsg msg : cModel.mData.mHistory_msg) {
	    mHandler.updateSms(msg);
	}
    }
}
