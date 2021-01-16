package com.example.lets_plan.activity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import com.example.lets_plan.R;
import com.example.lets_plan.logic.SharedPreferencesSingleton;
import com.example.lets_plan.logic.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_Splash extends Activity_Base {
    private ImageView splash_IMG_logo;
    private String uid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        findViews();
        initViews();
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null) {
            uid = fbUser.getUid();
        }
        startAnimation(splash_IMG_logo);
    }

    private void findViews() {
        this.splash_IMG_logo = findViewById(R.id.splash_IMG_logo);
    }

    private void initViews() {
    }

    private void startAnimation(View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        view.setY(-height / 2f);
        view.animate()
                .translationY(0)
                .setDuration(Constants.ANIMATION_DURATION_IN_MS)
                .setInterpolator(new BounceInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        SharedPreferencesSingleton.getInstance().getPrefs().edit().putString(Constants.USER_INFO, uid).apply();
                        if (uid != null)
                            Log.d("UserID", uid);
                        Intent intent = new Intent(Activity_Splash.this, Activity_Main.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }
}