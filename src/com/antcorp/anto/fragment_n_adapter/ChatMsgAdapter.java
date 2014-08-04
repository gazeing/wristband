package com.antcorp.anto.fragment_n_adapter;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import com.antcorp.anto.data.GlobalData;
import com.antcorp.anto.widget.MyLog;
import com.antcorp.anto.widget.UtilStatics;
import com.antcorp.anto.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

@SuppressWarnings("unused")
public class ChatMsgAdapter extends ArrayAdapter<ChatMsg> implements
		StickyListHeadersAdapter, SectionIndexer {

	private ArrayList<ChatMsg> chatMsgs;
	private Context context;

	private int[] sectionIndices;
	private String[] sectionsDates;

	public ChatMsgAdapter(Context context, ArrayList<ChatMsg> objects) {
		super(context, R.layout.item_chat_fromme, objects);
		this.context = context;
		this.chatMsgs = objects;
		sectionIndices = getSectionIndices();
		sectionsDates = getSectionsDates();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMsg chat = chatMsgs.get(position);

		boolean isFromMe = chat.sender_id.equals(GlobalData.m_antOUser.id);
		View v = convertView;

		LayoutInflater vi = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (isFromMe) {
			v = vi.inflate(R.layout.item_chat_fromme, null);
		} else {
			v = vi.inflate(R.layout.item_chat_fromother, null);
		}
		TextView chatName = (TextView) v.findViewById(R.id.textView_name);
		if (chatName != null) {
			if (isFromMe)
				chatName.setText("");
			else
				chatName.setText(chat.sendername);
		}

		TextView chatTime = (TextView) v.findViewById(R.id.textView_time);
		if (chatTime != null)
			chatTime.setText(getTimeFromString(chat.time));

		TextView chatMsg = (TextView) v.findViewById(R.id.textView_msg);
		if (chatMsg != null)
			chatMsg.setText(chat.msg);
		return v;
	}

	@Override
	public int getPositionForSection(int section) {
		if (section >= sectionIndices.length) {
			section = sectionIndices.length - 1;
		} else if (section < 0) {
			section = 0;
		}
		return sectionIndices[section];
	}

	@Override
	public int getSectionForPosition(int position) {
		for (int i = 0; i < sectionIndices.length; i++) {
			if (position < sectionIndices[i]) {
				return i - 1;
			}
		}
		return sectionIndices.length - 1;
	}

	@Override
	public Object[] getSections() {

		return sectionsDates;
	}

	class HeaderViewHolder {
		TextView text1;
	}

	class ViewHolder {
		TextView text;
	}

	@Override
	public int getCount() {
		return chatMsgs.size();
	}

	@Override
	public ChatMsg getItem(int position) {
		return chatMsgs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;
		if (convertView == null) {
			holder = new HeaderViewHolder();

			LayoutInflater vi = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.list_header, null);
			holder.text1 = (TextView) convertView
					.findViewById(R.id.list_header_title);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}

		String headerChar = getDate(position);
		
		holder.text1.setText(headerChar);
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		String date = getDate(position);
		int id = 0;
		for (String d : sectionsDates) {
			if (date.equals(d))
				return id;
			else
				id++;
		}

		return id;
	}

	public void clear() {
		sectionIndices = new int[0];
		sectionsDates = new String[0];
		chatMsgs = new ArrayList<ChatMsg>();
		notifyDataSetChanged();
	}

	public void restore() {
		chatMsgs = new ArrayList<ChatMsg>();
		sectionIndices = getSectionIndices();
		sectionsDates = getSectionsDates();
		notifyDataSetChanged();
	}

	private String[] getSectionsDates() {

		String[] dates = new String[sectionIndices.length];
		if (chatMsgs.size() > 0) {
			for (int i = 0; i < sectionIndices.length; i++) {
				dates[i] = getDateFromString(chatMsgs.get(sectionIndices[i]).time);
			}
		}
		return dates;
	}

	private int[] getSectionIndices() {
		ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
		String lastDate = getDate(0);
		sectionIndices.add(0);
		for (int i = 1; i < chatMsgs.size(); i++) {
			if (!getDate(i).equals(lastDate)) {
				lastDate = getDate(i);
				sectionIndices.add(i);
			}
		}
		int[] sections = new int[sectionIndices.size()];
		for (int i = 0; i < sectionIndices.size(); i++) {
			sections[i] = sectionIndices.get(i);
		}
		return sections;
	}

	private String getDate(int position) {
		if (chatMsgs.size() > position)
			return UtilStatics.TransferTimeFormatToDate(UtilStatics
					.getLongFromServerTimeFormat(chatMsgs.get(position).time));
		else
			return "";
	}

	private String getTime(int position) {
		if (chatMsgs.size() > position)
			return UtilStatics.TransferTimeFormatToTime(UtilStatics
					.getLongFromServerTimeFormat(chatMsgs.get(position).time));
		else
			return "";
	}

	private String getTimeFromString(String timeString) {

		return UtilStatics.TransferTimeFormatToTime(UtilStatics
				.getLongFromServerTimeFormat(timeString));
	}

	private String getDateFromString(String timeString) {

		return UtilStatics.TransferTimeFormatToDate(UtilStatics
				.getLongFromServerTimeFormat(timeString));
	}

}
