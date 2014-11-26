package tw.edu.fju.imd.mobilesales.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.edu.fju.imd.mobilesales.R;
import tw.edu.fju.imd.mobilesales.utils.DialogHelper;
import tw.edu.fju.imd.mobilesales.utils.TypeFaceHelper;
import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
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
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ClientNoteRecordFragment extends Fragment {

	public ClientNoteRecordFragment() {
	}

	private ListView listView;
	private String s = "";
	private SearchView mSearchView;
	private Menu menu;
	private List<ParseObject> clientObject;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.client_listview, container, false);
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());

		listView = (ListView) v.findViewById(R.id.listView1);

		loadDataFromParse();

		return v;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
	}

	private void loadDataFromParse() {
		final ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
				clientObject = objects;
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

							DialogHelper.showDeleteDialog(getActivity(), "ClientNote", data, position,
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
		ClientNoteDetailFragment clientNoteView = new ClientNoteDetailFragment();
		clientNoteView.setArguments(bundle2);
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, clientNoteView)
				.addToBackStack(null)
				.commit();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

		switch (item.getItemId()) {
		case R.id.action_add_client_note:
			fragmentManager.beginTransaction()
					.add(R.id.content_frame, new ClientNoteAddFragement())
					.addToBackStack(null)
					.commit();
			return true;
		case R.id.group_by_purpose:
			Log.d("debug", "group_by_purpose");
			fragmentManager.beginTransaction()
					.add(R.id.content_frame, new ClientNoteGroupByPurposeFragment())
					.addToBackStack(null)
					.commit();
			return true;
		case R.id.group_by_clientTag:
			Log.d("debug", "group_by_clientTag");
			fragmentManager.beginTransaction()
					.add(R.id.content_frame, new ClientNoteGroupsByTagFragment())
					.addToBackStack(null)
					.commit();

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
		setupSearchView(menu);
	}

	private void setupSearchView(Menu menu) {
		Log.d("debug", "setupSearchView");
		MenuItem searchItem = menu.findItem(R.id.search_clientnote);
		mSearchView = (SearchView) searchItem.getActionView();
		mSearchView.setQueryHint("搜尋客戶名稱");
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
			public boolean onQueryTextSubmit(String text) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String text) {

				if (clientObject == null)
					return false;

				final ArrayList<Map<String, String>> searchClientData = new ArrayList<Map<String, String>>();
				final ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

				for (ParseObject ob : clientObject) {
					if (ob.getString("client").contains(text) || text.equals("")) {
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
				}
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

						DialogHelper.showDeleteDialog(getActivity(), "ClientNote", list, position,
								adapter);

						return true;
					}
				});
				return false;
			}
		});
	}

	protected boolean isAlwaysExpanded() {
		return false;
	}
}
