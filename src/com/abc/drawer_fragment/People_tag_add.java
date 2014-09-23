package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.abc.model.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class People_tag_add extends Fragment {

	public View v;
	public Button buttonOK, buttonNO;
	private EditText editTexttags;
	public LinearLayout ll;
	public ListView listView;
	public ArrayList<HashMap<String, Object>> contactsArrayList;
	public String tag = "";
	public String mode = "";
	public boolean checksave;
	public boolean[] ischeck;
	private ProgressDialog progressDialog;

	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.people_tag_add, container, false);
		ll = (LinearLayout) v.findViewById(R.id.ll);
		buttonOK = (Button) v.findViewById(R.id.buttonOK);
		buttonNO = (Button) v.findViewById(R.id.buttonNO);
		listView = (ListView) v.findViewById(R.id.lvtaggadd);
		editTexttags = (EditText) v.findViewById(R.id.editTexttags);
		progressDialog = new ProgressDialog(getActivity());
		if (mode.equals("gadd")) {
			ll.setVisibility(8);

		} else {
			ll.setVisibility(0);

		}

		getParseDate();

		buttonOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checksave = true;
				if (mode.equals("add")) {
					tag = editTexttags.getText().toString();
					ParseObject testObject = new ParseObject("Tag");// DATABASE_TABLE_NAME
					testObject.put("name", editTexttags.getText().toString());
					testObject.setACL(new ParseACL(ParseUser.getCurrentUser()));

					testObject.saveInBackground(new SaveCallback() {

						@Override
						public void done(ParseException e) {
							if (e == null) {

							} else {

								checksave = false;
							}
						}
					});

				}

				for (int i = 0; i < ischeck.length; i++) {
					Log.v("", "123123 id:" + i + " ck:" + ischeck[i]);
					if (ischeck[i]) {
						progressDialog.setCancelable(false);
						progressDialog.setTitle("Loading...");
						progressDialog.show();
						ParseQuery<ParseObject> query = ParseQuery
								.getQuery("Client");
						// Log.v("","ID="+ID);

						query.getInBackground(contactsArrayList.get(i)
								.get("ID").toString(),
								new GetCallback<ParseObject>() {

									public void done(ParseObject gameScore,
											ParseException e) {
										progressDialog.dismiss();
										if (e == null) {

											gameScore.put("tag", tag);

											gameScore
													.saveInBackground(new SaveCallback() {

														@Override
														public void done(
																ParseException ex) {
															// TODO
															// Auto-generated
															// method
															// stub
															if (ex == null) {

															} else {
																// Toast.makeText(getActivity(),
																// "false:"+ex.getMessage().toString(),
																// Toast.LENGTH_LONG).show();
																checksave = false;
															}
														}
													});
										}
									}
								});

					}

				}
				if (checksave) {
					if (mode.equals("add")) {
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.content_frame, new People_tag())
								.commit();
					} else if (mode.equals("gadd")) {
						People_tag_list ppadd = new People_tag_list();
						ppadd.setTag(tag);
						Fragment fg = (Fragment) ppadd;
						FragmentManager fragmentManager = getFragmentManager();
						fragmentManager.beginTransaction()
								.replace(R.id.content_frame, fg).commit();
					}

				}

			}
		});

		buttonNO.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, new People_tag()).commit();
			}
		});

		return v;
	}

	public void setTag(String s) {
		tag = s;
	}

	public void setMode(String s) {
		mode = s;
	}

	public void getParseDate() {
		contactsArrayList = new ArrayList<HashMap<String, Object>>();
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");

		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub

				progressDialog.dismiss();
				if (e == null) {
					// Log.v("score", "Retrieved " + objects.size() +
					// " scores");

					for (int i = 0; i < objects.size(); i++) {
						HashMap<String, Object> hm = new HashMap<String, Object>();
						hm.put("ID", objects.get(i).getObjectId().toString());
						hm.put("NAME", objects.get(i).get("name").toString());
						hm.put("NUMBER", objects.get(i).get("tel").toString());
						hm.put("TAG", objects.get(i).get("tag").toString());
						contactsArrayList.add(hm);

					}

					People_lv_BtnAdapter_check Btnadapter = new People_lv_BtnAdapter_check(
							getActivity(), contactsArrayList,
							R.layout.people_tag_checkbox_1, new String[] {
									"NAME", "TAG" }, new int[] { R.id.check1 });
					listView.setAdapter(Btnadapter);

				} else {
					Log.v("score", "Error: " + e.getMessage());
				}
			}
		});
	}

	public class People_lv_BtnAdapter_check extends BaseAdapter {

		private ArrayList<HashMap<String, Object>> mAppList;
		private LayoutInflater mInflater;
		private Context mContext;
		private String[] keyString;
		private int[] valueViewID;
		private ItemView itemView;

		private ProgressDialog progressDialog;

		private class ItemView {
			CheckBox cb;
		}

		public People_lv_BtnAdapter_check(Context c,
				ArrayList<HashMap<String, Object>> appList, int resource,
				String[] from, int[] to) {
			mAppList = appList;
			mContext = c;
			mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			keyString = new String[from.length];
			valueViewID = new int[to.length];
			ischeck = new boolean[appList.size()];

			System.arraycopy(from, 0, keyString, 0, from.length);
			System.arraycopy(to, 0, valueViewID, 0, to.length);
		}

		public boolean[] getIsCheck() {
			// TODO Auto-generated method stub
			// return 0;
			return ischeck;
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			// return null;

			if (convertView != null) {
				itemView = (ItemView) convertView.getTag();
			} else {
				convertView = mInflater.inflate(R.layout.people_tag_checkbox_1,
						null);
				itemView = new ItemView();

				itemView.cb = (CheckBox) convertView
						.findViewById(valueViewID[0]);
				;
				for (int i = 0; i < ischeck.length; i++) {
					ischeck[i] = false;
				}
				convertView.setTag(itemView);
			}

			if (mAppList != null) {

				if (!tag.equals("")) {
					if (mAppList.get(position).get(keyString[1]).toString()
							.equals(tag)) {
						itemView.cb.setChecked(true);
					}
				}

				itemView.cb.setText(mAppList.get(position).get(keyString[0])
						.toString());
				itemView.cb
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								// TODO Auto-generated method stub
								ischeck[position] = isChecked;
								Log.v("", "id:" + position + " ck:" + isChecked);
							}
						});
			}

			return convertView;
		}

	}
}
