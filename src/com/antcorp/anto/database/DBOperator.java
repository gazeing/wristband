package com.antcorp.anto.database;



import javax.crypto.BadPaddingException;

import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.widget.StringTransfer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBOperator {


	private DBOpenHelper helper;

	public DBOperator(Context context) {
		helper = new DBOpenHelper(context);

	}
	
	//store encrypted user info (a json string of account and password) to database
	public void storeUserInfo(String macAddress, String info) {
		String cipher = "";
		SQLiteDatabase db = helper.getWritableDatabase();

		db.delete("userinfo", null, null);
		ContentValues values = new ContentValues();

		try {
			cipher = StringTransfer.encrypt(macAddress, info);
			MyLog.i("encrypt",macAddress+"+"+info+"="+cipher);
		} catch (Exception e) {
			MyLog.i(e);
		}
		values.put("infodata", cipher);
		//db.delete("userinfo", null, null);

		db.insert("userinfo", null, values);

		db.close();
	}
	
	//clean the userinfo
	public void cleanUserInfo(){
		SQLiteDatabase db = helper.getWritableDatabase();

		db.delete("userinfo", null, null);
		db.close();
	}

	//get user info (a json string of account and password) from database
	public String withdrawUserInfo(String macAddress) {
		String info = null;
		try {
			SQLiteDatabase db = helper.getReadableDatabase();

			// query all record
			Cursor c = db.query(false, "userinfo", new String[] { 
					"infodata" }, null, null, null, null, null, null);

			String cipher = "";
			while (c.moveToNext())
				cipher = c.getString(0);
			c.close();
			db.close();
			
			if (cipher.length()==0)
				return null;

			MyLog.i("decrypt",macAddress+"+"+info+"="+cipher);
			info = StringTransfer.decrypt(macAddress, cipher);
			MyLog.i("decrypt",macAddress+"+"+info+"="+cipher);
		}catch (BadPaddingException e) {
			MyLog.i(e);
		} 
		catch (Exception e) {
			MyLog.i(e);
		}
		return info;
	}
	
	//write the current version data to database
	public void writeCurrentVersion(String version){
		try {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("current_version", version);
		db.delete("Version", null, null);

		db.insert("Version", null,values);
		db.close();
		} catch (Exception e) {
			MyLog.i(e);
		}
	}
	
	//get the version from database
	public String readCurrentVersion(){
		String version = null;
		try {
			SQLiteDatabase db = helper.getReadableDatabase();

			// query all record
			Cursor c = db.query(false, "Version", new String[] { 
					"current_version" }, null, null, null, null, null, null);


			while (c.moveToNext())
				version = c.getString(0);
			c.close();
			db.close();


		} catch (Exception e) {
			MyLog.i(e);
		}
		return version;
	}
}
