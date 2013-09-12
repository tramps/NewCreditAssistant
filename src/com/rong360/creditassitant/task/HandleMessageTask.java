package com.rong360.creditassitant.task;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.rong360.creditassitant.exception.CustomException;
import com.rong360.creditassitant.exception.ECode;

public abstract class HandleMessageTask extends BaseTask<Void, Object, Object> {
    private static final String TAG = "HandleMessageTask";

    private boolean mShowCodeMsg = true;
    private Map<Integer, Object> mCodeMsgs = new HashMap<Integer, Object>();
    private static Map<Integer, Object> mDefaultCodeMsg;

    protected Callback mCallback;

    static {
	mDefaultCodeMsg = new HashMap<Integer, Object>();
	// mDefaultCodeMsg.put(key, value)
    }

    public HandleMessageTask(Context context) {
	super(context);
    }

    public HandleMessageTask setCallback(Callback callback) {
	mCallback = callback;
	return this;
    }

    public void setShowCodeMsg(boolean b) {
	mShowCodeMsg = b;
    }

    public void setCodeMsg(Integer code, String msg) {
	mCodeMsgs.put(code, msg);
    }

    public void setCodeMsg(Integer code, int res) {
	mCodeMsgs.put(code, res);
    }

    public void disableCodeMsg(Integer code) {
	mCodeMsgs.put(code, "");
    }

    @Override
    protected void onPostExecute(Object result) {
	processResult(result);
	super.onPostExecute(result);
    }

    @Override
    protected Object doInBackground(Void... params) {
	return doInBackground();
    }

    protected abstract Object doInBackground();

    private void processResult(Object result) {
	if (isCancelRequested()) {
	    return;
	}

	int code = 0;
	if (null == result) {
	    result = ECode.FAIL;
	} else if (result.equals(ECode.CANCELED)) {
	    return;
	} else if (result instanceof CustomException) {
	    CustomException e = (CustomException) result;
	    code = e.getCode();
	} else if (result instanceof Integer) {
	    code = (Integer) result;
	} else {
	    code = ECode.FAIL;
	}

	String codeMsg = getCodeMsg(code);
	if (codeMsg == null) {
	    codeMsg = getCodeMsg(ECode.FAIL);
	}
	if (mShowCodeMsg && codeMsg != null && codeMsg.length() > 0) {
	    showResultMessage(codeMsg);
	}

	if (mCallback == null) {
	    return;
	}
	if (code == ECode.SUCCESS) {
	    mCallback.onSuccess(this, code);
	} else {
	    mCallback.onFail(this, code);
	}
    }

    private void showResultMessage(String codeMsg) {
	Toast.makeText(mContext, codeMsg, Toast.LENGTH_LONG).show();
	Log.i(TAG, codeMsg);
    }

    private String getCodeMsg(int code) {
	Object o = mCodeMsgs.get(code);
	if (null != o) {
	    return convertCodeMsg(o);
	} else {
	    return getDefaultCodeMsg(code);
	}
    }

    private String getDefaultCodeMsg(int code) {
	Object o = mDefaultCodeMsg.get(code);

	return convertCodeMsg(o);
    }

    private String convertCodeMsg(Object o) {
	if (null == o) {
	    return null;
	} else if (o instanceof String) {
	    return (String) o;
	} else if (o instanceof Integer) {
	    return mContext.getResources().getString((Integer) o);
	}

	return null;
    }

    public static interface Callback {
	public void onSuccess(HandleMessageTask task, Object t);

	public void onFail(HandleMessageTask task, Object t);
    }

    public String getResult() {
	return "";
    }
}
