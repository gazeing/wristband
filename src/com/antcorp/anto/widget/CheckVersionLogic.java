package com.antcorp.anto.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;


public class CheckVersionLogic {

	Activity fa;

	public CheckVersionLogic(Activity fa) {
		super();
		this.fa = fa;
	}
	
	public boolean checkVersion(String current_version){
		PackageInfo pInfo;
		try {
			pInfo = fa.getPackageManager().getPackageInfo(fa.getPackageName(), 0);
			String version = pInfo.versionName;

				
				return worthToUpdate(version,current_version);

		} catch (NameNotFoundException e) {
			MyLog.i(e);
		}
		catch (Exception e) {
			MyLog.i(e);
		}
		return false;
	}
	
	private boolean worthToUpdate(String version, String current_version) {

		try {
		double packageVersion = Double.parseDouble(version);
		double serverVersion = Double.parseDouble(current_version);
		if(packageVersion>=serverVersion)
			return true;
		} catch (NumberFormatException e) {
			MyLog.i(e);
		}
		
		

		return false;
	}

	public AlertDialog askForUpdate() {
		AlertDialog.Builder adb = new AlertDialog.Builder(fa);
		adb.setTitle("Update");
		adb.setMessage("We have a new verion of Ant-O, would you like to update?");
		adb.setPositiveButton("No", new DialogInterface.OnClickListener() {//
					@Override
					public void onClick(DialogInterface dialog, int i) {


						fa.finish();

					}
				});

		adb.setNeutralButton("Yes", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				String url = "https://play.google.com/store/apps/details?id=com.antcorp.anto";
				StartBrowser sb = new StartBrowser(url, fa);
				MyLog.i("StartBrowser: " + url);
				sb.startBrowse();
				
				fa.finish();

			}
		});

		return adb.show();
		
	}
}
