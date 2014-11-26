package tw.edu.fju.imd.mobilesales.fragment;

import java.util.ArrayList;
import java.util.List;

import tw.edu.fju.imd.mobilesales.R;
import tw.edu.fju.imd.mobilesales.utils.SpinnerHelper;
import tw.edu.fju.imd.mobilesales.utils.TypeFaceHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MessageAddFragment extends Fragment {

	private Button sendButton, cancelButton, peopleButton;
	private EditText contentEditText, subjectEditText;
	private TextView receiverTextView;
	private Spinner modelSpinner;
	private ProgressDialog progressDialog;
	private AlertDialog peopleDialog;
	protected List<ParseObject> messageModels, peoples;
	protected String receiverPhoneNumbers , receiverEmail;

	public MessageAddFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.message_layout, container, false);
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());

		subjectEditText = (EditText) v.findViewById(R.id.subjectET);
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

		final OnItemSelectedListener oldListener = modelSpinner
				.getOnItemSelectedListener();
		modelSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
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
				onCreateDialog();
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, new MessageHistoryFragment())
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
		SpinnerHelper.buildCustomerData(getActivity(), modelSpinner,
				"MessageModel", "範本", null);
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

	protected Dialog onCreateDialog() {
		Dialog dialog = null;
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.message_send_method, null);

		new AlertDialog.Builder(getActivity()).setTitle("選擇寄送方式").setView(v)
				.setPositiveButton("確認", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CheckBox mailCB = (CheckBox) v
								.findViewById(R.id.mailCheckBox);
						CheckBox msmCB = (CheckBox) v
								.findViewById(R.id.msmCheckBox);

						if (mailCB.isChecked()) {
							Intent it2 = new Intent(Intent.ACTION_SEND);
							String[] receiver = null;
							if (receiverEmail.contains(",")) {

								receiver = receiverEmail.split(",");

							}

							it2.putExtra(Intent.EXTRA_EMAIL, receiver);
							it2.putExtra(Intent.EXTRA_TEXT, contentEditText
									.getText().toString());
							it2.putExtra(Intent.EXTRA_SUBJECT, subjectEditText
									.getText().toString());
							it2.setType("message/rfc822");
							startActivity(Intent.createChooser(it2, "寄送工具"));

						}
						if (msmCB.isChecked()) {
							String content = contentEditText.getText()
									.toString();

							String receiver = receiverPhoneNumbers;
							if (receiver != null) {
								Log.d("receiver", receiver);
								progressDialog.setCancelable(false);
								progressDialog.setTitle("Loading...");
								progressDialog.show();

								ParseObject object = new ParseObject("Message");
								object.put("Message_content", content);
								object.put("Message_receiver", receiverTextView
										.getText().toString());
								object.setACL(new ParseACL(ParseUser
										.getCurrentUser()));
								object.saveInBackground(new SaveCallback() {

									public void done(ParseException e) {
										progressDialog.dismiss();
										if (e == null) {
											try {
												Toast.makeText(
														getActivity()
																.getBaseContext(),
														"成功送出訊息",
														Toast.LENGTH_SHORT)
														.show();
												SmsManager smsManager = SmsManager
														.getDefault();
												String[] temp = null;
												if (receiverPhoneNumbers
														.contains(",")) {

													temp = receiverPhoneNumbers
															.split(",");

												}
												for (int i = 0; i < temp.length; i++) {

													smsManager.sendTextMessage(
															temp[i].toString(),
															null,
															contentEditText
																	.getText()
																	.toString(),
															PendingIntent
																	.getBroadcast(
																			getActivity(),
																			0,
																			new Intent(),
																			0),
															null);

												}
											} catch (Exception exception) {
												exception.printStackTrace();
											}
										}
									}

								});
							} else {
								Toast.makeText(getActivity().getBaseContext(),
										"請選擇聯絡人", Toast.LENGTH_SHORT).show();
							}

						}

					}
				})

				.setNeutralButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();

		return dialog;
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
									MessageExpandableFragment mEX = (MessageExpandableFragment) getActivity().getSupportFragmentManager()
											.findFragmentById(R.id.fragment1);

									receiverPhoneNumbers = mEX
											.getPhoneNumbers();
									receiverEmail = mEX.getEmail();
									receiverTextView.setText(mEX.getName());

									Log.d("123", "123");

								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// Canceled.
								}
							}).show();
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