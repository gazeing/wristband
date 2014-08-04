package com.antcorp.anto.nfc;

import java.util.Observable;
import java.util.Observer;





import com.antcorp.anto.widget.MyLog;

import android.annotation.TargetApi;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class NFCActivity extends FragmentActivity implements Observer {

	protected NFCForegroundUtil nfcForegroundUtil = null;
	protected boolean remindeNfc = false; //the flag shows if has already bring user to the page opens NFC
	protected Tag tag = null;
	
	protected String nfcContent;   //stores the content have to be written to nfc tags;
	protected NdefMessageWriter nmw = new NdefMessageWriter(this);
	
	public String readText = "";  //this is the field stores text read from nfc tag
	public String readUid = "";  //this is the field stores Uid read from nfc tag
	
	public static String PACKAGE_NAME;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		//set package name to writer, force the tag's ApplicationRecord
		 PACKAGE_NAME = getApplicationContext().getPackageName();
		 nmw.setPackageName(PACKAGE_NAME);
		 
		if (CheckVersion.CheckVersion10())
			nfcForegroundUtil = new NFCForegroundUtil(this);
		
//		GlobalData.m_Observer.addObserver(this);  //register the observer


	}
	
	protected boolean isNfcAvailable() {
		if (CheckVersion.CheckVersion11()) {
			if (nfcForegroundUtil == null)
				return false;
			if (nfcForegroundUtil.getNfc() == null)
				return false;
			return true;
		} else
			return false;
	}

	@Override
	protected void onPause() {

		super.onPause();
		if (isNfcAvailable()) {
			nfcForegroundUtil.disableForeground();
		}
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onResume() {

		super.onResume();

		if (isNfcAvailable()) {

			nfcForegroundUtil.enableForeground();

			if (!nfcForegroundUtil.getNfc().isEnabled()) {
				if (!remindeNfc) {
					Toast.makeText(
							getApplicationContext(),
							"Please activate NFC and press Back to return to the application!",
							Toast.LENGTH_LONG).show();
					startActivity(new Intent(
							android.provider.Settings.ACTION_WIRELESS_SETTINGS));
					remindeNfc = true;
				}
			}
		}
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
//		GlobalData.m_Observer.deleteObserver(this);  //delete the observer
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	@Override
	protected void onNewIntent(Intent intent) {

		// super.onNewIntent(intent);
		if (CheckVersion.CheckVersion10()) {
			try {
				readText="";
				
				setIntent(intent);
				NFCResolver nfcR = new NFCResolver(this);
				Tag tag_g = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				String uid = nfcR.ByteArrayToHexString(tag_g.getId());
				readUid =uid;
				if (nfcContent != null) {

					nmw.WriteNdefTag(intent, nfcContent);
				}


				String text = nfcR.resolveIntent(intent);

//				if (text == null) {
//					tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//					text = NFCUtil.readTag(tag);
//					if ((text == null) || (text.length() == 0))
//						return;
//					// showNfcPage(text);
//				}

				if ((text == null) || (text.length() == 0))
					return;

				
				readText = text;

				
			} catch (Exception e) {
				MyLog.i(e.getMessage());
			}

		}

	}
	
	protected void writeNFCTag(String text) {

		if (isNfcAvailable()) {
			nfcContent = text;



		} else
			Toast.makeText(this,
					"Please make sure NFC option is on in your device.",
					Toast.LENGTH_SHORT).show();
	}

	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		
	}

}
