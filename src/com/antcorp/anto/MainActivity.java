package com.antcorp.anto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabContentFactory;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.data.TagInfo;
import com.antcorp.anto.database.DBOperator;
import com.antcorp.anto.fragment_n_adapter.ActiveFragment;
import com.antcorp.anto.fragment_n_adapter.ConnectionFragment;
import com.antcorp.anto.fragment_n_adapter.MyBagFragment;
import com.antcorp.anto.fragment_n_adapter.MyTag;
import com.antcorp.anto.fragment_n_adapter.Notification;
import com.antcorp.anto.fragment_n_adapter.NotifictionFragment;
import com.antcorp.anto.interfaces.ServiceCallBackBase;
import com.antcorp.anto.location.CollectLocation;
import com.antcorp.anto.nfc.NFCActivity;
import com.antcorp.anto.service.AntNotificationService;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.widget.NoOwnerDialog;
import com.antcorp.anto.widget.PagerAdapter;
import com.antcorp.anto.widget.TextTabVIew;
import com.antcorp.anto.widget.TimeoutProgressDialog;
import com.antcorp.anto.R;

public class MainActivity extends NFCActivity implements
		TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
	class TabFactory implements TabContentFactory {
		private final Context mContext;

		public TabFactory(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);

			v.setMinimumWidth(0);
			v.setMinimumHeight(0);

			return v;
		}

	}

	@SuppressWarnings("unused")
	private class TabInfo {
		private String tag;
		private Class<?> clss;
		private Bundle args;
		private Fragment fragment;

		TabInfo(String tag, Class<?> clazz, Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}
	}

	class WebResponse extends ServiceCallBackBase {
		public WebResponse() {
			super(MainActivity.this);

		}

		@Override
		public void AntOWebResponseAddToBagSuccess(int piRetCode) {
			if (piRetCode == 0) {

				MainActivity.this.getMyBagInfo();
				GlobalData.m_current_tagInfo = null;// clear global tag info
													// after success add
				MainActivity.this.setPageTo(0);
			} else
				Toast.makeText(MainActivity.this, R.string.add_to_my_bag_fail,
						Toast.LENGTH_SHORT).show();

		}

		@Override
		public void AntOWebResponseChangeTagName(int piRetCode) {
			if (piRetCode == 0) {

				MainActivity.this.getMyBagInfo();

			} else
				Toast.makeText(MainActivity.this, R.string.rename_item_fail,
						Toast.LENGTH_SHORT).show();

		}

		@Override
		public void AntOWebResponseGetNotifyList(int piRetCode,
				ArrayList<Notification> pMyNoti) {
			if (piRetCode == 0) {
				bringUpChatFromIntent();
				if (GlobalData.notifictionFragment != null) {
					GlobalData.notifictionFragment.onResume();

					
				}

			}

		}

		@Override
		public void AntOWebResponseGetTagInfo(int piRetCode, TagInfo taginfo) {
			pd.dismiss();
			if (piRetCode == 0) {
				if (taginfo == null)
					return;
				Context context = MainActivity.this;
				if (taginfo.getAntOwner() != null) {
					Intent intent = new Intent(context, TagInfoActivity.class);
					// intent.putExtra("json", jsonString);
					context.startActivity(intent);
				} else {
					NoOwnerDialog nod = new NoOwnerDialog();
					MyTag t = new MyTag("", taginfo.getDataTag().getTagid(),
							taginfo.getDataTag().getTagType(), "", false,
							taginfo.getDataTag().getNfcUid());

					nod.ShowDialog(MainActivity.this, t, "Add");

				}
			} else {
				// this code doesnot belong to antcorp
				Toast.makeText(MainActivity.this, R.string.id_not_found,
						Toast.LENGTH_SHORT).show();

			}

		}

		@Override
		public void AntOWebResponseLogOff(int piRetCode) {
			if (piRetCode == 0) {

				// clean data
				mBoundService.SetCallBackFunc(null);
				GlobalData.CleanUp();

				if (mConnection != null)
					unbindService(mConnection);

				mBoundService = null;

				// if logoff successful close mainactivity and open login page
				MainActivity.this.finish();
				Intent i = new Intent(MainActivity.this,
						LoginBaseActivity.class);
				// set the flag
				i.putExtra("fromLogoff", true);
				MainActivity.this.startActivity(i);

			}

		}

		@Override
		public void AntOWebResponseMyBag(int piRetCode, ArrayList<MyTag> pMyBag) {

			// update my bag, connection and active screen
			if (GlobalData.bagFragment != null) {
				GlobalData.bagFragment.onResume();
			}
			if (GlobalData.connectionFragment != null) {
				GlobalData.connectionFragment.onResume();
			}
			if (GlobalData.activeFragment != null) {
				GlobalData.activeFragment.onResume();
			}

		}

		@Override
		public void AntOWebResponseRemoveAllNotifications(int piRetCode) {
			if (piRetCode == 0) {

				MainActivity.this.getNotiList();

			} else
				Toast.makeText(MainActivity.this, R.string.remove_all_msg_fail,
						Toast.LENGTH_SHORT).show();

		}

		@Override
		public void AntOWebResponseRemoveNotification(int piRetCode) {
			pd.dismiss();

		}

		@Override
		public void AntOWebResponseRemoveTag(int piRetCode) {
			if (GlobalData.bagFragment != null) {
				GlobalData.bagFragment.onResume();
			}
			if (GlobalData.connectionFragment != null) {
				GlobalData.connectionFragment.onResume();
			}

		}

	}
	private static void AddTab(MainActivity activity, TabHost tabHost,
			TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
		tabHost.addTab(tabSpec);
	}

	Intent mIntentBkgService = null;

	TimeoutProgressDialog pd;

	Handler handler = new Handler();

	final int GPS_RETRIEVE_TIME = 180000; // update GPS location every 3min

	WebResponse mWebCall = new WebResponse();

	// private GoogleMap mMap;
	// LatLng ll = null;
	private TabHost mTabHost;
	private ViewPager mViewPager;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, MainActivity.TabInfo>();

	private PagerAdapter mPagerAdapter;

	CollectLocation cl;

	final String ANTCORP_PROFIX = "Ant-O:";

	AlertDialog ad;

	boolean flag_fromViewPagerScroll = true;

	AntNotificationService mBoundService;

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mBoundService = ((AntNotificationService.LocalBinder) service)
					.getService();

			mBoundService.SetCallBackFunc(mWebCall);
			// mBoundService.GetMyBagInfo(); //Steven edit, to avoid clear data

			if (GlobalData.m_NFCIntent != null) {
				onNewIntent(GlobalData.m_NFCIntent);
				GlobalData.m_NFCIntent = null;
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;

			MyLog.i("Binding", "called onServiceDisconnected");
		}

	};

	public void AddToBag(String taginfo, String tagname, String Uid) {
		if (mBoundService == null)
			return;

		mBoundService.RegisterAsMine(taginfo, tagname, 1, Uid);

	}

	public void bringUpChatFromIntent() {
		if (GlobalData.m_ChatIntent != null) {
			if (GlobalData.m_ChatIntent.hasExtra("noti_id")) {
				String noti_id = GlobalData.m_ChatIntent
						.getStringExtra("noti_id");
				for (Notification noti : GlobalData.m_notifications) {
					if (noti.getNotificationId().equals(noti_id)) {
						Intent intent = new Intent(this, ChatActivity.class);
						intent.putExtra("tagid", noti.getTagid());
						intent.putExtra("notiId", noti_id);
						intent.putExtra("uid", noti.getUid());
						intent.putExtra("sender_name", noti.getSender_name());
						intent.putExtra("sender_surname",
								noti.getSender_surname());
						intent.putExtra("owner_name", noti.getAbout_name());
						intent.putExtra("owner_surname",
								noti.getAbout_surname());
						intent.putExtra("owner_count",
								noti.getMsg_count_owner());
						intent.putExtra("sender_count",
								noti.getMsg_count_sender());
						intent.putExtra("max_count", noti.getMsg_max());
						this.startActivity(intent);
						GlobalData.m_ChatIntent = null;
					}
				}
			}

			// clean intent after use

			
		}

	}

	public void changeTagName(String tagid, String tagname) {
		if (mBoundService == null)
			return;

		mBoundService.ChangeTagName(tagid, tagname);
	}

	private void confirmExit() {
		AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
		adb.setTitle("Quit");
		adb.setMessage("Quit Ant-O?");
		adb.setPositiveButton("Quit", new DialogInterface.OnClickListener() {//
					@Override
					public void onClick(DialogInterface dialog, int i) {

						GlobalData.CleanUp();

						if (mBoundService != null) {
							mBoundService.SetCallBackFunc(null);
							mBoundService.StopAllServices();
						}

						if (mConnection != null) {
							unbindService(mConnection);
						}

						if (mIntentBkgService != null) {
							stopService(mIntentBkgService);
							mIntentBkgService = null;
						}

						mBoundService = null;

						MainActivity.this.finish();//

					}
				});

		adb.setNeutralButton("Logoff", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mBoundService != null) {

					pd.setMessage("Logoff...");
					pd.show();
					mBoundService.LogOff();
				}
			}
		});

		ad = adb.show();
	}

	public void getMyBagInfo() {

		if (mBoundService == null)
			return;
		mBoundService.GetMyBagInfo();
	}

	public void getNotiList() {
		if (mBoundService == null)
			return;

		mBoundService.GetNotifyListOpen();
	}

	public void getTagInfo(String id, String Uid) {
		if (mBoundService == null)
			return;

		pd.setMessage("Reading tag...");
		pd.show();
		mBoundService.GetTagInfo(id, Uid);

	}

	private void init(Bundle savedInstanceState) {
		// Initialise the TabHost
		this.initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

		// Intialise ViewPager
		this.intialiseViewPager();
	}

	private void initGpsLocation() {
		cl = new CollectLocation(this);
		// cl.getLocation();

		updateLocation();
	}

	/**
	 * Initialise the Tab Host
	 */
	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;

		TextTabVIew myBagTab = new TextTabVIew(this, "");
		TextTabVIew connTab = new TextTabVIew(this, "");
		TextTabVIew notifiTab = new TextTabVIew(this, "");
		TextTabVIew activeTab = new TextTabVIew(this, "");

		MainActivity.AddTab(this, this.mTabHost,
				this.mTabHost.newTabSpec("My bag").setIndicator(myBagTab),
				(tabInfo = new TabInfo("My bag", MyBagFragment.class, args)));

		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		MainActivity.AddTab(this, this.mTabHost,
				this.mTabHost.newTabSpec("Connections").setIndicator(connTab),
				(tabInfo = new TabInfo("Connections", ConnectionFragment.class,
						args)));

		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		MainActivity.AddTab(
				this,
				this.mTabHost,
				this.mTabHost.newTabSpec("Notifications").setIndicator(
						notifiTab), (tabInfo = new TabInfo("Notifications",
						NotifictionFragment.class, args)));

		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		MainActivity.AddTab(this, this.mTabHost,
				this.mTabHost.newTabSpec("Active").setIndicator(activeTab),
				(tabInfo = new TabInfo("Active", ActiveFragment.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);

		this.mTabHost.setCurrentTab(0);
		MyLog.i("	this.mTabHost.setCurrentTab(0);");

		mTabHost.getTabWidget().setEnabled(false);

		// Default to first tab
		// this.onTabChanged("Tab1");
		//
		mTabHost.setOnTabChangedListener(this);
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// menu.clear();
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.remove_bag_menu, menu);
	// return super.onCreateOptionsMenu(menu);
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case R.id.remove_bag:
	// removeFromBag();
	// break;
	// }
	// return true;
	// }

	/**
	 * Initialise ViewPager
	 */
	private void intialiseViewPager() {

		List<Fragment> fragments = new Vector<Fragment>();
		fragments
				.add(Fragment.instantiate(this, MyBagFragment.class.getName()));
		fragments.add(Fragment.instantiate(this,
				ConnectionFragment.class.getName()));
		fragments.add(Fragment.instantiate(this,
				NotifictionFragment.class.getName()));
		fragments
				.add(Fragment.instantiate(this, ActiveFragment.class.getName()));

		PagerAdapter adapter = new PagerAdapter(
				super.getSupportFragmentManager(), fragments);
		this.mPagerAdapter = adapter;

		this.mViewPager = (ViewPager) super.findViewById(R.id.viewpager);
		this.mViewPager.setAdapter(this.mPagerAdapter);
		this.mViewPager.setOnPageChangeListener(this);

		this.mViewPager.post(new Runnable() {
			public void run() {
				// guarded viewPager.setCurrentItem
				MainActivity.this.mViewPager.setCurrentItem(0);
				MyLog.i("	MainActivity.this.mViewPager.setCurrentItem(0);");

				setFirstColony();
			}
		});

	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		// this.getParent().onBackPressed();
		confirmExit();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		init(savedInstanceState);

		pd = new TimeoutProgressDialog(this);
		pd.setCanceledOnTouchOutside(false);
		pd.setCancelable(false);

		mIntentBkgService = new Intent(this, AntNotificationService.class);
		startService(mIntentBkgService);
		bindService(mIntentBkgService, mConnection, Context.BIND_AUTO_CREATE);

		initGpsLocation();

		// store the current version
		if (GlobalData.m_current_version != null) {
			DBOperator db = new DBOperator(this);
			db.writeCurrentVersion(GlobalData.m_current_version);
		}
		// setUpMapIfNeeded();

		// if(getIntent().hasExtra("nfc")){
		// MyLog.i("##########i'm here#########   "+getIntent().toString());
		// onNewIntent(getIntent());
		// }

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cl.endGPSUpdate();

	}

	@Override
	protected void onNewIntent(Intent intent) {

		super.onNewIntent(intent);

		MyLog.i("***************new Intent detected, the value is    "
				+ intent.toString());
		MyLog.i("***************nfc tag detected, the value is    " + readText);

		if (readText.startsWith(ANTCORP_PROFIX)) {
			// this code belong to antcorp
			MyLog.i("resolveAntTag(readText.substring(ANTCORP_PROFIX.length()),readUid);");
			resolveAntTag(readText.substring(ANTCORP_PROFIX.length()), readUid);
			readText = "";
			;
		} else {
			// this code doesnot belong to antcorp
			Toast.makeText(MainActivity.this, R.string.id_not_found,
					Toast.LENGTH_SHORT).show();

		}

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	@Override
	public void onPageSelected(int position) {

		if (this.mTabHost.getCurrentTab() != position) {
			flag_fromViewPagerScroll = false;
			this.mTabHost.setCurrentTab(position);
			MyLog.i("	this.mTabHost.setCurrentTab(  " + position);
			flag_fromViewPagerScroll = true;
		}
	}

	@Override
	protected void onPause() {

		super.onPause();
		// cl.endGPSUpdate();
	}

	@Override
	protected void onResume() {
		super.onResume();
		bringUpChatFromIntent();

	}

	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("tab", mTabHost.getCurrentTabTag());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		if (mBoundService != null) {
			mBoundService.SetCallBackFunc(mWebCall);

			if (GlobalData.m_NFCIntent != null) {
				onNewIntent(GlobalData.m_NFCIntent);
				GlobalData.m_NFCIntent = null;
			}

		}
	}

	@Override
	public void onTabChanged(String tag) {
		// TabInfo newTab = this.mapTabInfo.get(tag);
		int pos = this.mTabHost.getCurrentTab();

		if (flag_fromViewPagerScroll) {
			if (this.mViewPager.getCurrentItem() != pos) {
				this.mViewPager.setCurrentItem(pos);
				MyLog.i("	MainActivity.this.mViewPager.setCurrentItem(;" + pos);
			}
		}
	}

	public void removeAllNotifications() {
		if (mBoundService == null)
			return;
		pd.setMessage("Removing...");
		pd.show();
		mBoundService.RemoveAllNotifications();
	}

	public void removeFromBag(final MyTag m_tag) {
		AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this);
		adb.setTitle("Confirm Remove");
		adb.setMessage("This process cannot be undone. Are you sure?");
		adb.setPositiveButton("Sure", new DialogInterface.OnClickListener() {//
					@Override
					public void onClick(DialogInterface dialog, int i) {

						removeFromBagConfirmed(m_tag);
					}
				});

		adb.setNeutralButton("No", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ad.dismiss();
			}
		});

		ad = adb.show();
	}

	protected void removeFromBagConfirmed(MyTag m_tag) {
		if (mBoundService == null)
			return;

		mBoundService.RemoveTag(m_tag.getTagId());

	}

	private void resolveAntTag(String id, String Uid) {
		MyLog.i("		ReadTagIdLogic rd = new ReadTagIdLogic(this, id,Uid);");
		ReadTagIdLogic rd = new ReadTagIdLogic(this, id, Uid);
		rd.startLogic();
	}

	// guide the user to set the first colony if there is nothing in server
	private void setFirstColony() {
		if (GlobalData.m_colonys.size() < 1) {
			// if set the first colony, take user to colony page
			setPageTo(1);
			Intent i = new Intent(MainActivity.this,
					EditTagFromRegiActivity.class);
			i.putExtra("isUserOwner", true);
			i.putExtra("name", GlobalData.m_antOUser.name);
			i.putExtra("surname", GlobalData.m_antOUser.surname);
			startActivity(i);
		}

	}

	public void setPageTo(int i) {
		this.mViewPager.setCurrentItem(i);
		MyLog.i("	MainActivity.this.mViewPager.setCurrentItem(" + i);
		this.mTabHost.setCurrentTab(i);
		MyLog.i("	this.mTabHost.setCurrentTab(" + i);

	}

	// add this try to enable options menu
	@Override
	public void supportInvalidateOptionsMenu() {
		mViewPager.post(new Runnable() {

			@Override
			public void run() {
				MainActivity.super.supportInvalidateOptionsMenu();
			}
		});
	}

	private void updateLocation() {
		cl.getLocation();

		// Runnable run = new Runnable() {
		//
		// @Override
		// public void run() {
		// updateLocation() ;
		//
		// }
		// };
		//
		// handler.postDelayed(run, GPS_RETRIEVE_TIME);

	}

}
