package com.example.lets_plan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_plan.R;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.data.Table;
import com.example.lets_plan.logic.recyclerview.handler.ExtendedGuestslistHandler;
import com.example.lets_plan.logic.recyclerview.handler.ItemsHandler;
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.callback.DataClickedListener;
import com.example.lets_plan.logic.callback.DataReadyInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fragment_Table extends Fragment_Base {
    private EditText table_EDT_name;
    private AutoCompleteTextView table_DDM_max_capacity;
    private AutoCompleteTextView table_DDM_categories;
    private RecyclerView table_RCV_guests_list;
    private Button table_BTN_save;
    private Button table_BTN_delete;
    private ItemsHandler<Guest> guestsItemsHandler;
    private ItemsHandler<Table> tableItemsHandler;
    private Table currentTable;
    private Table oldTableData;
    private boolean newTable;

    public Fragment_Table(ItemsHandler<Table> tableItemsHandler) {
        this.tableItemsHandler = tableItemsHandler;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        this.newTable = fromJson(getFromSharedPreferences(Constants.NEW_TABLE, "true"), Boolean.class);
        List<Guest> currentGuests = new ArrayList<>();
        if (this.newTable) {
            this.currentTable = new Table();
            this.currentTable.setMaxCapacity(Long.valueOf(Constants.MIN_TABLE_CAPACITY_OPTIONS));
            this.currentTable.setCategory(Constants.OTHER_CATEGORY);
            this.oldTableData = new Table();
            this.currentTable.setCategory(Constants.OTHER_CATEGORY);
        } else {
            this.oldTableData = fromJson(getFromSharedPreferences(Constants.CURRENT_TABLE, ""), Table.class);
            this.currentTable = new Table(oldTableData);
            currentTable.getGuests().forEach(phone -> {
                Guest guest = DataHandler.getInstance().findGuestByPhone(phone);
                if (guest != null) {
                    currentGuests.add(guest);
                }
            });
        }
        this.guestsItemsHandler = new ExtendedGuestslistHandler(true, currentGuests);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initViews();
        updateDropdowns();
        if (!newTable) {
            guestsItemsHandler.updateList(currentTable.getCategory());
        }
    }

    private void findViews(View view) {
        this.table_EDT_name = view.findViewById(R.id.table_EDT_name);
        this.table_DDM_max_capacity = view.findViewById(R.id.table_DDM_max_capacity);
        this.table_DDM_categories = view.findViewById(R.id.table_DDM_categories);
        this.table_RCV_guests_list = view.findViewById(R.id.table_RCV_guests_list);
        this.table_BTN_save = view.findViewById(R.id.table_BTN_save);
        this.table_BTN_delete = view.findViewById(R.id.table_BTN_delete);
    }

    private void initViews() {
        updateDropdowns();
        guestsItemsHandler.setRecyclerView(this.table_RCV_guests_list, Objects.requireNonNull(getActivity()).getApplicationContext(), Constants.ALL);
        guestsItemsHandler.setDataReadyInterface(new DataReadyInterface() {
            @Override
            public void dataReady() {
                updateDropdowns();
            }
        });
        if (currentTable.getName() != null && !currentTable.getName().isEmpty()) {
            this.table_EDT_name.setText(currentTable.getName());
        }
        if (currentTable.getMaxCapacity() != null) {
            this.table_DDM_max_capacity.setText(currentTable.getMaxCapacity().toString(), false);
        }
        if (currentTable.getCategory() != null && !currentTable.getCategory().isEmpty()) {
            this.table_DDM_categories.setText(currentTable.getCategory(), false);
        }
        setButtonsClickListeners();
    }

    private void setButtonsClickListeners() {
        guestsItemsHandler.setDataClickedListener(new DataClickedListener<Guest>() {
            @Override
            public void dataClicked(Guest guest) {
                saveToSharedPreferences(Constants.VALID_GUEST, true);
                if (currentTable.getGuests().contains(guest.getPhoneNumber())) {
                    currentTable.getGuests().remove(guest.getPhoneNumber());
                    guest.setTable(null);
                } else if (guest.getTable() != null && !guest.getTable().isEmpty()) {
                    Toast.makeText(getActivity(), Constants.GUEST_ALREADY_IN_A_TABLE, Toast.LENGTH_LONG).show();
                    saveToSharedPreferences(Constants.VALID_GUEST, false);
                } else if (Table.sumGuests(currentTable)+guest.getNumberOfGuests() > currentTable.getMaxCapacity()) {
                    Toast.makeText(getActivity(), Constants.TABLE_IS_FULL, Toast.LENGTH_LONG).show();
                    saveToSharedPreferences(Constants.VALID_GUEST, false);
                } else {
                    currentTable.getGuests().add(guest.getPhoneNumber());
                    guest.setTable(currentTable.getName());
                }
            }
        });
        this.table_BTN_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = table_EDT_name.getText().toString();
                if (name.isEmpty() || currentTable.getMaxCapacity() == null || currentTable.getCategory() == null) {
                    return;
                }
                if (currentTable.getName() == null || DataHandler.getInstance().isDataModified(currentTable.getName(), name)) {
                    currentTable.setName(name);
                    setTableToGuests();
                }
                if (newTable) {
                    tableItemsHandler.addNewItem(currentTable);
                    tableItemsHandler.saveItem(currentTable);
                } else {
                    tableItemsHandler.updateItem(oldTableData, currentTable);
                }
                if (Objects.requireNonNull(getActivity()).getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            }
        });
        this.table_BTN_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!newTable) {
                    tableItemsHandler.removeItem(oldTableData);
                }
                if (Objects.requireNonNull(getActivity()).getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            }
        });
    }

    private void setTableToGuests() {
        currentTable.getGuests().forEach(phone -> {
                    Guest guest = DataHandler.getInstance().findGuestByPhone(phone);
                    if (guest != null) {
                        guest.setTable(currentTable.getName());
                    }
                });
    }

    private void updateDropdowns() {
        // Max capacity dropdown
        List<Long> numberList = new ArrayList<>();
        for (long i = Constants.MIN_TABLE_CAPACITY_OPTIONS; i <= Constants.MAX_TABLE_CAPACITY_OPTIONS; i++) {
            numberList.add(i);
        }
        this.table_DDM_max_capacity.setAdapter(
                new ArrayAdapter<>(
                        Objects.requireNonNull(getActivity()).getApplicationContext(),
                        R.layout.dropdown_menu_list_item,
                        numberList)
        );

        this.table_DDM_max_capacity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentTable.setMaxCapacity((Long) parent.getItemAtPosition(position));
            }
        });

        // Filters dropdown
        List<String> categories = new ArrayList<>(DataHandler.getInstance().getAllCategoriesNames());
        categories.remove(Constants.ALL);
        this.table_DDM_categories.setAdapter(new ArrayAdapter<>(
                getActivity().getApplicationContext(),
                R.layout.dropdown_menu_list_item,
                categories
        ));

        this.table_DDM_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentTable.setCategory(parent.getItemAtPosition(position).toString());
                updateGuestslistRecyclerView();
            }
        });
    }

    private void updateGuestslistRecyclerView() {
        if (!currentTable.getCategory().equals(Constants.OTHER_CATEGORY)) {
            List<String> temp = new ArrayList<>();
            currentTable.getGuests().forEach(phone -> {
                Guest guest = DataHandler.getInstance().findGuestByPhone(phone);
                if (guest != null) {
                    if (guest.getCategory().equals(currentTable.getCategory()) || currentTable.getCategory().equals(Constants.OTHER_CATEGORY)) {
                        temp.add(phone);
                    } else {
                        guest.setTable(null);
                    }
                }
            });
            currentTable.setGuests(temp);
        }
        guestsItemsHandler.updateList(currentTable.getCategory());
    }
}
