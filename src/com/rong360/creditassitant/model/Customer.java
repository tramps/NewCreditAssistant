package com.rong360.creditassitant.model;

import com.rong360.creditassitant.json.JSONBean;

public class Customer implements JSONBean {

    /**
	 * 
	 */
    private static final long serialVersionUID = 5642639408595041121L;

    // necessary
    public int mId;
    public String mName;
    public String mTel;
    public long mLoan;
    public long mTime; // when customer added/revised;

    // optional
    public int mCash = -1;
    public int mBank = -1;
    public int mIdentity = -1;
    public int mHouse = -1;
    public int mCar = -1;
    public int mCreditRecord = -1;

    public boolean mIsFollow;
    public boolean mHasChecked;
    public boolean mIsFavored;
    private boolean mIsDisplayed;

    public String mLastFollowComment;
    public long mAlarmTime;

    public String mProgress="";
    public String mSource;

    public long mOrderNo;
    public boolean mIsImported; // type

    public boolean IsDisplayed() {
	return mIsDisplayed;
    }

    public void setIsDisplayed(boolean mIsDisplayed) {
	this.mIsDisplayed = mIsDisplayed;
    }

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

    public int getCash() {
	return mCash;
    }

    public void setCash(int mCash) {
	this.mCash = mCash;
    }

    public int getBank() {
	return mBank;
    }

    public void setBank(int mBank) {
	this.mBank = mBank;
    }

    public int getIdentity() {
	return mIdentity;
    }

    public void setIdentity(int mIdentity) {
	this.mIdentity = mIdentity;
    }

    public int getHouse() {
	return mHouse;
    }

    public void setHouse(int mHouse) {
	this.mHouse = mHouse;
    }

    public int getCar() {
	return mCar;
    }

    public void setCar(int mCar) {
	this.mCar = mCar;
    }

    public int getCreditRecord() {
	return mCreditRecord;
    }

    public void setCreditRecord(int mCreditRecord) {
	this.mCreditRecord = mCreditRecord;
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
