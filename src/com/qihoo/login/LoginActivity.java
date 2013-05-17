package com.qihoo.login;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.qihoo.CommentPublishActivity;
import com.qihoo.MyMoodActivity;
import com.qihoo.NoteWallActivity;
import com.qihoo.NotePublishActivity;
import com.qihoo.R;
import com.qihoo.register.RegisterActivity;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class LoginActivity extends Activity {

	private String userName; // 账号
	private String password; // 密码

	String strResult="error"; // 登陆结果

	/** 以下是UI */
	private EditText view_userName;
	private EditText view_password;
	private CheckBox view_rememberMe;
	private Button view_loginSubmit;
	private Button view_loginRegister;
	
	private String loginGo = new String();//登录成功之后返回到的页面

	/** MENU作为退出的菜单 */
	private static final int MENU_EXIT = Menu.FIRST - 1;

	/** 用来操作SharePreferences的标识 */
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";

	/** 如果登录成功后,用于保存用户名到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";

	/** 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";
	
	/** 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_STATUS = "MAP_LOGIN_STATUS";

	/** 如果登陆失败,这个可以给用户确切的消息显示,true是网络连接失败,false是用户名和密码错误 */
	private boolean isNetError;

	/** 登录loading提示框 */
	private ProgressDialog proDialog;

	/** 该Activity初始化的时候调用 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		
		Bundle extras =	getIntent().getExtras(); 
		if(extras!=null) 
			loginGo = extras.getString("LoginGo");
				
		/** 设置布局文件 */
		findViewsById();
		/** 初始化注册View组件 */
		initView(false);
		/** 初始化UI */
		// 需要去submitListener里面设置URL
		setListener();
		/** 设置监听器 */
	}

	/** 初始化注册View组件 */
	private void findViewsById() {
		view_userName = (EditText) findViewById(R.id.loginUserNameEdit);
		view_password = (EditText) findViewById(R.id.loginPasswordEdit);
		view_rememberMe = (CheckBox) findViewById(R.id.loginRememberMeCheckBox);
		view_loginSubmit = (Button) findViewById(R.id.loginSubmit);
		view_loginRegister = (Button) findViewById(R.id.loginRegister);
	}

	/**
	 * 初始化界面
	 * 
	 * @param isRememberMe
	 *            如果当时点击了RememberMe,并且登陆成功过一次,则saveSharePreferences(true,ture)后,
	 *            则直接进入
	 * */
	private void initView(boolean isRememberMe) 
	{
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		String userName = share.getString(SHARE_LOGIN_USERNAME, "");
		String password = share.getString(SHARE_LOGIN_PASSWORD, "");
		Log.d(this.toString(), "userName=" + userName + " password=" + password);
		if (!"".equals(userName)) {
			view_userName.setText(userName);
		}
		if (!"".equals(password)) {
			view_password.setText(password);
			view_rememberMe.setChecked(true);
		}
		// 如果密码也保存了,则直接让登陆按钮获取焦点
		if (view_password.getText().toString().length() > 0) {
			// view_loginSubmit.requestFocus();
			// view_password.requestFocus();
		}
		share = null;
	}

	/**
	 * 检查用户登陆,服务器通过DataOutputStream的dos.writeInt(int);来判断是否登录成功(
	 * 服务器返回int>0登陆成功,否则失败),登陆成功后根据isRememberMe来判断是否保留密码(用户名是会保留的),
	 * 如果连接服务器超过5秒,也算连接失败.
	 * 
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @param validateUrl
	 *            检查登陆的地址
	 * */
	private boolean validateLocalLogin(String userName, String password,
			String validateUrl) {
		
		System.out.println("username:" + userName);
		System.out.println("password:" + password);
		// 用于标记登陆状态
		boolean loginState = false;

		HttpPost httpRequest = new HttpPost(validateUrl);
		// Post运作传送变数必须用NameValuePair[]阵列储存

		// 传参数 服务端获取的方法为request.getParameter("name")

		List params = new ArrayList();
		params.add(new BasicNameValuePair("username", userName));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("func_type", "detect_user"));

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

				System.out.println("strResult: " + strResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//如果返回SUCCESS状态码，则登陆成功
		if (strResult.replaceAll("\n", "").equals(StatusCodeUtil.SUCCESS)) 
		{
			loginState = true;
		}

		// 登陆成功，是否保存密码
		if (loginState) {
			if (isRememberMe()) {
				saveSharePreferences(true, true, true);
			} else {
				saveSharePreferences(true, false, false);
			}
		} else {
			// 如果不是网络错误
			if (!isNetError) {
				clearShare(); //清除密码
			}
		}
		if (!view_rememberMe.isChecked()) {
			clearShare(); //清除密码
		}
		return loginState;
	}

	/**
	 * 如果登录成功过,则将登陆用户名和密码记录在SharePreferences
	 * 
	 * @param saveUserName
	 *            是否将用户名保存到SharePreferences
	 * @param savePassword
	 *            是否将密码保存到SharePreferences
	 * */
	private void saveSharePreferences(boolean saveUserName, boolean savePassword, boolean saveStatus) {
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
		if(saveStatus) {
			Log.d(this.toString(), "saveStatus="
					+ saveStatus);
			share.edit()
					.putBoolean(SHARE_LOGIN_STATUS,
							saveStatus).commit();
		}
		share = null;
	}

	/** 记住我的选项是否勾选 */
	private boolean isRememberMe() {
		if (view_rememberMe.isChecked()) {
			return true;
		}
		return false;
	}

	/** 记住我checkBoxListener */
	private OnCheckedChangeListener rememberMeListener = new OnCheckedChangeListener() 
	{
		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) 
		{
			if (view_rememberMe.isChecked()) {
				Toast.makeText(LoginActivity.this, "如果登录成功,以后账号和密码会自动输入!",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	/** 登录Button Listener */
	private OnClickListener submitListener = new OnClickListener() 
	{
		public void onClick(View v) {
			proDialog = ProgressDialog.show(LoginActivity.this, "连接中..","连接中..请稍后....", true, true);
			// 开一个线程进行登录验证,主要是用于失败,成功可以直接通过startAcitivity(Intent)转向
			Thread loginThread = new Thread(new LoginThread());
			loginThread.start();
		}
	};

	/** 注册Button Listener */
	private OnClickListener registerLstener = new OnClickListener()
	{
		public void onClick(View v) 
		{
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, RegisterActivity.class);
			// 转向注册页面
			startActivity(intent);
			LoginActivity.this.finish();
			overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
		}
	};

	//返回按键
	public boolean onKeyDown(int keyCode, KeyEvent event) 
    { 
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
        { 
        	setResult(0);
			LoginActivity.this.finish();
			overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
            return false; 
        } 
        return false; 
    }
	
	/** 设置监听器 */
	private void setListener() 
	{
		view_loginSubmit.setOnClickListener(submitListener);
		view_loginRegister.setOnClickListener(registerLstener);
		view_rememberMe.setOnCheckedChangeListener(rememberMeListener);
		
		ImageView btnGoBack = (ImageView) this.findViewById(R.id.btnGoBack);
		btnGoBack.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				setResult(0);
				LoginActivity.this.finish();
				overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
			}
		});
		
		
		ImageView btnGoAhead = (ImageView) this.findViewById(R.id.btnGoAhead);
		btnGoAhead.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				// 转向注册页面
				startActivity(intent);
				LoginActivity.this.finish();
				overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
			}
		});
		
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_EXIT, 0, getResources().getText(R.string.MENU_EXIT));
		return true;
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		super.onMenuItemSelected(featureId, item);
		if (item.getItemId() == MENU_EXIT) 
		{
			setResult(0);
			LoginActivity.this.finish();
			overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
		}
		return true;
	}

	/** 清除密码 */
	private void clearShare() {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		share.edit().putString(SHARE_LOGIN_PASSWORD, "").commit();
		share.edit().putBoolean(SHARE_LOGIN_STATUS, false).commit();
		share = null;
	}

	/** 登录后台通知更新UI线程,主要用于登录失败,通知UI线程更新界面 */

	Handler loginHandler = new Handler() {

		public void handleMessage(Message msg) {
			isNetError = msg.getData().getBoolean("isNetError");
			if (proDialog != null) {
				proDialog.dismiss();
			}
			if (isNetError) {
				Toast.makeText(LoginActivity.this,
						"登陆失败:\n1.请检查您网络连接.\n2.请联系我们.!", Toast.LENGTH_SHORT)
						.show();
			}
			// 用户名和密码错误
			else {
				Toast.makeText(LoginActivity.this, "登陆失败,请输入正确的用户名和密码!",
						Toast.LENGTH_SHORT).show();
				// 清除以前的SharePreferences密码
				clearShare();
			}
		}
	};

	//处理登陆线程，若成功转向别的Activity，否则交给Handler处理，显示一个toast
	class LoginThread implements Runnable 
	{
		public void run() 
		{
			userName = view_userName.getText().toString();
			password = view_password.getText().toString();
			// 这里换成你的验证地址
			String validateURL = StatusCodeUtil.URL;
			boolean loginState = validateLocalLogin(userName, password,
					validateURL);
			Log.d(this.toString(), "validateLogin");

			// 登陆成功
			if (loginState) {

				saveSharePreferences(true,true,true);
				proDialog.dismiss();
				
				if(loginGo!=null && loginGo.equals("addNote"))
				{
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, NotePublishActivity.class);
					startActivity(intent);
					LoginActivity.this.finish();
					overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
				}
				else if(loginGo!=null && loginGo.equals("addComment"))
				{
					Bundle extras =	getIntent().getExtras(); 
					String	modeId = extras.getString("modeID");
					
					
					
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, CommentPublishActivity.class);
					intent.putExtra("modeID", modeId);
					startActivity(intent);
					LoginActivity.this.finish();
					overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
				}
				else
				{
				
					setResult(1);
					LoginActivity.this.finish();
					overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
				}
			} else {
				// 通过调用handler来通知UI主线程更新UI,
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putBoolean("isNetError", isNetError);
				message.setData(bundle);
				loginHandler.sendMessage(message);
			}
		}

	}
}
