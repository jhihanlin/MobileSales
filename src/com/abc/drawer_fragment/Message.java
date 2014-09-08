package com.abc.drawer_fragment;

import com.abc.model.R;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.content.Context;
import android.widget.AdapterView.OnItemSelectedListener;

public class Message extends Fragment {
	/** Called when the activity is first created. */
	private View v;
	private EditText editTextReceiver;
	private EditText editTextContent;
	private Button buttonSend;
	private Button buttonCancel;
	private Context mContext;
	private ArrayAdapter<String> lunchList;
	private Spinner spinner;
	private String[] lunch = { "Hello^_^", "How are you?", "Good Morning!",
			"Good Evening!", "Thank tou!" };

	public Message() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.message_layout, container, false);
		editTextReceiver = (EditText) v.findViewById(R.id.EditTextReceiver);
		editTextContent = (EditText) v.findViewById(R.id.EditTextContent);
		buttonSend = (Button) v.findViewById(R.id.ButtonSend);
		buttonCancel = (Button) v.findViewById(R.id.ButtonCancel);
		mContext = this.getActivity();
		spinner = (Spinner) v.findViewById(R.id.spinner1);
		lunchList = new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_spinner_item, lunch);
		spinner.setAdapter(lunchList);
		final Activity activity = getActivity();
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				editTextContent.setText("" + lunch[position]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		buttonCancel.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				editTextContent.setText("");
				editTextReceiver.setText("");
				// åˆ©ç”¨Toastçš„éœæ…‹å‡½å¼makeTextä¾†å»ºç«‹Toastç‰©ä»¶
				Toast.makeText(getActivity().getBaseContext(), "Clear!",
						Toast.LENGTH_SHORT).show();
			}
		});

		buttonSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// åˆ©ç”¨Toastçš„éœæ…‹å‡½å¼makeTextä¾†å»ºç«‹Toastç‰©ä»¶
				Toast.makeText(getActivity().getBaseContext(), "Sent!",
						Toast.LENGTH_SHORT).show();
				SmsManager smsManager = SmsManager.getDefault();
				try {
					smsManager.sendTextMessage(editTextReceiver.getText()
							.toString(), null, editTextContent.getText()
							.toString(), PendingIntent.getBroadcast(
							getActivity(), 0, new Intent(), 0), null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				;
				editTextContent.setText("");
			}

		});
		return v;

	}
}