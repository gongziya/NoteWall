package com.qihoo;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JsonUtils {
	
	public static List<Mode> parseModeFromJson(String jsonData) throws JSONException
	{
		if (jsonData == null || jsonData.equals(""))
		{ 
			return null;
		}
		
		JSONArray json = new JSONArray(jsonData);
		
		List<Mode> listMode = new ArrayList();
		for (int i = 0; i < json.length(); i++)
		{
			Mode mode = new Mode();
			JSONObject result = json.optJSONObject(i);
			mode.setMODE_ID(result.getString("MODE_ID"));
			mode.setACCOUNT(result.getString("ACCOUNT"));
			mode.setMODE_INFO(result.getString("MODE_INFO"));
			mode.setSTATUS_CODE(result.getString("STATUS_CODE"));
			mode.setWALL_CODE(result.getString("WALL_CODE"));
			String time = result.getString("CREATE_TIME");
			mode.setCREATE_TIME(time);
			
			listMode.add(mode);
		}
		
		return listMode;
	}
	
	/**
	 * 锟斤拷json锟斤拷锟阶拷锟轿拷锟斤拷鄣锟絣ist锟斤拷式锟斤拷锟?
	 * 
	 * @param jsonData
	 * @return
	 * @throws JSONException
	 */
	public static List<Comment> parseCommentFromJson(String jsonData) throws JSONException
	{
		if (jsonData == null || jsonData.equals(""))
		{ //锟斤拷锟斤拷锟较⑽拷眨锟斤拷蚍祷乜锟?
			return null;
		}
		
		JSONArray json = new JSONArray(jsonData);
		
		List<Comment> listComment = new ArrayList();
		for (int i = 0; i < json.length(); i++)
		{
			Comment comment = new Comment();
			JSONObject result = json.optJSONObject(i);
			comment.setMODE_ID(result.getString("MODE_ID"));
			comment.setCOMMENT_ID(result.getString("COMMENT_ID"));
			comment.setCOMMENT_ACCOUNT(result.getString("COMMENT_ACCOUNT"));
			comment.setCOMMENT_LEVEL(result.getInt("COMMENT_LEVEL"));
			comment.setCOMMENT_CONTENT(result.getString("COMMENT_CONTENT"));
			comment.setCOMMENT_TIME(result.getString("COMMENT_TIME"));
			
			listComment.add(comment);
		}
		
		return listComment;
	}
}
