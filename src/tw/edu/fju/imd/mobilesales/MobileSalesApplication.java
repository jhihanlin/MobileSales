package tw.edu.fju.imd.mobilesales;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import tw.edu.fju.imd.mobilesales.activity.MainActivity;

import com.parse.Parse;
import com.parse.PushService;

import android.app.Application;

public class MobileSalesApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Fabric.with(this, new Crashlytics());
		Parse.initialize(this, "8mNYYPLOR08iJAkCt535lP8BfOcNo1ouO2bTbdte",
				"5Jsm0reTBpRnhope1dRrmXMgpCZjXCO40jlAYBdC");
		PushService.setDefaultPushCallback(this, MainActivity.class);
		PushService.subscribe(this, "all", MainActivity.class);
	}
}
