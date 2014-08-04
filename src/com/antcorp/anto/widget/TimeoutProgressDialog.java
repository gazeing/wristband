package com.antcorp.anto.widget;

import com.antcorp.anto.data.GlobalData;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

public class TimeoutProgressDialog extends ProgressDialog {

	// time: the time u want to dismiss progress
	// default value is 16 s
	long time = 16000;

	public TimeoutProgressDialog(Context context) {
		super(context);

	}

	public TimeoutProgressDialog(Context context, long time) {
		super(context);

		this.time = time;
	}
	
	

	@Override
	public void setMessage(CharSequence message) {

		//silvio do not want show any msg to user
		super.setMessage("");
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		timerDelayRemoveDialog();
		registerToGlobal();
	}

	public void timerDelayRemoveDialog() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if (TimeoutProgressDialog.this != null)
					if (TimeoutProgressDialog.this.isShowing())
						TimeoutProgressDialog.this.dismiss();
			}
		}, time);
	}
	
	
	private void registerToGlobal(){
		GlobalData.m_currentPd = this;
	}

	@Override
	protected void onStop() {
		GlobalData.m_currentPd = null;
		super.onStop();
	}

}
