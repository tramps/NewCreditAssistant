package com.rong360.creditassitant.model.result;

import com.rong360.creditassitant.json.JSONBean;

public class Result implements JSONBean {
    public String mError;
    public String mMsg;
    public String getError() {
        return mError;
    }
    public void setError(String mError) {
        this.mError = mError;
    }
    public String getMsg() {
        return mMsg;
    }
    public void setMsg(String mMsg) {
        this.mMsg = mMsg;
    }

}
