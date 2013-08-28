package com.rong360.creditassitant.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.rong360.creditassitant.model.ActionHandler;
import com.rong360.creditassitant.model.CommentHandler;
import com.rong360.creditassitant.model.CustomerHandler;
import com.rong360.creditassitant.model.HistoryMsgHandler;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();
    private static final int mVersion = 1;
    private static final String mDBName = "Rong";
    private static List<TableInfo> mInfos = new ArrayList<TableInfo>();

    static {
	mInfos.add(CustomerHandler.TABLE_INFO);
	mInfos.add(CommentHandler.TABLE_INFO);
	mInfos.add(HistoryMsgHandler.TABLE_INFO);
	mInfos.add(ActionHandler.TABLE_INFO);
	// mInfos.add(NoticeIgnoreHandler.TABLE_INFO);
    }

    public DbHelper(Context context) {
	super(context, mDBName, null, mVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
	for (TableInfo info : mInfos) {
	    try {
		Log.i(TAG, "create table: " + info.tableName);
		db.execSQL(info.createSql);
	    } catch (SQLException e) {
		Log.w(TAG, "oncreate", e);
	    }
	}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	for (TableInfo info : mInfos) {
	    try {
		Log.i(TAG, "create table: update " + info.tableName);
		db.execSQL("DROP TABLE IF EXISTS " + info.tableName);
	    } catch (SQLException e) {
		Log.w(TAG, "onUpgrade", e);
	    }

	    onCreate(db);
	}
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	onUpgrade(db, oldVersion, newVersion);
    }

    public static class TableInfo {
	public String tableName;
	public String createSql;

	public TableInfo(String table, String sql) {
	    tableName = table;
	    createSql = sql;
	}

	@Override
	public boolean equals(Object o) {
	    if (o instanceof TableInfo) {
		return tableName != null
			&& tableName.equals(((TableInfo) o).tableName);
	    }

	    return super.equals(o);
	}

    }

    public abstract static class BaseDbHandler {
	protected DbHelper mHelper;
	protected Context mContext;

	public BaseDbHandler(Context context) {
	    mContext = context;
	    mHelper = new DbHelper(context);
	}

	protected abstract String getTablename();

	protected abstract String getCreateSql();

	public void deleteAll() {
	    SQLiteStatement statement =
		    mHelper.getWritableDatabase().compileStatement(
			    "delete from " + getTablename());
	    statement.execute();
	}

	public int getCount() {
	    SQLiteStatement statement =
		    mHelper.getReadableDatabase().compileStatement(
			    "select count(*) from " + getTablename());
	    long count = statement.simpleQueryForLong();
	    statement.close();

	    return (int) count;
	}

	public void close() {
	    mHelper.close();
	}

    }
}
