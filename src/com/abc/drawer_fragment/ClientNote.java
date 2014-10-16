package com.abc.drawer_fragment;

import android.app.Fragment;

import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.abc.drawer_fragment.ClientNoteUtils.ClientNoteOnItemSelectedListener;
import com.abc.model.R;
import com.abc.model.utils.TypeFaceHelper;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ClientNote extends Fragment {

	EditText m_titleText;
	EditText m_contentText;
	EditText m_locationText;
	EditText m_remarksText;
	Spinner m_purpose;
	Spinner m_remind;
	Spinner m_client;
	Button m_datepickerButton = null;
	Button m_timepickerButton = null;
	Button saveButton;
	Calendar c = null;
	protected List<ParseObject> purpose;
	protected List<ParseObject> clientName;

	private ProgressDialog progressDialog;

	String[] remindTime = new String[] { "10 minutes ago", "15 minutes ago",
			"30 minutes ago", "1 hour ago", "3 hours ago", "12 hours ago",
			"1 day ago" };

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.client_note_layout, container, false);
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());

		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();

		m_titleText = (EditText) v.findViewById(R.id.titleText);
		m_purpose = (Spinner) v.findViewById(R.id.purposeSpinner);
		m_client = (Spinner) v.findViewById(R.id.clientSpinner);
		m_contentText = (EditText) v.findViewById(R.id.contextText);
		m_locationText = (EditText) v.findViewById(R.id.locationText);
		m_remind = (Spinner) v.findViewById(R.id.remindSpinner);
		m_remarksText = (EditText) v.findViewById(R.id.remarksText);
		m_datepickerButton = (Button) v.findViewById(R.id.datepickerButton);
		m_timepickerButton = (Button) v.findViewById(R.id.timepickerButton);
		saveButton = (Button) v.findViewById(R.id.save);
		saveButton.setTypeface(typeface);
		Button cancelButton = (Button) v.findViewById(R.id.cancel);
		cancelButton.setTypeface(typeface);
		// remind Spinner
		ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_spinner_item,
				remindTime);
		adapterTime
				.setDropDownViewResource(android.R.layout.simple_spinner_item);
		m_remind.setAdapter(adapterTime);

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Purpose"); // get
																				// Parse
																				// table:ClientNote
		query.findInBackground(new FindCallback<ParseObject>() {
			ArrayList<String> purposArrayList;

			@Override
			public void done(List<ParseObject> objects,
					com.parse.ParseException e) {
				if (e == null) { // put resule into a variable:clientNotes

					try {
						progressDialog.dismiss();

						purposArrayList = new ArrayList<String>();
						purpose = objects;
						if (purpose != null) {
							for (ParseObject purposeObject : purpose) {
								if (purposeObject.getString("name") != null)
									purposArrayList.add(purposeObject
											.getString("name"));
								Log.d("purposeArrayList",
										purposArrayList.toString());

							}
						}
						if (purposArrayList.size() <= 0) {
							Log.d("debug", "purposArrayList" + purposArrayList.size());
							purposArrayList.add("::選擇目的::");
						}
						purposArrayList.add("《新增目的》");
						final ArrayAdapter<String> purposeAdapter = new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_spinner_item,
								purposArrayList);
						purposeAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_item);
						m_purpose.setAdapter(purposeAdapter);
						m_purpose.setOnItemSelectedListener(new ClientNoteUtils.ClientNoteOnItemSelectedListener(getActivity(), m_purpose));
						m_purpose.setOnLongClickListener(new OnLongClickListener() {

							@Override
							public boolean onLongClick(View v) {
								// if (purposeAdapter.getCount() - 1 ==
								// position) {
								// return true;
								// }
								// AlertDialog.Builder builder = new
								// AlertDialog.Builder(getActivity());
								// builder.setTitle("是否刪除");
								// builder.setPositiveButton("刪除", new
								// DialogInterface.OnClickListener() {
								//
								// @Override
								// public void onClick(DialogInterface dialog,
								// int which) {
								// final String pp =
								// purposeAdapter.getItem(position);
								// ParseQuery<ParseObject> query = new
								// ParseQuery<ParseObject>("Purpose");
								// query.whereEqualTo("name", pp);
								// query.findInBackground(new
								// FindCallback<ParseObject>() {
								//
								// @Override
								// public void done(List<ParseObject> objects,
								// ParseException e) {
								// for (ParseObject object : objects) {
								// object.deleteEventually();
								// purposeAdapter.remove(pp);
								// purposeAdapter.notifyDataSetChanged();
								// }
								// }
								// });
								// }
								// });
								// builder.setNegativeButton("取消", new
								// DialogInterface.OnClickListener() {
								//
								// @Override
								// public void onClick(DialogInterface dialog,
								// int which) {
								//
								// }
								// });
								// builder.show();
								Log.d("debug", "onLongClick");
								return true;
							}
						});
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});

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
						}
						ArrayAdapter<String> clientNameAdapter = new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_spinner_item,
								clientNameArrayList);
						clientNameAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_item);
						m_client.setAdapter(clientNameAdapter);
					} catch (Exception e2) {
						e2.printStackTrace();
					}

				}
			}
		});
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
		String str = formatter.format(curDate);

		m_datepickerButton.setText(str);
		Time t = new Time();
		t.setToNow();
		int hour = t.hour; // 0-23
		int minute = t.minute;
		m_timepickerButton.setText(hour + " : " + minute);

		// DatePickerDialog
		m_datepickerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onCreateDialog(m_datepickerButton).show();
			}
		});

		// TimePickerDialog
		m_timepickerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onCreateDialog2(m_timepickerButton).show();
			}
		});

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String title = m_titleText.getText().toString();
				String client = m_client.getSelectedItem().toString();
				String purpose = m_purpose.getSelectedItem().toString();
				Log.d("purpose", purpose);
				String content = m_contentText.getText().toString();
				String date = m_datepickerButton.getText().toString();
				String time = m_timepickerButton.getText().toString();
				String location = m_locationText.getText().toString();
				String remind = m_remind.getSelectedItem().toString();
				String remarks = m_remarksText.getText().toString();

				progressDialog.setCancelable(false);
				progressDialog.setTitle("Loading...");
				progressDialog.show();

				ParseObject object = new ParseObject("ClientNote");
				object.put("client", client);
				object.put("title", title);
				object.put("purpose", purpose);
				object.put("content", content);
				object.put("date", date);
				object.put("time", time);
				object.put("location", location);
				object.put("remind", remind);
				object.put("remarks", remarks);
				object.setACL(new ParseACL(ParseUser.getCurrentUser()));
				object.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						progressDialog.dismiss();
						if (e == null) {
							Toast.makeText(getActivity(), "儲存成功",
									Toast.LENGTH_SHORT).show();
							FragmentManager fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction()
									.replace(R.id.content_frame, new ClientNoteList()).commit();
						} else {
							Toast.makeText(getActivity(), "儲存失敗",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, new ClientNoteList())
						.commit();
			}
		});

		return v;
	}

	// DatePickerDialog
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

	// TimePickerDialog
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

}