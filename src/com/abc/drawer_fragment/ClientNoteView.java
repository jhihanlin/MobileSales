package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.abc.model.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ClientNoteView extends Fragment {
	protected List<ParseObject> clientName;
	int index;

	public ClientNoteView() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.client_note_view, container, false);

		Bundle arguments = getArguments();
		Log.d("bundle2", arguments.getBundle("bundle2").toString());
		Bundle bundle = arguments.getBundle("bundle2");
		ArrayList arrayList = new ArrayList();
		arrayList = bundle.getParcelableArrayList("arrayList");
		Log.d("arrayList", arrayList.toString());

		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		list = (ArrayList<Map<String, String>>) arrayList;
		Log.d("list", list.get(0).get("title").toString());

		if (arguments != null) {
			Log.d("BUNDLE != null", "NO NULL");
		} else {
			Log.d("BUNDLE == null", "NULL");
		}

		EditText getTitle = (EditText) v.findViewById(R.id.view_title);
		final Spinner getClient = (Spinner) v.findViewById(R.id.view_clientSpinner);
		Spinner getPurpose = (Spinner) v.findViewById(R.id.view_purposeSpinner);
		Button getDateButton = (Button) v.findViewById(R.id.view_date);
		Button getTimeButton = (Button) v.findViewById(R.id.view_time);
		EditText getContent = (EditText) v.findViewById(R.id.view_content);
		EditText getLocation = (EditText) v.findViewById(R.id.view_location);
		Spinner getRemind = (Spinner) v.findViewById(R.id.view_remind);
		EditText getRemarks = (EditText) v.findViewById(R.id.view_remarks);
		getTitle.setText(list.get(0).get("title"));
		final String client = list.get(0).get("client");
		Log.d("This name", client);

		final ProgressDialog progressDialog = new ProgressDialog(getActivity());// loading
		// bar
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();

		ParseQuery<ParseObject> queryClientName = new ParseQuery<ParseObject>(
				"Client"); 
		queryClientName.findInBackground(new FindCallback<ParseObject>() {
			ArrayList<String> clientNameArrayList;

			@Override
			public void done(List<ParseObject> objects,
					com.parse.ParseException e) {
				if (e == null) {

					try {
						clientNameArrayList = new ArrayList<String>();
						clientName = objects;
						if (clientName != null) {
							for (ParseObject clientNameObject : clientName) {
								if (clientNameObject.getString("name") != null)
									clientNameArrayList.add(clientNameObject
											.getString("name"));
								Log.d("clientNameArrayList",
										clientNameArrayList.toString());

							}
							index = clientNameArrayList.indexOf(client);
							Log.d("clientNameIndexOf", "index" + index);

						}
						ArrayAdapter<String> clientNameAdapter = new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_spinner_item,
								clientNameArrayList);
						clientNameAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_item);
						getClient.setAdapter(clientNameAdapter);
						getClient.setSelection(index, true);
						progressDialog.dismiss();


					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});
		Log.d("index", "index" + index);
		return v;
	}

	private void loadDataFromParse() {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				"ClientNote");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				for (ParseObject ob : objects) {
				}
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

}
