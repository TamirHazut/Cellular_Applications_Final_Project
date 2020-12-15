package com.example.lets_plan.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.lets_plan.R;

public class Fragment_Main extends Fragment_Base {
    private RadioButton main_RBT_login;
    private RadioButton main_RBT_signup;
    private ImageButton main_IBT_next;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initViews();
    }

    private void findViews(View view) {
        this.main_RBT_login = view.findViewById(R.id.main_RBT_login);
        this.main_RBT_signup = view.findViewById(R.id.main_RBT_signup);
        this.main_IBT_next = view.findViewById(R.id.main_IBT_next);
    }

    private void initViews() {
        this.main_RBT_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCurrentView();
            }
        });
        this.main_RBT_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCurrentView();
            }
        });
        this.main_IBT_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (main_RBT_login.isChecked()) {
                    Log.d("gaga", "Login completed");
                } else if (main_RBT_signup.isChecked()) {
                    Log.d("gaga", "Signup completed");
                }
            }
        });
        this.main_RBT_login.setChecked(true);
        changeCurrentView();
    }

    private void changeCurrentView() {
        changeButtonViews();
        switchChildFragments(createFragment());
    }

    private Fragment createFragment() {
        Fragment fragment = null;
        if (this.main_RBT_login.isChecked()) {
            fragment = new Fragment_Login();
        } else if (this.main_RBT_signup.isChecked()) {
            fragment = new Fragment_Signup();
        }
        return fragment;
    }

    private void changeButtonViews() {
        if (this.main_RBT_login.isChecked()) {
            this.main_RBT_login.setBackgroundResource(R.drawable.style_btn_tab_selected);
            this.main_RBT_login.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
            this.main_RBT_signup.setBackgroundResource(R.drawable.style_btn_tab_not_selected);
            this.main_RBT_signup.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));
        } else if (this.main_RBT_signup.isChecked()) {
            this.main_RBT_login.setBackgroundResource(R.drawable.style_btn_tab_not_selected);
            this.main_RBT_login.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));
            this.main_RBT_signup.setBackgroundResource(R.drawable.style_btn_tab_selected);
            this.main_RBT_signup.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
        }
    }

    private void switchChildFragments(Fragment fragment) {
        if (fragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_LAY_form, fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(getClass().getSimpleName())
                    .commit();
        }
    }

}
