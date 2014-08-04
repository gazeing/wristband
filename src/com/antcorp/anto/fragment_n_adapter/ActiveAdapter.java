package com.antcorp.anto.fragment_n_adapter;

import java.util.ArrayList;

import com.antcorp.anto.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ActiveAdapter extends ArrayAdapter<Active> {

	@SuppressWarnings("unused")
	private Context context;
	private ArrayList<Active> actives;

	public ActiveAdapter(Context context, ArrayList<Active> objects) 
	{
		super(context, R.layout.item_active, objects);
		this.context = context;
		this.actives = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.item_active, null);
		}
		
		Active act = actives.get(position);
		
		TextView actName = (TextView) v.findViewById(R.id.textView_name);
		if (actName != null)
			actName.setText(act.name);
		
		TextView actTag = (TextView) v.findViewById(R.id.textView_tag);
		if (actTag != null)
			actTag.setText(act.tagName);
		
		return v;
	}
	
}
