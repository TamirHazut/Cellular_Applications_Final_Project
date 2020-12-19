package com.example.lets_plan;

import android.app.Application;

import com.example.lets_plan.activity.Activity_Base;
import com.example.lets_plan.logic.LocationHandlerSingleton;
import com.example.lets_plan.logic.SharedPreferencesSingleton;
import com.example.lets_plan.logic.UserRepositorySingleton;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesSingleton.init(this);
        LocationHandlerSingleton.init(this);
    }

}