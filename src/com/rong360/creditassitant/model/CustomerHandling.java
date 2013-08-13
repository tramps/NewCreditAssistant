package com.rong360.creditassitant.model;

import java.io.Serializable;

public class CustomerHandling implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//only once
	public static final int TYPE_MODIFY_PROGRESS = 0;  //修改进度：
	public static final int TYPE_FOLLOW = 2;			//设置提醒：
	public static final int TYPE_CANCEL_FOLLOW = 3; 	//取消提醒
	public static final int TYPE_FINISH_FOLLOW = 4;		//完成提醒：  
	
	public static final int TYPE_MODIFY_COMMENT = 1; 	//修改备注
	public static final int TYPE_MODIFIY_QUALITY = 5; 	//更改客户资质
	public static final int TYPE_NEW_CUSTOMER = 6;		//保存为客户
	
	public static final String MODIFY_PROGRESS = "进度修改为：";
	public static final String SET_FOLLOW = "设置提醒： ";
	public static final String CANCEL_FOLLOW = "取消提醒： ";
	public static final String FINISH_FOLLOW = "完成提醒： ";
	public static final String MODIFY_COMMENT = "备注修改为： ";
	public static final String MODIFY_QUANLITY = "更改客户资质： ";
	public static final String NEW_CUSTOMER = "保存为客户";
	
	private int mId;
	private int mType; //type 0~6; 
	private int mTime;
	private int mCustomerId;
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
	public int getTime() {
		return mTime;
	}
	public void setTime(int mTime) {
		this.mTime = mTime;
	}
	public int getCustomerId() {
		return mCustomerId;
	}
	public void setCustomerId(int mCustomerId) {
		this.mCustomerId = mCustomerId;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
