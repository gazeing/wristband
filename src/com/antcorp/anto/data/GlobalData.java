package com.antcorp.anto.data;

import java.util.ArrayList;

import android.content.Intent;

import com.antcorp.anto.fragment_n_adapter.Active;
import com.antcorp.anto.fragment_n_adapter.ActiveFragment;
import com.antcorp.anto.fragment_n_adapter.ChatMsg;
import com.antcorp.anto.fragment_n_adapter.Colony;
import com.antcorp.anto.fragment_n_adapter.Connection;
import com.antcorp.anto.fragment_n_adapter.ConnectionFragment;
import com.antcorp.anto.fragment_n_adapter.MyBagFragment;
import com.antcorp.anto.fragment_n_adapter.MyTag;
import com.antcorp.anto.fragment_n_adapter.Notification;
import com.antcorp.anto.fragment_n_adapter.NotifictionFragment;
import com.antcorp.anto.network.DataWebAntCorp;
import com.antcorp.anto.nfc.NfcWrittenObserver;
import com.antcorp.anto.service.AntNotificationService;
import com.antcorp.anto.widget.ImageThreadLoader;
import com.antcorp.anto.widget.TimeoutProgressDialog;

public class GlobalData 
{
	public static DataWebAntCorp m_data;

	static public NfcWrittenObserver m_Observer = new NfcWrittenObserver();
	
	static public double lastestLatitude;
	static public double lastestLongitude;
	static public double lastestGPSAccuracy;
	
	static public String m_macString;// mac address of machine, get from LoginBaseActivity
	
	static public AntOUser m_antOUser= new AntOUser();; //store user information after successful login.
	static public AntOAuth m_aAntOAuth = new AntOAuth();//store user name and password for relogin
	
//	static public Double m_currentVersion;
	
	public static ArrayList<MyTag> 			m_tags 					= new ArrayList<MyTag>(); 
	public static ArrayList<Connection> 	m_connetions 			= new ArrayList<Connection>();
	public static ArrayList<Colony> 		m_colonys 				= new ArrayList<Colony>();
	public static ArrayList<Active> 		m_actives 				= new ArrayList<Active>();
	public static ArrayList<Notification> 	m_notifications 		= new ArrayList<Notification>(); //the notifications i received
	public static ArrayList<Notification> 	m_other_notifications 	= new ArrayList<Notification>(); //the notification i sent to others
	
	public static ConnectionFragment connectionFragment = null;
	public static MyBagFragment bagFragment = null;
	public static ActiveFragment activeFragment = null;
	public static NotifictionFragment notifictionFragment = null;

	public static int mLastServerError = 0;
	
	public static AntNotificationService m_service; //keep an instance of service globally
	
	public static ArrayList<ChatMsg> 			m_current_chatMsgs 			= new ArrayList<ChatMsg>(); 
	public static ArrayList<ChatMsg> 			m_map_chatMsgs 			= new ArrayList<ChatMsg>(); 
	
	public static TagInfo m_current_tagInfo;

	public static String m_notficationID;
	public static String m_newMemberId;
	
	public static String m_current_version;
	

	public static ImageThreadLoader m_imageLoader = new ImageThreadLoader();
	
	public static Intent m_NFCIntent;
	public static Intent m_ChatIntent;

	public static TimeoutProgressDialog m_currentPd;
	
	public static void CleanUp()
	{
		m_data = null;
	
		lastestLatitude		= 0.00;
		lastestLongitude	= 0.00;
		lastestGPSAccuracy=0;
	
		m_antOUser	= new AntOUser();; //store user information after successful login.
		m_aAntOAuth = new AntOAuth();//store user name and password for relogin
	
		m_tags 					= new ArrayList<MyTag>(); 
	   	m_connetions 			= new ArrayList<Connection>();
	   	m_colonys 				= new ArrayList<Colony>();
	   	m_actives 				= new ArrayList<Active>();
	   	m_notifications 		= new ArrayList<Notification>(); //the notifications i received
	   	m_other_notifications 	= new ArrayList<Notification>(); //the notification i sent to others
	
		bagFragment 			= null;
		activeFragment 			= null;
		notifictionFragment 	= null;

		mLastServerError = 0;
		
		m_service = null;
		
		m_current_chatMsgs	= new ArrayList<ChatMsg>(); 
		m_current_tagInfo 	= null;
		
		m_notficationID = "";
		m_newMemberId 	= "";
		m_NFCIntent=null;
		m_ChatIntent=null;
		m_currentPd =null;
	}
}


