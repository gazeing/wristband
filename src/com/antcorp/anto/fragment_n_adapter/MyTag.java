package com.antcorp.anto.fragment_n_adapter;

public class MyTag 
{
	String 	tagName;
	String	tagId;
	int 	tagType;
	String 	tagImageAddress;
	boolean hasConnection;
	String uid;
	
	public MyTag(String tagName, String tagValue, int tagType, String tagImageAddress, boolean hasConnection,String uid) 
	{
		super();
		
		this.tagName 			= tagName;
		this.tagId 				= tagValue;
		this.tagType 			= tagType;
		this.tagImageAddress 	= tagImageAddress;
		this.hasConnection 		= hasConnection;
		this.uid = uid;
	}

	public String getTagName() {
		return tagName;
	}

	public String getTagId() {
		return tagId;
	}

	public int getTagType() {
		return tagType;
	}

	public String getTagImageAddress() {
		return tagImageAddress;
	}

	public boolean isHasConnection() {
		return hasConnection;
	}

	public String getUid() {
		return uid;
	}
	
	
}
