package com.adapters;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.beanclasses.SounsLevelCalibrationstdBean;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	public static HashMap<String, List<SounsLevelCalibrationstdBean>> _listDataChild;

	public ExpandableListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<SounsLevelCalibrationstdBean>> listChildData) {
		_listDataChild = new HashMap<String, List<SounsLevelCalibrationstdBean>>();
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
	}

	/*public ExpandableListAdapter(MaterialReqAckList context,
								 List<String> listDataHeader,
								 HashMap<String,List<String>> listDataChild) {

		_listDataChild = new HashMap<String, List<String>>();
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listDataChild;
	}*/

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

		final SounsLevelCalibrationstdBean childText = (SounsLevelCalibrationstdBean) getChild(
				groupPosition, childPosition);

		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(com.stavigilmonitoring.R.layout.soundlevelcalibrationstditem, null);
		}

		TextView txtchildtime = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.callibrationtimeDisplay);
		TextView txtChildstd = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.calibrationStdDispaly);
		TextView txtChildvol = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.calibrationVolumeDisplay);
		TextView txtChildsys = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.SysVolumeDisplay);

		txtchildtime.setText(childText.getCTIME());
		txtChildstd.setText(childText.getStandard());
		txtChildvol.setText(childText.getCallibrationVolume());
		txtChildsys.setText(childText.getSystemVolume());
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return (this._listDataChild
				.get(this._listDataHeader.get(groupPosition)).size());
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
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(com.stavigilmonitoring.R.layout.soundlevel_cal_date_item, null);
		}

		TextView lblListHeader = (TextView) convertView.findViewById(com.stavigilmonitoring.R.id.txtdate);
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}