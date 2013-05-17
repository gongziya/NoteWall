package com.qihoo.register;

import com.qihoo.MyMoodActivity;
import com.qihoo.R;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.qihoo.login.LoginActivity;
import com.qihoo.personal.UpdatePwdActivity;
import com.qihoo.util.StatusCodeUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;


public class RegisterActivity extends Activity {

	private String userName; //用户名
	private char sex;   //性别
	private String password;   //密码
	private String strResult;   //注册结果

	private EditText view_userName;   
	private RadioGroup view_sexGroup;
	private EditText view_password;
	private EditText view_passwordConfirm;
	private Button view_submit;
	private Button view_backLogin;

	private static final int MENU_EXIT = Menu.FIRST - 1;
	
	StringBuilder suggest;  //提示信息
	
	/** 用来操作SharePreferences的标识 */
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";

	/** 如果注册成功后,用于保存用户名到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";

	/** 如果注册成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";

	/** 如果注册失败,这个可以给用户确切的消息显示,true是网络连接失败,false是用户名和密码错误 */
	private boolean isNetError;
	/** 注册loading提示框 */
	private ProgressDialog proDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
		findViews();  
		setListener();
	}

	/** 1.初始化注册view组件 */
	private void findViews() {
		view_userName = (EditText) findViewById(R.id.registerUserName);
		view_sexGroup = (RadioGroup) findViewById(R.id.radioGroup);
		view_password = (EditText) findViewById(R.id.registerPassword);
		view_passwordConfirm = (EditText) findViewById(R.id.registerPasswordConfirm);
		view_submit = (Button) findViewById(R.id.registerSubmit);
		view_backLogin = (Button)findViewById(R.id.backLogin);
	}
	
	//返回按钮
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{ 
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
	    { 
			Intent intent = new Intent();
			intent.setClass(RegisterActivity.this, LoginActivity.class);
			startActivity(intent);
			RegisterActivity.this.finish();
			overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
			return false;
	    } 
	        return false; 
	}

	private void setListener() 
	{
		view_sexGroup.setOnCheckedChangeListener(sexGroupListener);
		view_submit.setOnClickListener(submitListener);
		view_backLogin.setOnClickListener(backListener);
		
		
		ImageView btnGoBack = (ImageView) this.findViewById(R.id.btnGoBack);
		btnGoBack.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, LoginActivity.class);
				startActivity(intent);
				RegisterActivity.this.finish();
				overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
			}
		});
	}

	/** 监听性别按钮 */
	private OnCheckedChangeListener sexGroupListener = new OnCheckedChangeListener()
	{
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			if (((RadioButton) findViewById(R.id.radiomale)).getId() == checkedId) {
				sex = '0';
			} else {
				sex = '1';
			}
		}
	};

	/** 监听注册确定按钮 */
	private OnClickListener submitListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			String userName = view_userName.getText().toString();
			String password = view_password.getText().toString();
			String passwordConfirm = view_passwordConfirm.getText().toString();
			validateForm(userName, sex, password, passwordConfirm);
			if (suggest.length() == 0) {
				proDialog = ProgressDialog.show(RegisterActivity.this, "注册中..",
						"连接中..请稍后....", true, true);
				// 开一个线程进行注册,主要是用于失败,成功可以直接通过startAcitivity(Intent)转向
				Thread registerThread = new Thread(new RegisterThread());
				registerThread.start();

				// registerHandler.post(new RegisterThread());
			}
		}
	};
	
	
	
	/** 监听返回登陆按钮 */
	private OnClickListener backListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			Intent intent = new Intent();
			intent.setClass(RegisterActivity.this, LoginActivity.class);
			startActivity(intent);
			RegisterActivity.this.finish();
			overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
		}
		
	};
	
	class RegisterThread implements Runnable 
	{
		public void run() 
		{
			userName = view_userName.getText().toString();
			password = view_password.getText().toString();
			
			// 这里换成你的验证地址
			String validateURL = StatusCodeUtil.URL;
			boolean registerState = validateLocalRegister(userName, sex,
					password, validateURL);
			Log.d(this.toString(), "validateRegister");

			// 注册成功
			if (registerState) {
				/*
				setResult(0);
				RegisterActivity.this.finish();
				overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
				*/
				
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this,LoginActivity.class);
				// 转向注册页面
				startActivity(intent);
				RegisterActivity.this.finish();
				overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
				
				proDialog.dismiss();
			} else {
				// 通过调用handler来通知UI主线程更新UI,
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putBoolean("isNetError", isNetError);
				message.setData(bundle);
				registerHandler.sendMessage(message);
			}
		}
	}
	
	/** 注册后台通知更新UI线程,主要用于注册失败,通知UI线程更新界面 */

	Handler registerHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			isNetError = msg.getData().getBoolean("isNetError");
			if (proDialog != null) {
				proDialog.dismiss();

			}
			if (isNetError) {
				Toast.makeText(RegisterActivity.this,
						"注册失败:\n1.请检查您网络连接.\n2.请联系我们.!", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(RegisterActivity.this, "该账号已存在，请重新输入！",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	/** 验证注册表单 */
	private void validateForm(String userName, char sex, String password,
			String password2) {
		suggest = new StringBuilder();
		Log.d(this.toString(), "validate");
		if (userName.length() < 1) {
			suggest.append(getText(R.string.suggust_userName) + "\n");
		}
		if (sex != '0' && sex != '1') {
			suggest.append(getText(R.string.suggust_sexNotEmpty) + "\n");
		}
		if (password.length() < 1 || password2.length() < 1) {
			suggest.append(getText(R.string.suggust_passwordNotEmpty) + "\n");
		}
		if (!password.equals(password2)) {
			suggest.append(getText(R.string.suggest_passwordNotSame));
		}
		if (suggest.length() > 0) {
			// sub是
			Toast.makeText(this, suggest.subSequence(0, suggest.length() - 1),
					Toast.LENGTH_SHORT).show();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_EXIT, 0, R.string.MENU_EXIT);
		return true;
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		if (item.getItemId() == MENU_EXIT) {
			finish();
		}
		;
		/*
		 * switch (item.getItemId()) { case MENU_EXIT: finish(); break;
		 */
		return true;
	}

	private boolean validateLocalRegister(String userName, char sex,
			String password, String validateUrl) {
		System.out.println("username:" + userName);
		System.out.println("password:" + password);
		// 用于标记注册状态
		boolean registerState = false;

		HttpPost httpRequest = new HttpPost(validateUrl);
		// Post运作传送变数必须用NameValuePair[]阵列储存

		// 传参数 服务端获取的方法为request.getParameter("name")

		List params = new ArrayList();
		params.add(new BasicNameValuePair("username", userName));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("sex", Character.toString(sex)));
		params.add(new BasicNameValuePair("func_type", "insert_user"));

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
			registerState = true;
		}

		// 注册成功
		if (registerState) {

			saveSharePreferences(true, true);
		}
		return registerState;
	}

	/**
	 * 如果成功注册,则将注册用户名和密码记录在SharePreferences
	 * 
	 * @param saveUserName
	 *            是否将用户名保存到SharePreferences
	 * @param savePassword
	 *            是否将密码保存到SharePreferences
	 * */
	private void saveSharePreferences(boolean saveUserName, boolean savePassword) {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		if (saveUserName) {
			Log.d(this.toString(), "saveUserName="
					+ view_userName.getText().toString());
			share.edit()
					.putString(SHARE_LOGIN_USERNAME,
							view_userName.getText().toString()).commit();
		}
		if (savePassword) {
			share.edit()
					.putString(SHARE_LOGIN_PASSWORD,
							view_password.getText().toString()).commit();
		}
		share = null;
	}
	
}
