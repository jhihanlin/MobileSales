package tw.edu.fju.imd.mobilesales.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.edu.fju.imd.mobilesales.R;
import tw.edu.fju.imd.mobilesales.utils.DialogHelper;
import tw.edu.fju.imd.mobilesales.utils.TypeFaceHelper;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class BoardHistoryFragment extends Fragment {

	public BoardHistoryFragment() {
	}

	private ListView board_list;
	private ProgressDialog pd;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.board_listview, container, false);
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());
		board_list = (ListView) v.findViewById(R.id.board_list);
		loadDataFromParse();

		return v;
	}

	private void loadDataFromParse() {
		pd = new ProgressDialog(getActivity());
		pd = (ProgressDialog) DialogHelper.mProgressDialog(getActivity());
		pd.show();

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
				"Board");
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				pd.dismiss();

				final ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
				final ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

				for (ParseObject ob : objects) {
					Map<String, String> item = new HashMap<String, String>();
					item.put("content", ob.getString("content"));
					item.put("username", ob.getString("username"));
					item.put("id", ob.getObjectId());

					data.add(item);

				}
				try {
					final SimpleAdapter adapter = new SimpleAdapter(getActivity(),
							data, android.R.layout.simple_list_item_2,
							new String[] { "content", "username" }, new int[] {
									android.R.id.text1, android.R.id.text2 });
					board_list.setAdapter(adapter);
					board_list.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {

						}
					});
					board_list.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

							DialogHelper.showDeleteDialog(getActivity(), "Board", data, position,
									adapter);
							return true;
						}
					});
				} catch (Exception e2) {
					e2.printStackTrace();
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
