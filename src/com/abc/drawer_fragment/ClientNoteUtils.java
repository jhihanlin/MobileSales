package com.abc.drawer_fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ClientNoteUtils {

	public static class ClientNoteOnItemSelectedListener implements OnItemSelectedListener {

		private ArrayAdapter<String> purposeAdapter;
		private Activity activity;
		private Spinner spinner;

		public ClientNoteOnItemSelectedListener(Activity activity, Spinner spinner) {
			this.purposeAdapter = (ArrayAdapter<String>) spinner.getAdapter();
			this.activity = activity;
			this.spinner = spinner;
		}

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (position == purposeAdapter.getCount() - 1) {
				ClientNoteUtils.showAddPurposeDialog(activity, purposeAdapter);
				spinner.setSelection(0);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}

	public static void showAddPurposeDialog(final Activity activity, final ArrayAdapter<String> purposeAdapter) {
		final ProgressDialog progressDialog = new ProgressDialog(activity);

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("新增目的");
		final EditText ed = new EditText(activity);
		builder.setView(ed);
		builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				progressDialog.setCancelable(false);
				progressDialog.setTitle("Loading...");
				progressDialog.show();

				final String s = ed.getText().toString();
				ParseObject ps = new ParseObject("Purpose");
				ps.put("name", s);
				ps.setACL(new ParseACL(ParseUser.getCurrentUser()));

				ps.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						progressDialog.dismiss();
						purposeAdapter.remove("-新增目的-");
						purposeAdapter.add(s);
						purposeAdapter.add("-新增目的-");
						purposeAdapter.remove("  ");
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
