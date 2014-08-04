package com.antcorp.anto.widget;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.network.AntRetStatus;
import com.antcorp.anto.network.DataWebAntCorp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

@SuppressLint("SimpleDateFormat")
public class UtilStatics {

	public static DataWebAntCorp AntCorpInfo(String pInfoWeb) {
		DataWebAntCorp lRet = new DataWebAntCorp();
		
		if(pInfoWeb.startsWith("<!DOCTYPE html>")){
			lRet.miRetCode=AntRetStatus.SERVER_ERROR;
			return lRet;
		}

		String json = ParseJsonData(pInfoWeb);

		// yLog.i("ParseJsonData = "+json );
		if ((json == null) || (json.length() == 0))
			return lRet;
		int isCode = AntRetStatus.INTERNAL_FAILURE;
		String data = "";
		try {
			JSONObject j = new JSONObject(json);
			isCode = j.getInt("AntO");
			if (!j.isNull("AntOData"))
				data = j.getString("AntOData");
		} catch (JSONException e) {
			MyLog.i("json error: " + e.getMessage());
		}
		lRet.miRetCode = isCode;
		lRet.msDataJson = data;

		// String lsHeader = ParseData (pInfoWeb, "head");
		String lsMetaContent = ParseDataContent(pInfoWeb, "meta content");

		// Token
		if (lsMetaContent.length() > 0
				&& lsMetaContent.indexOf("csrf-token") <= 0) {
			int liExit = 0;

			while (pInfoWeb.length() > 0 || liExit > 10) {
				pInfoWeb = pInfoWeb.replace("<meta content=" + lsMetaContent
						+ "/>", "");
				lsMetaContent = ParseDataContent(pInfoWeb, "meta content");

				if (lsMetaContent.indexOf("csrf-token") > 0)
					break;

				liExit++;
			}
		}

		if (lsMetaContent.indexOf("csrf-token") > 0) {
			lsMetaContent = lsMetaContent.replace(" name=\"csrf-token\" ", "");
			lsMetaContent = lsMetaContent.replace("\"", "").trim();

			lRet.msToken = lsMetaContent;

			// MyLog.i("	lRet.msToken = "+lRet.msToken );
		}

		GlobalData.m_data = lRet;
		return lRet;
	}

	public static boolean checkKeyNotNull(String jsonString, String key) {
		if (jsonString != null) {
			try {
				JSONObject json = new JSONObject(jsonString);
				if (json.has(key)) {
					return !((json.isNull(key)) || json.getString(key).equals(
							"null"));
				}
			} catch (JSONException e) {
				MyLog.i(e);
			}
		}
		return false;
	}

	public static int getColorByStatus(int tagStatus) {
		int color;
		switch (tagStatus) {
		case 0:
			color = (Color.BLUE);
			break;
		case 1:
			color = (Color.RED);
			break;
		case 2:
			color = (Color.GREEN);
			break;
		case 3:
			color = (Color.YELLOW);
			break;
		default:
			color = (Color.BLACK);
		}

		return color;
	}

	public static long getLongFromServerTimeFormat(String format) {
		long qu = 0;
		if (format != null) {
			try {

//				SimpleDateFormat sdf = new SimpleDateFormat(
//						"yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//				if (format.length() <= 25)
				format = format.substring(0,format.lastIndexOf(":"))+"+0000";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
				sdf.setTimeZone(TimeZone.getDefault());
				qu = sdf.parse(format).getTime();

			} catch (ParseException e) {
				MyLog.i(e.getMessage());
			}
		}

		return qu;
	}

	public static String getNameAndIntialSurname(String name, String surname) {
		if ((surname == null) || (surname.length() == 0))
			return name;
		else
			return name + " " + surname.charAt(0);
	}

//	public static String getWebServerPrefix() {
//		String pre = null;
//		pre = (MyLog.isLogging) ? "http://192.168.15.100"
//				: "http://www.identibank.com";
//		pre = "http://www.identibank.com";
//		return pre;
//
//	}

