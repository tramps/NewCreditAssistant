package com.rong360.creditassitant.model;

import java.io.Serializable;

public class CustomerSource implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5659938812359334371L;
	
	public static final String SELF_OWNED = "self_owned";
	public static final String ON_LINE = "import";
	
	private int mId;
	private String mSource;
	public int getId() {
		return mId;
	}
	public void setId(int mId) {
		this.mId = mId;
	}
	public String getSource() {
		return mSource;
	}
	public void setSource(String mSource) {
		this.mSource = mSource;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
