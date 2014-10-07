package com.abc.drawer_fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.abc.model.R;
import com.abc.model.utils.TypeFaceHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class Search extends Fragment implements LocationListener {

	AutoCompleteTextView autoTV;
	private ProgressDialog progressDialog;
	protected List<ParseObject> clientNames;
	private GoogleMap gmap;
	private MapView mapView;
	float zoom = 17;
	double lat, lng;
	private LocationManager locMgr;
	private String bestProv;
	private Context context;
	private boolean first = true;
	private Button nearButton, searchButton;
	private MarkerOptions markOption;

	public Search() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.search_layout, container, false);
		Typeface typeface = TypeFaceHelper.getCurrentTypeface(getActivity());

		mapView = (MapView) v.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		gmap = mapView.getMap();
		locMgr = (LocationManager) getActivity().getSystemService(
				context.LOCATION_SERVICE);
		autoTV = (AutoCompleteTextView) v
				.findViewById(R.id.AutoCompleteTextView1);
		nearButton = (Button) v.findViewById(R.id.nearButton);
		nearButton.setTypeface(typeface);
		searchButton = (Button) v.findViewById(R.id.btSubmit);
		searchButton.setTypeface(typeface);
		progressDialog = new ProgressDialog(getActivity());
		loadClientFromParse();

		Criteria criteria = new Criteria();
		bestProv = locMgr.getBestProvider(criteria, true);

		gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		gmap.getUiSettings().setZoomGesturesEnabled(true);
		gmap.setMyLocationEnabled(true);
		gmap.setTrafficEnabled(true);
		gmap.getUiSettings().setZoomGesturesEnabled(true);
		gmap.setMyLocationEnabled(true);

		Circle circle = gmap.addCircle(new CircleOptions()
				.center(new LatLng(-33.87365, 151.20689)).radius(10000)
				.strokeColor(Color.RED).fillColor(Color.BLUE));

		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String locationName = autoTV.getText().toString().trim();

				if (locationName.length() > 0) {
					locationNameToMarker(locationName);
				} else {
					Toast.makeText(getActivity().getBaseContext(),
							"請輸入地址", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		autoTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(android.widget.AdapterView<?> parent,
					View view, int position, long id) {

				String locationName = autoTV.getText().toString().split(",")[1]
						.trim();

				if (locationName.length() > 0) {
					locationNameToMarker(locationName);
				} else {
					Toast.makeText(getActivity().getBaseContext(),
							"請輸入地址", Toast.LENGTH_SHORT)
							.show();
				}
			};
		});

		autoTV.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				String locationName = autoTV.getText().toString().trim();

				if (locationName.length() > 0) {
					locationNameToMarker(locationName);
				} else {
					Toast.makeText(getActivity().getBaseContext(),
							"請輸入地址", Toast.LENGTH_SHORT)
							.show();
				}

			};

		});

		nearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final List<String> latLongData = getLatlongData();

				for (int i = 0; i < latLongData.size(); i++) {
					final String ii = latLongData.get(i).toString();
					double lati = Double.parseDouble(latLongData.get(i).split(
							",")[1].trim());
					double lng = Double.parseDouble(latLongData.get(i).split(
							",")[2].trim());
					gmap.addMarker(new MarkerOptions()
							.position(new LatLng(lati, lng))
							.title(latLongData.get(i).split(",")[0].trim())
							.snippet(ii));

					gmap.setOnMarkerClickListener(new OnMarkerClickListener() {

						@Override
						public boolean onMarkerClick(Marker arg0) {
							Log.d("debug", arg0.toString());

							Dialog onCreateDialog = onCreateDialog(arg0
									.getSnippet());
							return false;
						}
					});
				}
			}
		});

		return v;

	}

	protected Dialog onCreateDialog(final String string) {

		Dialog dialog = null;
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.search_marker, null);

		TextView nameTV = (TextView) v.findViewById(R.id.nameTextView);
		TextView telTV = (TextView) v.findViewById(R.id.telTextView);
		TextView addTV = (TextView) v.findViewById(R.id.addTextView);

		telTV.setText("電話： " + string.split(",")[3].trim());
		addTV.setText("地址： " + string.split(",")[4].trim());

		new AlertDialog.Builder(getActivity())
				.setTitle(string.split(",")[0].trim())
				.setView(v)
				.setPositiveButton("撥打電話",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String uri = "tel:"
										+ string.split(",")[3].trim();
								Intent intent = new Intent(Intent.ACTION_CALL);
								intent.setData(Uri.parse(uri));
								startActivity(intent);
							}
						})

				.setNeutralButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();
		return dialog;
	}

	private void loadClientFromParse() {
		progressDialog.setTitle("Loading...");
		progressDialog.setCancelable(false);
		progressDialog.show();

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Client"); // get
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects,
					com.parse.ParseException e) {
				if (e == null) { 
					clientNames = objects;
					Log.d("debug", "objects.size()=" + objects.size());
				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						getAutoCompleteData());
				autoTV.setAdapter(adapter);
				progressDialog.dismiss();

				getLatlongData();

			}
		});
	}

	private List<String> getAutoCompleteData() {

		List<String> data = new ArrayList<String>();
		if (clientNames != null) {
			for (ParseObject clientName : clientNames) {
				Log.d("debug", clientName.getString("name"));
				String nameAndAddress = "";
				if ((clientName.getString("name")) != null) {
					nameAndAddress += clientName.getString("name");
				}
				if ((clientName.getString("add")) != null) {
					nameAndAddress += "," + clientName.getString("add");
				}
				data.add(nameAndAddress);
			}

		}

		return data;
	}

	private List<String> getLatlongData() {

		List<String> data1 = new ArrayList<String>();
		if (clientNames != null) {

			for (ParseObject clientName : clientNames) {
				Log.d("debug", clientName.getString("name"));
				String nameAndLatlong = "";

				if ((clientName.getString("addLatLong")) != null) {

					if ((clientName.getString("name")) != null) {

						if ((clientName.getString("addLatLong")) != null) {
							nameAndLatlong += clientName.getString("name");
							nameAndLatlong += ","
									+ clientName.getString("addLatLong");

							if ((clientName.getString("tel")) != null) {
								nameAndLatlong += ","
										+ clientName.getString("tel");
							}
							if ((clientName.getString("add")) != null) {
								nameAndLatlong += ","
										+ clientName.getString("add");
							}
						}

					}
					data1.add(nameAndLatlong);

				}
			}

		}

		return data1;

	}

	protected void showMarkOption(String locationName[]) {
		// TODO Auto-generated method stub
		gmap.clear();

		// String locationName1 =
		// autoTV.getText().toString().split(",")[1].trim();
		// String locationName = autoTV.getText().toString().trim();
		// getLatlongData().getText().toString().split(",")[1].trim();

		List<String> latLongData = getLatlongData();

		for (int i = 0; i < latLongData.size(); i++) {
			double lati = Double.parseDouble(latLongData.get(i).split(",")[1]
					.trim());
			double lng = Double.parseDouble(latLongData.get(i).split(",")[2]
					.trim());
			gmap.addMarker(new MarkerOptions().position(new LatLng(lati, lng))
					.title(latLongData.get(i).split(",")[0].trim()));
		}

	}

	protected void locationNameToMarker(String locationName) {
		// TODO Auto-generated method stub
		gmap.clear();
		Geocoder gecoder = new Geocoder(getActivity());
		List<Address> addressList = null;
		int maxResults = 1;
		try {
			addressList = gecoder.getFromLocationName(locationName, maxResults);
		} catch (IOException e) {
			Log.e("GeocoderActivity", e.toString());
		}

		if (addressList == null || addressList.isEmpty()) {
			Toast.makeText(getActivity().getBaseContext(), "找不到資料",
					Toast.LENGTH_SHORT).show();
		} else {

			Address address = addressList.get(0);

			LatLng position = new LatLng(address.getLatitude(),
					address.getLongitude());

			String snippet = address.getAddressLine(0);

			// LatLng postion;
			gmap.addMarker(new MarkerOptions().position(position)
					.title(locationName).snippet(snippet));

			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(position).zoom(15).build();
			gmap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));

		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
		if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locMgr.requestLocationUpdates(bestProv, 1000, 1, this);
		} else {
			Toast.makeText(getActivity().getBaseContext(),
					"請開啟GPS功能", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mapView.onPause();
		locMgr.removeUpdates(this);
	}

	private boolean isMapReady() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLocationChanged(Location location) {
		String x = "緯=" + Double.toString(location.getLatitude());
		String y = "經=" + Double.toString(location.getLongitude());

		LatLng point = new LatLng(location.getLatitude(),
				location.getLongitude());
		Log.d("debug", point.toString());
		zoom = 17;

		if (first == true) {
			gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoom));
			first = false;
		}
		gmap.setMyLocationEnabled(true);
		// Toast.makeText(getActivity(), x + "\n" + y,
		// Toast.LENGTH_LONG).show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MapsInitializer.initialize(getActivity());

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

}