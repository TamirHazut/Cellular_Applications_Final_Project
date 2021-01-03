package com.example.lets_plan.logic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.lets_plan.logic.utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

public class LocationHandlerSingleton {
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng currentLocation;
    private static LocationHandlerSingleton instance;

    private LocationHandlerSingleton(Context context) {
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new LocationHandlerSingleton(context.getApplicationContext());
        }
    }
    public void getLastLocation(Activity activity) {
        try {
            if (checkPermissions(activity)) {
                if (isLocationEnabled(activity)) {
                    this.mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData(activity);
                            } else {
                                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                Gson gson = new Gson();
                                SharedPreferencesSingleton.getInstance().getPrefs().edit().putString(Constants.LOCATION, gson.toJson(currentLocation)).apply();
                            }
                        }
                    });
                } else {
                    Toast.makeText(activity, "This app needs location services", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(intent);
                }
            } else {
                requestPermissions(activity);
            }
        } catch (SecurityException ex) {
            Toast.makeText(activity, "This app needs location services", Toast.LENGTH_LONG).show();
        }
    }

    private void requestNewLocationData(Activity activity) {
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(5);
            mLocationRequest.setFastestInterval(0);
            mLocationRequest.setNumUpdates(1);

            this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
            this.mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location mLastLocation = locationResult.getLastLocation();
                    currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                }
            }, Looper.myLooper());
        } catch (SecurityException ex) {
            requestPermissions(activity);
        }
    }

    private boolean checkPermissions(Activity activity) {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, Constants.PERMISSION_ID);
    }

    private boolean isLocationEnabled(Activity activity) {
        LocationManager locationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void validateLocation(Activity activity) {
        if (checkPermissions(activity) && isLocationEnabled(activity)) {
            getLastLocation(activity);
        }
    }

    public static LocationHandlerSingleton getInstance() {
        return instance;
    }

}
