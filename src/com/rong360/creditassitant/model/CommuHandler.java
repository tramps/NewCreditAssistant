package com.rong360.creditassitant.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.ModelHeler;

public class CommuHandler {
    private static final String TAG = CommuHandler.class.getSimpleName();

    private static final String SMS_URI = "content://sms/";

    private static final String ADDRESS = "address";
    private static final String BODY = "body";
    private static final String DATE = "date";
    private static final String TYPE = "type";
    private static final String THREAD_ID = "thread_id";

    private static final Uri smsUri = Uri.parse(SMS_URI);

    private static String[] smsProjection = new String[] { ADDRESS, BODY, DATE,
	    TYPE, THREAD_ID };
    private static String[] callLogProjection = new String[] {
	    CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE,
	    CallLog.Calls.DURATION };

    private static String[] contactProjection = new String[] {
	    ContactsContract.CommonDataKinds.Phone.NUMBER,
	    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
	    ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED };

    private static final String MAX_DATE = "max_date";

    // private static String[] allCallLogProjection = new String[] { "date",
    // CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DURATION };

    public static ArrayList<Communication> getAllSmsByTel(Context context,
	    String tel) {
	ArrayList<Communication> comms = new ArrayList<Communication>();
	String pureTem = TelHelper.getPureTel(tel);
	Cursor c =
		context.getContentResolver().query(smsUri, smsProjection,
			"address like \'%" + pureTem + "\'", null, "date desc");

	Communication com;
	while (c.moveToNext()) {
	    com = buildSms(c);
	    comms.add(com);
	}
	c.close();
	Log.i(TAG, "sms size" + comms.size());
	return comms;
    }

    private static Communication buildCallLog(Cursor c) {
	Communication com = new Communication();
	String tel = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
	com.setTel(TelHelper.getPureTel(tel));
	com.setTime(c.getLong(c.getColumnIndex(CallLog.Calls.DATE)));
	com.setType(c.getInt(c.getColumnIndex(CallLog.Calls.TYPE)));
	com.setDuration(c.getLong(c.getColumnIndex(CallLog.Calls.DURATION)));
	return com;
    }

    private static Communication buildSms(Cursor c) {
	Communication com = new Communication();
	String tel = c.getString(c.getColumnIndex(ADDRESS));
	com.setTel(TelHelper.getPureTel(tel));
	com.setTime(c.getLong(c.getColumnIndex(DATE)));
	com.setType(c.getInt(c.getColumnIndex(TYPE)));
	com.setContent(c.getString(c.getColumnIndex(BODY)));
	com.setThreadId(c.getLong(c.getColumnIndex(THREAD_ID)));
	return com;
    }

    public static ArrayList<Communication> getAllCallLog(Context context) {
	ArrayList<Communication> comms = new ArrayList<Communication>();
	Cursor c =
		context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
			callLogProjection, null, null,
			CallLog.Calls.DEFAULT_SORT_ORDER);

	HashMap<String, Customer> phone2NameMap =
		GlobalValue.getIns().getPhoneNameMap();
	ArrayList<String> contacts =
		GlobalValue.getIns().getContactPhones(context);
	HashSet<String> phones = new HashSet<String>();
	Communication com;
	Customer customer;
	while (c.moveToNext()) {
	    com = buildCallLog(c);
	    // if (contacts.contains(com.getTel())) {
	    // continue;
	    // }

	    if (phones.contains(com.getTel())) {
		continue;
	    } else {
		phones.add(com.getTel());
	    }

	    // Log.i(TAG, com.getTel());
	    if (phone2NameMap.containsKey(com.getTel())) {
		customer = phone2NameMap.get(com.getTel());
		// Log.i(TAG, customer.getName() + customer.getTel());
		com.setName(customer.getName());
		com.setId(customer.getId());
		if (customer.getProgress() != null) {
		    com.setProgress(customer.getProgress());
		} 
//		else {
//		    com.setLocation(LocationHelper.getAreaByNumber(context,
//			    com.getTel()));
//		}
	    } else {
		com.setId(-1);
//		com.setLocation(LocationHelper.getAreaByNumber(context,
//			    com.getTel()));
	    }
	    
	    comms.add(com);
	}
	c.close();

