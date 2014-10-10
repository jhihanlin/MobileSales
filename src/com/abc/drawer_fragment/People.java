package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.abc.model.R;
import com.abc.model.utils.TypeFaceHelper;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class People extends Fragment {

	public People() {
	}

	public ListView listView;
	public View v;
	public Button addPeople, imbtndel, imbtnupdata, importPeople, searchPeople;
	public Button peopleButton, tagButton;
	public AutoCompleteTextView autoComplete;
	public ArrayList<HashMap<String, String>> contactsArrayList;
	public String[] contactsName;
	private ProgressDialog dialog;
	private EditText edtname, edtbirthday, edttel, edtemail, edtadd, edttag,
			edtnote, edtsname;
	public int size = 0;
	public boolean start = true;
	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.people_layout, container, false);

		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());

		listView = (ListView) v.findViewById(R.id.lvPEOPLE);
		importPeople = (Button) v.findViewById(R.id.importPeople);
		addPeople = (Button) v.findViewById(R.id.addPeople);
		searchPeople = (Button) v.findViewById(R.id.btnsearch);
		searchPeople.setTypeface(typeface);
		edtsname = (EditText) v.findViewById(R.id.editTextname);
		TextView peoplelist_tx = (TextView) v.findViewById(R.id.peoplelist_tx);
		peoplelist_tx.setTypeface(typeface);

		progressDialog = new ProgressDialog(getActivity());
		getParseDate("");

		peopleButton = (Button) v.findViewById(R.id.peopleButton);
		peopleButton.setTypeface(typeface);
		tagButton = (Button) v.findViewById(R.id.tagButton);
		tagButton.setTypeface(typeface);
		peopleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().getFragmentManager().beginTransaction()
						.replace(R.id.content_frame, new People()).commit();
			}
		});

		tagButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getActivity().getFragmentManager().beginTransaction()
						.replace(R.id.content_frame, new People_tag()).commit();
			}
		});

		addPeople.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				People_add ppadd = new People_add();
				ppadd.setMode("add");
				Fragment fg = ppadd;
				getActivity().getFragmentManager().beginTransaction()
						.replace(R.id.content_frame, fg).commit();
			}
		});

		importPeople.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(getActivity())
						.setTitle("確定要匯入手機聯絡人")
						.setPositiveButton("確認",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										progressDialog.setCancelable(false);
										progressDialog.setTitle("Loading...");
										progressDialog.show();

										Thread thread = new Thread() {
											@Override
											public void run() {
												try {
													setParseData();

												} catch (Exception e) {
													e.printStackTrace();
												} finally {
												}
											}
										};

										thread.start();
										Toast.makeText(getActivity(), "匯入成功",
												Toast.LENGTH_LONG).show();
										getActivity().getFragmentManager().beginTransaction()
												.replace(R.id.content_frame, new People()).commit();
										progressDialog.dismiss();
									}
								}).setNegativeButton("取消", null).show();
			}
		});
		searchPeople.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				progressDialog.setCancelable(false);
				progressDialog.setTitle("Loading...");
				progressDialog.show();
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");

				query.findInBackground(new FindCallback<ParseObject>() {
					@Override
					public void done(List<ParseObject> arg0, ParseException arg1) {
						progressDialog.dismiss();
						// TODO Auto-generated method stub
						if (arg1 == null) {
							Log.v("", "SIZE:" + arg0.size() + " scores");
							if (arg0.size() == 0) {

							} else {
								contactsArrayList.clear();
								for (int i = 0; i < arg0.size(); i++) {
									if (edtsname.getText().equals("")) {
										HashMap<String, String> hm = new HashMap<String, String>();
										hm.put("ID", arg0.get(i).getObjectId()
												.toString());
										hm.put("NAME", arg0.get(i).get("name")
												.toString());
										hm.put("NUMBER", arg0.get(i).get("tel")
												.toString());
										contactsArrayList.add(hm);
									} else {
										if (arg0.get(i).getString("name")
												.toString()
												.contains(edtsname.getText())) {

											HashMap<String, String> hm = new HashMap<String, String>();
											hm.put("ID", arg0.get(i)
													.getObjectId().toString());
											hm.put("NAME",
													arg0.get(i).get("name")
															.toString());
											hm.put("NUMBER",
													arg0.get(i).get("tel")
															.toString());
											contactsArrayList.add(hm);
										}
									}

								}

								if (contactsArrayList.size() == 0) {

								}

								SimpleAdapter adapter = new SimpleAdapter(
										getActivity(), contactsArrayList,
										R.layout.people_tag_entry,
										new String[] { "NAME", "NUMBER" },
										new int[] { R.id.txtNAMEPHONE,
												R.id.txtDATAPHONE });
								listView.setAdapter(null);
								listView.setAdapter(adapter);
							}
						} else {
							Log.v("score", "Error: " + arg1.getMessage());
						}
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

	public void setListView() {
		SimpleAdapter adapter = new SimpleAdapter(getActivity(),
				contactsArrayList, R.layout.people_tag_entry, new String[] {
						"NAME", "NUMBER" }, new int[] { R.id.txtNAMEPHONE,
						R.id.txtDATAPHONE });
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				final String Oid = contactsArrayList.get(position).get("ID");
				final String name = contactsArrayList.get(position).get("NAME");
				final String number = contactsArrayList.get(position).get(
						"NUMBER");
				new AlertDialog.Builder(getActivity())
						.setTitle(number)
						.setItems(new String[] { "詳細資料", "撥打電話" },
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										switch (which) {
										case 0:
											People_add ppadd = new People_add();
											ppadd.setMode("edit");
											ppadd.setID(Oid);

											Fragment fg = ppadd;
											getActivity()
													.getFragmentManager()
													.beginTransaction()
													.replace(
															R.id.content_frame,
															fg).commit();

											break;

										case 1:
											Intent call = new Intent(
													Intent.ACTION_CALL, Uri
															.parse("tel:"
																	+ number));
											startActivity(call);

											break;
										}

									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									}
								}).show();
			}

		});

	}

	public void getParseDate(final String name) {
		contactsArrayList = new ArrayList<HashMap<String, String>>();
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");

		if (!name.equals("")) {
			query.whereEqualTo("name", name);
		}

		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				progressDialog.dismiss();
				if (e == null) {
					contactsName = new String[objects.size()];
					if (name.equals("")) {
						size = objects.size();
					}

					for (int i = 0; i < objects.size(); i++) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("ID", objects.get(i).getObjectId().toString());
						hm.put("NAME", objects.get(i).get("name").toString());
						hm.put("NUMBER", objects.get(i).get("tel").toString());
						contactsArrayList.add(hm);
						contactsName[i] = objects.get(i).get("name").toString();
					}

					setListView();

					if (start) {
						start = false;
					}

				} else {
					Log.v("score", "Error: " + e.getMessage());
				}
			}
		});
		query.clearCachedResult();

	}

	public void setParseData() {
		String id;
		String mimetype;

		ContentResolver contentResolver = getActivity().getContentResolver();

		Cursor cursor = contentResolver
				.query(android.provider.ContactsContract.Contacts.CONTENT_URI,
						new String[] { android.provider.ContactsContract.Contacts._ID },
						null, null, null);
		while (cursor.moveToNext()) {

			id = cursor
					.getString(cursor
							.getColumnIndex(android.provider.ContactsContract.Contacts._ID));

			Cursor contactInfoCursor = contentResolver.query(
					android.provider.ContactsContract.Data.CONTENT_URI,
					new String[] {
							android.provider.ContactsContract.Data.CONTACT_ID,
							android.provider.ContactsContract.Data.MIMETYPE,
							android.provider.ContactsContract.Data.DATA1 },
					android.provider.ContactsContract.Data.CONTACT_ID + "="
							+ id, null, null);
			String name = "";
			String email = "";
			String phone = "";
			String postal = "";
			String birthday = "";
			while (contactInfoCursor.moveToNext()) {
				mimetype = contactInfoCursor
						.getString(contactInfoCursor
								.getColumnIndex(android.provider.ContactsContract.Data.MIMETYPE));
				String value = contactInfoCursor
						.getString(contactInfoCursor
								.getColumnIndex(android.provider.ContactsContract.Data.DATA1));
				if (mimetype.contains("/name")) {
					System.out.println("Name=" + value);
					name = value;
				} else if (mimetype.contains("/email")) {
					System.out.println("Email=" + value);
					email = value;
				} else if (mimetype.contains("/phone")) {
					System.out.println("Tel=" + value);
					phone = value;
				} else if (mimetype.contains("/postal")) {
					System.out.println("Address=" + value);
					postal = value;
				} else if (mimetype.contains("/birthday")) {
					System.out.println("birthday=" + value);
					birthday = value;
				}

				progressDialog.dismiss();

				Log.v("", "" + mimetype);

			}
			ParseObject testObject = new ParseObject("Client");
			testObject.put("name", name);
			testObject.put("email", email);
			testObject.put("tel", phone);
			testObject.put("add", postal);
			testObject.put("birthday", birthday);
			testObject.put("tag", "");
			testObject.setACL(new ParseACL(ParseUser.getCurrentUser()));
			testObject.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException ex) {
					// TODO Auto-generated method stub
					if (ex == null) {

					} else {

					}
				}
			});
			testObject = null;
			System.out.println("*********");
			contactInfoCursor.close();
		}
		cursor.close();
	}

	private void setContentView(int peopleAdd) {
		// TODO Auto-generated method stub

	}
}