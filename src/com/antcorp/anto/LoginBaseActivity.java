package com.antcorp.anto;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.antcorp.anto.data.AntOUser;
import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.data.TagInfo;
import com.antcorp.anto.database.DBOperator;
import com.antcorp.anto.fragment_n_adapter.ChatMsg;
import com.antcorp.anto.fragment_n_adapter.LoginFragment;
import com.antcorp.anto.fragment_n_adapter.MyTag;
import com.antcorp.anto.fragment_n_adapter.Notification;
import com.antcorp.anto.fragment_n_adapter.RegisterFragment;
import com.antcorp.anto.gcm.GCMActivity;
import com.antcorp.anto.interfaces.ServiceInterface;
import com.antcorp.anto.network.AntRetStatus;
import com.antcorp.anto.service.AntNotificationService;
import com.antcorp.anto.widget.CheckVersionLogic;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.widget.TimeoutProgressDialog;
import com.antcorp.anto.R;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;

public class LoginBaseActivity extends GCMActivity 
{
	DBOperator db = new DBOperator(this);
	Intent mIntentBkgService	= null;
	
	private ViewPager mPager;
	
	TimeoutProgressDialog pd;
	AlertDialog ad;

	WebResponse	mWebCall = new WebResponse();
	
	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private ScreenSlidePagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mIntentBkgService = new Intent(this, AntNotificationService.class);
		startService(mIntentBkgService);
		
