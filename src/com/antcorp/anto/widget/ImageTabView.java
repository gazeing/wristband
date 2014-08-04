package com.antcorp.anto.widget;

import com.antcorp.anto.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

@SuppressLint("ViewConstructor")
public class ImageTabView extends LinearLayout {  
    ImageView imageView ; 
    


	
	public ImageTabView(Context c, int drawable) {  
	    super(c);  
	    imageView = new ImageView(c);  
	    imageView.setImageResource(drawable);
	    LayoutParams lp  = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    lp.setMargins(2, 2, 2, 2);
	    imageView.setLayoutParams(lp);
	    
		setGravity(Gravity.CENTER);
		this.setBackgroundResource(R.drawable.state_backcolor);
		this.setOrientation(LinearLayout.VERTICAL);

		addView(imageView);



	    }  

	 
}