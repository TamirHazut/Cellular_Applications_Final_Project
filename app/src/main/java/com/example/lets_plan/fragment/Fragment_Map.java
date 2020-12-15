package com.example.lets_plan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.lets_plan.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Stack;

public class Fragment_Map extends Fragment {
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private final Stack<MarkerOptions> markers;

    public Fragment_Map() {
        this.markers = new Stack<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        findViews(v);
        initViews();
        this.mMapView.onCreate(savedInstanceState);
        return v;
    }

    private void findViews(View v) {
        this.mMapView = v.findViewById(R.id.mapView);
    }

    private void initViews() {
        this.mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
                MarkerOptions marker = new MarkerOptions();
                while (!markers.isEmpty()) {
                    marker = markers.pop();
                    mGoogleMap.addMarker(marker);
                }
                if (marker.getPosition() != null) {
                    CameraPosition liberty = CameraPosition.builder().target(marker.getPosition()).zoom(16).bearing(0).tilt(45).build();
                    mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(liberty));
                }
            }
        }); //this is important
    }



    @Override
    public void onResume() {
        super.onResume();
        this.mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        this.mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        this.mMapView.onLowMemory();
    }

    public void updateMapFocus(LatLng location) {
        this.updateMapFocus(CameraPosition.builder()
                .target(location)
                .zoom(16)
                .bearing(0)
                .tilt(45)
                .build());
    }

    public void updateMapFocus(CameraPosition cameraPosition) {
        if (this.mGoogleMap != null) {
            this.mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public GoogleMap getGoogleMap() {
        return mGoogleMap;
    }

    public void placeMarker(String title, String snippet, double lat, double lng) {
        this.markers.push(new MarkerOptions().title(title).snippet(snippet).position(new LatLng(lat, lng)));
    }
}