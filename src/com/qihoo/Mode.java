package com.qihoo;

import java.sql.Time;
import java.util.Date;

/**
 * 心情类
 * 
 * @author Administrator
 *
 */
public class Mode {

	/**
	 * 心情主键
	 */
	private String MODE_ID;
	
	/**
	 * 用户账户
	 */
	private String ACCOUNT;
	
	/**
	 * 心情内容
	 */
	private String MODE_INFO;
	
	/**
	 * 墙的状态码
	 */
	private String WALL_CODE;
	
	/**
	 * 心情状态码
	 */
	private String STATUS_CODE;
	
	/**
	 * 心情创建时间
	 */
	private String CREATE_TIME;

	public String getMODE_ID() {
		return MODE_ID;
	}

	public void setMODE_ID(String mODE_ID) {
		MODE_ID = mODE_ID;
	}

	public String getACCOUNT() {
		return ACCOUNT;
	}

	public void setACCOUNT(String aCCOUNT) {
		ACCOUNT = aCCOUNT;
	}

	public String getMODE_INFO() {
		return MODE_INFO;
	}

	public void setMODE_INFO(String mODE_INFO) {
		MODE_INFO = mODE_INFO;
	}

	public String getWALL_CODE() {
		return WALL_CODE;
	}

	public void setWALL_CODE(String wALL_CODE) {
		WALL_CODE = wALL_CODE;
	}

	public String getSTATUS_CODE() {
		return STATUS_CODE;
	}

	public void setSTATUS_CODE(String sTATUS_CODE) {
		STATUS_CODE = sTATUS_CODE;
	}

	public String getCREATE_TIME() {
		return CREATE_TIME;
	}

	public void setCREATE_TIME(String cREATE_TIME) {
		CREATE_TIME = cREATE_TIME;
	}


	
}
