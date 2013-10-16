package com.rong360.creditassitant.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.rong360.creditassitant.util.DbHelper.BaseDbHandler;
import com.rong360.creditassitant.util.DbHelper.TableInfo;

public class ActionHandler extends BaseDbHandler {
    private static final String TAG = "ActionHandler";

    public static final int TYPE_NEW = 0;
    public static final int TYPE_COMMENT = 1;
    public static final int TYPE_QUALITY = 2;

    public static final int TYPE_PROGRESS = 6;
    public static final int TYPE_SET_ALARM = 3;
    public static final int TYPE_CANCEL_ALARM = 4;
    public static final int TYPE_FINISH_ALARM = 5;

    public static final String PREFIX_NEW = "保存为客户";
    public static final String PREFIX_COMMENT = "备注修改为：";
    public static final String PREFIX_QUALITY = "更改客户资质：";
    public static final String PREFIX_PROGRESS = "进度修改为：";
    public static final String PREFIX_SET_ALARM = "设置提醒：";
    public static final String PREFIX_CANCEL_ALARM = "取消提醒";
    public static final String PREFIX_FINISH_ALARM = "完成提醒：";

    private static final String ID = "_id";
    private static final String CUSTOMER_ID = "customer_id";
    private static final String TYPE = "type";
    private static final String CONTENT = "content";
    private static final String TIME = "time";

    public static HashMap<Integer, String> mHintMap;

    static {
	mHintMap = new HashMap<Integer, String>(10);
	mHintMap.put(TYPE_NEW, PREFIX_NEW);
	mHintMap.put(TYPE_COMMENT, PREFIX_COMMENT);
	mHintMap.put(TYPE_QUALITY, PREFIX_QUALITY);
	mHintMap.put(TYPE_PROGRESS, PREFIX_PROGRESS);
	mHintMap.put(TYPE_SET_ALARM, PREFIX_SET_ALARM);
	mHintMap.put(TYPE_CANCEL_ALARM, PREFIX_CANCEL_ALARM);
	mHintMap.put(TYPE_FINISH_ALARM, PREFIX_FINISH_ALARM);
    }

    private static final String TABLE_NAME = "action";
    private static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME
	    + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
	    + CUSTOMER_ID + " INTEGER, " +  CONTENT + " TEXT, " + TIME
	    + " LONG, " + TYPE + " INTEGER, " + " UNIQUE (" + ID + ")" + "); ";

    public static TableInfo TABLE_INFO = new TableInfo(TABLE_NAME, CREATE_SQL);

    public ActionHandler(Context context) {
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

    public ArrayList<Action> getAllActionById(int customerId) {
	String sql = "select * from action where customer_id = ? order by time desc";
	ArrayList<Action> actions = new ArrayList<Action>();
	Cursor c =
		mHelper.getReadableDatabase().rawQuery(sql,
			new String[] { String.valueOf(customerId) });

	Action a;
	while (c.moveToNext()) {
	    a = makeAction(c);
	    if (a != null) {
		actions.add(a);
	    }
	}

	return actions;
    }
    
    public ArrayList<Action> getUpdateActions(long updateTime) {
	String sql = "select * from action where time > ?";
	ArrayList<Action> actions = new ArrayList<Action>();
	Cursor c =
		mHelper.getReadableDatabase().rawQuery(sql,
			new String[] { String.valueOf(updateTime) });

	Action a;
	while (c.moveToNext()) {
	    a = makeAction(c);
	    if (a != null) {
		actions.add(a);
	    }
	}

	return actions;
    }

    private Action makeAction(Cursor c) {
	if (c == null) {
	    return null;
	}

	Action action = new Action();

	action.setId(c.getInt(c.getColumnIndex(ID)));
	action.setType(c.getInt(c.getColumnIndex(TYPE)));
	action.setTime(c.getLong(c.getColumnIndex(TIME)));
	action.setContent(c.getString(c.getColumnIndex(CONTENT)));
	action.setCustomerId(c.getInt(c.getColumnIndex(CUSTOMER_ID)));

	return action;
    }

    public boolean insertAction(Action action) {
	ContentValues cv = new ContentValues();
	cv.put(TYPE, action.getType());
	cv.put(CUSTOMER_ID, action.getCustomerId());
	cv.put(CONTENT, mHintMap.get(action.getType()) + action.getContent());
	cv.put(TIME, System.currentTimeMillis());
	try {
	    SQLiteDatabase db = mHelper.getWritableDatabase();
	    return db.insert(TABLE_NAME, "", cv) != -1;
	} catch (Exception e) {
	    Log.w(TAG, e.toString());
	}

	return false;
    }

    public boolean updateAction(Action action) {
	ContentValues cv = new ContentValues();
	cv.put(ID, action.getId());
	cv.put(CUSTOMER_ID, action.getCustomerId());
	cv.put(TYPE, action.getType());
	cv.put(CONTENT, mHintMap.get(action.getType()) + action.getContent());
	cv.put(TIME, System.currentTimeMillis());
	try {
	    SQLiteDatabase db = mHelper.getWritableDatabase();
	    return db.replace(TABLE_NAME, "", cv) != -1;
	} catch (Exception e) {
	    Log.w(TAG, e.toString());
	}

	return false;
    }

    public Action getActionByType(Action action) {
	int type = action.getType();
	if (type < TYPE_SET_ALARM) {
	    return null;
	}
	Calendar calc = Calendar.getInstance();
//	calc.setTimeInMillis(action.getTime());
	calc.set(Calendar.HOUR_OF_DAY, 0);
	calc.set(Calendar.MINUTE, 0);
	calc.set(Calendar.MILLISECOND, 0);
	long small = calc.getTimeInMillis();
	calc.roll(Calendar.DAY_OF_MONTH, 1);
	long big = calc.getTimeInMillis();
	String sql =
		"select * from action where customer_id = ? and type = ? and (time > ? and time < ?)";
	SQLiteDatabase db = mHelper.getReadableDatabase();
	Cursor c =
		db.rawQuery(
			sql,
			new String[] { String.valueOf(action.getCustomerId()), String.valueOf(type),
				String.valueOf(small), String.valueOf(big) });
	if (c.moveToNext()) {
	    return makeAction(c);
	}
	return null;
    }

    public boolean handleAction(Action action) {
	Action dbAction = getActionByType(action);
	if (dbAction == null) {
	    return insertAction(action);
	} else {
	    action.setId(dbAction.getId());
	    return updateAction(action);
	}
    }

    public void deleteAction(int customerId) {
	SQLiteStatement statement =
		mHelper.getWritableDatabase().compileStatement(
			"delete from " + TABLE_NAME + " where customer_id = "
				+ customerId);
	statement.execute();
    }
}
