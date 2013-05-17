package com.qihoo;

import java.util.Date;

/**
 * 评论类
 * 
 * @author Administrator
 *
 */
public class Comment {

	/**
	 * 评论主键
	 */
	private String COMMENT_ID;
	
	/**
	 * 心情主键，标记是那一条心情
	 */
	private String MODE_ID;
	
	/**
	 * 评论账户，用于说明是哪个用户评论
	 */
	private String COMMENT_ACCOUNT;
	
	/**
	 * 评论楼层
	 */
	private int COMMENT_LEVEL;
	
	/**
	 * 评论内容
	 */
	private String COMMENT_CONTENT;
	
	/**
	 * 评论时间
	 */
	private String COMMENT_TIME;

	public String getCOMMENT_ID() {
		return COMMENT_ID;
	}

	public void setCOMMENT_ID(String cOMMENT_ID) {
		COMMENT_ID = cOMMENT_ID;
	}

	public String getMODE_ID() {
		return MODE_ID;
	}

	public void setMODE_ID(String mODE_ID) {
		MODE_ID = mODE_ID;
	}

	public String getCOMMENT_ACCOUNT() {
		return COMMENT_ACCOUNT;
	}

	public void setCOMMENT_ACCOUNT(String cOMMENT_ACCOUNT) {
		COMMENT_ACCOUNT = cOMMENT_ACCOUNT;
	}

	public int getCOMMENT_LEVEL() {
		return COMMENT_LEVEL;
	}

	public void setCOMMENT_LEVEL(int cOMMENT_LEVEL) {
		COMMENT_LEVEL = cOMMENT_LEVEL;
	}

	public String getCOMMENT_CONTENT() {
		return COMMENT_CONTENT;
	}

	public void setCOMMENT_CONTENT(String cOMMENT_CONTENT) {
		COMMENT_CONTENT = cOMMENT_CONTENT;
	}

	public String getCOMMENT_TIME() {
		return COMMENT_TIME;
	}

	public void setCOMMENT_TIME(String cOMMENT_TIME) {
		COMMENT_TIME = cOMMENT_TIME;
	}

	
}
