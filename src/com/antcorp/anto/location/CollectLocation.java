package com.antcorp.anto.location;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.widget.MyLog;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

public class CollectLocation {

	Location location = null;
	Context context;
	LocationListener locationListener;
	Handler handler = new Handler();
	final int GPS_TIMEOUT = 30000; 
	final int GPS_UPDATE_TIME = 90000;  //update GPS location every 1.5min
	final int GPS_UPDATE_DISTANCE =0;

	
	public void setLocationListener(LocationListener locationListener) {
		this.locationListener = locationListener;
	}

	public CollectLocation(Context context) {
		super();
		this.context = context;
		this.locationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLocationChanged(Location location) {
				setGlobalLocation(location);
//				endGPSUpdate();
			}
		};

		sentGPSLocationRequest();
	}

	public Location getLocation() {
		LocationManager locMan = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = locMan.getBestProvider(criteria, true);
	
		location = locMan.getLastKnownLocation(provider);
//		location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if ((location == null)||(location.getLatitude()+location.getLongitude()==0)) {
			location = locMan
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		if (location == null) {
			location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		}
		if (location != null) {
			setGlobalLocation(location);
//			endGPSUpdate();
		}else{
			sentGPSLocationRequest();
		}
		return location;
	}

	public void setGlobalLocation(Location location) {
		GlobalData.lastestLatitude = location.getLatitude();
		GlobalData.lastestLongitude = location.getLongitude();
		GlobalData.lastestGPSAccuracy = location.getAccuracy();
	}

	public double getLatitude() {
		double nret = 0;
		Location lo = getLocation();
		if (lo != null)
			nret = location.getLatitude();

		return nret;
	}

	public double getLongitude() {
		double nret = 0;
		Location lo = getLocation();
		if (lo != null)
			nret = location.getLongitude();

		return nret;
	}

	public LocationManager getLocationManager() {
		return (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	public void sentGPSLocationRequest() {

		getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_TIME, GPS_UPDATE_DISTANCE, locationListener);
		
//		Runnable run = new Runnable() {
//
//			@Override
//			public void run() {
//				endGPSUpdate();
//
//			}
//		};
//
//		handler.postDelayed(run, GPS_TIMEOUT);


	}

	public void endGPSUpdate() {
		try {
			getLocationManager().removeUpdates(locationListener);

		} catch (Exception e) {
			MyLog.i(e);
		}
	}

}
