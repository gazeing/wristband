package com.antcorp.anto;

import java.net.MalformedURLException;
import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.data.TagInfo;
import com.antcorp.anto.fragment_n_adapter.ChatMsg;
import com.antcorp.anto.fragment_n_adapter.ChatMsgAdapter;
import com.antcorp.anto.fragment_n_adapter.Connection;
import com.antcorp.anto.fragment_n_adapter.MyTag;
import com.antcorp.anto.fragment_n_adapter.Notification;
import com.antcorp.anto.interfaces.ServiceCallBackBase;
import com.antcorp.anto.network.AntRetStatus;
import com.antcorp.anto.service.AntNotificationService;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.widget.TimeoutProgressDialog;
import com.antcorp.anto.widget.ViewGroupUtils;
import com.antcorp.anto.widget.ImageThreadLoader.ImageLoadedListener;
import com.antcorp.anto.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity {

	StickyListHeadersListView lView;
	ChatMsgAdapter adapter;

	TextView tv_max;
	String tagid, notiId, sender_name, owner_name, sender_surname,
			owner_surname, img,uid;
	int owner_count, sender_count, max_count;
	ArrayList<ChatMsg> objects = new ArrayList<ChatMsg>();

	boolean isOtherNoti = true;

	String lastedJSONMsgs;

	EditText et_chat;
	ImageButton btn_chat;

	final int UPDATE_LIST_DELAY = 7000; // the period to update chat list from
	// server
	Handler handler = new Handler();
	Runnable run;

	TimeoutProgressDialog pd;

	WebResponse mWebCall = new WebResponse(); // callback from the service
	Intent mIntentBkgService = null; // the intent to store the scence

	public void startTimer() {

		run = new Runnable() {

			@Override
			public void run() {
				getChatList();

			}
		};

		handler.postDelayed(run, UPDATE_LIST_DELAY);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// initail the service
		mIntentBkgService = new Intent(this, AntNotificationService.class);
		startService(mIntentBkgService);
		bindService(mIntentBkgService, mConnection, Context.BIND_AUTO_CREATE);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat);

		init();
	}

	public void getChatList() {

		// ProcessGetChatList pg = new ProcessGetChatList(this, chat_handler);
		// pg.setParams(tagid, notiId);
		// pg.postData();
		if ((tagid == null) || (notiId == null))
			return;

		if (mBoundService == null)
			return;
		mBoundService.GetChatList(tagid, notiId);

	}

	AntNotificationService mBoundService;// the instance of service

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

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		menu.clear();
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.chat_menu, menu);
		if (isOtherNoti) {
			menu.removeItem(menu.getItem(0).getItemId());
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
//		case R.id.close_chat:
//			closeChat();
//			break;

		case R.id.remove_chat:
			removeNotification();
			break;
			
		case R.id.contact_details:
			showTagInfo();
			break;

		}
		return true;
	}

	private void showTagInfo() {
		for (MyTag tag : GlobalData.m_tags) {
			if (tag.getTagId().equals(tagid)) {
				startLocalLogic(tag);
				return;
			}
		}
		
		//if cannot find the tag id in my bag, call server to get back tag info
	getTagInfo(tagid);
		
	}
	
	public void getTagInfo(String id) {
		if (mBoundService == null)
			return;

		pd.setMessage("Reading tag...");
		pd.show();
		mBoundService.GetTagInfo(id,uid);

	}

	private void startLocalLogic(MyTag tag) {
			for (Connection connection : GlobalData.m_connetions) {
			if (connection.getTag_id().equals(tag.getTagId())) {
				//add one more judgement in case of  the connection has not been removed, by steven
				if(connection.getCurrent_active().equals("1")){
				Intent intent = new Intent(this, TagInfoActivity.class);
				intent.putExtra("tagid", tag.getTagId());
				intent.putExtra("fromChat", true);
				this.startActivity(intent);
				return;
				}
			}
		}
		
	}

	@Override
	protected void onStop() {

		getMyBagInfo();
		GlobalData.m_current_chatMsgs.clear();

		try {
			if (mBoundService != null)
				unbindService(mConnection);
		} catch (Exception e) {
			MyLog.i(e);
		}

		handler.removeCallbacks(run);
		super.onStop();
	}

	private void init() {
		pd = new TimeoutProgressDialog(this);
		pd.setCanceledOnTouchOutside(false);
		pd.setCancelable(false);

		sender_name = getIntent().getExtras().getString("sender_name");
		owner_name = getIntent().getExtras().getString("owner_name");
		sender_surname = getIntent().getExtras().getString("sender_surname");
		owner_surname = getIntent().getExtras().getString("owner_surname");
		tagid = getIntent().getExtras().getString("tagid");
		notiId = getIntent().getExtras().getString("notiId");
		uid = getIntent().getExtras().getString("uid");
		owner_count = getIntent().getExtras().getInt("owner_count");
		sender_count = getIntent().getExtras().getInt("sender_count");
		max_count = getIntent().getExtras().getInt("max_count");
		// if sender is myself, it is a notification i sent to others
		isOtherNoti = (sender_name.equals(GlobalData.m_antOUser.name));

		et_chat = (EditText) findViewById(R.id.editText_chat);
		// try to hide soft keyboard
		hideSoftKeyboard(et_chat);


		lView = (StickyListHeadersListView) findViewById(R.id.listView1);

		// GlobalData.m_current_chatMsgs.clear();
		for (Notification n : GlobalData.m_notifications) {
			if (n.getNotificationId().equals(notiId)) {
				objects.addAll(n.getChat_list());
//				adapter.notifyDataSetChanged();
				// update globaldata for fullmap activity
				GlobalData.m_current_chatMsgs.clear();
				GlobalData.m_current_chatMsgs.addAll(n.getChat_list());

				img = n.getImg();
			}

		}
		adapter = new ChatMsgAdapter(this, objects);
		lView.setAdapter(adapter);

		lView.setSelection(lView.getCount() - 1);


		// getChatList();
		
		Button btn_menu = (Button) findViewById(R.id.button_menu);
		btn_menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ChatActivity.this.openOptionsMenu();
				
			}
		});

		TextView tv_name = (TextView) findViewById(R.id.textView_name);
		tv_name.setText(sender_name + " " + sender_surname);

		TextView tv_about = (TextView) findViewById(R.id.textView_about);
		tv_about.setText(owner_name + " " + owner_surname);

		tv_max = (TextView) findViewById(R.id.textView_maxcount);
		setCountNumber();

		btn_chat = (ImageButton) findViewById(R.id.button_send);
		btn_chat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String msg = et_chat.getText().toString();
				if(checkMsgEmpty(msg))
					return;
				if (isOtherNoti) {
					if (sender_count + 4 > max_count) {
						if (tv_max != null)
							tv_max.setTextColor(getResources().getColor(R.color.label_font));
					}
					if (sender_count + 1 > max_count) {

						exceedMax();

						return;
					}
				} else {
					if (owner_count + 3 > max_count) {
						if (tv_max != null)
							tv_max.setTextColor(getResources().getColor(R.color.label_font));
					}
					if (owner_count + 1 > max_count) {
						exceedMax();
						return;
					}

				}

				mBoundService.SendChatMsg(tagid, notiId, msg);
				
				hideSoftKeyboard(et_chat);
				// pd.setTitle("Sending message...");
				pd.setMessage("Sending message...");
				pd.show();
			}
		});

		final ImageView img_icon = (ImageView) findViewById(R.id.imageView_icon);
		if (img != null) {
			if (img.length() > 0) {
				try {

					Bitmap cachedImage = GlobalData.m_imageLoader.loadImage(
							img, new ImageLoadedListener() {
								public void imageLoaded(Bitmap imageBitmap) {

									try {

										imageBitmap = Bitmap
												.createScaledBitmap(
														imageBitmap, 100, 100,
														true);

									} catch (Exception e) {
										MyLog.i(e);
									}
									img_icon.setImageBitmap(imageBitmap);

								}
							});

					if (cachedImage != null) {
						img_icon.setImageBitmap(cachedImage);

					}
				} catch (MalformedURLException e) {
					MyLog.i("Bad remote image URL: " + img + e.getMessage());
				}
			}
		}

		ImageView img_map = (ImageView) findViewById(R.id.imageView_nav);
		img_map.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(ChatActivity.this, FullMapActivity.class);
