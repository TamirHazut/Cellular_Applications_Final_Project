package com.example.lets_plan.logic;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesSingleton {
    private static SharedPreferencesSingleton instance;
    private SharedPreferences prefs;

    private SharedPreferencesSingleton(Context context) {
        this.prefs = context.getSharedPreferences(Constants.SP_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesSingleton getInstance() {
        return instance;
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesSingleton(context.getApplicationContext());
        }
    }

    public SharedPreferences getPrefs() {
        return this.prefs;
    }
}
