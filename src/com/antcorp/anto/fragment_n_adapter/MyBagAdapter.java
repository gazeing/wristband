package com.antcorp.anto.fragment_n_adapter;

import java.util.ArrayList;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyBagAdapter extends ArrayAdapter<MyTag> {

	public ArrayList<MyTag> tags = new ArrayList<MyTag>();
	Context context;

	public MyBagAdapter(Context context, ArrayList<MyTag> objects) 
	{
		super(context, R.layout.item_mytag, objects);
		this.context = context;
		this.tags = objects;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.item_mytag, null);
		}

	//	v.setClickable(true);
		MyTag tag = tags.get(position);

		boolean isTypeWB = tag.tagType <2;
		boolean isTypeOn = false;
		
		for(Connection c:GlobalData.m_connetions){
			if (c.tag_id.equals(tag.tagId))
				if(c.current_active.equals("1"))
					isTypeOn = true;
		}
		
		ImageView tagIcon = (ImageView) v.findViewById(R.id.imageView_icon);
		if(tagIcon != null){
			int resId = R.drawable.type_wb_1;
			switch(tag.tagType ){
			case 1:
				resId = R.drawable.type_wb_1;
				break;
			case 2:
				resId = R.drawable.type_smart_tag;
				break;
			}
			
			tagIcon.setBackgroundResource(resId);
		}
		

		TextView tagName = (TextView) v.findViewById(R.id.textView_tagname);
		if (tagName != null){
			tagName.setText(tag.tagName);
			tagName.setTextColor(Color.rgb(255,204,0));
		}

		TextView tagValue = (TextView) v.findViewById(R.id.textView_tagvalue);
		if (tagValue != null){
			tagValue.setText(tag.tagId);
			tagValue.setTextColor(Color.rgb(108,7,0));
		}

		ImageView tagStatus = (ImageView) v.findViewById(R.id.imageView_status);
		if (tagStatus != null) {
			int resourceId = R.drawable.bag_tag_on;
			int type = 2 * (isTypeWB ? 1 : 0) + (isTypeOn ? 1 : 0);
			
			switch (type) {
			case 0:
				resourceId = R.drawable.bag_tag_empty;
				break;
				
			case 1:
				resourceId = R.drawable.bag_tag_on;
				break;
				
			case 2:
				resourceId = R.drawable.bag_wb_empty;
				break;
				
			case 3:
				resourceId = R.drawable.bag_wb_on;
				break;
				
			default:
				resourceId = R.drawable.bag_wb_empty;
				break;
				
			}
			tagStatus.setBackgroundResource(resourceId);
		}

		return v;
	}

}
