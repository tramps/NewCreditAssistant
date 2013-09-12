package com.rong360.creditassitant.task;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import com.rong360.creditassitant.exception.CustomException;
import com.rong360.creditassitant.json.JSONBean;
import com.rong360.creditassitant.json.JsonHelper;
import com.rong360.creditassitant.task.BaseHttpsManager.RequestParam;
import com.rong360.creditassitant.util.NetUtil;

/**
 * contain the base methods to invoke api via http. only for android.
 */
public class BaseHttpsManager {
    private static final String TAG = "BaseHttpsManager";
    /** connection timeout duration. */
    private static int TIMEOUT_CONNECTION = 20000;
    /** socket timeout duration */
    private static int TIMEOUT_SOCKET = 20000;

    protected static final String TAG_PARAM = "param";
    protected static final String PARAM_SERVER_API = "api";
    protected static final String PARAM_COMPRESS = "compress";

    protected static Context mContext;

    private static HttpParams mHttpParams;

    /**
     * must be called before invoke the class.
     * 
     * @param context
     */
    public static void init(Context context) {
	mContext = context.getApplicationContext();
	initHttpParameters(context);
    }

    /**
     * init the httpParam. add proxy to param if the network is cmwap.
     */
    private static void initHttpParameters(Context context) {
	if (mHttpParams == null) {
	    mHttpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(mHttpParams,
		    TIMEOUT_CONNECTION);
	    HttpConnectionParams.setSoTimeout(mHttpParams, TIMEOUT_SOCKET);
	    if (NetUtil.isNetworkCmwap(context)
		    && mHttpParams.getParameter(ConnRoutePNames.DEFAULT_PROXY) == null) {
		HttpHost proxy =
			new HttpHost(android.net.Proxy.getDefaultHost(),
				android.net.Proxy.getDefaultPort(), "http");
		Log.d(TAG, proxy.toHostString());
		mHttpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	    }
	}
    }

    public static final String REQUEST_LOG_FORMAT = "LEN:%.1f, %s";
    public static final String RESPONSE_LOG_FORMAT = "LEN:%.1f,%s return %s";

    /**
     * post a api to urlStr
     * 
     * @param urlStr
     *            the url to post the param
     * @param params
     *            the request param.
     * @return result of response
     * @throws CustomException
     */
    public static String executeRequest(RequestParam params)
	    throws CustomException {
	String responseStr = null;
	try {
	    String urlContent = params.toUrlWithParam(true);
	    Log.i(TAG, String.format(REQUEST_LOG_FORMAT,
		    urlContent.length() / 1024f, urlContent));
	    switch (params.getHttpMethod()) {
	    case RequestParam.METHOD_GET:
		responseStr = executeGetRequest(params);
		break;
	    case RequestParam.METHOD_POST:
		responseStr = executePostRequest(params);
		break;
	    }
	    float contentLength = responseStr.length() / 1024f;
	    Log.i(TAG,
		    String.format(RESPONSE_LOG_FORMAT, contentLength,
			    params.getUrl(), subString(responseStr, 100)));
	} catch (Exception e) {
	    Log.e(TAG, "Error in http request", e);
	    throw new CustomException("Error in http request", e);
	}
	return responseStr;
    }

    public static String executePostRequest(RequestParam params)
	    throws ParseException, IOException {
	String urlStr = params.getUrl();
	String responseStr =
		executePostRequest(urlStr, (List<NameValuePair>) params);
	return responseStr;
    }

    public static String executePostRequest(String urlStr,
	    List<NameValuePair> params) throws ParseException, IOException {
	HttpEntity requestEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
	return executePostRequest(urlStr, requestEntity);
    }

