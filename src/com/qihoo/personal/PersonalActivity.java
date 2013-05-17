package com.qihoo.personal;

import java.lang.reflect.Field;
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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.qihoo.MainActivity;
import com.qihoo.NoteWallActivity;
import com.qihoo.R;
import com.qihoo.login.LoginActivity;
import com.qihoo.util.StatusCodeUtil;

public class PersonalActivity extends Activity 
{

	private TextView username;
	private TextView sex;
	private TableRow tr_updateSex;
	private TableRow tr_updatePsw;
	private TableRow tr_version;
	private TableRow tr_suggest;
	private TableRow tr_aboutUs;
	private ImageView exitLogin;

	String strResult;
	String newSex;
	boolean isNetError;
	

	/** 用来操作SharePreferences的标识 */
	private final String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";

	/** 如果登录成功后,用于保存用户名到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";
	
	/** 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";

	/** 如果注册成功后,用于保存SEX到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_SEX = "MAP_LOGIN_SEX";
	
	/** 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_STATUS = "MAP_LOGIN_STATUS";
	
	//SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
	static int choice = -1;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.personal);
		findViewById();
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		username.setText(share.getString(SHARE_LOGIN_USERNAME, ""));
		sex.setText(share.getString(SHARE_LOGIN_SEX, ""));
		setListener();
	}

	private void findViewById() {
		// TODO Auto-generated method stub
		username = (TextView) findViewById(R.id.username);
		sex = (TextView) findViewById(R.id.sex);
		tr_updateSex = (TableRow) findViewById(R.id.tr_updateSex);
		tr_updatePsw = (TableRow) findViewById(R.id.tr_updatePassword);
		tr_version = (TableRow) findViewById(R.id.tr_version);
		tr_suggest = (TableRow) findViewById(R.id.tr_suggest);
		tr_aboutUs = (TableRow) findViewById(R.id.tr_aboutUs);
		exitLogin = (ImageView) findViewById(R.id.exitLogin);
	}
	
	private void setListener() {
		
		//修改性别信息
		tr_updateSex.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) {
				// TODO Auto-generated method stub
				radioDialog();
			}
		});
		
		//修改密码
		tr_updatePsw.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				Intent intent = new Intent();
				intent.setClass(PersonalActivity.this, UpdatePwdActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
			}
		});
		
		//查看版本
		tr_version.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v)
			{
				Intent intent = new Intent();
				intent.setClass(PersonalActivity.this, VersionActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
			}
		});
		
		//意见反馈
		tr_suggest.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				Intent intent = new Intent();
				intent.setClass(PersonalActivity.this, SuggestActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
			}
		});
		
		//关于我们
		tr_aboutUs.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PersonalActivity.this, AboutUsActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
			}
		});
		
		//退出登录
		exitLogin.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				clearShare();
				Intent intent = new Intent();
				intent.setClass(PersonalActivity.this, LoginActivity.class);
				setResult(0);
				PersonalActivity.this.finish();
				startActivity(intent);
				overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
			}
		});
		
		
		// 为返回按钮添加监听方法
		ImageView btnBack = (ImageView) findViewById(R.id.btnGoBack);
		btnBack.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) 
			{
				PersonalActivity.this.finish();
				overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
			}

		});
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
    { 
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
        { 
        	PersonalActivity.this.finish();
			overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
            return false; 
        } 
        return false; 
    }
	
	/** 清除密码 */
	private void clearShare() {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		share.edit().putString(SHARE_LOGIN_PASSWORD, "").commit();
		share.edit().putBoolean(SHARE_LOGIN_STATUS,false).commit();
		share = null;
	}

