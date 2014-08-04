package com.antcorp.anto.nfc;

import com.antcorp.anto.widget.MyLog;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.Build;
import android.os.Parcelable;


public class NFCResolver {

	Context context;
	
	
    public NFCResolver(Context context) {
		super();
		this.context = context;
	}
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	public String resolveIntent(Intent intent) {
		
		
		String text = null;
    	if(CheckVersion.CheckVersion11()){
    		Tag tag_g = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    		MyLog.i("^^^^^^^^^^^uid= "+ tag_g.getId()+"  =  "+ByteArrayToHexString(tag_g.getId()));
    		//TODO: build a custom nfc type;
	        String action = intent.getAction();
	        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
	                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
	                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
	            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//	            NdefMessage[] msgs;
//	            if (rawMsgs != null) {
//	                msgs = new NdefMessage[rawMsgs.length];
//	                for (int i = 0; i < rawMsgs.length; i++) {
//	                    msgs[i] = (NdefMessage) rawMsgs[i];
//	                }
//	            } else {
//	                // Unknown tag type
//	                byte[] empty = new byte[0];
//	                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
//	                Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//	                byte[] payload = dumpTagData(tag).getBytes();
//	                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
//	                NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
//	                msgs = new NdefMessage[] { msg };
//	            }
//	            // go to the tag page
//	            if (msgs == null || msgs.length == 0) {
//	                return text;
//	            }
//	            List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
//	            final int size = records.size();
////	            for (int i = 0; i < size; i++) {
////
////	                ParsedNdefRecord record = records.get(i);
////	                String text = record.getString();
////	                if ((text==null)||(text.length()==0))
////	                	return;
////	                StartBrowser sb = new StartBrowser(text,context);
////	    			sb.startBrowse();
////	            }
//	            if (size>0)
//	            {
//	            	 ParsedNdefRecord record = records.get(0);
//		             text = record.getString();
//		             if ((text==null)||(text.length()==0))
//		            	 return text;
////		             StartBrowser sb = new StartBrowser(text,context);
////		             sb.startBrowse();
//		             
//	            }
	            NdefMessage msg = (NdefMessage) rawMsgs[0];

	            text =new String(msg.getRecords()[0].getPayload());
	        }
    	}
    	return text;
    }
	
	// Converting byte[] to hex string:
	public String ByteArrayToHexString(byte [] inarray) {
	int i, j, in;
	String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
	String out= "";
	 
	for(j = 0 ; j < inarray.length ; ++j)
	{
	in = (int) inarray[j] & 0xff;
	i = (in >> 4) & 0x0f;
	out += hex[i];
	i = in & 0x0f;
	out += hex[i];
	}
	return out;
	}
    private String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	private String dumpTagData(Parcelable p) {
    	 StringBuilder sb = new StringBuilder();
    	if(CheckVersion.CheckVersion11()){
	       
	        Tag tag = (Tag) p;
	        byte[] id = tag.getId();
	        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
	        sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
	
	        String prefix = "android.nfc.tech.";
	        sb.append("Technologies: ");
	        for (String tech : tag.getTechList()) {
	            sb.append(tech.substring(prefix.length()));
	            sb.append(", ");
	        }
	        sb.delete(sb.length() - 2, sb.length());
	        for (String tech : tag.getTechList()) {
	            if (tech.equals(MifareClassic.class.getName())) {
	                sb.append('\n');
	                MifareClassic mifareTag = MifareClassic.get(tag);
	                String type = "Unknown";
	                switch (mifareTag.getType()) {
	                case MifareClassic.TYPE_CLASSIC:
	                    type = "Classic";
	                    break;
	                case MifareClassic.TYPE_PLUS:
	                    type = "Plus";
	                    break;
	                case MifareClassic.TYPE_PRO:
	                    type = "Pro";
	                    break;
	                }
	                sb.append("Mifare Classic type: ");
	                sb.append(type);
	                sb.append('\n');
	
	                sb.append("Mifare size: ");
	                sb.append(mifareTag.getSize() + " bytes");
	                sb.append('\n');
	
	                sb.append("Mifare sectors: ");
	                sb.append(mifareTag.getSectorCount());
	                sb.append('\n');
	
	                sb.append("Mifare blocks: ");
	                sb.append(mifareTag.getBlockCount());
	            }
	
	            if (tech.equals(MifareUltralight.class.getName())) {
	                sb.append('\n');
	                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
	                String type = "Unknown";
	                switch (mifareUlTag.getType()) {
	                case MifareUltralight.TYPE_ULTRALIGHT:
	                    type = "Ultralight";
	                    break;
	                case MifareUltralight.TYPE_ULTRALIGHT_C:
	                    type = "Ultralight C";
	                    break;
	                }
	                sb.append("Mifare Ultralight type: ");
	                sb.append(type);
	            }
	        }
     	}

        return sb.toString();
    }
}
