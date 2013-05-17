package com.qihoo.personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.qihoo.R;

public class SuggestActivity extends Activity {
	
	TextView suggest; 
	ImageView suggestImage; 

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.suggest);
		suggest = (TextView)findViewById(R.id.suggestView);
		suggestImage = (ImageView)findViewById(R.id.btnGoBack);
		suggest.setText("您对本软件有任何意见或建议，" + "\n" + "请发送邮件到guzhou1990@126.com," + "\n" + "谢谢您的反馈！");
		suggestImage.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				SuggestActivity.this.finish();
				overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
			}
		});
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
    { 
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
        { 
        	setResult(0);
        	SuggestActivity.this.finish();
			overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
            return false; 
        } 
        return false; 
    }
}
