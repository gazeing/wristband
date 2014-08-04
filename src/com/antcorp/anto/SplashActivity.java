package com.antcorp.anto;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

@SuppressLint("NewApi")
public class SplashActivity extends Activity {
	private static final int SPLASH_DISPLAY_TIME = 1000;
//Bundle nfcBundle = null;

//	NfcAdapter mNfcAdapter;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		 // Check for available NFC Adapter
//        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        if (mNfcAdapter == null) {
//            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
//            finish();
//            return;
//        }
//
//		 // Register callback
//        mNfcAdapter.setNdefPushMessageCallback(this, this);

		if(savedInstanceState != null)
			MyLog.i(savedInstanceState.toString());
		
		if(getIntent()!=null){
			MyLog.i("####################################"+getIntent().toString());
			if(getIntent().getExtras()!=null){
				MyLog.i("####################################"+getIntent().getExtras().toString());
				
				if(getIntent().getAction().equals("android.intent.action.MAIN")){
					GlobalData.m_ChatIntent = getIntent();
				}
			
			
			if(getIntent().getAction().equals("android.nfc.action.NDEF_DISCOVERED")){
				//nfcBundle = getIntent().getExtras();
				
				GlobalData.m_NFCIntent = getIntent();
			}
			}
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this,
						SplashLoginActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);


				SplashActivity.this.startActivity(intent);
				SplashActivity.this.finish();
				SplashActivity.this.overridePendingTransition(0, 0);
			}
		}, SPLASH_DISPLAY_TIME);
	}



	@Override
	public void onBackPressed() {

	
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		MyLog.i("####################################"+intent.getAction());
		super.onNewIntent(intent);
	}



//	@Override
//	public NdefMessage createNdefMessage(NfcEvent event) {
//		// TODO Auto-generated method stub
//		return null;
//	}
}
