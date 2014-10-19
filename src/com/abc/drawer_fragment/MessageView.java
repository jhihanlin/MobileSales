package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.abc.model.R;
import com.abc.model.utils.TypeFaceHelper;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MessageView extends Fragment {

	public MessageView() {
	}

	ListView message_view;
	TextView message_name;
	protected List<ParseObject> messageContent;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.message_view, container, false);
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());
		message_view = (ListView) v.findViewById(R.id.message_view);
		message_view.setClickable(false);
		message_view.setEnabled(false);
		message_name = (TextView) v.findViewById(R.id.message_name);
		message_view.setClickable(false);
		Bundle arguments = getArguments();
		Log.d("bundle2", arguments.getBundle("bundle2").toString());
		Bundle bundle = arguments.getBundle("bundle2");
		ArrayList arrayList = new ArrayList();
		arrayList = bundle.getParcelableArrayList("arrayList");
		Log.d("arrayList", arrayList.toString());

		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		list = (ArrayList<Map<String, String>>) arrayList;
		Log.d("list", list.get(0).get("Message_receiver").toString());

		String name = list.get(0).get("Message_receiver");
		message_name.setText(list.get(0).get("Message_receiver"));

		loadingFromParse(name);

		return v;
	}

	private void loadingFromParse(final String str) {
		final ProgressDialog progressDialog = new ProgressDialog(getActivity());
		progressDialog.setTitle("Loading...");
		progressDialog.setCancelable(false);
		progressDialog.show();

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Message");
		query.findInBackground(new FindCallback<ParseObject>() {
			private Object ArrayList;

			@Override
			public void done(List<ParseObject> objects,
					com.parse.ParseException e) {
				if (e == null) { // put resule into a variable:clientNames
					messageContent = objects;
					Log.d("debug", "objects.size()=" + objects.size());
					progressDialog.dismiss();
				}
				ArrayList<Map<String, String>> dataList = new ArrayList<Map<String, String>>();

				for (ParseObject nameContent : messageContent) {
					if ((nameContent.getString("Message_receiver")) != null) {
						if ((nameContent.getString("Message_content")) != null) {
							Map<String, String> item = new HashMap<String, String>();

							item.put("Message_receiver",
									nameContent.getString("Message_receiver"));
							item.put("Message_content",
									nameContent.getString("Message_content"));
							Log.d("Message_content",
									nameContent.getString("Message_content"));
							Log.d("Message_receiver",
									nameContent.getString("Message_receiver"));
							dataList.add(item);
							Log.d("item", item.toString());
						}
					}

				}

				List<String> data = new ArrayList<String>();

				for (int i = 0; i <= dataList.size() - 1; i++) {
					Log.d("123", dataList.get(i).get("Message_receiver")
							.toString());
					String name111 = dataList.get(i).get("Message_receiver")
							.toString();
					if (str.equals(name111)) {
						data.add(dataList.get(i).get("Message_content")
								.toString());
					}

				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						data);
				message_view.setAdapter(adapter);
				progressDialog.dismiss();

			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

}