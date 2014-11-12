package tw.edu.fju.imd.mobilesales.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tw.edu.fju.imd.mobilesales.R;
import tw.edu.fju.imd.mobilesales.utils.TypeFaceHelper;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.drive.internal.i;
import com.parse.FindCallback;
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
	public Button addPeople, imbtndel, imbtnupdata;
	public Button peopleButton, tagButton;
	public AutoCompleteTextView autoComplete;
	public ArrayList<HashMap<String, String>> contactsArrayList;
	public String[] contactsName;
	public int size = 0;
	public boolean start = true;
	private ProgressDialog progressDialog;
	private SearchView mSearchView;
	private Menu menu;
	private List<ParseObject> peopleObjects;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.people_layout, container, false);

		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());

		listView = (ListView) v.findViewById(R.id.lvPEOPLE);

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
						.replace(R.id.content_frame, new People())
						.addToBackStack(null)
						.commit();
			}
		});

		tagButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				getActivity().getFragmentManager().beginTransaction()
						.replace(R.id.content_frame, new People_tag())
						.addToBackStack(null)
						.commit();
			}
		});

		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
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
															fg)
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
				peopleObjects = objects;
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_people:
			People_add ppadd = new People_add();
			ppadd.setMode("add");
			getFragmentManager().beginTransaction()
					.add(R.id.content_frame, ppadd)
					.addToBackStack(null)
					.commit();
			break;
		case R.id.action_import_contact:
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
											.replace(R.id.content_frame, new People())
											.addToBackStack(null)
											.commit();
									progressDialog.dismiss();
								}
							}).setNegativeButton("取消", null).show();

			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.people_fragment_menu, menu);
		setupSearchView(menu);

	}

	private void setupSearchView(Menu menu) {
		Log.d("debug", "setupSearchView");
		MenuItem searchItem = menu.findItem(R.id.search_people);
		mSearchView = (SearchView) searchItem.getActionView();
		mSearchView.setQueryHint("搜尋聯絡人");
		if (isAlwaysExpanded()) {
			mSearchView.setIconifiedByDefault(false);
		} else {
			searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
					| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}

		SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
		if (searchManager != null) {
			List<SearchableInfo> searchables = searchManager
					.getSearchablesInGlobalSearch();

			SearchableInfo info = searchManager
					.getSearchableInfo(getActivity().getComponentName());
			for (SearchableInfo inf : searchables) {
				if (inf.getSuggestAuthority() != null
						&& inf.getSuggestAuthority().startsWith("applications")) {
					info = inf;
				}
			}
			mSearchView.setSearchableInfo(info);
		}
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {

				if (peopleObjects == null)
					return false;

				List<ParseObject> objects = peopleObjects;
				Log.v("", "SIZE:" + objects.size() + " scores");

				contactsArrayList.clear();
				for (int i = 0; i < objects.size(); i++) {
					if (newText.equals("") || objects.get(i).getString("name").contains(newText)) {
						HashMap<String, String> hm = new HashMap<String, String>();
						hm.put("ID", objects.get(i).getObjectId()
								.toString());
						hm.put("NAME", objects.get(i).get("name")
								.toString());
						hm.put("NUMBER", objects.get(i).get("tel")
								.toString());
						contactsArrayList.add(hm);
					}
				}

				SimpleAdapter adapter = new SimpleAdapter(
						getActivity(), contactsArrayList,
						R.layout.people_tag_entry,
						new String[] { "NAME", "NUMBER" },
						new int[] { R.id.txtNAMEPHONE,
								R.id.txtDATAPHONE });
				listView.setAdapter(null);
				listView.setAdapter(adapter);
				return false;
			}
		});
	}

	protected boolean isAlwaysExpanded() {
		return false;
	}

}