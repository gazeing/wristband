package com.antcorp.anto.service;

import java.util.ArrayList;

import com.antcorp.anto.SplashActivity;
import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.fragment_n_adapter.Notification;
import com.antcorp.anto.interfaces.ServiceInterface;
import com.antcorp.anto.network.AntRetStatus;
import com.antcorp.anto.network.ServerConn;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.R;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;

public class AntNotificationService extends Service {
	final int UPDATE_LIST_DELAY = 30000; // the period to update notification
	final IBinder mBinder = new LocalBinder();
	final int NOTYFI_ID = 8888;

	Context context;

	static NotificationManager mNotiManager = null;
	static android.app.Notification mNoti = null;
	static NotificationCompat.Builder mBuilder=null;
	static Intent mIntentCallBackMainActity = null;
	static PendingIntent mPI = null;
	static ServiceInterface mCallBack = null;

	ServerConn mServerConn = new ServerConn();
	
	static boolean mIsUserLoggedin = false;
	
	static String mWelcomeWord = "Log in";

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public AntNotificationService getService() {
			try {
				return AntNotificationService.this;
			} catch (Exception e) {
				MyLog.i(" Bind Error: " + e.getMessage());
				return null;
			}
		}
	}

	public void ClearCallBackFunc() {
		try {
			mCallBack = null;
		} catch (Exception e) {
		}
	}

	public boolean SetCallBackFunc(ServiceInterface pCB) {
		try {
			mCallBack = pCB;
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public void startTimer() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		MyLog.i(" Service Binded");

		try {
			return mBinder;
		} catch (Exception e) {
			MyLog.i(" Service Binded - FAIL");
			return null;
		}
	}

	@Override
	public void onCreate() {
		this.context = this;

		if (mNotiManager == null)
			mNotiManager = (NotificationManager) getSystemService(AntNotificationService.NOTIFICATION_SERVICE);

		MyLog.i(" The new Service was Created");

	}

	@Override
	public int onStartCommand(Intent pIntent, int piFlags, int piStartId) {
		try {
			if (mNoti == null) {

				mIntentCallBackMainActity = new Intent(getApplicationContext(),
						SplashActivity.class);
				mPI = PendingIntent.getActivity(getApplicationContext(),
						NOTYFI_ID, mIntentCallBackMainActity,
						PendingIntent.FLAG_UPDATE_CURRENT);
				mBuilder =new NotificationCompat.Builder(context)
				.setContentTitle("Ant-O").setContentText(mWelcomeWord)
				.setSmallIcon(R.drawable.ic_launcher).setOngoing(true)
				.setOnlyAlertOnce(true)
				.setContentIntent(mPI);

				mNoti = mBuilder.build();
			} else {
				stopForeground(true);
			}

			startForeground(NOTYFI_ID, mNoti);
			mNotiManager.notify(NOTYFI_ID, mNoti);

			MyLog.i(" Service Started");

			return Service.START_STICKY;
		} catch (Exception e) {
		}

		return 0;
	}
	
	public void updateNotificationText(String newText){
		if(mBuilder!=null){
			mBuilder.setContentText(newText);
			if(mNotiManager!=null){
				mNoti = mBuilder.build();
				startForeground(NOTYFI_ID, mNoti);
				mNotiManager.notify(NOTYFI_ID,mNoti);
			}
		}
	}


	@Override
	public void onDestroy() 
	{
		MyLog.i(" Service Destroyed");
		super.onDestroy();
	}

	public void StopAllServices()
	{
		mServerConn.ShutDown();
		
		GlobalData.CleanUp();
		
		stopForeground(true);
		startForeground(NOTYFI_ID, mNoti);
		
		mNotiManager.cancel(NOTYFI_ID);
		
    	mNoti = null;
    	mPI = null;
    	mIntentCallBackMainActity = null;
    	mNotiManager = null;
    	
    	mWelcomeWord = "Log in";
	}

	// // BAG
	public void GetMyBagInfo() {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new GetMyBagInfoFromServer(context).execute();
				}
			});
		} catch (Exception e) {
		}
	}

	private class GetMyBagInfoFromServer extends AntOServerBaseClass {

		public GetMyBagInfoFromServer(Context context) {
			super(context);
			
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
			mCallBack.AntOWebResponseMyBag(GlobalData.mLastServerError,
					GlobalData.m_tags);
			
		}

		@Override
		protected boolean callApiFunction(String... pData) {
			
			return mServerConn.GetAntOMyBag();
		}

	}
	
	////check current version
	
	public void CheckVersionAntO() {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new CheckVersionAntOServer(context).execute();
				}
			});
		} catch (Exception e) {
		}
	}

	private class CheckVersionAntOServer extends AntOServerBaseClass {

		public CheckVersionAntOServer(Context context) {
			super(context);
			}


		@Override
		protected boolean callApiFunction(String... pData) {
				return mServerConn.CheckVersionAntO();
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
			mCallBack.AntOWebResponseCheckVersion(GlobalData.mLastServerError,
					GlobalData.m_current_version);
			
		}
	}

	// //// Login
	public void LoginAntO(final String psEmail, final String psPwd,final String resid) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new LoginAntOServer(context).execute(psEmail, psPwd,resid);
				}
			});
		} catch (Exception e) {
		}
	}

	private class LoginAntOServer extends AntOServerBaseClass {
		public LoginAntOServer(Context context) {
			super(context);
			
		}





		@Override
		protected void passToCallback() {
			mIsUserLoggedin = (GlobalData.mLastServerError == AntRetStatus.OK);
			if(mIsUserLoggedin){
				GetNotifyListOpen();
				mWelcomeWord = GlobalData.m_antOUser.name+" "+GlobalData.m_antOUser.surname;
				updateNotificationText(mWelcomeWord);
			}
			if (mCallBack != null)
				mCallBack.AntOWebResponseLogin(GlobalData.mLastServerError,
						GlobalData.m_antOUser, GlobalData.m_tags);
			
		}

		@Override
		protected boolean callApiFunction(String... pData) {

			return mServerConn.LoginAntO(pData[0], pData[1], pData[2]);
		}
	}

	// //// Registration
	public void RegistrationAntO(final String psName, final String psSurname,
			final String psEmail, final String psPwd,final String resid) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new RegistrationAntOServer(context).execute(psName, psSurname,
							psEmail, psPwd,resid);
				}
			});
		} catch (Exception e) {
		}
	}

	private class RegistrationAntOServer extends AntOServerBaseClass {
		public RegistrationAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.RegistrationAntO(pData[0], pData[1], pData[2],
//						pData[3], pData[4])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("RegistrationAntO FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				mIsUserLoggedin = (GlobalData.mLastServerError == AntRetStatus.OK);
//				if(mIsUserLoggedin){
//				}
//				if (mCallBack != null)
//					mCallBack.AntOWebResponseRegistration(GlobalData.mLastServerError,
//							GlobalData.m_antOUser, GlobalData.m_tags);
//			} catch (Exception e) {
//				MyLog.i("RegistrationAntO On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.RegistrationAntO(pData[0], pData[1], pData[2],
					pData[3], pData[4]);
		}

		@Override
		protected void passToCallback() {
			mIsUserLoggedin = (GlobalData.mLastServerError == AntRetStatus.OK);
			if(mIsUserLoggedin){
				mWelcomeWord = GlobalData.m_antOUser.name+" "+GlobalData.m_antOUser.surname;
				updateNotificationText(mWelcomeWord);
			}
			if (mCallBack != null)
				mCallBack.AntOWebResponseRegistration(GlobalData.mLastServerError,
						GlobalData.m_antOUser, GlobalData.m_tags);
			
		}
	}
	
	//forget password
	
	public void ForgetPwAntO(final String psEmail) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new ForgetPwAntOServer(context).execute(psEmail);
				}
			});
		} catch (Exception e) {
		}
	}

	private class ForgetPwAntOServer extends AntOServerBaseClass {
		public ForgetPwAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.ForgetPwAntO(pData[0])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("ForgetPwAntO FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				mIsUserLoggedin = (GlobalData.mLastServerError == AntRetStatus.OK);
//				if(mIsUserLoggedin){
//					GetNotifyListOpen();
//				}
//				if (mCallBack != null)
//					mCallBack.AntOWebResponseForgetPw(GlobalData.mLastServerError);
//			} catch (Exception e) {
//				MyLog.i("ForgetPwAntO On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.ForgetPwAntO(pData[0]);
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
				mCallBack.AntOWebResponseForgetPw(GlobalData.mLastServerError);
			
		}
	}

	// add tag register as mine
	public void RegisterAsMine(final String tagid, final String tagname,
			final int agree_tos, final String uid) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new RegisterAsMineAntOServer(context).execute(tagid, tagname,
							agree_tos + "",uid);
				}
			});
		} catch (Exception e) {
		}
	}

	private class RegisterAsMineAntOServer extends
	AntOServerBaseClass {
		public RegisterAsMineAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.RegisterAsMineAntO(pData[0], pData[1],
//						pData[2],pData[3])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("RegisterAsMine FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				if (mCallBack != null)
//					mCallBack
//							.AntOWebResponseAddToBagSuccess(GlobalData.mLastServerError);
//			} catch (Exception e) {
//				MyLog.i("RegisterAsMineAntOServer On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.RegisterAsMineAntO(pData[0], pData[1],
					pData[2],pData[3]);
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
				mCallBack
						.AntOWebResponseAddToBagSuccess(GlobalData.mLastServerError);
			
		}
	}
	
	//change tag name
	public void ChangeTagName(final String tagid, final String tagname
			) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new ChangeTagNameAntOServer(context).execute(tagid, tagname
							);
				}
			});
		} catch (Exception e) {
		}
	}

	private class ChangeTagNameAntOServer extends
	AntOServerBaseClass {
		public ChangeTagNameAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.ChangeTagNameAntO(pData[0], pData[1])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("ChangeTagNameAntOServer FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				if (mCallBack != null)
//					mCallBack
//							.AntOWebResponseChangeTagName(GlobalData.mLastServerError);
//			} catch (Exception e) {
//				MyLog.i("ChangeTagNameAntOServer On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.ChangeTagNameAntO(pData[0], pData[1]);
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
				mCallBack
						.AntOWebResponseChangeTagName(GlobalData.mLastServerError);
			
		}
	}

	// get chat list

	public void GetChatList(final String tagid, final String notificationId) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new GetChatListAntOServer(context).execute(tagid, notificationId);
				}
			});
		} catch (Exception e) {
		}
	}

	private class GetChatListAntOServer extends AntOServerBaseClass {


		public GetChatListAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.GetChatListAntO(pData[0], pData[1])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("GetChatListAntOServer FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				if (mCallBack != null)
//					mCallBack.AntOWebResponseGetChatList(
//							GlobalData.mLastServerError,
//							GlobalData.m_current_chatMsgs);
//			} catch (Exception e) {
//				MyLog.i("GetChatListAntOServer On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.GetChatListAntO(pData[0], pData[1]);
		}

		@Override
		protected void passToCallback() {
						if (mCallBack != null)
				mCallBack.AntOWebResponseGetChatList(
						GlobalData.mLastServerError,
						GlobalData.m_current_chatMsgs);
			
		}
	}

	// ///send chat msg

	public void SendChatMsg(final String tagid, final String notificationId,
			final String msg) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new SendChatMsgAntOServer(context).execute(tagid, notificationId,
							msg);
				}
			});
		} catch (Exception e) {
		}
	}

	private class SendChatMsgAntOServer extends AntOServerBaseClass {
		public SendChatMsgAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.SendChatMessageAntO(pData[0], pData[1],
//						pData[2])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("SendChatMsgAntOServer FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				if (mCallBack != null)
//					mCallBack.AntOWebResponseSendChatSuccess(
//							GlobalData.mLastServerError,
//							GlobalData.m_current_chatMsgs);
//			} catch (Exception e) {
//				MyLog.i("SendChatMsgAntOServer On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.SendChatMessageAntO(pData[0], pData[1],
					pData[2]);
		}

		@Override
		protected void passToCallback() {
						if (mCallBack != null)
				mCallBack.AntOWebResponseSendChatSuccess(
						GlobalData.mLastServerError,
						GlobalData.m_current_chatMsgs);
			
		}
	}
	
	
	// remove notification
	public void RemoveNotification(final String chatId) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new RemoveNotificationAntOServer(context).execute(chatId);
				}
			});
		} catch (Exception e) {
		}
	}

	private class RemoveNotificationAntOServer extends
	AntOServerBaseClass {
		public RemoveNotificationAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.RemoveNotificationAntO(pData[0])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("RemoveNotificatioAntOServer FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				if (mCallBack != null)
//					mCallBack
//							.AntOWebResponseRemoveNotification(GlobalData.mLastServerError);
//			} catch (Exception e) {
//				MyLog.i("RemoveNotificatioAntOServer On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.RemoveNotificationAntO(pData[0]);
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
				mCallBack
						.AntOWebResponseRemoveNotification(GlobalData.mLastServerError);
			
		}
	}
	
	//close a chat
	public void CloseChat(final String chatId) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new CloseChatAntOServer(context).execute(chatId);
				}
			});
		} catch (Exception e) {
		}
	}

	private class CloseChatAntOServer extends
	AntOServerBaseClass {
		public CloseChatAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.CloseChatAntO(pData[0])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("CloseChatAntOServer FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				if (mCallBack != null)
//					mCallBack
//							.AntOWebResponseCloseChat(GlobalData.mLastServerError);
//			} catch (Exception e) {
//				MyLog.i("CloseChatAntOServer On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.CloseChatAntO(pData[0]);
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
				mCallBack
						.AntOWebResponseCloseChat(GlobalData.mLastServerError);
			
		}
	}

	// /get notify list

	public void GetNotifyListOpen() {
		try {
			if (mIsUserLoggedin)
				new GetNotifyListOpenAntOServer(context).execute();
			
		} catch (Exception e) {
			MyLog.i(e);
		}
	}

	private class GetNotifyListOpenAntOServer extends AntOServerBaseClass {
		
		public GetNotifyListOpenAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) 
