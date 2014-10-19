package com.abc.drawer_fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.model.R;
import com.abc.model.utils.TypeFaceHelper;
import com.google.android.gms.maps.MapsInitializer;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class Board extends Fragment {
	String content;

	public Board() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.board_layout, container, false);
		final ProgressDialog progressDialog = new ProgressDialog(getActivity());
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());

		final EditText push_content = (EditText) v.findViewById(R.id.push_content);
		push_content.setTypeface(typeface);
		TextView push_title = (TextView) v.findViewById(R.id.push_title);
		push_title.setTypeface(typeface);
		Button push_btn = (Button) v.findViewById(R.id.push_btn);
		push_btn.setTypeface(typeface);

		push_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				progressDialog.setCancelable(false);
				progressDialog.setTitle("Loading...");
				progressDialog.show();

				// get user
				ParseUser user = ParseUser.getCurrentUser();

				ParsePush pp = new ParsePush();
				pp.setChannel("all");
				content = push_content.getText().toString();
				pp.setMessage(content + " [" + user.getUsername() + "]");
				pp.sendInBackground();
				Toast.makeText(getActivity(), "send",
						Toast.LENGTH_SHORT).show();
				ParseObject object = new ParseObject("Board");
				object.put("content", content);
				object.put("username", user.getUsername());
				object.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						progressDialog.dismiss();

					}
				});
				Log.d("debug", content + "  " + user.getUsername());
			}
		});
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MapsInitializer.initialize(getActivity());
		setHasOptionsMenu(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_board_list:
			getActivity()
					.getFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame, new BoardList())
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
		inflater.inflate(R.menu.board_fragment_menu, menu);
	}

}
