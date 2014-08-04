package com.antcorp.anto;

import com.antcorp.anto.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

@SuppressLint("SimpleDateFormat")
public class EditTagActivity extends EditTagActivityBase {

	EditText et_name,et_surname;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_tag);
		init();
		
	}

	@Override
	protected void findWidgets() {
		
		super.findWidgets();
		
		et_name = (EditText) findViewById(R.id.editText_name);
		et_surname = (EditText) findViewById(R.id.editText_surname);
		
		et_name.setText(name);
		et_surname.setText(surname);
		
		// try to hide soft keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et_name.getWindowToken(), 0);
	}

	@Override
	protected String getSurName() {
		
		return et_surname.getText().toString();
	}

	@Override
	protected String getName() {
		
		return et_name.getText().toString();
	}
}
