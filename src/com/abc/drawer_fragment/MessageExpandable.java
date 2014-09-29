package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.app.ExpandableListActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.abc.model.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MessageExpandable extends Fragment {

	ExpandableListView exTV;
	SavedTabsListAdapter adapter;
	public boolean[] groupChecked;
	public boolean[][] childChecked;
	protected List<ParseObject> peoples;

	public MessageExpandable() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.message_expandable, container, false);

		exTV = (ExpandableListView) v.findViewById(R.id.expandableListView);

		loadPeopleFromParse();
		return v;
	}

	private void loadPeopleFromParse() {

		ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Client"); // get
		// Parse
		// table:Client
		query1.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects,
					com.parse.ParseException e) {
				if (e == null) { // put resule into a variable:clientNames
					peoples = objects;
					Log.d("debug", "objects.size()=" + objects.size());

					Map<String, List<ParseObject>> tagPeople = getPeopleData();

					Set<String> tags = tagPeople.keySet();

					String[] tagArray = new String[tags.size()];

					tagArray = tags.toArray(tagArray);
					adapter = new SavedTabsListAdapter();
					adapter.setData(tagPeople, tagArray);
					exTV.setAdapter(adapter);

				}

			}
		});

	}

	public String getName() {

		String phoneNum = "";

		for (int i = 0; i < adapter.groups.length; i++) {
			for (int j = 0; j < adapter.tagPeople.get(adapter.groups[i]).size(); j++) {
				Log.d("debug", "phone numbes:" + i + "," + j + ":"
						+ childChecked[i][j]);
				if (childChecked[i][j] == true || groupChecked[i]) {
					phoneNum += adapter.tagPeople.get(adapter.groups[i]).get(j)
							.getString("name") + ",";
				}
			}
		}
		return phoneNum;
	}
	
	
	public String getPhoneNumbers(){
		
		String phoneNum = "";

		for (int i = 0; i < adapter.groups.length; i++) {
			for (int j = 0; j < adapter.tagPeople.get(adapter.groups[i]).size(); j++) {
				Log.d("debug", "phone numbes:" + i + "," + j + ":"
						+ childChecked[i][j]);
				if (childChecked[i][j] == true || groupChecked[i]) {
					phoneNum += adapter.tagPeople.get(adapter.groups[i]).get(j)
							.getString("tel") + ",";
				}
			}
		}
		return phoneNum;
		
	}

	protected Map<String, List<ParseObject>> getPeopleData() {
		Map<String, List<ParseObject>> tagPeople = new HashMap<String, List<ParseObject>>();
		if (peoples != null) {
			for (ParseObject people : peoples) {

				List<ParseObject> ppl = tagPeople.get(people.getString("tag"));
				if (ppl == null) {
					ppl = new ArrayList<ParseObject>();
					tagPeople.put(people.getString("tag"), ppl);
				}
				ppl.add(people);

			}

		}

		return tagPeople;
	}

	public class SavedTabsListAdapter extends BaseExpandableListAdapter {


		private Map<String, List<ParseObject>> tagPeople;
		private String[] groups;

		public void setData(Map<String, List<ParseObject>> tagPeople,
				String[] tagArray) {
			this.tagPeople = tagPeople;
			this.groups = tagArray;
			groupChecked = new boolean[groups.length];

			childChecked = new boolean[groups.length][];
			for (int i = 0; i < childChecked.length; i++) {
				childChecked[i] = new boolean[tagPeople.get(groups[i]).size()];

			}
		}

		
		
		@Override
		public int getGroupCount() {
			return groups.length;
		}

		@Override
		public int getChildrenCount(int i) {

			return tagPeople.get(groups[i]).size();
		}

		@Override
		public Object getGroup(int i) {
			return groups[i];
		}

		@Override
		public Object getChild(int i, int i1) {
			return tagPeople.get(groups[i]).get(i1);
		}

		@Override
		public long getGroupId(int i) {
			return i;
		}

		@Override
		public long getChildId(int i, int i1) {
			return i1;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(final int i, boolean b, View view,
				ViewGroup viewGroup) {
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			View v = inflater.inflate(R.layout.message_expandable_1, null);

			TextView groupTV = (TextView) v.findViewById(R.id.textView1);
			groupTV.setText(groups[i]);

			CheckBox groupCB = (CheckBox) v.findViewById(R.id.checkBox1);
			groupCB.setChecked(groupChecked[i]);
			groupCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub
					groupChecked[i] = isChecked;
					SavedTabsListAdapter.this.notifyDataSetChanged();
				}
			});

			return v;
		}

		@Override
		public View getChildView(final int i, final int i1, boolean b,
				View view, ViewGroup viewGroup) {
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			View v = inflater.inflate(R.layout.message_expandable_item, null);

			TextView childTV = (TextView) v.findViewById(R.id.textView1);
			childTV.setText(tagPeople.get(groups[i]).get(i1).getString("name"));

			CheckBox childCB = (CheckBox) v.findViewById(R.id.checkBox1);

			childCB.setChecked(groupChecked[i]);
			childCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					// TODO Auto-generated method stub

					childChecked[i][i1] = isChecked;
				}
			});
			return v;
		}

		@Override
		public boolean isChildSelectable(int i, int i1) {
			return true;
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

}