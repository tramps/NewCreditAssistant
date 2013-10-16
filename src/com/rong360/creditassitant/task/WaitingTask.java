package com.rong360.creditassitant.task;

import java.util.Arrays;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

public abstract class WaitingTask extends HandleMessageTask {

    public WaitingTask(Context context) {
	super(context);
	mCheckNet = false;
	// TODO Auto-generated constructor stub
    }
    
    @Override
    protected WaitDialog createLoadingDialog() {
	return new WaitDialog(mContext);
    }
    
    @Override
    protected void onProgressUpdate(Object... values) {
	int[] im = (int[]) values[0];
	Log.i("waiting", "values" + Arrays.toString(im));
        setLoadingMessage("正在导入(" + im[0] + "/" + im[1] + ")");
    }

}
