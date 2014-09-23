package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

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

public class People_tag extends Fragment {

	public Button peopleButton, tagButton, btntagadd;
	private EditText editname;
	public ListView listView;
	public View v;
	public ArrayList<HashMap<String, Object>> contactsArrayList;
	private ProgressDialog progressDialog;
	public TextView tagName, m1, m2;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.people_tag, container, false);
		Typeface typeface = Typeface.createFromAsset(getActivity()
				.getAssets(), "fonts/Quicksand-Regular.ttf");// font
		
		peopleButton = (Button) v.findViewById(R.id.peopleButton);
		tagButton = (Button) v.findViewById(R.id.tagButton);
		listView = (ListView) v.findViewById(R.id.lvTAGPEOPLE);
		btntagadd = (Button) v.findViewById(R.id.button1);
		editname = (EditText) v.findViewById(R.id.editText1);
		progressDialog = new ProgressDialog(getActivity());
		getParseTagDate();

		tagName = (TextView) v.findViewById(R.id.tagName);
		m1 = (TextView) v.findViewById(R.id.message_tx1);
		m1.setTypeface(typeface);
		m2 = (TextView) v.findViewById(R.id.message_tx2);
		m2.setTypeface(typeface);
		m1.setVisibility(0);
		m2.setVisibility(8);
		tagName.setVisibility(8);

		peopleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, new People()).commit();
			}
		});

		tagButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, new People_tag()).commit();
			}
		});

		btntagadd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				People_tag_add ppadd = new People_tag_add();
				ppadd.setTag("");
				ppadd.setMode("add");
				Fragment fg = (Fragment) ppadd;
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, fg).commit();

			}
		});

		return v;
	}

	public void setListView() {
		People_lv_BtnAdapter Btnadapter = new People_lv_BtnAdapter(
				getActivity(), contactsArrayList,
				R.layout.people_contact_entry,
				new String[] { "NAME", "NUMBER" }, new int[] {
						R.id.txtNAMEPHONE, R.id.txtDATAPHONE,
						R.id.group_list_item_text }, getFragmentManager());
		listView.setAdapter(Btnadapter);

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				final String Oid = contactsArrayList.get(arg2).get("ID")
						.toString();
				final String name = contactsArrayList.get(arg2).get("NAME")
						.toString();
				final String number = contactsArrayList.get(arg2).get("NUMBER")
						.toString();
				new AlertDialog.Builder(getActivity())
						.setTitle("Delete")
						.setPositiveButton("Done",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// User clicked OK button
										ParseQuery<ParseObject> querytag = ParseQuery
												.getQuery("Tag");
										querytag.whereEqualTo("name", number);
										querytag.findInBackground(new FindCallback<ParseObject>() {
											@Override
											public void done(
													List<ParseObject> objects,
													ParseException e) {
												if (e == null
														&& objects.size() > 0) {
													objects.get(0)
															.deleteInBackground(
																	new DeleteCallback() {

																		@Override
																		public void done(
																				ParseException ex) {
																			// TODO
																			// Auto-generated
																			// method
																			// stub
																			if (ex == null) {

																			} else {

																			}
																		}
																	});
												}
											}
										});

										ParseQuery<ParseObject> query = ParseQuery
												.getQuery("Client");
										query.whereEqualTo("tag", number);
										query.findInBackground(new FindCallback<ParseObject>() {
											@Override
											public void done(
													List<ParseObject> objects,
													ParseException e) {
												if (e == null
														&& objects.size() > 0) {
													// objects.get(0).deleteInBackground();
													for (int i = 0; i < objects
															.size(); i++) {
														objects.get(i).put(
																"tag", "");

														objects.get(i)
																.saveInBackground(
																		new SaveCallback() {

																			@Override
																			// �u����
																			public void done(
																					ParseException ex) {
																				// TODO
																				// Auto-generated
																				// method
																				// stub
																				if (ex == null) {

																				} else {

																				}
																			}
																		});
													}

												}
											}
										});

										FragmentManager fragmentManager = getFragmentManager();
										fragmentManager
												.beginTransaction()
												.replace(R.id.content_frame,
														new People_tag())
												.commit();

									}
								}).setNegativeButton("Cancel", null).show();
				return true;
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				final String Oid = contactsArrayList.get(position).get("ID")
						.toString();
				final String name = contactsArrayList.get(position).get("NAME")
						.toString();
				final String number = contactsArrayList.get(position)
						.get("NUMBER").toString();

				People_tag_list ppadd = new People_tag_list();
				ppadd.setTag(number);
				Fragment fg = (Fragment) ppadd;
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, fg).commit();

			}

		});
	};

	public void getParseTagDate() {
		contactsArrayList = new ArrayList<HashMap<String, Object>>();

		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		ParseQuery<ParseObject> querytag = ParseQuery.getQuery("Tag");
		querytag.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					progressDialog.dismiss();
					String[] tag = new String[objects.size()];
					for (int tagi = 0; tagi < objects.size(); tagi++) {
						HashMap<String, Object> hm = new HashMap<String, Object>();
						// hm.put("ID",objects.get(tagi).getObjectId().toString());
						// hm.put("NAME", "tag");
						// hm.put("NUMBER",
						// objects.get(tagi).get("name").toString());
						hm.put("ID", objects.get(tagi).getObjectId().toString());
						hm.put("NAME", "tag");
						hm.put("NUMBER", objects.get(tagi).get("name")
								.toString());
						Log.v("score", ": "
								+ objects.get(tagi).get("name").toString());
						contactsArrayList.add(hm);
					}
					// getParseDate(tag);
					if (contactsArrayList.size() > 0) {
						// tag��Ʈw�S�Ȥ��n��slistview
						setListView();
					}

				} else {
					Log.v("score", "Error: " + e.getMessage());
				}

			}
		});
		// Log.v("", ""+tag.length);

	}

	public void setParseData() {
		String id;
		String mimetype;

		ContentResolver contentResolver = getActivity().getContentResolver();
		// �u�ݭn�qContacts�����ID�A��L�����i�H���n�A�q�L�d�ݤW���sĶ�᪺SQL�y�y�A�i�H�ݥX�N�ĤG�ӰѼ�
		// �]�m��null�A�q�{��^���C�D�`�h�A�O�@�ظ귽���O�C
		Cursor cursor = contentResolver
				.query(android.provider.ContactsContract.Contacts.CONTENT_URI,
						new String[] { android.provider.ContactsContract.Contacts._ID },
						null, null, null);
		while (cursor.moveToNext()) {
			ParseObject testObject = new ParseObject("Client");
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
			while (contactInfoCursor.moveToNext()) {
				mimetype = contactInfoCursor
						.getString(contactInfoCursor
								.getColumnIndex(android.provider.ContactsContract.Data.MIMETYPE));
				String value = contactInfoCursor
						.getString(contactInfoCursor
								.getColumnIndex(android.provider.ContactsContract.Data.DATA1));
				if (mimetype.contains("/name")) {
					System.out.println("Name=" + value);
					testObject.put("name", value);
				} else if (mimetype.contains("/email")) {
					System.out.println("Email=" + value);
					testObject.put("email", value);
				} else if (mimetype.contains("/phone")) {
					System.out.println("Tel=" + value);
					testObject.put("tel", value);
				} else if (mimetype.contains("/postal")) {
					System.out.println("Address=" + value);
					testObject.put("add", value);
				} else if (mimetype.contains("/birthday")) {
					System.out.println("birthday=" + value);
					testObject.put("birthday", value);
				}
				progressDialog.dismiss();

				// testObject.put("ID",
				// ParseUser.getCurrentUser().getObjectId());
				testObject.setACL(new ParseACL(ParseUser.getCurrentUser()));
				Log.v("", "" + mimetype);

			}
			testObject.saveInBackground(new SaveCallback() {
				@Override
				// �u����
				public void done(ParseException ex) {
					// TODO Auto-generated method stub
					if (ex == null) {

					} else {

					}
				}
			});

			System.out.println("*********");
			contactInfoCursor.close();
		}
		cursor.close();
	}

	private void setContentView(int peopleAdd) {
		// TODO Auto-generated method stub

	}

	public class People_lv_BtnAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, Object>> mAppList;
		private LayoutInflater mInflater;
		private Context mContext;
		private String[] keyString;
		private int[] valueViewID;
		private ItemView itemView;
		private List<String> ls;
		public ArrayList<HashMap<String, Object>> contactsArrayList;
		public FragmentManager fragmentManager;
		public boolean checksave;
		private ProgressDialog progressDialog;

		private class ItemView {
			TextView tvname;
			TextView tvnumber;
			TextView tvtag;

		}

		public People_lv_BtnAdapter(Context c,
				ArrayList<HashMap<String, Object>> appList, int resource,
				String[] from, int[] to, FragmentManager fm) {
			mAppList = appList;
			mContext = c;
			fragmentManager = fm;
			mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			keyString = new String[from.length];
			valueViewID = new int[to.length];

			System.arraycopy(from, 0, keyString, 0, from.length);
			System.arraycopy(to, 0, valueViewID, 0, to.length);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// return 0;
			return mAppList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			// return null;
			return mAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			// return 0;
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// return null;

			if (convertView != null) {
				itemView = (ItemView) convertView.getTag();
			} else {
				convertView = mInflater
						.inflate(R.layout.people_tag_entry, null);
				itemView = new ItemView();
				itemView.tvname = (TextView) convertView
						.findViewById(valueViewID[0]);
				itemView.tvnumber = (TextView) convertView
						.findViewById(valueViewID[1]);
				itemView.tvtag = (TextView) convertView
						.findViewById(valueViewID[2]);
				convertView.setTag(itemView);
			}

			// HashMap<String, Object> appInfo = mAppList.get(position);
			if (mAppList != null) {

				if (mAppList.get(position).get(keyString[0]).toString()
						.equals("tag")) {
					itemView.tvtag.setText(mAppList.get(position)
							.get(keyString[1]).toString());
					itemView.tvtag.setEnabled(false);
					itemView.tvtag.setVisibility(0);
					itemView.tvname.setVisibility(8);
					itemView.tvnumber.setVisibility(8);
				} else {
					// itemView.tvtag.setEnabled(true);
					// itemView.tvtag.setVisibility(8);
					// itemView.tvname.setVisibility(0);
					// itemView.tvnumber.setVisibility(0);
					// itemView.tvname.setText(mAppList.get(position).get(keyString[0]).toString());
					// itemView.tvnumber.setText(mAppList.get(position).get(keyString[1]).toString());
				}

			}

			return convertView;
		}

	}

}
