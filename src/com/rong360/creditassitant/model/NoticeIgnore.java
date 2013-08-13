package com.rong360.creditassitant.model;

import java.io.Serializable;

public class NoticeIgnore implements Serializable{
	private int id;
	private String mTel;
	private int Count;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTel() {
		return mTel;
	}
	public void setTel(String mTel) {
		this.mTel = mTel;
	}
	public int getCount() {
		return Count;
	}
	public void setCount(int count) {
		this.Count = count;
	}
	
}
