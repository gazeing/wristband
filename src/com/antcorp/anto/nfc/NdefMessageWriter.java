package com.antcorp.anto.nfc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.antcorp.anto.widget.MyLog;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class NdefMessageWriter {

	Context context;
	Ndef ndef;
	NdefFormatable ndefFormatable;

	NdefMessage message;
	String packageName;

	boolean isNeedLocked; // the flag shows if user is willing to lock tags

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public NdefMessageWriter(Context context) {
		super();
		this.context = context;
	}

	public boolean isTagLocked(Intent intent){
		boolean isLocked = true;
		if (intent == null) {
			Toast.makeText(context, "No intent", Toast.LENGTH_SHORT).show();
			return isLocked;
		}

		Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		if (tag == null) {
			Toast.makeText(context, "No tag", Toast.LENGTH_SHORT).show();
			return isLocked;
		}
		
		boolean isNdef = false;

		// Get an instance of Ndef for the tag.
		String[] techList = tag.getTechList();
		for (int i = 0; i < techList.length; i++) {
			MyLog.i("Tech num " + i + ": " + techList[i]);
			if (techList[i].equals("android.nfc.tech.Ndef"))
				isNdef = true;

		}

		if (isNdef) {
			ndef = Ndef.get(tag);
			if (ndef == null) {

				Toast.makeText(context, "No tag", Toast.LENGTH_SHORT)
						.show();
				return isLocked;
			}

			if (!ndef.isWritable()) {
				Toast.makeText(context, "Read-only tag.",
						Toast.LENGTH_SHORT).show();
				return isLocked;

			}else{
				isLocked = false;
			}
			
		} 
		
		return isLocked;
	}


	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void WriteNdefTag(Intent intent, String content) {

		if (intent == null) {
			Toast.makeText(context, "No intent", Toast.LENGTH_SHORT).show();
			return;
		}

		Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

		if (tag == null) {
			Toast.makeText(context, "No tag", Toast.LENGTH_SHORT).show();
			return;
		}


		try {
			String payload = content;
			// TODO: check here

			// NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
			// "android/appdescriptor".getBytes(Charset.forName("UTF-8")),
			// new byte[0], payload.getBytes(Charset.forName("UTF-8")));
			ContentType ct = new ContentType(payload, context);

			NdefRecord appRecord = null;
			if (CheckVersion.CheckVersion14()) {
				if (packageName.length() > 0)
					appRecord = NdefRecord.createApplicationRecord("com.antcorp.anto");
				else
					appRecord = NdefRecord
							.createApplicationRecord("com.antcorp.anto");
				if (ct.isUrl()) {
					NdefRecord[] records = { createURIRecord(payload),
							appRecord };
					message = new NdefMessage(records);
				} else {
//					NdefRecord[] records = { createOtherRecord(payload),
//							appRecord };
					NdefRecord[] records =new NdefRecord[] { NdefRecord.createMime(
	                        "application/com.antcorp.anto", payload.getBytes())
	                        /**
	                         * The Android Application Record (AAR) is commented out. When a device
	                         * receives a push with an AAR in it, the application specified in the AAR
	                         * is guaranteed to run. The AAR overrides the tag dispatch system.
	                         * You can add it back in to guarantee that this
	                         * activity starts when receiving a beamed message. For now, this code
	                         * uses the tag dispatch system.
	                         */
	                         ,appRecord
	                       };

					message = new NdefMessage(records);
				}
			} else {
				if (ct.isUrl()) {
					NdefRecord[] records = { createURIRecord(payload) };
					message = new NdefMessage(records);
				} else {
					NdefRecord[] records = { createOtherRecord(payload) };
					message = new NdefMessage(records);
				}
			}

			// NdefRecord[] records = { mimeRecord };
			// NdefMessage message = new NdefMessage(records);

			boolean isNdef = false;
			boolean isNdefFormatable = false;
			// Get an instance of Ndef for the tag.
			String[] techList = tag.getTechList();
			for (int i = 0; i < techList.length; i++) {
				MyLog.i("Tech num " + i + ": " + techList[i]);
				if (techList[i].equals("android.nfc.tech.Ndef"))
					isNdef = true;
				if (techList[i].equals("android.nfc.tech.NdefFormatable"))
					isNdefFormatable = true;
			}

			if (isNdef) {
				ndef = Ndef.get(tag);
				if (ndef == null) {

					Toast.makeText(context, "No tag", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				if (!ndef.isWritable()) {
					Toast.makeText(context, "Read-only tag.",
							Toast.LENGTH_SHORT).show();
					return;

				}

				if (hasSpace(message, ndef)) {
					// Enable I/O
					ndef.connect();

					// Write the message
					ndef.writeNdefMessage(message);

					Toast.makeText(context, "TAG written", Toast.LENGTH_SHORT)
							.show();

					if (isNeedLocked) {
						if (ndef.canMakeReadOnly()) {
							AsyncLockTag alt = new AsyncLockTag();
							alt.execute(ndef);

							Toast.makeText(context, "TAG Locked",
									Toast.LENGTH_SHORT).show();
						}

					}
					// if (ndef.canMakeReadOnly())
					// showLockDialog();
//					GlobalData.m_Observer.tagWritten(); // notify the activity
														// to confirm tag is
														// written
				}

			} else {

				if (isNdefFormatable) {

					ndefFormatable = NdefFormatable.get(tag);

					if (ndefFormatable == null) {

						Toast.makeText(context, "No tag", Toast.LENGTH_SHORT)
								.show();
						return;
					}

					ndefFormatable.connect();
					if(isNeedLocked){
						 ndefFormatable.formatReadOnly(message);
							Toast.makeText(context, "TAG Locked",
									Toast.LENGTH_SHORT).show();
					}else{
					ndefFormatable.format(message);
					}
					Toast.makeText(context, "TAG written", Toast.LENGTH_SHORT)
							.show();



//					GlobalData.m_Observer.tagWritten(); // notify the activity
														// to confirm tag is
														// written
				}
			}

		} catch (Exception ex) {
			Toast.makeText(context, "Could not write TAG", Toast.LENGTH_SHORT)
					.show();
			MyLog.i(ex.getMessage());

		} finally {

			if (ndefFormatable != null) {
				try {
					ndefFormatable.close();
				} catch (IOException e) {
				}
			}

			if (ndef != null) {
				try {
					ndef.close();
				} catch (IOException e) {
				}
			}

		}

	}



	public void setNeedLocked(boolean isNeedLocked) {
		this.isNeedLocked = isNeedLocked;
	}

	private NdefRecord createOtherRecord(String text)
			throws UnsupportedEncodingException {
		// create the message in according with the standard
		String lang = "en";
		byte[] textBytes = text.getBytes();
		byte[] langBytes = lang.getBytes("US-ASCII");
		int langLength = langBytes.length;
		int textLength = textBytes.length;

		byte[] payload = new byte[1 + langLength + textLength];
		payload[0] = (byte) langLength;

		// copy langbytes and textbytes into payload
		System.arraycopy(langBytes, 0, payload, 1, langLength);
		System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

		NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
				NdefRecord.RTD_TEXT, new byte[0], payload);

		return recordNFC;
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private NdefRecord createURIRecord(String text)
			throws UnsupportedEncodingException {

		Uri uri = Uri.parse(text);
		NdefRecord recordNFC = NdefRecord.createUri(uri);
		return recordNFC;
	}

	private boolean hasSpace(NdefMessage msg, Ndef tag) {
		int maxSize = tag.getMaxSize();
		int size = msg.toByteArray().length;

		if (size > maxSize) {
			Toast.makeText(context,
					"The content is too big (" + size + "/" + maxSize + ")",
					Toast.LENGTH_SHORT).show();
			return false;
		} else {
			return true;
		}
	}
}
