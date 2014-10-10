package com.abc.drawer_fragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.model.R;
import com.abc.model.utils.TypeFaceHelper;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ClientNoteGroupsByPurpose extends Fragment {

	public ClientNoteGroupsByPurpose() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.client_note_purpose, container, false);
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());
		final ListView ls = (ListView) v.findViewById(R.id.purpose_listview);
		final ProgressDialog progressDialog = new ProgressDialog(getActivity());

		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		ParseQuery<ParseObject> query_1 = new ParseQuery<ParseObject>(
				"Purpose");
		ParseObject ps=new ParseObject("ClientNote");
		query_1.orderByDescending("createdAt");
		query_1.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				progressDialog.dismiss();
				final ArrayList<String> arrayList = new ArrayList<String>();
				for (ParseObject ob : objects) {
					arrayList.add(ob.getString("name"));

				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_list_item_1, arrayList);
				ls.setAdapter(adapter);

				ls.setOnItemClickListener(new OnItemClickListener() {
					final ArrayList<Map<String, String>> plist = new ArrayList<Map<String, String>>();

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						final String p = arrayList.get(position);
						Log.d("debug", p);
						progressDialog.setCancelable(false);
						progressDialog.setTitle("Loading...");
						progressDialog.show();
						ParseQuery<ParseObject> query_2 = new ParseQuery<ParseObject>(
								"ClientNote");
						query_2.orderByDescending("date");

						query_2.findInBackground(new FindCallback<ParseObject>() {

							@Override
							public void done(List<ParseObject> objects, ParseException e) {
								progressDialog.dismiss();
								for (ParseObject ps : objects) {
									if (ps.getString("purpose").equals(p)) {
										Log.d("debug", "purpose==p" + ps.getString("title"));
										Map<String, String> item = new HashMap<String, String>();
										item.put("title", ps.getString("title"));
										item.put("date", ps.getString("date"));
										item.put("id", ps.getObjectId());
										plist.add(item);									}
								}
								if(plist.size()>0){
									
								}
								final SimpleAdapter adapter_2 = new SimpleAdapter(getActivity(),
										plist, R.layout.client_note_listview,
										new String[] { "title", "date" }, new int[] {
												R.id.clientNote_tx1, R.id.clientNote_tx2 });
								ls.setAdapter(adapter_2);
							}
						});

					}
				});
			}
		});

		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

}
