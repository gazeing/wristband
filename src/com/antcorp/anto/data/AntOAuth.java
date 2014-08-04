package com.antcorp.anto.data;

public class AntOAuth {

	public String username = "";
	public String password = "";
	
	public boolean hasValidAuth()
	{
		return username.length()+password.length()>0;
	}
}
