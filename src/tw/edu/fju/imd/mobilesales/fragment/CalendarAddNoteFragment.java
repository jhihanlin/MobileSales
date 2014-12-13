package tw.edu.fju.imd.mobilesales.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tw.edu.fju.imd.mobilesales.R;
import tw.edu.fju.imd.mobilesales.utils.DialogHelper;
import tw.edu.fju.imd.mobilesales.utils.SpinnerHelper;
import tw.edu.fju.imd.mobilesales.utils.TypeFaceHelper;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.widget.Button;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CalendarAddNoteFragment extends Fragment {

	private EditText m_titleText;
	private EditText m_contentText;
	private EditText m_locationText;
	private EditText m_remarksText;
	private Spinner m_purpose;
	private Spinner m_remind;
	private Spinner m_client;
	private Button m_datepickerButton = null;
	private Button m_timepickerButton = null;
	private Button saveButton;
	private Calendar c = null;
	protected List<ParseObject> purpose;
	protected List<ParseObject> clientName;
	private ProgressDialog pd;

	String[] remindTime = new String[] { "10 minutes ago", "15 minutes ago",
			"30 minutes ago", "1 hour ago", "3 hours ago", "12 hours ago",
			"1 day ago" };

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.calendar_note_layout, container, false);
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());

		m_titleText = (EditText) v.findViewById(R.id.title);
		m_purpose = (Spinner) v.findViewById(R.id.purposeSpinner_1);
		m_client = (Spinner) v.findViewById(R.id.clientSpinner_1);
		m_contentText = (EditText) v.findViewById(R.id.contextText_1);
		m_locationText = (EditText) v.findViewById(R.id.locationText_1);
		m_remind = (Spinner) v.findViewById(R.id.remindSpinner_1);
		m_remarksText = (EditText) v.findViewById(R.id.remarksText_1);
		m_datepickerButton = (Button) v.findViewById(R.id.datepickerButton_1);
		m_timepickerButton = (Button) v.findViewById(R.id.timepickerButton_1);
		saveButton = (Button) v.findViewById(R.id.save_1);
		saveButton.setTypeface(typeface);
		Button cancelButton = (Button) v.findViewById(R.id.cancel_1);
		cancelButton.setTypeface(typeface);
		// remind Spinner
		ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_spinner_item,
				remindTime);
		adapterTime
				.setDropDownViewResource(android.R.layout.simple_spinner_item);
		m_remind.setAdapter(adapterTime);
		SpinnerHelper.buildCustomerData(getActivity(), m_purpose, "Purpose", "目的", null);

		ParseQuery<ParseObject> queryClientName = new ParseQuery<ParseObject>(
				"Client"); // get Parse table:ClientNote
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
						}
						ArrayAdapter<String> clientNameAdapter = new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_spinner_item,
								clientNameArrayList);
						clientNameAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_item);
						m_client.setAdapter(clientNameAdapter);
					} catch (Exception e2) {
						e2.printStackTrace();
					}

				}
			}
		});
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
		String str = formatter.format(curDate);

		m_datepickerButton.setText(str);
		Time t = new Time();
		t.setToNow();
		int hour = t.hour; // 0-23
		int minute = t.minute;
		m_timepickerButton.setText(hour + " : " + minute);

		// DatePickerDialog
		m_datepickerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogHelper.onCreateDateDialog(getActivity(), m_datepickerButton).show();
			}
		});

		// TimePickerDialog
		m_timepickerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogHelper.onCreateTimeDialog(getActivity(), m_timepickerButton).show();
			}
		});

		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String title = m_titleText.getText().toString();
				String client = m_client.getSelectedItem().toString();
				String purpose = m_purpose.getSelectedItem().toString();
				Log.d("purpose", purpose);
				String content = m_contentText.getText().toString();
				String date = m_datepickerButton.getText().toString();
				String time = m_timepickerButton.getText().toString();
				String location = m_locationText.getText().toString();
				String remind = m_remind.getSelectedItem().toString();
				String remarks = m_remarksText.getText().toString();

				pd = new ProgressDialog(getActivity());
				pd = (ProgressDialog) DialogHelper.mProgressDialog(getActivity());
				pd.show();

				ParseObject object = new ParseObject("ClientNote");
				object.put("client", client);
				object.put("title", title);
				object.put("purpose", purpose);
				object.put("content", content);
				object.put("date", date);
				object.put("time", time);
				object.put("location", location);
				object.put("remind", remind);
				object.put("remarks", remarks);
				object.setACL(new ParseACL(ParseUser.getCurrentUser()));
				object.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						pd.dismiss();
						if (e == null) {
							Toast.makeText(getActivity(), "Successful",
									Toast.LENGTH_SHORT).show();
							FragmentManager fragmentManager = getFragmentManager();
							fragmentManager.beginTransaction()
									.replace(R.id.content_frame, new CalendarFragment())
									.addToBackStack(null)
									.commit();
						} else {
							Toast.makeText(getActivity(), "Error",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, new CalendarFragment())
						.addToBackStack(null)
						.commit();
			}
		});

		return v;
	}
}