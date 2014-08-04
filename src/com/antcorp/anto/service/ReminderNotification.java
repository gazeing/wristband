package com.antcorp.anto.service;

import java.util.ArrayList;

import com.antcorp.anto.SplashActivity;
import com.antcorp.anto.fragment_n_adapter.ChatMsg;
import com.antcorp.anto.R;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class ReminderNotification {

	static int mId = 0;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	public static int setNotification(int id, String text, String title, Context context,String notiid) 
	{
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.notifications_small_img)
												  					.setContentTitle(title).setContentText(text);

		mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		mBuilder.setLights(0xffffcc00, 1000, 500);

		Intent i = new Intent();
		i.setAction(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_LAUNCHER);
		i.setFlags(/* Intent.FLAG_ACTIVITY_NEW_TASK | */Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
		i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		i.setComponent(new ComponentName(context.getApplicationContext()
				.getPackageName(), SplashActivity.class.getName()));
		i.putExtra("noti_id", notiid);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(SplashActivity.class);
		
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(i);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT
				
		// Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP
				);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setAutoCancel(true);

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
//		mId = (int) System.nanoTime();
		notificationManager.notify(id, mBuilder.build());
		return id;
	}

	public static int setChatNotifications(int id,Context context,String NotiId){
		
		String lastmsg = "Click here to view the details";
		String senderName = "New message";
		
		
		return setNotification(id,lastmsg, senderName, context,NotiId);
	}
	
	
	public static int setChatNotifications(int id, ArrayList<ChatMsg> chat_list,String senderName,String senderId,
			Context context,String notiId) {
		
		if (chat_list.size() == 1) {
			ChatMsg chat = chat_list.get(0);
			String lastmsg = chat.getMsg();
			if(senderId.equals(chat.getSenderId()))
				return setNotification(id,lastmsg, senderName, context,notiId);
			
		} else if (chat_list.size() > 1) {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					context).setSmallIcon(R.drawable.notifications_small_img)
					.setContentTitle(chat_list.size() + " new messages")
					.setContentText("Expand this view to see them all");
					

			mBuilder.setLights(0xffffcc00, 100, 100);
			NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
			
			// Sets a title for the Inbox style big view
			inboxStyle.setBigContentTitle(chat_list.size() + " new messages");

			// Moves events into the big view
			for (ChatMsg chat : chat_list) {
				String lastmsg =chat.getMsg();

				inboxStyle.addLine(senderName + ": " + lastmsg);
			}
			inboxStyle.setSummaryText("Click to open it in Ant-O");
			// Moves the big view style object into the notification object.
			mBuilder.setStyle(inboxStyle);
			mBuilder.setNumber(chat_list.size());

			mBuilder.setSound(RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

			Intent i = new Intent();
			i.setAction(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_LAUNCHER);
			i.setFlags(/* Intent.FLAG_ACTIVITY_NEW_TASK | */Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);

			i.setComponent(new ComponentName(context.getApplicationContext()
					.getPackageName(), SplashActivity.class.getName()));
			
			i.putExtra("noti_id", notiId);

			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(SplashActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(i);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT

					);
			mBuilder.setContentIntent(resultPendingIntent);
			mBuilder.setAutoCancel(true);

			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

			notificationManager.notify(id, mBuilder.build());
			return id;

		}
		return 0;
	
	}

}
