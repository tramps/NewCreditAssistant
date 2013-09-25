package com.rong360.creditassitant.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.rong360.creditassitant.util.DbHelper.BaseDbHandler;
import com.rong360.creditassitant.util.DbHelper.TableInfo;
import com.rong360.creditassitant.util.GlobalValue;
import com.rong360.creditassitant.util.ModelHeler;

public class CustomerHandler extends BaseDbHandler {
    private static final String TAG = CustomerHandler.class.getSimpleName();

    private static final String ID = "_id";
    private static final String NAME = "name";
    private static final String TEL = "tel";
    private static final String LOAN = "loan";

    public static final String BANK = "bank";
    public static final String CASH = "cash";
    public static final String IDENTITY = "identity";
    public static final String HOUSE = "house";
    public static final String CAR = "car";
    public static final String CREDIT_RECORD = "credit_record";

    public static final String TIME = "time";
    public static final String ALARM_TIME = "alarm_time";

    public static final String PROGRESS = "progress";
    public static final String SOURCE = "source";
    private static final String ORDER_NO = "order_no";

    private static final String IS_FOLLOW = "is_follow";
    public static final String IS_FAVORED = "is_favored";
    private static final String LAST_FOLLOW_COMMENT = "last_follow_comment";
    private static final String HAS_CHECKED = "has_checked";

    private static final String TABLE_NAME = "customer";
    private static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME
	    + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " + TIME
	    + " LONG, " + NAME + " TEXT, " + TEL + " TEXT, " + LOAN + " LONG, "
	    + BANK + " INTEGER, " + CASH + " INTEGER, " + IDENTITY + " INTEGER, "
	    + HOUSE + " INTEGER, " + CAR + " INTEGER, " + CREDIT_RECORD + " INTEGER, "
	    + IS_FOLLOW + " INTEGER, " + IS_FAVORED
	    + " INTEGER, " + HAS_CHECKED + " INTEGER, " + PROGRESS + " TEXT, "
	    + SOURCE + " TEXT, " + ORDER_NO + " TEXT, " + LAST_FOLLOW_COMMENT
	    + " TEXT, " + ALARM_TIME + " LONG, "

	    + " UNIQUE (" + ID + ")" + "); ";

    public static TableInfo TABLE_INFO = new TableInfo(TABLE_NAME, CREATE_SQL);

    public CustomerHandler(Context context) {
	super(context);
    }

    @Override
    protected String getTablename() {
	return TABLE_NAME;
    }

    @Override
    protected String getCreateSql() {
	return CREATE_SQL;
    }

    public static Customer makeCustomer(Cursor c) {
	if (null == c) {
	    return null;
	}

	Customer customer = new Customer();
	customer.setId(c.getInt(c.getColumnIndex(ID)));
	customer.setName(c.getString(c.getColumnIndex(NAME)));
	customer.setTel(c.getString(c.getColumnIndex(TEL)));
	customer.setLoan(c.getLong(c.getColumnIndex(LOAN)));
	customer.setBank(c.getInt(c.getColumnIndex(BANK)));
	customer.setCash(c.getInt(c.getColumnIndex(CASH)));
	customer.setIdentity(c.getInt(c.getColumnIndex(IDENTITY)));
	customer.setHouse(c.getInt(c.getColumnIndex(HOUSE)));
	customer.setCar(c.getInt(c.getColumnIndex(CAR)));
	customer.setCreditRecord(c.getInt(c.getColumnIndex(CREDIT_RECORD)));
	customer.setTime(c.getLong(c.getColumnIndex(TIME)));
	customer.setIsFollow(c.getInt(c.getColumnIndex(IS_FOLLOW)) == 1);
	customer.setIsFavored(c.getInt(c.getColumnIndex(IS_FAVORED)) == 1);
	customer.setHasChecked(c.getInt(c.getColumnIndex(HAS_CHECKED)) == 1);
	customer.setLastFollowComment(c.getString(c
		.getColumnIndex(LAST_FOLLOW_COMMENT)));
	customer.setAlarmTime(c.getLong(c.getColumnIndex(ALARM_TIME)));

	customer.setProgress(c.getString(c.getColumnIndex(PROGRESS)));
	customer.setSource(c.getString(c.getColumnIndex(SOURCE)));
	customer.setOrderNo(c.getInt(c.getColumnIndex(ORDER_NO)));

	return customer;
    }

    public Customer getCustomerById(long id) {
	String sql = "select * from " + TABLE_NAME + " where " + ID + " = ?";
	SQLiteDatabase db = mHelper.getReadableDatabase();
	Cursor c = db.rawQuery(sql, new String[] { String.valueOf(id) });

	if (c.moveToNext()) {
	    return makeCustomer(c);
	}

	return null;
    }
    
    public ArrayList<Customer> getCustomerByFilter (String filter) {
	String slq = "select * from " + TABLE_NAME + " where " + filter;
	SQLiteDatabase db = mHelper.getReadableDatabase();
	Cursor c = db.rawQuery(slq, null);
	
	ArrayList<Customer> cuses = new ArrayList<Customer>();
	
	Customer customer;
	while (c.moveToNext()) {
	    customer = makeCustomer(c);
	    if (customer != null) {
		cuses.add(customer);
	    }
	}
	
	return cuses;
    }

    public Customer getCustomerByNameAndTel(String name, String tel) {
	String sql =
		"select * from " + TABLE_NAME + " where " + NAME + " = ? and "
			+ TEL + " = ? ";
	SQLiteDatabase db = mHelper.getReadableDatabase();
	Cursor c = db.rawQuery(sql, new String[] { name, tel });

	if (c.moveToNext()) {
	    return makeCustomer(c);
	}

	return null;
    }

