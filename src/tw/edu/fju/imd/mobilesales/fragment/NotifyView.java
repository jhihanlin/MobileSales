package tw.edu.fju.imd.mobilesales.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import tw.edu.fju.imd.mobilesales.R;
import tw.edu.fju.imd.mobilesales.utils.TypeFaceHelper;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class NotifyView extends Fragment {
	protected List<ParseObject> clientName;
	protected List<ParseObject> purposeName;
	Calendar c = null;

	int c_index;
	int p_index;
	private ProgressDialog progressDialog;

	String[] remindTime = new String[] { "10 minutes ago", "15 minutes ago",
			"30 minutes ago", "1 hour ago", "3 hours ago", "12 hours ago",
			"1 day ago" };

	public NotifyView() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.notify_view, container, false);
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());

		progressDialog = new ProgressDialog(getActivity());// loading
		// bar
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();

		Bundle arguments = getArguments();
		Log.d("bundle2", arguments.getBundle("bundle2").toString());
		Bundle bundle = arguments.getBundle("bundle2");
		ArrayList arrayList = new ArrayList();
		arrayList = bundle.getParcelableArrayList("arrayList");
		Log.d("arrayList", arrayList.toString());

		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
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
		final String id = list.get(0).get("id");

		getTitle.setText(list.get(0).get("title"));
		getTitle.setInputType(InputType.TYPE_NULL);// can't edit

		final String client = list.get(0).get("client");
		getClient.setEnabled(false);

		final String purpose = list.get(0).get("purpose");
		getPurpose.setEnabled(false);

		String date = list.get(0).get("date");
		String time = list.get(0).get("time");
		getDateButton.setClickable(false);
		getTimeButton.setClickable(false);

		String content = list.get(0).get("content");

		getContent.setText(content);

		getContent.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		getContent.setInputType(InputType.TYPE_NULL);

		// 文本显示的位置在EditText的最上方
		getContent.setGravity(Gravity.TOP);
		// 改变默认的单行模式
		getContent.setSingleLine(false);
		// 水平滚动设置为False
		getContent.setHorizontallyScrolling(false);
		getRemind.setEnabled(false);

		String location = list.get(0).get("location");
		getLocation.setText(location);
		getLocation.setInputType(InputType.TYPE_NULL);
		String remarks = list.get(0).get("remarks");
		getRemarks.setText(remarks);
		getRemarks.setInputType(InputType.TYPE_NULL);

		getDateButton.setText(date);
		getTimeButton.setText(time);
		getDateButton.setClickable(false);
		getTimeButton.setClickable(false);
		getRemind.setEnabled(false);

		loadClientNameSpinner(getClient, client);
		loadPurposeSpinner(getPurpose, purpose, progressDialog);

		ArrayAdapter<String> adapterTime = new ArrayAdapter<String>(
				this.getActivity(), android.R.layout.simple_spinner_item,
				remindTime);
		adapterTime
				.setDropDownViewResource(android.R.layout.simple_spinner_item);
		getRemind.setAdapter(adapterTime);

		return v;
	}

	private void loadPurposeSpinner(final Spinner getPurpose, final String purpose, final ProgressDialog progressDialog) {
		ParseQuery<ParseObject> queryPurpose = new ParseQuery<ParseObject>(
				"Purpose");
		queryPurpose.findInBackground(new FindCallback<ParseObject>() {
			ArrayList<String> purposeArrayList;

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {

					try {
						purposeArrayList = new ArrayList<String>();
						purposeName = objects;
						if (purposeName != null) {
							for (ParseObject purposeObject : purposeName) {
								if (purposeObject.getString("name") != null)
									purposeArrayList.add(purposeObject
											.getString("name"));
								Log.d("purposeArrayList",
										purposeArrayList.toString());

							}
							p_index = purposeArrayList.indexOf(purpose);
							Log.d("pIndexOf", "index" + p_index);

						}
						ArrayAdapter<String> purposeNameAdapter = new ArrayAdapter<String>(
								getActivity(),
								android.R.layout.simple_spinner_item,
								purposeArrayList);
						purposeNameAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_item);
						getPurpose.setAdapter(purposeNameAdapter);
						getPurpose.setSelection(p_index, true);
						progressDialog.dismiss();

					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

}
