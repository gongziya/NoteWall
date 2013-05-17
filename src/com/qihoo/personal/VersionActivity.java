package com.qihoo.personal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.qihoo.R;

public class VersionActivity extends Activity {
	
	ImageView versionImage;

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.version);
		versionImage = (ImageView)findViewById(R.id.btnGoBack);
		versionImage.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v) 
			{
				VersionActivity.this.finish();
				overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
			}
		});
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) 
    { 
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
        { 
        	setResult(0);
        	VersionActivity.this.finish();
			overridePendingTransition(R.anim.outtoright,R.anim.infromleft);
            return false; 
        } 
        return false; 
    }
	
}
