package com.antcorp.anto.data;

public class DataTag {
String createAt;
String tagName;

String tagid;
int tagType;
int tagLocked;
int tagSize;
String nfcUid;
String tagLote;

public DataTag(String createAt, String tagName, String tagid, int tagType,
		int tagLocked, String tag_lote,String nfc_uid,int tag_size) {
	super();
	this.createAt = createAt;
	this.tagName = tagName;
	this.tagid = tagid;
	this.tagType = tagType;
	this.tagLocked = tagLocked;
	this.tagSize = tag_size;
	this.nfcUid = nfc_uid;
	this.tagLote =tag_lote;

}
public String getCreateAt() {
	return createAt;
}
public String getTagName() {
	return tagName;
}
public String getTagid() {
	return tagid;
}
public int getTagType() {
	return tagType;
}
public int getTagLocked() {
	return tagLocked;
}
public int getTagSize() {
	return tagSize;
}
public String getNfcUid() {
	return nfcUid;
}
public String getTagLote() {
	return tagLote;
}








}
