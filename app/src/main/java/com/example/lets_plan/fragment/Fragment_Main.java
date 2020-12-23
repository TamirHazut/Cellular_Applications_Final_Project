package com.example.lets_plan.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.lets_plan.R;
import com.example.lets_plan.logic.UserRepositorySingleton;
import com.example.lets_plan.logic.callback.CallbackInterface;

public class Fragment_Main extends Fragment_Container {
    private RadioButton main_RBT_login;
    private RadioButton main_RBT_signup;
    private ImageButton main_IBT_next;

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
        setContainerViewId(R.id.main_LAY_form);

    }

    private void initViews() {
        setHasChildFragments(true);
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
                    Fragment_Login fragmentLogin = (Fragment_Login)getChildFragmentManager().findFragmentById(R.id.main_LAY_form);
                    UserRepositorySingleton.getInstance().signIn(fragmentLogin.getUser());
                } else if (main_RBT_signup.isChecked()) {
                    Fragment_Signup fragmentSignup = (Fragment_Signup) getChildFragmentManager().findFragmentById(R.id.main_LAY_form);
                    UserRepositorySingleton.getInstance().signUp(fragmentSignup.getUser());
                }
            }
        });
        this.main_RBT_login.setChecked(true);
        changeCurrentView();
    }

//    @Override
//    protected void changeCurrentView() {
//        changeButtonViews();
//        switchChildFragments(R.id.main_LAY_form, createFragment());
//    }

    @Override
    protected Fragment_Base createFragment() {
        Fragment_Base fragment = null;
        if (this.main_RBT_login.isChecked()) {
            fragment = new Fragment_Login();
            UserRepositorySingleton.getInstance().setCallbackInterface(new CallbackInterface() {
                @Override
                public void onCall() {
                    switchParentFragment(R.id.main_FGMT_container, new Fragment_Actions(), false);
                }
            });
        } else if (this.main_RBT_signup.isChecked()) {
            fragment = new Fragment_Signup();
            UserRepositorySingleton.getInstance().setCallbackInterface(new CallbackInterface() {
                @Override
                public void onCall() {
                    main_RBT_login.setChecked(true);
                    changeCurrentView();
                }
            });
        }
        return fragment;
    }

//    private void switchParentFragment() {
//        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.main_FGMT_container, new Fragment_Actions(), Fragment_Actions.class.getSimpleName()).addToBackStack(null).commit();
//    }

    @Override
    protected void changeButtonViews() {
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

//    private void switchChildFragments(Fragment fragment) {
//        if (fragment != null) {
//            getChildFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.main_LAY_form, fragment, fragment.getClass().getSimpleName())
//                    .addToBackStack(getClass().getSimpleName())
//                    .commit();
//        }
//    }

}
