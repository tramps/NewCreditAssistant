package com.rong360.creditassitant.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.http.util.CharArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

import com.rong360.creditassitant.R;
import com.rong360.creditassitant.exception.JsonParseException;
import com.rong360.creditassitant.util.FileUtil;
import com.rong360.creditassitant.util.GlobalValue;

public class JsonHelper {
    private static final String TAG = "JsonHelper";
//    private static final int RAW_FILE = R.raw.jsoninternal;
    private static final String REMOVE_PATTERN = "\"\\d+\":";

    public static void parseJSONFromString(String json)
	    throws JsonParseException {
//	try {
//	    JSONObject jsob = new JSONObject(json);
//	    GlobalValue global = GlobalValue.getIns()();
//	    global.clear();
//	    if (jsob.has(HpCity.KEY)) {
//		String value = jsob.getString(HpCity.KEY);
//		try {
//		    parseJSONToList(HpCity.class, global.getCities(),
//			    new JSONArray(value));
//		} catch (Exception e) {
//		    Log.e(TAG, "hpcity json error");
//		    Log.e(TAG, e.toString());
//		    value = value.replaceAll(REMOVE_PATTERN, "");
//		    value = value.substring(1, value.length() - 1);
//		    value = "[" + value + "]";
//		    parseJSONToList(HpCity.class, global.getCities(),
//			    new JSONArray(value));
//		}
//	    } else {
//		throw new JsonParseException("no hpcity data");
//	    }
//	    if (jsob.has(CityElement.KEY)) {
//		String value = jsob.getString(CityElement.KEY);
//		parseJSONToList(CityElement.class, global.getElements(),
//			new JSONArray(value));
//	    } else {
//		throw new JsonParseException("no city element data");
//	    }
//
//	    if (jsob.has(CityPolicy.KEY)) {
//		String value = jsob.getString(CityPolicy.KEY);
//		parseJSONToList(CityPolicy.class, global.getPolicies(),
//			new JSONArray(value));
//	    } else {
//		throw new JsonParseException("no city policy data");
//	    }
//
//	    if (jsob.has(CalcDesc.KEY)) {
//		String value = jsob.getString(CalcDesc.KEY);
//		parseJSONToList(CalcDesc.class, global.getCalcDesc(),
//			new JSONArray(value));
//	    } else {
//		throw new JsonParseException("no calc desc data");
//	    }
//
//	} catch (JSONException e) {
//	    throw new JsonParseException("input json format error!", e);
//	}
    }

//    public static void parseJSONFromModel(Context context)
//	    throws JsonParseException {
//	File model = NetHelper.getJsonModelFile(context);
//	String json = FileUtil.readFileContent(model);
//	if (model == null) {
//	    throw new JsonParseException("exception model");
//	} else if (json == null) {
//	    throw new JsonParseException("exception read json");
//	}
//
//	parseJSONFromString(json);
//    }

