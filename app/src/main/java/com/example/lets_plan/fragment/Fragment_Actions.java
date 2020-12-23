package com.example.lets_plan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.lets_plan.R;

public class Fragment_Actions extends Fragment_Container {
    private RadioButton actions_RBT_guestslist;
    private RadioButton actions_RBT_tablesarrangement;
    private RadioButton actions_RBT_eventhall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_actions, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initViews();
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
        this.actions_RBT_guestslist = view.findViewById(R.id.actions_RBT_guestslist);
        this.actions_RBT_tablesarrangement = view.findViewById(R.id.actions_RBT_tablesarrangement);
        this.actions_RBT_eventhall = view.findViewById(R.id.actions_RBT_eventhall);
        setContainerViewId(R.id.actions_LAY_view);
    }

    private void initViews() {
        setHasChildFragments(true);
        this.actions_RBT_guestslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCurrentView();
            }
        });
        this.actions_RBT_guestslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCurrentView();
            }
        });
        this.actions_RBT_guestslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCurrentView();
            }
        });
        this.actions_RBT_guestslist.setChecked(true);
        changeCurrentView();
    }

    @Override
    protected void changeButtonViews() {
        if (this.actions_RBT_guestslist.isChecked()) {
            this.actions_RBT_guestslist.setBackgroundResource(R.drawable.style_btn_tab_selected);
            this.actions_RBT_guestslist.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
            this.actions_RBT_tablesarrangement.setBackgroundResource(R.drawable.style_btn_tab_not_selected);
            this.actions_RBT_tablesarrangement.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));
            this.actions_RBT_eventhall.setBackgroundResource(R.drawable.style_btn_tab_not_selected);
            this.actions_RBT_eventhall.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));
        } else if (this.actions_RBT_tablesarrangement.isChecked()) {
            this.actions_RBT_tablesarrangement.setBackgroundResource(R.drawable.style_btn_tab_not_selected);
            this.actions_RBT_tablesarrangement.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));
            this.actions_RBT_guestslist.setBackgroundResource(R.drawable.style_btn_tab_selected);
            this.actions_RBT_guestslist.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
            this.actions_RBT_eventhall.setBackgroundResource(R.drawable.style_btn_tab_not_selected);
            this.actions_RBT_eventhall.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));
        } else if (this.actions_RBT_eventhall.isChecked()) {
            this.actions_RBT_eventhall.setBackgroundResource(R.drawable.style_btn_tab_not_selected);
            this.actions_RBT_eventhall.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));
            this.actions_RBT_guestslist.setBackgroundResource(R.drawable.style_btn_tab_selected);
            this.actions_RBT_guestslist.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
            this.actions_RBT_tablesarrangement.setBackgroundResource(R.drawable.style_btn_tab_not_selected);
            this.actions_RBT_tablesarrangement.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.black));
        }
    }

    @Override
    protected Fragment_Base createFragment() {
        Fragment_Base fragment = null;
        if (this.actions_RBT_guestslist.isChecked()) {
            fragment = new Fragment_Guestslist();
        } else if (this.actions_RBT_tablesarrangement.isChecked()) {
            fragment = new Fragment_TablesArrangement();
        } else if (this.actions_RBT_eventhall.isChecked()) {
            fragment = new Fragment_EventHall();
        }
        return fragment;
    }
}
