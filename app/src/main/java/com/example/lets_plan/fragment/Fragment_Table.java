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
import com.example.lets_plan.data.Category;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.data.Table;
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.recyclerview.adapter.GuestslistRecyclerViewAdapter;
import com.example.lets_plan.logic.callback.DataClickedListener;
import com.example.lets_plan.logic.callback.DataReadyInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Fragment_Table extends Fragment_Base {
    private EditText table_EDT_name;
    private AutoCompleteTextView table_DDM_max_capacity;
    private AutoCompleteTextView table_DDM_categories;
    private RecyclerView table_RCV_guests_list;
    private Button table_BTN_save;
    private Button table_BTN_delete;
    private Table currentTable;
    private Table oldTableData;
    private boolean newTable;

    public Fragment_Table() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_table, container, false);
        this.newTable = fromJson(getFromSharedPreferences(Constants.NEW_TABLE, "true"), Boolean.class);
        if (this.newTable) {
            this.currentTable = new Table();
            this.currentTable.setMaxCapacity(new Long(Constants.MIN_TABLE_CAPACITY_OPTIONS));
            this.currentTable.setCategory(Constants.ALL);
            this.oldTableData = new Table();
            this.currentTable.setCategory(Constants.ALL);
        } else {
            this.oldTableData = fromJson(getFromSharedPreferences(Constants.CURRENT_TABLE, ""), Table.class);
            this.currentTable = new Table(oldTableData);
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
        this.table_EDT_name = view.findViewById(R.id.table_EDT_name);
        this.table_DDM_max_capacity = view.findViewById(R.id.table_DDM_max_capacity);
        this.table_DDM_categories = view.findViewById(R.id.table_DDM_categories);
        this.table_RCV_guests_list = view.findViewById(R.id.table_RCV_guests_list);
        this.table_BTN_save = view.findViewById(R.id.table_BTN_save);
        this.table_BTN_delete = view.findViewById(R.id.table_BTN_delete);
    }

    private void initViews() {
        updateDropdowns();
        DataHandler.getInstance().initGuestslistHandler();
        DataHandler.getInstance().getGuestslistHandler().setChangeItemBackgroundOnClick(true);
        DataHandler.getInstance().getGuestslistHandler().setGuestslistRecyclerView(this.table_RCV_guests_list, getActivity().getApplicationContext(), currentTable.getCategory(), currentTable.getGuests());
        DataHandler.getInstance().getGuestslistHandler().setDataReadyInterface(new DataReadyInterface() {
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
        DataHandler.getInstance().getGuestslistHandler().setDataClickedListener(new DataClickedListener<Guest>() {
            @Override
            public void dataClicked(Guest guest) {
                saveToSharedPreferences(Constants.VALID_GUEST, true);
                if (currentTable.getGuests().contains(guest)) {
                    currentTable.getGuests().remove(guest);
                    guest.setTable(null);
                } else if (guest.getTable() != null && !guest.getTable().isEmpty()) {
                    Toast.makeText(getActivity(), Constants.GUEST_ALREADY_IN_A_TABLE, Toast.LENGTH_LONG).show();
                    saveToSharedPreferences(Constants.VALID_GUEST, false);
                } else if (currentTable.sum()+guest.getNumberOfGuests().longValue() > currentTable.getMaxCapacity().longValue()) {
                    Toast.makeText(getActivity(), Constants.TABLE_IS_FULL, Toast.LENGTH_LONG).show();
                    saveToSharedPreferences(Constants.VALID_GUEST, false);
                } else {
                    currentTable.getGuests().add(guest);
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
                    DataHandler.getInstance().getTablesArrangementHandler().addNewTable(currentTable);
                } else {
                    DataHandler.getInstance().getTablesArrangementHandler().updateTable(oldTableData, currentTable);
                }
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            }
        });
        this.table_BTN_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            }
        });
    }

    private void setTableToGuests() {
        currentTable.getGuests().forEach(guest -> guest.setTable(currentTable.getName()));
    }

    private void updateDropdowns() {
        // Max capacity dropdown
        List<Long> numberList = new ArrayList<>();
        for (long i = Constants.MIN_TABLE_CAPACITY_OPTIONS; i <= Constants.MAX_TABLE_CAPACITY_OPTIONS; i++) {
            numberList.add(new Long(i));
        }
        this.table_DDM_max_capacity.setAdapter(
                new ArrayAdapter<Long>(
                        getActivity().getApplicationContext(),
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
        List<String> filters = new ArrayList<>();
        filters.add(Constants.ALL);
        filters.addAll(DataHandler.getInstance().getGuestslistHandler().getFilterValues().stream().map(Category::getName).collect(Collectors.toList()));
        this.table_DDM_categories.setAdapter(new ArrayAdapter<String>(
                getActivity().getApplicationContext(),
                R.layout.dropdown_menu_list_item,
                filters
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
        if (!currentTable.getCategory().equals(Constants.ALL)) {
            List<Guest> temp = new ArrayList<>();
            currentTable.getGuests().stream().forEach(guest -> {
                if (guest.getCategory().equals(currentTable.getCategory())) {
                    temp.add(guest);
                }
            });
            currentTable.setGuests(temp);
        }

        DataHandler.getInstance().getGuestslistHandler().updateGuestsList(currentTable.getCategory());
    }
}
