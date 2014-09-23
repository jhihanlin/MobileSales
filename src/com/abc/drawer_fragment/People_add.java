package com.abc.drawer_fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.DatePicker;

import com.abc.model.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class People_add extends Fragment {
	private Button savePeople, delPeople, imbtnupdata, backPeople, editPeople,
			birthPeople;
	private EditText edtname, edtbirthday, edttel, edtemail, edtadd, edtnote;
	protected List<ParseObject> tag;
	public ArrayList<String> TagArrayList;
	Spinner sptag;
	public String mode = "";
	public String ID = "";
	public String textData = "";
	public DatePicker DatePicker;
	Calendar c = null;
	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		Calendar TodayDate = Calendar.getInstance();
		int sYear = TodayDate.get(Calendar.YEAR);
		int sMon = TodayDate.get(Calendar.MONTH) + 1;

		int sDay = TodayDate.get(Calendar.DAY_OF_MONTH);

		textData = DateFix(sYear) + "/" + DateFix(sMon) + "/" + DateFix(sDay);

		View v = inflater.inflate(R.layout.people_add, container, false);

		progressDialog = new ProgressDialog(getActivity());

		editPeople = (Button) v.findViewById(R.id.editPeople);
		savePeople = (Button) v.findViewById(R.id.savePeople);
		delPeople = (Button) v.findViewById(R.id.deletePeople);
		backPeople = (Button) v.findViewById(R.id.backPeople);

		edtname = (EditText) v.findViewById(R.id.edtname);
		// edtbirthday = (EditText) v.findViewById(R.id.edtbirthday);
		edttel = (EditText) v.findViewById(R.id.edttel);
		edtemail = (EditText) v.findViewById(R.id.edtemail);
		edtadd = (EditText) v.findViewById(R.id.edtaddress);
		sptag = (Spinner) v.findViewById(R.id.sptag);
		edtnote = (EditText) v.findViewById(R.id.edtnote);

		birthPeople = (Button) v.findViewById(R.id.datepickerButton);
		DatePicker = (DatePicker) v.findViewById(R.id.datePicker1);
		DatePicker.init(TodayDate.get(Calendar.YEAR),
				TodayDate.get(Calendar.MONTH),
				TodayDate.get(Calendar.DAY_OF_MONTH),
				new DatePicker.OnDateChangedListener() {
					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						String Year = DateFix(year);
						String Mon = DateFix(monthOfYear + 1);
						String Day = DateFix(dayOfMonth);
						textData = Year + "/" + Mon + "/" + Day;
					}
				});

		if (mode.equals("add")) {
			// progressDialog.setCancelable(false);
			// progressDialog.setTitle("Loading...");
			// progressDialog.show();
			delPeople.setVisibility(8);
			editPeople.setVisibility(8);
			// progressDialog.dismiss();
		}
		if (mode.equals("edit")) {

			savePeople.setVisibility(8);
			edtname.setEnabled(false);
			// edtbirthday = (EditText) v.findViewById(R.id.edtbirthday);
			edttel.setEnabled(false);
			edtemail.setEnabled(false);
			edtadd.setEnabled(false);
			sptag.setEnabled(false);
			edtnote.setEnabled(false);
			DatePicker.setEnabled(false);

		}
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Tag"); // get
		// Parse
		// table:ClientNote
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects,
					com.parse.ParseException e) {
				progressDialog.dismiss();
				TagArrayList = new ArrayList<String>();
				if (e == null) { // put resule into a variable:clientNotes
					tag = objects;
					if (tag != null) {
						for (ParseObject purposeObject : tag) {
							TagArrayList.add(purposeObject.getString("name"));
							Log.d("TagArrayList", TagArrayList.toString());

						}
					}else{
						//tag資料庫沒資料預設空值
						TagArrayList.add("");
					}
					ArrayAdapter<String> purposeAdapter = new ArrayAdapter<String>(
							getActivity(),
							android.R.layout.simple_spinner_item, TagArrayList);
					purposeAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_item);
					sptag.setAdapter(purposeAdapter);

					if (mode.equals("edit")) {
						progressDialog.setCancelable(false);
						progressDialog.setTitle("Loading...");
						progressDialog.show();
						ParseQuery<ParseObject> query = ParseQuery
								.getQuery("Client");

						query.getInBackground(ID,
								new GetCallback<ParseObject>() {// GUERY

									public void done(ParseObject gameScore,
											ParseException e) {
										progressDialog.dismiss();
										if (e == null) {
											edtname.setText(gameScore
													.get("name") == null ? ""
													: gameScore.get("name")
															.toString());
											// edtbirthday.setText(gameScore.get("birthday")==null?"":gameScore.get("birthday").toString());
											edttel.setText(gameScore.get("tel") == null ? ""
													: gameScore.get("tel")
															.toString());
											edtemail.setText(gameScore
													.get("email") == null ? ""
													: gameScore.get("email")
															.toString());
											edtadd.setText(gameScore.get("add") == null ? ""
													: gameScore.get("add")
															.toString());
											for (int i = 0; i < TagArrayList
													.size(); i++) {
												if (TagArrayList
														.get(i)
														.toString()
														.equals(gameScore
																.get("tag") == null ? ""
																: gameScore
																		.get("tag")
																		.toString())) {
													sptag.setSelection(i);
												}
											}

										}
									}
								});
						query.clearCachedResult();
					}

				}
			}
		});
		query.clearCachedResult();

		editPeople.setOnClickListener(new OnClickListener() {

			// ADD
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editPeople.setVisibility(8);
				savePeople.setVisibility(0);
				edtname.setEnabled(true);
				// edtbirthday = (EditText) v.findViewById(R.id.edtbirthday);
				edttel.setEnabled(true);
				edtemail.setEnabled(true);
				edtadd.setEnabled(true);
				sptag.setEnabled(true);
				edtnote.setEnabled(true);
				DatePicker.setEnabled(true);
			}
		});

		savePeople.setOnClickListener(new OnClickListener() {

			// ADD
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (mode.equals("add")) {
					btnadd();
				}
				if (mode.equals("edit")) {
					btnedit();
				}

			}
		});

		delPeople.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDeleteDialog();
			}
		});

		backPeople.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, new People()).commit();
			}
		});

		return v;

	}

	// showDeleteDialog(data, position, adapter);

	public void showDeleteDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Delete");
		builder.setPositiveButton("Delete",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						btndel();
					}
				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.show();
	}

	public String DateFix(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	public boolean checktext() {
		boolean check = true;

		if (edttel.getText().toString().equals("")) {

			Toast.makeText(getActivity(), "please enter phone numbers",
					Toast.LENGTH_LONG).show();
			check = false;
		}

		if (edtadd.getText().toString().equals("")) {

			Toast.makeText(getActivity(), "please enter address",
					Toast.LENGTH_LONG).show();
			check = false;
		}

		return check;
	}

	public void btnadd() {

		ParseObject testObject = new ParseObject("Client");
		testObject.put("name", edtname.getText().toString());
		testObject.put("birthday", textData);
		testObject.put("tel", edttel.getText().toString());
		testObject.put("email", edtemail.getText().toString());
		testObject.put("add", edtadd.getText().toString());
		String tag = sptag.getSelectedItem().toString();
		testObject.put("tag", tag);
		testObject.put("note", edtnote.getText().toString());

		if (checktext()) {
			testObject.setACL(new ParseACL(ParseUser.getCurrentUser()));

			testObject.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException ex) {
					// TODO Auto-generated method stub
					if (ex == null) {

						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.content_frame, new People())
								.commit();
					} else {

					}
				}
			});
		}
	}

	public void btnedit() {
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
		Log.v("", "mode=" + mode);
		Log.v("", "ID=" + ID);

		query.getInBackground(ID, new GetCallback<ParseObject>() {// GUERY

					public void done(ParseObject gameScore, ParseException e) {
						if (e == null) {
							progressDialog.dismiss();
							// Now let's update it with some new data. In this
							// case, only cheatMode and score
							// will get sent to the Parse Cloud. playerName
							// hasn't changed.
							gameScore.put("name", edtname.getText().toString());
							gameScore.put("birthday", textData);
							gameScore.put("tel", edttel.getText().toString());
							gameScore.put("email", edtemail.getText()
									.toString());
							gameScore.put("add", edtadd.getText().toString());
							String tag = sptag.getSelectedItem().toString();
							gameScore.put("tag", tag);
							gameScore.put("note", edtnote.getText().toString());

							gameScore.saveInBackground(new SaveCallback() {

								@Override
								public void done(ParseException ex) {
									// TODO Auto-generated method stub
									if (ex == null) {

										FragmentManager fragmentManager = getFragmentManager();
										fragmentManager
												.beginTransaction()
												.replace(R.id.content_frame,
														new People()).commit();
									} else {

									}
								}
							});
						}
					}
				});
	}

	public void btndel() {
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
		Log.v("", "mode=" + mode);
		Log.v("", "ID=" + ID);

		query.getInBackground(ID, new GetCallback<ParseObject>() {// GUERY

					public void done(ParseObject gameScore, ParseException e) {
						progressDialog.dismiss();
						if (e == null) {
							gameScore.deleteInBackground(new DeleteCallback() {

								@Override
								public void done(ParseException ex) {
									// TODO Auto-generated method stub
									if (ex == null) {

										FragmentManager fragmentManager = getFragmentManager();
										fragmentManager
												.beginTransaction()
												.replace(R.id.content_frame,
														new People()).commit();
									} else {

									}
								}
							});
						}
					}
				});
	}

	public void setMode(String setmode) {
		this.mode = setmode;
	}

	public void setID(String setid) {
		this.ID = setid;
	}
}
