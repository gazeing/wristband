package com.antcorp.anto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.antcorp.anto.data.AntOUser;
import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.data.TagInfo;
import com.antcorp.anto.fragment_n_adapter.ChatMsg;
import com.antcorp.anto.fragment_n_adapter.MyTag;
import com.antcorp.anto.fragment_n_adapter.Notification;
import com.antcorp.anto.interfaces.ServiceInterface;
import com.antcorp.anto.network.AntRetStatus;
import com.antcorp.anto.service.AntNotificationService;
import com.antcorp.anto.widget.ComboBox;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.widget.TimeoutProgressDialog;
import com.antcorp.anto.widget.ImageThreadLoader.ImageLoadedListener;
import com.antcorp.anto.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class EditTagActivityBase extends Activity {

	final int TAKE_PHOTO_FOR_RESULT = 1001;
	final int CROP_PHOTO_FOR_RESULT = 1002;
	Uri picUri;
	Button btn_photo, btn_menu;
	Button btn;

	String path = "";
	int rotate = 0;

	TimeoutProgressDialog pd;

	ComboBox cb;
	EditText et_contactName, et_contactNum, et_additionalInfo,
			et_contactSurname;
	String tagid, name, surname, contactName, contactSurname, contactNum, img,
			additionInfo, memberid, isMemberOwner;
	String imgString;

	WebResponse mWebCall = new WebResponse();
	Intent mIntentBkgService = null;

	String isFromLogin = "0";

	// the image URI
	Uri uriSavedImage;

	int miDispDensity = 0;
	int miDispHeight = 0;
	int miDispWidth = 0;
	int miMetricType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mIntentBkgService = new Intent(this, AntNotificationService.class);
		startService(mIntentBkgService);
		bindService(mIntentBkgService, mConnection, Context.BIND_AUTO_CREATE);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// setContentView(R.layout.activity_edit_tag);
		// init();

		pd = new TimeoutProgressDialog(this);
		pd.setCanceledOnTouchOutside(false);
		pd.setCancelable(false);
	}

	@Override
	protected void onResume() {
		DefineScreenSize();

		if (miDispHeight > 1200) {
			et_additionalInfo.setLines(4);
		}

		super.onResume();
	}

	@SuppressWarnings("deprecation")
	protected void init() {
		Bundle b = getIntent().getExtras();

		if (b != null) {
			if (getIntent().hasExtra("isUserOwner")) {

				name = b.getString("name");
				surname = b.getString("surname");

				isFromLogin = "1";// we don't need judge if the value is true.
									// if it has, it is true.
			} else {

				tagid = b.getString("tagid");
				name = b.getString("name");
				surname = b.getString("surname");
				contactName = b.getString("contactName");
				contactSurname = b.getString("contactSurName");
				contactNum = b.getString("contactNum");
				img = b.getString("userImage");
				additionInfo = b.getString("addInfo");
				memberid = b.getString("memberid");
				isMemberOwner = b.getString("isMemberOwner");
			}
		}

		findWidgets();

		et_contactName.setText(contactName);
		et_contactSurname.setText(contactSurname);
		et_contactNum.setText(contactNum);
		et_additionalInfo.setText(additionInfo);

		btn_photo = (Button) findViewById(R.id.button_photo);
		btn_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				path = takePhoto();
			}
		});

		if (isMemberOwner == null)
			isMemberOwner = "0";

		if (isMemberOwner.compareTo("0") == 0 && memberid != null) {
			ImageView liTop_image = (ImageView) findViewById(R.id.imageView_antobanner);
			liTop_image
					.setImageResource(R.drawable.banner_read_info_top_settings);

			btn_menu = (Button) findViewById(R.id.button_menu);
			btn_menu.setClickable(true);
			btn_menu.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					EditTagActivityBase.this.openOptionsMenu();

				}
			});
		}

		// add default img
		if (img == null) {

		} else {
			try {

				Bitmap cachedImage = GlobalData.m_imageLoader.loadImage(img,
						new ImageLoadedListener() {
							public void imageLoaded(Bitmap imageBitmap) {

								try {
									imgString = convertPhoto(imageBitmap);
									imageBitmap = Bitmap.createScaledBitmap(
											imageBitmap, 100, 100, true);
								} catch (Exception e) {
									MyLog.i(e);
								}
								btn_photo
										.setBackgroundDrawable(new BitmapDrawable(
												imageBitmap));
							}
						});

				if (cachedImage != null) {
					imgString = convertPhoto(cachedImage);
					btn_photo.setBackgroundDrawable(new BitmapDrawable(
							cachedImage));

				}
			} catch (MalformedURLException e) {
				MyLog.i("Bad remote image URL: " + img + e.getMessage());
			}
		}



		btn = (Button) findViewById(R.id.button_register);

		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// avoid second click
				btn.setClickable(false);

				name = getName();// et_name.getText().toString();
				surname = getSurName();// et_surname.getText().toString();
				contactName = et_contactName.getText().toString();
				contactSurname = et_contactSurname.getText().toString();
				contactNum = et_contactNum.getText().toString();
				additionInfo = et_additionalInfo.getText().toString();

				setConnection(name, surname, contactName, contactSurname,
						contactNum, imgString, additionInfo, memberid);

			}
		});



	}

	protected String getSurName() {
		// TODO Auto-generated method stub
		return "";
	}

	protected String getName() {
		// TODO Auto-generated method stub
		return "";
	}

	protected void findWidgets() {

		et_contactName = (EditText) findViewById(R.id.editText_contactname);
		et_contactSurname = (EditText) findViewById(R.id.editText_contactSurname);
		et_contactNum = (EditText) findViewById(R.id.editText_contactNum);
		et_additionalInfo = (EditText) findViewById(R.id.editText_addtion_info);

	}

	protected void setConnection(String name2, String surname2,
			String contactName2, String contactSurname2, String contactNum2,
			String img2, String additionInfo2, String memberid2) {

		if (mBoundService == null)
			return;
		if ((img2 == null) || (img2 == "null"))
			img2 = "";

		pd.setMessage("Sending Data...");
		pd.show();

		if (memberid2 == null)
			mBoundService.AddNewColony(name2, surname2, contactName2,
					contactSurname2, contactNum2, img2, additionInfo2,
					isFromLogin);
		else
			mBoundService.EditColony(name2, surname2, contactName2,
					contactSurname2, contactNum2, img2, additionInfo2,
					memberid2);

	}

	public void deleteContact(String memberID) {

		if (mBoundService == null)
			return;
		if (isMemberOwner.equals("1")) {
			Toast.makeText(EditTagActivityBase.this, R.string.delete_own_contact_fail, Toast.LENGTH_LONG)
					.show();
			return;
		}

		pd.setMessage("Deleting Data...");
		pd.show();
		mBoundService.DeleteColony(memberID);
	}

	public void getMyBagInfo() {

		if (mBoundService == null)
			return;
		mBoundService.GetMyBagInfo();
	}
	public void getnoti() {

		if (mBoundService == null)
			return;
		mBoundService.GetNotifyListOpen();
		;
	}
	// /create optional menu here
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (isMemberOwner == null)
			isMemberOwner = "0";

		if (isMemberOwner.compareTo("0") == 0 && memberid != null) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.edit_connection_menu, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.remove_contact: {
			if (memberid != null)
				deleteContact(memberid);
		}
			break;

		}
		return true;
	}

	public String takePhoto() {
	
		String filePath = "";
		try {
			// use standard intent to capture an image
			Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());

			File pictureFolder = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			File imagesFolder = new File(pictureFolder, "MyImages");

			if (!imagesFolder.exists()) {

				boolean ismaked = imagesFolder.mkdirs();
				MyLog.i("imagesFolder.mkdirs()= " + ismaked);
			}

			filePath = Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_PICTURES).getPath()
					+ "/MyImages/MyTag_" + timeStamp + ".png";
			File image = new File(imagesFolder, "MyTag_" + timeStamp + ".png");
			uriSavedImage = Uri.fromFile(image);

			captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

			// we will handle the returned data in onActivityResult
			startActivityForResult(captureIntent, TAKE_PHOTO_FOR_RESULT);
		} catch (ActivityNotFoundException anfe) {
			// display an error message
			Toast.makeText(this, R.string.not_image_capture_support, Toast.LENGTH_SHORT).show();
		}
		return filePath;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == TAKE_PHOTO_FOR_RESULT) {
				
				try {
					getContentResolver().notifyChange(uriSavedImage, null);
					File imageFile = new File(path);
					ExifInterface exif = new ExifInterface(
							imageFile.getAbsolutePath());
					int orientation = exif.getAttributeInt(
							ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_NORMAL);

					switch (orientation) {
					case ExifInterface.ORIENTATION_ROTATE_270:
						rotate = 270;
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						rotate = 180;
						break;
					case ExifInterface.ORIENTATION_ROTATE_90:
						rotate = 90;
						break;
					}
					MyLog.i("Exif orientation: " + orientation);
				} catch (Exception e) {
					MyLog.i(e);
				}
			

				picUri = uriSavedImage;
				if (picUri != null)
					performCrop();
				
			}

			else if (requestCode == CROP_PHOTO_FOR_RESULT) {

				resolvePhotoResult();

			
			}
		}

	}

	@SuppressWarnings("deprecation")
	private void resolvePhotoResult() {
		Bitmap bm = BitmapFactory.decodeFile(path);
		if (bm == null) {
			Toast.makeText(EditTagActivityBase.this, R.string.save_picture_fail, Toast.LENGTH_LONG).show();
			return;
		}

		try {
			bm = Bitmap.createScaledBitmap(bm, 400, 400, true);
			int w = bm.getWidth();
			int h = bm.getHeight();
			Matrix mtx = new Matrix();
			mtx.postRotate(rotate);
			bm = Bitmap.createBitmap(bm, 0, 0, w, h, mtx, true);
		} catch (Exception e) {
			MyLog.i(e);
		}

		imgString = convertPhoto(bm);
		btn_photo.setBackgroundDrawable(new BitmapDrawable(bm));
		
	}

	private void performCrop() {
		try {
			// call the standard crop action intent (the user device may not
			// support it)
			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			// indicate image type and Uri
			cropIntent.setDataAndType(picUri, "image/*");
			
			 List<ResolveInfo> list = getPackageManager().queryIntentActivities( cropIntent, 0 );
			    int size = list.size();
			    if (size == 0) {            
			        Toast.makeText(this, R.string.image_crop_app_fail, Toast.LENGTH_SHORT).show();

			        return;
			    } else {
			
			// set crop properties
			cropIntent.putExtra("crop", "true");
			// indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			// indicate output X and Y
			cropIntent.putExtra("outputX", 400);
			cropIntent.putExtra("outputY", 400);
			// retrieve data on return
			// retrieve data on return
			cropIntent.putExtra("return-data", false);
			
			for(int i = 0;i<list.size();i++){
			 ResolveInfo res = list.get(i);
			 MyLog.i("------------------------package list: "+res.activityInfo.packageName+","+res.activityInfo.name);
			}

	        
			//check the native crop app
			//if it is installed, pass intent to it, or give it to system
			if(isPackageExisted("com.google.android.gallery3d" ))
				cropIntent.setComponent( new ComponentName("com.google.android.gallery3d"   ,"com.android.gallery3d.app.CropImage"));
			else if(isPackageExisted("com.sec.android.gallery3d" ))
				cropIntent.setComponent( new ComponentName("com.sec.android.gallery3d","com.sec.android.gallery3d.app.CropImage"));


			cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
			// start the activity - we handle returning in onActivityResult
			startActivityForResult(cropIntent, CROP_PHOTO_FOR_RESULT);
			    }
		} catch (ActivityNotFoundException anfe) {
			// display an error message
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast
					.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
			
			resolvePhotoResult() ;
		}

	}
	public boolean isPackageExisted(String targetPackage){
		   PackageManager pm=getPackageManager();
		   try {
		    @SuppressWarnings("unused")
			PackageInfo info=pm.getPackageInfo(targetPackage,PackageManager.GET_ACTIVITIES);
		       } catch (NameNotFoundException e) {
		    return false;
		    }  
		    return true;
		   }

	AntNotificationService mBoundService;

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mBoundService = ((AntNotificationService.LocalBinder) service)
					.getService();

			GlobalData.m_service = mBoundService; // store instance

			mBoundService.SetCallBackFunc(mWebCall);
			// mBoundService.GetMyBagInfo();
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
			GlobalData.m_service = null;

			MyLog.i("Binding", "called onServiceDisconnected");
		}

	};

	public String convertPhoto(Bitmap bm) {

		if (bm == null)
			return "";

		String encodedImage = "";
		try {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 70, baos); // bm is the
																// bitmap
																// object
			byte[] b = baos.toByteArray();
			encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
		} catch (Exception e) {
			MyLog.i(e);
		}
		MyLog.i("##########################encodedImage.length()=  "+encodedImage.length());
		return encodedImage;
	}

	@Override
	protected void onStop() {
		try {
			if (mBoundService != null)
				unbindService(mConnection);
		} catch (Exception e) {

		}
		super.onStop();
	}

	class WebResponse implements ServiceInterface {

		@Override
		public void AntOWebResponseMyBag(int piRetCode, ArrayList<MyTag> pMyBag) {

			btn.setClickable(true);
			if (0 == piRetCode) {
				if (GlobalData.connectionFragment != null)
					GlobalData.connectionFragment.onResume();
				EditTagActivityBase.this.finish();
			}

		}

		@Override
		public void AntOWebResponseLogin(int piRetCode, AntOUser pUser,
				ArrayList<MyTag> pMyBag) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseRegistration(int piRetCode, AntOUser pUser,
				ArrayList<MyTag> pMyBag) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseGetChatList(int piRetCode,
				ArrayList<ChatMsg> pChatMsgs) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseSendChatSuccess(int piRetCode,
				ArrayList<ChatMsg> pChatMsgs) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseGetNotifyList(int piRetCode,
				ArrayList<Notification> pMyNotis) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseLogOff(int piRetCode) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseGetTagInfo(int piRetCode, TagInfo taginfo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseEditTagInfoSuccess(int piRetCode) {
		}

		@Override
		public void AntOWebResponseAddToBagSuccess(int piRetCode) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseNotifyTagOwner(int piRetCode,
				String notificationId) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseAddNewColony(int piRetCode,
				String newMemberId) {
			
			
			btn.setClickable(true);
			pd.dismiss();
			if (piRetCode == AntRetStatus.OK) {
				//if we r editing the first colony (belong to user himself), update the global information
				if(isFromLogin.equals("1")){
					GlobalData.m_antOUser.name = name;
					GlobalData.m_antOUser.surname = surname;
				}
					
				getMyBagInfo();
				getnoti();
			} else {
				Toast.makeText(EditTagActivityBase.this, R.string.edit_fail, Toast.LENGTH_SHORT)
						.show();
			}

		}

		@Override
		public void AntOWebResponseEditColony(int piRetCode) {
			btn.setClickable(true);
			pd.dismiss();
			if (piRetCode == AntRetStatus.OK) {
				//if we r editing the first colony (belong to user himself), update the global information
				if(isFromLogin.equals("1")){
					GlobalData.m_antOUser.name = name;
					GlobalData.m_antOUser.surname = surname;
				}
				getMyBagInfo();
				getnoti();
			} else
				Toast.makeText(EditTagActivityBase.this, R.string.edit_fail, Toast.LENGTH_SHORT)
						.show();

		}

		@Override
		public void AntOWebResponseCheckVersion(int mLastServerError,
				String m_current_version) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseDeleteColony(int piRetCode) {
			btn.setClickable(true);
			pd.dismiss();
			if (piRetCode == AntRetStatus.OK) {
				getMyBagInfo();

			} else
				Toast.makeText(EditTagActivityBase.this, R.string.delete_fail, Toast.LENGTH_SHORT).show();

		}

		@Override
		public void AntOWebResponseDeleteConnectionSuccess(int piRetCode) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseChangeTagName(int piRetCode) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseRemoveAllNotifications(int piRetCode) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseRemoveNotification(int piRetCode) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseCloseChat(int piRetCode) {
			// TODO Auto-generated method stub

		}

		@Override
		public void AntOWebResponseForgetPw(int piRetCode) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void AntOWebResponseRemoveTag(int piRetCode) {
			// TODO Auto-generated method stub
			
		}

	}

	public void DefineScreenSize() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		miMetricType = metrics.densityDpi;
		miDispWidth = metrics.widthPixels;

		miDispDensity = (int) (metrics.density * 16 + (metrics.densityDpi / 16) + 0.5);

		switch (metrics.densityDpi) {
		case DisplayMetrics.DENSITY_XXHIGH:
			miDispHeight = metrics.heightPixels - 96;
			break;

		case DisplayMetrics.DENSITY_XHIGH:
			miDispHeight = metrics.heightPixels - 72;
			break;

		case DisplayMetrics.DENSITY_HIGH:
			miDispHeight = metrics.heightPixels - 48;
			break;

		case DisplayMetrics.DENSITY_MEDIUM:
			miDispHeight = metrics.heightPixels - 32;
			break;

		case DisplayMetrics.DENSITY_LOW:
			miDispHeight = metrics.heightPixels - 24;
			break;

		default:
			miDispDensity = metrics.densityDpi / 16;
			miDispHeight = metrics.heightPixels;
		}
	}

}
