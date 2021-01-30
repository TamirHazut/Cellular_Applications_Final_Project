package com.example.lets_plan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.lets_plan.R;

public class Fragment_Actions extends Fragment_Container {
    private RadioButton actions_RBT_guests_list;
    private RadioButton actions_RBT_tables_arrangement;
    private RadioButton actions_RBT_event_hall;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_actions, container, false);
        findViews(view);
        initViews();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        changeCurrentView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void findViews(View view) {
        this.actions_RBT_guests_list = view.findViewById(R.id.actions_RBT_guests_list);
        this.actions_RBT_tables_arrangement = view.findViewById(R.id.actions_RBT_tables_arrangement);
        this.actions_RBT_event_hall = view.findViewById(R.id.actions_RBT_event_hall);
        setContainerViewId(R.id.actions_LAY_view);
    }

    private void initViews() {
        setHasChildFragments(true);
        this.actions_RBT_guests_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { changeCurrentView(); }
        });
        this.actions_RBT_tables_arrangement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCurrentView();
            }
        });
        this.actions_RBT_event_hall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCurrentView();
            }
        });
        this.actions_RBT_guests_list.setChecked(true);
    }

    @Override
    protected void changeButtonViews() {
        if (this.actions_RBT_guests_list.isChecked()) {
            this.actions_RBT_guests_list.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
            this.actions_RBT_tables_arrangement.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));
            this.actions_RBT_event_hall.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));
        } else if (this.actions_RBT_tables_arrangement.isChecked()) {
            this.actions_RBT_guests_list.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));
            this.actions_RBT_tables_arrangement.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
            this.actions_RBT_event_hall.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));
        } else if (this.actions_RBT_event_hall.isChecked()) {
            this.actions_RBT_guests_list.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));
            this.actions_RBT_tables_arrangement.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));
            this.actions_RBT_event_hall.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
        }
    }

    @Override
    protected Fragment_Base createFragment() {
        Fragment_Base fragment = null;
        if (this.actions_RBT_guests_list.isChecked()) {
            fragment = new Fragment_Guests_List();
        } else if (this.actions_RBT_tables_arrangement.isChecked()) {
            fragment = new Fragment_Tables_Arrangement();
        } else if (this.actions_RBT_event_hall.isChecked()) {
            fragment = new Fragment_Map();
        }
        return fragment;
    }
}
