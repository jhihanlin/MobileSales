package com.abc.drawer_fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abc.model.R;
import com.parse.ParsePush;
import com.parse.ParseUser;

public class Board extends Fragment {

	public Board() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.board_layout, container, false);
		
		final EditText push_content = (EditText) v.findViewById(R.id.push_content);
		Button push_btn = (Button) v.findViewById(R.id.push_btn);
		
		push_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//get user
				ParseUser user = ParseUser.getCurrentUser();

				ParsePush pp = new ParsePush();
				pp.setChannel("all");
				pp.setMessage(push_content.getText().toString()+" [" +user.getUsername()+"]" );
				pp.sendInBackground();
				Toast.makeText(getActivity(), "send",
						Toast.LENGTH_SHORT).show();
			}
		});
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

}
