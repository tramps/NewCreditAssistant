package com.rong360.creditassitant.model;

import com.rong360.creditassitant.json.JSONBean;

public class HistoryMsg implements JSONBean {
    public int mId;
    public String mMsg;
    public long mTime;
    
    public int getId() {
        return mId;
    }
    public void setId(int mId) {
        this.mId = mId;
    }
    public String getMsg() {
        return mMsg;
    }
    public void setMsg(String mMsg) {
        this.mMsg = mMsg;
    }
    public long getTime() {
        return mTime;
    }
    public void setTime(long mTime) {
        this.mTime = mTime;
    }
}
