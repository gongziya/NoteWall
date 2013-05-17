package com.qihoo;

import com.qihoo.personal.PersonalActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	static final int NOTEWALLACTIVITY = 1;
	static final int MYMOODACTIVITY = 2;
	static final int PERSONALCENTER = 3;
	
	MyImageView modeSquare;
	MyImageView myModeWall;
	MyImageView personalCenter;
	
	ImageView goAhead;
	ImageView exitActivity;
	int latestActivity=1;
	
	private long mExitTime;
	
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState); 
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mainpanel); 
    
        
        goAhead = (ImageView)findViewById(R.id.btnGoAhead);
        goAhead.setOnClickListener(new OnClickListener() 
        {
			public void onClick(View v) 
			{
				createActivity(latestActivity);
			}
		});
        
        modeSquare=(MyImageView) findViewById(R.id.modeSquareWall);
        modeSquare.setOnClickListener(new OnClickListener() 
        {
			public void onClick(View v) 
			{
				createActivity(NOTEWALLACTIVITY);
			}
		});
        
        myModeWall=(MyImageView) findViewById(R.id.myModeWall);
        myModeWall.setOnClickListener(new OnClickListener() 
        {
			public void onClick(View v) 
			{
				createActivity(MYMOODACTIVITY);
			}
		});
        
        personalCenter=(MyImageView) findViewById(R.id.personalCenter);
        personalCenter.setOnClickListener(new OnClickListener() 
        {
			public void onClick(View v) 
			{
				createActivity(PERSONALCENTER);
			}
		});
        
    } 
    
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	latestActivity=requestCode;
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void createActivity(int activityFlag)
    {
    	if(NOTEWALLACTIVITY==activityFlag)
    	{
    		Intent intent=new Intent(MainActivity.this,NoteWallActivity.class);
			startActivityForResult(intent,NOTEWALLACTIVITY);
			overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
    	}
    	else if(MYMOODACTIVITY==activityFlag)
    	{
    		Intent intent=new Intent(MainActivity.this,MyMoodActivity.class);
			startActivityForResult(intent,MYMOODACTIVITY);
			overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
    	}
    	else if(PERSONALCENTER==activityFlag)
    	{
    		Intent intent=new Intent(MainActivity.this,PersonalActivity.class);
			startActivityForResult(intent,PERSONALCENTER);
			overridePendingTransition(R.anim.infromright,R.anim.outtoleft);
    	}
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) 
        {
                if ((System.currentTimeMillis() - mExitTime) > 2000) 
                {
                        Object mHelperUtils;
                        Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        mExitTime = System.currentTimeMillis();

                } else 
                {
                        finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
}
}