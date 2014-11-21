package tw.edu.fju.imd.mobilesales.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import tw.edu.fju.imd.mobilesales.R;
import tw.edu.fju.imd.mobilesales.activity.MainActivity;
import tw.edu.fju.imd.mobilesales.utils.SpinnerHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.DatePicker;

import com.google.android.gms.maps.model.LatLng;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class People_add extends Fragment {
	private Button savePeople, delPeople, imbtnupdata, editPeople,
			photoButton;
	Button DatePicker = null;
	private EditText edtname, edttel, edtemail, edtadd, edtnote;
	private ImageView photoImage;
	private Spinner sptag;
	public String mode = "";
	public String ID = "";
	public String textData = "";
	// public DatePicker DatePicker;
	Calendar c;
	private ProgressDialog progressDialog;

	private Uri outputFile;
	private static final int TAKE_PHOTO_REQUEST_CODE = 0;
	private static final int OPEN_ALBUM_REQUEST_CODE = 1;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.people_add, container, false);

		final Calendar TodayDate = Calendar.getInstance();
		final int sYear = TodayDate.get(Calendar.YEAR);
		final int sMon = TodayDate.get(Calendar.MONTH) + 1;
		final int sDay = TodayDate.get(Calendar.DAY_OF_MONTH);
		textData = DateFix(sYear) + "/" + DateFix(sMon) + "/" + DateFix(sDay);

		progressDialog = new ProgressDialog(getActivity());

		editPeople = (Button) v.findViewById(R.id.editPeople);
		savePeople = (Button) v.findViewById(R.id.savePeople);
		delPeople = (Button) v.findViewById(R.id.deletePeople);
		edtname = (EditText) v.findViewById(R.id.edtname);
		edttel = (EditText) v.findViewById(R.id.edttel);
		edtemail = (EditText) v.findViewById(R.id.edtemail);
		edtadd = (EditText) v.findViewById(R.id.edtaddress);
		sptag = (Spinner) v.findViewById(R.id.sptag);
		edtnote = (EditText) v.findViewById(R.id.edtNote);

		photoImage = (ImageView) v.findViewById(R.id.photoImage);
		photoButton = (Button) v.findViewById(R.id.photoButton);
		photoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setTitle("設置大頭貼");
				builder.setPositiveButton("從相簿",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(
										Intent.ACTION_GET_CONTENT);
								intent.setType("image/*");
								intent.addCategory(Intent.CATEGORY_OPENABLE);
								startActivityForResult(intent,
										OPEN_ALBUM_REQUEST_CODE);

							}
						});
				builder.setNeutralButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				builder.setNegativeButton("拍照",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								outputFile = getOutputFile();

								Intent intent = new Intent();
								intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										outputFile);
								startActivityForResult(intent,
										TAKE_PHOTO_REQUEST_CODE);

							}

							private Uri getOutputFile() {
								// TODO Auto-generated method stub
								File dcimDir = Environment
										.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
								if (dcimDir.exists() == false) {
									dcimDir.mkdirs();
								}

								File file = new File(dcimDir, "photo.png");
								return Uri.fromFile(file);
							}
						});
				builder.show();
			}
		});

		DatePicker = (Button) v.findViewById(R.id.datepickerButton11);

		if (mode.equals("add")) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
			Date curDate = new Date(System.currentTimeMillis());
			String str = formatter.format(curDate);

			DatePicker.setText(str);
			DatePicker.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					onCreateDialog(DatePicker).show();
				}
			});

			Log.v("", textData);
			savePeople.setVisibility(View.VISIBLE);
			delPeople.setVisibility(View.GONE);
			editPeople.setVisibility(View.GONE);
			SpinnerHelper.buildCustomerData(getActivity(), sptag, "Tag", "標籤", null);
		}

		if (mode.equals("edit")) {
			savePeople.setVisibility(View.GONE);
			photoButton.setEnabled(false);
			edtname.setEnabled(false);
			edttel.setEnabled(false);
			edtemail.setEnabled(false);
			edtadd.setEnabled(false);
			sptag.setEnabled(false);
			DatePicker.setEnabled(false);
			edtnote.setEnabled(false);
			delPeople.setVisibility(View.VISIBLE);
			setEditMode();
		}

		editPeople.setOnClickListener(new OnClickListener() {

			// ADD
			public void onClick(View v) {
				editPeople.setVisibility(View.GONE);
				savePeople.setVisibility(View.VISIBLE);
				photoButton.setEnabled(true);
				edtname.setEnabled(true);
				edttel.setEnabled(true);
				edtemail.setEnabled(true);
				edtadd.setEnabled(true);
				sptag.setEnabled(true);
				DatePicker.setEnabled(true);
				edtnote.setEnabled(true);
				delPeople.setVisibility(View.GONE);
			}
		});

		savePeople.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mode.equals("add")) {
					btnadd();
				}
				if (mode.equals("edit")) {
					btnedit();
				}
			}
		});

		delPeople.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDeleteDialog();
			}
		});

		return v;

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
			if (resultCode == getActivity().RESULT_OK) {
				photoImage.setImageURI(outputFile);
				photoImage.buildDrawingCache();
				final Bitmap bitmap = photoImage.getDrawingCache();

				savePeople.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mode.equals("add")) {
							btnadd(bitmap);
						}
						if (mode.equals("edit")) {
							btnedit(bitmap);
						}
					}
				});

			}
		} else if (requestCode == OPEN_ALBUM_REQUEST_CODE) {
			if (resultCode == getActivity().RESULT_OK) {

				Uri selectedImageUri = data.getData();
				photoImage.setImageURI(selectedImageUri);
				try {
					final Bitmap bitmap = MediaStore.Images.Media.getBitmap(
							getActivity().getContentResolver(),
							selectedImageUri);

					savePeople.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (mode.equals("add")) {
								btnadd(bitmap);
							}
							if (mode.equals("edit")) {
								btnedit(bitmap);
							}
						}
					});

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Log.d("debug", data.getData().toString());
			}
		}
	}

	public void btnadd() {
		textData = DatePicker.getText().toString();
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();

		ParseObject object = new ParseObject("Client");
		object.put("name", edtname.getText().toString());
		object.put("birthday", textData);
		object.put("tel", edttel.getText().toString());
		object.put("email", edtemail.getText().toString());
		object.put("add", edtadd.getText().toString());
		String tag = sptag.getSelectedItem().toString();
		object.put("tag", tag);

		Geocoder gecoder = new Geocoder(getActivity());
		List<Address> addressList = null;
		int maxResults = 1;
		try {
			addressList = gecoder.getFromLocationName(edtadd.getText()
					.toString(), maxResults);
		} catch (IOException e) {
			Log.e("GeocoderActivity", e.toString());
		}

		if (addressList == null || addressList.isEmpty()) {

		} else {

			Address address = addressList.get(0);
			LatLng position = new LatLng(address.getLatitude(),
					address.getLongitude());
			String positionString = position.latitude + ","
					+ position.longitude;
			object.put("addLatLong", positionString);

		}

		if (checktext()) {
			object.setACL(new ParseACL(ParseUser.getCurrentUser()));
			object.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException e) {
					progressDialog.dismiss();
					if (e == null) {
						Toast.makeText(getActivity(), "Successful",
								Toast.LENGTH_SHORT).show();
						getActivity().getSupportFragmentManager().beginTransaction()
								.replace(R.id.content_frame, new People())
								.addToBackStack(null)
								.commit();
					} else {
						Toast.makeText(getActivity(), "Error",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		} else {
			progressDialog.dismiss();
		}

	}

	public void btnadd(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 90, baos);
		byte[] bytes = baos.toByteArray();
		final ParseFile file = new ParseFile("photo.png", bytes);

		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();

		ParseObject object = new ParseObject("Client");
		object.put("photo", file);
		object.put("name", edtname.getText().toString());
		object.put("birthday", textData);
		object.put("tel", edttel.getText().toString());
		object.put("email", edtemail.getText().toString());
		object.put("add", edtadd.getText().toString());
		String tag = sptag.getSelectedItem().toString().equals("NO TAG") ? ""
				: sptag.getSelectedItem().toString();
		object.put("tag", tag);

		Geocoder gecoder = new Geocoder(getActivity());
		List<Address> addressList = null;
		int maxResults = 1;
		try {
			addressList = gecoder.getFromLocationName(edtadd.getText()
					.toString(), maxResults);
		} catch (IOException e) {
			Log.e("GeocoderActivity", e.toString());
		}

		if (addressList == null || addressList.isEmpty()) {

		} else {

			Address address = addressList.get(0);
			LatLng position = new LatLng(address.getLatitude(),
					address.getLongitude());
			String positionString = position.latitude + ","
					+ position.longitude;
			object.put("addLatLong", positionString);

		}

		if (checktext()) {
			object.setACL(new ParseACL(ParseUser.getCurrentUser()));

			object.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException e) {
					progressDialog.dismiss();
					if (e == null) {
						Toast.makeText(getActivity(), "Successful",
								Toast.LENGTH_SHORT).show();
						getActivity().getSupportFragmentManager().beginTransaction()
								.replace(R.id.content_frame, new People())
								.addToBackStack(null)
								.commit();
					} else {
						Toast.makeText(getActivity(), "Error",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}

	}

	public void btnedit() {
		textData = DatePicker.getText().toString();
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
		Log.v("", "mode=" + mode);
		Log.v("", "ID=" + ID);

		query.getInBackground(ID, new GetCallback<ParseObject>() {// GUERY

					public void done(ParseObject object, ParseException e) {
						if (e == null) {
							progressDialog.dismiss();
							object.put("name", edtname.getText().toString());
							object.put("birthday", textData);
							object.put("tel", edttel.getText().toString());
							object.put("email", edtemail.getText().toString());
							object.put("add", edtadd.getText().toString());
							String tag = sptag.getSelectedItem().toString()
									.equals("NO TAG") ? "" : sptag
									.getSelectedItem().toString();
							object.put("tag", tag);

							Geocoder gecoder = new Geocoder(getActivity());
							List<Address> addressList = null;
							int maxResults = 1;
							try {
								addressList = gecoder
										.getFromLocationName(edtadd.getText()
												.toString(), maxResults);
							} catch (IOException e1) {
								Log.e("GeocoderActivity", e1.toString());
							}

							if (addressList == null || addressList.isEmpty()) {

							} else {

								Address address = addressList.get(0);
								LatLng position = new LatLng(address
										.getLatitude(), address.getLongitude());
								String positionString = position.latitude + ","
										+ position.longitude;
								object.put("addLatLong", positionString);

							}

							if (checktext()) {
								object.setACL(new ParseACL(ParseUser
										.getCurrentUser()));

								object.saveInBackground(new SaveCallback() {

									@Override
									public void done(ParseException e) {
										progressDialog.dismiss();
										if (e == null) {
											Toast.makeText(getActivity(),
													"Successful",
													Toast.LENGTH_SHORT).show();
											getActivity()
													.getSupportFragmentManager()
													.beginTransaction()
													.replace(
															R.id.content_frame,
															new People())
													.addToBackStack(null)
													.commit();
										} else {
											Toast.makeText(getActivity(),
													"Error", Toast.LENGTH_SHORT)
													.show();
										}
									}
								});
							}
						}
					}
				});
	}

	public void btnedit(Bitmap bitmap) {
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 90, baos);
		byte[] bytes = baos.toByteArray();
		final ParseFile file = new ParseFile("photo.png", bytes);
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");

		Log.v("", "mode=" + mode);
		Log.v("", "ID=" + ID);

		query.getInBackground(ID, new GetCallback<ParseObject>() {// GUERY

					public void done(ParseObject object, ParseException e) {
						if (e == null) {
							progressDialog.dismiss();

							object.put("photo", file);

							object.put("name", edtname.getText().toString());
							object.put("birthday", textData);
							object.put("tel", edttel.getText().toString());
							object.put("email", edtemail.getText().toString());
							object.put("add", edtadd.getText().toString());
							String tag = sptag.getSelectedItem().toString()
									.equals("NO TAG") ? "" : sptag
									.getSelectedItem().toString();
							object.put("tag", tag);

							Geocoder gecoder = new Geocoder(getActivity());
							List<Address> addressList = null;
							int maxResults = 1;
							try {
								addressList = gecoder
										.getFromLocationName(edtadd.getText()
												.toString(), maxResults);
							} catch (IOException e1) {
								Log.e("GeocoderActivity", e1.toString());
							}

							if (addressList == null || addressList.isEmpty()) {

							} else {

								Address address = addressList.get(0);
								LatLng position = new LatLng(address
										.getLatitude(), address.getLongitude());
								String positionString = position.latitude + ","
										+ position.longitude;
								object.put("addLatLong", positionString);

							}

							if (checktext()) {
								object.setACL(new ParseACL(ParseUser
										.getCurrentUser()));

								object.saveInBackground(new SaveCallback() {

									@Override
									public void done(ParseException e) {
										progressDialog.dismiss();
										if (e == null) {
											Toast.makeText(getActivity(),
													"編輯成功",
													Toast.LENGTH_SHORT).show();
											getActivity()
													.getSupportFragmentManager()
													.beginTransaction()
													.replace(
															R.id.content_frame,
															new People())
													.addToBackStack(null)
													.commit();
										} else {
											Toast.makeText(getActivity(),
													"錯誤", Toast.LENGTH_SHORT)
													.show();
										}
									}
								});
							}
						}
					}
				});
	}

	public void btndel() {
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
		Log.v("", "mode=" + mode);
		Log.v("", "ID=" + ID);

		query.getInBackground(ID, new GetCallback<ParseObject>() {

			public void done(ParseObject object, ParseException e) {
				progressDialog.dismiss();
				if (e == null) {
					object.deleteInBackground(new DeleteCallback() {

						@Override
						public void done(ParseException ex) {
							if (ex == null) {

								getActivity()
										.getSupportFragmentManager()
										.beginTransaction()
										.replace(R.id.content_frame,
												new People())
										.addToBackStack(null)
										.commit();
							} else {

							}
						}
					});
				}
			}
		});
	}

	public void showDeleteDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("是否刪除此聯絡人");
		builder.setPositiveButton("確認",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						btndel();
					}
				});
		builder.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.show();
	}

	protected Dialog onCreateDialog(final Button btn) {
		Dialog dialog = null;
		c = Calendar.getInstance();
		dialog = new DatePickerDialog(getActivity(),
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

	public String DateFix(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	public void setMode(String setmode) {
		this.mode = setmode;
	}

	public void setID(String setid) {
		this.ID = setid;
	}

	public boolean checktext() {
		boolean check = true;
		String errorMessage = "";
		if (edttel.getText().toString().equals("")) {
			errorMessage += "請輸入電話。";
			check = false;
		}

		if (edtadd.getText().toString().equals("")) {
			errorMessage += "請輸入地址。";
			check = false;
		}
		if (check == false) {
			Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
		}

		return check;
	}

	private void setEditMode() {
		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		ParseQuery<ParseObject> query = ParseQuery
				.getQuery("Client");

		query.getInBackground(ID,
				new GetCallback<ParseObject>() {

					public void done(ParseObject object,
							ParseException e) {
						progressDialog.dismiss();

						SpinnerHelper.buildCustomerData(getActivity(), sptag, "Tag", "標籤", object.getString("tag"));

						if (e == null) {

							ParseFile file = object
									.getParseFile("photo");

							if (file != null) {
								try {
									byte[] data = file
											.getData();
									Bitmap bitmap = BitmapFactory
											.decodeByteArray(
													data, 0,
													data.length);
									photoImage
											.setImageBitmap(bitmap);

								} catch (ParseException e1) {
									e1.printStackTrace();
								}
							}
							if (object.get("birthday").equals(
									"")) {
								SimpleDateFormat formatter = new SimpleDateFormat(
										"yyyy/MM/dd");
								Date curDate = new Date(System
										.currentTimeMillis());
								String str = formatter
										.format(curDate);

								DatePicker.setText(str);
								DatePicker
										.setOnClickListener(new View.OnClickListener() {
											@Override
											public void onClick(
													View v) {

												onCreateDialog(
														DatePicker)
														.show();
											}
										});
								textData = DatePicker.getText()
										.toString();
							} else {
								Calendar c = Calendar
										.getInstance();
								c.set(Calendar.YEAR,
										Integer.parseInt(object
												.get("birthday")
												.toString()
												.substring(0, 4)));
								c.set(Calendar.MONTH,
										Integer.parseInt(object
												.get("birthday")
												.equals("") ? "123"
												: object.get(
														"birthday")
														.toString()
														.substring(
																5,
																7))); // 將月份改成1月
								c.set(Calendar.DAY_OF_MONTH,
										Integer.parseInt(object
												.get("birthday")
												.toString()
												.substring(8,
														10))); // 將日改成31日
								SimpleDateFormat formatter = new SimpleDateFormat(
										"yyyy/MM/dd");
								Date curDate = new Date(c
										.getTimeInMillis());
								String str = formatter
										.format(curDate);

								DatePicker.setText(str);
								DatePicker
										.setOnClickListener(new View.OnClickListener() {
											@Override
											public void onClick(
													View v) {

												onCreateDialog(
														DatePicker)
														.show();
											}
										});
								textData = DatePicker.getText()
										.toString();

							}
							edtname.setText(object.get("name") == null ? ""
									: object.get("name")
											.toString());
							edttel.setText(object.get("tel") == null ? ""
									: object.get("tel")
											.toString());
							edtemail.setText(object
									.get("email") == null ? ""
									: object.get("email")
											.toString());
							edtadd.setText(object.get("add") == null ? ""
									: object.get("add")
											.toString());

						}
					}
				});
		query.clearCachedResult();
	}

}