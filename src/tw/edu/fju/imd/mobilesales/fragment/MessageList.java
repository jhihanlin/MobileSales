package tw.edu.fju.imd.mobilesales.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.edu.fju.imd.mobilesales.R;
import tw.edu.fju.imd.mobilesales.utils.TypeFaceHelper;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MessageList extends Fragment {

	public MessageList() {

	}

	private ListView listView;
	private ProgressBar progressBar;
	protected List<ParseObject> messageObjects;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.message_listview, container, false);
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());

		progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
		listView = (ListView) v.findViewById(R.id.listView);
		loadDataFromParse();
		return v;
	}

	private void loadDataFromParse() {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Message");
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				messageObjects = objects;
				progressBar.setVisibility(View.GONE);

				final ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
				final ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

				for (ParseObject ob : objects) {
					Map<String, String> item = new HashMap<String, String>();
					item.put("Message_receiver",
							ob.getString("Message_receiver"));
					item.put("Message_content", ob.getString("Message_content"));
					item.put("id", ob.getObjectId());
					data.add(item);

					Map<String, String> item2 = new HashMap<String, String>();
					item2.put("Message_receiver",
							ob.getString("Message_receiver"));
					item2.put("Message_content",
							ob.getString("Message_content"));
					item2.put("id", ob.getObjectId());
					list.add(item2);
				}

				try {
					final SimpleAdapter adapter = new SimpleAdapter(
							getActivity(),
							data,
							android.R.layout.simple_list_item_2,
							new String[] { "Message_receiver",
									"Message_content" },
							new int[] { android.R.id.text1, android.R.id.text2 });
					listView.setAdapter(adapter);

					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							sendValueToClientNoteView(list, position);
						}

						private void sendValueToClientNoteView(
								ArrayList<Map<String, String>> list,
								int position) {
							// TODO Auto-generated method stub
							Bundle bundle = new Bundle();
							Log.d("list[position]", list.get(position)
									.toString());
							ArrayList arrayList = new ArrayList();
							arrayList.add(list.get(position));
							bundle.putParcelableArrayList("arrayList",
									arrayList);

							Bundle bundle2 = new Bundle();
							bundle2.putBundle("bundle2", bundle);
							MessageView messageView = new MessageView();
							messageView.setArguments(bundle2);
							getActivity().getFragmentManager()
									.beginTransaction()
									.replace(R.id.content_frame, messageView)
									.addToBackStack(null)
									.commit();
						}
					});

					listView.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {

							showDeleteDialog(data, position, adapter);

							return true;
						}
					});

				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});

	}

	public void showDeleteDialog(final List<Map<String, String>> data,
			final int index, final SimpleAdapter adapter) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("是否刪除");
		builder.setPositiveButton("刪除",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String Object_id = data.get(index).get("id");

						Log.d("id", Object_id);
						ParseObject obj = ParseObject.createWithoutData(
								"Message", Object_id);
						obj.deleteEventually();
						data.remove(index);

						adapter.notifyDataSetChanged();
					}
				});
		builder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {

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
		case R.id.action_add_message:
			fragmentManager.beginTransaction()
					.add(R.id.content_frame, new Message())
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
		inflater.inflate(R.menu.message_fragment_menu, menu);
	}
}