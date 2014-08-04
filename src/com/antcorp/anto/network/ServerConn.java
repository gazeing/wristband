package com.antcorp.anto.network;

import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.antcorp.anto.data.DataTag;
import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.data.TagInfo;
import com.antcorp.anto.fragment_n_adapter.Active;
import com.antcorp.anto.fragment_n_adapter.ChatMsg;
import com.antcorp.anto.fragment_n_adapter.Colony;
import com.antcorp.anto.fragment_n_adapter.Connection;
import com.antcorp.anto.fragment_n_adapter.MyTag;
import com.antcorp.anto.fragment_n_adapter.Notification;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.widget.UtilStatics;

public class ServerConn {
	public static BasicCookieStore cookieStore = new BasicCookieStore();

	static int miCounterErr = 0;
	Integer miLogin = -1;
	BasicHttpContext httpContext;

	// Create and initialize scheme registry
	SchemeRegistry schemeRegistry = new SchemeRegistry();

//	public final static String WEB_SERVER_ADDR = "http://192.168.42.149:3000";
//	 public final static String WEB_SERVER_ADDR = "http://192.168.0.15:3000";
	 public final static String WEB_SERVER_ADDR = "https://ant-o.com";

	public final static String IMAGE_PREFIX = WEB_SERVER_ADDR
			+ "/assets/profile/";

	ClientConnectionManager cm;
	HttpClient httpClient;

	static boolean mbTurnOn = true;

	boolean mbOnCall = false;

	public ServerConn() {
		RegisterConn();
	}

	private boolean RegisterConn() {
		httpClient = getSSLHttpClient();

		Cookie cookie = new BasicClientCookie("name", "value");
		(cookieStore).addCookie(cookie);

		return true;
	}

	public HttpClient getSSLHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new AllSSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUseExpectContinue(params, false);

