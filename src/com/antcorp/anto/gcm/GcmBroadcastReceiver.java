/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.antcorp.anto.gcm;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.fragment_n_adapter.ChatMsg;
import com.antcorp.anto.service.ReminderNotification;
import com.antcorp.anto.widget.MyLog;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * This {@code WakefulBroadcastReceiver} takes care of creating and managing a
 * partial wake lock for your app. It passes off the work of processing the GCM
 * message to an {@code IntentService}, while ensuring that the device does not
 * go back to sleep in the transition. The {@code IntentService} calls
 * {@code GcmBroadcastReceiver.completeWakefulIntent()} when it is ready to
 * release the wake lock.
 */

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

	Context context;
    public static final String TAG = "GCM Receiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// Explicitly specify that GcmIntentService will handle the intent.
		MyLog.i("************************************************");
		this.context = context;
		Bundle extras = intent.getExtras();
		Log.i(TAG,"Received: " + extras.toString());
		dealWithNotification(context, extras);

		setResultCode(Activity.RESULT_OK);

	}

	private void dealWithNotification(Context context, Bundle extras) {
		if (extras.getString("action") != null) {
			String action = extras.getString("action");
			if (action.equals("NOTI_TYPE_CHAT")) {
				String lsData = extras.getString("data");

				String chatid = "";
				String tagid = "";
				try {
					JSONObject jbase = new JSONObject(lsData);
					chatid = jbase.getString("chat_id");
					tagid = jbase.getString("tag_id");
				} catch (JSONException e) {
					MyLog.i(e);
				}

				if (GlobalData.m_service != null) {
					Log.i(TAG,"GetNotifyListOpen(): " );
					GlobalData.m_service.GetNotifyListOpen();
					GlobalData.m_service.GetChatList(tagid, chatid);
				}
				else
					Log.i(TAG,"xxxxxxxxxxxxxxxxxxxxxxxxx     GlobalData.m_service == null(): " );
				showChatNotification(lsData);

			} else if (action.equals("NOTI_TYPE_NEW")) {
				String lsData = extras.getString("data");
				showNewNotification(lsData);

				if (GlobalData.m_service != null){
					Log.i(TAG,"GetNotifyListOpen(): " );
					GlobalData.m_service.GetNotifyListOpen();
				}
				else
					Log.i(TAG,"xxxxxxxxxxxxxxxxxxxxxxxxx     GlobalData.m_service == null(): " );

			}

		}

	}

	private void showNewNotification(String lsData) {
		String  notiId="";
		try {
			JSONObject jbase = new JSONObject(lsData);

//			sender_name = jbase.getString("sender_name");
//			sender_surname = jbase.getString("sender_surname");
//			time = jbase.getString("updated_at");
			
			notiId = jbase.getString("chat_id");
			
//			long realtime = UtilStatics.getLongFromServerTimeFormat(time);
//			time = UtilStatics.TransferTimeFormat(realtime);

		} catch (JSONException e) {
			MyLog.i(e);
		}
		int id = 0;
//		ReminderNotification.setNotification(id ,time, sender_name + " " + sender_surname + " just notify you.", context,notiId);
		
		ReminderNotification.setNotification(id ,"Click here to view the details","Someone notified you", context, notiId);

	}

	@SuppressWarnings("unused")
	private void showChatNotification(String lsData) {
//		String sender_name = "";
//		String owner_name = "";
//		String sender_surname = "";
//		String owner_surname = "";
		String chatid = "";
		String tagid = "";
//		String senderId = "";

		ArrayList<ChatMsg> chat_list = new ArrayList<ChatMsg>();

		try {
			JSONObject jbase = new JSONObject(lsData);
			chatid = jbase.getString("chat_id");
			tagid = jbase.getString("tag_id");
//			JSONObject j = jbase.getJSONObject("chat_message");
//
//			sender_name = j.getString("sender_name");
//
//			sender_surname = j.getString("sender_surname");
//			owner_surname = j.getString("owner_surname");
//			owner_name = j.getString("owner_name");
//			senderId = j.getString("sender_id");
//
//			GlobalData.m_current_chatMsgs.clear();
//			JSONArray jArray = j.getJSONArray("messages");
//
//			for (int i = 0; i < jArray.length(); i++) {
//				JSONObject json = jArray.getJSONObject(i);
//				String id = json.getString("sender_message_id");
//				String time = json.getString("created_at");
//
//				long realtime = UtilStatics.getLongFromServerTimeFormat(time);
//				time = UtilStatics.TransferTimeFormat(realtime);
//				String message = json.getString("message");
//				double latitude = json.getDouble("latitude");
//				double longitude = json.getDouble("longitude");
//
//				String name = sender_name;
//				if ((sender_name != null) && (owner_name != null)
//						&& (senderId != null)&&(id!=null)) {
//					if (id.equals(senderId))
//						name = sender_name;
//					else
//						name = owner_name;
//				}
//
//				ChatMsg chatMsg = new ChatMsg(name, time, message, id,
//						longitude, latitude);
//
//				GlobalData.m_current_chatMsgs.add(chatMsg);
//
//				if (!id.equals(GlobalData.m_antOUser.id))
//					chat_list.add(chatMsg);
//				// Trigger next timer run?
//			}

		} catch (JSONException e) {
			MyLog.i(e);
		}
		int id = chatid.hashCode();
		ReminderNotification.setChatNotifications(id, context,chatid);
	}

}
