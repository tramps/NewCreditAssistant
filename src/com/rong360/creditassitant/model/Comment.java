package com.rong360.creditassitant.model;

import java.io.Serializable;

public class Comment implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7240494628876494609L;
	
	private int mId;
	private int mCustomerId;
	private String mComment;
	private long mAlarmTime;
	private long mReviseTime;			//when comment add/revised ;
	
	
	public int getId() {
		return mId;
	}
	public void setId(int mId) {
		this.mId = mId;
	}
	public int getCustomerId() {
		return mCustomerId;
	}
	public void setCustomerId(int mCustomerId) {
		this.mCustomerId = mCustomerId;
	}
	public String getComment() {
		return mComment;
	}
	public void setComment(String mComment) {
		this.mComment = mComment;
	}
	public long getAlarmTime() {
		return mAlarmTime;
	}
	public void setAlarmTime(long mAlarmTime) {
		this.mAlarmTime = mAlarmTime;
	}
	public long getReviseTime() {
		return mReviseTime;
	}
	public void setReviseTime(long mReviseTime) {
		this.mReviseTime = mReviseTime;
	}
	
}
