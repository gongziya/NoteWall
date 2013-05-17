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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qihoo.login.LoginActivity;
import com.qihoo.util.StatusCodeUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NoteWallActivity extends Activity {

	iGallery gallery1;
	iGallery gallery2;
	iGallery gallery3;
	
	ArrayList modeList = null; 
	
	String jsonData;
	
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
        setContentView(R.layout.notewall);
 
        setBackWall();
        
        setBtnClickListener();
        
        createRandModeList();
    }
	
	private void setBackWall()
	{
		 LinearLayout layContent = (LinearLayout) this.findViewById(R.id.notewallContent);
	
		 Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.wall6);  
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
        	NoteWallActivity.this.finish();
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
				NoteWallActivity.this.finish();
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
		String username = share.getString(SHARE_LOGIN_USERNAME, "");
		boolean status = share.getBoolean(SHARE_LOGIN_STATUS, false);
		if(status) 
		{
			Intent intent = new Intent();
			intent.setClass(NoteWallActivity.this, NotePublishActivity.class);
			startActivityForResult(intent,101);
			overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
		} 
		else {
			Toast.makeText(NoteWallActivity.this, "请先登录！",Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setClass(NoteWallActivity.this, LoginActivity.class);
			intent.putExtra("LoginGo","addNote"); 
			startActivityForResult(intent,0);
			overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
		}
	}
	
	
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	 {
		 if(101==requestCode)
		 {
			 if(resultCode==1)
			 {
				 createRandModeList();
			 }
		 }
		 super.onActivityResult(requestCode, resultCode, data);
	 }
	 
	
	private void createRandModeList() 
	{
		modeList = new ArrayList<Mode>();
		proDialog = ProgressDialog.show(NoteWallActivity.this, "连接中..",	"正在获取数据..请稍后....", true, true);
	
		Thread getListThread = new Thread(new GetMoodListThread());
		getListThread.start();
		
	}
	
	private void createNoteWallContent()
	{
		gallery1 = (iGallery) this.findViewById(R.id.image_gallery1);	
		ImageAdapter imageadapter1 = new ImageAdapter(NoteWallActivity.this);
    	imageadapter1.setFlag(0);
    	gallery1.setAdapter(imageadapter1);
		gallery1.setSelection(0);
		
		/*
		gallery1 = (iGallery) this.findViewById(R.id.image_gallery1);
    	gallery2 = (iGallery) this.findViewById(R.id.image_gallery2);
    	gallery3 = (iGallery) this.findViewById(R.id.image_gallery3);
    	

    	ImageAdapter imageadapter1 = new ImageAdapter(NoteWallActivity.this);
    	imageadapter1.setFlag(0);
		
    	ImageAdapter imageadapter2 = new ImageAdapter(NoteWallActivity.this);
    	imageadapter2.setFlag(1);
    	
    	ImageAdapter imageadapter3 = new ImageAdapter(NoteWallActivity.this);
    	imageadapter3.setFlag(2);
    	
    	gallery1.setAdapter(imageadapter1);
		gallery1.setSelection(0);
		
		gallery2.setAdapter(imageadapter2);
		gallery2.setSelection(0);
		
		gallery3.setAdapter(imageadapter3);
		gallery3.setSelection(0);
		*/
	}
	
	class ImageAdapter extends BaseAdapter{
    	
    	private Context mContext;
    	private int flag;
    	
    	public ImageAdapter(Context context)
    	{
    		this.mContext = context;
    	}
    	
		public int getCount() {
				return (modeList.size())/3;
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

		public View getView(int position, View convertView, ViewGroup parent) 
		{
		
			DisplayMetrics mDisplayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
			int ScreenWidth = mDisplayMetrics.widthPixels;
			int ScreenHeight = mDisplayMetrics.heightPixels;
			
			Animation zoomAnimation=AnimationUtils.loadAnimation(NoteWallActivity.this, R.anim.zoomout);
			zoomAnimation.setFillAfter(true);
			
			int laywidth=ScreenWidth/2;
			int layheight=ScreenHeight*3/10;
			
			LinearLayout layOuter = new LinearLayout(NoteWallActivity.this);
			layOuter.setOrientation(LinearLayout.VERTICAL);
			layOuter.setLayoutParams(new Gallery.LayoutParams(laywidth,layheight*3));
			layOuter.setPadding(0, 5, 0, 5);
			
			for(int flag=0;flag<3;flag++)
			{
				//心情表情
				ImageView imgMoodFace = new ImageView(mContext);
			
				LinearLayout lay = new LinearLayout(NoteWallActivity.this);
				lay.setOrientation(LinearLayout.VERTICAL);
				lay.setLayoutParams(new Gallery.LayoutParams(laywidth,layheight));
			
				final Mode mode = (Mode) modeList.get((position*3+flag)%(modeList.size()));
				if(mode.getSTATUS_CODE().equals("0"))
				{
					imgMoodFace.setImageResource(R.drawable.jiujie);
					lay.setBackgroundResource(R.drawable.longnote0);
				}
				else if(mode.getSTATUS_CODE().equals("1"))
				{
					imgMoodFace.setImageResource(R.drawable.shangxin);
					lay.setBackgroundResource(R.drawable.longnote1);
				}
				else if(mode.getSTATUS_CODE().equals("2"))
				{
					imgMoodFace.setImageResource(R.drawable.shengqi);
					lay.setBackgroundResource(R.drawable.longnote2);
				}
				else if(mode.getSTATUS_CODE().equals("3"))
				{
					imgMoodFace.setImageResource(R.drawable.pingjing);
					lay.setBackgroundResource(R.drawable.longnote3);
				}
				else if(mode.getSTATUS_CODE().equals("4"))
				{
					imgMoodFace.setImageResource(R.drawable.kaixin);
					lay.setBackgroundResource(R.drawable.longnote4);
				}
				else
				{
					imgMoodFace.setImageResource(R.drawable.pingjing);
					lay.setBackgroundResource(R.drawable.longnote3);
				}
			
				//心情主体内容
				TextView textContent  = new TextView(NoteWallActivity.this);
				textContent.setWidth(laywidth);
				textContent.setHeight(layheight*3/5);
				textContent.setTextColor(Color.rgb(32,35,31));
				textContent.setTextSize(12);
				textContent.setText(mode.getMODE_INFO());
				textContent.setPadding(laywidth/10, laywidth/20, laywidth/10+5, laywidth/20);
				lay.addView(textContent);
			
				//心情作者
				TextView textTitle  = new TextView(NoteWallActivity.this);
				textTitle.setWidth(laywidth);
				textTitle.setTextColor(Color.rgb(32,35,31));
				textTitle.setTextSize(14);
				textTitle.setGravity(Gravity.RIGHT);
				textTitle.setText(mode.getACCOUNT());
				textTitle.setPadding(5,0,laywidth/8,0);
				lay.addView(textTitle);
			
			
				LinearLayout laybottom = new LinearLayout(NoteWallActivity.this);
				laybottom.setOrientation(LinearLayout.HORIZONTAL);
		    
				ImageView imgCai = new ImageView(mContext);
				ImageView imgZan = new ImageView(mContext);
				imgCai.setImageResource(R.drawable.cai);
				imgZan.setImageResource(R.drawable.zan);
			
				imgCai.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						CustomToast.showToast(getApplicationContext(), "您踩了此心情!",1000);
					}
				});
			
				imgZan.setOnClickListener(new OnClickListener() {
				
					public void onClick(View v) 
					{
						CustomToast.showToast(getApplicationContext(), "您赞扬了此心情!",1000);
					}
				});
			
				TextView caiCount = new TextView(NoteWallActivity.this);
				caiCount.setWidth(laywidth/10);
				caiCount.setHeight(14);
				caiCount.setGravity(Gravity.RIGHT);
				laybottom.addView(caiCount);
				laybottom.addView(imgCai);  
			
				TextView placeholder = new TextView(NoteWallActivity.this);
				placeholder.setWidth(laywidth/10);
				placeholder.setHeight(14);
				laybottom.addView(placeholder);
				laybottom.addView(imgMoodFace);
			
			
				TextView zanCount = new TextView(NoteWallActivity.this);
				zanCount.setWidth(laywidth/7);
				zanCount.setHeight(14);
				laybottom.addView(zanCount);
				laybottom.addView(imgZan);  
			
				lay.addView(laybottom);		
			
				imgMoodFace.setOnClickListener(new OnClickListener() 
				{
					public void onClick(View v) 
					{
						Intent intent=new Intent(NoteWallActivity.this,ViewCommentsActivity.class);
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
				
						Intent intent=new Intent(NoteWallActivity.this,ViewCommentsActivity.class);
						intent.putExtra("modeID",mode.getMODE_ID()); 
						intent.putExtra("modeContent",mode.getMODE_INFO()); 
						intent.putExtra("modeAuthor",mode.getACCOUNT()); 
						startActivity(intent);
						overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
					}				
				});
				
			//	lay.startAnimation(zoomAnimation);			
				layOuter.addView(lay);
			}
			layOuter.startAnimation(zoomAnimation);
			return layOuter;
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
	
	Handler getMoodListHandler = new Handler() {

		public void handleMessage(Message msg) 
		{
			String operState = msg.getData().getString("operState");
			if (proDialog != null) {
				proDialog.dismiss();
			}
			
			if (operState.equals("NetError")) 
			{
			Toast.makeText(NoteWallActivity.this,"获取广场心情列表失败:\n1.请检查您网络连接!", Toast.LENGTH_SHORT).show();
			}
			else if(operState.equals("getListSuccess"))
			{
				createNoteWallContent();
			}
		}
	};
	
	private boolean getMoodListFromServer(String validateUrl)
	{
		// 用于标记操作状态
		boolean operState = true;
		HttpPost httpRequest = new HttpPost(validateUrl);
		List params = new ArrayList();
		params.add(new BasicNameValuePair("func_type", "public_mode"));
		
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

	class GetMoodListThread implements Runnable 
	{
		public void run() 
		{
			String validateURL = StatusCodeUtil.MODE_URL;
			boolean operState = getMoodListFromServer(validateURL);
			
			try {
				if(jsonData!=null)
				{
					JSONArray json=new JSONArray(jsonData);
					for(int i=0; i<json.length(); i++)
					{
						JSONObject jo = json.getJSONObject(i);
						Mode usermode = new Mode();
						usermode.setMODE_ID(jo.getString("MODE_ID"));
						usermode.setSTATUS_CODE( jo.getString("STATUS_CODE"));
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
				// 通过调用handler来通知UI主线程更新UI,
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("operState", "getListSuccess");
				message.setData(bundle);
				getMoodListHandler.sendMessage(message);
			} 
			else 
			{
				// 通过调用handler来通知UI主线程更新UI,
				Message message = new Message();
				Bundle bundle = new Bundle();
				bundle.putString("operState", "NetError");
				message.setData(bundle);
				getMoodListHandler.sendMessage(message);
			}
		}
	}
}