    public Customer getCustomerByTel(String tel) {
	ArrayList<Customer> customers = GlobalValue.getIns().getAllCustomers();
	for (Customer c : customers) {
	    if (ModelHeler.isTelEqual(tel, c.getTel())) {
		return c;
	    }
	}
	String slq = "select * from " + TABLE_NAME + " where " + TEL + " = ? ";
	SQLiteDatabase db = mHelper.getReadableDatabase();
	Cursor c = db.rawQuery(slq, new String[] { tel });
	if (c.moveToNext()) {
	    return makeCustomer(c);
	}

	return null;
    }

    public int getMaxId() {
	SQLiteDatabase db = mHelper.getWritableDatabase();
	String sql = "select max(_id) _id from " + TABLE_NAME;
	Cursor c = db.rawQuery(sql, null);
	if (c.moveToNext())
	    return c.getInt(c.getColumnIndex(ID));
	else
	    return 0;
    }

    private Cursor queryAllCustomers() {
	SQLiteDatabase db = mHelper.getWritableDatabase();
	String sql = "select * from customer order by time desc";
	return db.rawQuery(sql, null);
    }

    public ArrayList<Customer> getAllCustomers() {
	Cursor c = queryAllCustomers();
	ArrayList<Customer> rs = new ArrayList<Customer>();

	Customer customer;
	while (c.moveToNext()) {
	    customer = makeCustomer(c);
	    if (customer != null) {
		rs.add(customer);
	    }
	}

	return rs;
    }

    public Cursor getAllFavoredCustomers() {
	SQLiteDatabase db = mHelper.getReadableDatabase();
	String sql =
		"select * from customer where is_favored = 1 order by time desc";
	return db.rawQuery(sql, null);
    }

    public int getFavoredCount() {
	SQLiteStatement statement =
		mHelper.getWritableDatabase().compileStatement(
			"select count(*) from customer where is_favored = 1");
	long count = statement.simpleQueryForLong();
	statement.close();
	return (int) count;
    }

    public boolean updateCustomer(Customer customer) {
	ContentValues cv = new ContentValues();
	cv.put(ID, customer.getId());
	cv.put(NAME, customer.getName());
	cv.put(TEL, customer.getTel());
	cv.put(LOAN, customer.getLoan());
	cv.put(BANK, customer.getBank());
	cv.put(CASH, customer.getCash());
	cv.put(IDENTITY, customer.getIdentity());
	cv.put(HOUSE, customer.getHouse());
	cv.put(CAR, customer.getCar());
	cv.put(CREDIT_RECORD, customer.getCreditRecord());
	cv.put(TIME, System.currentTimeMillis());
	cv.put(IS_FOLLOW, customer.isIsFollow());
	cv.put(IS_FAVORED, customer.isIsFavored());
	cv.put(HAS_CHECKED, customer.isHasChecked());
	cv.put(LAST_FOLLOW_COMMENT, customer.getLastFollowComment());
	cv.put(ALARM_TIME, customer.getAlarmTime());
	cv.put(PROGRESS, customer.getProgress());
	cv.put(SOURCE, customer.getSource());
	cv.put(ORDER_NO, customer.getOrderNo());

	try {
	    SQLiteDatabase db = mHelper.getWritableDatabase();
	    return db.replace(TABLE_NAME, "", cv) != -1;
	} catch (Exception e) {
	    Log.w(TAG, e.toString());
	}

	return false;
    }
    
    public void removeSourceByName(String source) {
	String sql = "update customer set source = '' where source = '" + source + "'";
	try {
	    SQLiteDatabase  db = mHelper.getWritableDatabase();
	    db.execSQL(sql);
	} catch (Exception e) {
	    Log.w(TAG, e.toString());
	}
    }

    public void deleteCustomer(int customerId) {
	SQLiteStatement statement =
		mHelper.getWritableDatabase().compileStatement(
			"delete from " + TABLE_NAME + " where _id = "
				+ customerId);
	statement.execute();
    }

    public boolean insertCustomer(Customer customer) {
	ContentValues cv = new ContentValues();
	cv.put(NAME, customer.getName());
	cv.put(TEL, customer.getTel());
	cv.put(LOAN, customer.getLoan());
	cv.put(BANK, customer.getBank());
	cv.put(CASH, customer.getCash());
	cv.put(IDENTITY, customer.getIdentity());
	cv.put(HOUSE, customer.getHouse());
	cv.put(CAR, customer.getCar());
	cv.put(CREDIT_RECORD, customer.getCreditRecord());
	if (customer.getTime() == 0) {
	    cv.put(TIME, System.currentTimeMillis());
	} else {
	    cv.put(TIME, customer.getTime());
	}
	cv.put(IS_FOLLOW, customer.isIsFollow());
	cv.put(IS_FAVORED, customer.isIsFavored());
	cv.put(HAS_CHECKED, customer.isHasChecked());
	cv.put(LAST_FOLLOW_COMMENT, customer.getLastFollowComment());
	cv.put(ALARM_TIME, customer.getAlarmTime());
	if (customer.getProgress() == null) {
	    cv.put(PROGRESS, "潜在客户");
	} else {
	    cv.put(PROGRESS, customer.getProgress());
	}
	cv.put(SOURCE, customer.getSource());
	cv.put(ORDER_NO, customer.getOrderNo());

	try {
	    SQLiteDatabase db = mHelper.getWritableDatabase();
	    return db.insert(TABLE_NAME, "", cv) != -1;
	} catch (Exception e) {
	    Log.w(TAG, e.toString());
	}

	return false;
    }

}
