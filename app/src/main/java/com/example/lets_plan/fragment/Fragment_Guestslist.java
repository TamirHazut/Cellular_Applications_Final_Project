package com.example.lets_plan.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_plan.R;
import com.example.lets_plan.data.Filter;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.logic.Constants;
import com.example.lets_plan.logic.GuestslistHandler;
import com.example.lets_plan.logic.callback.DataReadyInterface;
import com.example.lets_plan.logic.callback.GuestClickedListener;
import com.example.lets_plan.logic.callback.ItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fragment_Guestslist extends Fragment_Base {
    private AutoCompleteTextView guestslist_DDM_filters;
    private RecyclerView guestslist_RCV_list;
    private GuestslistHandler guestslistHandler;
    private Button guestslist_BTN_sendinvites;
    private ImageButton guestslist_IBT_add;

    public Fragment_Guestslist() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_guestslist, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDropdown();
    }

    private void findViews(View view) {
        this.guestslist_DDM_filters = view.findViewById(R.id.guestslist_DDM_filters);
        this.guestslist_RCV_list = view.findViewById(R.id.guestslist_RCV_list);
        this.guestslist_BTN_sendinvites = view.findViewById(R.id.guestslist_BTN_sendinvites);
        this.guestslist_IBT_add = view.findViewById(R.id.guestslist_IBT_add);
    }

    private void initViews() {
        this.guestslistHandler = new GuestslistHandler(getContext().getApplicationContext(), getFromSharedPreferences(Constants.USER_INFO, ""), this.guestslist_RCV_list);
        this.guestslistHandler.setDataReadyInterface(new DataReadyInterface() {
            @Override
            public void dataReady() {
                updateDropdown();
            }
        });
        this.guestslistHandler.setGuestClickedListener(new GuestClickedListener() {
            @Override
            public void guestClicked(Guest guest) {
                switchParentFragment(R.id.main_FGMT_container, new Fragment_Guest(guestslistHandler, guest, false), false);
            }
        });
        this.guestslist_IBT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchParentFragment(R.id.main_FGMT_container, new Fragment_Guest(guestslistHandler), false);
            }
        });
        this.guestslist_BTN_sendinvites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void updateDropdown() {
        List<Filter> filters = new ArrayList<>();
        filters.add(this.guestslistHandler.getTotalFilter());
        filters.addAll(this.guestslistHandler.getFilterValues());
        this.guestslist_DDM_filters.setAdapter(
                new ArrayAdapter<Filter>(
                        getActivity().getApplicationContext(),
                        R.layout.dropdown_menu_list_item,
                        filters
                )
        );
        this.guestslist_DDM_filters.setText(filters.get(0).toString(), false);
        this.guestslist_DDM_filters.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String[] temp = s.toString().split(" ");
                String filter = new String("");
                if (temp.length > 2) {
                    int i;
                    for (i = 0; i < temp.length-2; i++) {
                        filter += temp[i] + " ";
                    }
                    filter += temp[i];
                } else {
                    filter = temp[0];
                }
                guestslistHandler.updateGuestsList(filter);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

}
