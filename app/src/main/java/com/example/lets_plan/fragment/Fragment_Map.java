package com.example.lets_plan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.lets_plan.R;
import com.example.lets_plan.data.EventHall;
import com.example.lets_plan.logic.PermissionHandlerSingleton;
import com.example.lets_plan.logic.MapDataHandler;
import com.example.lets_plan.logic.callback.DataClickedListener;
import com.example.lets_plan.logic.callback.DataReadyInterface;
import com.example.lets_plan.logic.utils.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

public class Fragment_Map extends Fragment_Base {
    private MapView mMapView;
    private MapDataHandler mapDataHandler;


    public Fragment_Map() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        this.mapDataHandler = MapDataHandler.getInstance();
        this.mapDataHandler.setAPIKey(getActivity().getResources().getString(R.string.search_api_key));
        this.mapDataHandler.setDataClickedListener(new DataClickedListener<EventHall>() {
            @Override
            public void dataClicked(EventHall data) {
                saveToSharedPreferences(Constants.CURRENT_EVENT_HALL, toJson(data, EventHall.class));
                switchParentFragment(R.id.main_FGMT_container, new Fragment_Event_Hall(), true);
            }
        });
        PermissionHandlerSingleton.getInstance().setDataReadyInterface(new DataReadyInterface() {
            @Override
            public void dataReady() {
                mapDataHandler.setCurrentLocationReady(true);
                mapDataHandler.setSettingsAfterMapAndDataAreReady();
            }
        });
        PermissionHandlerSingleton.getInstance().checkLocationPermission();
        findViews(v);
        initViews();
        this.mMapView.onCreate(savedInstanceState);
        return v;
    }

    private void findViews(View v) {
        this.mMapView = (MapView) v.findViewById(R.id.mapView);
    }

    private void initViews() {
        this.mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapDataHandler.mapReady(googleMap);
            }
        });
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

}