package com.example.lets_plan.logic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lets_plan.R;
import com.example.lets_plan.logic.callback.DataReadyInterface;
import com.example.lets_plan.logic.utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class PermissionHandlerSingleton {
    private AppCompatActivity activity;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng currentLocation;
    private static PermissionHandlerSingleton instance;
    private DataReadyInterface dataReadyInterface;

    private PermissionHandlerSingleton(AppCompatActivity activity) {
        this.activity = activity;
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity);
    }

    public static void init(AppCompatActivity activity) {
        if (instance == null) {
            instance = new PermissionHandlerSingleton(activity);
        }
    }

    public static PermissionHandlerSingleton getInstance() {
        return instance;
    }

    public void checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, Constants.SMS_PERMISSION_ID);
            } else {
                openAppSettingsToManuallyPermission(Constants.SMS_PERMISSION_ID);
            }
        }
    }

    public void checkLocationPermission() {
        if (!isLocationEnabled()) {
            openSettingsToManuallyPermission();
        } else {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_ID);
                } else {
                    openAppSettingsToManuallyPermission(Constants.LOCATION_PERMISSION_ID);
                }
            } else {
                this.mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData(activity);
                        } else {
                            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            if (dataReadyInterface != null) {
                                dataReadyInterface.dataReady();
                            }
                        }
                    }
                });
            }
        }
    }

    public void openLocationPermissionInfoDialog() {
        String message = this.activity.getString(R.string.permission_rationale);
        AlertDialog alertDialog = new AlertDialog.Builder(this.activity, 0)
                .setMessage(message)
                .setPositiveButton(this.activity.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkLocationPermission();
                            }
                        })
                .show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    public void openSettingsToManuallyPermission() {
        String message = this.activity.getString(R.string.message_location_disabled);
        AlertDialog alertDialog = new AlertDialog.Builder(this.activity, 0)
                .setMessage(message)
                .setPositiveButton(this.activity.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                activity.startActivityForResult(intent, Constants.LOCATION_PERMISSION_ID);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(this.activity.getString(R.string.cancel), null)
                .show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    public void openAppSettingsToManuallyPermission(int requestCode) {
        String message = this.activity.getString(R.string.message_permission_disabled);
        AlertDialog alertDialog = new AlertDialog.Builder(this.activity, 0)
                .setMessage(message)
                .setPositiveButton(this.activity.getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                intent.setData(uri);
                                activity.startActivityForResult(intent, requestCode);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(this.activity.getString(R.string.cancel), null)
                .show();
        alertDialog.setCanceledOnTouchOutside(true);
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
                    if (dataReadyInterface != null) {
                        dataReadyInterface.dataReady();
                    }
                }
            }, Looper.myLooper());
        } catch (SecurityException ex) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, Constants.LOCATION_PERMISSION_ID);
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager)this.activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void requestPermissions(String[] permissions, int requestCode) {
        this.activity.requestPermissions(permissions, requestCode);
    }

    public void setDataReadyInterface(DataReadyInterface dataReadyInterface) {
        this.dataReadyInterface = dataReadyInterface;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }
}
