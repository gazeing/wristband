package com.antcorp.anto.fragment_n_adapter;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ActiveFragment extends Fragment 
{
	private RelativeLayout ll;
	private FragmentActivity fa;

	ListView listview;

	ActiveAdapter adapter1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		fa = super.getActivity();
		// Intent intent = fa.getIntent();
		ll = (RelativeLayout) inflater.inflate(R.layout.fragment_active,
				container, false);
		init();

		return ll;
	}
	
	@Override
	public void onResume() 
	{
		super.onResume();

		//getList();
		adapter1.notifyDataSetChanged();
	}


	protected void addActive(String name, String info, String tagid, int i) 
	{
		Active a = new Active(name, tagid, info, i);	
		
		adapter1.add(a);
		adapter1.notifyDataSetChanged();
	}


	private void init() {
		listview = (ListView) ll.findViewById(R.id.listView1);

		adapter1 = new ActiveAdapter(fa, GlobalData.m_actives);

		listview.setAdapter(adapter1);
		
		GlobalData.activeFragment =this;
	}
}
