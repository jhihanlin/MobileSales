package tw.edu.fju.imd.mobilesales.utils;

import android.content.Context;
import android.graphics.Typeface;

// singleton pattern
public class TypeFaceHelper {

	private static String FONT_PATH = "fonts/Arial.ttf";
	private static Typeface typeface;

	public static Typeface getCurrentTypeface(Context context) {
		if (typeface == null) {
			typeface = Typeface.createFromAsset(context.getAssets(), FONT_PATH);
		}
		return typeface;
	}
}
