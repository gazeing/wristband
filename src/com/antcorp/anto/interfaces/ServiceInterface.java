package com.antcorp.anto.interfaces;

import java.util.ArrayList;

import com.antcorp.anto.data.AntOUser;
import com.antcorp.anto.data.TagInfo;
import com.antcorp.anto.fragment_n_adapter.ChatMsg;
import com.antcorp.anto.fragment_n_adapter.MyTag;
import com.antcorp.anto.fragment_n_adapter.Notification;

public interface ServiceInterface 
{
	void AntOWebResponseMyBag(int piRetCode, ArrayList<MyTag> pMyBag); //
	void AntOWebResponseLogin(int piRetCode, AntOUser pUser, ArrayList<MyTag> pMyBag); //
	void AntOWebResponseRegistration(int piRetCode, AntOUser pUser, ArrayList<MyTag> pMyBag); //
	void AntOWebResponseGetChatList(int piRetCode,  ArrayList<ChatMsg> pChatMsgs);  //
	void AntOWebResponseSendChatSuccess(int piRetCode,  ArrayList<ChatMsg> pChatMsgs); //
	void AntOWebResponseGetNotifyList(int piRetCode,  ArrayList<Notification> pMyNotis); //
	void AntOWebResponseLogOff(int piRetCode); //
	void AntOWebResponseGetTagInfo(int piRetCode, TagInfo taginfo);
	void AntOWebResponseEditTagInfoSuccess(int piRetCode); //
	void AntOWebResponseAddToBagSuccess(int piRetCode); //
	void AntOWebResponseNotifyTagOwner(int piRetCode,String notificationId);
	void AntOWebResponseAddNewColony(int piRetCode,String newMemberId);
	void AntOWebResponseEditColony(int piRetCode);
	void AntOWebResponseCheckVersion(int piRetCode,
			String m_current_version);
	void AntOWebResponseDeleteColony(int piRetCode);
	void AntOWebResponseDeleteConnectionSuccess(int piRetCode);
	void AntOWebResponseChangeTagName(int piRetCode);
	void AntOWebResponseRemoveAllNotifications(int piRetCode);
	void AntOWebResponseRemoveNotification(int piRetCode);
	void AntOWebResponseCloseChat(int piRetCode);
	void AntOWebResponseForgetPw(int piRetCode);
	void AntOWebResponseRemoveTag(int piRetCode);
}
