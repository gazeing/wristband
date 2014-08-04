package com.antcorp.anto.network;



import com.antcorp.anto.widget.MyLog;

import android.util.Base64;

public class LoginAuth {
	static String auth = null;

	public static String getAuth() {
		return auth;
	}

	public static void setAuth(String auth) {
		LoginAuth.auth = auth;
	}
	
	public static String setAuth(String name, String password) {
		
		byte[] data;
		try {
			data = (name+":"+password).getBytes("ASCII");
			auth = new String(Base64.encode(data,  Base64.NO_WRAP),"ASCII");
		} catch (Exception e) {
			MyLog.i(e.getMessage());
		}
		return auth;
	}

}
