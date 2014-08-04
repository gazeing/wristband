package com.antcorp.anto.data;

import com.antcorp.anto.fragment_n_adapter.Colony;

public class TagInfo {

	String antOwner;
	String belongToMe;

	Colony colony;
	DataTag dataTag;
	public TagInfo(String antOwner, String belongToMe, Colony colony,
			DataTag dataTag) {
		super();
		this.antOwner = antOwner;
		this.belongToMe = belongToMe;
		this.colony = colony;
		this.dataTag = dataTag;
	}
	public String getAntOwner() {
		return antOwner;
	}
	public String getBelongToMe() {
		return belongToMe;
	}
	public Colony getColony() {
		return colony;
	}
	public DataTag getDataTag() {
		return dataTag;
	}
	
	
	

	
	
	
}
