package com.example.lets_plan.activity;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lets_plan.logic.utils.Constants;


public class Activity_Base extends AppCompatActivity {
    protected boolean isDoubleBackPressToClose = true;
    private long mBackPressed;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            backToMain();
        } else {
            playerExitValidate();
        }
    }

    private void playerExitValidate() {
        if (this.isDoubleBackPressToClose) {
            if (this.mBackPressed + Constants.BACK_PRESSED_TIME_INTERVAL > System.currentTimeMillis()) {
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

    public void backToMain() {
//        FragmentManager fm = getSupportFragmentManager();
//        fm.popBackStackImmediate(Fragment_Main.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
