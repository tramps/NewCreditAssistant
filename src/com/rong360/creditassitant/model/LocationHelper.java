package com.rong360.creditassitant.model;

import java.io.File;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.FileUtil;
import com.rong360.creditassitant.util.PreferenceHelper;

public class LocationHelper {
    public static final String DEFAULT_FOLER = "data";
    public static final String DEFAULT_NAME = "loc.db";
    
    public static final String PRE_KEY_DB = "pre_key_db";
    public static final String BACKED = "backed";
    
    private static final String SQL_MOBILE = "select b.city from area b, loc l where l.number = ? and b._id = l.area_id";
    private static final String SQL_HOME = "select city from area where code = ? or code = ?";
    private static final String TAG = "LocationHelper";
    
    public static void back2SdCard(Context context) {
	File db = getFile(context);
	if (db.exists() && db.length() > 1024) {
	    return;
	} else {
	    InputStream is = context.getResources().openRawResource(R.raw.tightcell);
	    FileUtil.writeFile(db, is);
	    PreferenceHelper.getHelper(context).writePreference(PRE_KEY_DB, BACKED);
	}
    }
    
    private static File getFile(Context context) {
	return FileUtil.getFile(context, DEFAULT_FOLER, DEFAULT_NAME);
    }
    
    private static SQLiteDatabase getSqliteDb(Context context) {
	SQLiteDatabase db = SQLiteDatabase.openDatabase(getFile(context).getPath(), null, SQLiteDatabase.OPEN_READONLY);
	return db;
    }
    
    public static  String getAreaByNumber(Context context, String number) {
	if (number == null){
	    return "";
	}
	
	
//	String sql;
//	String first, second = "";
//	Cursor c = null;
//	SQLiteDatabase db = getSqliteDb(context);
//	if (number.length() == 11 && number.startsWith("1")) {
//	    first = number.substring(0, 7);
//	    sql = SQL_MOBILE;
//	    c = db.rawQuery(sql, new String[]{first});
//	} else if (number.length() > 10) {
//	    sql = SQL_HOME;
//	    first = number.substring(1, 3);
//	    second = number.substring(1, 4);
//	    Log.i(TAG, first + " " + second);
//	    c = db.rawQuery(sql, new String[]{first, second});
//	}
//	if (c != null && c.moveToNext()) {
//	    return c.getString(c.getColumnIndex("city"));
//	}
	return "";
    }
}
