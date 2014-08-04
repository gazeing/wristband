package com.antcorp.anto.widget;

import java.net.MalformedURLException;

import com.antcorp.anto.R;
import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.widget.ImageThreadLoader.ImageLoadedListener;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowPortraitDialog {

	Dialog alert;
	ImageView image;
	final int PORTRAIT_HEIGHT = 300;
	
	
	public Dialog ShowDialog(Activity activity,String name,String imgString){
		final Dialog addDialog = new Dialog(activity, R.style.AddBagDialog);
		addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		addDialog.setContentView(R.layout.dialog_show_portrait);
		
		TextView tv_name= (TextView) addDialog.findViewById(R.id.textView1);
		tv_name.setText(name);
		
		//set image
		image = (ImageView) addDialog.findViewById(R.id.imageView1);
		try {

			Bitmap cachedImage = GlobalData.m_imageLoader.loadImage(imgString,
					new ImageLoadedListener() {
						public void imageLoaded(Bitmap imageBitmap) {

							try {

								imageBitmap = Bitmap.createScaledBitmap(
										imageBitmap, PORTRAIT_HEIGHT, PORTRAIT_HEIGHT, true);

							} catch (Exception e) {
								MyLog.i(e);
							}
							image.setImageBitmap(imageBitmap);

						}
					});

			if (cachedImage != null) {
				image.setImageBitmap(cachedImage);

			}
		} catch (MalformedURLException e) {
			MyLog.i("Bad remote image URL: " + imgString + e.getMessage());
		}
		
		addDialog.show();
		
		alert = addDialog;
		return addDialog;
	}
}
