package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.abc.drawer_fragment.MessageExpandable.SavedTabsListAdapter;
import com.abc.model.R;
import com.abc.model.utils.SpinnerHelper;
import com.abc.model.utils.TypeFaceHelper;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class Message extends Fragment {

	private Button sendButton, cancelButton, peopleButton;
	private EditText contentEditText;
	private TextView receiverTextView;
	private Spinner modelSpinner;
	private ProgressDialog progressDialog;
	private AlertDialog peopleDialog;
	protected List<ParseObject> messageModels, peoples;
	protected String receiverPhoneNumbers;

	public Message() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.message_layout, container, false);
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());
		TextView c_purpose = (TextView) v.findViewById(R.id.c_purpose);
		c_purpose.setTypeface(typeface);
		TextView text11 = (TextView) v.findViewById(R.id.text11);
		text11.setTypeface(typeface);
		sendButton = (Button) v.findViewById(R.id.sendButton);
		sendButton.setTypeface(typeface);
		cancelButton = (Button) v.findViewById(R.id.cancelButton);
		cancelButton.setTypeface(typeface);
		peopleButton = (Button) v.findViewById(R.id.peopleButton);
		contentEditText = (EditText) v.findViewById(R.id.contentEditText);
		modelSpinner = (Spinner) v.findViewById(R.id.modelSpinner);
		receiverTextView = (TextView) v.findViewById(R.id.receiverTextView);

		progressDialog = new ProgressDialog(getActivity());
		loadMessageModelFromParse();

		final OnItemSelectedListener oldListener = modelSpinner.getOnItemSelectedListener();
		modelSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent,
					View view, int position, long id) {
				if (oldListener != null) {
					oldListener.onItemSelected(parent, view, position, id);
				}
				String sModel = (String) modelSpinner.getSelectedItem();
				contentEditText.setText(sModel);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				if (oldListener != null) {
					oldListener.onNothingSelected(parent);
				}
			}
		});

		sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = contentEditText.getText().toString();

				String receiver = receiverPhoneNumbers;
				if (receiver != null) {
					Log.d("receiver", receiver);
					progressDialog.setCancelable(false);
					progressDialog.setTitle("Loading...");
					progressDialog.show();

					ParseObject object = new ParseObject("Message");
					object.put("Message_content", content);
					object.put("Message_receiver", receiverTextView.getText()
							.toString());
					object.setACL(new ParseACL(ParseUser.getCurrentUser()));
					object.saveInBackground(new SaveCallback() {

						public void done(ParseException e) {
							progressDialog.dismiss();
							if (e == null) {
								try {
									Toast.makeText(getActivity().getBaseContext(), "成功送出訊息",
											Toast.LENGTH_SHORT).show();
									SmsManager smsManager = SmsManager.getDefault();
									String[] temp = null;
									if (receiverPhoneNumbers.contains(",")) {

										temp = receiverPhoneNumbers.split(",");

									}
									for (int i = 0; i < temp.length; i++) {

										smsManager.sendTextMessage(temp[i].toString(), null,
												contentEditText.getText().toString(),
												PendingIntent.getBroadcast(getActivity(), 0,
														new Intent(), 0), null);

									}
								} catch (Exception exception) {
									exception.printStackTrace();
								}
							}
						}

					});
				} else {
					Toast.makeText(getActivity().getBaseContext(), "請選擇聯絡人",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, new MessageList())
						.commit();
			}
		});

		peopleButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				onCreateDialog1();
			}
		});
		return v;
	}

	private void loadMessageModelFromParse() {
		SpinnerHelper.buildCustomerData(getActivity(), modelSpinner, "MessageModel", "範本", null);
	}

	protected List<String> getPeopleData() {
		List<String> data = new ArrayList<String>();
		if (peoples != null) {
			for (ParseObject message : peoples) {
				Log.d("debug", message.getString("name"));
				String nameAndTel = "";
				if ((message.getString("name")) != null) {
					nameAndTel += message.getString("name");
				}
				if ((message.getString("tel")) != null) {
					nameAndTel += "," + message.getString("tel");
				}
				if ((message.getString("tag")) != null) {
					nameAndTel += "," + message.getString("tag");
				}
				data.add(nameAndTel);
			}

		}

		return data;
	}

	protected void onCreateDialog1() {

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		if (peopleDialog == null) {
			View expandableFragment = inflater.inflate(
					R.layout.message_expandable_fragment, null);
			peopleDialog = new AlertDialog.Builder(getActivity())
					.setTitle("選擇聯絡人")
					.setView(expandableFragment)
					.setPositiveButton("完成",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									MessageExpandable mEX = (MessageExpandable) getFragmentManager()
											.findFragmentById(R.id.fragment1);

									receiverPhoneNumbers = mEX
											.getPhoneNumbers();
									receiverTextView.setText(mEX.getName());
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// Canceled.
								}
							})
					.show();
		} else {
			peopleDialog.show();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

}