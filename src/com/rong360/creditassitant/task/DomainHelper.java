package com.rong360.creditassitant.task;

import java.security.MessageDigest;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.http.NameValuePair;

import android.util.Log;

import com.rong360.creditassitant.task.BaseHttpsManager.RequestParam;

public class DomainHelper {
    private static final String DOMAIN = "http://10.10.10.35/appapi";

    public static final String SUFFIX_GET_AUTH_CODE = "/getauthcode?";
    public static final String SUFFIX_REGISTER = "/register?";
    public static final String SUFFIX_LOGIN = "/login?";
    public static final String SUFFIX_SYNC_ORDER = "/syncorder?";
    public static final String SUFFIX_BACKUP = "/backup?";
    public static final String SUFFIX_RECOVER = "/recover?";
    public static final String SUFFIX_FIND_PASS = "/findPassword?";
    public static final String SUFFIX_DELETE = "/deleteCustomer?";
    
    
    private static final String SECRET_TOKEN = "@rong360#ryj-app";

    private static final String TAG = "DomainHelper";

    public static String getFullUrl(String suffix, RequestParam param) {
	StringBuilder sb = new StringBuilder(DOMAIN);
	sb.append(suffix);
	sb.append(getSecureParams(param));
	return sb.toString();
    }
    
    public static String getApi(String suffix) {
	return DOMAIN + suffix;
    }

    public static String getSecureParams(RequestParam param) {
	String p = param.getParamStr();
	return p + "&token=" + getSignature(param, SECRET_TOKEN);
    }
    
    public static String getSecretToken(RequestParam params) {
	return getSignature(params, SECRET_TOKEN);
    }

    public static String getSignature(RequestParam params, String secret) {
	// 先将参数以其参数名的字典序升序进行排序
	Map<String, String> sortedParams = new TreeMap<String, String>();
	for (NameValuePair p : params) {
	    sortedParams.put(p.getName(), p.getValue());
	}
	Set<Entry<String, String>> entrys = sortedParams.entrySet();

	// 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
	StringBuilder basestring = new StringBuilder();
	for (Entry<String, String> param : entrys) {
	    basestring.append(param.getKey()).append("=")
		    .append(param.getValue()).append("&");
	}
	basestring.append(secret);
	
	Log.i(TAG, basestring.toString());

	// 使用MD5对待签名串求签
	byte[] bytes = null;
	try {
	    MessageDigest md5 = MessageDigest.getInstance("MD5");
	    bytes = md5.digest(basestring.toString().getBytes("UTF-8"));
	} catch (Exception ex) {
	    Log.e(TAG, ex.toString());
	}

	// 将MD5输出的二进制结果转换为小写的十六进制
	StringBuilder sign = new StringBuilder();
	for (int i = 0; i < bytes.length; i++) {
	    String hex = Integer.toHexString(bytes[i] & 0xFF);
	    if (hex.length() == 1) {
		sign.append("0");
	    }
	    sign.append(hex);
	}
	return sign.toString();
    }

}
