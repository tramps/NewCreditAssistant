package com.rong360.creditassitant.task;

import com.rong360.creditassitant.exception.ECode;
import com.rong360.creditassitant.task.BaseHttpsManager.RequestParam;

import android.content.Context;
import android.util.Log;

public class PostDataTask extends HandleMessageTask {

    private static final String TAG = "PostDataTask";
    private RequestParam mParams;
    private String mResult;

    public PostDataTask(Context context, RequestParam param) {
	super(context);
	setShowProgressDialog(false);
	mParams = param;
	Log.i(TAG, "post url: " + param.getUrl());
    }

    @Override
    protected Object doInBackground() {
	if (mParams != null) {
	    try {
		mResult = BaseHttpsManager.executePostRequest(mParams);
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
