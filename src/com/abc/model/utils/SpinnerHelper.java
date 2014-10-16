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

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class SpinnerHelper {

	public static void buildCustomerData(final Activity activity, final Spinner spinner, final String className, final String title, final String selection) {
		final ProgressDialog progressDialog = new ProgressDialog(activity);
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(className);

		query.findInBackground(new FindCallback<ParseObject>() {
			ArrayList<String> purposArrayList;
			private int selectionPosition = -1;

			@Override
			public void done(List<ParseObject> purpose,
					com.parse.ParseException e) {
				if (e == null) {

					try {
						progressDialog.dismiss();

						purposArrayList = new ArrayList<String>();
						if (purpose != null) {
							for (ParseObject purposeObject : purpose) {
								if (purposeObject.getString("name") != null)
									purposArrayList.add(purposeObject
											.getString("name"));
								Log.d("purposeArrayList",
										purposArrayList.toString());

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
						spinner.setAdapter(purposeAdapter);
						spinner.setOnItemSelectedListener(new SpinnerHelper.ClientNoteOnItemSelectedListener(activity, spinner, className, title));
						if (selectionPosition != -1) {
							spinner.setSelection(selectionPosition);
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});
	}

	private static class ClientNoteOnItemSelectedListener implements OnItemSelectedListener {

		private ArrayAdapter<String> purposeAdapter;
		private Activity activity;
		private Spinner spinner;
		private String className;
		private String title;

		public ClientNoteOnItemSelectedListener(Activity activity, Spinner spinner, String className, String title) {
			this.purposeAdapter = (ArrayAdapter<String>) spinner.getAdapter();
			this.activity = activity;
			this.spinner = spinner;
			this.className = className;
			this.title = title;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (position == purposeAdapter.getCount() - 1) {
				SpinnerHelper.showAddPurposeDialog(activity, purposeAdapter, className, title);
				spinner.setSelection(0);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

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
				ParseObject ps = new ParseObject(className);
				ps.put("name", s);
				ps.setACL(new ParseACL(ParseUser.getCurrentUser()));

				ps.saveInBackground(new SaveCallback() {

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
