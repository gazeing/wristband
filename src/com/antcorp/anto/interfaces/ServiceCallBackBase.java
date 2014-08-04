package com.antcorp.anto.interfaces;

import java.util.ArrayList;

import android.content.Context;
import android.widget.Toast;

import com.antcorp.anto.R;
import com.antcorp.anto.data.AntOUser;
import com.antcorp.anto.data.TagInfo;
import com.antcorp.anto.fragment_n_adapter.ChatMsg;
import com.antcorp.anto.fragment_n_adapter.MyTag;
import com.antcorp.anto.fragment_n_adapter.Notification;

public class ServiceCallBackBase implements ServiceInterface {
	
	Context context;
	

	public ServiceCallBackBase(Context context) {
		super();
		this.context = context;
	}
	
	protected void showConnectionError(int code){
		if (code==-9999){
			Toast.makeText(context, R.string.connection_field,
					Toast.LENGTH_SHORT).show();
			return;
		}
	}

	@Override
	public void AntOWebResponseMyBag(int piRetCode, ArrayList<MyTag> pMyBag) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseLogin(int piRetCode, AntOUser pUser,
			ArrayList<MyTag> pMyBag) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseRegistration(int piRetCode, AntOUser pUser,
			ArrayList<MyTag> pMyBag) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseGetChatList(int piRetCode,
			ArrayList<ChatMsg> pChatMsgs) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseSendChatSuccess(int piRetCode,
			ArrayList<ChatMsg> pChatMsgs) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseGetNotifyList(int piRetCode,
			ArrayList<Notification> pMyNotis) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseLogOff(int piRetCode) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseGetTagInfo(int piRetCode, TagInfo taginfo) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseEditTagInfoSuccess(int piRetCode) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseAddToBagSuccess(int piRetCode) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseNotifyTagOwner(int piRetCode,
			String notificationId) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseAddNewColony(int piRetCode, String newMemberId) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseEditColony(int piRetCode) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseCheckVersion(int piRetCode,
			String m_current_version) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseDeleteColony(int piRetCode) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseDeleteConnectionSuccess(int piRetCode) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseChangeTagName(int piRetCode) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseRemoveAllNotifications(int piRetCode) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseRemoveNotification(int piRetCode) {
//		showConnectionError(piRetCode);
//
	}

	@Override
	public void AntOWebResponseCloseChat(int piRetCode) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseForgetPw(int piRetCode) {
//		showConnectionError(piRetCode);

	}

	@Override
	public void AntOWebResponseRemoveTag(int piRetCode) {
//		showConnectionError(piRetCode);

	}

}