		bindService(mIntentBkgService, mConnection, Context.BIND_AUTO_CREATE);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_loginbase);

		pd = new TimeoutProgressDialog(this);
		pd.setCanceledOnTouchOutside(false);
		pd.setCancelable(false);

		initPaging();
		
		MyLog.i("this machine mac address = " + GlobalData.m_macString);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		if (mBoundService != null){
			mBoundService.SetCallBackFunc(mWebCall);
		
		}
			
		DefineScreenSize();
	}
	
	
	@SuppressWarnings("unused")
	public void DefineScreenSize() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int miMetricType = metrics.densityDpi;
		int miDispWidth = metrics.widthPixels;

		int miDispDensity = (int) (metrics.density * 16 + (metrics.densityDpi / 16) + 0.5);

		int miDispHeight = 0;
		
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
	
	
	AntNotificationService mBoundService;

	private ServiceConnection mConnection = new ServiceConnection() 
	{
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mBoundService = ((AntNotificationService.LocalBinder) service).getService();
			
			GlobalData.m_service = mBoundService; // store instance
			
			mBoundService.SetCallBackFunc(mWebCall);
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
			GlobalData.m_service = null;

			MyLog.i("Binding", "called onServiceDisconnected");
		}

	};
	


	private void initPaging() 
	{
		LoginFragment fragmentLogin = new LoginFragment();
		fragmentLogin.SetActivityProvider(this);
		
		RegisterFragment fragmentRegister = new RegisterFragment();
		fragmentRegister.SetActivityProvider(this);

		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPagerAdapter.addFragment(fragmentLogin);
		mPagerAdapter.addFragment(fragmentRegister);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mPagerAdapter);
		
		//check if the currentversion is null, show the register page;
		
		String version = db.readCurrentVersion();
		MyLog.i("current version is = "+version);
		if(version==null){
			setToRegisterPage();
		}
		
	}

	@Override
	public void onBackPressed() 
	{
		if (mPager.getCurrentItem() == 0) {
			confirmExit();
		} else {
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
	}

	
	public void setToRegisterPage() 
	{
		if (mPager.getCurrentItem() == 0)
			mPager.setCurrentItem(1);
	}

	
	private void confirmExit() {
		AlertDialog.Builder adb = new AlertDialog.Builder(LoginBaseActivity.this);
		adb.setTitle("Quit");
		adb.setMessage("Quit Ant-O?");
		adb.setPositiveButton("Quit", new DialogInterface.OnClickListener() {//
					@Override
					public void onClick(DialogInterface dialog, int i) {

						GlobalData.CleanUp();

						if (mBoundService != null) {
							mBoundService.SetCallBackFunc(null);
							mBoundService.StopAllServices();
						}

						if (mConnection != null) {
							unbindService(mConnection);
						}

						if (mIntentBkgService != null) {
							stopService(mIntentBkgService);
							mIntentBkgService = null;
						}

						mBoundService = null;

						LoginBaseActivity.this.finish();//

					}
				});
//
//		adb.setNeutralButton("Logoff", new OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (mBoundService != null) {
//
//					pd.setMessage("Logoff...");
//					pd.show();
//					mBoundService.LogOff();
//				}
//			}
//		});

		ad = adb.show();
	}
	@Override
	protected void onStop() 
	{
		try{
			if (mBoundService != null)
				unbindService(mConnection);
		}
		catch(Exception e){
			
		}
		super.onStop();
	}


	/**
	 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects,
	 * in sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter 
	{
		private final List<Fragment> mFragments = new ArrayList<Fragment>();

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public void addFragment(Fragment fragment) {
			mFragments.add(fragment);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}

		@Override
		public Fragment getItem(int position)
		{
			return mFragments.get(position);
		}
	}
	
	public void Login(String psEmail, String psPwd)
	{
		if (mBoundService == null)
			return;
		
		pd.setMessage("Login...");
		pd.show();
		mBoundService.LoginAntO(psEmail, psPwd,getRegid());
	}
	public void storeUserInfo(String email, String pwd){
		
		JSONObject json = new JSONObject();
		try {
			json.put("email", email);
			json.put("pwd",pwd);
		} catch (JSONException e) {
			MyLog.i(e);
		}
	
		db.storeUserInfo(GlobalData.m_macString, json.toString());
	}
	public void cleanUserInfo(){
		db.cleanUserInfo();
		
	}

	public void Registration(String psName, String psSurBame, String psEmail, String psPwd)
	{
		if (mBoundService == null)
			return;
		pd.setMessage("Registering...");
		pd.show();
		mBoundService.RegistrationAntO(psName, psSurBame, psEmail, psPwd,getRegid());
	}
	
	public void forgetPassword(String email) {
		if (mBoundService == null)
			return;
		pd.setMessage("Send...");
		pd.show();
		mBoundService.ForgetPwAntO(email);
	}
	
//	public Intent passNFCBundle(Intent i){
//		if(getIntent().hasExtra("nfc")){
//			i.putExtra("nfc", getIntent().getBundleExtra("nfc"));
//		}
//		return i;
//	}
	
//	public void askForUpdate() {
//		AlertDialog.Builder adb = new AlertDialog.Builder(LoginBaseActivity.this);
//		adb.setTitle("Update");
//		adb.setMessage("We have a new verion of Ant-O, would you like to update?");
//		adb.setPositiveButton("No", new DialogInterface.OnClickListener() {//
//					@Override
//					public void onClick(DialogInterface dialog, int i) {
//
//
//						LoginBaseActivity.this.finish();
//
//					}
//				});
//
//		adb.setNeutralButton("Yes", new OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				
//				String url = "https://play.google.com/store/apps/details?id=com.antcorp.anto";
//				StartBrowser sb = new StartBrowser(url, LoginBaseActivity.this);
//				MyLog.i("StartBrowser: " + url);
//				sb.startBrowse();
//				
//				LoginBaseActivity.this.finish();
//
//			}
//		});
//
//		ad = adb.show();
//		
//	}
	
	class WebResponse implements ServiceInterface
	{
		@Override
		public void AntOWebResponseMyBag(int piRetCode, ArrayList<MyTag> pMyBag) 
		{
			
		}

		@Override
		public void AntOWebResponseLogin(int piRetCode, AntOUser pUser,ArrayList<MyTag> pMyBag) 
		{
			pd.dismiss();
			if (piRetCode == AntRetStatus.OK){
				Intent i = new Intent(LoginBaseActivity.this, MainActivity.class);
//				passNFCBundle(i);
				startActivity(i);
				
				finish();
			}
			else{
				Toast.makeText(getApplicationContext(), R.string.usr_pwd_not_match, Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void AntOWebResponseRegistration(int piRetCode, AntOUser pUser, ArrayList<MyTag> pMyBag) 
		{
			pd.dismiss();
			if (piRetCode == AntRetStatus.OK){
				Intent i = new Intent(LoginBaseActivity.this, MainActivity.class);
//				passNFCBundle(i);
				startActivity(i);

				finish();
			}else if (piRetCode == AntRetStatus.USER_EXIST){
				Toast.makeText(getApplicationContext(), R.string.email_already_registered, Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(getApplicationContext(), R.string.usr_pwd_not_match, Toast.LENGTH_LONG).show();
			}
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
				ArrayList<Notification> pMyNoti) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void AntOWebResponseLogOff(int piRetCode) {
			// TODO Auto-generated method stub
			
		}



		@Override
		public void AntOWebResponseEditTagInfoSuccess(int piRetCode) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void AntOWebResponseAddToBagSuccess(int piRetCode) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void AntOWebResponseGetTagInfo(int piRetCode, TagInfo taginfo) {
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
			// TODO Auto-generated method stub
			
		}

		@Override
		public void AntOWebResponseEditColony(int piRetCode) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void AntOWebResponseCheckVersion(int piRetCode,
				String m_current_version) {
			// TODO Auto-generated method stub
			MyLog.i("#############check version "+ m_current_version);
			if (piRetCode == AntRetStatus.OK) {
				
				CheckVersionLogic cvl = new CheckVersionLogic(LoginBaseActivity.this);
				if(cvl.checkVersion(m_current_version)){
				
				}
				else{
					ad = cvl.askForUpdate();
				}

				
//				PackageInfo pInfo;
//				try {
//					pInfo = LoginBaseActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
//					String version = pInfo.versionName;
//					if(version.equals(m_current_version)){
//						
////						tryDoLogin();
//					}else{
//						//advise user to update it
//						askForUpdate();
//					}
//				} catch (NameNotFoundException e) {
//					MyLog.i(e);
//				}
			}
		}

		@Override
		public void AntOWebResponseDeleteColony(int mLastServerError) {
			// TODO Auto-generated method stub
			
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
			pd.dismiss();
			if (piRetCode == AntRetStatus.OK){
				Toast.makeText(getApplicationContext(), R.string.recovery_pw, Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(getApplicationContext(), R.string.recovery_pw_fail, Toast.LENGTH_LONG).show();
			}
			
		}

		@Override
		public void AntOWebResponseRemoveTag(int piRetCode) {
			// TODO Auto-generated method stub
			
		}
		
	}




}
