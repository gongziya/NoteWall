package com.qihoo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qihoo.NoteWallActivity.GetMoodListThread;
import com.qihoo.login.LoginActivity;
import com.qihoo.util.StatusCodeUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyMoodActivity extends Activity {

	iGallery gallery;
	
	ArrayList modeList = null;  
	String jsonData;	
	
	String username;
	
	/** 用来操作SharePreferences的标识 */
	private final  String SHARE_LOGIN_TAG = "MAP_SHARE_LOGIN_TAG";

	/** 如果登录成功后,用于保存用户名到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_USERNAME = "MAP_LOGIN_USERNAME";

	/** 如果登录成功后,用于保存PASSWORD到SharedPreferences,以便下次不再输入 */
	private String SHARE_LOGIN_PASSWORD = "MAP_LOGIN_PASSWORD";
	
	/** 如果登录成功后,用于保存STATUS到SharedPreferences,以便下次不再登录 */
	private String SHARE_LOGIN_STATUS = "MAP_LOGIN_STATUS";
	
	private ProgressDialog proDialog;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mymoodwall);
 
        setBackWall();
        
        setBtnClickListener();
       
        createRandModeList();
    }
	
	
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	 {
		 if(101==requestCode) // 发表心情
		 {
			 if(resultCode==1)
			 {
				 createRandModeList();
			 }
		 }
		 else if(0==requestCode) //登录页面
		 {
			 if(resultCode==0)
			 {
				 MyMoodActivity.this.finish();
				 overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
			 }
			 else if(resultCode == 1)
				 createRandModeList();
		 }
		 super.onActivityResult(requestCode, resultCode, data);
	 }
	
	private void setBackWall()
	{
		 LinearLayout layContent = (LinearLayout) this.findViewById(R.id.notewallContent);
		 Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wall2);  
		 BitmapDrawable bd = new BitmapDrawable(bitmap);  
		 bd.setTileModeXY(TileMode.REPEAT , TileMode.REPEAT );  
		 bd.setDither(true);  
		 layContent.setBackgroundDrawable(bd);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
    { 
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
        { 
        	setResult(0);
			MyMoodActivity.this.finish();
			overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
            return false; 
        } 
        return false; 
    }
	
	private void setBtnClickListener()
	{
		ImageView btnGoBack = (ImageView) this.findViewById(R.id.btnGoBack);
		btnGoBack.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				setResult(0);
				MyMoodActivity.this.finish();
				overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
			}
		});
		
		ImageView btnNewNote = (ImageView) this.findViewById(R.id.btnNewNote);
		btnNewNote.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				loginCheck();
			}
		});
	}
	
	public void loginCheck()
	{
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		username = share.getString(SHARE_LOGIN_USERNAME, "");
		boolean status = share.getBoolean(SHARE_LOGIN_STATUS, false);
		if(status) 
		{
			Intent intent = new Intent();
			intent.setClass(MyMoodActivity.this, NotePublishActivity.class);
			startActivityForResult(intent,101);
			overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
		} 
		else {
			Toast.makeText(MyMoodActivity.this, "请先登录！",Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setClass(MyMoodActivity.this, LoginActivity.class);
			startActivityForResult(intent,0);
			overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
		}
	}
	
	
	private void createRandModeList()
	{
		SharedPreferences share = getSharedPreferences(SHARE_LOGIN_TAG, 0);
		username = share.getString(SHARE_LOGIN_USERNAME, "");
		boolean status = share.getBoolean(SHARE_LOGIN_STATUS, false);
		
		if(!status) 
		{
			Toast.makeText(MyMoodActivity.this, "请先登录！",Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setClass(MyMoodActivity.this, LoginActivity.class);
			startActivityForResult(intent,0);
			overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
			return;
		}
		
		proDialog = ProgressDialog.show(MyMoodActivity.this, "连接中..",	"正在获取数据..请稍后....", true, true);
		Thread getmoodlistThread = new Thread(new ServerInterfaceThread(0));
		getmoodlistThread.start();
	}
	
	private void createNoteWallContent()
	{
		gallery = (iGallery) this.findViewById(R.id.image_gallery);
    	ImageAdapter imageadapter = new ImageAdapter(this);
    	imageadapter.setFlag(0);
		gallery.setAdapter(imageadapter);
		gallery.setSelection(0);
	}
	
	class ImageAdapter extends BaseAdapter{
    	
    	private Context mContext;
    	private int flag;
    	
    	public ImageAdapter(Context context)
    	{
    		this.mContext = context;
    	}
    	
		public int getCount() {
			return modeList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}
		
		public void setFlag(int f)
		{
			flag = f;
		}

		public View getView(final int position, View convertView, ViewGroup parent) 
		{
			DisplayMetrics mDisplayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
			int ScreenWidth = mDisplayMetrics.widthPixels;
			int ScreenHeight = mDisplayMetrics.heightPixels;
			
			int laywidth=ScreenWidth*5/6;
			int layheight=ScreenHeight*5/8;
			
			//心情表情
			ImageView imgMoodFace = new ImageView(mContext);
			
			Animation zoomAnimation=AnimationUtils.loadAnimation(MyMoodActivity.this, R.anim.zoomout);
			zoomAnimation.setFillAfter(true);
			
			final LinearLayout lay = new LinearLayout(MyMoodActivity.this);
			lay.setOrientation(LinearLayout.VERTICAL);
			lay.setLayoutParams(new Gallery.LayoutParams(laywidth,layheight));
			
			final Mode mode = (Mode) modeList.get(position%modeList.size());
			if(mode.getSTATUS_CODE().equals("0"))
			{
				imgMoodFace.setImageResource(R.drawable.jiujie1);
				lay.setBackgroundResource(R.drawable.longnote0);
			}
			if(mode.getSTATUS_CODE().equals("1"))
			{
				imgMoodFace.setImageResource(R.drawable.shangxin1);
				lay.setBackgroundResource(R.drawable.longnote1);
			}
			if(mode.getSTATUS_CODE().equals("2"))
			{
				imgMoodFace.setImageResource(R.drawable.shengqi1);
				lay.setBackgroundResource(R.drawable.longnote2);
			}
			if(mode.getSTATUS_CODE().equals("3"))
			{
				imgMoodFace.setImageResource(R.drawable.pingjing1);
				lay.setBackgroundResource(R.drawable.longnote3);
			}
			if(mode.getSTATUS_CODE().equals("4"))
			{
				imgMoodFace.setImageResource(R.drawable.kaixin1);
				lay.setBackgroundResource(R.drawable.longnote4);
			}
			
			//心情主体内容
			TextView textContent  = new TextView(MyMoodActivity.this);
			textContent.setHeight(layheight*3/5);
			textContent.setTextColor(Color.rgb(32,35,31));
			textContent.setTextSize(16);
			textContent.setText(mode.getMODE_INFO());
			textContent.setPadding(laywidth/12, laywidth/20, laywidth/12, laywidth/20);
			lay.addView(textContent);
			
			//心情日期
			TextView textTitle  = new TextView(MyMoodActivity.this);
			textTitle.setWidth(laywidth);
			textTitle.setHeight(layheight/10);
			textTitle.setTextColor(Color.rgb(32,35,31));
			textTitle.setTextSize(14);
			textTitle.setGravity(Gravity.RIGHT);
			textTitle.setTextColor(Color.rgb(62,65,61));
			textTitle.setText(mode.getCREATE_TIME());
			textTitle.setPadding(5,0,laywidth/12,0);
			lay.addView(textTitle);
			
			
			LinearLayout laybottom = new LinearLayout(MyMoodActivity.this);
			laybottom.setOrientation(LinearLayout.HORIZONTAL);
		    
			ImageView imgCai = new ImageView(mContext);
			ImageView imgZan = new ImageView(mContext);
			imgCai.setImageResource(R.drawable.cai);
			imgZan.setImageResource(R.drawable.zan);
			
			
			TextView CaiCount = new TextView(MyMoodActivity.this);
			CaiCount.setWidth(laywidth/6);
			CaiCount.setHeight(layheight/8);
			CaiCount.setTextSize(12);
			CaiCount.setTextColor(Color.rgb(112,115,111));
			CaiCount.setGravity(Gravity.RIGHT);
			CaiCount.setText("101");
			laybottom.addView(CaiCount);
			laybottom.addView(imgCai);  
			
			TextView placeholder = new TextView(MyMoodActivity.this);
			placeholder.setWidth(laywidth/4);
			placeholder.setHeight(layheight/8);
			laybottom.addView(placeholder);
			
			laybottom.addView(imgMoodFace);
			
			TextView ZanCount = new TextView(MyMoodActivity.this);
			ZanCount.setWidth(laywidth/4);
			ZanCount.setHeight(layheight/8);
			ZanCount.setTextSize(12);
			ZanCount.setTextColor(Color.rgb(112,115,111));
			ZanCount.setGravity(Gravity.RIGHT);
			ZanCount.setText("901");
			laybottom.addView(ZanCount);
			laybottom.addView(imgZan);  
			lay.addView(laybottom);		
			
			
			LinearLayout laydelete = new LinearLayout(MyMoodActivity.this);
			laydelete.setOrientation(LinearLayout.HORIZONTAL);
			ImageView imgdelete = new ImageView(mContext);
			imgdelete.setImageResource(R.drawable.delete);
			imgdelete.setPadding(laywidth/20, 0, laywidth/5, 0);
			laydelete.addView(imgdelete);
			
			TextView placeholder1 = new TextView(MyMoodActivity.this);
			placeholder1.setWidth(laywidth*3/4);
			placeholder1.setHeight(laywidth/20);
			laydelete.addView(placeholder1);
			
			lay.addView(laydelete);		
			
			imgdelete.setOnClickListener(new OnClickListener() 
			{
				public void onClick(View v) 
				{
					AlertDialog.Builder builder = new Builder(MyMoodActivity.this);
					builder.setMessage("确定要删除该心情吗?");
					builder.setTitle("提示");
					builder.setPositiveButton("确认",
							new android.content.DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) 
								{
									dialog.dismiss();
									CustomToast.showToast(getApplicationContext(),"您删除了此心情！", 1000);
									
									//通知服务器删除心情
									Thread deleteThread = new Thread(new ServerInterfaceThread(1,mode.getMODE_ID()));
									deleteThread.start();
									modeList.remove(position);
									
									Animation zoomAnimation=AnimationUtils.loadAnimation(MyMoodActivity.this, R.anim.zoomin);
									zoomAnimation.setFillAfter(true);
									lay.startAnimation(zoomAnimation);			
									notifyDataSetChanged();
								}
							});
					builder.setNegativeButton("取消",
							new android.content.DialogInterface.OnClickListener() {
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
			});
			
			imgMoodFace.setOnClickListener(new OnClickListener() 
			{
				public void onClick(View v) {
					Intent intent=new Intent(MyMoodActivity.this,ViewCommentsActivity.class);
					intent.putExtra("modeID",mode.getMODE_ID()); 
					intent.putExtra("modeContent",mode.getMODE_INFO()); 
					intent.putExtra("modeAuthor",mode.getACCOUNT()); 
					startActivity(intent);
					overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
				}
			});
			
			textContent.setOnClickListener(new OnClickListener() 
			{
				public void onClick(View v) 
				{
					Intent intent=new Intent(MyMoodActivity.this,ViewCommentsActivity.class);
					intent.putExtra("modeID",mode.getMODE_ID()); 
					intent.putExtra("modeContent",mode.getMODE_INFO()); 
					intent.putExtra("modeAuthor",mode.getACCOUNT()); 
					startActivity(intent);
					overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
				}
			});
			
			lay.startAnimation(zoomAnimation);			
			return lay;
		}
	}

	
	public static String convertStreamToString(InputStream is) 
	{
		  BufferedReader reader = null;
		  try {
		   reader = new BufferedReader(new InputStreamReader(is,"UTF-8"), 512 * 1024);
		  } catch (UnsupportedEncodingException e1)
		  {
		   e1.printStackTrace();
		  }
		  StringBuilder sb = new StringBuilder();

		  String line = null;
		  try {
		   while ((line = reader.readLine()) != null) {
		     sb.append(line);
		   }
		  } catch (IOException e) {
		   Log.e("DataProvier convertStreamToString", e.getLocalizedMessage(),
		     e);
		  } finally {
		   try {
		    is.close();
		   } catch (IOException e) {
		    e.printStackTrace();
		   }
		  }
		 return sb.toString();
	}
	
	private boolean getMoodListFromServer(String validateUrl)
	{
		// 用于标记操作状态
		boolean operState = true;
		HttpPost httpRequest = new HttpPost(validateUrl);
		List params = new ArrayList();
		params.add(new BasicNameValuePair("func_type", "myself_mode"));
		params.add(new BasicNameValuePair("username", username));
		
		try {
			// 发出HTTP request
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			// 取得HTTP response
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);

			// 若状态码为200 ok
			if (httpResponse.getStatusLine().getStatusCode() == 200) 
			{
				// 取出回应字串
				InputStream inputStream = httpResponse.getEntity().getContent();
				jsonData = convertStreamToString(inputStream);
			}
		} catch (Exception e) 
		{
			operState=false;
			e.printStackTrace();
		}
		return operState;
	}
	
	private boolean deleteOneMoodToServer(String validateUrl,String moodid)
	{
		// 用于标记操作状态
		boolean operState = true;
		HttpPost httpRequest = new HttpPost(validateUrl);
		List params = new ArrayList();
		params.add(new BasicNameValuePair("func_type", "delete_mode"));
		params.add(new BasicNameValuePair("mode_id", moodid));
		
		try {
			// 发出HTTP request
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			// 取得HTTP response
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);

			// 若状态码为200 ok
			if (httpResponse.getStatusLine().getStatusCode() == 200) 
			{
				
			}
		} catch (Exception e) 
		{
			operState=false;
			e.printStackTrace();
		}
		return operState;
	}
	
	Handler withServerOperHandler = new Handler() 
	{
		public void handleMessage(Message msg) 
		{
			String operState = msg.getData().getString("operState");
			if (proDialog != null) 
			{
				proDialog.dismiss();
			}
			
			if (operState.equals("NetError")) 
			{
				Toast.makeText(MyMoodActivity.this,"获取我的心情列表失败:\n1.请检查您网络连接!", Toast.LENGTH_SHORT).show();
			}
			else if(operState.equals("getListSuccess"))
			{
				createNoteWallContent();
			}
		}
	};

	//处理登陆线程，若成功转向别的Activity，否则交给Handler处理，显示一个toast
	class ServerInterfaceThread implements Runnable 
	{
		private int operatorType;
		private String modeid;
		ServerInterfaceThread(int operType)
		{
			operatorType = operType;
		}
		
		ServerInterfaceThread(int operType,String mi)
		{
			operatorType = operType;
			modeid = mi;
		}
		public void run() 
		{
			if(operatorType == 0)
			{
				getMoodFromServer();
			}
			else
			{
				deleteMoodToServer();
			}
		}
		
		private void getMoodFromServer()
		{
			modeList = new ArrayList<Mode>();
			String validateURL = StatusCodeUtil.MODE_URL;
			boolean operState = getMoodListFromServer(validateURL);
			try 
			{
				if(jsonData!=null)
				{
					JSONArray json=new JSONArray(jsonData);
					for(int i=0;  i<json.length(); i++)
					{
						JSONObject jo = json.getJSONObject(i);
						Mode usermode = new Mode();
						usermode.setMODE_ID(jo.getString("MODE_ID"));
						usermode.setSTATUS_CODE(jo.getString("STATUS_CODE"));
						usermode.setMODE_INFO(jo.getString("MODE_INFO"));
						usermode.setCREATE_TIME(jo.getString("CREATE_TIME"));
						usermode.setACCOUNT(jo.getString("ACCOUNT"));
						modeList.add(usermode);
					}
				}
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			if (operState) 
			{
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("operState", "getListSuccess");
				message.setData(bundle);
				withServerOperHandler.sendMessage(message);
			} 
			else 
			{
				// 通过调用handler来通知UI主线程更新UI,
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("operState", "NetError");
				message.setData(bundle);;
				withServerOperHandler.sendMessage(message);
			}
		}
		
		private void deleteMoodToServer()
		{

			String validateURL = StatusCodeUtil.MODE_URL;
			boolean operState = deleteOneMoodToServer(validateURL,modeid);
			if (operState) 
			{
				proDialog.dismiss();
			} 
			else 
			{
				// 通过调用handler来通知UI主线程更新UI,
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("operState", "NetError");
				message.setData(bundle);;
				withServerOperHandler.sendMessage(message);
			}
		}
	}
}