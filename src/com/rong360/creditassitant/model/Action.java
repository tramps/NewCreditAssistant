package com.rong360.creditassitant.model;

import com.rong360.creditassitant.json.JSONBean;

public class Action implements JSONBean {
    public int mId;
    public int mCustomerId;
    public int mType;
    public long mTime;
    public String mContent = "";

    public Action(int customerId, int type) {
	mCustomerId = customerId;
	mType = type;
    }

    public Action() {

    }

    public int getCustomerId() {
	return mCustomerId;
    }

    public void setCustomerId(int mCustomerId) {
	this.mCustomerId = mCustomerId;
    }

    public String getContent() {
	return mContent;
    }

    public void setContent(String mContent) {
	this.mContent = mContent;
    }

    public int getId() {
	return mId;
    }

    public void setId(int mId) {
	this.mId = mId;
    }

    public int getType() {
	return mType;
    }

    public void setType(int mType) {
	this.mType = mType;
    }

    public long getTime() {
	return mTime;
    }

    public void setTime(long mTime) {
	this.mTime = mTime;
    }

}
