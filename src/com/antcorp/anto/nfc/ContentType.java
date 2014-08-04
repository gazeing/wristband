package com.antcorp.anto.nfc;



import android.annotation.SuppressLint;
import android.content.Context;

@SuppressLint("DefaultLocale")
public class ContentType {
	public static final int PLAINTEXT = 0;
	public static final int URL = 1;
	public static final int VCARD = 2;
	public static final int MAILTO = 3;

	String content;
	Context context;

	public ContentType(String content, Context context) {
		super();
		this.content = content;
		this.context = context;
	}

//	public String getFavoriteMenuName(int id) {
//		String[] menu = returnFavoriteMenuArray();
//		if (id < menu.length)
//			return menu[id];
//		else
//			return null;
//	}
//
//	public String getHistoryMenuName(int id) {
//		String[] menu = returnHistoryMenuArray();
//		if (id < menu.length)
//			return menu[id];
//		else
//			return null;
//	}
//
//	public String getMenuName(int id, boolean isOnline) {
//		String[] menu = returnMenuArray(isOnline);
//		if (id < menu.length)
//			return menu[id];
//		else
//			return null;
//	}

//	public String getTitle() {
//		String title = "";
//		if (isVcard()) {
//			VcardParser vp = new VcardParser(content);
//			title = "CONTACT: "
//					+ (vp.getNames() == null ? vp.getFullname() : vp.getNames());
//			return title;
//		} else if (isMailTo()) {
//			title = "Email";
//			return title;
//		}
//
//		else if (isUrl()) {
//			title = "URL";
//			return title;
//		}
//
//		else {
//			title = "PLAINTEXT";
//		}
//		return title;
//
//	}

	public int identifyContent() {
		if (isVcard())
			return VCARD;
		else if (isMailTo())
			return MAILTO;
		else if (isUrl())
			return URL;

		else
			return PLAINTEXT;
	}

	public boolean isMailTo() {
		String contentLow = content.toLowerCase();
		return (contentLow.startsWith("mailto:"));
	}

	public boolean isUrl() {
		String contentLow = content.toLowerCase();
		if(contentLow.startsWith("mailto:"))
			return false;
		if(contentLow.contains("@"))
			return false;
		return (contentLow.startsWith("http://")
				|| contentLow.startsWith("https://")
				|| contentLow.startsWith("www.") || contentLow.contains("."));
	}

