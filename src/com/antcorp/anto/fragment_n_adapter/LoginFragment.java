package com.antcorp.anto.fragment_n_adapter;

import com.antcorp.anto.LoginBaseActivity;
import com.antcorp.anto.R;
import com.antcorp.anto.widget.ForgetPwDialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class LoginFragment extends Fragment {

	private RelativeLayout ll;
	private FragmentActivity fa;
	EditText etEmail, etPwd;
	CheckBox cb;
	
	LoginBaseActivity mBaseActivity = null;
	
	Button mbtLogin = null,mbtMenu;


	public void SetActivityProvider(LoginBaseActivity pBaseActivity)
	{
		mBaseActivity = pBaseActivity;
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		fa = super.getActivity();
		ll = (RelativeLayout) inflater.inflate(R.layout.fragment_login,container, false);
		
		init();

		return ll;
	}
	
	
///create optional menu here
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) 
	{
	   // inflater=fa.getMenuInflater();
	    inflater.inflate(R.menu.login_menu, menu);

		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId())
	    {
	    case R.id.forget:
	    	forgetPassword();
	        break;

	    }
	    return true;
	}
	

	private void forgetPassword() {
//		((LoginBaseActivity)fa).forgetPassword();
		ForgetPwDialog  fp = new ForgetPwDialog();
		fp.ShowDialog((LoginBaseActivity) fa);
		
	}


	private void init() 
	{
		etEmail = (EditText) ll.findViewById(R.id.editText2);
		InputMethodManager imm = (InputMethodManager) fa.getSystemService(Context.INPUT_METHOD_SERVICE);
		
		imm.hideSoftInputFromWindow(etEmail.getWindowToken(), 0);

		etPwd = (EditText) ll.findViewById(R.id.editText1);
		
		cb = (CheckBox) ll.findViewById(R.id.checkBox1);
		cb.setChecked(true);
		

		mbtLogin = (Button) ll.findViewById(R.id.button_login);
		mbtLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				if (mBaseActivity != null)
					mBaseActivity.Login(etEmail.getText().toString(), etPwd.getText().toString());
				
				//if "remember me" is checked, store the email and password, else clean the data
				if(cb.isChecked()){
					
					mBaseActivity.storeUserInfo(etEmail.getText().toString(),etPwd.getText().toString());

				}else{
					mBaseActivity.cleanUserInfo();
				}
					
			}
		});
		
		setHasOptionsMenu(true);
		mbtMenu = (Button) ll.findViewById(R.id.button_menu);
		mbtMenu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fa.openOptionsMenu();
				
			}
		});
		
	}
}
