package com.rong360.creditassitant.util;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

public class LocCache {
    private static final String TAG = "LocCache";
    
    private static LocCache mCache;
    private SparseArray<String> mNumCodeMap;
    private SparseArray<String> mHomeMap;
    
    private static final String PREFERENCE_NAME = "pre_cache_name";
    private static final String PRE_KEY_CACHE = "pre_key_cache";
    
    public static LocCache getInstance() {
	if (mCache == null) {
	    mCache = new LocCache();
	}
	
	return mCache;
    }
    
    public SparseArray<String> getMap() {
	if (mNumCodeMap == null) {
	    mNumCodeMap = new SparseArray<String>();
	}
	
	return mNumCodeMap;
    }
    
    public SparseArray<String> getHomeMap() {
	if (mHomeMap == null) {
	    mHomeMap = new SparseArray<String>();
	}
	
	return mHomeMap;
    }
    
    private void initHomeMap() {
	
    }
    
    public void saveCache(Context context) {
	StringBuilder sb = new StringBuilder();
	for (int i = 0; i < mNumCodeMap.size(); i++) {
	    int key = mNumCodeMap.keyAt(i);
	    String v = mNumCodeMap.valueAt(i);
	    sb.append(key).append(",").append(v).append("#");
	}
	
	Log.i(TAG, "map size: " + mNumCodeMap.size());
	PreferenceHelper.getHelper(context, PREFERENCE_NAME).writePreference(PRE_KEY_CACHE, sb.toString());
    }
    
    public void restoreCache(Context context) {
	String cache = PreferenceHelper.getHelper(context, PREFERENCE_NAME).readPreference(PRE_KEY_CACHE);
	if (cache == null) {
	    return;
	}
	String[] maps = cache.split("#");
	SparseArray<String> sa = getMap();
	for (String map : maps) {
	    String[] keyValue = map.split(",");
	    if (keyValue.length != 2) {
		continue;
	    }
	    sa.put(Integer.valueOf(keyValue[0]), keyValue[1]);
	}
	
	Log.i(TAG, "map size: " + mNumCodeMap.size());
    }
    
    public void add2Cache(int key, String loc) {
	if (mNumCodeMap == null) {
	    mNumCodeMap = new SparseArray<String>();
	}
	mNumCodeMap.put(key, loc);
    }

}
