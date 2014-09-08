package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.abc.model.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ClientNoteList extends Fragment {

	public ClientNoteList() {
	}

	ListView listView;
	Button addEvent;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.client_listview, container, false);
		listView = (ListView) v.findViewById(R.id.listView1);
		addEvent=(Button) v.findViewById(R.id.button1);
		loadDataFromParse();
		addEvent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
				.replace(R.id.content_frame, new ClientNote()).commit();
			}
		});
		return v;
	}

	private void loadDataFromParse() {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				"ClientNote");
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
				for (ParseObject ob : objects) {
					Map<String, String> item = new HashMap<String, String>();
					item.put("title", ob.getString("title"));
					item.put("client", ob.getString("client"));

					data.add(item);
				}
				SimpleAdapter adapter = new SimpleAdapter(getActivity(), data,
						android.R.layout.simple_list_item_2, new String[] {
								"title", "client" }, new int[] {
								android.R.id.text1, android.R.id.text2 });
				listView.setAdapter(adapter);
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

}
