package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.abc.model.R;
import com.abc.model.utils.TypeFaceHelper;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class People_tag extends Fragment {

	public Button peopleButton, tagButton, btntagadd;
	public ListView listView;
	public View v;
	public ArrayList<HashMap<String, Object>> contactsArrayList;
	private ProgressDialog progressDialog;
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
		getParseTagDate();

		tagName = (TextView) v.findViewById(R.id.tagName);
		m1 = (TextView) v.findViewById(R.id.message_tx1);
		m1.setTypeface(typeface);
		m2 = (TextView) v.findViewById(R.id.message_tx2);
		m2.setTypeface(typeface);

		m1.setVisibility(0);
		m2.setVisibility(8);
		tagName.setVisibility(8);

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

				People_tag_add ppadd = new People_tag_add();
				ppadd.setTag("");
				ppadd.setMode("add");
				Fragment fg = (Fragment) ppadd;
				getActivity().getFragmentManager()
						.beginTransaction()
						.replace(R.id.content_frame, fg)
						.addToBackStack(null)
						.commit();

			}
		});

		return v;
	}

	public void setListView() {
		People_lv_BtnAdapter Btnadapter = new People_lv_BtnAdapter(
				getActivity(), contactsArrayList,
				R.layout.people_contact_entry,
				new String[] { "NAME" }, new int[] {
						R.id.tagname }, getFragmentManager());
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
										querytag.whereEqualTo("name", name);
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
										query.whereEqualTo("tag", name);
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
																			// åš™ç·šåš™è¸è•­åš™è¸è•­
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

										Thread thread = new Thread() {
											@Override
											public void run() {
												try {
													Thread.sleep(1000);
													Message msg = new Message();
													getActivity().getFragmentManager()
															.beginTransaction()
															.replace(R.id.content_frame, new People_tag())
															.addToBackStack(null)
															.commit();
												} catch (Exception e) {
													e.printStackTrace();
												} finally {
												}
											}
										};
										// é–‹å§‹åŸ·è¡ŒåŸ·è¡Œç·’
										thread.start();

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
				ppadd.setTag(name);
				Fragment fg = (Fragment) ppadd;
				getActivity().getFragmentManager()
						.beginTransaction()
						.replace(R.id.content_frame, fg)
						.addToBackStack(null)
						.commit();

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
						hm.put("ID", objects.get(tagi).getObjectId().toString());
						hm.put("NAME", objects.get(tagi).get("name").toString());
						hm.put("NUMBER", "");
						Log.v("score", ": "
								+ objects.get(tagi).get("name").toString());
						contactsArrayList.add(hm);
					}
					if (contactsArrayList.size() > 0) {
						setListView();
					}

				} else {
					Log.v("score", "Error: " + e.getMessage());
				}

			}
		});
		// Log.v("", ""+tag.length);

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
						.inflate(R.layout.people_contact_entry, null);
				itemView = new ItemView();

				itemView.tvtag = (TextView) convertView
						.findViewById(valueViewID[0]);
				convertView.setTag(itemView);
			}

			// HashMap<String, Object> appInfo = mAppList.get(position);
			if (mAppList != null) {

				itemView.tvtag.setText(mAppList.get(position).get(keyString[0]).toString());

			}

			return convertView;
		}

	}

}