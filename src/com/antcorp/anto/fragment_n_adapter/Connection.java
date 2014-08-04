package com.antcorp.anto.fragment_n_adapter;

public class Connection {

	String connectionId;
	String colony_member_id;
	String current_active;
	String tag_id;
	String createdAt;
	String updatedAt;
	public Connection(String connectionId, String colony_member_id,
			String current_active, String tag_id, String createdAt,
			String updatedAt) {
		super();
		this.connectionId = connectionId;
		this.colony_member_id = colony_member_id;
		this.current_active = current_active;
		this.tag_id = tag_id;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	public String getConnectionId() {
		return connectionId;
	}
	public String getColony_member_id() {
		return colony_member_id;
	}
	public String getCurrent_active() {
		return current_active;
	}
	public String getTag_id() {
		return tag_id;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	
	
	
	
	

	
}
