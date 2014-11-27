package tw.edu.fju.imd.mobilesales.utils;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.parse.ParseObject;

public class DialogHelper {
	public static void showDeleteDialog(Activity activity, final String className, final List<Map<String, String>> data, final int index, final BaseAdapter adapter) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("是否刪除");
		builder.setPositiveButton("刪除", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String Object_id = data.get(index).get("id");

				Log.d("id", Object_id);
				ParseObject obj = ParseObject.createWithoutData(
						className, Object_id);
				obj.deleteEventually();
				data.remove(index);

				adapter.notifyDataSetChanged();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();
	}

	// DatePickerDialog
	public static Dialog onCreateDateDialog(Activity activity, final Button btn) {
		Calendar c = null;
		Dialog dialog = null;

		c = Calendar.getInstance();
		dialog = new DatePickerDialog(activity,
				new DatePickerDialog.OnDateSetListener() {
					public void onDateSet(DatePicker dp, int year, int month,
							int dayOfMonth) {
						String text = String.format("%d/%02d/%02d", year,
								(month + 1), dayOfMonth);
						btn.setText(text);
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH));
		return dialog;
	}

	// TimePickerDialog
	public static Dialog onCreateTimeDialog(Activity activity, final Button btn) {
		Calendar c = null;
		Dialog dialog2 = null;
		c = Calendar.getInstance();
		dialog2 = new TimePickerDialog(activity,
				new TimePickerDialog.OnTimeSetListener() {
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						btn.setText(hourOfDay + ":" + minute);
					}
				}, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
		return dialog2;
	}

}
