package com.antcorp.anto.fragment_n_adapter;

import java.util.ArrayList;

public class Notification {
	String owner_name;
	String owner_surname;
	String sender_name;
	String sender_surname;
	String about_name;
	String about_surname;
	String noti_status;
	
	//boolean isOpen;
	//String msg;
	String img;
	double lat;
	double lon;
	String notificationId;
	String tagid;
	String lastupdated;
	//isFromMe: it's true if i sent the notification to others
	boolean isFromMe;
	
	ArrayList<ChatMsg> chat_list;
	
	int msg_count_sender;
	int msg_count_owner;
	int msg_max;
	
	String uid;
	
	public Notification(String owner_name, String owner_surname,
			String sender_name, String sender_surname, String about_name,
			String about_surname, String noti_status, String img, double lat,
			double lon, String notificationId, String tagid,
			String lastupdated, ArrayList<ChatMsg> chat_list,
			int msg_count_sender, int msg_count_owner, int msg_max,boolean isFromMe,String uid) {
		super();
		this.owner_name = owner_name;
		this.owner_surname = owner_surname;
		this.sender_name = sender_name;
		this.sender_surname = sender_surname;
		this.about_name = about_name;
		this.about_surname = about_surname;
		this.noti_status = noti_status;
		this.img = img;
		this.lat = lat;
		this.lon = lon;
		this.notificationId = notificationId;
		this.tagid = tagid;
		this.lastupdated = lastupdated;
		this.chat_list = chat_list;
		this.msg_count_sender = msg_count_sender;
		this.msg_count_owner = msg_count_owner;
		this.msg_max = msg_max;
		this.isFromMe=isFromMe;
		this.uid = uid;
		
	}
	public String getOwner_name() {
		return owner_name;
	}
	public String getOwner_surname() {
		return owner_surname;
	}
	public String getSender_name() {
		return sender_name;
	}
	public String getSender_surname() {
		return sender_surname;
	}

	public String getAbout_name() {
		return about_name;
	}
	public String getAbout_surname() {
		return about_surname;
	}
	public String getNoti_status() {
		return noti_status;
	}
	public String getImg() {
		return img;
	}
	public double getLat() {
		return lat;
	}
	public double getLon() {
		return lon;
	}
	public String getNotificationId() {
		return notificationId;
	}
	public String getTagid() {
		return tagid;
	}
	public String getLastupdated() {
		return lastupdated;
	}
	public ArrayList<ChatMsg> getChat_list() {
		return chat_list;
	}
	public int getMsg_count_sender() {
		return msg_count_sender;
	}
	public int getMsg_count_owner() {
		return msg_count_owner;
	}
	public int getMsg_max() {
		return msg_max;
	}
	public boolean isFromMe() {
		return isFromMe;
	}
	public String getUid() {
		return uid;
	}
	

	



	
	

}
