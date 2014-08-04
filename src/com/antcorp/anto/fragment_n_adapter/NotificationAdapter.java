package com.antcorp.anto.fragment_n_adapter;

import java.net.MalformedURLException;
import java.util.ArrayList;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.widget.ImageThreadLoader.ImageLoadedListener;
import com.antcorp.anto.FullMapActivity;
import com.antcorp.anto.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NotificationAdapter extends ArrayAdapter<Notification> {

	private Context context;
	private ArrayList<Notification> notifications;

//	private ImageThreadLoader imageLoader = new ImageThreadLoader();
	ImageView img_icon;

	public NotificationAdapter(Context context, ArrayList<Notification> objects) {
		super(context, R.layout.item_notification, objects);
		this.context = context;
		this.notifications = objects;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

			LayoutInflater vi = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.item_notification, null);

		Notification noti = notifications.get(position);

		TextView tv_name = (TextView) v.findViewById(R.id.textView_name);
		if (tv_name != null)
			tv_name.setText(noti.sender_name+" "+noti.sender_surname);
		
		TextView tv_about = (TextView) v.findViewById(R.id.textView_about);
		if (tv_about != null)
			tv_about.setText(noti.about_name + " "+noti.about_surname);

		TextView tv_time = (TextView) v.findViewById(R.id.textView_time);
		if (tv_time != null) {
			tv_time.setText(noti.lastupdated);
		}

		img_icon = (ImageView) v.findViewById(R.id.imageView_icon);
		try {
				Bitmap cachedImage = GlobalData.m_imageLoader.loadImage(noti.img,
						new ImageLoadedListener() {
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

			+ noti.img + e.getMessage());
		}

		ImageView img_nav = (ImageView) v.findViewById(R.id.imageView_nav);
		if (img_nav != null) {
			img_nav.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO: check if it is ok?
					ListView parent = (ListView) v.getParent().getParent();
					int pos = parent.getPositionForView((View) v.getParent());
					Notification noti = notifications.get(pos); 
					MyLog.i("longitude = " + noti.lon + ",   latitude ="
							+ noti.lat);
					
					if(noti.isFromMe)
						showMap(noti);
					else
						showBuildInMap(noti);

				}


			});
		}

		return v;
	}
	
	protected void showBuildInMap(Notification noti) {
		String lastmsg ="";
		if(noti.chat_list.size()>0)
			lastmsg=noti.chat_list.get(noti.chat_list.size()-1).msg;
		final String uri = "http://maps.google.com/maps?q="
				+ noti.lat + ',' + noti.lon + "(" + noti.sender_name
				+ ": " + lastmsg + ")&z=15";
		
//		final String uri = "http://maps.google.com/maps?saddr="+GlobalData.lastestLatitude+','+GlobalData.lastestLongitude+"&daddr="
//		+ noti.lat + ',' + noti.lon +"&z=15";

		context.startActivity(new Intent(
				android.content.Intent.ACTION_VIEW, Uri.parse(uri))); 
		
	}

	private void showMap(Notification noti) {
		GlobalData.m_map_chatMsgs.clear();
		for(ChatMsg chat:noti.chat_list){
			boolean isFromMe = chat.getSenderId().equals(GlobalData.m_antOUser.id);
			if(isFromMe){
				//add sender msg to map
				GlobalData.m_map_chatMsgs.add(chat);
			}
		}
		Intent i = new Intent(context, FullMapActivity.class);
//		i.putExtras(getIntent().getExtras());
//		i.putExtra("jsonArray", lastedJSONMsgs);
		i.putExtra("from", "msg");
		context.startActivity(i);
		
	}

}
