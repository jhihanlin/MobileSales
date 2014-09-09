package com.abc.drawer_fragment;

import java.util.List;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.abc.model.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ClientNoteView extends Fragment {

	public ClientNoteView() {
	}

 
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.client_note_view, container, false);
		EditText getTitle =  (EditText) v.findViewById(R.id.view_title);
		Spinner getClient = (Spinner) v.findViewById(R.id.view_clientSpinner);
		Spinner getPurpose = (Spinner) v.findViewById(R.id.view_purposeSpinner);
		Button getDateButton = (Button) v.findViewById(R.id.view_date);
		Button getTimeButton =(Button) v.findViewById(R.id.view_time);
		EditText getContent =(EditText) v.findViewById(R.id.view_content);
		EditText getLocation =(EditText) v.findViewById(R.id.view_location);
		Spinner getRemind =(Spinner) v.findViewById(R.id.view_remind);
		EditText getRemarks= (EditText) v.findViewById(R.id.view_remarks);
		
		
				return v;
	}

	private void loadDataFromParse() {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				"ClientNote");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				for (ParseObject ob : objects) {
								}
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

}
