package com.abc.drawer_fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.Toast;

import com.abc.model.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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

		mapView = (MapView) v.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		gmap = mapView.getMap();
		locMgr = (LocationManager) getActivity().getSystemService(
				context.LOCATION_SERVICE);
		autoTV = (AutoCompleteTextView) v
				.findViewById(R.id.AutoCompleteTextView1);
		nearButton = (Button) v.findViewById(R.id.nearButton);
		searchButton = (Button) v.findViewById(R.id.btSubmit);

		progressDialog = new ProgressDialog(getActivity());
		loadClientFromParse();

		Criteria criteria = new Criteria();
		bestProv = locMgr.getBestProvider(criteria, true);

		gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // 瓣繡�癡�珍汀冕汀��
		// gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);//癡癒�疑竹蜆岑怏疑把�汕岔汀��
		// gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);// 矇��阬溘紐汀冕汀��
		// gmap.setMapType(GoogleMap.MAP_TYPE_HYBRID);//
		// 矇��阬溘紐汀冕汀�汕溘溼氐岑怏疑把�汕岔汀�����
		gmap.getUiSettings().setZoomGesturesEnabled(true);
		gmap.setMyLocationEnabled(true);// 矇癒簪癟瞻繙癡�¯秉氐溘掙刈蝓岑蝓�
		gmap.setTrafficEnabled(true);// 矇癒簪癟瞻繙瓣繙瞻矇�禳癡糧�￣阬乳�
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
							"You didn't enter the address", Toast.LENGTH_SHORT)
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
							"You didn't enter the address", Toast.LENGTH_SHORT)
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
							"You didn't enter the address", Toast.LENGTH_SHORT)
							.show();
				}

			};

		});

		nearButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> latLongData = getLatlongData();
				for (int i = 0; i < latLongData.size(); i++) {
					double lati = Double.parseDouble(latLongData.get(i).split(
							",")[1].trim());
					double lng = Double.parseDouble(latLongData.get(i).split(
							",")[2].trim());
					gmap.addMarker(new MarkerOptions()
							.position(new LatLng(lati, lng))
							.title(latLongData.get(i).split(",")[0].trim())
							.snippet(latLongData.get(i).split(",")[3].trim()));
				}
			}
		});

		return v;

	}

	private void loadClientFromParse() {
		progressDialog.setTitle("Loading...");
		progressDialog.setCancelable(false);
		progressDialog.show();

		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Client"); // get
		// Parse
		// table:Client
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects,
					com.parse.ParseException e) {
				if (e == null) { // put resule into a variable:clientNames
					clientNames = objects;
					Log.d("debug", "objects.size()=" + objects.size());
				}

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						getActivity(), android.R.layout.simple_list_item_1,
						getAutoCompleteData());
				// 癡穡簫疇簧禳癡�¯秉乒�嫖�〡氐¯姻乒�汕永巫﹦�色��￣氐凌�乒�汕岔氐捍�
				autoTV.setAdapter(adapter);
				progressDialog.dismiss();

				getLatlongData();

			}
		});
	}

	// 疇�笛氐倒arse癟禳�阬喇�￣色�,疇�冕氐癡繚顫疇禮�氐�
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
			Toast.makeText(getActivity().getBaseContext(), "can't found",
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
		super.onResume(); // 疇礎�汍嬝PS疆��岑黑笛阬溘紐氐敉￣刈蝓抽��嘔乒�〣蜆純敷色�甄棺色�冕刈蝓岑蝓�
		mapView.onResume();
		if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locMgr.requestLocationUpdates(bestProv, 1000, 1, this);
		} else {
			Toast.makeText(getActivity().getBaseContext(),
					"please check your GPS on", Toast.LENGTH_SHORT).show();
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
		String x = "Lati=" + Double.toString(location.getLatitude());
		String y = "Long=" + Double.toString(location.getLongitude());

		LatLng point = new LatLng(location.getLatitude(),
				location.getLongitude());
		Log.d("debug", point.toString());
		zoom = 17;

		if (first == true) {
			gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoom));
			first = false;
		}
		gmap.setMyLocationEnabled(true);
		Toast.makeText(getActivity(), x + "\n" + y, Toast.LENGTH_LONG).show();
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