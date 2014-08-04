package com.antcorp.anto;

import java.util.ArrayList;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.fragment_n_adapter.ChatMsg;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.widget.UtilStatics;
import com.antcorp.anto.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.view.Window;

public class FullMapActivity extends FragmentActivity {

	private GoogleMap mMap;
	// LatLng ll = null;

	ArrayList<LatLng> locations = new ArrayList<LatLng>();
	ArrayList<ChatMsg> msgs = new ArrayList<ChatMsg>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		getMsgArray();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_map);

		ViewGroup mapHost = (ViewGroup) findViewById(R.id.map_view);
		mapHost.requestTransparentRegion(mapHost);

//		Button btn_back = (Button) findViewById(R.id.button1);
//		btn_back.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				FullMapActivity.this.finish();
//			}
//		});

		setUpMapIfNeeded();
	}

	private void getMsgArray() {
		if (getIntent().getStringExtra("from").equals("chat")) {
			msgs = GlobalData.m_map_chatMsgs;
		} else if (getIntent().getStringExtra("from").equals("msg")) {
			msgs = GlobalData.m_map_chatMsgs;
		}

		MyLog.i("GlobalData.m_current_chatMsgs.size =  "
				+ GlobalData.m_current_chatMsgs.size());
	}

	private void setUpMap() {
		// We will provide our own zoom controls.
		mMap.getUiSettings().setZoomControlsEnabled(true);

		for (ChatMsg msg : msgs) {
			LatLng ll = new LatLng(msg.getLatitude(), msg.getLongitude());
			
			long realtime = UtilStatics
					.getLongFromServerTimeFormat(msg.getTime());
			String timeString = UtilStatics.TransferTimeFormat(realtime);

			Marker m = mMap.addMarker(new MarkerOptions()
					.position(ll)
					.title(msg.getMsg())
					.snippet(timeString)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

			m.showInfoWindow();

			mMap.setMyLocationEnabled(false);
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 15));

			locations.add(ll);
		}
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (mMap != null) {
				setUpMap();
			}
		}
	}
}
