package com.abc.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SignUpCallback;

public class LoginActivity extends Activity {

	private EditText accountEditText;
	private EditText passwordEditText;
	private Button registerButton;
	private Button loginButton;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		Parse.initialize(this, "8mNYYPLOR08iJAkCt535lP8BfOcNo1ouO2bTbdte",
				"5Jsm0reTBpRnhope1dRrmXMgpCZjXCO40jlAYBdC");

		Log.d("debug", "login activity");

		progressDialog = new ProgressDialog(this);

		// PushService.setDefaultPushCallback(this, MainActivity.class);
		// ParseInstallation.getCurrentInstallation().saveInBackground();
		// ParseAnalytics.trackAppOpened(getIntent());

		accountEditText = (EditText) findViewById(R.id.accountEditText);
		passwordEditText = (EditText) findViewById(R.id.passwordEditText);
		loginButton = (Button) findViewById(R.id.loginButton);
		registerButton = (Button) findViewById(R.id.registerButton);

		loginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String account = accountEditText.getText().toString();
				String passwrod = passwordEditText.getText().toString();
				Log.d("debug", "login:" + account);

				progressDialog.setCancelable(false);
				progressDialog.setTitle("Loading...");
				progressDialog.show();
				ParseUser.logInInBackground(account, passwrod,
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
								}
							}
						});
			}
		});
		registerButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String account = accountEditText.getText().toString();
				String passwrod = passwordEditText.getText().toString();

				Log.d("debug", "signup:" + account);

				progressDialog.setCancelable(false);
				progressDialog.setTitle("Loading...");
				progressDialog.show();

				final ParseUser user = new ParseUser();
				user.setUsername(account);
				user.setPassword(passwrod);
				user.signUpInBackground(new SignUpCallback() {

					@Override
					public void done(ParseException e) {

						progressDialog.dismiss();

						if (e != null) {
							e.printStackTrace();
						} else {
							goToMainActivity();
							LoginActivity.this.finish();
						}
					}
				});
			}
		});
	}

	private void goToMainActivity() {
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
	}

}
