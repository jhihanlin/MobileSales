package tw.edu.fju.imd.mobilesales.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.edu.fju.imd.mobilesales.PeopleContactAdapter;
import tw.edu.fju.imd.mobilesales.R;
import tw.edu.fju.imd.mobilesales.utils.SpinnerHelper;
import tw.edu.fju.imd.mobilesales.utils.TypeFaceHelper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class PeopleContactView extends Fragment {
	protected List<ParseObject> clientName;
	protected List<ParseObject> purposeName;
	private ProgressDialog progressDialog;

	public PeopleContactView() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.people_contact_view, container, false);

		progressDialog = new ProgressDialog(getActivity());

		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());

		Bundle arguments = getArguments();
		Log.d("bundle2", arguments.getBundle("bundle2").toString());
		Bundle bundle = arguments.getBundle("bundle2");
		ArrayList arrayList = bundle.getParcelableArrayList("arrayList");
		Log.d("arrayList", arrayList.toString());

		ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) arrayList;

		if (arguments != null) {
			Log.d("BUNDLE != null", "NO NULL");
		} else {
			Log.d("BUNDLE == null", "NULL");
		}

		final TextView getName = (TextView) v.findViewById(R.id.contact_name);
		final ImageView getPhoto = (ImageView) v.findViewById(R.id.contact_pic);
		final ListView listView = (ListView) v.findViewById(R.id.contact_listview);

		final Button edit = (Button) v.findViewById(R.id.edit);
		edit.setTypeface(typeface);
		final Button delete = (Button) v.findViewById(R.id.delete);
		delete.setTypeface(typeface);
		getName.setText(list.get(0).get("name").toString());
		Log.d("debug", getName.toString());
		final ArrayList<Map<String, String>> getContactData = new ArrayList<Map<String, String>>();

		for (int i = 0; i < list.get(0).size(); i++) {

			Map<String, String> item = new HashMap<String, String>();
			item.put("name", list.get(0).get("name"));
			item.put("tag", list.get(0).get("tag"));
			item.put("birthday", list.get(0).get("birthday"));
			item.put("tel", list.get(0).get("tel"));
			item.put("email", list.get(0).get("email"));
			item.put("add", list.get(0).get("add"));
			item.put("note", list.get(0).get("note"));
			item.put("id", list.get(0).get("id"));

			getContactData.add(item);

			Log.d("debug", getContactData.toString());
			listView.setAdapter(new PeopleContactAdapter(getActivity()));
		}
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

}