//				i.putExtras(getIntent().getExtras());
//				i.putExtra("jsonArray", lastedJSONMsgs);
				i.putExtra("from", "chat");
			
				ChatActivity.this.startActivity(i);
				// ChatActivity.this.openOptionsMenu();

			}
		});

		// startTimer();
		// getChatList();

		if (isOtherNoti) {
			if (sender_count >=max_count) {
				exceedMax();

			}
			//if i'm the owner of chat, Shows all sender's message only as P.O.I.
			GlobalData.m_map_chatMsgs.clear();
			for(ChatMsg chat:GlobalData.m_current_chatMsgs){
				boolean isFromMe = chat.getSenderId().equals(GlobalData.m_antOUser.id);
				if(isFromMe){
					//add sender msg to map
					GlobalData.m_map_chatMsgs.add(chat);
				}
			}

			
		} else {
			if (owner_count >= max_count) {
				exceedMax();
			}
			
			//if i'm the owner of chat, Shows all messages as P.O.I.
			GlobalData.m_map_chatMsgs = GlobalData.m_current_chatMsgs;
		}

	}

	private void hideSoftKeyboard(EditText et_chat2) {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et_chat2.getWindowToken(), 0);
		
	}

	protected boolean checkMsgEmpty(String msg) {
		if(msg!=null)
			if(msg.length()>0)
				return false;
		
		Toast.makeText(ChatActivity.this, R.string.chat_empty, Toast.LENGTH_SHORT)
		.show();
		return true;
		
	}

	protected void exceedMax() {
		if (tv_max != null)
			tv_max.setTextColor(getResources().getColor(R.color.label_font));
		Toast.makeText(ChatActivity.this, R.string.chat_limit, Toast.LENGTH_SHORT)
				.show();
		if (et_chat != null) {
			et_chat.setEnabled(false);
			et_chat.setText("");
			et_chat.setHint("Closed");
		}
		if (btn_chat != null)
			btn_chat.setClickable(false);
		
		LayoutInflater vi = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View labelView = vi.inflate(R.layout.label_chat_closed, null);
		View editView = findViewById(R.id.Relative_edit);
		
		ViewGroupUtils.replaceView(editView, labelView);
		

	}

	private void setCountNumber() {
		if (isOtherNoti)
			tv_max.setText(sender_count + "/" + max_count);
		else {
			tv_max.setText(owner_count + "/" + max_count);
		}

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

	public void removeNotification() {
		if (mBoundService == null)
			return;
		pd.setMessage("Removing...");
		pd.show();
		mBoundService.RemoveNotification(notiId);
	}

	public void closeChat() {
		if (mBoundService == null)
			return;
		pd.setMessage("Closing...");
		pd.show();
		mBoundService.CloseChat(notiId);
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	class WebResponse extends ServiceCallBackBase {

		public WebResponse() {
			super(ChatActivity.this);

		}



		@Override
		public void AntOWebResponseGetChatList(int piRetCode,
				ArrayList<ChatMsg> pChatMsgs) {
			pd.dismiss();
			if (piRetCode == AntRetStatus.OK) {
				objects.clear();
				objects.addAll(pChatMsgs);
//				adapter.clear();
//				adapter.addAll(pChatMsgs);

				adapter.notifyDataSetChanged();
				lView.invalidate();
				lView.setSelection(lView.getCount() - 1);
				getnoti();
			} else {

			}
			// handler.postDelayed(run, UPDATE_LIST_DELAY);

		}

		@Override
		public void AntOWebResponseSendChatSuccess(int piRetCode,
				ArrayList<ChatMsg> pChatMsgs) {

			if (piRetCode == AntRetStatus.OK) {
				et_chat.setText("");
				getChatList();
				if (isOtherNoti)
					sender_count++;
				else
					owner_count++;
				setCountNumber();
			} else {
				pd.dismiss();
				Toast.makeText(ChatActivity.this, R.string.send_msg_fail, Toast.LENGTH_SHORT)
						.show();
			}
		}


		@Override
		public void AntOWebResponseGetTagInfo(int piRetCode, TagInfo taginfo) {
			pd.dismiss();
			if (piRetCode == 0) {
				if (taginfo == null)
					return;
				Context context = ChatActivity.this;
				if (taginfo.getAntOwner() != null) {
					Intent intent = new Intent(context, TagInfoActivity.class);
					// intent.putExtra("json", jsonString);
					intent.putExtra("fromChat", true);
					context.startActivity(intent);
				} 
			}

		}

	

		@Override
		public void AntOWebResponseRemoveNotification(int piRetCode) {
			pd.dismiss();
			if (piRetCode == 0) {
				getnoti();
				ChatActivity.this.finish();
			} else
				Toast.makeText(ChatActivity.this, R.string.remove_chat_fail, Toast.LENGTH_SHORT)
						.show();

		}

		@Override
		public void AntOWebResponseCloseChat(int piRetCode) {
			pd.dismiss();
			if (piRetCode == 0) {
				getnoti();
				ChatActivity.this.finish();
			} else
				Toast.makeText(ChatActivity.this, R.string.close_chat_fail, Toast.LENGTH_SHORT)
						.show();
		}



	}

}