//		{
//			try {
//				MyLog.i("if (!mServerConn.GetNotifyListAntO()) {");
//				
//				if (!mServerConn.GetNotifyListAntO()) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("GetNotifyListOpenAntOServer FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) 
//		{
//			try {
//				if (mCallBack != null) {
//					mCallBack.AntOWebResponseGetNotifyList(GlobalData.mLastServerError,GlobalData.m_notifications);
//					AntOWebResponseGetNotifyList(GlobalData.mLastServerError,GlobalData.m_notifications);
//				}
//			} catch (Exception e) {
//				MyLog.i("GetNotifyListOpenAntOServer On PostExecute Error: " + e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.GetNotifyListAntO();
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null) {
				mCallBack.AntOWebResponseGetNotifyList(GlobalData.mLastServerError,GlobalData.m_notifications);
				AntOWebResponseGetNotifyList(GlobalData.mLastServerError,GlobalData.m_notifications);
			}
		}
	}

	void AntOWebResponseGetNotifyList(int piRetCode,
			ArrayList<Notification> pMyNotis) {
		// try to update layout, if it exists
		if (GlobalData.notifictionFragment != null) {
			GlobalData.notifictionFragment.onResume();
		}



	}

	// get tag info
	public void GetTagInfo(final String id,final String Uid) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new GetTagInfoAntOServer(context).execute(id,Uid);
				}
			});
		} catch (Exception e) {
		}
	}

	private class GetTagInfoAntOServer extends AntOServerBaseClass {
		public GetTagInfoAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.GetTagInfoAntO(pData[0],pData[1])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("GetTagInfoAntOServer FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				if (mCallBack != null)
//					mCallBack.AntOWebResponseGetTagInfo(
//							GlobalData.mLastServerError,
//							GlobalData.m_current_tagInfo);
//			} catch (Exception e) {
//				MyLog.i("GetTagInfoAntOServer On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.GetTagInfoAntO(pData[0],pData[1]);
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
				mCallBack.AntOWebResponseGetTagInfo(
						GlobalData.mLastServerError,
						GlobalData.m_current_tagInfo);
			
		}
	}

	// Edit connection

	public void EditConnection(final String tagid, final String memberId) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new EditConnectionAntOServer(context).execute(tagid, memberId);
				}
			});
		} catch (Exception e) {
		}
	}

	private class EditConnectionAntOServer extends
	AntOServerBaseClass {
		public EditConnectionAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.EditConnectionAntO(pData[0], pData[1])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("EditConnectionAntOServer FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				if (mCallBack != null)
//					mCallBack
//							.AntOWebResponseEditTagInfoSuccess(GlobalData.mLastServerError);
//			} catch (Exception e) {
//				MyLog.i("EditConnectionAntOServer On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.EditConnectionAntO(pData[0], pData[1]);
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
				mCallBack
						.AntOWebResponseEditTagInfoSuccess(GlobalData.mLastServerError);
			
		}
	}
	
	//delete connection
	
	public void DeleteConnection(final String tagid) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new DeleteConnectionAntOServer(context).execute(tagid);
				}
			});
		} catch (Exception e) {
		}
	}

	private class DeleteConnectionAntOServer extends
	AntOServerBaseClass {
		public DeleteConnectionAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.DeleteConnectionAntO(pData[0])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("DeleteConnectionAntOServer FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				if (mCallBack != null)
//					mCallBack
//							.AntOWebResponseDeleteConnectionSuccess(GlobalData.mLastServerError);
//			} catch (Exception e) {
//				MyLog.i("DeleteConnectionAntOServer On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.DeleteConnectionAntO(pData[0]);
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
				mCallBack
						.AntOWebResponseDeleteConnectionSuccess(GlobalData.mLastServerError);
			
		}
	}

	// notify tag owner

	public void NotifyTagOwner(final String tagid, final String Message,final String uid) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new NotifyTagOwnerAntOServer(context).execute(tagid, Message,uid);
				}
			});
		} catch (Exception e) {
		}
	}

	private class NotifyTagOwnerAntOServer extends
	AntOServerBaseClass {
		public NotifyTagOwnerAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.NotifyTagOwnerAntO(pData[0], pData[1], pData[2])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("NotifyTagOwnerAntOServer FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				if (mCallBack != null)
//					mCallBack.AntOWebResponseNotifyTagOwner(
//							GlobalData.mLastServerError,
//							GlobalData.m_notficationID);
//			} catch (Exception e) {
//				MyLog.i("NotifyTagOwnerAntOServer On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.NotifyTagOwnerAntO(pData[0], pData[1], pData[2]);
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
				mCallBack.AntOWebResponseNotifyTagOwner(
						GlobalData.mLastServerError,
						GlobalData.m_notficationID);
			
		}
	}
	
	//add new colony member
	
	public void AddNewColony(final String name, final String surname,
			final String contactName,final String contactSurName,final  String contactNum, final String img,
			final String additionInfo,final String isOwnerUser) {
		try {
			Handler mHandler = new Handler();


			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new AddNewColonyAntOServer(context).execute(name, surname,contactName,contactSurName,contactNum,img,additionInfo,isOwnerUser);
				}
			});
		} catch (Exception e) {
		}
	}

	private class AddNewColonyAntOServer extends
	AntOServerBaseClass {
		public AddNewColonyAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.AddNewMemberAntO(pData[0], pData[1],pData[2], pData[3],
//						pData[4],pData[5],pData[6],pData[7])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("AddNewColonyAntOServer FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				if (mCallBack != null)
//					mCallBack.AntOWebResponseAddNewColony(
//							GlobalData.mLastServerError,
//							GlobalData.m_newMemberId);
//			} catch (Exception e) {
//				MyLog.i("AddNewColonyAntOServer On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.AddNewMemberAntO(pData[0], pData[1],pData[2], pData[3],
					pData[4],pData[5],pData[6],pData[7]);
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
				mCallBack.AntOWebResponseAddNewColony(
						GlobalData.mLastServerError,
						GlobalData.m_newMemberId);
			
		}
	}
	
	//edit colony member
	
	public void EditColony(final String name, final String surname,
			final String contactName,final String contactSurName,final  String contactNum, final String img,
			final String additionInfo,final String memberId) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new EditColonyAntOServer(context).execute(name, surname,contactName,contactSurName,contactNum,img,additionInfo,memberId);
				}
			});
		} catch (Exception e) {
		}
	}

	private class EditColonyAntOServer extends
	AntOServerBaseClass {
		public EditColonyAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.EditColonyMemberAntO(pData[0], pData[1],pData[2], pData[3],
//						pData[4],pData[5],pData[6],pData[7])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("EditColonyAntOServer FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				if (mCallBack != null)
//					mCallBack.AntOWebResponseEditColony(
//							GlobalData.mLastServerError);
//			} catch (Exception e) {
//				MyLog.i("EditColonyAntOServer On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.EditColonyMemberAntO(pData[0], pData[1],pData[2], pData[3],
					pData[4],pData[5],pData[6],pData[7]);
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
				mCallBack.AntOWebResponseEditColony(
						GlobalData.mLastServerError);
			
		}
	}
	
	//Delete colony member
	public void DeleteColony(final String memberId) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new DeleteColonyAntOServer(context).execute(memberId);
				}
			});
		} catch (Exception e) {
		}
	}

	private class DeleteColonyAntOServer extends
	AntOServerBaseClass {
		public DeleteColonyAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn. DeleteColonyAntO(pData[0])) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("DeleteColonyAntOServer FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				if (mCallBack != null)
//					mCallBack.AntOWebResponseDeleteColony(
//							GlobalData.mLastServerError);
//			} catch (Exception e) {
//				MyLog.i("DeleteColonyAntOServer On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn. DeleteColonyAntO(pData[0]);
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
				mCallBack.AntOWebResponseDeleteColony(
						GlobalData.mLastServerError);
			
		}
	}
	
	//remove tag from my bag
	public void RemoveTag(final String tagId) {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new RemoveTagAntOServer(context).execute(tagId);
				}
			});
		} catch (Exception e) {
		}
	}

	private class RemoveTagAntOServer extends
	AntOServerBaseClass {
		public RemoveTagAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Void doInBackground(String... pData) {
			try {
				if (!mServerConn. RemoveTagAntO(pData[0])) {
					// Check the error (GlobalData.mLastServerError).
					// Try to recover with automatic login if it is the case
				}

			} catch (Exception e) {
				MyLog.i("RemoveTagAntOServer FAIL: " + e.getMessage());
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void unused) {
			try {
				if (mCallBack != null)
					mCallBack.AntOWebResponseRemoveTag(
							GlobalData.mLastServerError);
			} catch (Exception e) {
				MyLog.i("RemoveTagAntOServer On PostExecute Error: "
						+ e.getMessage());
			}
		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn. RemoveTagAntO(pData[0]);
		}

		@Override
		protected void passToCallback() {
			if (mCallBack != null)
				mCallBack.AntOWebResponseRemoveTag(
						GlobalData.mLastServerError);
			
		}
	}
	
	
	//remove all notifications
		public void RemoveAllNotifications() {
			try {
				Handler mHandler = new Handler();

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						new RemoveAllNotificationsAntOServer(context).execute();
					}
				});
			} catch (Exception e) {
			}
		}

		private class RemoveAllNotificationsAntOServer extends
		AntOServerBaseClass {
			public RemoveAllNotificationsAntOServer(Context context) {
				super(context);
				// TODO Auto-generated constructor stub
			}

//			@Override
//			protected Void doInBackground(String... pData) {
//				try {
//					if (!mServerConn. RemoveAllNotificationsAntO() ){
//						// Check the error (GlobalData.mLastServerError).
//						// Try to recover with automatic login if it is the case
//					}
//
//				} catch (Exception e) {
//					MyLog.i("RemoveAllNotificationsAntOServer FAIL: " + e.getMessage());
//				}
//
//				return null;
//			}
//
//			@Override
//			protected void onPostExecute(Void unused) {
//				try {
//					if (mCallBack != null)
//						mCallBack.AntOWebResponseRemoveAllNotifications(
//								GlobalData.mLastServerError);
//				} catch (Exception e) {
//					MyLog.i("RemoveAllNotificationsAntOServer On PostExecute Error: "
//							+ e.getMessage());
//				}
//			}

			@Override
			protected boolean callApiFunction(String... pData) {
				// TODO Auto-generated method stub
				return mServerConn. RemoveAllNotificationsAntO();
			}

			@Override
			protected void passToCallback() {
				if (mCallBack != null)
					mCallBack.AntOWebResponseRemoveAllNotifications(
							GlobalData.mLastServerError);
				
			}
		}
	

	// log off

	public void LogOff() {
		try {
			Handler mHandler = new Handler();

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new LogOffAntOServer(context).execute();
				}
			});
		} catch (Exception e) {
		}
	}

	private class LogOffAntOServer extends AntOServerBaseClass {
		public LogOffAntOServer(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

//		@Override
//		protected Void doInBackground(String... pData) {
//			try {
//				if (!mServerConn.LogoffAntO()) {
//					// Check the error (GlobalData.mLastServerError).
//					// Try to recover with automatic login if it is the case
//				}
//
//			} catch (Exception e) {
//				MyLog.i("LogOffAntOServer FAIL: " + e.getMessage());
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void unused) {
//			try {
//				if (mCallBack != null)
//					mCallBack.AntOWebResponseLogOff(GlobalData.mLastServerError);
//			} catch (Exception e) {
//				MyLog.i("LogOffAntOServer On PostExecute Error: "
//						+ e.getMessage());
//			}
//		}

		@Override
		protected boolean callApiFunction(String... pData) {
			// TODO Auto-generated method stub
			return mServerConn.LogoffAntO();
		}

		@Override
		protected void passToCallback() {
			if (GlobalData.mLastServerError == AntRetStatus.OK){
				mWelcomeWord = "Log in";
				updateNotificationText(mWelcomeWord);
			}
			if (mCallBack != null){
				mCallBack.AntOWebResponseLogOff(GlobalData.mLastServerError);
			}
		}
	}

}
