package com.abc.drawer_fragment;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import com.abc.model.R;

public class CalendarFragment extends Fragment {
	private Button dateBtn = null;
	private Calendar c = null;
	private EditText showDate;
	private EditText editNote;
	private Button sendBtn;
	public CalendarFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}


	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.calender_layout, container, false);
		showDate = (EditText) v.findViewById(R.id.showDate);
		editNote = (EditText) v.findViewById(R.id.editNote);
		sendBtn = (Button) v.findViewById(R.id.sendBtn);
		
		Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Quicksand-Regular.ttf");
		showDate.setTypeface(typeface);
		
		if (container == null)
			return null;
		
		dateBtn = (Button) v.findViewById(R.id.dateBtn);
		dateBtn.setTypeface(typeface);
		dateBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 onCreateDialog(dateBtn).show();
			}
		});
		return v;
	}
	protected Dialog onCreateDialog(final Button btn) {
        Dialog dialog = null;
        c = Calendar.getInstance();
        	    dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
//                        btn.setText(year + "年" + (month + 1) + "月" + dayOfMonth + "日");
                    	showDate.setText(year + "年" + (month + 1) + "月" + dayOfMonth + "日");
                    }
                }, c.get(Calendar.YEAR), //傳入年份
                        c.get(Calendar.MONTH), // 傳入月份
                        c.get(Calendar.DAY_OF_MONTH) // 傳入天數
                );
               
                return dialog;
    }
	
}
