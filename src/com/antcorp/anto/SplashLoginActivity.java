package com.antcorp.anto;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;










import com.antcorp.anto.data.AntOUser;
import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.database.DBOperator;
import com.antcorp.anto.fragment_n_adapter.MyTag;
import com.antcorp.anto.gcm.GCMActivity;
import com.antcorp.anto.interfaces.ServiceCallBackBase;
import com.antcorp.anto.location.CollectLocation;
import com.antcorp.anto.network.AntRetStatus;
import com.antcorp.anto.service.AntNotificationService;
import com.antcorp.anto.widget.CheckVersionLogic;
import com.antcorp.anto.widget.CollectPhoneInformation;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.R;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Window;

public class SplashLoginActivity extends GCMActivity {
	
	private boolean isTimeout = false;
	private boolean isNetworkDone = false;
	private boolean isLoginOk = false;
	AlertDialog ad;
//	ProgressDialog pd;

	DBOperator db = new DBOperator(this);
	
	Intent mIntentBkgService = null;
	WebResponse mWebCall = new WebResponse();
	
	AntNotificationService mBoundService;
	String jsonString; 
	CollectLocation cl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				
//				pd = new ProgressDialog(SplashLoginActivity.this);
//				pd.setCanceledOnTouchOutside(false);
//				pd.setCancelable(false);
//				
//				pd.setMessage("Collecting MacAddress...");
//				pd.show();
				
				CollectPhoneInformation cl = new CollectPhoneInformation(
						SplashLoginActivity.this.getApplicationContext());
				GlobalData.m_macString = cl.getMacAddress();
				
//				pd.setMessage("Preparing user info...");
				jsonString = db.withdrawUserInfo(GlobalData.m_macString);
//				pd.setMessage("Preparing GPS...");
				initGpsLocation();
				mIntentBkgService = new Intent(SplashLoginActivity.this, AntNotificationService.class);
				startService(mIntentBkgService);
				bindService(mIntentBkgService, mConnection, Context.BIND_AUTO_CREATE);

				isTimeout = true;
//				pd.dismiss();
				jump();
			}
		}, 500);
	}


	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mBoundService = ((AntNotificationService.LocalBinder) service)
					.getService();

			GlobalData.m_service = mBoundService; // store instance

			mBoundService.SetCallBackFunc(mWebCall);
			// get the current version
			mBoundService.CheckVersionAntO();



		}

		@Override
		public void onServiceDisconnected(ComponentName className) {

			mBoundService = null;
			GlobalData.m_service = null;

			MyLog.i("Binding", "called onServiceDisconnected");
		}

	};
	
	public void tryDoLogin() {
		if (jsonString != null) {

			JSONObject json;
			try {
				json = new JSONObject(jsonString);
				String username = json.getString("email");
				String password = json.getString("pwd");
				if ((username != null) && (password != null))
					Login(username, password);
			} catch (JSONException e) {
				MyLog.i(e);
			}

		}
		
	}
	
	
	private void onNetworkDone() {
		isNetworkDone = true;

		jump();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		cl.endGPSUpdate();
	}
	private void initGpsLocation() {
		cl = new CollectLocation(this);
		cl.getLocation();
	}

	public void Login(String psEmail, String psPwd) {
		if (mBoundService == null)
			return;

		mBoundService.LoginAntO(psEmail, psPwd, getRegid());
	}

//	public Intent passNFCBundle(Intent i){
//		if(getIntent().hasExtra("nfc")){
//			i.putExtra("nfc", getIntent().getBundleExtra("nfc"));
//		}
//		return i;
//	}
	
	protected void jump() {
		if (isTimeout && isNetworkDone) {
			if (isLoginOk) {
				Intent intent = new Intent(SplashLoginActivity.this,
						MainActivity.class);
//				passNFCBundle(intent);
				SplashLoginActivity.this.startActivity(intent);
				SplashLoginActivity.this.finish();

			} else {
				Intent intent = new Intent(SplashLoginActivity.this,
						LoginBaseActivity.class);
//				passNFCBundle(intent);
				SplashLoginActivity.this.startActivity(intent);
				SplashLoginActivity.this.finish();
			}

		}
		//if user never store use info, there is no call to login, direct go to login screen
		else if(jsonString==null){
			Intent intent = new Intent(SplashLoginActivity.this,
					LoginBaseActivity.class);
//			passNFCBundle(intent);
			SplashLoginActivity.this.startActivity(intent);
			SplashLoginActivity.this.finish();
		}
	}
	
	@Override
	public void onBackPressed() {

		super.onBackPressed();
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
	
	
	class WebResponse extends ServiceCallBackBase {

		public WebResponse() {
			super(SplashLoginActivity.this);
			
		}



		@Override
		public void AntOWebResponseLogin(int piRetCode, AntOUser pUser,
				ArrayList<MyTag> pMyBag) {

			if (piRetCode == AntRetStatus.OK) {
				isLoginOk = true;
			} else {
				isLoginOk = false;
			}
			onNetworkDone();
		}


		@Override
		public void AntOWebResponseCheckVersion(int piRetCode,
				String m_current_version) {
			
			if (piRetCode == AntRetStatus.OK) {
			

			CheckVersionLogic cvl = new CheckVersionLogic(SplashLoginActivity.this);
			if(cvl.checkVersion(m_current_version)){
				tryDoLogin();
			}
			else{
				ad = cvl.askForUpdate();
			}

			

			
			}else{
				
				SplashLoginActivity.this.finish();
			}

		}

	
	}







}
