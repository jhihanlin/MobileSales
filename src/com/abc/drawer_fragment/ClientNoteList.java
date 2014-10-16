package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.abc.model.R;
import com.abc.model.utils.TypeFaceHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ClientNoteList extends Fragment {

	public ClientNoteList() {
	}

	ListView listView;
	Button addEvent;
	Button searchButton;
	Button groupByBtn;
	EditText inputClient;
	String s = "";

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.client_listview, container, false);
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());
		groupByBtn = (Button) v.findViewById(R.id.groupByBtn);
		groupByBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("groupByBtn", "Onclick");
				getActivity().openContextMenu(groupByBtn);
			}
		});

		registerForContextMenu(groupByBtn);
		listView = (ListView) v.findViewById(R.id.listView1);
		inputClient = (EditText) v.findViewById(R.id.editText1);
		searchButton = (Button) v.findViewById(R.id.button2);
		searchButton.setTypeface(typeface);

		addEvent = (Button) v.findViewById(R.id.button1);
		loadDataFromParse();
		addEvent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, new ClientNote()).commit();
			}
		});

		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final ProgressDialog progressDialog = new ProgressDialog(getActivity());// loading
				// bar
				progressDialog.setCancelable(false);
				progressDialog.setTitle("Loading...");
				progressDialog.show();
				s = inputClient.getText().toString();
				Log.d("s", s);
				if (s.length() > 0) {
					final ArrayList<Map<String, String>> searchClientData = new ArrayList<Map<String, String>>();
					final ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

					ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
							"ClientNote");
					query.orderByDescending("date");
					query.findInBackground(new FindCallback<ParseObject>() {

						@Override
						public void done(List<ParseObject> objects, ParseException e) {
							progressDialog.dismiss();

							for (ParseObject ob : objects) {
								if (ob.getString("client").equals(s) || (ob.getString("client").indexOf(s) != -1)) {
									Map<String, String> item = new HashMap<String, String>();
									item.put("title", ob.getString("title"));
									item.put("client", ob.getString("client"));
									item.put("id", ob.getObjectId());
									searchClientData.add(item);

									Map<String, String> item2 = new HashMap<String, String>();
									item2.put("title", ob.getString("title"));
									item2.put("client", ob.getString("client"));
									item2.put("purpose", ob.getString("purpose"));
									item2.put("date", ob.getString("date"));
									item2.put("time", ob.getString("time"));
									item2.put("content", ob.getString("content"));
									item2.put("location", ob.getString("location"));
									item2.put("remind", ob.getString("remind"));
									item2.put("remarks", ob.getString("remarks"));
									item2.put("id", ob.getObjectId());
									list.add(item2);

								}

								try {

									final SimpleAdapter adapter = new SimpleAdapter(getActivity(),
											searchClientData, R.layout.client_note_listview,
											new String[] { "title", "client" }, new int[] {
													R.id.clientNote_tx1, R.id.clientNote_tx2 });
									listView.setAdapter(adapter);
									listView.setOnItemClickListener(new OnItemClickListener() {

										@Override
										public void onItemClick(AdapterView<?> parent,
												View view, int position, long id) {

											sendValueToClientNoteView(list, position);
										}
									});
									listView.setOnItemLongClickListener(new OnItemLongClickListener() {

										@Override
										public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

											showDeleteDialog(searchClientData, position,
													adapter);

											return true;
										}
									});
									listView.setOnItemLongClickListener(new OnItemLongClickListener() {

										@Override
										public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

											showDeleteDialog(list, position, adapter);

											return true;
										}
									});
								} catch (Exception e3) {
									e3.printStackTrace();
								}
							}
						}
					});
				} else {
					progressDialog.dismiss();
					Toast.makeText(getActivity().getBaseContext(),
							"請輸入客戶名稱", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});
		return v;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.group_by_purpose:
			Log.d("debug", "group_by_purpose");
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, new ClientNoteGroupsByPurpose()).commit();
		case R.id.group_by_clientTag:
			Log.d("debug", "group_by_clientTag");

			return true;
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// getActivity().getMenuInflater().inflate(R.menu.client_note_menu,
		// menu);
		// Log.d("menu", "run menu");
	}

	private void loadDataFromParse() {
		final ProgressDialog progressDialog = new ProgressDialog(getActivity());// loading
																				// bar
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				"ClientNote");
		query.orderByDescending("date");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				progressDialog.dismiss();

				final ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
				final ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

				for (ParseObject ob : objects) {
					Map<String, String> item = new HashMap<String, String>();
					item.put("title", ob.getString("title"));
					item.put("client", ob.getString("client") + "\n" + ob.getString("date"));
					item.put("id", ob.getObjectId());

					data.add(item);

					Map<String, String> item2 = new HashMap<String, String>();
					item2.put("title", ob.getString("title"));
					item2.put("client", ob.getString("client"));
					item2.put("purpose", ob.getString("purpose"));
					item2.put("date", ob.getString("date"));
					item2.put("time", ob.getString("time"));
					item2.put("content", ob.getString("content"));
					item2.put("location", ob.getString("location"));
					item2.put("remind", ob.getString("remind"));
					item2.put("remarks", ob.getString("remarks"));
					item2.put("id", ob.getObjectId());
					list.add(item2);
				}
				try {
					final SimpleAdapter adapter = new SimpleAdapter(getActivity(),
							data, R.layout.client_note_listview,
							new String[] { "title", "client" }, new int[] {
									R.id.clientNote_tx1, R.id.clientNote_tx2 });

					listView.setAdapter(adapter);
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {

							sendValueToClientNoteView(list, position);
						}
					});
					listView.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

							showDeleteDialog(data, position,
									adapter);

							return true;
						}
					});
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

		});
	}

	public void sendValueToClientNoteView(ArrayList<Map<String, String>> list, int position) {
		Bundle bundle = new Bundle();
		Log.d("list[position]", list.get(position).toString());
		ArrayList arrayList = new ArrayList();
		arrayList.add(list.get(position));
		bundle.putParcelableArrayList("arrayList", arrayList);

		Bundle bundle2 = new Bundle();
		bundle2.putBundle("bundle2", bundle);
		ClientNoteView clientNoteView = new ClientNoteView();
		clientNoteView.setArguments(bundle2);
		getActivity()
				.getFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, clientNoteView)
				.commit();
	}

	public void showDeleteDialog(final List<Map<String, String>> data,
			final int index,
			final SimpleAdapter adapter) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("是否刪除");
		builder.setPositiveButton("刪除", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String Object_id = data.get(index).get("id");

				Log.d("id", Object_id);
				ParseObject obj = ParseObject.createWithoutData(
						"ClientNote", Object_id);
				obj.deleteEventually();
				data.remove(index);

				adapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		FragmentManager fragmentManager = getFragmentManager();
		switch (item.getItemId()) {
		case R.id.action_add_client_note:
			fragmentManager.beginTransaction()
					.add(R.id.content_frame, new ClientNote())
					.addToBackStack(null)
					.commit();
			return true;
		case R.id.group_by_purpose:
			Log.d("debug", "group_by_purpose");
			fragmentManager.beginTransaction()
					.add(R.id.content_frame, new ClientNoteGroupsByPurpose())
					.addToBackStack(null)
					.commit();
			return true;
		case R.id.group_by_clientTag:
			Log.d("debug", "group_by_clientTag");
			return true;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.clientnote_fragment_menu, menu);
	}
}
