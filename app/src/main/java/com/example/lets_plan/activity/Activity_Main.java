package com.example.lets_plan.activity;

import android.os.Bundle;

import com.example.lets_plan.R;
import com.example.lets_plan.fragment.Fragment_Actions;
import com.example.lets_plan.fragment.Fragment_Container;
import com.example.lets_plan.fragment.Fragment_Main;
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.PermissionHandlerSingleton;
import com.example.lets_plan.logic.SharedPreferencesSingleton;
import com.example.lets_plan.logic.UserRepositorySingleton;
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

        // Loader
        RotateLoading main_loader = findViewById(R.id.main_loader);
        DataHandler.getInstance().setRotateLoading(main_loader);

        // Location
        PermissionHandlerSingleton.init(this);

        // Start Application
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

}