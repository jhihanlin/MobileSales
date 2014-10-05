package com.abc.model;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SignUpCallback;

public class LoginActivity extends Activity {

	private EditText accountEditText;
	private EditText passwordEditText;
	private Button registerButton;
	private Button loginButton;
	private CheckBox loginCheckBox;
	private ProgressDialog progressDialog;

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		Log.d("debug", "login activity");

		progressDialog = new ProgressDialog(this);// loading bar

		sp = getSharedPreferences("settings", Context.MODE_PRIVATE);// use
																	// SharedPreferences
																	// to store
																	// data
		editor = sp.edit();// editor is SharedPReferences one of class

		Typeface typeface = Typeface.createFromAsset(getAssets(),
				"fonts/NotoSansCJKtc-Thin.otf");// font

		// action bar (title)
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTypeface(typeface);

		accountEditText = (EditText) findViewById(R.id.accountEditText);
		accountEditText.setTypeface(typeface);

		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		passwordEditText.setTypeface(typeface);

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

					progressDialog.setCancelable(false);
					progressDialog.setTitle("Loading...");
					progressDialog.show();

					final ParseUser user = new ParseUser();
					user.setUsername(username);
					user.setPassword(password);
					user.put("checked", checked);
					Log.d("debug", "checked:" + "status" + loginCheckBox.isChecked());
					user.signUpInBackground(new SignUpCallback() {

						@Override
						public void done(ParseException e) {

							progressDialog.dismiss();
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
		String username = accountEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		boolean checked = loginCheckBox.isChecked();
		Log.d("debug", "login:" + username);
		Log.d("debug", "checked:" + "status" + loginCheckBox.isChecked());

		editor.putString("username", username); // when user keyin owen
												// username it will put
												// in editor
		editor.putString("password", password);
		editor.putBoolean("checked", checked);
		editor.commit();
		boolean sp_checked = sp.getBoolean("checked", false);
		if (sp_checked) {
			final ParseUser user = new ParseUser();
			user.put("checked", checked);
			user.saveInBackground();
		}
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		ParseUser.logInInBackground(username, password,
				new LogInCallback() {
					@Override
					public void done(ParseUser user,
							com.parse.ParseException e) {
						progressDialog.dismiss();

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
}
