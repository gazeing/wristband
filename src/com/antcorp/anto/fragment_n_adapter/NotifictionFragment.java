package com.antcorp.anto.fragment_n_adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

import com.antcorp.anto.ChatActivity;
import com.antcorp.anto.MainActivity;
import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.widget.TimeoutProgressDialog;
import com.antcorp.anto.R;

public class NotifictionFragment extends Fragment implements OnItemClickListener{

	private RelativeLayout ll;
	private FragmentActivity fa;

	ListView  listview;
	
	NotificationAdapter adapter1 ;
	
	Button btn_menu;
	TimeoutProgressDialog pd;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fa = super.getActivity();
		// Intent intent = fa.getIntent();
		ll = (RelativeLayout) inflater.inflate(R.layout.fragment_notification,
				container, false);
		init();

		return ll;
	}

	///create optional menu here
		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		   // inflater=fa.getMenuInflater();
		    inflater.inflate(R.menu.notification_menu, menu);

			super.onCreateOptionsMenu(menu, inflater);
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch(item.getItemId())
		    {
		    case R.id.remove_all:
		    	removeAllNotifications();
		        break;

		    }
		    return true;
		}


	@Override
	public void onResume() {
		
		super.onResume();
		

		adapter1.notifyDataSetChanged();
	}



	private void removeAllNotifications() {
		((MainActivity)fa).removeAllNotifications();
		
	}

	private void init() {
		pd = new TimeoutProgressDialog(fa);
		pd.setCanceledOnTouchOutside(false);
		pd.setCancelable(false);
		listview = (ListView) ll.findViewById(R.id.listView1);
		
		adapter1 = new NotificationAdapter(fa, GlobalData.m_notifications);
		listview.setAdapter(adapter1);
		listview.setOnItemClickListener(this);
		
		GlobalData.notifictionFragment = this;
		
		setHasOptionsMenu(true);
		btn_menu = (Button) ll.findViewById(R.id.button_menu);
		btn_menu.setClickable(true);
		btn_menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fa.openOptionsMenu();
				
			}
		});
		
		
	}


	@Override
	public void onItemClick (AdapterView<?> parent, View view, int position, long id){
		Notification noti = GlobalData.m_notifications.get(position);
		Intent intent = new Intent(fa,ChatActivity.class);
		intent.putExtra("tagid", noti.tagid);
		intent.putExtra("notiId", noti.notificationId);
		intent.putExtra("uid", noti.uid);
		intent.putExtra("sender_name", noti.sender_name);
		intent.putExtra("sender_surname", noti.sender_surname);
		intent.putExtra("owner_name", noti.about_name);
		intent.putExtra("owner_surname", noti.about_surname);
		intent.putExtra("owner_count", noti.msg_count_owner);
		intent.putExtra("sender_count", noti.msg_count_sender);
		intent.putExtra("max_count", noti.msg_max);
		fa.startActivity(intent);
		
	}
}
