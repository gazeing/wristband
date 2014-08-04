package com.antcorp.anto.fragment_n_adapter;

import java.net.MalformedURLException;
import java.util.ArrayList;

import com.antcorp.anto.EditTagActivity;
import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.widget.ImageThreadLoader.ImageLoadedListener;
import com.antcorp.anto.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ColonyAdapter extends ArrayAdapter<Colony> {

	private Context context;
	private ArrayList<Colony> connections;
//	private ImageThreadLoader imageLoader = new ImageThreadLoader();
	ImageView img_icon ;

	public ColonyAdapter(Context context, ArrayList<Colony> objects) {
		super(context, R.layout.item_connection, objects);
		this.context = context;
		this.connections = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if (connections == null || connections.size() < position)
			return v;
		
	//	if (v == null) {
			LayoutInflater vi = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.item_connection, null);
		//}

		Colony connection = connections.get(position);

		boolean isActive = false;

		for (Connection c : GlobalData.m_connetions) {
			if (c.getColony_member_id().equals(connection.colony_member_id)) {

				isActive = (c.getCurrent_active().equals("1"));
			}
		}

		TextView connectionName = (TextView) v.findViewById(R.id.textView_name);
		if (connectionName != null)
			connectionName.setText(connection.name + " " + connection.surname);

		TextView connectionInfo = (TextView) v.findViewById(R.id.textView_info);
		if (connectionInfo != null)
			connectionInfo.setText(connection.info);

		ImageView active = (ImageView) v.findViewById(R.id.imageView_active);
		if (isActive)
			active.setVisibility(View.VISIBLE);
		else
			active.setVisibility(View.INVISIBLE);

		img_icon = (ImageView) v.findViewById(R.id.imageView_icon);
		if (img_icon != null) {
			if (connection.is_member_owner.equals("1"))
				img_icon.setImageResource(R.drawable.colony_owner_default_image);
			else if (connection.img.length() <= 0)
					img_icon.setImageResource(R.drawable.default_user);
			else{
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
										return;
									}
									
									img_icon.setImageBitmap(imageBitmap);
									notifyDataSetChanged();
								}
							});
					
					
					if (cachedImage != null) {
						img_icon.setImageBitmap(cachedImage);
//						//set to cache
//						GlobalData.m_image_cache.put(connection.img, cachedImage);
//					}
					}
					
				} catch (MalformedURLException e) {
					MyLog.i("Bad remote image URL: "+ connection.img+ e.getMessage());
				}


			}
		}

		ImageView imageEdit = (ImageView) v.findViewById(R.id.imageView_edit);
		if (imageEdit != null) {
			imageEdit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ListView parent = (ListView) v.getParent().getParent();
					int pos = parent.getPositionForView((View) v.getParent());
					Colony c = connections.get(pos);

					String tagid = "";
					for (Connection co : GlobalData.m_connetions) {
						if (co.getColony_member_id().equals(c.colony_member_id)) {
							tagid = co.getTag_id();

						}
					}

					Intent i = new Intent(context, EditTagActivity.class);
					i.putExtra("tagid", tagid);
					i.putExtra("memberid", c.getColony_member_id());
					i.putExtra("addInfo", c.info);
					i.putExtra("userImage", c.img);
					i.putExtra("contactName", c.contactName);
					i.putExtra("contactSurName", c.contactSurName);
					i.putExtra("contactNum", c.contactPhone1);
					i.putExtra("name", c.name);
					i.putExtra("surname", c.surname);
					i.putExtra("isMemberOwner", c.getIs_member_owner());
					context.startActivity(i);
				}
			});
		}

		return v;
	}
}
