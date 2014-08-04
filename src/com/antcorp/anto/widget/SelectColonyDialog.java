package com.antcorp.anto.widget;

import java.util.Observable;

import com.antcorp.anto.TagInfoActivity;
import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.fragment_n_adapter.Colony;
import com.antcorp.anto.fragment_n_adapter.DialogColonyAdapter;
import com.antcorp.anto.fragment_n_adapter.MyTag;
import com.antcorp.anto.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class SelectColonyDialog extends Observable implements OnItemClickListener/*, OnKeyListener*/ {
	
	Context context;
	ListView listview;
	DialogColonyAdapter adapter1;
	Dialog addDialog;
	String tagId;
	OnDismissListener od = null;
	public void setOd(OnDismissListener od) {
		this.od = od;
	}

	public Dialog ShowDialog(final Context context,MyTag mytag)
	{
		this.context = context;
		this.tagId = mytag.getTagId();
		addDialog = new Dialog(context, R.style.AddBagDialog);
		if(od!=null)
		addDialog.setOnDismissListener(od);
		addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		addDialog.setContentView(R.layout.dialog_select_colony);
		
		listview = (ListView) addDialog.findViewById(R.id.listView1);
		
		adapter1 = new DialogColonyAdapter(context, GlobalData.m_colonys);
		listview.setAdapter(adapter1);
		listview.setOnItemClickListener(this);


		TextView tv_tagname = (TextView) addDialog.findViewById(R.id.textView1);
		tv_tagname.setText(mytag.getTagName());
		
		
		addDialog.show();
		
		return addDialog;
	}
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		Colony c = GlobalData.m_colonys.get(position);

		 setChanged();
		notifyObservers();
		
		Intent i = new Intent(context, TagInfoActivity.class);
		
		i.putExtra("tagid",tagId );
		i.putExtra("memberid", c.getColony_member_id());
		context.startActivity(i);
		
		addDialog.cancel();
		

	}


}
