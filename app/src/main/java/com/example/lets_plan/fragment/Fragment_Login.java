package com.example.lets_plan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lets_plan.R;
import com.example.lets_plan.logic.data.User;

public class Fragment_Login extends Fragment_Base {
    private EditText signup_EDT_email;
    private EditText login_EDT_password;

    public Fragment_Login() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initViews();
    }

    private void findViews(View view) {
        this.signup_EDT_email = view.findViewById(R.id.login_EDT_email);
        this.login_EDT_password = view.findViewById(R.id.login_EDT_password);
    }

    private void initViews() {

    }

    public User getUser() {
        return new User().setEmail(this.signup_EDT_email.getText().toString())
                .setPassword(this.login_EDT_password.getText().toString());
    }
}
