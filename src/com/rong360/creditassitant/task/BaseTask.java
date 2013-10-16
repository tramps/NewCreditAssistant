package com.rong360.creditassitant.task;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

public abstract class BaseTask<A, B, C> extends AsyncTask<A, B, C> {
    public static interface OnTaskCancelListener {
	public void onCancel(BaseTask task);
    }

    private static final String TAG = BaseTask.class.getSimpleName();
    protected Context mContext;
    protected Resources mRes;

    private boolean mFinishActivityOnCancel = false;
    private boolean mFinishTaskOnCancel = true;
    private boolean mTaskCancelAble = true;

    private boolean mShowProgressDialog = true;
    private WaitDialog mProgressDialog;

    private String mMessage;
    private boolean mIsCanceled;

    public BaseTask(Context context) {
	this(context, false);
    }

    public BaseTask(Context context, boolean finishActivityOnCancel) {
	this.mFinishActivityOnCancel = finishActivityOnCancel;
	this.mContext = context;
	mRes = context.getResources();
	mMessage = "连接服务器，请等待..";
    }

    public void setFinishActivityOnCancel(boolean mFinishActivityOnCancel) {
	this.mFinishActivityOnCancel = mFinishActivityOnCancel;
    }

    public void setFinishTaskOnCancel(boolean mFinishTaskOnCancel) {
	this.mFinishTaskOnCancel = mFinishTaskOnCancel;
    }

    public void setTaskCancelAble(boolean mTaskCancelAble) {
	this.mTaskCancelAble = mTaskCancelAble;
    }

    public void setShowProgressDialog(boolean mShowProgressDialog) {
	this.mShowProgressDialog = mShowProgressDialog;
    }

    public void setMessage(String mMessage) {
	this.mMessage = mMessage;
    }

    @Override
    protected void onPreExecute() {
	super.onPreExecute();
	if (mShowProgressDialog) {
	    showDialog();
	}
    }

    @Override
    protected void onCancelled() {
	dismissDialog();
    }

    protected void onPostExecute(C result) {
	dismissDialog();
    }

    private void dismissDialog() {
	try {
	    if (mProgressDialog != null && mProgressDialog.isShowing()) {
		mProgressDialog.dismiss();
		mProgressDialog = null;
	    }
	} catch (Exception e) {
	    Log.e(TAG, e.getMessage());
	}
    }

    public void cancel() {
	super.cancel(true);
	mIsCanceled = true;
    }

    public boolean isCancelRequested() {
	return mIsCanceled;
    }

    private void showDialog() {
	try {
	    if (mProgressDialog == null) {
		mProgressDialog = createLoadingDialog();
		mProgressDialog.setCancelable(mTaskCancelAble);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {

		    @Override
		    public void onCancel(DialogInterface dialog) {
			if (mFinishActivityOnCancel) {
			    finishContext();
			}

			if (mFinishTaskOnCancel) {
			    cancel(true);
			    if (mOnCancelListener != null) {
				mOnCancelListener.onCancel(BaseTask.this);
			    }
			}

		    }
		});
		if (!mProgressDialog.isShowing()) {
		    mProgressDialog.show();
		    mProgressDialog.setMessage(mMessage);
		}
		
	    }
	} catch (Exception e) {
	    Log.e(TAG, e.toString());
	}

    }

    public void finishContext() {
	if (mContext instanceof Activity) {
	    ((Activity) mContext).onBackPressed();
	}
    }

    protected WaitDialog createLoadingDialog() {
	return new WaitDialog(mContext);
    }


    public void setLoadingMessage(String message) {
	mMessage = message;
	mProgressDialog.setMessage(message);
    }

    private Context getTopParent(Activity context) {
	Activity parent = context.getParent();
	while (parent != null) {
	    context = parent;
	    parent = context.getParent();
	}

	return context;
    }

    private OnTaskCancelListener mOnCancelListener;

    public void setOnCancelListener(OnTaskCancelListener l) {
	mOnCancelListener = l;
    }

}
