package com.rong360.creditassitant.model;

import java.io.Serializable;

public class Customer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5642639408595041121L;
	
	//necessary
	private int mId;
	private String mName;
	private String mTel;
	private long mLoan;
	private long mTime;		//when customer added/revised;
	
	//optional
	private long mCash;
	private long mBank;
	private long mIdentity;
	private long mHouse;
	private long mCar;
	private long mCreditRecord;
	private long mLocation;
	
	private boolean mIsFollow;
	private boolean mHasChecked; 
	private boolean mIsFavored;
	
	private String mLastFollowComment;
	private long mAlarmTime;
	
	private String mProgress;
	private String mSource;
	
	private long mOrderNo;
	private boolean mIsImported;  //type 
	
	public long getAlarmTime() {
	    return mAlarmTime;
	}
	
	public void setAlarmTime(long alarm) {
	    mAlarmTime = alarm;
	}
	
	public String getProgress() {
		return mProgress;
	}
	
	public void setProgress(String progress) {
		mProgress = progress;
	}
	
	public String getSource() {
		return mSource;
	}
	
	public void setSource(String source) {
		mSource = source;
	}
	
	public long getOrderNo() {
		return mOrderNo;
	}
	
	public void setOrderNo(long orderNo) {
		mOrderNo = orderNo;
	}
	
	public boolean isImported() {
		return mIsImported;
	}

	public void setIsImported(boolean isImported) {
		mIsImported = isImported;
	}
	
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
	public void setTel(String mTel) {
		this.mTel = mTel;
	}
	public long getLoan() {
		return mLoan;
	}
	public void setLoan(long mLoan) {
		this.mLoan = mLoan;
	}
	public long getTime() {
		return mTime;
	}
	public void setTime(long mTime) {
		this.mTime = mTime;
	}
	public long getCash() {
		return mCash;
	}
	public void setCash(long mCash) {
		this.mCash = mCash;
	}
	public long getBank() {
		return mBank;
	}
	public void setBank(long mBank) {
		this.mBank = mBank;
	}
	public long getIdentity() {
		return mIdentity;
	}
	public void setIdentity(long mIdentity) {
		this.mIdentity = mIdentity;
	}
	public long getHouse() {
		return mHouse;
	}
	public void setHouse(long mHouse) {
		this.mHouse = mHouse;
	}
	public long getCar() {
		return mCar;
	}
	public void setCar(long mCar) {
		this.mCar = mCar;
	}
	public long getCreditRecord() {
		return mCreditRecord;
	}
	public void setCreditRecord(long mCreditRecord) {
		this.mCreditRecord = mCreditRecord;
	}
	public long getLocation() {
		return mLocation;
	}
	public void setLocation(long mLocation) {
		this.mLocation = mLocation;
	}
	
	public boolean isIsFollow() {
		return mIsFollow;
	}
	public void setIsFollow(boolean mIsFollow) {
		this.mIsFollow = mIsFollow;
	}
	public String getLastFollowComment() {
		return mLastFollowComment;
	}
	public void setLastFollowComment(String mLastFollowComment) {
		this.mLastFollowComment = mLastFollowComment;
	}
	public boolean isIsFavored() {
		return mIsFavored;
	}
	public void setIsFavored(boolean mIsFavored) {
		this.mIsFavored = mIsFavored;
	}
	public boolean isHasChecked() {
		return mHasChecked;
	}
	public void setHasChecked(boolean mHasChecked) {
		this.mHasChecked = mHasChecked;
	}
}
