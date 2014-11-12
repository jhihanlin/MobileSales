package tw.edu.fju.imd.mobilesales.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tw.edu.fju.imd.mobilesales.R;
import tw.edu.fju.imd.mobilesales.utils.TypeFaceHelper;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class People_tag_list extends Fragment {

	public Button peopleButton, tagButton, btntagadd;
	public ListView listView;
	public View v;

	public ArrayList<HashMap<String, Object>> contactsArrayList;
	private ProgressDialog progressDialog;
	public String tag = "";
	public TextView tagName, m1, m2;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.people_tag, container, false);
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());

		peopleButton = (Button) v.findViewById(R.id.peopleButton);
		tagButton = (Button) v.findViewById(R.id.tagButton);
		listView = (ListView) v.findViewById(R.id.lvTAGPEOPLE);
		btntagadd = (Button) v.findViewById(R.id.button1);
		progressDialog = new ProgressDialog(getActivity());
		getParseDate(tag);

		tagName = (TextView) v.findViewById(R.id.tagName);
		m1 = (TextView) v.findViewById(R.id.message_tx1);
		m2 = (TextView) v.findViewById(R.id.message_tx2);

		if (tag.equals("")) {
			m1.setVisibility(0);
			m2.setVisibility(8);
		} else {
			m1.setVisibility(8);
			m2.setVisibility(0);
		}

		tagName.setText(tag);
		peopleButton.setTypeface(typeface);
		tagButton.setTypeface(typeface);

		peopleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().getFragmentManager()
						.beginTransaction()
						.replace(R.id.content_frame, new People())
						.addToBackStack(null)
						.commit();
			}
		});

		tagButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().getFragmentManager()
						.beginTransaction()
						.replace(R.id.content_frame, new People_tag())
						.addToBackStack(null)
						.commit();
			}
		});

		btntagadd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Thread thread = new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep(2000);
							People_tag_add ppadd = new People_tag_add();
							ppadd.setTag(tag);
							ppadd.setMode("gadd");

							Fragment fg = ppadd;
							getActivity().getFragmentManager()
									.beginTransaction()
									.replace(R.id.content_frame, fg)
									.addToBackStack(null)
									.commit();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
						}
					}
				};
				thread.start();
			}
		});

		return v;
	}

	public void setTag(String s) {
		tag = s;
	}

	public void setListView() {
		People_lv_BtnAdapter Btnadapter = new People_lv_BtnAdapter(
				getActivity(), contactsArrayList,
				R.layout.people_contact_entry,
				new String[] { "NAME", "NUMBER" }, new int[] {
						R.id.txtNAMEPHONE, R.id.txtDATAPHONE }, getFragmentManager());
		listView.setAdapter(Btnadapter);

		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				final String Oid = contactsArrayList.get(arg2).get("ID")
						.toString();
				final String name = contactsArrayList.get(arg2).get("NAME")
						.toString();
				final String number = contactsArrayList.get(arg2).get("NUMBER")
						.toString();
				new AlertDialog.Builder(getActivity())
						.setTitle("是否刪除")
						.setPositiveButton("刪除",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										progressDialog.setCancelable(false);
										progressDialog.setTitle("Loading...");
										progressDialog.show();
										ParseQuery<ParseObject> query = ParseQuery
												.getQuery("Client");

										query.getInBackground(Oid,
												new GetCallback<ParseObject>() {

													public void done(
															ParseObject gameScore,
															ParseException e) {

														progressDialog
																.dismiss();
														if (e == null) {

															gameScore.put(
																	"tag", "");

															gameScore
																	.saveInBackground(new SaveCallback() {

																		@Override
																		public void done(
																				ParseException ex) {
																			if (ex == null) {
																				Thread thread = new Thread() {
																					@Override
																					public void run() {

																						try {
																							Thread.sleep(300);
																							Message msg = new Message();
																							People_tag_list ppadd = new People_tag_list();
																							ppadd.setTag(tag);
																							Fragment fg = ppadd;
																							getActivity().getFragmentManager()
																									.beginTransaction()
																									.replace(
																											R.id.content_frame,
																											fg)
																									.addToBackStack(null)
																									.commit();
																						} catch (Exception e) {
																							e.printStackTrace();
																						} finally {
																						}
																					}
																				};
																				thread.start();

																			} else {

																			}
																		}
																	});
														}
													}
												});

									}
								}).setNegativeButton("取消", null).show();
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
											getActivity().getFragmentManager()
													.beginTransaction()
													.add(R.id.content_frame, ppadd)
													.addToBackStack(null)
													.commit();

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
	};

	public void getParseDate(final String tag) {
		contactsArrayList = new ArrayList<HashMap<String, Object>>();
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();

		ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");

		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				progressDialog.dismiss();
				if (e == null) {
					int index = 0;
					for (int i = 0; i < objects.size(); i++) {
						Log.v("score", "123: "
								+ objects.get(i).get("tag").toString() + "="
								+ tag);
						if (objects.get(i).get("tag").toString().equals(tag)) {
							HashMap<String, Object> hmp = new HashMap<String, Object>();
							hmp.put("ID", objects.get(i).getObjectId()
									.toString());
							hmp.put("NAME", objects.get(i).get("name")
									.toString());
							hmp.put("NUMBER", objects.get(i).get("tel")
									.toString());
							contactsArrayList.add(hmp);
							Log.v("score", "123: " + objects.get(i).get("tel"));
							index++;
						}
					}

					Log.v("score", "111: " + index);
					if (index > 0) {
						setListView();
					}

				} else {
					Log.v("score", "Error: " + e.getMessage());
				}
			}
		});

	}

	public void setParseData() {
		String id;
		String mimetype;

		ContentResolver contentResolver = getActivity().getContentResolver();
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
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

				testObject.setACL(new ParseACL(ParseUser.getCurrentUser()));
				Log.v("", "" + mimetype);

			}
			testObject.saveInBackground(new SaveCallback() {
				@Override
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
		public boolean isEnabled(int position) {
			if (mAppList.get(position).get(keyString[0]).toString()
					.equals("tag")) {
				return false;
			}
			return super.isEnabled(position);
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
				convertView.setTag(itemView);
			}

			if (mAppList != null) {
				if (mAppList.size() > 0) {

					itemView.tvname.setText(mAppList.get(position)
							.get(keyString[0]).toString());
					itemView.tvnumber.setText(mAppList.get(position)
							.get(keyString[1]).toString());
				}

			}

			return convertView;
		}

	}

}