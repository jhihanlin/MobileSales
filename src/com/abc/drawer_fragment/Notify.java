package com.abc.drawer_fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.abc.model.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class Notify extends Fragment {

	public Notify() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.notify_listview, container, false);
		final ListView no_listview = (ListView) v.findViewById(R.id.no_listview);

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date curDate = new Date(System.currentTimeMillis()); // get System's
																// time
		final String str = formatter.format(curDate);

		final ProgressDialog progressDialog = new ProgressDialog(getActivity());// loading
		// bar
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		final ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		final ArrayList<Map<String, String>> list2 = new ArrayList<Map<String, String>>();
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				"ClientNote");
		query.orderByDescending("date");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				progressDialog.dismiss();
				for (ParseObject ob : objects) {
					if (ob.getString("date").equals(str)) {

						Map<String, String> item = new HashMap<String, String>();
						item.put("title", ob.getString("title"));
						item.put("date", ob.getString("date") + " " + ob.getString("time"));
						item.put("id", ob.getObjectId());
						list.add(item);

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
						list2.add(item2);
					}
					try {
						final SimpleAdapter adapter = new SimpleAdapter(getActivity(),
								list, android.R.layout.simple_list_item_2,
								new String[] { "title", "date" }, new int[] {
										android.R.id.text1, android.R.id.text2 });
						no_listview.setAdapter(adapter);
						no_listview.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {

								sendValueToNotifyView(list2, position);
							}
						});
						no_listview.setOnItemLongClickListener(new OnItemLongClickListener() {

							@Override
							public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

								showDeleteDialog(list, position,
										adapter);

								return true;
							}
						});
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});
		return v;
	}

	public void sendValueToNotifyView(ArrayList<Map<String, String>> list, int position) {
		Bundle bundle = new Bundle();
		Log.d("list[position]", list.get(position).toString());
		ArrayList arrayList = new ArrayList();
		arrayList.add(list.get(position));
		bundle.putParcelableArrayList("arrayList", arrayList);

		Bundle bundle2 = new Bundle();
		bundle2.putBundle("bundle2", bundle);
		NotifyView notifyView = new NotifyView();
		notifyView.setArguments(bundle2);
		getActivity()
				.getFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, notifyView)
				.addToBackStack(null)
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
	}

}
