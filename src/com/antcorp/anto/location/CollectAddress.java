package com.antcorp.anto.location;

import java.util.List;




import com.antcorp.anto.widget.MyLog;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

public class CollectAddress {

	public static String getLocationInfoString(Context context,double latitude, double longitude) {
        context.getSystemService(Context.LOCATION_SERVICE);
		Geocoder gc = new Geocoder(context);

		List<Address> adds = null;
		
		try {
			adds = gc.getFromLocation(latitude, longitude, 1);
		} catch (Exception e) {
			
			MyLog.i("location",e.toString());
			return "Unknown";
		}
		
		if (adds==null)
			return "Unknown";
		if (adds.size()>0)
			return adds.get(0).getLocality()+", "+adds.get(0).getCountryName()+", "+adds.get(0).getPostalCode();
		
		else return "Unknown";
	}
}
