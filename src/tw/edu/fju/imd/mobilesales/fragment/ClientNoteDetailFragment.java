package tw.edu.fju.imd.mobilesales.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.edu.fju.imd.mobilesales.R;
import tw.edu.fju.imd.mobilesales.utils.DialogHelper;
import tw.edu.fju.imd.mobilesales.utils.SpinnerHelper;
import tw.edu.fju.imd.mobilesales.utils.TypeFaceHelper;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class ClientNoteDetailFragment extends Fragment {
	protected List<ParseObject> clientName;
	protected List<ParseObject> purposeName;
	private ProgressDialog pd;
	int c_index;
	int p_index;

	String[] remindTime = new String[] { "10 minutes ago", "15 minutes ago",
			"30 minutes ago", "1 hour ago", "3 hours ago", "12 hours ago",
			"1 day ago" };

	public ClientNoteDetailFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.client_note_view, container, false);
		// bar
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());

		Bundle arguments = getArguments();
		Log.d("bundle2", arguments.getBundle("bundle2").toString());
		Bundle bundle = arguments.getBundle("bundle2");
		ArrayList arrayList = bundle.getParcelableArrayList("arrayList");
		Log.d("arrayList", arrayList.toString());

		ArrayList<Map<String, String>> list = arrayList;
		list = (ArrayList<Map<String, String>>) arrayList;
		Log.d("list", list.get(0).get("title").toString());

		if (arguments != null) {
			Log.d("BUNDLE != null", "NO NULL");
		} else {
			Log.d("BUNDLE == null", "NULL");
		}

		final EditText getTitle = (EditText) v.findViewById(R.id.view_title);
		final Spinner getClient = (Spinner) v.findViewById(R.id.view_clientSpinner);
		final Spinner getPurpose = (Spinner) v.findViewById(R.id.view_purposeSpinner);
		final Button getDateButton = (Button) v.findViewById(R.id.view_date);
		final Button getTimeButton = (Button) v.findViewById(R.id.view_time);
		final EditText getContent = (EditText) v.findViewById(R.id.view_content);
		final EditText getLocation = (EditText) v.findViewById(R.id.view_location);
		final Spinner getRemind = (Spinner) v.findViewById(R.id.view_remind);
		final EditText getRemarks = (EditText) v.findViewById(R.id.view_remarks);
		final Button edit = (Button) v.findViewById(R.id.edit);
		edit.setTypeface(typeface);
		final Button save = (Button) v.findViewById(R.id.save);
		save.setTypeface(typeface);
		final String id = list.get(0).get("id");
		save.setVisibility(View.GONE);

		getTitle.setText(list.get(0).get("title"));
		getTitle.setInputType(InputType.TYPE_NULL);// can't edit
		getClient.setEnabled(false);
		final String client = list.get(0).get("client");
		final String purpose = list.get(0).get("purpose");
		getPurpose.setEnabled(false);
		String date = list.get(0).get("date");
		String time = list.get(0).get("time");
		String content = list.get(0).get("content");
		getDateButton.setClickable(false);
		getTimeButton.setClickable(false);

		getContent.setText(content);

		getContent.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		getContent.setInputType(InputType.TYPE_NULL);
		getRemind.setEnabled(false);
		getContent.setGravity(Gravity.TOP);
		getContent.setSingleLine(false);
		getContent.setHorizontallyScrolling(false);

		String location = list.get(0).get("location");
		getLocation.setText(location);
		getLocation.setInputType(InputType.TYPE_NULL);
		String remarks = list.get(0).get("remarks");
		getRemarks.setText(remarks);
		getRemarks.setInputType(InputType.TYPE_NULL);

		getDateButton.setText(date);
		getTimeButton.setText(time);

		loadClientNameSpinner(getClient, client);
		loadPurposeSpinner(getPurpose, purpose);

		ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_spinner_item,
				remindTime);
		adapterTime
				.setDropDownViewResource(android.R.layout.simple_spinner_item);
		getRemind.setAdapter(adapterTime);

		// edit
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pd = new ProgressDialog(getActivity());
				pd = (ProgressDialog) DialogHelper.mProgressDialog(getActivity());
				pd.show();

				getTitle.setInputType(InputType.TYPE_CLASS_TEXT);
				getContent.setInputType(InputType.TYPE_CLASS_TEXT);
				getLocation.setInputType(InputType.TYPE_CLASS_TEXT);
				getRemarks.setInputType(InputType.TYPE_CLASS_TEXT);
				getClient.setEnabled(true);
				getPurpose.setEnabled(true);
				getRemind.setEnabled(true);
				getDateButton.setClickable(true);
				getTimeButton.setClickable(true);
				getDateButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						DialogHelper.onCreateDateDialog(getActivity(), getDateButton).show();
					}
				});
				getTimeButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						DialogHelper.onCreateTimeDialog(getActivity(), getTimeButton).show();
					}
				});
				createSaveButton(id);

				pd.dismiss();

			}

			private void createSaveButton(final String id) {

				save.setVisibility(View.VISIBLE);
				edit.setVisibility(View.GONE);

				save.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final ArrayList<Map<String, String>> editClientNote = new ArrayList<Map<String, String>>();
						Map<String, String> it = new HashMap<String, String>();

						it.put("title", getTitle.getText().toString());
						it.put("client", getClient.getSelectedItem().toString());
						Log.d("clientSpinner", getClient.getSelectedItem().toString());
						it.put("purpose", getPurpose.getSelectedItem().toString());
						it.put("date", getDateButton.getText().toString());
						it.put("time", getTimeButton.getText().toString());
						it.put("content", getContent.getText().toString());
						it.put("location", getLocation.getText().toString());
						it.put("remind", getRemind.getSelectedItem().toString());
						it.put("remarks", getRemarks.getText().toString());
						editClientNote.add(it);
						Log.d("editClientNote", editClientNote.get(0).toString());
						changeDataToParse(editClientNote, id);

					}
				});

			}

		});
		return v;
	}

	private void loadPurposeSpinner(Spinner Purpose, String selectionPurpose) {
		SpinnerHelper.buildCustomerData(getActivity(), Purpose, "Purpose", "目的", selectionPurpose);
	}

	private void loadClientNameSpinner(final Spinner getClient, final String client) {
		ParseQuery<ParseObject> queryClientName = new ParseQuery<ParseObject>(
				"Client");
		queryClientName.findInBackground(new FindCallback<ParseObject>() {
			ArrayList<String> clientNameArrayList;

			@Override
			public void done(List<ParseObject> objects,
					com.parse.ParseException e) {
				if (e == null) {

					try {
						clientNameArrayList = new ArrayList<String>();
						clientName = objects;
						if (clientName != null) {
							for (ParseObject clientNameObject : clientName) {
								if (clientNameObject.getString("name") != null)
									clientNameArrayList.add(clientNameObject
											.getString("name"));
								Log.d("clientNameArrayList",
										clientNameArrayList.toString());

							}
							c_index = clientNameArrayList.indexOf(client);
							Log.d("clientNameIndexOf", "index" + c_index);

						}
						ArrayAdapter<String> clientNameAdapter = new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_spinner_item,
								clientNameArrayList);
						clientNameAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_item);
						getClient.setAdapter(clientNameAdapter);
						getClient.setSelection(c_index, true);

					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});
	}

	private void changeDataToParse(final ArrayList<Map<String, String>> ed, String id) {
		pd = new ProgressDialog(getActivity());
		pd = (ProgressDialog) DialogHelper.mProgressDialog(getActivity());
		pd.show();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("ClientNote");

		// Retrieve the object by id
		query.getInBackground(id, new GetCallback<ParseObject>() {
			public void done(ParseObject ob, ParseException e) {
				pd.dismiss();
				if (e == null) {
					ob.put("title", ed.get(0).get("title"));
					ob.put("client", ed.get(0).get("client"));
					ob.put("purpose", ed.get(0).get("purpose"));
					ob.put("date", ed.get(0).get("date"));
					ob.put("time", ed.get(0).get("time"));
					ob.put("content", ed.get(0).get("content"));
					ob.put("location", ed.get(0).get("location"));
					ob.put("remind", ed.get(0).get("remind"));
					ob.put("remarks", ed.get(0).get("remarks"));
					ob.saveInBackground(new SaveCallback() {
						@Override
						public void done(ParseException e) {
							Toast.makeText(getActivity(), "編輯成功", Toast.LENGTH_LONG).show();
							// go back to list
							getActivity()
									.getSupportFragmentManager()
									.beginTransaction()
									.replace(R.id.content_frame, new ClientNoteRecordFragment())
									.addToBackStack(null)
									.commit();
						}
					});
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
