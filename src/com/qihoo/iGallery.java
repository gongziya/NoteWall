package com.qihoo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class iGallery extends Gallery 
{  
  
	 private MotionEvent e;

	 public iGallery(Context context) {
         super(context);
         // TODO Auto-generated constructor stub
	 }
	 
    public iGallery(Context context, AttributeSet attrs) 
    {  
        super(context, attrs);  
    }  
  
    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) 
    {  
        return e2.getX() > e1.getX();  
    }  
  
    public boolean onInterceptTouchEvent(MotionEvent ev) {
            // TODO Auto-generated method stub
            boolean bb=super.onInterceptTouchEvent(ev);
            if(ev.getAction()==MotionEvent.ACTION_DOWN){
                    e=MotionEvent.obtain(ev);
                    super.onTouchEvent(ev);
            }else if(ev.getAction()==MotionEvent.ACTION_MOVE){
                    //手指触摸的大小.........这儿我设的是20像素
                    if(Math.abs(ev.getX()-e.getX())>15 || Math.abs(ev.getY()-e.getY())>15){
                            bb=true;
                    }
            }
            return bb;
    }

    
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
    {  
        int keyCode;  
        if (isScrollingLeft(e1, e2))
        {        
            keyCode = KeyEvent.KEYCODE_DPAD_LEFT;  
        } 
        else 
        {  
            keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;  
        }  
        onKeyDown(keyCode, null);  
        return true;  
    }  
}  