package com.rong360.creditassitant.model;

import java.io.Serializable;

public class HistorySms implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 91757219337302737L;
	
	private int mId;
	private String mContent;
	private long mTime;
	
	public int getId() {
		return mId;
	}
	public void setId(int mId) {
		this.mId = mId;
	}
	public String getContent() {
		return mContent;
	}
	public void setContent(String mContent) {
		this.mContent = mContent;
	}
	public long getTime() {
		return mTime;
	}
	public void setTime(long mTime) {
		this.mTime = mTime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
