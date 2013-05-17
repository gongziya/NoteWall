package com.qihoo;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.qihoo.util.StatusCodeUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentPublishActivity extends Activity {
	/** Called when the activity is first created. */

	private ImageView btnPublish;
	private ImageView btnBack;
	private EditText contentET;
	
	/** 用来操作SharePreferences的标识 */
	private final  String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";

	/** 如果登录成功后,用于保存用户名到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";

	/** 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";
	
	/** 如果登录成功后,用于保存STATUS到SharedPreferences,以便下次不再登录 */
	private String SHARE_LOGIN_STATUS = "MAP_LOGIN_STATUS";

	public final static int TOTAL_NUM = 100;

	// private Toast mToast;

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 100:
				((TextView) findViewById(R.id.ETPublish)).setText("");
				break;
			case 200:
				CustomToast.showToast(getApplicationContext(), "发表评论成功",1000);
				break;
			}
		}
	};

	/************************* 方法 **************************************/
	private boolean modePublish(String modeID,String commentContent,
			String validateUrl) {
		// SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		// boolean loginStatus = share.getBoolean(SHARE_LOGIN_STATUS, false);
		// //判断登录状态
		String strResult = ""; // 接收返回结果
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		String userName = share.getString(SHARE_LOGIN_USERNAME, "");
 
		/*
		 * if(loginStatus == false) { Log.i("未登录","跳转到登录界面"); return false; }
		 * 
		 * userName = share.getString(SHARE_LOGIN_USERNAME, "");
		 * if(userName.equals("")) { Log.i("未登录","跳转到登录界面"); return false; }
		 */
		
		String commentAccount = userName;
		
		Log.i("心情ID", modeID);
		Log.i("评论账号", commentAccount);
		Log.i("评论内容", commentContent);
		Log.i("评论地址", validateUrl);

		HttpPost httpRequest = new HttpPost(validateUrl);
		// Post运作传送变数必须用NameValuePair[]阵列储存

		// 传参数 服务端获取的方法为request.getParameter("name")

		List params = new ArrayList();
		params.add(new BasicNameValuePair("mode_id", modeID));
		params.add(new BasicNameValuePair("comment_account", commentAccount));
		params.add(new BasicNameValuePair("comment_content", commentContent));
		params.add(new BasicNameValuePair("func_type", "insert_comment"));

		try {

			// 发出HTTP request

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			// 取得HTTP response

			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);

			// 若状态码为200 ok

			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				// 取出回应字串

				strResult = EntityUtils.toString(httpResponse.getEntity());
				Log.i("评论成功", strResult);
			//	System.out.println("strResult: " + strResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 如果返回SUCCESS状态码，则登陆成功
		if (strResult.replaceAll("\n", "").equals(StatusCodeUtil.SUCCESS)) {
			
			//利用handler显示Toast
			Message msg = new Message();
	        msg.what = 200;
	        CommentPublishActivity.this.mHandler.sendMessage(msg);
	        
	        setResult(1);
			CommentPublishActivity.this.finish();
			overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
	        
		} else {
			Log.w("发表评论失败", "跳转到心情列表页面");
			Log.w("返回结果", strResult);
			
			setResult(0);
			CommentPublishActivity.this.finish();
			overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
				
			return false;
		}

		/*
		 * // 登陆成功，是否保存密码 if (loginState) { if (isRememberMe()) {
		 * saveSharePreferences(true, true, true); } else {
		 * saveSharePreferences(true, false, false); } } else { // 如果不是网络错误 if
		 * (!isNetError) { clearSharePassword(); //清除密码 } } if
		 * (!view_rememberMe.isChecked()) { clearSharePassword(); //清除密码 }
		 */
		return true;
	}

	// 定义弹出返回对话框方法
	protected void dialog() {
		AlertDialog.Builder builder = new Builder(CommentPublishActivity.this);
		builder.setMessage("确定要放弃发表评论吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					// @Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						CommentPublishActivity.this.finish();
						CustomToast.showToast(getApplicationContext(),
								"您已退出评论", 1000);
					}
				});
		builder.setNegativeButton("取消",
				new android.content.DialogInterface.OnClickListener() {
					// @Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						CustomToast.showToast(getApplicationContext(),
								"您已取消操作", 1000);
					}
				});

		AlertDialog quitDialog = builder.create();
		quitDialog.setCanceledOnTouchOutside(false);
		quitDialog.show();
	}


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.publishcomment);

		contentET = (EditText) findViewById(R.id.ETPublish);
		final TextView hasnumTV = (TextView) findViewById(R.id.textPublish);

		// 添加文本改变监听方法
		contentET.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
			}

			public void afterTextChanged(Editable s) {
				int number = TOTAL_NUM - s.length();
				hasnumTV.setText("评论心情（" + number + "）");
				selectionStart = contentET.getSelectionStart();
				selectionEnd = contentET.getSelectionEnd();
				if (temp.length() > TOTAL_NUM) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					contentET.setText(s);
					contentET.setSelection(tempSelection);// 设置光标在最后
					CustomToast.showToast(getApplicationContext(), "输入字数超出限制",
							1000);
				}
			}
		});

		// 为发表按钮添加监听方法
		btnPublish = (ImageView) findViewById(R.id.buttonPublish);
		btnPublish.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				EditText contentET = (EditText) findViewById(R.id.ETPublish);
				String strContent = contentET.getText().toString();
				if (strContent.equals("")) {
					/*
					 * if (mToast != null) mToast.setText("您还没输入任何心情"); else
					 * mToast = Toast.makeText(getApplicationContext(),
					 * "您还没输入任何心情", Toast.LENGTH_SHORT); mToast.show();
					 */
					CustomToast.showToast(getApplicationContext(), "您还没输入任何评论",
							1000);
				} else {
					
					Thread sendThread = new Thread(new SendThread());
					sendThread.start();
					
					// System.out.println(strContent);
					// contentET.setText("");
				}
			}
		});

		// 为返回按钮添加监听方法
		btnBack = (ImageView) findViewById(R.id.buttonBack);
		btnBack.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String strContent = contentET.getText().toString();
				dialog();

				if (strContent.equals("")) {

				} else {

				}
			}

		});

	}

	/*
	 * //为返回键添加监听方法
	 * 
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
	 * (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	 * dialog(); return false; } return false; }
	 */

	// 处理发送心情线程，若成功转向别的Activity，否则交给Handler处理，显示一个toast
	class SendThread implements Runnable {

		public void run() {

			// contentET = (EditText)findViewById(R.id.ETPublish);
			String strContent = ((TextView) findViewById(R.id.ETPublish))
					.getText().toString(); // 获取评论
			Log.i("strContent", strContent);

			// 不能在用户线程更新UI，UI必须在UI线程中更新
			// ((TextView)findViewById(R.id.ETPublish))(R.id.ETPublish).setText("");
			
			Message msg = new Message();
	        msg.what = 100;
	        CommentPublishActivity.this.mHandler.sendMessage(msg);
	        
	        /*
			try{
		          Thread.sleep(2000);
		          Message msg = new Message();
		          msg.what = 100;
		          CommentPublishActivity.this.mHandler.sendMessage(msg);
		       
		        }
		        catch(InterruptedException e){
		        	e.printStackTrace();
		        }
			*/

	       
	        
	        Bundle extras =	getIntent().getExtras();
	        String modeID = extras.getString("modeID");
	        
			if (modePublish(modeID, strContent, StatusCodeUtil.COMMENT_URL) == true) {
				Log.i("确认成功", "评论已发表！");
			} else {
				Log.w("警告", "评论发表失败！");
			}

		}

	}

}
