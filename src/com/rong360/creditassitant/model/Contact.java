package com.rong360.creditassitant.model;

import java.io.Serializable;

import com.rong360.creditassitant.util.SpellConverter;

public class Contact implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3489923237997620284L;

	private String mTel;
	private String mName;
	private long mCreateTime;
	
	public String getTel() {
		return mTel;
	}
	public void setTel(String mTel) {
		this.mTel = mTel;
	}
	public String getSpell() {
	    if (mName == null) {
		return "#";
	    } else {
		return SpellConverter.getSpells(mName).getFirstSpell();
	    }
	}
	
	public String getName() {
	    return mName;
	}
	public void setName(String mName) {
		this.mName = mName;
	}
	public long getCreateTime() {
		return mCreateTime;
	}
	public void setCreateTime(long mCreateTime) {
		this.mCreateTime = mCreateTime;
	}
	
	
}