    private static String executePostRequest(String urlStr,
	    HttpEntity requestEntity) throws ParseException, IOException {
	if (mContext == null)
	    throw new RuntimeException("Must call init() first");
	if (urlStr == null || requestEntity == null)
	    throw new RuntimeException("Param url or entity is null.");
	HttpPost httpRequest = new HttpPost(urlStr);
	httpRequest.setHeader("Accept-Encoding", "gzip");
	httpRequest.setEntity(requestEntity);
	HttpResponse httpResponse =
		new DefaultHttpClient(mHttpParams).execute(httpRequest);
	String responseStr = null;
	if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	    responseStr = parseHttpResponse(httpResponse);
	}
	return responseStr;
    }

    public static String executeGetRequest(RequestParam param)
	    throws ParseException, IOException {
	return executeGetRequest(param.toUrlWithParam(true));
    }

    public static String executeGetRequest(String url) throws ParseException,
	    IOException {
	return executeGetRequest(URI.create(url));
    }

    public static String executeGetRequest(URI urlStr) throws ParseException,
	    IOException {
	if (mContext == null)
	    throw new RuntimeException("Must call init() first");
	HttpGet httpRequest = new HttpGet(urlStr);
	httpRequest.setHeader("Accept-Encoding", "gzip");
	HttpResponse httpResponse =
		new DefaultHttpClient(mHttpParams).execute(httpRequest);
	String responseStr = null;
	if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	    responseStr = parseHttpResponse(httpResponse);
	}
	return responseStr;
    }

    private static String parseHttpResponse(HttpResponse httpResponse)
	    throws ParseException, IOException {
	HttpEntity responseEntity = httpResponse.getEntity();
	InputStream is = responseEntity.getContent();
	if (isEncodingGZIP(httpResponse)) {
	    is = new GZIPInputStream(is);
	}
	return NetUtil.entityStreamToString(is,
		EntityUtils.getContentCharSet(responseEntity));
    }

    private static boolean isEncodingGZIP(HttpResponse httpResponse) {
	Header[] headers = httpResponse.getHeaders("Content-Encoding");
	if (headers != null && headers.length > 0) {
	    for (Header h : headers)
		if (h.getValue().indexOf("gzip") >= 0)
		    return true;
	}
	return false;
    }

    private static String subString(String str, int maxSize) {
	int len = str.length();
	if (len <= maxSize)
	    return str;
	return str.substring(0, maxSize).replace("\n", "\t") + "...";
    }

    /**
     * get http response of a remote file.
     * 
     * @param fileUrl
     * @return
     */
    public static HttpResponse getHttpResponse(String fileUrl) {
	HttpGet httpRequest = new HttpGet(fileUrl);
	HttpResponse httpResponse = null;
	try {
	    httpResponse =
		    new DefaultHttpClient(mHttpParams).execute(httpRequest);
	} catch (Exception e) {
	    Log.e(TAG, "error in getting respponse for " + fileUrl, e);
	}
	return httpResponse;
    }

    public static <T extends JSONBean> T processApi(RequestParam param,
	    Class res) throws CustomException {
	String result = executeRequest(param);
	return (T) JsonHelper.parseJSONToObject(res, result);
    }

    public static <T extends JSONBean> T processApi(String url, Class res)
	    throws CustomException {
	String result = null;
	try {
	    result = executeGetRequest(url);
	} catch (Exception e) {
	    Log.e(TAG, e.getMessage());
	    throw new CustomException("Error in http request", e);
	}
	return (T) JsonHelper.parseJSONToObject(res, result);
    }

    /**
     * base class tools that wrap the params for
     * {@link BaseHttpsManager#postAPI(Context, String, RequestParam, int)}
     */
    public static class RequestParam extends ArrayList<NameValuePair> {
	private static final long serialVersionUID = 1L;
	public static final int METHOD_POST = 0;
	public static final int METHOD_GET = 1;
	// used for cache.
	protected Map<String, Boolean> isOptionalMap;
	protected String mUrl;
	protected int mHttpMethod;

	public RequestParam(String url) {
	    mUrl = url;
	    mHttpMethod = METHOD_POST;
	    isOptionalMap = new HashMap<String, Boolean>();
	}

	public RequestParam() {
	    mUrl = "";
	    mHttpMethod = METHOD_POST;
	    isOptionalMap = new HashMap<String, Boolean>();
	}

	public int getHttpMethod() {
	    return mHttpMethod;
	}

	public void setHttpMethod(int method) {
	    mHttpMethod = method;
	}

	public void addNameValuePair(String key, Object value) {
	    addNameValuePair(key, value, false);
	}

	public void
		addNameValuePair(String key, Object value, boolean optional) {
	    if (value != null && key != null)
		add(new SingleNameValuePair(key, value.toString()));
	    isOptionalMap.put(key, optional);
	}

	public Object getParamValue(String key) {
	    NameValuePair n = getNameValuePair(key);
	    if (n != null)
		return n.getValue();
	    return null;
	}

	public NameValuePair getNameValuePair(String key) {
	    for (NameValuePair n : this)
		if (n.getName().equals(key))
		    return n;
	    return null;
	}

	@Override
	public boolean add(NameValuePair object) {
	    remove(object);
	    return super.add(object);
	}

	public String getUrl() {
	    return mUrl;
	}

	/** combine url and params. */
	public String toUrlWithParam(boolean withOptional) {
	    String url = mUrl;
	    String param = getParamStr(withOptional);
	    if (url.indexOf('?') >= 0) {
		url += "&" + param;
	    } else {
		url += "?" + param;
	    }
	    return url;
	}

	private String getParamStr(boolean withOptional) {
	    StringBuffer sb = new StringBuffer();
	    for (NameValuePair p : this) {
		boolean isOptional = isOptionalMap.get(p.getName());
		if (!isOptional || withOptional)
		    sb.append(p.getName() + "=" + p.getValue() + "&");
	    }
	    String res = sb.toString();
	    if (res.length() > 0)
		res = res.substring(0, res.length() - 1);
	    return res;
	}

	public String getParamStr() {
	    StringBuilder sb = new StringBuilder();
	    for (NameValuePair p : this) {
		sb.append(p.getName()).append("=").append(p.getValue())
			.append("&");
	    }

	    if (sb.length() > 0) {
		sb.deleteCharAt(sb.length() - 1);
	    }

	    return sb.toString();
	}

	@Override
	public String toString() {
	    return toUrlWithParam(true);
	}

    }

    private static class SingleNameValuePair extends BasicNameValuePair {

	public SingleNameValuePair(String name, String value) {
	    super(name, value);
	}

	@Override
	public boolean equals(Object object) {
	    if (object instanceof NameValuePair) {
		String name = ((NameValuePair) object).getName();
		if (name != null && name.equals(getName()))
		    return true;
	    }
	    return false;
	}

    }

}
