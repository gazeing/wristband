package com.antcorp.anto.fragment_n_adapter;

public class ChatMsg {
	String sendername;
	String time;
	String msg;
	String sender_id;
	double longitude;
	double latitude;
	public ChatMsg(String sendername, String time, String msg, String id,
			double longitude, double latitude) {
		super();
		this.sendername = sendername;
		this.time = time;
		this.msg = msg;
		this.sender_id = id;
		this.longitude = longitude;
		this.latitude = latitude;
	}
	public String getSendername() {
		return sendername;
	}
	public String getTime() {
		return time;
	}
	public String getMsg() {
		return msg;
	}
	public String getSenderId() {
		return sender_id;
	}
	public double getLongitude() {
		return longitude;
	}
	public double getLatitude() {
		return latitude;
	}

	
	


}
