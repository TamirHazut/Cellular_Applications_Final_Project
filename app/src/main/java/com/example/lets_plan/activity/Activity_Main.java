package com.example.lets_plan.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.lets_plan.R;
import com.example.lets_plan.fragment.Fragment_Actions;
import com.example.lets_plan.fragment.Fragment_Container;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inits
        DataHandler.init(this);
        UserRepositorySingleton.init(this);
        SharedPreferencesSingleton.getInstance().getPrefs().edit().putString(Constants.CURRENT_GUEST_CATEGORY, Constants.ALL).putString(Constants.CURRENT_TABLE_CATEGORY, Constants.ALL).apply();
        Gson gson = new Gson();

        // Loader
        RotateLoading main_loader = findViewById(R.id.main_loader);
        DataHandler.getInstance().setRotateLoading(main_loader);

        // Location
        String jsonFromMemory = SharedPreferencesSingleton.getInstance().getPrefs().getString(Constants.LOCATION, "");
        LatLng userLocation = gson.fromJson(jsonFromMemory, LatLng.class);
        if (userLocation == null) {
            LocationHandlerSingleton.getInstance().getLastLocation(this);
        }
        if (savedInstanceState == null) {
            if (findViewById(R.id.main_FGMT_container) != null) {
                String uid = SharedPreferencesSingleton.getInstance().getPrefs().getString(Constants.USER_INFO, null);
                Fragment_Container fragment_container;
                if (uid == null) {
                    fragment_container = new Fragment_Main();
                } else {
                    DataHandler.getInstance().setOwnerID(uid);
                    fragment_container = new Fragment_Actions();
                }
                getSupportFragmentManager().beginTransaction().add(R.id.main_FGMT_container, fragment_container).commit();
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