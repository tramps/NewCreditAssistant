package com.rong360.creditassitant.model;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.rong360.creditassitant.util.DbHelper.BaseDbHandler;
import com.rong360.creditassitant.util.DbHelper.TableInfo;

public class CommentHandler extends BaseDbHandler {
	private static final String TAG = CommentHandler.class.getSimpleName();

	private static final String ID = "_id";
	private static final String CUSTOMER_ID = "customer_id";
	private static final String COMMENT = "comment";
	private static final String ALARM_TIME = "alarm_time";
	private static final String REVISE_TIME = "revise_time";

	private static final String TABLE_NAME = "comment";
	private static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME
			+ " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " + CUSTOMER_ID
			+ " INTEGER, " + COMMENT + " TEXT, " + ALARM_TIME + " TEXT, "
			+ REVISE_TIME + " LONG, "

			+ " UNIQUE (" + ID + ")" + "); ";
	
	private static final String NO_COMMENT = "���ޱ�ע";

	public static final TableInfo TABLE_INFO = new TableInfo(TABLE_NAME,
			CREATE_SQL);
	
	public CommentHandler(Context context) {
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

	public static Comment makeComment(Cursor c) {
		if (null == c) {
			return null;
		}

		Comment comment = new Comment();
		comment.setId(c.getInt(c.getColumnIndex(ID)));
		comment.setCustomerId(c.getInt(c.getColumnIndex(CUSTOMER_ID)));
		comment.setAlarmTime(c.getLong(c.getColumnIndex(ALARM_TIME)));
		comment.setReviseTime(c.getLong(c.getColumnIndex(REVISE_TIME)));
		comment.setComment(c.getString(c.getColumnIndex(COMMENT)));

		return comment;
	}

	public Comment getCommentById(long id) {
		String sql = "select * from " + TABLE_NAME + " where " + ID + " = ?";
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor c = db.rawQuery(sql, new String[] { String.valueOf(id) });

		if (c.moveToNext()) {
			return makeComment(c);
		}

		return null;
	}
	
	
	public Comment getLastCommenByCustomerId(int customerId) {
		String sql = "select * from " + TABLE_NAME + " where " + CUSTOMER_ID
				+ " = ? order by " + REVISE_TIME + " DESC limit 1;";
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor c = db.rawQuery(sql, new String[] { String.valueOf(customerId) });
		
		while (c.moveToNext()) {
			return makeComment(c);
		}
		return null;
	}

	public List<Comment> getAllCommentsByCustomerId(int customerId) {
		String sql = "select * from " + TABLE_NAME + " where " + CUSTOMER_ID
				+ " = ? order by " + REVISE_TIME + " DESC;";
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor c = db.rawQuery(sql, new String[] { String.valueOf(customerId) });
		
		List<Comment> comments = new ArrayList<Comment>();
		while (c.getCount() > 0 && c.moveToNext()) {
			comments.add(makeComment(c));
		}
		
		c.close();
		
		return comments;
	}
	
	public void deleteAllCommentsByCustomerId(int customerId) {
		SQLiteStatement statement = mHelper.getWritableDatabase()
				.compileStatement("delete from " + TABLE_NAME + " where customer_id = " + customerId);
		statement.execute();
		statement.close();
	}

	public boolean insertComment(Comment comment) {
		ContentValues cv = new ContentValues();
		cv.put(CUSTOMER_ID, comment.getCustomerId());
		cv.put(COMMENT, comment.getComment());
		cv.put(ALARM_TIME, comment.getAlarmTime());
		cv.put(REVISE_TIME, System.currentTimeMillis());

		try {
			SQLiteDatabase db = mHelper.getWritableDatabase();
			return db.insert(TABLE_NAME, "", cv) != -1;
		} catch (Exception e) {
			Log.w(TAG, e.toString());
		}

		return false;
	}
	
	
	public boolean updateComment(Comment comment) {
		ContentValues cv = new ContentValues();
		cv.put(ID, comment.getId());
		cv.put(CUSTOMER_ID, comment.getCustomerId());
		cv.put(COMMENT, comment.getComment());
		cv.put(ALARM_TIME, comment.getAlarmTime());
		cv.put(REVISE_TIME, System.currentTimeMillis());

		try {
			SQLiteDatabase db = mHelper.getWritableDatabase();
			return db.replace(TABLE_NAME, "", cv) != -1;
		} catch (Exception e) {
			Log.w(TAG, e.toString());
		}

		return false;
	}

}
