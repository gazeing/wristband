package com.antcorp.anto;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.fragment_n_adapter.Connection;
import com.antcorp.anto.fragment_n_adapter.MyTag;
import com.antcorp.anto.widget.SelectColonyDialog;

import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;

public class ReadTagIdLogic 
{
	MainActivity context;
	String id;
	private String Uid;

	//Observer ob = null;
	
	OnDismissListener od = null;

	public void setOd(OnDismissListener od) {
		this.od = od;
	}
	public ReadTagIdLogic(MainActivity context, String id,String Uid) {
		super();
		this.context = context;
		this.id = id;
		this.Uid = Uid;
	}

	public void startLogic() 
	{
		for (MyTag tag : GlobalData.m_tags) {
			if (tag.getTagId().equals(id)) {
				startLocalLogic(tag);
				return;
			}
		}
		
		//if cannot find the tag id in my bag, call server to get back tag info
		context.getTagInfo(id,Uid);
	}

	private void startLocalLogic(MyTag tag) 
	{
		for (Connection connection : GlobalData.m_connetions) {
			if (connection.getTag_id().equals(tag.getTagId())) {
				//add one more judgement in case of  the connection has not been removed, by steven
				if(connection.getCurrent_active().equals("1")){
				Intent intent = new Intent(context, TagInfoActivity.class);
				intent.putExtra("tagid", tag.getTagId());
				context.startActivity(intent);
				return;
				}
			}
		}

		SelectColonyDialog sDialog = new SelectColonyDialog();
		
		if(od!=null){
			sDialog.setOd(od);
		}
		sDialog.ShowDialog(context,tag);

	}
}
