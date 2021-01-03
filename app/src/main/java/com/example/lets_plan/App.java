package com.example.lets_plan;

import android.app.Application;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.lets_plan.activity.Activity_Base;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.LocationHandlerSingleton;
import com.example.lets_plan.logic.SharedPreferencesSingleton;
import com.example.lets_plan.logic.UserRepositorySingleton;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        SharedPreferencesSingleton.init(this);
        LocationHandlerSingleton.init(this);
    }

}