package com.rong360.creditassitant.util;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.rong360.creditassitant.model.result.SyncResult;
import com.rong360.creditassitant.model.result.TResult;
import com.rong360.creditassitant.service.NotificationHelper;
import com.rong360.creditassitant.task.BaseHttpsManager.RequestParam;
import com.rong360.creditassitant.task.DomainHelper;
import com.rong360.creditassitant.task.HandleMessageTask;
import com.rong360.creditassitant.task.HandleMessageTask.Callback;
import com.rong360.creditassitant.task.PostDataTask;
import com.rong360.creditassitant.task.TransferDataTask;
import com.rong360.creditassitant.task.WaitingTask;
import com.umeng.analytics.MobclickAgent;

public class CloudHelper {
    protected static final String TAG = "CloudHelper";
    public static final String PRE_KEY_LAST_BACK = "pre_key_last_back";
    public static final String PRE_KEY_MAX_ID = "pre_key_max_id";

    public static final String PRE_KEY_DELETE_IDS = "pre_key_delete_ids";

    private static HashMap<Integer, String> mCodeMap;
    private static HashMap<Integer, String> mUseMap;
    private static HashMap<Integer, String> mProgressMap;

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

	mProgressMap = new HashMap<Integer, String>();
	mProgressMap.put(10, "意向客户");
	mProgressMap.put(130, "意向客户");
	mProgressMap.put(70, "不符客户");
	mProgressMap.put(90, "失败客户");
	mProgressMap.put(120, "意向客户");
	mProgressMap.put(150, "进件客户");
	mProgressMap.put(155, "失败客户");
	mProgressMap.put(170, "成功客户");

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
	 if (!isForce && DateUtil.getDaySpan(lastUpdateTime) < 3
	 && updateCustomers.size() < 10) {
	 return;
	 }

	JSONObject jArray = new JSONObject();