			int timeoutConnection = 15000;
			HttpConnectionParams
					.setConnectionTimeout(params, timeoutConnection);

			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 15000;
			HttpConnectionParams.setSoTimeout(params, timeoutSocket);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);
			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public void ShutDown() {
		if (cm != null)
			cm.shutdown();
	}

	public String CallAntCorp_Post(String pUrl, List<NameValuePair> pDataSend) {
		String lsRet = "";
		// for (int liFor = 0; liFor < 3; liFor++) {
		lsRet = CallAntCorp_Post(pUrl, pDataSend, false);

		// if (lsRet.trim().length() > 0) {
		// check for -100 and redo login (!?)
		return lsRet;
		// }

		// try {
		// Thread.sleep(500);
		// } catch (InterruptedException e) {
		// CrashReport.ReportToServer(e);
		// }
		// }

		// httpClient.getConnectionManager().closeExpiredConnections();
		// RegisterConn();

		// return lsRet;
	}

	public synchronized String CallAntCorp_Post(String pUrl,
			List<NameValuePair> pDataSend, boolean pbForceClose) {
		String lsRet = "";

		if (!mbTurnOn || mbOnCall)
			return lsRet;

		try {
			mbOnCall = true;

			if (pbForceClose) {
				cm.closeIdleConnections(1, TimeUnit.MILLISECONDS);
				cm.closeExpiredConnections();

				RegisterConn();
			}

			HttpPost httppost = new HttpPost(pUrl);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.addAll(pDataSend.subList(0, pDataSend.size()));
			if (HttpHelper.HasProtectForgery()) {
				nameValuePairs.add(new BasicNameValuePair(HttpHelper
						.GetForgeryParam(), HttpHelper.GetForgeryToken()));
				MyLog.i("HttpHelper.GetForgeryParam()= "
						+ HttpHelper.GetForgeryParam()
						+ ",  HttpHelper.GetForgeryToken()= "
						+ HttpHelper.GetForgeryToken());
			}

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpContext localContext = new BasicHttpContext();
			localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

			HttpResponse response = httpClient.execute(httppost, localContext);

			if (response != null)
				lsRet = HttpHelper.request(response);

			mbOnCall = false;
			miCounterErr = 0;

			nameValuePairs.clear();

			return lsRet;

		} catch (ClientProtocolException e) {
			miCounterErr++;
			if (miCounterErr > 50) {
				CrashReport.ReportToServer(e);
				miCounterErr = 0;
			}

			httpClient.getConnectionManager().closeExpiredConnections();
			RegisterConn();

		} catch (IOException e) {
			MyLog.i(e);
			miCounterErr++;
			if (miCounterErr > 50) {
				CrashReport.ReportToServer(e);
				miCounterErr = 0;
			}

			if (miCounterErr > 3)
				miCounterErr = 0;

		} catch (Exception e) {
			miCounterErr++;
			if (miCounterErr > 50) {
				CrashReport.ReportToServer(e);
				miCounterErr = 0;
			}

			httpClient.getConnectionManager().closeExpiredConnections();
			RegisterConn();
		}

		mbOnCall = false;

		return lsRet;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean GetAntOMyBag() {
		String lsData = "";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		// nameValuePairs.add(new BasicNameValuePair("game_id", psGameId ));

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR + "/api/ant_o_my_bag",
				nameValuePairs);

		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			ParseServerData_MyBag(lsData);

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	public boolean ParseServerData_MyBag(String psData) {
		try {

			GlobalData.m_tags.clear();
			GlobalData.m_connetions.clear();
			GlobalData.m_colonys.clear();
			GlobalData.m_actives.clear();

			JSONObject jsonData = new JSONObject(psData);
			if (!jsonData.isNull("bag_data")) {
				JSONObject jsonO = jsonData.getJSONObject("bag_data");

				// JSONObject jsonO = new JSONObject(jsonString);
				JSONArray jArray = jsonO.getJSONArray("bag_item");
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject j = jArray.getJSONObject(i);
					String name = j.getString("tag_name");
					String id = j.getString("tag_id");
					int type = j.getInt("tag_type");
					String uid = "";
					if (j.has("nfc_uid"))
						uid = j.getString("nfc_uid");
					// String img = j.getString("tag_id");
					addTag(name, id, type, "", true, uid);
				}

				JSONArray jConnectionArray = jsonO
						.getJSONArray("user_connection");

				for (int i = 0; i < jConnectionArray.length(); i++) {
					JSONObject json = jConnectionArray.getJSONObject(i);
					String connectionId = json.getString("id");
					String colony_member_id = json
							.getString("colony_member_id");
					String current_active = json.getString("current_active");
					String tag_id = json.getString("tag_id");
					String createdAt = json.getString("created_at");
					String updatedAt = json.getString("updated_at");

					addConnection(connectionId, colony_member_id,
							current_active, tag_id, createdAt, updatedAt);
				}
			}
			JSONArray jColonyArray = jsonData.getJSONArray("colony_members");
			for (int i = 0; i < jColonyArray.length(); i++) {
				JSONObject json = jColonyArray.getJSONObject(i);
				String name = json.getString("name_user");
				String surname = json.getString("surname_user");
				String contactSurname = json.getString("surname_contact");
				String contactName = json.getString("name_contact");
				String contactPhone1 = json.getString("contact_phone1");
				String ant_owner_id = json.getString("ant_owner_id");
				String info = json.getString("aditional_info");
				String img = json.getString("user_image");
				String colony_id = json.getString("id");
				String created_at = json.getString("created_at");
				String updated_at = json.getString("updated_at");
				String is_member_owner = "0";
				if (json.has("is_member_owner"))
					is_member_owner = json.getString("is_member_owner");

				addColony(name, surname, contactName, contactSurname,
						contactPhone1, ant_owner_id, info,
						img.length() > 0 ? IMAGE_PREFIX + img : img, colony_id,
						created_at, updated_at, is_member_owner);
			}

			addActives();

			return true;
		} catch (Exception e) {
			MyLog.i(e);
			return false;
		}
	}

	private void addActives() {
		boolean isActive = false;
		String tagid = "";
		for (Connection c : GlobalData.m_connetions) {

			tagid = c.getTag_id();
			isActive = (c.getCurrent_active().equals("1"));

			String name = "";
			String surname = "";
			String info = "";

			for (Colony colony : GlobalData.m_colonys) {
				if (c.getColony_member_id()
						.equals(colony.getColony_member_id())) {
					name = colony.getName();
					surname = colony.getSurname();
					info = colony.getInfo();
				}
			}

			if (isActive) {
				String tagname = "My tag";
				for (MyTag t : GlobalData.m_tags) {
					if (t.getTagId().equals(tagid)) {
						tagname = t.getTagName();
					}
				}
				Active a = new Active(name + " " + surname, tagname, info, 1);
				GlobalData.m_actives.add(a);
			}
		}

	}

	protected void addColony(String name, String surname, String contactName,
			String contactSurName, String contactPhone1, String antOwnerId,
			String info, String img, String colony_member_id, String createdAt,
			String updatedAt, String is_member_owner) {

		Colony colony = new Colony(name, surname, contactName, contactSurName,
				contactPhone1, antOwnerId, info, img, colony_member_id,
				createdAt, updatedAt, is_member_owner);
		// is the colony belongs to user, put it on the top of list
		if (is_member_owner.equals("1"))
			GlobalData.m_colonys.add(0, colony);
		else
			GlobalData.m_colonys.add(colony);

		// boolean isActive = false;
		// String tagid = "";
		// for (Connection c : GlobalData.m_connetions) {
		// if (c.getColony_member_id().equals(colony_member_id)) {
		// tagid = c.getTag_id();
		// isActive = (c.getCurrent_active().equals("1"));
		// }
		// }
		// if (isActive) {
		// String tagname = "My tag";
		// for (MyTag t : GlobalData.m_tags) {
		// if (t.getTagId().equals(tagid)) {
		// tagname = t.getTagName();
		// }
		// }
		// Active a = new Active(name + " " + surname, tagname, info, 1);
		// GlobalData.m_actives.add(a);
		// }
	}

	protected void addConnection(String connectionId, String colony_member_id,
			String current_active, String tag_id, String createdAt,
			String updatedAt) {

		Connection connection = new Connection(connectionId, colony_member_id,
				current_active, tag_id, createdAt, updatedAt);
		GlobalData.m_connetions.add(connection);

	}

	private void addTag(String name, String id, int type, String imgAddress,
			boolean hasConnection, String uid) {
		MyTag tag = new MyTag(name, id, type, "", hasConnection, uid);
		GlobalData.m_tags.add(tag);
	}

	// //////////// Login and Register a new user

	public boolean LoginAntO(String psEmail, String psPwd, String regid) {
		String lsData = "";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		nameValuePairs.add(new BasicNameValuePair("email", psEmail));
		nameValuePairs.add(new BasicNameValuePair("pwd", psPwd));
		nameValuePairs.add(new BasicNameValuePair("mac_me",
				GlobalData.m_macString));
		nameValuePairs.add(new BasicNameValuePair("mobile_os_type", "ANDROID"));
		nameValuePairs.add(new BasicNameValuePair("regis_id", regid));

		// s
		// "APA91bERWBTbAM8IG1anMVUJL8_WxS47VBvecyL8O8K-qfMJqSaKFnXIw3jiN7qDc8k2SFlkfrpt64FTe6eF8hrgIFae9gL7VhhCUt7WoGRcibXH51YH-nUc_G3X104221SrYc8WeO4kPCMiH_jyvH5KSlXFEvKTFQ"));

		MyLog.i("regid= "+regid);
		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR + "/api/ant_o_login",
				nameValuePairs);

		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			ParseServerData_User(lsData);

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	public boolean RegistrationAntO(String psName, String psSurname,
			String psEmail, String psPwd, String regid) {
		String lsData = "";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		nameValuePairs.add(new BasicNameValuePair("name", psName));
		nameValuePairs.add(new BasicNameValuePair("surname", psSurname));
		nameValuePairs.add(new BasicNameValuePair("email", psEmail));
		nameValuePairs.add(new BasicNameValuePair("pwd", psPwd));
		nameValuePairs.add(new BasicNameValuePair("mac_me",
				GlobalData.m_macString));
		nameValuePairs.add(new BasicNameValuePair("mobile_os_type", "ANDROID"));
		nameValuePairs.add(new BasicNameValuePair("regis_id", regid));
		// "APA91bERWBTbAM8IG1anMVUJL8_WxS47VBvecyL8O8K-qfMJqSaKFnXIw3jiN7qDc8k2SFlkfrpt64FTe6eF8hrgIFae9gL7VhhCUt7WoGRcibXH51YH-nUc_G3X104221SrYc8WeO4kPCMiH_jyvH5KSlXFEvKTFQ"));

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_registration", nameValuePairs);

		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			ParseServerData_User(lsData);

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	public String ParseInfoResponse(String psJsonFromServer) {
		try {
			DataWebAntCorp lWebRet = UtilStatics.AntCorpInfo(psJsonFromServer);

			GlobalData.mLastServerError = lWebRet.miRetCode;
			return lWebRet.msDataJson;
		} catch (Exception e) {
			return "";
		}

	}

	public boolean ParseServerData_User(String psData) {

		try {

			JSONObject json = new JSONObject(psData);
			JSONObject jsonUser = json.getJSONObject("user_info");

			GlobalData.m_antOUser.name = jsonUser.getString("ant_name");
			GlobalData.m_antOUser.surname = jsonUser.getString("ant_surname");
			GlobalData.m_antOUser.email = jsonUser.getString("ant_email");
			GlobalData.m_antOUser.id = jsonUser.getString("id");

			MyLog.i("**********user info is (" + GlobalData.m_antOUser.name
					+ ", " + GlobalData.m_antOUser.surname + ", "
					+ GlobalData.m_antOUser.email + ", "
					+ GlobalData.m_antOUser.id + ") ");

			ParseServerData_MyBag(psData);

		} catch (JSONException e) {
			MyLog.i(e);
			return false;
		}

		return true;

	}

	// /////////
	// //////////////forget password
	public boolean ForgetPwAntO(String email) {
		@SuppressWarnings("unused")
		String lsData = "";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		nameValuePairs.add(new BasicNameValuePair("ant_o_email", email));

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_recovery_password", nameValuePairs);

		MyLog.i("~~~~~~~~~~~~return=  " + lsRet);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	// ////////////
	// //////////// Add to my Bag

	public boolean RegisterAsMineAntO(String tagid, String tagname,
			String agree_tos, String Uid) {
		@SuppressWarnings("unused")
		String lsData = "";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		nameValuePairs.add(new BasicNameValuePair("ant_o_tag_id", tagid));
		nameValuePairs.add(new BasicNameValuePair("tag_name", tagname));
		nameValuePairs.add(new BasicNameValuePair("agree_tos", agree_tos + ""));
		nameValuePairs.add(new BasicNameValuePair("lat",
				GlobalData.lastestLatitude + ""));
		nameValuePairs.add(new BasicNameValuePair("lon",
				GlobalData.lastestLongitude + ""));

		if (Uid.length() > 0)
			nameValuePairs.add(new BasicNameValuePair("ant_o_nfc_id", Uid));

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_register_as_mine", nameValuePairs);

		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	// //change tag name

	public boolean ChangeTagNameAntO(String tagid, String tagname) {
		@SuppressWarnings("unused")
		String lsData = "";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		nameValuePairs.add(new BasicNameValuePair("ant_o_tag_id", tagid));
		nameValuePairs.add(new BasicNameValuePair("new_name", tagname));

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_change_tag_name", nameValuePairs);

		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	// /// Log off

	public boolean LogoffAntO() {
		@SuppressWarnings("unused")
		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR + "/api/ant_o_logoff",
				nameValuePairs);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	// /// get Chat list

	public boolean GetChatListAntO(String tagId, String notificationId) {
		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		nameValuePairs.add(new BasicNameValuePair("ant_o_tag_id", tagId));
		nameValuePairs.add(new BasicNameValuePair("chat_id", notificationId));
		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_get_chat_list", nameValuePairs);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			ParseServerData_ChatList(lsData);

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	private void ParseServerData_ChatList(String lsData) {
		try {
			JSONObject j = new JSONObject(lsData);

			String sender_name = j.getString("sender_name");
			@SuppressWarnings("unused")
			String sender_surname = j.getString("sender_surname");

			String owner_name = j.getString("owner_name");
			GlobalData.m_current_chatMsgs.clear();
			JSONArray jArray = j.getJSONArray("messages");

			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json = jArray.getJSONObject(i);
				String id = json.getString("sender_message_id");
				String time = json.getString("created_at");

//				long realtime = UtilStatics.getLongFromServerTimeFormat(time);
//				time = UtilStatics.TransferTimeFormat(realtime);
				String message = json.getString("message");
				double latitude = json.getDouble("latitude");
				double longitude = json.getDouble("longitude");

				String name = sender_name;
				if (id.equals(GlobalData.m_antOUser.id))
					name = GlobalData.m_antOUser.name;
				else
					name = (GlobalData.m_antOUser.name.equals(sender_name)) ? owner_name
							: sender_name;

				ChatMsg chatMsg = new ChatMsg(name, time, message, id,
						longitude, latitude);

				GlobalData.m_current_chatMsgs.add(chatMsg);
				// Trigger next timer run?
			}

		} catch (JSONException e) {
			MyLog.i(e);
		}

	}

	// ///////send chat msg

	public boolean SendChatMessageAntO(String tagid, String notificationId,
			String msg) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		nameValuePairs.add(new BasicNameValuePair("ant_o_tag_id", tagid));
		nameValuePairs.add(new BasicNameValuePair("message", msg));
		nameValuePairs.add(new BasicNameValuePair("chat_id", notificationId));
		nameValuePairs.add(new BasicNameValuePair("latitude",
				GlobalData.lastestLatitude + ""));
		nameValuePairs.add(new BasicNameValuePair("longitude",
				GlobalData.lastestLongitude + ""));
		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_chat_notify", nameValuePairs);
		ParseInfoResponse(lsRet);

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	// ////////////get notify open list

	public boolean GetNotifyListAntO() {
		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_get_notification_list", nameValuePairs);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			ParseServerData_NotifyList(lsData);

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	// parse my_notification_list array
	private void parseNotiArray(JSONArray jArray, boolean isFromMe) {

		try {
			if (jArray != null) {
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject json = jArray.getJSONObject(i);

					String sendername = json.getString("sender_name");
					String senderSurname = "";
					if (json.has("sender_surname"))
						senderSurname = json.getString("sender_surname");
					String ownerName = "";
					if (json.has("owner_name"))
						ownerName = json.getString("owner_name");
					String ownerSurname = json.getString("owner_surname");
					String aboutname = json.getString("name_user_tag");
					String aboutSurname = json.getString("surname_user_tag");
					String notiId = json.getString("id");
					String tagid = json.getString("tag_id");
					String userImage = json.getString("user_image_tag");
					double lati = json.getDouble("latitude");
					double longi = json.getDouble("longitude");

					String uid = "";
					if (json.has("nfc_uid"))
						uid = json.getString("nfc_uid");

					int status = json.getInt("noti_status");
					String lastUpdate = json.getString("updated_at");

					long realtime = UtilStatics
							.getLongFromServerTimeFormat(lastUpdate);
					lastUpdate = UtilStatics.TransferTimeFormat(realtime);

					int msg_count_sender = json.getInt("msg_count_sender");
					int msg_count_owner = json.getInt("msg_count_owner");
					int msg_max = 10; // json.getInt("msg_count_sender");
					JSONArray jsonArray = json
							.getJSONArray("notification_message");
					ArrayList<ChatMsg> chat_list = new ArrayList<ChatMsg>();

					for (int n = 0; n < jsonArray.length(); n++) {
						JSONObject jchat = jsonArray.getJSONObject(n);

						String id = jchat.getString("sender_message_id");
						String time = jchat.getString("created_at");

//						realtime = UtilStatics
//								.getLongFromServerTimeFormat(time);
//						time = UtilStatics.TransferTimeFormat(realtime);
						String message = jchat.getString("message");
						double latitude = jchat.getDouble("latitude");
						double longitude = jchat.getDouble("longitude");

						// if (sender_name.equals(GlobalData.m_antOUser.name))
						// sender_name =
						// getIntent().getExtras().getString("tagname");
						String name = sendername;
						if (id.equals(GlobalData.m_antOUser.id))
							name = GlobalData.m_antOUser.name;
						else
							name = (GlobalData.m_antOUser.name
									.equals(sendername)) ? ownerName
									: sendername;

						ChatMsg chatMsg = new ChatMsg(name, time, message, id,
								longitude, latitude);
						chat_list.add(chatMsg);

					}

					Notification noti = new Notification(ownerName,
							ownerSurname, sendername, senderSurname, aboutname,
							aboutSurname, status + "",
							userImage.length() > 0 ? IMAGE_PREFIX + userImage
									: userImage, lati, longi, notiId, tagid,
							lastUpdate, chat_list, msg_count_sender,
							msg_count_owner, msg_max, isFromMe, uid);
					GlobalData.m_notifications.add(noti);
				}
			}

		} catch (Exception e) {
			MyLog.i(e);
		}

	}

	private void ParseServerData_NotifyList(String lsData) {
		try {
			GlobalData.m_notifications.clear();
			JSONObject jsonObject = new JSONObject(lsData);

			JSONArray jArray = jsonObject.getJSONArray("my_notification_list");

			parseNotiArray(jArray, false);

			JSONArray jArray_other = jsonObject
					.getJSONArray("others_notification");
			parseNotiArray(jArray_other, true);

		} catch (JSONException e) {
			MyLog.i(e);
		}

	}

	// ////////// get tag info

	public boolean GetTagInfoAntO(String id, String Uid) {
		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		nameValuePairs.add(new BasicNameValuePair("ant_o_id", id));
		if (Uid.length() > 0)
			nameValuePairs.add(new BasicNameValuePair("ant_o_nfc_id", Uid));

		String lsRet = CallAntCorp_Post(
				WEB_SERVER_ADDR + "/api/ant_o_get_info", nameValuePairs);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			ParseServerData_GetInfo(lsData);

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	private void ParseServerData_GetInfo(String lsData) {
		String jsonString = lsData;
		if (jsonString != null) {

			JSONObject json;
			try {
				json = new JSONObject(jsonString);
				if (json != null) {
					Colony c = null;
					DataTag d = null;

					// when parse "ant_owner":null,we will get"null" here, have
					// to deal with it
					String antOwner = json.getString("ant_owner");

					if (antOwner.equals("null"))
						antOwner = null;

					String belongToMe = "";
					if (json.has("belong_to_me"))
						belongToMe = json.getString("belong_to_me");

					if (json.has("user_connection")) {
						JSONObject jsonConnection = null;
						// if user_connection is empty, the value will be "null"
						if (!json.getString("user_connection").equals("null")
								&& !json.getString("user_connection").equals(
										"[]")) {
							jsonConnection = json
									.getJSONObject("user_connection");

							String name = "";
							String surname = "";
							String contactSurname = "";
							String contactName = "";
							String contactPhone1 = "";
							String ant_owner_id = "";
							String info = "";
							String img = "";
							String colony_id = "";
							String created_at = "";
							String updated_at = "";
							String is_member_owner = "0";

							if (jsonConnection.has("name_user"))
								name = jsonConnection.getString("name_user");
							if (jsonConnection.has("surname_user"))
								surname = jsonConnection
										.getString("surname_user");
							if (jsonConnection.has("surname_contact"))
								contactSurname = jsonConnection
										.getString("surname_contact");
							if (jsonConnection.has("name_contact"))
								contactName = jsonConnection
										.getString("name_contact");
							if (jsonConnection.has("contact_phone1"))
								contactPhone1 = jsonConnection
										.getString("contact_phone1");
							if (jsonConnection.has("ant_owner_id"))
								ant_owner_id = jsonConnection
										.getString("ant_owner_id");
							if (jsonConnection.has("aditional_info"))
								info = jsonConnection
										.getString("aditional_info");
							if (jsonConnection.has("user_image"))
								img = jsonConnection.getString("user_image");
							if (jsonConnection.has("id"))
								colony_id = jsonConnection.getString("id");
							if (jsonConnection.has("created_at"))
								created_at = jsonConnection
										.getString("created_at");
							if (jsonConnection.has("updated_at"))
								updated_at = jsonConnection
										.getString("updated_at");
							if (jsonConnection.has("is_member_owner"))
								is_member_owner = jsonConnection
										.getString("is_member_owner");

							c = new Colony(name, surname, contactName,
									contactSurname, contactPhone1,
									ant_owner_id, info,
									+img.length() > 0 ? IMAGE_PREFIX + img
											: img, colony_id, created_at,
									updated_at, is_member_owner);
						}
					}

					String tagid = "";
					if (json.has("tag_id"))
						tagid = json.getString("tag_id");

					if (json.has("data_tag")) {
						JSONObject jsonTagData = json.getJSONObject("data_tag");
						if (jsonTagData != null) {
							String createAt = "";
							String tagName = "";
							int tagType = -1;
							int tagLocked = 0;
							int tagSize = 0;
							String tagLote = "";
							String nfcUid = "";

							if (jsonTagData.has("created_at"))
								createAt = jsonTagData.getString("created_at");
							if (jsonTagData.has("tag_name"))
								tagName = jsonTagData.getString("tag_name");
							if (jsonTagData.has("tag_id"))
								tagid = jsonTagData.getString("tag_id");
							if (jsonTagData.has("tag_type"))
								tagType = jsonTagData.getInt("tag_type");
							if (jsonTagData.has("tag_locked"))
								tagLocked = jsonTagData.getInt("tag_locked");
							if (jsonTagData.has("tag_size"))
								tagSize = jsonTagData.getInt("tag_size");
							if (jsonTagData.has("tag_lote"))
								tagLote = jsonTagData.getString("tag_lote");
							if (jsonTagData.has("nfc_uid"))
								nfcUid = jsonTagData.getString("nfc_uid");

							// double latitude = 0;//
							// jsonTagData.getDouble("latitude");
							// double longitude = 0;//
							// jsonTagData.getDouble("longitude");

							d = new DataTag(createAt, tagName, tagid, tagType,
									tagLocked, tagLote, nfcUid, tagSize);

						}
					}

					GlobalData.m_current_tagInfo = new TagInfo(antOwner,
							belongToMe, c, d);
				}
			} catch (JSONException e) {
				MyLog.i(e);
			}

		}

	}

	// //// edit connections
	public boolean EditConnectionAntO(String tagid, String memberId) {
		@SuppressWarnings("unused")
		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		nameValuePairs.add(new BasicNameValuePair("ant_o_tag_id", tagid));
		nameValuePairs.add(new BasicNameValuePair("member_id", memberId));

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_set_connection", nameValuePairs);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	// ///// notify tag Owner

	public boolean NotifyTagOwnerAntO(String tagid, String Message, String uid) {

		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		nameValuePairs.add(new BasicNameValuePair("ant_o_tag_id", tagid));
		nameValuePairs.add(new BasicNameValuePair("message", Message));
		nameValuePairs.add(new BasicNameValuePair("latitude",
				GlobalData.lastestLatitude + ""));
		nameValuePairs.add(new BasicNameValuePair("longitude",
				GlobalData.lastestLongitude + ""));
		nameValuePairs.add(new BasicNameValuePair("ant_o_nfc_id", uid));

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR + "/api/ant_o_notify",
				nameValuePairs);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			GlobalData.m_notficationID = lsData;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	// ////////////add a new colony member()
	public boolean AddNewMemberAntO(String name, String surname,
			String contactName, String contactSurName, String contactNum,
			String img, String additionInfo, String isOwnerUser) {

		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		nameValuePairs.add(new BasicNameValuePair("name_connection", name));
		nameValuePairs
				.add(new BasicNameValuePair("surname_connection", surname));
		nameValuePairs.add(new BasicNameValuePair("contact_name", contactName));
		nameValuePairs.add(new BasicNameValuePair("contact_surname",
				contactSurName));
		nameValuePairs.add(new BasicNameValuePair("phone1", contactNum));
		nameValuePairs.add(new BasicNameValuePair("image_connection", img));
		nameValuePairs.add(new BasicNameValuePair("additional_info",
				additionInfo));
		nameValuePairs.add(new BasicNameValuePair("is_member_owner",
				isOwnerUser));

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_add_new_member", nameValuePairs);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			ParseServerData_addNewMember(lsData);
		// GlobalData.m_newMemberId = lsData;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	private void ParseServerData_addNewMember(String lsData) {
		String jsonString = lsData;
		if (jsonString != null) {

			JSONObject json;
			try {
				json = new JSONObject(jsonString);
				GlobalData.m_newMemberId = json.getString("new_member_id");
			} catch (JSONException e) {
				MyLog.i(e);
			}
		}
	}

	// ////////////////edit colony member
	public boolean EditColonyMemberAntO(String name, String surname,
			String contactName, String contactSurName, String contactNum,
			String img, String additionInfo, String memberId) {

		@SuppressWarnings("unused")
		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		nameValuePairs.add(new BasicNameValuePair("name_connection", name));
		nameValuePairs
				.add(new BasicNameValuePair("surname_connection", surname));
		nameValuePairs.add(new BasicNameValuePair("contact_name", contactName));
		nameValuePairs.add(new BasicNameValuePair("contact_surname",
				contactSurName));
		nameValuePairs.add(new BasicNameValuePair("phone1", contactNum));
		nameValuePairs.add(new BasicNameValuePair("image_connection", img));
		nameValuePairs.add(new BasicNameValuePair("additional_info",
				additionInfo));
		nameValuePairs.add(new BasicNameValuePair("member_id", memberId));

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_edit_colony_member", nameValuePairs);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	// //delete connection
	public boolean DeleteConnectionAntO(String tagid) {
		@SuppressWarnings("unused")
		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("ant_o_tag_id", tagid));

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_disconnect_member", nameValuePairs);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	// ///delete colony
	public boolean DeleteColonyAntO(String memberId) {
		@SuppressWarnings("unused")
		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("ant_member_id", memberId));

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_delete_colony_member", nameValuePairs);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	// remove tag from my bag

	public boolean RemoveTagAntO(String tagId) {
		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("ant_o_tag_id", tagId));

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_remove_from_my_bag", nameValuePairs);
		MyLog.i(lsRet);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			ParseServerData_removeTag(lsData);
		;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	private void ParseServerData_removeTag(String jsonString) {
		try {

			GlobalData.m_tags.clear();
			GlobalData.m_connetions.clear();

			JSONObject jsonO = new JSONObject(jsonString);
			JSONArray jArray = jsonO.getJSONArray("bag_item");
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject j = jArray.getJSONObject(i);
				String name = j.getString("tag_name");
				String id = j.getString("tag_id");
				int type = j.getInt("tag_type");
				String uid = "";
				if (j.has("nfc_uid"))
					uid = j.getString("nfc_uid");
				// String img = j.getString("tag_id");
				addTag(name, id, type, "", true, uid);
			}

			JSONArray jConnectionArray = jsonO.getJSONArray("user_connection");

			for (int i = 0; i < jConnectionArray.length(); i++) {
				JSONObject json = jConnectionArray.getJSONObject(i);
				String connectionId = json.getString("id");
				String colony_member_id = json.getString("colony_member_id");
				String current_active = json.getString("current_active");
				String tag_id = json.getString("tag_id");
				String createdAt = json.getString("created_at");
				String updatedAt = json.getString("updated_at");

				addConnection(connectionId, colony_member_id, current_active,
						tag_id, createdAt, updatedAt);
			}
		} catch (Exception e) {
			MyLog.i(e);

		}

	}

	// /remove all notifications
	public boolean RemoveAllNotificationsAntO() {
		@SuppressWarnings("unused")
		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_remove_all_notifications", nameValuePairs);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			;
		;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	// ////check current version
	public boolean CheckVersionAntO() {
		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

		nameValuePairs
		.add(new BasicNameValuePair("os_type", "ANDROID"));
		
		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_current_version", nameValuePairs);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			ParseServerData_checkVersion(lsData);
		;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	private void ParseServerData_checkVersion(String lsData) {
		String jsonString = lsData;
		if (jsonString != null) {

			JSONObject json;
			try {
				json = new JSONObject(jsonString);
				GlobalData.m_current_version = json.getString("version");
			} catch (JSONException e) {
				MyLog.i(e);
			}
		}

	}

	// /////////////////////////////////////

	public void SetWebAccess(boolean pbTurnON) {
		mbTurnOn = pbTurnON;
	}

	static class CrashReport {
		static void ReportToServer(Exception e) {
			MyLog.i(e.getMessage());
		}
	}

	public boolean RemoveNotificationAntO(String chatid) {
		@SuppressWarnings("unused")
		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("ant_o_chat_id", chatid));

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_remove_notification", nameValuePairs);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

	public boolean CloseChatAntO(String chatid) {
		@SuppressWarnings("unused")
		String lsData = "";
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("ant_o_chat_id", chatid));

		String lsRet = CallAntCorp_Post(WEB_SERVER_ADDR
				+ "/api/ant_o_close_chat", nameValuePairs);
		if ((lsData = ParseInfoResponse(lsRet)).length() > 0)
			;

		return (GlobalData.mLastServerError == AntRetStatus.OK);
	}

}