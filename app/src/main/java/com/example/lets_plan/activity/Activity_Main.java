package com.example.lets_plan.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.lets_plan.R;
import com.example.lets_plan.fragment.Fragment_Main;
import com.example.lets_plan.logic.Constants;
import com.example.lets_plan.logic.LocationHandlerSingleton;
import com.example.lets_plan.logic.SharedPreferencesSingleton;
import com.example.lets_plan.logic.UserRepositorySingleton;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class Activity_Main extends Activity_Base {
    private LatLng userLocation;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserRepositorySingleton.init(this);
        setContentView(R.layout.activity_main);
        gson = new Gson();
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