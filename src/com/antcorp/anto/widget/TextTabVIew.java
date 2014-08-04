package com.antcorp.anto.widget;


import com.antcorp.anto.R;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextTabVIew extends LinearLayout {
	public TextTabVIew(Context context){
		super(context);
	}

	public TextTabVIew(Context context,String tag) {
		super(context);

		setGravity(Gravity.CENTER);
		this.setBackgroundResource(R.drawable.state_backcolor);
		this.setOrientation(LinearLayout.VERTICAL);

		TextView tv = new TextView(context);
		tv.setGravity(Gravity.CENTER_HORIZONTAL);
		tv.setText(tag);
		tv.setTextSize(14);

		tv.setMaxLines(1);
		addView(tv);
	}

}
