package com.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beanclasses.NotificationBean;

import java.util.HashMap;
import java.util.List;

public class NotifyExpandableListAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<String> _listDataSubHeader;
	private List<String> _listDataFinalHeader;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	public static HashMap<String, List<NotificationBean>> _listDataChild;
	public static HashMap<String, List<NotificationBean>> _listDataSubChild;

	public NotifyExpandableListAdapter(Context context, List<String> listDataHeader,
                                       HashMap<String, List<NotificationBean>> listChildData,
									   List<String> listDataSubHeader,
									   HashMap<String, List<NotificationBean>> listSubChildData) {
		_listDataChild = new HashMap<String, List<NotificationBean>>();
		_listDataSubChild = new HashMap<String, List<NotificationBean>>();

		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
		this._listDataSubHeader = listDataSubHeader;
		this._listDataSubChild = listSubChildData;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.get(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		//final NotificationBean childText = (NotificationBean) getChild(
		//		groupPosition, childPosition);

		/*if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(
					R.layout.notify_child_item, null);
		}

		TextView txtchildtext = (TextView) convertView
				.findViewById(R.id.notify_text_footer);

		txtchildtext.setText(childText.getStationName());
		return convertView;*/

		final NotificationBean childText = (NotificationBean) getChild(
				groupPosition, childPosition);
		TextView tv = new TextView(_context);
		tv.setText(childText.getStationName());
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(20);
		tv.setPadding(15, 5, 5, 5);
		tv.setBackgroundColor(Color.YELLOW);
		tv.setLayoutParams(new ListView.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		//return tv
		//_listDataSubHeader.add(childText.getStationName());
		/*NotifyExpandableListAdapter.CustExpListview SecondLevelexplv = new NotifyExpandableListAdapter.CustExpListview(_context);
		SecondLevelexplv.setAdapter(new NotifyExpandableListAdapter.SecondLevelAdapter(_context,childPosition, _listDataSubHeader,_listDataSubChild));
		//notifyDataSetChanged();
		SecondLevelexplv.setGroupIndicator(null);*/

		return tv;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return (this._listDataChild
				.get(this._listDataHeader.get(groupPosition)).size());
		//return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		/*if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(
					R.layout.notify_header_item, null);
		}

		TextView lblListHeader = (TextView) convertView
				.findViewById(R.id.notify_text_header);
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);

		return convertView;*/
		TextView tv = new TextView(_context);
		tv.setText(headerTitle);
		tv.setTextColor(Color.BLACK);
		tv.setTextSize(20);
		tv.setBackgroundColor(Color.BLUE);
		tv.setPadding(10, 7, 7, 7);

		return tv;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public static class CustExpListview extends ExpandableListView {

		int intGroupPosition, intChildPosition, intGroupid;

		public CustExpListview(Context context) {
			super(context);
		}

		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(960,
					MeasureSpec.AT_MOST);
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(960,
					MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public static class SecondLevelAdapter extends BaseExpandableListAdapter {
		Context _context;
		int _headerposition;
		private List<String> _listDataChildHeader;
		private HashMap<String, List<NotificationBean>> _listFinalChildData;

		public SecondLevelAdapter(Context context,int headerposition, List<String> listDataChildHeader,
								  HashMap<String, List<NotificationBean>> listFinalChildData) {
			_listFinalChildData = new HashMap<String, List<NotificationBean>>();

			this._context = context;
			this._headerposition= headerposition;
			this._listDataChildHeader = listDataChildHeader;
			this._listFinalChildData = listFinalChildData;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return this._listFinalChildData.get(this._listDataChildHeader.get(_headerposition))
					.get(childPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
								 boolean isLastChild, View convertView, ViewGroup parent) {
			final NotificationBean childText = (NotificationBean) getChild(
					_headerposition, childPosition);
			TextView tv = new TextView(_context);
			tv.setText(childText.getMessage());
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(20);
			tv.setPadding(15, 5, 5, 5);
			tv.setBackgroundColor(Color.GREEN);
			tv.setLayoutParams(new ListView.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
			return tv;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return (this._listFinalChildData
					.get(this._listDataChildHeader.get(_headerposition)).size());
		}

		@Override
		public Object getGroup(int groupPosition) {
			return this._listDataChildHeader.get(_headerposition);
		}

		@Override
		public int getGroupCount() {
			return this._listDataChildHeader.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return _headerposition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
								 View convertView, ViewGroup parent) {
			String headerTitle = (String) getGroup(_headerposition);
			TextView tv = new TextView(_context);
			tv.setText(headerTitle);
			tv.setTextColor(Color.BLACK);
			tv.setTextSize(20);
			tv.setPadding(12, 7, 7, 7);
			tv.setBackgroundColor(Color.RED);

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

	}

}