	public static Boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public static Intent newEmailIntent(Context context, String address,
			String subject, String body, String cc) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
		intent.putExtra(Intent.EXTRA_TEXT, body);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_CC, cc);
		intent.setType("message/rfc822");
		return intent;
	}

	// Those are side funcs to help extract the data
	static String ParseData(String pdData, String psTag) {
		if ((pdData != null) && (psTag != null)) {
			int liIni = 0, liEnd = 0;

			liIni = pdData.indexOf("<" + psTag + ">")
					+ ("<" + psTag + ">").length();
			liEnd = pdData.indexOf("</" + psTag + ">");

			if (liEnd > 0 && liEnd > liIni)
				return pdData.substring(liIni, liEnd);
		}

		return "";
	}

	static String ParseDataContent(String pdData, String psTag) {
		int liIni = 0, liEnd = 0;

		liIni = pdData.indexOf("<" + psTag + "=")
				+ ("<" + psTag + "=").length();
		liEnd = pdData.indexOf("/>");

		if (liEnd > 0 && liEnd > liIni)
			return pdData.substring(liIni, liEnd);

		return "";
	}

	static String ParseJsonData(String pdData) {
		if ((pdData != null) && (pdData.length() > 18)) {
			int liIni = 0, liEnd = 0;

			String start = "<!--[CDATA[ [";
			String end = "] ]]-->";

			liIni = pdData.indexOf(start) + start.length();
			liEnd = pdData.indexOf(end);

			if (liEnd > 0 && liEnd > liIni)
				return pdData.substring(liIni, liEnd);
		}

		return "";
	}

	public static void showNetworkAlert(Context context, int icon_id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("Please check your internet connection.")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});

		builder.setIcon(icon_id);
		builder.setTitle("Connection Problem");
		builder.show();

	}

	public static String convertStringToMd5(String s) {

		// Create MD5 Hash
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			byte[] array = md.digest(s.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			MyLog.i(e);
		}
		return "";
	}
	


	public static Bitmap transferImageStringToBitmap(String img, int scalePixel) {
		if (img != null) {
			if (img.length() > 0) {
				byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(
						decodedString, 0, decodedString.length);

				if ((decodedByte != null)) {
					decodedByte = Bitmap.createScaledBitmap(decodedByte,
							scalePixel, scalePixel, true);

					return decodedByte;
				}
			}
		}
		return null;
	}

//	public static String TransferServerFormatToDisplay(String format) {
//		return TransferTimeFormat(getLongFromServerTimeFormat(format));
//	}
//
//	public static String TransferServerTimeFormat(long time) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//		String date = sdf.format(new Date(time));
//		// date = date.substring(0, date.length() - 2) + ":"
//		// + date.substring(date.length() - 2);
//		return date + 'Z';
//
//	}

	public static String TransferTimeFormat(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("E dd, MMM/yyyy - HH:mm aa");
		sdf.setTimeZone(TimeZone.getDefault());
		return sdf.format(new Date(time));
	}
	

//	public static long getLongFromTimeFormat(String format) {
//		long qu = 0;
//		if (format != null) {
//			try {
//				SimpleDateFormat sdf = new SimpleDateFormat(
//						"E dd, MMM/yyyy - HH:mm aa");
//				qu = sdf.parse(format).getTime();
//
//			} catch (ParseException e) {
//				MyLog.i(e.getMessage());
//			}
//		}
//
//		return qu;
//	}

	public static String TransferTimeFormatToDate(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("E dd, MMM/yyyy");
		sdf.setTimeZone(TimeZone.getDefault());
		return sdf.format(new Date(time));
	}
	public static String TransferTimeFormatToTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aa");
		sdf.setTimeZone(TimeZone.getDefault());
		return sdf.format(new Date(time));
	}

}
