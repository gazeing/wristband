package com.antcorp.anto.fragment_n_adapter;

import com.antcorp.anto.MainActivity;
import com.antcorp.anto.ReadTagIdLogic;
import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.widget.NoOwnerDialog;
import com.antcorp.anto.widget.StartBrowser;
import com.antcorp.anto.R;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MyBagFragment extends Fragment implements OnItemClickListener,
		OnItemLongClickListener/*, Observer*/ ,DialogInterface.OnDismissListener{

	private RelativeLayout ll;
	private FragmentActivity fa;

	ListView listview;
	MyBagAdapter adapter1;


	boolean buttonIsEnabled = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fa = super.getActivity();
		// Intent intent = fa.getIntent();
		ll = (RelativeLayout) inflater.inflate(R.layout.fragment_mybag,
				container, false);
		init();

		return ll;
	}

	@Override
	public void onResume() {

		super.onResume();
		adapter1.notifyDataSetChanged();
		listview.setEnabled(true);
	}

	private void init() {
		listview = (ListView) ll.findViewById(R.id.listView1);
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		adapter1 = new MyBagAdapter(fa, GlobalData.m_tags);
		listview.setAdapter(adapter1);

		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		


		ImageButton btn_buy = (ImageButton) ll.findViewById(R.id.button_buy);
		if (btn_buy != null)
			btn_buy.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String url = "http://pozi.be/anto";
					StartBrowser sb = new StartBrowser(url, fa);
					MyLog.i("StartBrowser: " + url);
					sb.startBrowse();
				}
			});

		GlobalData.bagFragment = this;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) 
	{
			parent.setEnabled(false);
			MyTag tag = GlobalData.m_tags.get(position);
			ReadTagIdLogic rt = new ReadTagIdLogic((MainActivity) fa, tag.tagId,"");

			rt.setOd(this);
			rt.startLogic();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		MyTag tag = GlobalData.m_tags.get(position);
		NoOwnerDialog nd = new NoOwnerDialog();
		nd.ShowDialog((MainActivity) fa, tag, "Save");

		return false;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		listview.setEnabled(true);
		
	}
}
