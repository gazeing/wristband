package com.antcorp.anto.fragment_n_adapter;

import com.antcorp.anto.EditTagActivity;
import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ConnectionFragment extends Fragment 
{
	private RelativeLayout ll;
	private FragmentActivity fa;

	ListView  listview;
	ColonyAdapter adapter1;
	Button btn_menu;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		fa = super.getActivity();
		ll = (RelativeLayout) inflater.inflate(R.layout.fragment_connection,container, false);
		
		init();

		return ll;
	}

	///create optional menu here
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
	    inflater.inflate(R.menu.my_colony_menu, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch(item.getItemId()){
	    	case R.id.new_contact:{
	    		addNewMember();
	    	}
	    		break;

	    }
	    
	    return true;
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		
		GlobalData.connectionFragment = null;
	}

	@Override
	public void onResume() 
	{
		super.onResume();

		adapter1.notifyDataSetChanged();
	}


	private void init() 
	{
		listview = (ListView) ll.findViewById(R.id.listView1);
		
		adapter1 = new ColonyAdapter(fa, GlobalData.m_colonys);
		listview.setAdapter(adapter1);
			
		GlobalData.connectionFragment = this;
	
		
		
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

	protected void addNewMember() {
		fa.startActivity(new Intent(fa,EditTagActivity.class));
		
	}
}
