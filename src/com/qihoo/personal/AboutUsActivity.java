package com.qihoo.personal;

import com.qihoo.MyMoodActivity;
import com.qihoo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutUsActivity extends Activity {
	TextView aboutUs; 

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.aboutus);
		aboutUs = (TextView)findViewById(R.id.aboutUsView);
		
		aboutUs.setText("本软件由飞扬八期小叮当一队开发!"+"\n感谢您的使用！" + "\n" );
	
		
		// 为返回按钮添加监听方法
		ImageView btnBack = (ImageView) findViewById(R.id.btnGoBack);
		btnBack.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v)
			{
				AboutUsActivity.this.finish();
				overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
			}
		});
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
    { 
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
        { 
        	setResult(0);
        	AboutUsActivity.this.finish();
			overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
            return false; 
        } 
        return false; 
    }

}