    public static void parseJSONFromRaw(Context context)
	    throws JsonParseException {
	Resources res = context.getResources();
	BufferedReader reader = null;
	try {
	    reader =
		    new BufferedReader(new InputStreamReader(
			    res.openRawResource(R.raw.tightcell), "UTF-8"));
	} catch (UnsupportedEncodingException e) {
	    Log.e(TAG, e.getMessage());
	    Log.e(TAG, e.getCause().toString());
	} catch (NotFoundException e) {
	    Log.e(TAG, e.getMessage());
	    Log.e(TAG, e.getCause().toString());
	}

	if (reader == null) {
	    return;
	}

	CharArrayBuffer buffer = new CharArrayBuffer(4096);
	try {
	    char[] tmp = new char[1024];
	    int l;
	    while ((l = reader.read(tmp)) != -1) {
		buffer.append(tmp, 0, l);
	    }
	} catch (Exception e) {
	    throw new JsonParseException("read file erroe", e);
	} finally {
	    try {
		reader.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	parseJSONFromString(buffer.toString());
    }

    public static <T extends JSONBean> T parseJSONToObject(Class<T> tlass,
	    String json) throws JsonParseException {
	try {
	    if (isChild(tlass, JSONArrayBean.class)) {
		Class<? extends JSONArrayBean<? extends JSONBean>> arrayClass =
			(Class<? extends JSONArrayBean<? extends JSONBean>>) tlass;
		return (T) parseJSONToObject(arrayClass, new JSONArray(json));
	    } else
		return parseJSONToObject(tlass, new JSONObject(json));
	} catch (Exception e) {
	    throw new JsonParseException("input json format error", e);
	}
    }

    /** child extend or implements parent */
    private static boolean isChild(Class<?> child, Class<?> parent) {
	boolean isChild = false;
	Class<?> c = child;
	while (c != null) {
	    if (c == parent || contain(c.getInterfaces(), parent))
		return true;
	    c = c.getSuperclass();
	}
	return isChild;
    }

    private static boolean contain(Object[] list, Object obj) {
	for (Object o : list)
	    if (o == obj)
		return true;
	return false;
    }

    public static <T extends JSONArrayBean> T parseJSONToObject(Class<T> tlass,
	    JSONArray jsArray) throws JsonParseException {
	try {
	    T list = tlass.newInstance();
	    for (int i = 0; i < jsArray.length(); i++) {
		JSONBean item =
			parseJSONToObject(list.getGenericClass(),
				jsArray.getJSONObject(i));
		list.add(item);
	    }
	    return list;
	} catch (Exception e) {
	    throw new JsonParseException("Class: " + tlass.getName()
		    + "can not be instantiate.", e);
	}
    }

    public static <T extends JSONBean> List<T> parseJSONToList(Class<T> tlass,
	    List<T> list, JSONArray jsArray) throws JsonParseException {
	for (int i = 0; i < jsArray.length(); i++) {
	    try {
		T item = parseJSONToObject(tlass, jsArray.getJSONObject(i));
		list.add(item);
	    } catch (Exception e) {
		throw new JsonParseException("class: " + tlass.getName()
			+ "parse error.", e);
	    }
	}

	return list;
    }

    public static <T extends JSONBean> T parseJSONToObject(Class<T> klass,
	    JSONObject jsObj) throws JsonParseException {
	if (jsObj == null)
	    return null;
	T instance = null;
	try {
	    instance = klass.newInstance();
	} catch (Exception e) {
	    throw new JsonParseException(
		    "can not create instance for " + klass, e);
	}
	Field[] fields = klass.getFields();

	boolean optional = false;
	String key = null;
	Object value = null;
	Class<?> fType = null;

	for (Field member : fields) {
	    if (!Modifier.isPublic(member.getModifiers())
		    || Modifier.isStatic(member.getModifiers()))
		continue;
	    optional = containAnnotation(member, Optional.class);
	    if (member instanceof Field) {
		Field field = (Field) member;
		fType = field.getType();
		key = field.getName().substring(1).toLowerCase();
	    } else
		continue;
	    if (jsObj.has(key)) {
		try {
		    value = jsObj.get(key);
		} catch (JSONException e) {
		    Log.e(TAG, "", e);
		}
		try {
		    Object newValue = wrapToObject(fType, value);
		    if (member instanceof Field) {
			Field field = (Field) member;
			field.set(instance, newValue);
		    }
		} catch (Exception e) {
		    Log.e(TAG, "Error in setting Value: " + value
			    + ", Method: " + member.getName(), e);
		}
	    } else {
		if (!optional) {
		    Log.e(TAG, member.getDeclaringClass().getName() + "." + key
			    + " doesn't exist in json object");
		}
	    }
	}
	return instance;
    }

    private static Object wrapToObject(Class<?> fType, Object value)
	    throws JsonParseException {
	try {
	    String typeName = fType.getSimpleName();
	    if (typeName.equals("int") || typeName.equals("Integer")) {
		return Integer.valueOf(value.toString());
	    } else if (typeName.equals("boolean") || typeName.equals("Boolean")) {
		return Boolean.valueOf(value.toString());
	    } else if (typeName.equals("double") || typeName.equals("Double")) {
		return Double.valueOf(value.toString());
	    } else if (typeName.equals("String")) {
		return value.toString();
	    } else if (typeName.equals("Date")) {
		Timestamp t = Timestamp.valueOf(value.toString());
		return new Date(t.getTime());
	    } else if (typeName.equals("String[]")) {
		String[] strs = null;
		JSONArray ja = new JSONArray(value.toString());
		int count = ja.length();
		strs = new String[count];
		for (int i = 0; i < count; i++)
		    strs[i] = ja.getString(i);
		return strs;
	    } else if (typeName.equals("int[]")) {
		int[] ints = null;
		JSONArray ja = new JSONArray(value.toString());
		int count = ja.length();
		ints = new int[count];
		for (int i = 0; i < count; i++)
		    ints[i] = ja.getInt(i);
		return ints;
	    } else if (fType.isArray()) {
		String gName = fType.toString();
		int s = gName.indexOf("[L") + 2;
		int e = gName.indexOf(";");
		gName = gName.substring(s, e);
		Class<? extends JSONBean> subC =
			(Class<? extends JSONBean>) Class.forName(gName);
		JSONArray ja = new JSONArray(value.toString());
		int count = ja.length();
		Object objs = Array.newInstance(subC, count);
		for (int i = 0; i < count; i++) {
		    JSONObject jsob = ja.getJSONObject(i);
		    JSONBean subInstance = parseJSONToObject(subC, jsob);
		    Array.set(objs, i, subInstance);
		}
		return objs;
	    } else {
		Class<? extends JSONBean> cla =
			(Class<? extends JSONBean>) fType;
		JSONObject jsob = new JSONObject(value.toString());
		JSONBean obj = parseJSONToObject(cla, jsob);
		return obj;
	    }
	} catch (Exception e) {
	    throw new JsonParseException(e);
	}
    }

    public static JSONObject parseObjectToJSON(Object instance) {
	Field[] fields = instance.getClass().getFields();
	Method[] methods = instance.getClass().getMethods();
	String key = null;
	Object value = null;
	JSONObject res = new JSONObject();
	boolean discard;
	for (Field member : fields) {
	    try {
		if (!Modifier.isPublic(member.getModifiers())
			|| Modifier.isStatic(member.getModifiers()))
		    continue;
		discard = containAnnotation(member, Discard.class);
		if (discard)
		    continue;
		key = member.getName().substring(1);
		if (member instanceof Field) {
		    Field field = (Field) member;
		    value = field.get(instance);
		}
		value = wrapToJSON(value);
		res.putOpt(key, value);
	    } catch (Exception e) {
		Log.e(TAG,
			"fail in parsing field/value : " + key + "/" + value, e);
	    }
	}
	return res;
    }

    public static <T extends JSONBean> JSONArray parseListToJsonArray(
	    List<T> list) {
	JSONArray ja = new JSONArray();
	int i = 0;
	for (T t : list) {
	    try {
		ja.put(i++, parseObjectToJSON(t));
	    } catch (JSONException e) {
		e.printStackTrace();
	    }
	}
	return ja;
    }

    private static Object wrapToJSON(Object object) {
	try {
	    if (object == null) {
		return null;
	    }
	    if (object instanceof JSONObject || object instanceof JSONArray
		    || object instanceof Byte || object instanceof Character
		    || object instanceof Short || object instanceof Integer
		    || object instanceof Long || object instanceof Boolean
		    || object instanceof Float || object instanceof Double
		    || object instanceof String) {
		return object;
	    }

	    if (object instanceof Date) {
		Date d = (Date) object;
		Timestamp t = new Timestamp(d.getTime());
		return t.toString();
	    }

	    if (object.getClass().isArray()) {
		JSONArray array = new JSONArray();
		int length = Array.getLength(object);
		for (int i = 0; i < length; i += 1) {
		    array.put(wrapToJSON(Array.get(object, i)));
		}
		return array;
	    }

	    Package objectPackage = object.getClass().getPackage();
	    String objectPackageName =
		    objectPackage != null ? objectPackage.getName() : "";
	    if (objectPackageName.startsWith("java.")
		    || objectPackageName.startsWith("javax.")
		    || object.getClass().getClassLoader() == null) {
		return object.toString();
	    }
	    return parseObjectToJSON(object);
	} catch (Exception e) {
	    Log.e(TAG, "error in wrapingToJSON", e);
	    return null;
	}
    }

    private static boolean containAnnotation(Member member,
	    Class<? extends Annotation> anno) {
	if (member instanceof Method) {
	    Method method = (Method) member;
	    return method.getAnnotation(anno) != null;
	} else if (member instanceof Field) {
	    Field field = (Field) member;
	    return field.getAnnotation(anno) != null;
	}
	return false;
    }
}
