package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.model.R;
import com.abc.model.utils.TypeFaceHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ClientNoteGroupsByPurpose extends Fragment {
	private List<ParseObject> clientNotes;
	private ListView ls;
	private TextView groupByText;

	public ClientNoteGroupsByPurpose() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.client_note_group_by, container, false);
		ls = (ListView) v.findViewById(R.id.purpose_listview);
		groupByText = (TextView) v.findViewById(R.id.group_by_textview);
		groupByText.setText("依目的分類記事");
		final ProgressDialog progressDialog = new ProgressDialog(getActivity());

		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		ParseQuery<ParseObject> queryPurpose = new ParseQuery<ParseObject>(
				"Purpose");
		queryPurpose.orderByDescending("createdAt");
		queryPurpose.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(final List<ParseObject> purpose, ParseException e) {
				ParseQuery<ParseObject> queryClientNote = new ParseQuery<ParseObject>(
						"ClientNote");

				queryClientNote.findInBackground(new FindCallback<ParseObject>() {

					@Override
					public void done(List<ParseObject> objects, ParseException e) {
						clientNotes = objects;
						progressDialog.dismiss();
						setListViewData(purpose, v);
					}
				});

			}

		});

		return v;
	}

	private void setListViewData(List<ParseObject> purpose, View v) {
		final List<String> arrayList = new ArrayList<String>();
		final List<String> arrayListId = new ArrayList<String>();

		LinearLayout layout1 = (LinearLayout) v.findViewById(R.id.purpose_layout);
		if (purpose.size() <= 0) {
			final TextView tx = new TextView(getActivity());
			tx.setText("目前無分類");
			layout1.addView(tx);

		}
		for (ParseObject ob : purpose) {
			String purposeName = ob.getString("name");

			int count = 0;
			for (ParseObject note : clientNotes) {
				if (purposeName.equals(note.getString("purpose")))
					count++;
			}

			arrayListId.add(ob.getObjectId());
			arrayList.add(purposeName + "(" + count + ")");
		}
		try {
			final ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, arrayList);
			ls.setAdapter(array_adapter);

			ls.setOnItemClickListener(new OnItemClickListener() {
				final List<Map<String, String>> plist = new ArrayList<Map<String, String>>();
				final List<Map<String, String>> plist_view = new ArrayList<Map<String, String>>();

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					String purposeName = arrayList.get(position).split("\\(")[0];
					Log.d("debug", purposeName);

					for (ParseObject clientNote : clientNotes) {

						if (clientNote.getString("purpose").equals(purposeName) == false)
							continue;

						Log.d("debug", "purpose==p" + clientNote.getString("title"));
						Map<String, String> item = new HashMap<String, String>();
						item.put("title", clientNote.getString("title"));
						item.put("date", clientNote.getString("date"));
						item.put("id", clientNote.getObjectId());
						plist.add(item);

						Map<String, String> item2 = new HashMap<String, String>();
						item2.put("title", clientNote.getString("title"));
						item2.put("client", clientNote.getString("client"));
						item2.put("purpose", clientNote.getString("purpose"));
						item2.put("date", clientNote.getString("date"));
						item2.put("time", clientNote.getString("time"));
						item2.put("content", clientNote.getString("content"));
						item2.put("location", clientNote.getString("location"));
						item2.put("remind", clientNote.getString("remind"));
						item2.put("remarks", clientNote.getString("remarks"));
						item2.put("id", clientNote.getObjectId());
						plist_view.add(item2);
					}
					if (plist.size() > 0) {
						try {
							final SimpleAdapter adapter_2 = new SimpleAdapter(getActivity(),
									plist, R.layout.client_note_listview,
									new String[] { "title", "date" }, new int[] {
											R.id.clientNote_tx1, R.id.clientNote_tx2 });
							ls.setAdapter(adapter_2);
							ls.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
									sendValueToClientNoteView(plist_view, position);
								}
							});
						} catch (Exception e2) {
							e2.printStackTrace();
						}

					} else {
						Toast.makeText(getActivity().getBaseContext(),
								"此分類無記事", Toast.LENGTH_SHORT)
								.show();
					}
				}
			});
			ls.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

					showDeleteDialog(arrayListId, position, array_adapter);

					return true;
				}
			});

		} catch (Exception e3) {
			e3.printStackTrace();
		}
	}

	public void sendValueToClientNoteView(List<Map<String, String>> list, int position) {
		Bundle bundle = new Bundle();
		Log.d("list[position]", list.get(position).toString());
		ArrayList<Map<String, String>> arrayList = new ArrayList<Map<String, String>>();
		arrayList.add(list.get(position));
		bundle.putParcelableArrayList("arrayList", (ArrayList<? extends Parcelable>) arrayList);

		Bundle bundle2 = new Bundle();
		bundle2.putBundle("bundle2", bundle);
		ClientNoteView clientNoteView = new ClientNoteView();
		clientNoteView.setArguments(bundle2);
		getActivity()
				.getFragmentManager()
				.beginTransaction()
				.add(R.id.content_frame, clientNoteView)
				.addToBackStack(null)
				.commit();

	}

	public void showDeleteDialog(final List<String> data,
			final int index,
			final ArrayAdapter<String> array_adapter) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("是否刪除");
		builder.setPositiveButton("刪除", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String objectId = data.get(index);

				Log.d("id", objectId);
				ParseObject obj = ParseObject.createWithoutData(
						"Purpose", objectId);
				obj.deleteEventually();
				data.remove(index);
				Toast.makeText(getActivity().getBaseContext(),
						"刪除成功", Toast.LENGTH_SHORT)
						.show();
				array_adapter.remove(array_adapter.getItem(index));
				array_adapter.notifyDataSetChanged();
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
