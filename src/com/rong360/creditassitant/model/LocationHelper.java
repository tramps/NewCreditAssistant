package com.rong360.creditassitant.model;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.util.FileUtil;
import com.rong360.creditassitant.util.LocCache;
import com.rong360.creditassitant.util.PreferenceHelper;

public class LocationHelper {
    private static final String TAG = "LocationHelper";
    
    public static final String DEFAULT_FOLER = "data";
    public static final String DEFAULT_NAME = "loc.db";

    public static final String PRE_KEY_DB = "pre_key_db";
    public static final String BACKED = "backed";

    private static final String SQL_MOBILE =
	    "select b.city from area b, loc l where l.number = ? and b._id = l.area_id";
    private static final String SQL_HOME =
	    "select city from area where code = ? or code = ?";
    private static final String SQL_ALL =
	    "select l.number, b.city from area b, loc l where b._id = l.area_id "
		    + "and l.number in ";
    private static final String SQL_AREA =
	    "select distinct(code), city from area";

    private static SQLiteDatabase mLocDb;

    public static void back2SdCard(Context context) {
	File db = getFile(context);
	Log.i(TAG, "back started");
	if (db.exists() && db.length() > 1024) {
//	    return;
	} else {
	    Log.i(TAG, "back to sd");
	    InputStream is =
		    context.getResources().openRawResource(R.raw.tightcell);
	    FileUtil.writeFile(db, is);
	    PreferenceHelper.getHelper(context).writePreference(PRE_KEY_DB,
		    BACKED);
	}
	LocationHelper.initHomeMap(context);
    }

    private static File getFile(Context context) {
	return FileUtil.getFile(context, DEFAULT_FOLER, DEFAULT_NAME);
    }

    private static SQLiteDatabase getSqliteDb(Context context) {
	if (mLocDb == null) {
	    mLocDb =
		    SQLiteDatabase.openDatabase(getFile(context).getPath(),
			    null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}
	return mLocDb;
    }

    public static void setAllMobileLoc(Context context,
	    ArrayList<Communication> coms) {
	if (coms == null || coms.size() == 0) {
	    return;
	}

	StringBuilder sb = new StringBuilder();
	sb.append("(");
	for (Communication c : coms) {
	    if (c.getTel().length() < 9) {
		continue;
	    }
	    String tel = c.getTel().replace(" ", "").substring(0, 7);
	    sb.append(tel);
	    sb.append(",");
	}
	sb = sb.deleteCharAt(sb.length() - 1);
	sb.append(");");

	String sql = SQL_ALL + sb.toString();
	Log.i(TAG, sql);
	SQLiteDatabase db;
	try {
	    db = getSqliteDb(context);
	} catch (Exception e) {
	    Log.e(TAG, e.toString());
	    return;
	}
	Cursor c = db.rawQuery(sql, null);
	LocCache cache = LocCache.getInstance();
	while (c != null && c.moveToNext()) {
	    int number = c.getInt(c.getColumnIndex("number"));
	    String city = c.getString(c.getColumnIndex("city"));
	    cache.add2Cache(number, city);
	}

	SparseArray<String> mobileMap = cache.getMap();
	SparseArray<String> areaMap = cache.getHomeMap();
	for (Communication com : coms) {
	    String tel = com.getTel().replace(" ", "");
	    if (tel.length() < 9) {
		continue;
	    }
	    if (tel.length() == 11 && tel.startsWith("1")) {
		int key = Integer.valueOf(tel.substring(0, 7));
		String va = mobileMap.get(key);
		com.setLocation(va == null ? "" : va);
	    } else {
		String fir = tel.substring(1, 3);
		String va = areaMap.get(Integer.valueOf(fir));
		if (va != null) {
		    com.setLocation(va);
		    continue;
		}
		String sec = tel.substring(1, 4);
		va = areaMap.get(Integer.valueOf(sec));
		if (va != null) {
		    com.setLocation(va);
		} else {
		    Log.e(TAG, "not found! " + tel);
		}
	    }
	}
    }

    public static void initHomeMap(Context context) {
	SQLiteDatabase db = getSqliteDb(context);
	Cursor c = db.rawQuery(SQL_AREA, null);

	SparseArray<String> homeMap = LocCache.getInstance().getHomeMap();
	while (c != null && c.moveToNext()) {
	    int key = c.getInt(c.getColumnIndex("code"));
	    String city = c.getString(c.getColumnIndex("city"));
	    if (homeMap.get(key) != null) {
		continue;
	    }
	    homeMap.put(key, city);
	}
	
	Log.i(TAG, "home map: " + homeMap.size());
    }

    public static String getAreaByNumber(Context context, String number) {
	if (number == null) {
	    return "";
	}

	String sql;
	String first, second = "";
	Cursor c = null;
	SQLiteDatabase db = getSqliteDb(context);
	LocCache cache = LocCache.getInstance();
	String key = "";
	if (number.length() == 11 && number.startsWith("1")) {
	    first = number.substring(0, 7);
	    String v = cache.getMap().get(Integer.valueOf(first));
	    if (v != null) {
		Log.i(TAG, v + "mobile cached");
		return v;
	    } else {
		sql = SQL_MOBILE;
		c = db.rawQuery(sql, new String[] { first });
		key = first;
	    }

	} else if (number.length() >= 9) {
	    sql = SQL_HOME;
	    first = number.substring(1, 3).trim();
	    second = number.substring(1, 4).trim();
	    String v = cache.getHomeMap().get(Integer.valueOf(first));
	    if (v == null) {
		v = cache.getHomeMap().get(Integer.valueOf(second));
	    }
	    if (v != null) {
		Log.i(TAG, v + "home cached");
	    }
	    return v;
	    // Log.i(TAG, first + " " + second);
	    // c = db.rawQuery(sql, new String[] { first, second });
	}
	if (c != null && c.moveToNext()) {
	    String city = c.getString(c.getColumnIndex("city"));
	    cache.add2Cache(Integer.valueOf(key), city);

	    return city;
	}

	return "";
    }
}
