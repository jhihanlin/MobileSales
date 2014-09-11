package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.abc.model.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ClientNoteView extends Fragment {
	protected List<ParseObject> clientName;
	protected List<ParseObject> purposeName;
	Calendar c = null;

	int c_index;
	int p_index;
	String[] remindTime = new String[] { "10 minutes ago", "15 minutes ago",
			"30 minutes ago", "1 hour ago", "3 hours ago", "12 hours ago",
			"1 day ago" };
	public ClientNoteView() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.client_note_view, container, false);
		final ProgressDialog progressDialog = new ProgressDialog(getActivity());// loading
		// bar
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		
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
		final Spinner getPurpose = (Spinner) v.findViewById(R.id.view_purposeSpinner);
		final Button getDateButton = (Button) v.findViewById(R.id.view_date);
		final Button getTimeButton = (Button) v.findViewById(R.id.view_time);
		EditText getContent = (EditText) v.findViewById(R.id.view_content);
		EditText getLocation = (EditText) v.findViewById(R.id.view_location);
		Spinner getRemind = (Spinner) v.findViewById(R.id.view_remind);
		EditText getRemarks = (EditText) v.findViewById(R.id.view_remarks);
		getTitle.setText(list.get(0).get("title"));
		final String client = list.get(0).get("client");
		final String purpose = list.get(0).get("purpose");
		String date = list.get(0).get("date");
		String time = list.get(0).get("time");
		String content = list.get(0).get("content");
		getContent.setText(content);
		String location = list.get(0).get("location");
		getLocation.setText(location);
		String remarks = list.get(0).get("remarks");
		getRemarks.setText(remarks);
		
		getDateButton.setText(date);
		getTimeButton.setText(time);

		getDateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onCreateDialog(getDateButton).show();
			}
		});
		getTimeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onCreateDialog2(getTimeButton).show();
			}
		});

		

		LoadClientNameSpinner(getClient, client);
		LoadPurposeSpinner(getPurpose, purpose, progressDialog);

		ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_spinner_item,
				remindTime);
		adapterTime
				.setDropDownViewResource(android.R.layout.simple_spinner_item);
		getRemind.setAdapter(adapterTime);
		
		return v;
	}

	private void LoadPurposeSpinner(final Spinner getPurpose, final String purpose, final ProgressDialog progressDialog) {
		ParseQuery<ParseObject> queryPurpose = new ParseQuery<ParseObject>(
				"Purpose");
		queryPurpose.findInBackground(new FindCallback<ParseObject>() {
			ArrayList<String> purposeArrayList;

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {

					try {
						purposeArrayList = new ArrayList<String>();
						purposeName = objects;
						if (purposeName != null) {
							for (ParseObject purposeObject : purposeName) {
								if (purposeObject.getString("name") != null)
									purposeArrayList.add(purposeObject
											.getString("name"));
								Log.d("purposeArrayList",
										purposeArrayList.toString());

							}
							p_index = purposeArrayList.indexOf(purpose);
							Log.d("pIndexOf", "index" + p_index);

						}
						ArrayAdapter<String> purposeNameAdapter = new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_spinner_item,
								purposeArrayList);
						purposeNameAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_item);
						getPurpose.setAdapter(purposeNameAdapter);
						getPurpose.setSelection(p_index, true);
						progressDialog.dismiss();

					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});
	}

	private void LoadClientNameSpinner(final Spinner getClient, final String client) {
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
							c_index = clientNameArrayList.indexOf(client);
							Log.d("clientNameIndexOf", "index" + c_index);

						}
						ArrayAdapter<String> clientNameAdapter = new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_spinner_item,
								clientNameArrayList);
						clientNameAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_item);
						getClient.setAdapter(clientNameAdapter);
						getClient.setSelection(c_index, true);

					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});
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

	protected Dialog onCreateDialog(final Button btn) {
		Dialog dialog = null;
		c = Calendar.getInstance();
		dialog = new DatePickerDialog(getActivity(),
				new DatePickerDialog.OnDateSetListener() {
					public void onDateSet(DatePicker dp, int year, int month,
							int dayOfMonth) {
						String text = String.format("%d/%02d/%02d", year,
								(month + 1), dayOfMonth);
						btn.setText(text);
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH));
		return dialog;
	}

	protected Dialog onCreateDialog2(final Button btn) {
		Dialog dialog2 = null;
		c = Calendar.getInstance();
		dialog2 = new TimePickerDialog(getActivity(),
				new TimePickerDialog.OnTimeSetListener() {
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						btn.setText(hourOfDay + ":" + minute);
					}
				}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
		return dialog2;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

}
