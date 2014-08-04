package com.antcorp.anto.fragment_n_adapter;

import java.net.MalformedURLException;
import java.util.ArrayList;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.widget.ImageThreadLoader.ImageLoadedListener;
import com.antcorp.anto.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogColonyAdapter extends ArrayAdapter<Colony> {

	@SuppressWarnings("unused")
	private Context context;
	private ArrayList<Colony> connections;
	
	ImageView img_icon ;
	
	public DialogColonyAdapter(Context context, ArrayList<Colony> objects) {
		super(context, R.layout.item_dialog_select_colony, objects);
		this.context = context;
		this.connections = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
	
			LayoutInflater vi = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.item_dialog_select_colony, null);
	

		Colony connection = connections.get(position);

		TextView connectionName = (TextView) v.findViewById(R.id.textView_name);
		if (connectionName != null)
			connectionName.setText(connection.name+" "+connection.surname);
		
//		TextView connectionSurName = (TextView) v.findViewById(R.id.textView_surname);
//		if (connectionSurName != null)
//			connectionSurName.setText(connection.surname);


		img_icon = (ImageView) v.findViewById(R.id.imageView1);
		if (img_icon != null) {
			if (connection.is_member_owner.equals("1"))
				img_icon.setImageResource(R.drawable.colony_owner_default_image);
			else {
				try {
					Bitmap cachedImage = GlobalData.m_imageLoader.loadImage(
							connection.img,
							new ImageLoadedListener() {
								@Override
								public void imageLoaded(Bitmap imageBitmap) {
									try {
										imageBitmap = Bitmap.createScaledBitmap(
												imageBitmap, 100, 100, true);
									} catch (Exception e) {
										MyLog.i(e);
									}
									img_icon.setImageBitmap(imageBitmap);
									notifyDataSetChanged();
								}
							});
					
					
					if (cachedImage != null) {
						img_icon.setImageBitmap(cachedImage);
					}
					
				} catch (MalformedURLException e) {
					MyLog.i("Bad remote image URL: "
							
							+ connection.img+ e.getMessage());
				}


			}
		}

		return v;
	}
}
