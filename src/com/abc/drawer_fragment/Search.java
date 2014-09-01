package com.abc.drawer_fragment;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abc.model.MainActivity;
import com.abc.model.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Search extends Fragment implements LocationListener {

	private GoogleMap gmap;
	float zoom;
	double lat, lng;
	private Button searchButton;
	private EditText locationText;
	private LocationManager locMgr;
	private String bestProv;
	private Context context;
	private boolean first=true;
	public Search() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.search_layout, container, false);

		gmap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();

		LatLng Taipei101 = new LatLng(25.033611, 121.565000); // 台北 101
		zoom = 17;
		gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(Taipei101, zoom));
		gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // 一般地圖

		locMgr = (LocationManager) getActivity().getSystemService(
				context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		bestProv = locMgr.getBestProvider(criteria, true);

		searchButton = (Button) v.findViewById(R.id.btSubmit);
		locationText = (EditText) v.findViewById(R.id.etLocationName);

		gmap.getUiSettings().setZoomGesturesEnabled(true);
		gmap.setMyLocationEnabled(true);

		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String locationName = locationText.getText().toString().trim();

				if (locationName.length() > 0) {
					locationNameToMarker(locationName);
				} else {
					Toast.makeText(getActivity().getBaseContext(), "You didn't enter the address",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		return v;

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
		super.onResume(); // 如果GPS或網路定位開啟，更新位置

		if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locMgr.requestLocationUpdates(bestProv, 1000, 1, this);
		} else {
			Toast.makeText(getActivity().getBaseContext(), "please check and open your internet",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		locMgr.removeUpdates(this);
	}

	private boolean isMapReady() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLocationChanged(Location location) {

		String x = "緯=" + Double.toString(location.getLatitude());
		String y = "經=" + Double.toString(location.getLongitude());
		LatLng Point = new LatLng(location.getLatitude(),
				location.getLongitude());
		zoom = 17; // 設定放大倍率1(地球)-21(街景)

	
		if (first == true) {
			gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(Point, zoom));
			first = false;
		}
		gmap.setMyLocationEnabled(true); 
		Toast.makeText(getActivity(), x + "\n" + y, Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

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