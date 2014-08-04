package com.antcorp.anto.widget;

import com.antcorp.anto.MainActivity;
import com.antcorp.anto.fragment_n_adapter.MyTag;
import com.antcorp.anto.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class NoOwnerDialog {

	//AlertDialog.Builder ad ;
	Dialog alert;
	MainActivity context;
	MyTag m_tag;
	EditText ed_name;
	Button btn_menu;
//	String uid;
	
	public Dialog ShowDialog(final MainActivity baseActivity,final MyTag tag,String action){

		this.context =baseActivity;
		this.m_tag =tag;
//		this.uid = Uid;
		
		
		final Dialog addDialog = new AddTagDialog(context, R.style.AddBagDialog);
		
		//addDialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		addDialog.setContentView(R.layout.dialog_tag_nonowner);


		
		
		
		ImageView tagIcon = (ImageView) addDialog.findViewById(R.id.imageView_tagPortrait_dialog);
		if(tagIcon != null){
			int resId = R.drawable.type_wb_1;
			switch(tag.getTagType() ){
				case 1:
					resId = R.drawable.type_wb_1;
					break;
				case 2:
					resId = R.drawable.type_smart_tag;
					break;
			}
			
			tagIcon.setImageResource(resId);
		}
		
		btn_menu = (Button) addDialog.findViewById(R.id.button_menu);
		btn_menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addDialog.openOptionsMenu();
				
			}
		});
		
		ed_name = (EditText) addDialog.findViewById(R.id.editText_wbname);
		TextView tv_taginfo =(TextView)(addDialog.findViewById(R.id.textView_tagInfo));
		if (tv_taginfo!=null)
			tv_taginfo.setText(tag.getTagId());
		
		ImageButton btn_add = (ImageButton)(addDialog.findViewById(R.id.button_addToBag));
		if (btn_add!=null)
			if(action.equals("Add")){
				btn_add.setImageResource(R.drawable.add_to_bag_bt);
				btn_add.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						alert.cancel();
					
						if(ed_name.getText().toString().length()==0){
							Toast.makeText(context, ed_name.getHint().toString() + R.string.required_field, Toast.LENGTH_SHORT).show();
							return;
						}
						
						AddToBag(tag.getTagId(), ed_name.getText().toString(), 1,tag.getUid());   //change to use sevice
						
					}
				});
			}else if(action.equals("Save")){
				btn_add.setImageResource(R.drawable.ant_o_save_bt);
				ed_name.setText(tag.getTagName());

				btn_add.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						alert.cancel();
						if(ed_name.getText().toString().length()==0){
							Toast.makeText(context, ed_name.getHint().toString() + R.string.required_field, Toast.LENGTH_SHORT).show();
							return;
						}
						
						EditTagName(tag.getTagId(), ed_name.getText().toString());   //change to use sevice
						
					}
				});
			}

		
		addDialog.show();
		
		alert = addDialog;
		return addDialog;
	}
	
	

	
	protected void EditTagName(String taginfo2, String string) {
		context.changeTagName(taginfo2, string);
		
	}

	protected void AddToBag(String taginfo2, String string, int i,String uid) {
		context.AddToBag(taginfo2, string,uid);
		
	}
	
	class AddTagDialog extends Dialog{

		public AddTagDialog(Context context, int theme) {
			super(context, theme);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			menu.clear();
			MenuInflater inflater = context.getMenuInflater();
			inflater.inflate(R.menu.remove_bag_menu, menu);
			return super.onCreateOptionsMenu(menu);
		}
		
		@Override
		public boolean onMenuItemSelected(int featureId, MenuItem item) {

			return onOptionsItemSelected(item);
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.remove_bag:
				MyLog.i("case R.id.remove_bag:");
				removeFromBag();
				break;
			}
			return true;
		}

		
		
	}

	public void removeFromBag() {
		MyLog.i("context.removeFromBag(m_tag);");
		context.removeFromBag(m_tag);
		alert.cancel();;
	}
}