	protected void radioDialog() 
	{
		AlertDialog.Builder builder = new Builder(PersonalActivity.this); 
		builder.setTitle("请选择性别"); 
        builder.setIcon(R.drawable.radio_icon); 
        builder.setSingleChoiceItems(
      		     new String[] {"男","女"}, -1,
       		     new DialogInterface.OnClickListener() {
       		      public void onClick(DialogInterface dialog, int which) {
       		    	  choice = which;
       		    	  //dialog.dismiss();
       		      }
       		     });
        
        builder.setPositiveButton("确认", 
                new android.content.DialogInterface.OnClickListener() { 
                  //  @Override 
                    public void onClick(DialogInterface dialog, int which) { 
                    	//System.out.println("test");
                    	
                    	if(choice == -1)
                    	{
                    		//利用反射机制使对话框不退出 
                    		 try  
                             {  
                                 Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
                                 field.setAccessible(true);  
                                  //设置mShowing值，欺骗android系统   
                                 field.set(dialog, false);  
                             }catch(Exception e) {  
                                 e.printStackTrace();  
                             }
                    		 //CustomToast.showToast(getApplicationContext(), "请选择心情种类", 1000);
                    	}
                    	else
                    	{                    		
                    		try  
                            {  
                                Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
                                field.setAccessible(true);  
                                 //设置mShowing值，欺骗android系统   
                                field.set(dialog,true);  
                            }catch(Exception e) {  
                                e.printStackTrace();  
                            }
                    		//choice = -1;
                    		newSex = (choice==0 ? "男" : "女");
                    		Thread sexThread = new Thread(new UpdateSexThread());
                    		sexThread.start();
                    	}
                        
                        dialog.dismiss(); 
                    } 
                }); 
        builder.setNegativeButton("取消", 
                new android.content.DialogInterface.OnClickListener() { 
               //     @Override 
                    public void onClick(DialogInterface dialog, int which) { 
                    	
                    	//允许对话框消失
                    	try  
                        {  
                            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");  
                            field.setAccessible(true);  
                             //设置mShowing值，欺骗android系统   
                            field.set(dialog,true);  
                        }catch(Exception e) {  
                            e.printStackTrace();  
                        }
                    	choice = -1;
                         Log.i("我的第一个android日志","取消单选对话框操作");
                         Toast.makeText(PersonalActivity.this, "您已取消操作", Toast.LENGTH_SHORT);
                         dialog.dismiss();
                    } 
                }); 
        
        AlertDialog quitDialog =builder.create();
        quitDialog.setCanceledOnTouchOutside(false);
        quitDialog.show();    
         
    } 
	
	class UpdateSexThread implements Runnable {
		public void run()
		{
			
			String userName = username.getText().toString();
			
			// 这里换成你的验证地址
			String validateURL = StatusCodeUtil.URL;
			boolean updateSexState = validateUpdateSex(userName, newSex,
					 validateURL);
			Log.d(this.toString(), "validateRegister");

			// 修改成功
			if (updateSexState) {
				/*Toast.makeText(RegisterActivity.this, "注册成功，请登录！",
						Toast.LENGTH_SHORT).show();*/
				SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
        		
        		share.edit().putString(SHARE_LOGIN_SEX, newSex).commit();
        		Message message = new Message();
        		message.arg1 = 10;
        		
				
			} else {
				// 通过调用handler来通知UI主线程更新UI,
				Message message = new Message();
				message.arg1 = 20;
				Bundle bundle = new Bundle();
				bundle.putBoolean("isNetError", isNetError);
				message.setData(bundle);
				updateSexHandler.sendMessage(message);
			}
		}
	}
	
	Handler updateSexHandler = new Handler() 
	{
		public void handleMessage(Message msg) 
		{
			super.handleMessage(msg);
			switch(msg.arg1) {
			case 10: 
				sex.setText(newSex);
				break;
			case 20:
				isNetError = msg.getData().getBoolean("isNetError");
				if (isNetError) {
					Toast.makeText(PersonalActivity.this,
							"修改信息失败:\n1.请检查您网络连接.\n2.请联系我们.!", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(PersonalActivity.this, "请稍后重试！",
							Toast.LENGTH_SHORT).show();
				}
			}
			
		}
		
	};
	
	private boolean validateUpdateSex(String userName, String newSex,
			String validateUrl) {
		System.out.println("username:" + userName);
		System.out.println("sex:" + newSex);
		// 用于标记修改性别状态
		boolean updateSexStatus = false;

		HttpPost httpRequest = new HttpPost(validateUrl);
		// Post运作传送变数必须用NameValuePair[]阵列储存

		// 传参数 服务端获取的方法为request.getParameter("name")

		List params = new ArrayList();
		params.add(new BasicNameValuePair("username", userName));
		params.add(new BasicNameValuePair("sex", newSex));
		params.add(new BasicNameValuePair("func_type", "update_user"));

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
			updateSexStatus = true;
		}

		// 性别修改成功
		if (updateSexStatus) {

			saveSharePreferences(true, true);
		}
		return updateSexStatus;
	}

	/**
	 * 如果成功注册,则将注册用户名和密码记录在SharePreferences
	 * 
	 * @param saveUserName
	 *            是否将用户名保存到SharePreferences
	 * @param savePassword
	 *            是否将密码保存到SharePreferences
	 * */
	private void saveSharePreferences(boolean saveUserName, boolean saveSex) {
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		if (saveUserName) {
			Log.d(this.toString(), "saveUserName="
					+ username.getText().toString());
			share.edit()
					.putString(SHARE_LOGIN_USERNAME,
							username.getText().toString()).commit();
		}
		if(saveSex) {
			share.edit().putString(SHARE_LOGIN_SEX, newSex);
		}
		share = null;
	}

}
