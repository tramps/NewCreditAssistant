package com.rong360.creditassitant.model;

import java.io.Serializable;

public class Communication implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7494326146897333940L;
	
	public static final int TYPE_INCOMING = 1;
	public static final int TYPE_OUTCOMING = 2; 
	public static final int TYPE_MISSED = 3;
	
	//common
	private int mId; 
	private String mTel;
	private long mTime;
	private int mType;
	private String mName;
	private long mThreadId;
	
	private String mProgress;
	private String mLocation;
	
	//call
	private long mDuration;
	//sms
	private String mContent;
	
	
	public int getId() {
		return mId;
	}
	public void setId(int mId) {
		this.mId = mId;
	}
	
	public String getName() {
		return mName;
	}
	public void setName(String mName) {
		this.mName = mName;
	}
	public String getTel() {
		return mTel;
	}
	public void setTel(String tel) {
		this.mTel = tel;
	}
	public long getTime() {
		return mTime;
	}
	public void setTime(long time) {
		this.mTime = time;
	}
	public int getType() {
		return mType;
	}
	public void setType(int type) {
		this.mType = type;
	}
	
	public long getThreadId() {
		return mThreadId;
	}
	public void setThreadId(long mThreadId) {
		this.mThreadId = mThreadId;
	}
	public long getDuration() {
		return mDuration;
	}
	public void setDuration(long duration) {
		this.mDuration = duration;
	}
	public String getContent() {
		return mContent;
	}
	public void setContent(String content) {
		this.mContent = content;
	}
	
	public String getProgress() {
	    return mProgress;
	}
	public void setProgress(String mProgress) {
	    this.mProgress = mProgress;
	}
	public String getLocation() {
	    return mLocation;
	}
	public void setLocation(String mLocation) {
	    this.mLocation = mLocation;
	}
	
	
//	@Override
//	public int hashCode() {
//		return mTel.hashCode();
//	}
	
}
