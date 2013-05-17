package com.qihoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import com.qihoo.login.LoginActivity;
import com.qihoo.util.StatusCodeUtil;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ViewCommentsActivity extends ListActivity {

	
	private String strResult; // 接收返回结果
	private int commentsNum = 0;
	private ListView listView;
	private ImageView btnBack; //返回按钮
	private ImageView btnComment; //评论按钮
	private SimpleAdapter adapter = null;
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	
	private TextView moodContentTextView;
	private TextView moodAuthorTextView;
	

	/** 用来操作SharePreferences的标识 */
	private final  String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";

	/** 如果登录成功后,用于保存用户名到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";

	/** 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";
	
	/** 如果登录成功后,用于保存STATUS到SharedPreferences,以便下次不再登录 */
	private String SHARE_LOGIN_STATUS = "MAP_LOGIN_STATUS";
	
	String modeId;
	
//	private List<Comment>	listComment;
//	boolean isUpdate = false; //评论是否更新
	// private List<String> data = new ArrayList<String>();
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.commentlist);
		
		
		showComments();
		
				// 为返回按钮添加监听方法
				btnBack = (ImageView) findViewById(R.id.buttonBack);
				btnBack.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) 
					{
						ViewCommentsActivity.this.finish();
						overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
					}

				});
				
				// 为评论按钮添加监听方法
				btnComment = (ImageView) findViewById(R.id.buttonComments);
				btnComment.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) 
					{
						loginCheck();
					}

				});
	}
	

	public void loginCheck()
	{
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		String username = share.getString(SHARE_LOGIN_USERNAME, "");
		boolean status = share.getBoolean(SHARE_LOGIN_STATUS, false);
		if(status) 
		{
			Intent intent = new Intent();
			intent.setClass(ViewCommentsActivity.this, CommentPublishActivity.class);
			intent.putExtra("modeID",modeId); 
			startActivityForResult(intent,102);
			overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
		} 
		else {
			Toast.makeText(ViewCommentsActivity.this, "请先登录！",Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setClass(ViewCommentsActivity.this, LoginActivity.class);
			intent.putExtra("LoginGo","addComment"); 
			intent.putExtra("modeID",modeId); 
			
			startActivityForResult(intent,0);
			overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
		}
	}
	
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	 {
		 		 
		 if(102==requestCode)
		 {
			 
			 //发表成功更新数据
			 if(resultCode==1)
			 {
				 load();
				 commentsNum = 0; //数据从头更新
				 list.clear(); 
				 getData();
				 adapter.notifyDataSetChanged();//通知数据更新
			 }
		 }
		 super.onActivityResult(requestCode, resultCode, data);
	 }
	 

	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
    { 
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
        { 
        	ViewCommentsActivity.this.finish();
			overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
            return false; 
        } 
        return false; 
    }
	
	private  List<Map<String, Object>> getData() {	
		
//		synchronized(strResult){

		List<Comment> listComment = null;
		try {
			listComment = JsonUtils.parseCommentFromJson(strResult);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		}
		/*
		for (Comment com : listComment)
		{
			com.getCOMMENT_TIME();
			com.getCOMMENT_ACCOUNT();
			com.getCOMMENT_CONTENT();
			com.getCOMMENT_LEVEL();
		}
		*/
		Map<String, Object> map;
		Comment com;     
		
		for(;commentsNum<listComment.size();)
		{

			com = listComment.get(commentsNum++);		
			map = new HashMap<String, Object>();
			map.put("name", com.getCOMMENT_ACCOUNT());
			map.put("time", com.getCOMMENT_TIME());
			map.put("comments", com.getCOMMENT_CONTENT());
			list.add(map);
			
			if((commentsNum % 5) == 0 ) break;
		}
		
		if(commentsNum>=listComment.size())
		{
			CustomToast.showToast(getApplicationContext(), "没有更多评论了",500);
		//	RelativeLayout footView = (RelativeLayout) LayoutInflater.from(ViewCommentsActivity.this).inflate(R.layout.listfooter, null);
		//	TextView tvMore = (TextView) footView.findViewById(R.id.more_comments);
		//	tvMore.setText("");			
		}
		//	listView.removeFooterView(LayoutInflater.from(CommentsActivity.this).inflate(R.layout.listfooter, null));

		return list;
	}
	
	//从服务器拉取数据函数
	private void load()
	{
		Thread getCommentsThread = new Thread(new GetCommentsThread());
		getCommentsThread.start();
		
		try {
			getCommentsThread.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private void showComments()
	{
		load();  //拉取数据
		
		//listView = (ListView) findViewById(R.layout.vlist);  
		listView = getListView();
	     getData();  
			
				adapter = new SimpleAdapter(ViewCommentsActivity.this, getData(),
							R.layout.vlist, new String[] { "name", "time", "comments" },
							new int[] { R.id.tv_name, R.id.tv_time, R.id.tv_comments });

			
			listView.addHeaderView(LayoutInflater.from(ViewCommentsActivity.this).inflate(R.layout.listheader, null),null, true);
			
			//向listview加尾结点
			//listView.addFooterView(
			//		LayoutInflater.from(ViewCommentsActivity.this).inflate(R.layout.listfooter, null));
			
			listView.setAdapter(adapter);
			 
	        listView.setOnScrollListener(listener);
		 
	      //向listview加头结点
			String moodContent = "";
			String moodAuthor = "";
			Bundle extras =	getIntent().getExtras(); 
			if(extras!=null) 
			{
				moodContent = extras.getString("modeContent");
				moodAuthor  = extras.getString("modeAuthor");
			}
			moodContentTextView = (TextView)this.findViewById(R.id.author_content);
			moodContentTextView.setText(moodContent);
			
			moodAuthorTextView = (TextView)this.findViewById(R.id.author_name);
			moodAuthorTextView.setText(moodAuthor);
			
		
	}
	
	
	private AbsListView.OnScrollListener listener = new AbsListView.OnScrollListener() {  
		
		
        public void onScrollStateChanged(AbsListView view, int scrollState) {  
            if (view.getLastVisiblePosition() == view.getCount() - 1) {
            	CustomToast.showToast(getApplicationContext(), "正在努力加载中...",500);
                getData();  
                adapter.notifyDataSetChanged();  
            }  
        }  
  
       
        public void onScroll(AbsListView view, int firstVisibleItem,  
                int visibleItemCount, int totalItemCount) {  
  
        }  
    }; 
	
	public boolean getComments(String modeId, String validateUrl){
		// SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		// boolean loginStatus = share.getBoolean(SHARE_LOGIN_STATUS, false);
		// //判断登录状态
	

		/*
		 * if(loginStatus == false) { Log.i("未登录","跳转到登录界面"); return false; }
		 * 
		 * userName = share.getString(SHARE_LOGIN_USERNAME, "");
		 * if(userName.equals("")) { Log.i("未登录","跳转到登录界面"); return false; }
		 */

		Log.i("用户名", modeId);
		Log.i("评论地址", validateUrl);

		HttpPost httpRequest = new HttpPost(validateUrl);
		// Post运作传送变数必须用NameValuePair[]阵列储存

		// 传参数 服务端获取的方法为request.getParameter("name")

		List params = new ArrayList();
		params.add(new BasicNameValuePair("mode_id", modeId));
		params.add(new BasicNameValuePair("func_type", "show_comment"));

		try {

			// 发出HTTP request

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			// 取得HTTP response

			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			Log.i("ye", "hahahahahahhahahahhahah");
			// 若状态码为200 ok

			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				// 取出回应字串
				strResult = EntityUtils.toString(httpResponse.getEntity());
				Log.i("拉取成功： ", strResult);
			}
			else
			{
				return false;
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
		/*
		// 如果返回SUCCESS状态码，则登陆成功
		if (strResult.replaceAll("\n", "").equals(StatusCodeUtil.SUCCESS)) {
			Log.i("发表成功", "跳转到我的心情页面");
		} else {
			Log.w("发表失败", "跳转到我的心情页面");
			Log.w("返回结果", strResult);
			return false;
		}
		
		/*

		/*
		 * // 登陆成功，是否保存密码 if (loginState) { if (isRememberMe()) {
		 * saveSharePreferences(true, true, true); } else {
		 * saveSharePreferences(true, false, false); } } else { // 如果不是网络错误 if
		 * (!isNetError) { clearSharePassword(); //清除密码 } } if
		 * (!view_rememberMe.isChecked()) { clearSharePassword(); //清除密码 }
		 */
		
	}
	
	// 处理获取评论线程，若成功转向别的Activity，否则交给Handler处理，显示一个toast
	class  GetCommentsThread implements Runnable{

			public void run() {				
				
				Bundle extras =	getIntent().getExtras(); 
				if(extras!=null) 
				{
					modeId = extras.getString("modeID");
					Log.i("心情ID", modeId);
				}
				
				if (getComments(modeId,StatusCodeUtil.COMMENT_URL) == true) 
				{
					Log.i("拉取成功", "YES！");					
				} else {
					Log.w("拉取失败", "NO！");
				}

			}

		}
}
