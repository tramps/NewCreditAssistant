package com.rong360.creditassitant.model;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rong360.creditassitant.util.DbHelper.BaseDbHandler;
import com.rong360.creditassitant.util.DbHelper.TableInfo;

public class HistoryMsgHandler extends BaseDbHandler {
    private static final String TAG = "HistoryMsgHandler";
    
    private static final String ID = "_id";
    private static final String MSG = "msg";
    private static final String TIME = "time";
    
    private static final String TABLE_NAME = "historymsg";
    private static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME
	    + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " + TIME
	    + " LONG, " + MSG + " TEXT, " 
	    + " UNIQUE (" + ID + ")" + "); ";
    
    public static TableInfo TABLE_INFO = new TableInfo(TABLE_NAME, CREATE_SQL);
    	
    public HistoryMsgHandler(Context context) {
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
    
    public ArrayList<HistoryMsg> getAllHistoryMsges() {
	String sql = "select * from historymsg order by time desc";
	ArrayList<HistoryMsg> msges = new ArrayList<HistoryMsg>();
	Cursor c = mHelper.getReadableDatabase().rawQuery(sql, null);
	
	HistoryMsg msg;
	while (c.moveToNext()) {
	    msg = makeSms(c);
	    if (msg != null) {
		msges.add(msg);
	    }
	}
	
	return msges;
    }
    
    private HistoryMsg makeSms(Cursor c) {
	if (c == null) {
	    return null;
	}
	
	HistoryMsg sms = new HistoryMsg();
	
	sms.setId(c.getInt(c.getColumnIndex(ID)));
	sms.setMsg(c.getString(c.getColumnIndex(MSG)));
	sms.setTime(c.getLong(c.getColumnIndex(TIME)));
	
	return sms;
    }
    
    public boolean insertSms(String msg) {
	ContentValues cv = new ContentValues();
	cv.put(MSG, msg);
	cv.put(TIME, System.currentTimeMillis());
	try {
	    SQLiteDatabase db = mHelper.getWritableDatabase();
	    return db.insert(TABLE_NAME, "", cv) != -1;
	} catch (Exception e) {
	    Log.w(TAG, e.toString());
	}

	return false;
    }

}
