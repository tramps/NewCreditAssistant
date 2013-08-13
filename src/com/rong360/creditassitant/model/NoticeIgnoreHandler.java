package com.rong360.creditassitant.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.rong360.creditassitant.util.DbHelper.BaseDbHandler;
import com.rong360.creditassitant.util.DbHelper.TableInfo;

public class NoticeIgnoreHandler extends BaseDbHandler {
	private static final String TAG = NoticeIgnoreHandler.class.getSimpleName();
	public static int RESHOW_THRESHOLD = 5;
	
	private static final String ID = "_id";
	private static final String TEL = "tel";
	private static final String COUNT = "count";
			
	private static final String TABLE_NAME = "ignore";
	private static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME
			+ " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " + TEL
			+ " TEXT, " + COUNT + " INTEGER);";
	
	public static final TableInfo TABLE_INFO = new TableInfo(TABLE_NAME, CREATE_SQL);

	public NoticeIgnoreHandler(Context context) {
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
	
	public static NoticeIgnore makeNoticeIgnore(Cursor c) {
		if (null == c) {
			return null;
		}

		NoticeIgnore ignore = new NoticeIgnore();
		ignore.setId(c.getInt(c.getColumnIndex(ID)));
		ignore.setTel(c.getString(c.getColumnIndex(TEL)));
		ignore.setCount(c.getInt(c.getColumnIndex(COUNT)));
		
		return ignore;
	}

	public NoticeIgnore getIgnoreByTel(String tel) {
		String slq = "select * from " + TABLE_NAME + " where " + TEL
					+ " = ? ";
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor c = db.rawQuery(slq, new String[]{tel});
		if (c.moveToNext()) {
			return makeNoticeIgnore(c);
		}
		
		return null;
	}
	
	public void remove(NoticeIgnore ignore) {
		SQLiteStatement statement = mHelper.getWritableDatabase()
				.compileStatement("delete from " + TABLE_NAME + " where _id = " + ignore.getId());
		statement.execute();
	}
	
	public boolean updateIgnore(NoticeIgnore ignore) {
		ContentValues values = new ContentValues();
		values.put(ID, ignore.getId());
		values.put(TEL, ignore.getTel());
		values.put(COUNT, ignore.getCount());
		
		Log.i(TAG, "tel:" + ignore.getTel() + " count:" + ignore.getCount());
		SQLiteDatabase db = mHelper.getWritableDatabase();
		try {
			return db.replace(TABLE_NAME, "", values) != -1;
		} catch (Exception e) {
			Log.w(TAG, "updateIgnore" + e.toString());
		}
		
		return false;
	}
	
	public boolean insertIgnore(NoticeIgnore ignore) {
		ContentValues values = new ContentValues();
		values.put(TEL, ignore.getTel());
		values.put(COUNT, ignore.getCount());
		
		SQLiteDatabase db = mHelper.getWritableDatabase();
		try {
			return db.insert(TABLE_NAME, "", values) != -1;
		} catch (Exception e) {
			Log.w(TAG, "insert " + e.toString());
		}
		
		return false;
	}

}
