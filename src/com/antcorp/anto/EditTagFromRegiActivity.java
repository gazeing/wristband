package com.antcorp.anto;

import com.antcorp.anto.R;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class EditTagFromRegiActivity extends EditTagActivityBase{
	
	TextView tv_name, tv_surname;
	
	String msName = "", msSurname = "";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_edit_tag_from_registration);
		init();
		if(btn_menu!=null){
			btn_menu.setClickable(false);
		}
		
	}

	@Override
	protected void findWidgets() {
		tv_name = (TextView) findViewById(R.id.editText_name);
//		tv_surname = (TextView) findViewById(R.id.editText_surname);
		et_contactName = (EditText) findViewById(R.id.editText_contactname);
		et_contactSurname = (EditText) findViewById(R.id.editText_contactSurname);
		et_contactNum = (EditText) findViewById(R.id.editText_contactNum);
		et_additionalInfo = (EditText) findViewById(R.id.editText_addtion_info);
		
		// try to hide soft keyboard
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et_contactName.getWindowToken(), 0);
		
		msName = name;
		msSurname = surname;
		
		tv_name.setText(name + " " + surname);
//		tv_surname.setText(surname);
	}

	// /create optional menu here
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}
	
	@Override
	protected String getSurName() {
	
		return msSurname;
	}

	
	@Override
	protected String getName() {
		
		return msName;
	}
	
	
}
