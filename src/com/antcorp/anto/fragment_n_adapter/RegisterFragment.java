package com.antcorp.anto.fragment_n_adapter;

import com.antcorp.anto.LoginBaseActivity;
import com.antcorp.anto.MainActivity;
import com.antcorp.anto.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class RegisterFragment extends Fragment {

	private RelativeLayout ll;
	private FragmentActivity fa;
	
	EditText etName,etEmail,etPwd,etRepeatPwd,etSurname;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 0) {
				fa.startActivity(new Intent(fa,MainActivity.class));
				fa.finish();
			}
		}

	};
	
	LoginBaseActivity mBaseActivity = null;

	public void SetActivityProvider(LoginBaseActivity pBaseActivity)
	{
		mBaseActivity = pBaseActivity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		fa = super.getActivity();

		ll = (RelativeLayout) inflater.inflate(R.layout.fragment_register,
				container, false);
		init();
		

		return ll;
	}

	private void init() 
	{
		etName  = (EditText) ll.findViewById(R.id.editText_name);
		etEmail =  (EditText) ll.findViewById(R.id.editText_email);
		etPwd =  (EditText) ll.findViewById(R.id.editText_pwd);
//		etRepeatPwd =  (EditText) ll.findViewById(R.id.editText_pwdRepeat);
		etSurname = (EditText) ll.findViewById(R.id.editText_surname);
		
		Button btn = (Button) ll.findViewById(R.id.button_register);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				if(!etPwd.getText().toString().equals(etRepeatPwd.getText().toString())){
//					etPwd.setText("");
//					etRepeatPwd.setText("");
//					Toast.makeText(fa, R.string.invalid_password, Toast.LENGTH_LONG).show();
//					return;
//				}
				
				if(!isValidEmail(etEmail.getText().toString())){
					Toast.makeText(fa, R.string.invalid_email, Toast.LENGTH_LONG).show();
					return;
				}
				
				if(checkEditText(etName)&&checkEditText(etEmail)&&checkEditText(etPwd)&&checkPwdDigits(etPwd)){
					if (mBaseActivity != null)
						mBaseActivity.Registration(etName.getText().toString(), etSurname.getText().toString(), etEmail.getText().toString(), etPwd.getText().toString());
					
						mBaseActivity.storeUserInfo(etEmail.getText().toString(),etPwd.getText().toString());
				}
			}


		});
		
	}
	protected boolean checkPwdDigits(EditText etPwd2) {
		if (etPwd2.getText().toString().length()>7)
			return true;
		else{
			Toast.makeText(fa, R.string.password_min_size, Toast.LENGTH_LONG).show();
			return false;
		}
	}

	private boolean checkEditText(EditText et) {
		if (et.getText().toString().length()>0)
			return true;
		else{
			Toast.makeText(fa, et.getHint().toString() + R.string.required_field, Toast.LENGTH_LONG).show();
			return false;
		}
		
	}
	
	public final static boolean isValidEmail(CharSequence target) {
	    if (target == null) {
	        return false;
	    } else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	}

}
