package com.antcorp.anto;

import java.net.MalformedURLException;
import java.util.ArrayList;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.data.TagInfo;
import com.antcorp.anto.fragment_n_adapter.Colony;
import com.antcorp.anto.fragment_n_adapter.Connection;
import com.antcorp.anto.fragment_n_adapter.MyTag;
import com.antcorp.anto.interfaces.ServiceCallBackBase;
import com.antcorp.anto.network.AntRetStatus;
import com.antcorp.anto.service.AntNotificationService;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.widget.SelectColonyDialog;
import com.antcorp.anto.widget.ShowPortraitDialog;
import com.antcorp.anto.widget.TimeoutProgressDialog;
import com.antcorp.anto.widget.ViewGroupUtils;
import com.antcorp.anto.widget.ImageThreadLoader.ImageLoadedListener;
import com.antcorp.anto.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TagInfoActivity extends Activity {
	String tagid, ownerId, notificationId;
	int belongToMe = 0;
	AlertDialog ad;

	String addInfo, userImage, contactNum, contactName, contactSurName, name,
			surname, memberId, isMemberOwner;

	TextView tv_tag, tv_fullname, tv_contactname, tv_contactnum, et_addtion;
	ImageView image;

	TimeoutProgressDialog pd;

	String uid = "";;

	Button btn_action, btn_menu;;
	boolean isOwner = false;
	int bgId = R.drawable.ant_o_edit_bt;

	WebResponse mWebCall = new WebResponse(); // callback from the service
	Intent mIntentBkgService = null; // the intent to store the scence

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initail the service
		mIntentBkgService = new Intent(this, AntNotificationService.class);
		startService(mIntentBkgService);
		bindService(mIntentBkgService, mConnection, Context.BIND_AUTO_CREATE);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_taginfo);

		pd = new TimeoutProgressDialog(this);
		pd.setCanceledOnTouchOutside(false);
		pd.setCancelable(false);
		init(getIntent());
	}

	AntNotificationService mBoundService; // the instance of service

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

	private void init(Intent i) {

		btn_menu = (Button) findViewById(R.id.button_menu);
		btn_menu.setClickable(true);
		btn_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				TagInfoActivity.this.openOptionsMenu();

			}
		});

		btn_action = (Button) findViewById(R.id.button_tagAction);
		tv_tag = (TextView) findViewById(R.id.textView_tagdata);
		OnClickListener portrait_listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				openPortraiteDialog();

			}
		};
		tv_fullname = (TextView) findViewById(R.id.textView_fullname);
		tv_fullname.setOnClickListener(portrait_listener);
		OnClickListener ocl = new OnClickListener() {

			@Override
			public void onClick(View v) {
				openCallDialog();

			}
		};
		tv_contactname = (TextView) findViewById(R.id.textView_contactName);
		tv_contactname.setOnClickListener(ocl);
		tv_contactnum = (TextView) findViewById(R.id.textView_contactNum);
		tv_contactnum.setOnClickListener(ocl);
		et_addtion = (TextView) findViewById(R.id.editText_addtionInfo);
		image = (ImageView) findViewById(R.id.imageView_tagPortrait);
		image.setOnClickListener(portrait_listener);

		if (i.hasExtra("tagid")) {
			tagid = i.getExtras().getString("tagid");

			// //if intent has key"memberid", means it comes from
			// SelectColonyDialog
			if (i.hasExtra("memberid")) {
				memberId = i.getExtras().getString("memberid");
				for (Colony c : GlobalData.m_colonys) {
					if (memberId.equals(c.getColony_member_id())) {
						addInfo = c.getInfo();
						userImage = c.getImg();
						contactName = c.getContactName() + " "
								+ c.getContactSurName();
						contactNum = c.getContactPhone1();
						name = c.getName();
						surname = c.getSurname();
						belongToMe = 1;// bcz this tag is in our bag, so
										// it is belongs to us;

						bgId = R.drawable.ant_o_save_bt;

					}
				}

			} else {

				for (Connection co : GlobalData.m_connetions) {
					if (co.getTag_id().equals(tagid)) {
						for (Colony c : GlobalData.m_colonys) {
							if (co.getColony_member_id().equals(
									c.getColony_member_id())) {
								addInfo = c.getInfo();
								userImage = c.getImg();
								contactName = c.getContactName() + " "
										+ c.getContactSurName();
								contactNum = c.getContactPhone1();
								name = c.getName();
								surname = c.getSurname();
								belongToMe = 1;// bcz this tag is in our bag, so
												// it is belongs to us;
								memberId = c.getColony_member_id();

								bgId = R.drawable.ant_o_change_member;

							}
						}
					}
				}

			}

			for (MyTag tag : GlobalData.m_tags) {
				if (tag.getTagId().equals(tagid))
					tv_tag.setText(tag.getTagName());
			}

		}

		// if the m_current_tagInfo has value mean it comes from server info
		else if (GlobalData.m_current_tagInfo != null) {
			if (GlobalData.m_current_tagInfo.getColony() != null) {
				tagid = GlobalData.m_current_tagInfo.getDataTag().getTagid();
				Colony c = GlobalData.m_current_tagInfo.getColony();
				addInfo = c.getInfo();
				userImage = c.getImg();
				contactName = c.getContactName() + " " + c.getContactSurName();
				contactNum = c.getContactPhone1();
				name = c.getName();
				surname = c.getSurname();
				memberId = c.getColony_member_id();

				bgId = R.drawable.ant_o_notify_bt;
				ImageView topbanner = (ImageView) findViewById(R.id.imageView_antobanner);
				if (topbanner != null) {
					topbanner.setImageResource(R.drawable.banner_read_info_top);
					btn_menu.setClickable(false);
				}
			}

			tv_tag.setText(tagid);

			uid = GlobalData.m_current_tagInfo.getDataTag().getNfcUid();
		}
		btn_action.setBackgroundResource(bgId);

		View.OnClickListener ol = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bgId == R.drawable.ant_o_change_member) {
					editNewMember();
				} else if (bgId == R.drawable.ant_o_save_bt) {
					saveConnection();
				} else {
					if (uid.length() > 0)
						notifyOwner(tagid, "", uid);

				}
			}
		};

		btn_action.setOnClickListener(ol);

		tv_fullname.setText(name + " " + surname);
		tv_contactname.setText(contactName);
		tv_contactnum.setText(contactNum);
		et_addtion.setText(addInfo);
		setImageView(userImage);

		if (i.hasExtra("fromChat")) {
			prepareForChatActivity();
		}

	}

	protected void openPortraiteDialog() {
		ShowPortraitDialog sp = new ShowPortraitDialog();
		sp.ShowDialog(this, name + " " + surname, userImage);
		
	}

	protected void openCallDialog() {

		try {
//			final String tel = contactNum;
//			final String name = contactName;
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(contactNum);
			adb.setMessage("Call " + contactName + " ?");
			adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {//
						@Override
						public void onClick(DialogInterface dialog, int i) {

							callContact(contactNum);

						}

					});
			adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int i) {
					//

				}

			});
			ad = adb.show();//
			// do someting with item
		} catch (Exception e) {
			MyLog.i(e);
		}

	}

	protected void callContact(String tel) {
		try {
			String url = "tel:" + tel.trim();
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
			this.startActivity(intent);
		} catch (Exception e) {
			MyLog.i(e.getMessage());
		}

	}

	private void prepareForChatActivity() {

		if (btn_action != null)
			ViewGroupUtils.removeView(btn_action);
		if (btn_menu != null)
			ViewGroupUtils.removeView(btn_menu);
		ImageView imageTopbanner = (ImageView) findViewById(R.id.imageView_antobanner);
		if (imageTopbanner != null)
			imageTopbanner.setImageResource(R.drawable.banner_read_info_top);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		init(intent);
	}

	protected void saveConnection() {
		MyLog.i("protected void saveConnection() {");
		if (memberId == null) {
			// Toast.makeText(this,
			// "Please select a colony or use menu to create a new one!",
			// Toast.LENGTH_SHORT).show();
			return;
		}
		if (mBoundService == null)
			return;

		pd.setMessage("Saving...");
		pd.show();
		mBoundService.EditConnection(tagid, memberId);

	}

	private void setImageView(String userImage2) {
		try {

			Bitmap cachedImage = GlobalData.m_imageLoader.loadImage(userImage2,
					new ImageLoadedListener() {
						public void imageLoaded(Bitmap imageBitmap) {

							try {

								imageBitmap = Bitmap.createScaledBitmap(
										imageBitmap, 100, 100, true);

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
			MyLog.i("Bad remote image URL: " + userImage2 + e.getMessage());
		}
	}

	protected void setData(Colony c) {
		name = c.getName();
		surname = c.getSurname();
		contactName = c.getContactName();
		contactNum = c.getContactPhone1();
		addInfo = c.getInfo();
		userImage = c.getImg();
		contactSurName = c.getContactSurName();

		memberId = c.getColony_member_id();
		isMemberOwner = c.getIs_member_owner();

		tv_tag.setText(tagid);
		tv_fullname.setText(name + " " + surname);

		
		tv_contactname.setText(contactName);
		tv_contactnum.setText(contactNum);
		et_addtion.setText(addInfo);
		setImageView(userImage);

	}

	public void notifyOwner(String tagid, String msg, String uid) {
		if (mBoundService == null)
			return;
		pd.setMessage("Notifying...");
		pd.show();
		mBoundService.NotifyTagOwner(tagid, msg, uid);
	}

	public void getMyBagInfo() {

		if (mBoundService == null)
			return;
		mBoundService.GetMyBagInfo();
	}

	@Override
	protected void onResume() {
		if (isOwner && name == null) {
		}

		super.onResume();
	}

	@Override
	protected void onStop() {
		try {
			if (mBoundService != null)
				unbindService(mConnection);
		} catch (Exception e) {
			MyLog.i(e);
		}
		super.onStop();
	}

	// /create optional menu here
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.edit_colony:
			editNewMember();
			break;

		case R.id.remove_colony:
			removeNewMember();
			break;

		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if (!getIntent().hasExtra("fromChat")) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.taginfo_menu, menu);
			if (bgId == R.drawable.ant_o_change_member) {
				menu.removeItem(menu.getItem(0).getItemId());
			} else if (bgId == R.drawable.ant_o_save_bt) {

				menu.removeItem(menu.getItem(1).getItemId());
			} else {
				menu.clear();

			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	public void getnoti() {

		if (mBoundService == null)
			return;
		mBoundService.GetNotifyListOpen();
		;
	}

	private void removeNewMember() {
		if (mBoundService == null)
			return;
		if (tagid == null)
			return;

		pd.setMessage("Removing...");
		pd.show();
		mBoundService.DeleteConnection(tagid);

	}

	private void editNewMember() {
		btn_menu.setClickable(false);
		for (MyTag tag : GlobalData.m_tags) {
			if (tag.getTagId().equals(tagid)) {
				// TagInfoActivity.this.finish();
				SelectColonyDialog sDialog = new SelectColonyDialog();
				sDialog.ShowDialog(TagInfoActivity.this, tag);

			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 200) {
			if (resultCode == RESULT_OK) {

			}
		}

	}

	class WebResponse extends ServiceCallBackBase {

		public WebResponse() {
			super(TagInfoActivity.this);
		
		}

		@Override
		public void AntOWebResponseMyBag(int piRetCode, ArrayList<MyTag> pMyBag) {
			if (piRetCode == AntRetStatus.OK) {

				if (!getIntent().hasExtra("fromChat"))
					TagInfoActivity.this.finish();
			}

		}

	

		@Override
		public void AntOWebResponseGetTagInfo(int piRetCode, TagInfo taginfo) {
			if (piRetCode == 0) {
				if (taginfo == null)
					return;

				if (taginfo.getAntOwner() != null) {
					init(getIntent());
				}
			}
		}

		@Override
		public void AntOWebResponseEditTagInfoSuccess(int piRetCode) {
			pd.dismiss();
			if (piRetCode == AntRetStatus.OK) {
				getMyBagInfo();
			}

		}

	
		@Override
		public void AntOWebResponseNotifyTagOwner(int piRetCode,
				String notificationId) {
			pd.dismiss();
			if (piRetCode == 0) {
				// String jsonString = msg.getData().getString("json");
				// notificationId = jsonString;
				if (notificationId.length() > 10) {

					getnoti();
					Intent intent = new Intent(TagInfoActivity.this,
							ChatActivity.class);

					intent.putExtra("tagid", tagid);
					intent.putExtra("notiId", notificationId);
					intent.putExtra("sender_name", GlobalData.m_antOUser.name);
					intent.putExtra("sender_surname",
							GlobalData.m_antOUser.surname);
					intent.putExtra("owner_name", name);
					intent.putExtra("owner_surname", surname);
					intent.putExtra("owner_count", 0);
					intent.putExtra("sender_count", 0);
					intent.putExtra("max_count", 10);
					TagInfoActivity.this.startActivity(intent);
					TagInfoActivity.this.finish();

				}

			} else {
				Toast.makeText(TagInfoActivity.this, R.string.send_notify_fail,
						Toast.LENGTH_SHORT).show();
			}

		}

	

		@Override
		public void AntOWebResponseDeleteConnectionSuccess(int piRetCode) {
			pd.dismiss();
			if (piRetCode == AntRetStatus.OK) {
				getMyBagInfo();
			}

		}

		

	}

}
