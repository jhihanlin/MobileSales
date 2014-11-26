package tw.edu.fju.imd.mobilesales.utils;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.BaseAdapter;

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
}
