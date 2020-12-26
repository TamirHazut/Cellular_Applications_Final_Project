package com.example.lets_plan.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lets_plan.R;
import com.example.lets_plan.data.Filter;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.logic.Constants;
import com.example.lets_plan.logic.GuestslistHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Fragment_Guest extends Fragment_Base {
    private EditText guest_EDT_fullname;
    private EditText guest_EDT_phonenumber;
    private AutoCompleteTextView guest_DDM_numberofguests;
    private AutoCompleteTextView guest_DDM_categories;
    private EditText guest_EDT_new_category;
    private Button guest_BTN_save;
    private Button guest_BTN_cancel;
    private Guest currentGuest;
    private Guest oldGuestData;
    private GuestslistHandler guestslistHandler;
    private boolean newGuest;

    public Fragment_Guest(GuestslistHandler guestslistHandler) {
        this.guestslistHandler = guestslistHandler;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_guest, container, false);
        this.newGuest = fromJson(getFromSharedPreferences(Constants.NEW_GUEST, "true"), Boolean.class);
        if (this.newGuest) {
            this.currentGuest = new Guest();
            this.currentGuest = new Guest();
        } else {
            this.currentGuest = fromJson(getFromSharedPreferences(Constants.CURRENT_GUEST, ""), Guest.class);
            this.oldGuestData = new Guest(currentGuest);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initViews();
    }

    private void findViews(View view) {
        this.guest_EDT_fullname = view.findViewById(R.id.guest_EDT_fullname);
        this.guest_EDT_phonenumber = view.findViewById(R.id.guest_EDT_phonenumber);
        this.guest_DDM_numberofguests = view.findViewById(R.id.guest_DDM_numberofguests);
        this.guest_DDM_categories = view.findViewById(R.id.guest_DDM_categories);
        this.guest_EDT_new_category = view.findViewById(R.id.guest_EDT_new_category);
        this.guest_BTN_save = view.findViewById(R.id.guest_BTN_save);
        this.guest_BTN_cancel = view.findViewById(R.id.guest_BTN_cancel);
    }

    private void initViews() {
        updateDropdowns();
        if (currentGuest.getFullname() != null && !currentGuest.getFullname().isEmpty()) {
            this.guest_EDT_fullname.setText(currentGuest.getFullname());
        }
        if (currentGuest.getPhoneNumber() != null && !currentGuest.getPhoneNumber().isEmpty()) {
            this.guest_EDT_phonenumber.setText(currentGuest.getPhoneNumber());
            this.guest_EDT_phonenumber.setEnabled(false);
            this.guest_EDT_phonenumber.setInputType(EditorInfo.TYPE_NULL);
        }
        if (currentGuest.getNumberOfGuests() != null) {
            this.guest_DDM_numberofguests.setText(currentGuest.getNumberOfGuests().toString(), false);
        }
        if (currentGuest.getCategory() != null && !currentGuest.getCategory().isEmpty()) {
            this.guest_DDM_categories.setText(currentGuest.getCategory(), false);
        }
        setButtonsClickListeners();
    }

    private void updateDropdowns() {
        // How many guests dropdown
        List<Long> numberList = new ArrayList<>();
        for (int i = 1; i <= Constants.MAX_NUMBER_OF_GUESTS_OPTIONS; i++) {
            numberList.add(new Long(i));
        }
        this.guest_DDM_numberofguests.setAdapter(
                new ArrayAdapter<Long>(
                        getActivity().getApplicationContext(),
                        R.layout.dropdown_menu_list_item,
                        numberList)
        );
        this.guest_DDM_numberofguests.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    currentGuest.setNumberOfGuests(Long.parseLong(s.toString()));
                } catch (NumberFormatException ex) {
                    currentGuest.setNumberOfGuests(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // Filters dropdown
        List<String> filters = guestslistHandler.getFilterValues().stream().map(Filter::getName).collect(Collectors.toList());
        filters.add(Constants.OTHER_CATEGORY);
        this.guest_DDM_categories.setAdapter(new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                R.layout.dropdown_menu_list_item,
                filters
        ));
        this.guest_DDM_categories.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!newGuest) {
                    oldGuestData.setCategory(s.toString());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String filter = s.toString();
                switch (filter) {
                    case Constants.OTHER_CATEGORY:
                        guest_EDT_new_category.setVisibility(View.VISIBLE);
                        currentGuest.setCategory(null);
                        break;
                    default:
                        guest_EDT_new_category.setText("");
                        guest_EDT_new_category.setVisibility(View.INVISIBLE);
                        currentGuest.setCategory(filter);
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setButtonsClickListeners() {
        this.guest_BTN_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = guest_EDT_fullname.getText().toString();
                String phonenumber = guest_EDT_phonenumber.getText().toString();
                String new_category = guest_EDT_new_category.getText().toString();
                if (fullname.isEmpty()
                        || phonenumber.isEmpty()
                        || currentGuest.getNumberOfGuests() == null
                        || (currentGuest.getCategory() == null && new_category.isEmpty())) {
                    return;
                }
                if (currentGuest.getFullname() == null || isDataModified(currentGuest.getFullname(), fullname)) {
                    currentGuest.setFullname(fullname);
                }
                if (isDataModified(oldGuestData.getCategory(), currentGuest.getCategory())) {
                    if (currentGuest.getCategory() == null) {
                        currentGuest.setCategory(new_category);
                    }
                }
                if (newGuest) {
                    currentGuest.setPhoneNumber(phonenumber);
                    guestslistHandler.addNewGuest(currentGuest);
                } else {
                    guestslistHandler.updateGuest(oldGuestData, currentGuest);
                }
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            }
        });
        this.guest_BTN_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            }
        });
    }

    private <T> boolean isDataModified(T o1, T o2) {
        return !o1.equals(o2);
    }
}
