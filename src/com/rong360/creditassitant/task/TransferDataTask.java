package com.rong360.creditassitant.task;

import android.content.Context;
import android.util.Log;

import com.rong360.creditassitant.exception.ECode;

public class TransferDataTask extends HandleMessageTask {
    private static final String TAG = "TransferDataTask";
    private String mUrl;
    private String mResult;

    public TransferDataTask(Context context, String url) {
	super(context);
	setShowProgressDialog(true);
	mUrl = url;
	Log.i(TAG, "url: " + mUrl);
    }

    @Override
    protected Object doInBackground() {
	if (mUrl != null) {
	    try {
		mResult = BaseHttpsManager.executeGetRequest(mUrl);
	    } catch (Exception e) {
		Log.e(TAG, e.toString());
		return ECode.FAIL;
	    }
	}

	return ECode.SUCCESS;
    }

    @Override
    public String getResult() {
	return mResult;
    }
}
