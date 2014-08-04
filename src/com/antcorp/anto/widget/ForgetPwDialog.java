package com.antcorp.anto.widget;

import com.antcorp.anto.LoginBaseActivity;
import com.antcorp.anto.R;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

public class ForgetPwDialog {
	LoginBaseActivity activity;

	Dialog alert;
	EditText et;
	
	public Dialog ShowDialog(final LoginBaseActivity activity){
		this.activity = activity;
		
		final Dialog addDialog = new Dialog(activity, R.style.AddBagDialog);
		addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		addDialog.setContentView(R.layout.dialog_forget_pw);
		
		et = (EditText) addDialog.findViewById(R.id.editText1);
		ImageButton bt = (ImageButton) addDialog.findViewById(R.id.button1);
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String email = et.getText().toString();
				if (email.length()>0){
					activity.forgetPassword(email);
					addDialog.dismiss();
				}
				
				
			}
		});
		
		addDialog.show();
		
		alert = addDialog;
		return addDialog;
	}

}
