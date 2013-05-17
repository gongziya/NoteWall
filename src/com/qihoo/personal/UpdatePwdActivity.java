package com.qihoo.personal;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qihoo.NoteWallActivity;
import com.qihoo.R;
import com.qihoo.util.StatusCodeUtil;

public class UpdatePwdActivity extends Activity {
	
	/** 用来操作SharePreferences的标识 */
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";

	/** 如果登录成功后,用于保存用户名到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	
	/** 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";
	
	String strResult;    //服务器返回结果
	String oldpassword;   //旧密码
	String newpassword;   //新密码
	boolean isNetError;   //是否是网络错误
	
	private EditText oldPwdEdit;
	private EditText newPwdEdit;
	private Button ok;
	private Button cancel;
	

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.updatepwd);
		oldPwdEdit = (EditText)findViewById(R.id.oldPasswordEdit);
		newPwdEdit = (EditText)findViewById(R.id.newPasswordEdit);
		
		ok = (Button)findViewById(R.id.ok);
		cancel = (Button)findViewById(R.id.cancel);
		ok.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				oldpassword = oldPwdEdit.getText().toString().trim();
				newpassword = newPwdEdit.getText().toString().trim();
				Thread passwordThread = new Thread(new PasswordThread());
				passwordThread.start();
			}
		});
		cancel.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				Intent intent = new Intent();
				intent.setClass(UpdatePwdActivity.this, PersonalActivity.class);
				UpdatePwdActivity.this.finish();
				startActivity(intent);
				overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
			}
		});
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
    { 
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
        { 
        	setResult(0);
        	UpdatePwdActivity.this.finish();
			overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
            return false; 
        } 
        return false; 
    }
	
	class PasswordThread implements Runnable {

		public void run() 
		{
			SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
			String userName = share.getString(SHARE_LOGIN_USERNAME, "");
			
			// 这里换成你的验证地址
			String validateURL = StatusCodeUtil.URL;
			boolean updatePwdStatus = validateUpdatePwd(userName, oldpassword, newpassword,
					 validateURL);
			Log.d(this.toString(), "validateRegister");

			// 修改成功
			if (updatePwdStatus) {
        		share.edit().putString(SHARE_LOGIN_PASSWORD, newpassword).commit();
        		Intent intent = new Intent();
        		intent.setClass(UpdatePwdActivity.this, PersonalActivity.class);
        		startActivity(intent);
				
			} else {
				// 通过调用handler来通知UI主线程更新UI,
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putBoolean("isNetError", isNetError);
				message.setData(bundle);
				updatePwdHandler.sendMessage(message);
			}
		}
	}
	
	Handler updatePwdHandler = new Handler() 
	{
		public void handleMessage(Message msg) 
		{
			super.handleMessage(msg);
			isNetError = msg.getData().getBoolean("isNetError");
			if (isNetError) {
				Toast.makeText(UpdatePwdActivity.this,
						"修改信息失败:\n1.请检查您网络连接.\n2.请联系我们.!", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(UpdatePwdActivity.this, "请稍后重试！",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	private boolean validateUpdatePwd(String userName, String oldpassword, String newpassword,String validateUrl) 
	{
		System.out.println("username:" + userName);
		// 用于标记修改性别状态
		boolean updatePwdStatus = false;

		HttpPost httpRequest = new HttpPost(validateUrl);
		// Post运作传送变数必须用NameValuePair[]阵列储存

		// 传参数 服务端获取的方法为request.getParameter("name")

		List params = new ArrayList();
		params.add(new BasicNameValuePair("username", userName.trim()));
		params.add(new BasicNameValuePair("old_password", oldpassword));
		params.add(new BasicNameValuePair("new_password", newpassword));
		params.add(new BasicNameValuePair("func_type", "change_user"));

		try {

			// 发出HTTP request

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			// 取得HTTP response

			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			System.out.println("确认状态码"
					+ httpResponse.getStatusLine().getStatusCode());

			// 若状态码为200 ok

			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				// 取出回应字串

				strResult = EntityUtils.toString(httpResponse.getEntity());

				System.out.println("strResult: " + strResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (strResult.replaceAll("\n", "").equals(StatusCodeUtil.SUCCESS)) {
			updatePwdStatus = true;
		}

		return updatePwdStatus;
	}
}
