package com.qihoo.launcher;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.qihoo.MainActivity;
import com.qihoo.R;

public class FirstActivity extends Activity {

	private ViewPager viewPager;
	private ViewGroup main, group;
	private ImageView imageView;
	private ImageView[] imageViews;
	private ArrayList<View> list;
	private ImageButton enterButton;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		Boolean isFirstIn = false;
		SharedPreferences pref = getSharedPreferences("FirstActivity", 0);
		isFirstIn = pref.getBoolean("isFirstIn", true);
		
		if (!isFirstIn)
		{
			Intent intent = new Intent();
            intent.setClass(FirstActivity.this, MainActivity.class);
            startActivity(intent);
            FirstActivity.this.finish();
		}
		else
		{
			LayoutInflater inflater = getLayoutInflater();
			list = new ArrayList<View>();
			list.add(inflater.inflate(R.layout.item0, null));
			list.add(inflater.inflate(R.layout.item1, null));
			list.add(inflater.inflate(R.layout.item2, null));
			list.add(inflater.inflate(R.layout.item3, null));
			
			imageViews = new ImageView[list.size()];
			ViewGroup main = (ViewGroup) inflater.inflate(R.layout.activity_main, null);  

			ViewGroup group = (ViewGroup) main.findViewById(R.id.viewGroup);  
	  
	        viewPager = (ViewPager) main.findViewById(R.id.viewPager);  
	  
	        for (int i = 0; i < list.size(); i++) {  
	            imageView = new ImageView(FirstActivity.this);  
	            imageView.setLayoutParams(new LayoutParams(20,20));  
	            imageView.setPadding(0, 0, 0, 0);  
	            imageViews[i] = imageView;  
	            if (i == 0) {  
	                imageViews[i].setBackgroundResource(R.drawable.guide_dot_red);  
	            } else {  
	                imageViews[i].setBackgroundResource(R.drawable.guide_dot_black);  
	            }  
	            
	            if (i == list.size() - 1)
	            {
	            	enterButton = (ImageButton)(list.get(i).findViewById(R.id.enterButton));
	            	enterButton.setOnClickListener(new EnterOnClick());
	            	
	            	FrameLayout.LayoutParams absParams = 
	            		    (FrameLayout.LayoutParams)enterButton.getLayoutParams();
            		absParams.bottomMargin = 65;
            		absParams.rightMargin = 110;
            		enterButton.setLayoutParams(absParams);
	            }
	            
	            group.addView(imageView);  
	        }  
	        
	        setContentView(main);  
	  
	        viewPager.setAdapter(new MyAdapter());  
	        viewPager.setOnPageChangeListener(new MyListener()); 
	        
	        Editor editor = pref.edit();
	        /**
	         * 在真实使用时，需要将true更改为false
	         */
	        editor.putBoolean("isFirstIn", false);
	        editor.commit();
		}
		
        
    }  
  
	class EnterOnClick implements View.OnClickListener
	{

		public void onClick(View arg0) 
		{
		  Intent intent = new Intent();
          intent.setClass(FirstActivity.this, MainActivity.class);
          startActivity(intent);
          
          FirstActivity.this.finish();
          }
	}
	
    class MyAdapter extends PagerAdapter {  
  
        public int getCount() {  
            return list.size();  
        }  
  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            return arg0 == arg1;  
        }  
  
        public int getItemPosition(Object object) {  
            // TODO Auto-generated method stub  
            return super.getItemPosition(object);  
        }  
  
        public void destroyItem(View arg0, int arg1, Object arg2) {  
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).removeView(list.get(arg1));  
        }  
  
        public Object instantiateItem(View arg0, int arg1) {  
            // TODO Auto-generated method stub  
            ((ViewPager) arg0).addView(list.get(arg1));  
            return list.get(arg1);  
        }  
  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
            // TODO Auto-generated method stub  
  
        }  
  
        public Parcelable saveState() {  
            // TODO Auto-generated method stub  
            return null;  
        }  
  
        public void startUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        public void finishUpdate(View arg0) {  
            // TODO Auto-generated method stub  
  
        }  
    }  
  
    class MyListener implements OnPageChangeListener {  
  
        public void onPageScrollStateChanged(int arg0) {  
            // TODO Auto-generated method stub  
  
        }  
  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
            // TODO Auto-generated method stub  
  
        }  
  
        public void onPageSelected(int arg0) {  
            for (int i = 0; i < imageViews.length; i++) 
            {  
                imageViews[arg0]  
                        .setBackgroundResource(R.drawable.guide_dot_red);  
                if (arg0 != i) 
                {  
                    imageViews[i]  
                            .setBackgroundResource(R.drawable.guide_dot_black);  
                }  
            }  
        }  
  
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
