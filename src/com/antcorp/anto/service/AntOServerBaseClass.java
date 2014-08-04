package com.antcorp.anto.service;

import com.antcorp.anto.R;
import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.network.AntRetStatus;
import com.antcorp.anto.widget.MyLog;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public abstract class AntOServerBaseClass  extends
AsyncTask<String, Void, Void> {
	
	Context context;
	boolean isCallfailed;
	
	

	public AntOServerBaseClass(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected Void doInBackground(String... params) {
		try {
			isCallfailed=!callApiFunction(params);


		} catch (Exception e) {
			MyLog.i("Server FAIL: " + e.getMessage());
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void unused) {
		try {
			if (GlobalData.m_currentPd!=null)
				GlobalData.m_currentPd.dismiss();
			if(isCallfailed){
				if (GlobalData.mLastServerError==AntRetStatus.INTERNAL_FAILURE){
					Toast.makeText(context, R.string.connection_field,
							Toast.LENGTH_LONG).show();
					return;
				}else if (GlobalData.mLastServerError==AntRetStatus.SERVER_ERROR){
					Toast.makeText(context, R.string.server_error,
							Toast.LENGTH_LONG).show();
					return;
				}
			}
			
				passToCallback();
		} catch (Exception e) {
			MyLog.i("Server On PostExecute Error: "
					+ e.getMessage());
		}
	}
	protected abstract boolean callApiFunction(String... pData) ;
	protected abstract void passToCallback() ;

	
	
	


}