	try {
	    JSONArray objec = JsonHelper.parseListToJsonArray(updateCustomers);
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
	// Log.i(TAG, jArray.toString());
	RequestParam params =
		new RequestParam(
			DomainHelper.getApi(DomainHelper.SUFFIX_BACKUP));
	params.addNameValuePair("mobile", tel);
	params.addNameValuePair("password", pass);
	params.addNameValuePair("data", jArray.toString());
	String token = DomainHelper.getSecretToken(params);
	params.addNameValuePair("token", token);
	PostDataTask tTask = new PostDataTask(context, params);
	tTask.setCallback(new Callback() {

	    @Override
	    public void onSuccess(HandleMessageTask task, Object t) {
		try {
		    Log.i(TAG, "res:" + task.getResult());
		    TResult res =
			    JsonHelper.parseJSONToObject(TResult.class,
				    task.getResult());
		    if (res.mResult.getError() == (ECode.SUCCESS)) {
			PreferenceHelper helper =
				PreferenceHelper.getHelper(context);
			helper.writePreference(PRE_KEY_LAST_BACK,
				"" + System.currentTimeMillis());
		    } else if (res.mResult.getError() == 4) {
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

    public static void syncOrder(Context context) {
	syncOrder(context, false);
    }
    
    public static interface IOnFinished {
	public void onSuccess(int count);
	public void onFail();
    }
    
    public static void syncOrder(Context context, IOnFinished onFinish) {
	syncOrder(context, false, onFinish);
    }
    
    public static void syncOrder(final Context context,
	    final boolean showNotification) {
	syncOrder(context, showNotification, null);
    }

    public static void syncOrder(final Context context,
	    final boolean showNotification, final IOnFinished onFinish) {
	MobclickAgent.onEvent(context, RongStats.IMP_RONG_START);
	PreferenceHelper helper = PreferenceHelper.getHelper(context);
	String tel =
		helper.readPreference(ImportPartnerActivity.PRE_KEY_BD_TEL);
	String pass =
		helper.readPreference(ImportPartnerActivity.PRE_KEY_BD_PASS);
	Log.d(TAG, "tel : " + tel);
	if (tel != null && pass != null) {
	    String maxOrderId = helper.readPreference(PRE_KEY_MAX_ID);
	    Log.i(TAG, "max order id" + maxOrderId);
	    RequestParam params = new RequestParam();
	    params.addNameValuePair("mobile", tel);
	    params.addNameValuePair("password", pass);
	    params.addNameValuePair("max_order_id", maxOrderId);
	    String ryj = PreferenceHelper.getHelper(context).readPreference(AuthCodeActivity.EXTRA_TEL);
	    if (ryj == null) {
		ryj = "";
	    }
	    params.addNameValuePair("ryj_account", ryj);
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
				JsonHelper.parseJSONToObject(SyncResult.class,
					task.getResult());
			if (res.mResult.getError() == (ECode.SUCCESS) || res.mResult.getError() == (ECode.SUCCESS)) {
			    transfer2Customers(showNotification, res, context, onFinish);
			    onFinish.onSuccess(res.mRes_total);
			    MobclickAgent.onEvent(context, RongStats.IMP_RONG_SUC);
			    // finish();
			} else if (res.mResult.getError() == 1) {
			    MyToast.makeText(context, "后端不可用").show();
			    MobclickAgent.onEvent(context, RongStats.IMP_RONG_FAIL);
			}
			
		    } catch (JsonParseException e) {
			Log.e(TAG, e.toString());
			if (onFinish != null)
			onFinish.onSuccess(0);
			MobclickAgent.onEvent(context, RongStats.IMP_RONG_FAIL);
		    }
		}

		@Override
		public void onFail(HandleMessageTask task, Object t) {
		    if (onFinish != null)
		    onFinish.onFail();
		    MobclickAgent.onEvent(context, RongStats.IMP_RONG_FAIL);
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
			Log.i(TAG, "total customer: "
				+ res.mData.mCustomer.length);
			save2db(res, context);
			// helper.writePreference(PRE_KEY_BACK_UP,
			// "" + System.currentTimeMillis());
			MyToast.makeText(context,
				"已恢复" + res.mData.mCustomer.length + "个客户")
				.show();
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

    protected static void transfer2Customers(final boolean showNotification, final SyncResult res, final Context context, final IOnFinished onFinish) {
	ImportTask task = new ImportTask(context, res);
	task.setCallback(new Callback() {
	    
	    @Override
	    public void onSuccess(HandleMessageTask task, Object t) {
		PreferenceHelper helper =
			    PreferenceHelper.getHelper(context);
		    Log.i(TAG, "total count: " + res.mRes_total);
		    helper.writePreference(
			    AuthorisePartnerActivity.PRE_KEY_LAST_SYNC,
			    "" + System.currentTimeMillis());
		    if (showNotification) {
			NotificationHelper.showNotification(context,
				res.mRes_total);
		    } else {
			MyToast.makeText(context,
				"已同步" + res.mRes_total + "个订单").show();
		    }
		    if (onFinish != null) { 
			onFinish.onSuccess(res.mRes_total);
		    }
	    } 
	    
	    @Override
	    public void onFail(HandleMessageTask task, Object t) {
		// TODO Auto-generated method stub
		
	    }
	});
	
	task.execute();
    }

    protected static void save2db(CustomerModel cModel, Context context) {
	CustomerHandler cHandler =
		GlobalValue.getIns().getCustomerHandler(context);
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
    
    private static class ImportTask extends WaitingTask {
	private SyncResult mRes;
	 
	    public ImportTask(Context context, SyncResult res) {
		super(context);
		mRes = res;
	    }

	    @Override
	    protected Object doInBackground() {
		HashMap<String, Customer> phoneMap =
			GlobalValue.getIns().getPhoneNameMap();
		CustomerHandler handler =
			GlobalValue.getIns().getCustomerHandler(mContext);
		int i = 0;
		int maxId = -1;
		
		int[] im = new int[2];
		im[1] = mRes.mRes_total;
		Calendar calendar = Calendar.getInstance();
		for (BDCustomer bd : mRes.mData) {
		    // Log.i(TAG, "mobile :" + bd.mUser_mobile);
		    String mobile = TelHelper.getPureTel(bd.mUser_mobile);
		    Customer c = phoneMap.get(mobile);
		    if (c != null) {
			Log.w(TAG, "bd mobile exists" + c.getName());
			continue;
		    }

		    c = new Customer();
		    String name = bd.mUser_name;
		    if (name != null
			    && name.length() > AddCustomerActivity.MAX_NAME_LENGTH) {
			name = name.substring(0, AddCustomerActivity.MAX_NAME_LENGTH);
		    }
		    c.setName(name);
		    c.setTel(mobile);
		    long time = bd.mCreate_time;
		    c.setTime(time * 1000);
		    calendar.setTimeInMillis(c.getTime());
		    Log.i(TAG, "create tiem: " + DateUtil.yyyy_MM_dd.format(calendar.getTime()) + "  mills:" + c.getTime());
		    
		    c.setLoan(bd.mLoan_limit);
		    c.setSource("融360");
		    c.setOrderNo(bd.mId);
		    String proger = mProgressMap.get(bd.mStatus);
		    if (proger != null) {
			c.setProgress(proger);
		    }
		    if (bd.mId > maxId) {
			maxId = bd.mId;
		    }
		    c.setLastFollowComment(bd.mProduct_name + "-"
			    + mUseMap.get(bd.mApplication_type) + "-"
			    + mCodeMap.get(bd.mStatus));
		    handler.insertCustomer(c);
		    i++;
		    im[0] = i;
		    publishProgress(im);
		}

		// TODO;
		Log.i(TAG, "import " + i + " orders");
		GlobalValue.getIns().init(mContext);
		PreferenceHelper.getHelper(mContext).writePreference(PRE_KEY_MAX_ID,
			String.valueOf(maxId));
		return ECode.SUCCESS;
	    }
}

    public static void deleteCustomer(Context context) {
	if (!NetUtil.isNetworkAvailable(context)) {
	    return;
	}

	final PreferenceHelper helper = PreferenceHelper.getHelper(context);
	String ids = helper.readPreference(PRE_KEY_DELETE_IDS);
	if (ids == null || ids.length() == 0) {
	    return;
	}

	String tel = helper.readPreference(AuthCodeActivity.EXTRA_TEL);
	String pass = helper.readPreference(AuthCodeActivity.EXTRA_PASS);
	if (tel == null || pass == null) {
	    return;
	}

	RequestParam params =
		new RequestParam(
			DomainHelper.getApi(DomainHelper.SUFFIX_DELETE));
	JSONObject json = new JSONObject();
	try {
	    json.put("customer_ids", ids);
	} catch (JSONException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	params.addNameValuePair("mobile", tel);
	params.addNameValuePair("password", pass);
	params.addNameValuePair("data", json.toString());
	String token = DomainHelper.getSecretToken(params);
	params.addNameValuePair("token", token);
	PostDataTask tTask = new PostDataTask(context, params);
	tTask.setCallback(new Callback() {

	    @Override
	    public void onSuccess(HandleMessageTask task, Object t) {
		try {
		    Log.i(TAG, "res:" + task.getResult());
		    TResult res =
			    JsonHelper.parseJSONToObject(TResult.class,
				    task.getResult());
		    if (res.mResult.getError() == (ECode.SUCCESS)) {
			helper.removePreference(PRE_KEY_DELETE_IDS);
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
