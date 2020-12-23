package com.example.lets_plan.logic;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.lets_plan.logic.callback.CallbackInterface;
import com.example.lets_plan.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;


public class UserRepositorySingleton {
    private static UserRepositorySingleton instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth dbAuth;
    private final Gson gson;
    private Activity activity;
    private CallbackInterface callbackInterface;
    private FirebaseUser currentUser;

    private UserRepositorySingleton(Activity activity) {
        this.db = FirebaseFirestore.getInstance();
        this.dbAuth = FirebaseAuth.getInstance();
        this.gson = new Gson();
        this.activity = activity;
    }

    public void setCallbackInterface(CallbackInterface callbackInterface) {
        this.callbackInterface = callbackInterface;
    }

    public void signUp(User newUser) {
        if (newUser == null) {
            Toast.makeText(activity, Constants.REGISTRATION_FAILED, Toast.LENGTH_LONG).show();
            return;
        }
        dbAuth.createUserWithEmailAndPassword(newUser.getEmail(), newUser.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentUser = dbAuth.getCurrentUser();
                        currentUser.updateProfile(new UserProfileChangeRequest.Builder()
                                .setDisplayName(newUser.getFullname())
                                .build());
                        Toast.makeText(activity, Constants.REGISTERED_SUCCESSFULLY, Toast.LENGTH_LONG).show();
                        callbackInterface.onCall();
                    } else {
                        Toast.makeText(activity, Constants.REGISTRATION_FAILED, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void signIn(User user) {
        dbAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(
                (Activity) activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = dbAuth.getCurrentUser();
                            SharedPreferencesSingleton.getInstance().getPrefs().edit().putString(Constants.USER_INFO, currentUser.getEmail()).apply();
                            callbackInterface.onCall();
                            Toast.makeText(activity, Constants.LOGIN_SUCCESSFUL, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(activity, Constants.LOGIN_FAILED, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    public static void init(Activity activity) {
        if (instance == null) {
            instance = new UserRepositorySingleton(activity);
        }
    }

    public static UserRepositorySingleton getInstance() {
        return instance;
    }
}
