package com.example.lets_plan.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.lets_plan.R;
import com.example.lets_plan.fragment.Fragment_Main;
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.LocationHandlerSingleton;
import com.example.lets_plan.logic.SharedPreferencesSingleton;
import com.example.lets_plan.logic.UserRepositorySingleton;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.victor.loading.rotate.RotateLoading;

public class Activity_Main extends Activity_Base {
    private RotateLoading main_loader;
    private LatLng userLocation;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inits
        DataHandler.init(this);
        UserRepositorySingleton.init(this);
        SharedPreferencesSingleton.getInstance().getPrefs().edit().putString(Constants.CURRENT_GUEST_CATEGORY, Constants.ALL).putString(Constants.CURRENT_TABLE_CATEGORY, Constants.ALL).apply();
        gson = new Gson();

        // Loader
        this.main_loader = findViewById(R.id.main_loader);
        DataHandler.getInstance().setRotateLoading(this.main_loader);

        // Location
        String jsonFromMemory = SharedPreferencesSingleton.getInstance().getPrefs().getString(Constants.LOCATION, "");
        this.userLocation = gson.fromJson(jsonFromMemory, LatLng.class);
        if (this.userLocation == null) {
            LocationHandlerSingleton.getInstance().getLastLocation(this);
        }
        if (savedInstanceState == null) {
            if (findViewById(R.id.main_FGMT_container) != null) {
                Fragment_Main fragment_main = new Fragment_Main();
                getSupportFragmentManager().beginTransaction().add(R.id.main_FGMT_container, fragment_main).commit();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationHandlerSingleton.getInstance().getLastLocation(this);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocationHandlerSingleton.getInstance().validateLocation(this);
    }

}