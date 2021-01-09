package com.example.lets_plan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_plan.R;
import com.example.lets_plan.data.Category;
import com.example.lets_plan.data.Table;
import com.example.lets_plan.logic.recyclerview.handler.ItemsHandler;
import com.example.lets_plan.logic.recyclerview.handler.TablesArrangementHandler;
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.callback.DataReadyInterface;
import com.example.lets_plan.logic.callback.DataClickedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fragment_Tables_Arrangement extends Fragment_Base {
    private AutoCompleteTextView tables_arrangement_DDM_categories;
    private RecyclerView tables_arrangement_RCV_list;
    private ImageButton tables_arrangement_IBT_add;
    private final ItemsHandler<Table> itemsHandler;

    public Fragment_Tables_Arrangement() {
        this.itemsHandler = new TablesArrangementHandler();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_tables_arrangement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initViews();
        updateDropdown();
    }

    private void findViews(View view) {
        this.tables_arrangement_DDM_categories = view.findViewById(R.id.tables_arrangement_DDM_categories);
        this.tables_arrangement_RCV_list = view.findViewById(R.id.tables_arrangement_RCV_list);
        this.tables_arrangement_IBT_add = view.findViewById(R.id.tables_arrangement_IBT_add);
    }

    private void initViews() {
        String category = getFromSharedPreferences(Constants.CURRENT_TABLE_CATEGORY, Constants.ALL);
        itemsHandler.setRecyclerView(this.tables_arrangement_RCV_list, Objects.requireNonNull(getActivity()).getApplicationContext(), category);
        itemsHandler.setDataReadyInterface(new DataReadyInterface() {
            @Override
            public void dataReady() {
                updateDropdown();
                DataHandler.getInstance().getRotateLoading().stop();
            }
        });
        itemsHandler.setDataClickedListener(new DataClickedListener<Table>() {
            @Override
            public void dataClicked(Table table) {
                saveToSharedPreferences(Constants.NEW_TABLE, toJson(false, Boolean.class));
                saveToSharedPreferences(Constants.CURRENT_TABLE, toJson(table, Table.class));
                switchParentFragment(R.id.main_FGMT_container, new Fragment_Table(itemsHandler), false);
            }
        });
        this.tables_arrangement_IBT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToSharedPreferences(Constants.NEW_TABLE, toJson(true, Boolean.class));
                switchParentFragment(R.id.main_FGMT_container, new Fragment_Table(itemsHandler), false);
            }
        });
    }

    private void updateDropdown() {
        List<Category> categories = new ArrayList<>();
        DataHandler.getInstance().getTablesCategories().forEach(category -> {
            categories.add(new Category(category, DataHandler.getInstance().findTablesByCategory(category).size()));
        });
        this.tables_arrangement_DDM_categories.setAdapter(
                new ArrayAdapter<>(
                        Objects.requireNonNull(getActivity()).getApplicationContext(),
                        R.layout.dropdown_menu_list_item,
                        categories
                )
        );
        String currentCategory = getFromSharedPreferences(Constants.CURRENT_TABLE_CATEGORY, "");
        this.tables_arrangement_DDM_categories.setText((currentCategory != null ? currentCategory : Constants.ALL), false);
        this.tables_arrangement_DDM_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) parent.getItemAtPosition(position);
                saveToSharedPreferences(Constants.CURRENT_TABLE_CATEGORY, category.getName());
                tables_arrangement_DDM_categories.setText(category.getName(), false);
                itemsHandler.updateList(category.getName());
            }
        });
    }
}
