package com.abc.drawer_fragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.abc.model.R;
import com.abc.model.utils.TypeFaceHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class BoardList extends Fragment {

	public BoardList() {
	}

	ListView board_list;
	Button board_back;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.board_listview, container, false);
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());
		board_list = (ListView) v.findViewById(R.id.board_list);
		board_back = (Button) v.findViewById(R.id.board_back);
		board_back.setTypeface(typeface);
		loadDataFromParse();

		return v;
	}

	private void loadDataFromParse() {
		final ProgressDialog progressDialog = new ProgressDialog(getActivity());// loading
																				// bar
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				"Board");
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				progressDialog.dismiss();

				final ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
				final ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

				for (ParseObject ob : objects) {
					Map<String, String> item = new HashMap<String, String>();
					item.put("content", ob.getString("content"));
					item.put("username", ob.getString("username"));
					item.put("id", ob.getObjectId());

					data.add(item);

				}
				try {
					final SimpleAdapter adapter = new SimpleAdapter(getActivity(),
							data, android.R.layout.simple_list_item_2,
							new String[] { "content", "username" }, new int[] {
									android.R.id.text1, android.R.id.text2 });
					board_list.setAdapter(adapter);
					board_list.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {

						}
					});
					board_list.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

							showDeleteDialog(data, position,
									adapter);
							// Toast.makeText(getActivity(), "deleted",
							// Toast.LENGTH_LONG).show();

							return true;
						}
					});
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		board_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity()
						.getFragmentManager()
						.beginTransaction()
						.replace(R.id.content_frame, new Board())
						.addToBackStack(null)
						.commit();
			}
		});
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
						"Board", Object_id);
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
	}

}
