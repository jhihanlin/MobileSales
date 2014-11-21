package tw.edu.fju.imd.mobilesales.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.edu.fju.imd.mobilesales.R;
import tw.edu.fju.imd.mobilesales.utils.TypeFaceHelper;

import android.app.AlertDialog;
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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class PeopleListFragment extends Fragment {

	public PeopleListFragment() {
	}

	private List<ParseObject> peopleObject;
	private ListView listView;
	private SearchView mSearchView;
	private Menu menu;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.people_contact, container, false);

		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());

		listView = (ListView) v.findViewById(R.id.people_contact);

		loadDataFromParse();

		return v;
	}

	private void loadDataFromParse() {
		final ProgressDialog progressDialog = new ProgressDialog(getActivity());
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				"People");
		query.orderByDescending("name");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				progressDialog.dismiss();
				peopleObject = objects;
				final ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
				final ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

				for (ParseObject ob : objects) {
					Map<String, String> item = new HashMap<String, String>();
					item.put("photo", ob.getString("photo"));
					item.put("name", ob.getString("name"));
					item.put("id", ob.getObjectId());

					data.add(item);

					Map<String, String> item2 = new HashMap<String, String>();
					item2.put("name", ob.getString("name"));
					item2.put("tag", ob.getString("tag"));
					item2.put("birthday", ob.getString("birthday"));
					item2.put("tel", ob.getString("tel"));
					item2.put("email", ob.getString("email"));
					item2.put("add", ob.getString("add"));
					item2.put("note", ob.getString("note"));
					item2.put("id", ob.getObjectId());

					list.add(item2);
				}
				try {
					final SimpleAdapter adapter = new SimpleAdapter(getActivity(),
							data, R.layout.people_contact_item,
							new String[] { "photo", "name" }, new int[] {
									R.id.people_pic, R.id.people_name });
					Log.d("debug", data.toString());
					listView.setAdapter(adapter);
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

							sendValueToPeopleView(list, position);

						}
					});
					listView.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

							showDeleteDialog(list, position, adapter);

							return false;
						}
					});

				} catch (Exception e2) {
					e.printStackTrace();
				}
			}
		});
	}

	private void sendValueToPeopleView(ArrayList<Map<String, String>> list, int position) {
		Bundle bundle = new Bundle();
		Log.d("list[position]", list.get(position).toString());
		ArrayList arrayList = new ArrayList();
		arrayList.add(list.get(position));
		bundle.putParcelableArrayList("arrayList", arrayList);

		Bundle bundle2 = new Bundle();
		bundle2.putBundle("bundle2", bundle);
		PeopleDetailFragment peopleContactView = new PeopleDetailFragment();
		peopleContactView.setArguments(bundle2);
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, peopleContactView)
				.addToBackStack(null)
				.commit();
	}

	private void showDeleteDialog(ArrayList<Map<String, String>> list, int position, SimpleAdapter adapter) {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_people:
			People_add ppadd = new People_add();
			ppadd.setMode("add");
			getActivity().getSupportFragmentManager().beginTransaction()
					.add(R.id.content_frame, ppadd)
					.addToBackStack(null)
					.commit();
			break;
		case R.id.action_import_contact:
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
			public boolean onQueryTextChange(String text) {
				String textIsLowerCase = text.toUpperCase();
				String textIsUpperCase = text.toLowerCase();

				if (peopleObject == null)
					return false;

				final ArrayList<Map<String, String>> searchPeopleData = new ArrayList<Map<String, String>>();
				final ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

				for (ParseObject ob : peopleObject) {
					if (ob.getString("name").contains(text) || text.equals("") || ob.getString("name").contains(textIsLowerCase) || ob.getString("name").contains(textIsUpperCase)) {
						Map<String, String> item = new HashMap<String, String>();
						item.put("photo", ob.getString("photo"));
						item.put("name", ob.getString("name"));
						item.put("id", ob.getObjectId());
						searchPeopleData.add(item);

						Map<String, String> item2 = new HashMap<String, String>();
						item2.put("name", ob.getString("name"));
						item2.put("tag", ob.getString("tag"));
						item2.put("birthday", ob.getString("birthday"));
						item2.put("tel", ob.getString("tel"));
						item2.put("email", ob.getString("email"));
						item2.put("add", ob.getString("add"));
						item2.put("note", ob.getString("note"));
						item2.put("id", ob.getObjectId());

						list.add(item2);

					}
				}
				final SimpleAdapter adapter = new SimpleAdapter(getActivity(),
						searchPeopleData, R.layout.people_contact_item,
						new String[] { "photo", "name" }, new int[] {
								R.id.people_pic, R.id.people_name });
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent,
							View view, int position, long id) {

						sendValueToPeopleView(list, position);
					}
				});
				listView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

						showDeleteDialog(list, position, adapter);

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