package com.antcorp.anto.fragment_n_adapter;

public class Colony {
	String name;
	String surname;
	String contactName;
	String contactSurName;
	String contactPhone1;

	String antOwnerId;
	String info;
	String img;

	String colony_member_id;
	String createdAt;
	String updatedAt;
	String is_member_owner;
	public Colony(String name, String surname, String contactName,
			String contactSurName, String contactPhone1, String antOwnerId,
			String info, String img, String colony_member_id, String createdAt,
			String updatedAt,String is_member_owner) {
		super();
		this.name = name;
		this.surname = surname;
		this.contactName = contactName;
		this.contactSurName = contactSurName;
		this.contactPhone1 = contactPhone1;
		this.antOwnerId = antOwnerId;
		this.info = info;
		this.img = img;
		this.colony_member_id = colony_member_id;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.is_member_owner = is_member_owner;
	}
	public String getName() {
		return name;
	}
	public String getSurname() {
		return surname;
	}
	public String getContactName() {
		return contactName;
	}
	public String getContactSurName() {
		return contactSurName;
	}
	public String getContactPhone1() {
		return contactPhone1;
	}
	public String getAntOwnerId() {
		return antOwnerId;
	}
	public String getInfo() {
		return info;
	}
	public String getImg() {
		return img;
	}
	public String getColony_member_id() {
		return colony_member_id;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public String getIs_member_owner() {
		return is_member_owner;
	}
	
	
	
	
}
