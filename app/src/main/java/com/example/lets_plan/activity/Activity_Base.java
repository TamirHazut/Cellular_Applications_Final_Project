package com.example.lets_plan.activity;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lets_plan.logic.SharedPreferencesSingleton;
import com.example.lets_plan.logic.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;


public class Activity_Base extends AppCompatActivity {
    protected boolean isDoubleBackPressToClose = true;
    private long mBackPressed;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            playerExitValidate();
        }
    }

    private void playerExitValidate() {
        if (this.isDoubleBackPressToClose) {
            if (this.mBackPressed + Constants.BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
                if (SharedPreferencesSingleton.getInstance().getPrefs().getString(Constants.USER_INFO, null) != null) {
                    FirebaseAuth.getInstance().signOut();
                }
                super.onBackPressed();
                return;
            }
            else {
                Toast.makeText(this, "Tap back button again to exit", Toast.LENGTH_SHORT).show();
            }
            this.mBackPressed = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
    }
}
