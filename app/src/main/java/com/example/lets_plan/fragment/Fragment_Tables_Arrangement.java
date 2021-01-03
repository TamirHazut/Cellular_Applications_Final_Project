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
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.callback.DataReadyInterface;
import com.example.lets_plan.logic.callback.DataClickedListener;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Tables_Arrangement extends Fragment_Base {
    private AutoCompleteTextView tables_arrangement_DDM_filters;
    private RecyclerView tables_arrangement_RCV_list;
    private ImageButton tables_arrangement_IBT_add;

    public Fragment_Tables_Arrangement() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_tables_arrangement, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initViews();
        updateDropdown();
    }

    private void findViews(View view) {
        this.tables_arrangement_DDM_filters = view.findViewById(R.id.tables_arrangement_DDM_filters);
        this.tables_arrangement_RCV_list = view.findViewById(R.id.tables_arrangement_RCV_list);
        this.tables_arrangement_IBT_add = view.findViewById(R.id.tables_arrangement_IBT_add);
    }

    private void initViews() {
        DataHandler.getInstance().initTablesArrangementHandler();
        String filter = getFromSharedPreferences(Constants.CURRENT_TABLE_FILTER, Constants.ALL);
        DataHandler.getInstance().getTablesArrangementHandler().setTablesRecyclerView(this.tables_arrangement_RCV_list, getActivity().getApplicationContext(), filter);
        DataHandler.getInstance().getTablesArrangementHandler().setDataReadyInterface(new DataReadyInterface() {
            @Override
            public void dataReady() {
                updateDropdown();
                DataHandler.getInstance().getRotateLoading().stop();
            }
        });
        DataHandler.getInstance().getTablesArrangementHandler().setDataClickedListener(new DataClickedListener<Table>() {
            @Override
            public void dataClicked(Table table) {
                saveToSharedPreferences(Constants.NEW_TABLE, toJson(false, Boolean.class));
                saveToSharedPreferences(Constants.CURRENT_TABLE, toJson(table, Table.class));
                switchParentFragment(R.id.main_FGMT_container, new Fragment_Table(), false);
            }
        });
        this.tables_arrangement_IBT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToSharedPreferences(Constants.NEW_TABLE, toJson(true, Boolean.class));
                switchParentFragment(R.id.main_FGMT_container, new Fragment_Table(), false);
            }
        });
    }

    private void updateDropdown() {
        List<Category> categories = new ArrayList<>();
        categories.add(DataHandler.getInstance().getTablesArrangementHandler().getTotalCategory());
        categories.addAll(DataHandler.getInstance().getTablesArrangementHandler().getFilterValues());
        this.tables_arrangement_DDM_filters.setAdapter(
                new ArrayAdapter<Category>(
                        getActivity().getApplicationContext(),
                        R.layout.dropdown_menu_list_item,
                        categories
                )
        );
        String currentFilter = getFromSharedPreferences(Constants.CURRENT_TABLE_FILTER, "");
        String displayFilter = categories.stream().map(Category::getName).filter(currentFilter::equals).findAny().orElse(categories.get(0).getName());
        this.tables_arrangement_DDM_filters.setText(displayFilter, false);
        this.tables_arrangement_DDM_filters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) parent.getItemAtPosition(position);
                saveToSharedPreferences(Constants.CURRENT_TABLE_FILTER, category.getName());
                tables_arrangement_DDM_filters.setText(category.getName(), false);
                DataHandler.getInstance().getTablesArrangementHandler().updateTables(category.getName());
            }
        });
    }
}
