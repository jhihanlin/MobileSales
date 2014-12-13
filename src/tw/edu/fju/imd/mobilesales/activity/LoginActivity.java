package tw.edu.fju.imd.mobilesales.activity;

import tw.edu.fju.imd.mobilesales.R;
import tw.edu.fju.imd.mobilesales.utils.DialogHelper;
import tw.edu.fju.imd.mobilesales.utils.TypeFaceHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends Activity {

	private EditText accountEditText;
	private EditText passwordEditText;
	private Button registerButton;
	private Button loginButton;
	private CheckBox loginCheckBox;
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Log.d("debug", "login activity");

		sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
		editor = sp.edit();

		Typeface typeface = TypeFaceHelper.getCurrentTypeface(this);

		// action bar (title)
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTypeface(typeface);

		accountEditText = (EditText) findViewById(R.id.accountEditText);
		accountEditText.setTypeface(typeface);

		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		passwordEditText.setTypeface(typeface);
		setupUI(findViewById(R.id.loginLayout));
		loginCheckBox = (CheckBox) findViewById(R.id.loginCheckbox);
		loginCheckBox.setTypeface(typeface);

		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setTypeface(typeface);

		registerButton = (Button) findViewById(R.id.registerButton);
		registerButton.setTypeface(typeface);

		// login
		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				loginFromParse();
			}
		});

		// register
		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = accountEditText.getText().toString();
				String password = passwordEditText.getText().toString();
				boolean checked = loginCheckBox.isChecked();
				editor.putString("username", username);
				editor.putString("password", password);
				editor.putBoolean("checked", checked);
				editor.commit();

				if ("".equals(accountEditText.getText().toString().trim())) {
					showInputErrorDialog();
				}
				else if (accountEditText.getText().toString().trim().length() > 10) {
					showInputErrorDialog();
				}
				else {

					Log.d("debug", "signup:" + username);
					DialogHelper.mProgressDialog(LoginActivity.this).show();
					final ParseUser user = new ParseUser();
					user.setUsername(username);
					user.setPassword(password);
					user.put("checked", checked);
					Log.d("debug", "checked:" + "status" + loginCheckBox.isChecked());
					user.signUpInBackground(new SignUpCallback() {

						@Override
						public void done(ParseException e) {
							DialogHelper.mProgressDialog(LoginActivity.this).dismiss();
							if (e != null) {
								e.printStackTrace();
								Toast.makeText(LoginActivity.this, "Username already exists",
										Toast.LENGTH_LONG).show();
							} else {
								goToMainActivity();
								LoginActivity.this.finish();
							}
						}
					});
				}

			}
		});
		loadDataFromSharedPreference();
	}

	private void loadDataFromSharedPreference() {
		accountEditText.setText(sp.getString("username", ""));
		passwordEditText.setText(sp.getString("password", ""));
		loginCheckBox.setChecked(sp.getBoolean("checked", false));
		if (sp.getBoolean("checked", false)) {
			if (ParseUser.getCurrentUser() != null) {
				loginFromParse();
			}
		}
	}

	private void goToMainActivity() {
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
	}

	private void loginFromParse() {
		pd = new ProgressDialog(LoginActivity.this);
		pd = (ProgressDialog) DialogHelper.mProgressDialog(LoginActivity.this);
		pd.show();
		String username = accountEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		boolean checked = loginCheckBox.isChecked();
		Log.d("debug", "login:" + username);
		Log.d("debug", "checked:" + "status" + loginCheckBox.isChecked());

		editor.putString("username", username);
		editor.putString("password", password);
		editor.putBoolean("checked", checked);
		editor.commit();
		boolean sp_checked = sp.getBoolean("checked", false);
		if (sp_checked) {
			final ParseUser user = new ParseUser();
			user.put("checked", checked);
			user.saveInBackground();
		}
		ParseUser.logInInBackground(username, password,
				new LogInCallback() {
					@Override
					public void done(ParseUser user,
							com.parse.ParseException e) {
						pd.dismiss();
						if (user != null && e == null) {
							goToMainActivity();
							LoginActivity.this.finish();
						}
						if (e != null) {
							e.printStackTrace();
							Toast.makeText(LoginActivity.this, "Login failed",
									Toast.LENGTH_LONG).show();
						}
					}
				});
	}

	private void showInputErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
		builder.setTitle("Tips");
		builder.setMessage("You must input a-z or 0-9 & length < 10");
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});
		builder.show();
	}

	public void setupUI(View view) {

		// Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {

			view.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					hideSoftKeyboard(LoginActivity.this);
					return false;
				}

			});
		}
	}

	public void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
	}
}
