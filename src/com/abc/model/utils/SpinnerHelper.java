package com.abc.model.utils;

import java.util.ArrayList;
import java.util.List;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.GpsStatus.Listener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SpinnerAdapter;

public class SpinnerHelper {

	public static void buildCustomerData(final Activity activity, final Spinner spinner, final String className, final String title, final String selection) {
		final ProgressDialog progressDialog = new ProgressDialog(activity);
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(className);

		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> purpose,
					com.parse.ParseException e) {

				int selectionPosition = -1;
				ArrayList<String> purposArrayList;
				progressDialog.dismiss();

				if (e != null) {
					e.printStackTrace();
					return;
				}

				purposArrayList = new ArrayList<String>();
				if (purpose != null) {
					for (ParseObject purposeObject : purpose) {
						if (className.equals("MessageModel")) {
							if (purposeObject.getString("Model_content") != null)
								purposArrayList.add(purposeObject.getString("Model_content"));
						} else {
							if (purposeObject.getString("name") != null)
								purposArrayList.add(purposeObject.getString("name"));

						}
					}
				}
				if (purposArrayList.size() <= 0) {
					Log.d("debug", "purposArrayList" + purposArrayList.size());
					purposArrayList.add("::選擇" + title + "::");
				} else {
					for (int i = 0; i < purposArrayList.size(); i++) {
						if (purposArrayList.get(i).equals(selection)) {
							selectionPosition = i;
						}
					}

				}
				purposArrayList.add("《新增" + title + "》");
				final ArrayAdapter<String> purposeAdapter = new ArrayAdapter<String>(
						activity,
						android.R.layout.simple_spinner_item,
						purposArrayList);
				purposeAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_item);

				ClientNoteOnItemSelectedListener listener =
						new SpinnerHelper.ClientNoteOnItemSelectedListener(activity, spinner, className, title);
				listener.setOldListener(spinner.getOnItemSelectedListener());

				spinner.setAdapter(purposeAdapter);
				spinner.setOnItemSelectedListener(listener);

				if (selectionPosition != -1) {
					spinner.setSelection(selectionPosition);
				}
			}
		});
	}

	private static class ClientNoteOnItemSelectedListener implements OnItemSelectedListener {

		private Activity activity;
		private Spinner spinner;
		private String className;
		private String title;
		private OnItemSelectedListener oldListener;

		public ClientNoteOnItemSelectedListener(Activity activity, Spinner spinner, String className, String title) {
			this.activity = activity;
			this.spinner = spinner;
			this.className = className;
			this.title = title;
		}

		public void setOldListener(OnItemSelectedListener oldListener) {
			this.oldListener = oldListener;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (oldListener != null)
				oldListener.onItemSelected(parent, view, position, id);

			ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
			if (position == adapter.getCount() - 1) {
				SpinnerHelper.showAddPurposeDialog(activity, adapter, className, title);
				spinner.setSelection(0);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			if (oldListener != null)
				oldListener.onNothingSelected(parent);
		}

	}

	private static void showAddPurposeDialog(final Activity activity, final ArrayAdapter<String> purposeAdapter, final String className, final String title) {
		final ProgressDialog progressDialog = new ProgressDialog(activity);

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("新增" + title);
		final EditText ed = new EditText(activity);
		builder.setView(ed);
		builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressDialog.setCancelable(false);
				progressDialog.setTitle("Loading...");
				progressDialog.show();

				final String s = ed.getText().toString();
				ParseObject object = new ParseObject(className);
				if (className.equals("MessageModel")) {
					object.put("Model_content", s);
				} else {
					object.put("name", s);
				}
				object.setACL(new ParseACL(ParseUser.getCurrentUser()));

				object.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						progressDialog.dismiss();
						purposeAdapter.remove("《新增" + title + "》");
						purposeAdapter.add(s);
						purposeAdapter.add("《新增" + title + "》");
						purposeAdapter.remove("::選擇" + title + "::");
						purposeAdapter.notifyDataSetChanged();
					}
				});
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();

	}
}
