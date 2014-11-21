package tw.edu.fju.imd.mobilesales.adapter;

import tw.edu.fju.imd.mobilesales.R;
import tw.edu.fju.imd.mobilesales.R.array;
import tw.edu.fju.imd.mobilesales.R.drawable;
import tw.edu.fju.imd.mobilesales.R.layout;
import tw.edu.fju.imd.mobilesales.utils.TypeFaceHelper;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PeopleContactAdapter extends BaseAdapter {
	Context context;
	private String[] titles;
	private int[] images;

	public PeopleContactAdapter(Context context) {
		this.context = context;
		titles = context.getResources().getStringArray(R.array.planets_array);
		images = new int[] { R.drawable.people_icon, R.drawable.client_note_icon, R.drawable.calendar_icon, R.drawable.searchs_icon, R.drawable.messages_icon, R.drawable.broadcast_icon, R.drawable.no };
	}

	@Override
	public int getCount() {

		return titles.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater li = LayoutInflater.from(context);
		View view = li.inflate(R.layout.drawer_list_item2, null);


		return view;
	}
}