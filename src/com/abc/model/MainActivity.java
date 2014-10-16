package com.abc.model;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abc.drawer_fragment.CalendarFragment;
import com.abc.drawer_fragment.ClientNoteList;
import com.abc.drawer_fragment.MessageList;
import com.abc.drawer_fragment.Notify;
import com.abc.drawer_fragment.People;
import com.abc.drawer_fragment.Board;
import com.abc.drawer_fragment.Search;
import com.abc.model.R;
import com.abc.model.utils.TypeFaceHelper;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class MainActivity extends Activity {
	private DrawerLayout my_DrawerLayout;
	private ListView my_DrawerList;
	private RelativeLayout my_LeftDrawer;
	private ActionBarDrawerToggle my_DrawerToggle;

	private CharSequence my_DrawerTitle;
	private CharSequence my_Title;
	private String[] my_PlanetTitles;
	private ProgressDialog progressDialog;
	private static final int PHOTO_SUCCESS = 1;
	boolean doubleBackToExitPressedOnce = false;

	ImageView profile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Typeface typeface = TypeFaceHelper.getCurrentTypeface(this);
		progressDialog = new ProgressDialog(this);
		my_Title = my_DrawerTitle = getTitle();

		my_PlanetTitles = getResources().getStringArray(R.array.planets_array);
		my_DrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		my_DrawerList = (ListView) findViewById(R.id.left_drawer_list);
		my_LeftDrawer = (RelativeLayout) findViewById(R.id.left_drawer);
		TextView username = (TextView) findViewById(R.id.username);
		Button select_photo = (Button) findViewById(R.id.select_photo);
		Button logoutBtn = (Button) findViewById(R.id.logoutBtn);
		profile = (ImageView) findViewById(R.id.profile);
		loadFromParse();

		logoutBtn.setTypeface(typeface);

		logoutBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ParseUser.logOut();
				goToLoginActivity();
				MainActivity.this.finish();
			}
		});
		select_photo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle("編輯照片");
				builder.setPositiveButton("從相簿選取相片", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("image/*");
						intent.addCategory(Intent.CATEGORY_OPENABLE);
						startActivityForResult(intent, PHOTO_SUCCESS);

					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				builder.show();
			}
		});
		username.setText(ParseUser.getCurrentUser().getUsername());
		username.setTypeface(typeface);
		// set a custom shadow that overlays the main content when the drawer
		// opens
		// my_DrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
		// GravityCompat.START);

		// set up the drawer's list view with items and click listener
		my_DrawerList.setAdapter(new MenuAdapter(this));
		my_DrawerList.setOnItemClickListener(new DrawerItemClickListener());
		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		my_DrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		my_DrawerLayout, /* DrawerLayout object */
		R.drawable.drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
				) {
					public void onDrawerClosed(View view) {
						getActionBar().setTitle(my_Title);
						invalidateOptionsMenu(); // creates call to
													// onPrepareOptionsMenu()
					}

					public void onDrawerOpened(View drawerView) {
						getActionBar().setTitle(my_DrawerTitle);
						invalidateOptionsMenu(); // creates call to
													// onPrepareOptionsMenu()
					}
				};
		my_DrawerLayout.setDrawerListener(my_DrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = my_DrawerLayout.isDrawerOpen(my_LeftDrawer);
		menu.findItem(R.id.action_notice).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (my_DrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.action_notice:
			getFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new Notify()).commit();
			break;
		// case R.id.action_websearch:
		// // create intent to perform web search for this planet
		// Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
		// intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
		// // catch event that there's no activity to handle intent
		// if (intent.resolveActivity(getPackageManager()) != null) {
		// startActivity(intent);
		// } else {
		// Toast.makeText(this, R.string.app_not_available,
		// Toast.LENGTH_LONG).show();
		// }
		// return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments

		FragmentManager fragmentManager = getFragmentManager();
		switch (position) {
		case 0: {
			fragmentManager.beginTransaction()
					.add(R.id.content_frame, new People())
					.addToBackStack(null)
					.commit();
			break;
		}

		case 1: {
			fragmentManager.beginTransaction()
					.add(R.id.content_frame, new ClientNoteList())
					.addToBackStack(null)
					.commit();
			break;
		}
		case 2: {
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, new CalendarFragment())
					.addToBackStack(null)
					.commit();
			break;
		}
		case 3: {
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, new Search())
					.addToBackStack(null)
					.commit();
			break;
		}
		case 4: {
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, new MessageList())
					.addToBackStack(null)
					.commit();

			break;
		}
		case 5: {
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, new Board())
					.addToBackStack(null)
					.commit();

			break;
		}
		default:
			return;

		}

		my_DrawerList.setItemChecked(position, true);
		setTitle(my_PlanetTitles[position]);

		my_DrawerLayout.closeDrawer(my_LeftDrawer);
	}

	private void goToLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);
	}

	@Override
	public void setTitle(CharSequence title) {
		my_Title = title;
		getActionBar().setTitle(my_Title);

		// action bar (title)
		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		TextView yourTextView = (TextView) findViewById(titleId);
		yourTextView.setTypeface(TypeFaceHelper.getCurrentTypeface(this));
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		my_DrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		my_DrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("debug", "photo" + resultCode);
		Uri selectedImageUri = data.getData();
		profile.setImageURI(selectedImageUri);

		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		saveToParse(selectedImageUri);
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void saveToParse(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 90, baos);
		byte[] bytes = baos.toByteArray();

		final ParseFile file = new ParseFile("photo.png", bytes);
		ParseUser user = ParseUser.getCurrentUser();
		user.put("file", file);
		user.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				progressDialog.dismiss();
				if (e == null) {
					String url = file.getUrl();
					Log.d("debug", url);
				}
			}
		});
	}

	private void saveToParse(Uri uri) {
		try {
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					getContentResolver(), uri);
			saveToParse(bitmap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadFromParse() {

		ParseUser user = ParseUser.getCurrentUser();

		ParseFile file = user.getParseFile("file");
		try {
			if (file != null) {
				byte[] data = file.getData();
				Bitmap bitmap = BitmapFactory.decodeByteArray(data,
						0, data.length);
				profile.setImageBitmap(bitmap);
			}

		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		if (getFragmentManager().getBackStackEntryCount() == 1) {
			if (doubleBackToExitPressedOnce) {
				finish();
				return;
			}

			this.doubleBackToExitPressedOnce = true;
			Toast.makeText(this, "再按一次退出MobileSailes",
					Toast.LENGTH_SHORT).show();

			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					doubleBackToExitPressedOnce = false;
				}
			}, 2000);

		} else {
			super.onBackPressed();
		}

	}

}