	public boolean isVcard() {
		String contentLow = content.toLowerCase();
		return (contentLow.startsWith("begin:vcard"));
	}

//	public String[] returnFavoriteMenuArray() {
//		switch (identifyContent()) {
//		case ContentType.PLAINTEXT: {
//			String[] choices = { context.getString(R.string.scan_detail),
//					context.getString(R.string.feedback),
//					context.getString(R.string.send_out),
//					context.getString(R.string.write_nfc),
//					context.getString(R.string.delete_item) };
//
//			return choices;
//		}
//
//		case ContentType.URL: {
//			String[] choices = { context.getString(R.string.scan_detail),
//					context.getString(R.string.feedback),
//					context.getString(R.string.send_out),
//					context.getString(R.string.open),
//					context.getString(R.string.write_nfc),
//					context.getString(R.string.delete_item) };
//
//			return choices;
//		}
//
//		case ContentType.VCARD: {
//			String[] choices = { context.getString(R.string.scan_detail),
//					context.getString(R.string.feedback),
//					context.getString(R.string.send_out),
//					context.getString(R.string.add_to_contact),
//					context.getString(R.string.write_nfc),
//					context.getString(R.string.delete_item) };
//
//			return choices;
//		}
//		case ContentType.MAILTO: {
//			String[] choices = { context.getString(R.string.scan_detail),
//					context.getString(R.string.set_reminder),
//					context.getString(R.string.feedback),
//					context.getString(R.string.send_out),
//					context.getString(R.string.send_email),
//					context.getString(R.string.write_nfc),
//					context.getString(R.string.delete_item) };
//
//			return choices;
//		}
//
//		}
//		return null;
//	}
//
//	public String[] returnHistoryMenuArray() {
//		switch (identifyContent()) {
//		case ContentType.PLAINTEXT: {
//			String[] choices = { context.getString(R.string.scan_detail),
//					context.getString(R.string.set_reminder),
//					context.getString(R.string.feedback),
//					context.getString(R.string.send_out),
//					context.getString(R.string.add_to_favor),
//					context.getString(R.string.write_nfc),
//					context.getString(R.string.delete_item) };
//
//			return choices;
//		}
//
//		case ContentType.URL: {
//			String[] choices = { context.getString(R.string.scan_detail),
//					context.getString(R.string.set_reminder),
//					context.getString(R.string.feedback),
//					context.getString(R.string.send_out),
//					context.getString(R.string.open),
//					context.getString(R.string.add_to_favor),
//					context.getString(R.string.write_nfc),
//					context.getString(R.string.delete_item) };
//
//			return choices;
//		}
//
//		case ContentType.VCARD: {
//			String[] choices = { context.getString(R.string.scan_detail),
//					context.getString(R.string.set_reminder),
//					context.getString(R.string.feedback),
//					context.getString(R.string.send_out),
//					context.getString(R.string.add_to_contact),
//					context.getString(R.string.add_to_favor),
//					context.getString(R.string.write_nfc),
//					context.getString(R.string.delete_item) };
//
//			return choices;
//		}
//		case ContentType.MAILTO: {
//			String[] choices = { context.getString(R.string.scan_detail),
//					context.getString(R.string.set_reminder),
//					context.getString(R.string.feedback),
//					context.getString(R.string.send_out),
//					context.getString(R.string.send_email),
//					context.getString(R.string.add_to_favor),
//					context.getString(R.string.write_nfc),
//					context.getString(R.string.delete_item) };
//
//			return choices;
//		}
//
//		}
//		return null;
//	}
//
//	public String[] returnMenuArray(boolean isOnline) {
//
//		switch (identifyContent()) {
//		case ContentType.PLAINTEXT: {
//			String[] choicesOnline = {
//
//			context.getString(R.string.scan_detail),
//					context.getString(R.string.set_reminder),
//					context.getString(R.string.feedback),
//					context.getString(R.string.send_out),
//					context.getString(R.string.add_to_favor) };
//			String[] choicesOffline = {
//					context.getString(R.string.scan_detail),
//					context.getString(R.string.set_reminder),
//					context.getString(R.string.send_out)
//
//			};
//			return isOnline ? choicesOnline : choicesOffline;
//		}
//
//		case ContentType.URL: {
//			String[] choicesOnline = { context.getString(R.string.scan_detail),
//					context.getString(R.string.set_reminder),
//					context.getString(R.string.feedback),
//					context.getString(R.string.send_out),
//					context.getString(R.string.open),
//					context.getString(R.string.add_to_favor) };
//			String[] choicesOffline = {
//
//			context.getString(R.string.set_reminder),
//					context.getString(R.string.scan_detail),
//					context.getString(R.string.send_out),
//					context.getString(R.string.open) };
//			return isOnline ? choicesOnline : choicesOffline;
//		}
//
//		case ContentType.VCARD: {
//			String[] choicesOnline = {
//
//			context.getString(R.string.scan_detail),
//					context.getString(R.string.set_reminder),
//					context.getString(R.string.feedback),
//					context.getString(R.string.send_out),
//					context.getString(R.string.add_to_contact),
//					context.getString(R.string.add_to_favor) };
//			String[] choicesOffline = {
//
//			context.getString(R.string.scan_detail),
//					context.getString(R.string.set_reminder),
//					context.getString(R.string.send_out),
//					context.getString(R.string.add_to_contact) };
//			return isOnline ? choicesOnline : choicesOffline;
//		}
//		case ContentType.MAILTO: {
//			String[] choicesOnline = {
//
//			context.getString(R.string.scan_detail),
//					context.getString(R.string.set_reminder),
//					context.getString(R.string.feedback),
//					context.getString(R.string.send_out),
//					context.getString(R.string.send_email),
//					context.getString(R.string.add_to_favor) };
//			String[] choicesOffline = {
//
//			context.getString(R.string.scan_detail),
//					context.getString(R.string.set_reminder),
//					context.getString(R.string.send_out),
//					context.getString(R.string.send_email), };
//			return isOnline ? choicesOnline : choicesOffline;
//		}
//
//		}
//		return null;
//	}

}
