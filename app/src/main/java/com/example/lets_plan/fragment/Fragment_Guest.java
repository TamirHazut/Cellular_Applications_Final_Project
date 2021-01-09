package com.example.lets_plan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.lets_plan.R;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.data.Table;
import com.example.lets_plan.logic.recyclerview.handler.ItemsHandler;
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.DataHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fragment_Guest extends Fragment_Base {
    private EditText guest_EDT_full_name;
    private EditText guest_EDT_phone_number;
    private AutoCompleteTextView guest_DDM_number_of_guests;
    private AutoCompleteTextView guest_DDM_categories;
    private EditText guest_EDT_new_category;
    private Button guest_BTN_save;
    private Button guest_BTN_delete;
    private final ItemsHandler<Guest> itemsHandler;
    private Guest currentGuest;
    private Guest oldGuestData;
    private boolean newGuest;

    public Fragment_Guest(ItemsHandler<Guest> itemsHandler) {
        this.itemsHandler = itemsHandler;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_guest, container, false);
        this.newGuest = fromJson(getFromSharedPreferences(Constants.NEW_GUEST, "true"), Boolean.class);
        if (this.newGuest) {
            this.currentGuest = new Guest();
            this.currentGuest.setNumberOfGuests((long) Constants.MIN_NUMBER_OF_GUESTS_OPTIONS);
            this.oldGuestData = new Guest();
            this.oldGuestData.setNumberOfGuests((long) Constants.MIN_NUMBER_OF_GUESTS_OPTIONS);
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
        this.guest_EDT_full_name = view.findViewById(R.id.guest_EDT_full_name);
        this.guest_EDT_phone_number = view.findViewById(R.id.guest_EDT_phone_number);
        this.guest_DDM_number_of_guests = view.findViewById(R.id.guest_DDM_number_of_guests);
        this.guest_DDM_categories = view.findViewById(R.id.guest_DDM_categories);
        this.guest_EDT_new_category = view.findViewById(R.id.guest_EDT_new_category);
        this.guest_BTN_save = view.findViewById(R.id.guest_BTN_save);
        this.guest_BTN_delete = view.findViewById(R.id.guest_BTN_delete);
    }

    private void initViews() {
        updateDropdowns();
        if (!newGuest) {
            if (currentGuest.getFullname() != null && !currentGuest.getFullname().isEmpty()) {
                this.guest_EDT_full_name.setText(currentGuest.getFullname());
            }
            if (currentGuest.getPhoneNumber() != null && !currentGuest.getPhoneNumber().isEmpty()) {
                this.guest_EDT_phone_number.setText(currentGuest.getPhoneNumber());
                this.guest_EDT_phone_number.setEnabled(false);
                this.guest_EDT_phone_number.setInputType(EditorInfo.TYPE_NULL);
            }
            this.guest_DDM_number_of_guests.setText(String.valueOf(currentGuest.getNumberOfGuests()), false);
            if (currentGuest.getCategory() != null && !currentGuest.getCategory().isEmpty()) {
                this.guest_DDM_categories.setText(currentGuest.getCategory(), false);
            }
        }

        setButtonsClickListeners();
    }

    private void updateDropdowns() {
        // How many guests dropdown
        List<Long> numberList = new ArrayList<>();
        for (int i = Constants.MIN_NUMBER_OF_GUESTS_OPTIONS; i <= Constants.MAX_NUMBER_OF_GUESTS_OPTIONS; i++) {
            numberList.add((long) i);
        }
        this.guest_DDM_number_of_guests.setAdapter(
                new ArrayAdapter<>(
                        Objects.requireNonNull(getActivity()).getApplicationContext(),
                        R.layout.dropdown_menu_list_item,
                        numberList)
        );
        this.guest_DDM_number_of_guests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Long temp = (Long) parent.getItemAtPosition(position);
                if (currentGuest.getTable() != null && !currentGuest.getTable().isEmpty()) {
                    Table table = DataHandler.getInstance().findTableByCategoryAndName(currentGuest.getCategory(), currentGuest.getTable());
                    if (Table.sumGuests(table)-currentGuest.getNumberOfGuests()+temp > table.getMaxCapacity()) {
                        Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), Constants.TABLE_IS_FULL, Toast.LENGTH_SHORT).show();
                        guest_DDM_number_of_guests.setText(String.valueOf(currentGuest.getNumberOfGuests()));
                        return;
                    }
                }
                currentGuest.setNumberOfGuests(temp);
            }
        });
        // Filters dropdown
        List<String> categories = new ArrayList<>(DataHandler.getInstance().getAllCategoriesNames());
        categories.remove(Constants.ALL);
        this.guest_DDM_categories.setAdapter(new ArrayAdapter<>(
                getActivity().getApplicationContext(),
                R.layout.dropdown_menu_list_item,
                categories
        ));
        this.guest_DDM_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString();
                if (Constants.OTHER_CATEGORY.equals(category)) {
                    guest_EDT_new_category.setVisibility(View.VISIBLE);
                    currentGuest.setCategory(null);
                } else {
                    guest_EDT_new_category.setText("");
                    guest_EDT_new_category.setVisibility(View.INVISIBLE);
                    currentGuest.setCategory(category);
                }
            }
        });
    }

    private void setButtonsClickListeners() {
        this.guest_BTN_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = guest_EDT_full_name.getText().toString();
                String phonenumber = guest_EDT_phone_number.getText().toString();
                String new_category = guest_EDT_new_category.getText().toString();
                if (fullname.isEmpty()
                        || phonenumber.isEmpty()
                        || currentGuest.getNumberOfGuests() == null
                        || (currentGuest.getCategory() == null && (new_category.isEmpty() || new_category.equals(Constants.ALL) || new_category.equals(Constants.OTHER_CATEGORY)))) {
                    return;
                }
                if (currentGuest.getFullname() == null || DataHandler.getInstance().isDataModified(currentGuest.getFullname(), fullname)) {
                    currentGuest.setFullname(fullname);
                }
                if (currentGuest.getCategory() == null) {
                    currentGuest.setCategory(new_category);
                }
                if (newGuest) {
                    currentGuest.setPhoneNumber(phonenumber);
                    itemsHandler.addNewItem(currentGuest);
                    itemsHandler.saveItem(currentGuest);
                } else {
                    itemsHandler.updateItem(oldGuestData, currentGuest);
                }
                if (Objects.requireNonNull(getActivity()).getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            }
        });
        this.guest_BTN_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newGuest) {
                    itemsHandler.removeItem(oldGuestData);
                }
                if (Objects.requireNonNull(getActivity()).getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            }
        });
    }


}
