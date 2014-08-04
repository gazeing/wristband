package com.antcorp.anto.nfc;

import com.antcorp.anto.widget.MyLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NFCBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		MyLog.i("#################################  NFCBroadcastReceiver   "+intent.toString());

	}

}