	Log.i(TAG, "get all call log: " + comms.size());
	
	GlobalValue.getIns().setNeedUpdateCommunication(false);
	return comms;
    }
    
    public static void setNewAddName(String tel, Context context) {
	GlobalValue.getIns().setNeedUpdateCommunication(true);
	return;
//	ArrayList<Communication> coms = GlobalValue.getIns().getAllComunication(context);
//	Communication com = null;
//	for (Communication c : coms) {
//	    if (ModelHeler.isTelEqual(tel, c.getTel())) {
//		com = c;
//		break;
//	    }
//	}
//	
//	if (com != null) {
//	    HashMap<String, Customer> phoneNameMap = GlobalValue.getIns().getPhoneNameMap();
//	    Customer c = phoneNameMap.get(tel);
//	    if (c != null) {
//		com.setName(c.getName());
//		if (c.getProgress() != null) {
//		    com.setProgress(c.getProgress());
//		}
//		com.setId(c.getId());
//	    }
//	}
    }
    
    public static void removeNameByPhone(String tel, Context context) {
	GlobalValue.getIns().setNeedUpdateCommunication(true);
//	ArrayList<Communication> coms = GlobalValue.getIns().getAllComunication(context);
//	for (Communication c : coms) {
//	    if (ModelHeler.isTelEqual(tel, c.getTel())) {
//		c.setId(-1);
//		break;
//	    }
//	}
//	coms.remove(com);
    }

    public static ArrayList<Communication> getCallLogByTel(Context context,
	    String tel) {
	ArrayList<Communication> comms = new ArrayList<Communication>();
	String pureTem = TelHelper.getPureTel(tel);
	Cursor c =
		context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
			callLogProjection, "number like \'%" + pureTem + "\'",
			null, CallLog.Calls.DEFAULT_SORT_ORDER);
	Communication com;
	while (c.moveToNext()) {
	    com = buildCallLog(c);
//	    Log.i(TAG, tel);
	    comms.add(com);
	}
	c.close();

	return comms;
    }

    public static Communication getLastCallOfTel(Context context, String tel) {
	String pureTel = TelHelper.getPureTel(tel);
	Cursor c =
		context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
			callLogProjection, "number like \'%" + pureTel + "\'",
			null, CallLog.Calls.DEFAULT_SORT_ORDER + " limit 1");
	Communication com = null;
	while (c.moveToNext()) {
	    com = buildCallLog(c);
	    break;
	}
	c.close();

	return com;
    }

    public static ArrayList<String> getAllContactsPhone(Context context) {
	ArrayList<String> phones = new ArrayList<String>();
	Cursor c =
		context.getContentResolver().query(
			ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
			null, null, null, null);
	String tempTel;
	if (c != null) {
	    while (c.moveToNext()) {
		tempTel =
			c.getString(c
				.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		phones.add(TelHelper.getPureTel(tempTel));
	    }
	    c.close();
	}

//	Log.i(TAG, "contacts: " + phones.toString());
	return phones;
    }

    public static ArrayList<Contact> getAllContacts(Context context) {
	ArrayList<Contact> contacts = new ArrayList<Contact>();
	Cursor c =
		context.getContentResolver().query(
			ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
			contactProjection, null, null,
			"sort_key COLLATE LOCALIZED asc");
	if (c != null) {
	    while (c.moveToNext()) {
		Contact contact = new Contact();
		contact.setCreateTime(c.getLong(c
			.getColumnIndex(contactProjection[2])));
		contact.setName(c.getString(c
			.getColumnIndex(contactProjection[1])));
		contact.setTel(c.getString(c
			.getColumnIndex(contactProjection[0])));

		contacts.add(contact);
	    }

	    c.close();
	}

	Log.i(TAG, "contacts: " + contacts.size());
	return contacts;
    }

}
