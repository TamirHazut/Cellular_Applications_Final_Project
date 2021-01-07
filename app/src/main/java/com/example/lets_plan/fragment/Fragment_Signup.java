package com.example.lets_plan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lets_plan.R;
import com.example.lets_plan.data.User;
import com.example.lets_plan.logic.DataHandler;

public class Fragment_Signup extends Fragment_Base {
    private EditText signup_EDT_fullname;
    private EditText signup_EDT_email;
    private EditText signup_EDT_password;

    public Fragment_Signup() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initViews();
    }

    private void findViews(View view) {
        this.signup_EDT_fullname = view.findViewById(R.id.signup_EDT_fullname);
        this.signup_EDT_email = view.findViewById(R.id.signup_EDT_email);
        this.signup_EDT_password = view.findViewById(R.id.signup_EDT_password);
    }

    private void initViews() {

    }



    public User getUser() {
        return new User().setFullname(this.signup_EDT_fullname.getText().toString())
                .setEmail(this.signup_EDT_email.getText().toString())
                .setPassword(this.signup_EDT_password.getText().toString());
    }
}
