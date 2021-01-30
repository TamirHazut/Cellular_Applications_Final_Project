package com.example.lets_plan.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.lets_plan.logic.PermissionHandlerSingleton;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.LOCATION_PERMISSION_ID:
                PermissionHandlerSingleton.getInstance().checkLocationPermission();
                break;
            case Constants.SMS_PERMISSION_ID:
                PermissionHandlerSingleton.getInstance().checkSmsPermission();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.LOCATION_PERMISSION_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PermissionHandlerSingleton.getInstance().checkLocationPermission();
                } else {
                    Toast.makeText(this, "Permission denied to use location services", Toast.LENGTH_SHORT).show();
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        PermissionHandlerSingleton.getInstance().openLocationPermissionInfoDialog();
                    } else {
                        PermissionHandlerSingleton.getInstance().openAppSettingsToManuallyPermission(Constants.LOCATION_PERMISSION_ID);
                    }
                }
                break;
            }
            case Constants.SMS_PERMISSION_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PermissionHandlerSingleton.getInstance().checkSmsPermission();
                } else {
                    Toast.makeText(this, "Permission denied to use sms services", Toast.LENGTH_SHORT).show();
                    PermissionHandlerSingleton.getInstance().openAppSettingsToManuallyPermission(Constants.SMS_PERMISSION_ID);
                }
                break;
            }
        }
    }
}
