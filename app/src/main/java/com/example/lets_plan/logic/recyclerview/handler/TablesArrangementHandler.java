package com.example.lets_plan.logic.recyclerview.handler;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_plan.data.Category;
import com.example.lets_plan.data.Table;
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.callback.DataClickedListener;
import com.example.lets_plan.logic.callback.DataReadyInterface;
import com.example.lets_plan.logic.callback.ItemClickListener;
import com.example.lets_plan.logic.recyclerview.adapter.TablesarrangementRecyclerViewAdapter;
import com.example.lets_plan.logic.utils.Converter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class TablesArrangementHandler {
    // Recyclerview + Adapter
    private RecyclerView tables_arrangement_RCV_list;
    private TablesarrangementRecyclerViewAdapter tablesAdapter;
    //Data
    private Map<Category, Set<Table>> tables;
    private DataReadyInterface dataReadyInterface;
    private DataClickedListener<Table> dataClickedListener;

    public TablesArrangementHandler() {
        this.tables = new TreeMap<>();
        loadTables();
    }

    public void loadTables() {
        if (DataHandler.getInstance().getOwnerID() != null && !DataHandler.getInstance().getOwnerID().isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection(Constants.USERS_COLLECTION)
                    .document(DataHandler.getInstance().getOwnerID())
                    .collection(Constants.TABLES_COLLECTION)
                    .get()
                    .addOnSuccessListener(task -> {
                        task.forEach(snapshot -> {
                            Table table = Converter.mapToObject(snapshot.getData(), Table.class);
                            addTableToList(table);
                            if (dataReadyInterface != null) {
                                dataReadyInterface.dataReady();
                                initRecyclerViewAdapter(DataHandler.getInstance().getContext(), Constants.ALL);
                            }
                        });
                    });
        }
    }

    public void initRecyclerViewAdapter(Context context, String filter) {
        List<Table> filteredList = (filter.equals(Constants.ALL) ? getAllTables() : getFilteredList(filter).stream().collect(Collectors.toList()));
        setAdapter(filteredList);
        GridLayoutManager layoutManager = new GridLayoutManager(context, Constants.TABLES_IN_ROW);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.tables_arrangement_RCV_list.setLayoutManager(layoutManager);
        this.tables_arrangement_RCV_list.swapAdapter(this.tablesAdapter, false);
    }

    public void setAdapter(List<Table> filteredList) {
        this.tablesAdapter = new TablesarrangementRecyclerViewAdapter(filteredList);
        tablesAdapter.setClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dataClickedListener.dataClicked(tablesAdapter.getItem(position));
            }
        });
    }

    private void addTableToList(Table table) {
        if (table == null){
            return;
        }
        Category tempCategory = new Category(table.getCategory(), 0);
        if (this.tables.containsKey(tempCategory)) {
            tempCategory.addCount(1);
            this.tables.get(tempCategory).add(table);
        } else {
            addTableToNewList(tempCategory, table);
        }
    }

    private void addTableToNewList(Category category, Table table) {
        Set<Table> newFilteredSet = new TreeSet<>();
        category.addCount(1);
        newFilteredSet.add(table);
        this.tables.put(category, newFilteredSet);
    }

    public void addNewTable(Table table) {
        addTableToList(table);
        FirebaseFirestore.getInstance()
                .collection(Constants.USERS_COLLECTION)
                .document(DataHandler.getInstance().getOwnerID())
                .collection(Constants.TABLES_COLLECTION)
                .document(table.getName())
                .set(table);
    }

    public void updateTable(Table oldTable, Table table) {
        FirebaseFirestore.getInstance()
                .collection(Constants.USERS_COLLECTION)
                .document(DataHandler.getInstance().getOwnerID())
                .collection(Constants.TABLES_COLLECTION)
                .document(table.getName())
                .update(Converter.objectToMap(table));
        table.getGuests().forEach(guest -> FirebaseFirestore.getInstance()
                                                    .collection(Constants.USERS_COLLECTION)
                                                    .document(DataHandler.getInstance().getOwnerID())
                                                    .collection(Constants.GUESTS_COLLECTION)
                                                    .document(guest.getPhoneNumber())
                                                    .update(Converter.objectToMap(guest)));
        Set<Table> filteredTables = new TreeSet<>();
        Category category;
        // Change category
        boolean isCategoryChanges = !oldTable.getCategory().equals(table.getCategory());
        if (isCategoryChanges) {
            int oldFilterIndex = getFilterIndex(oldTable.getCategory());
            if (oldFilterIndex != -1) {
                category = getFilterValues().get(oldFilterIndex);
                filteredTables = getFilteredList(category.getName());
                category.substractCount(1);
                filteredTables.remove(oldTable);
                if (filteredTables.isEmpty()) {
                    this.tables.remove(category);
                }
            }
        }
        int filterIndex = getFilterIndex(table.getCategory());
        if (filterIndex != -1) {
            category = getFilterValues().get(filterIndex);
            filteredTables = getFilteredList(category.getName());
        }
        if (!isCategoryChanges) {
            Table tableToUpdate = filteredTables.stream().filter(e -> e.compareTo(table) == 0).findAny().get();
            tableToUpdate.copyData(table);
        } else {
            addTableToList(table);
        }
    }

    public Set<Table> getFilteredList(String category) {
        Category temp = new Category(category, 0);
         if (tables.containsKey(temp)) {
            return tables.get(temp);
        } else {
            return null;
        }
    }
    /*
    if (category.equals(Constants.ALL)) {
            Set<Table> allTables = new TreeSet<>();
            tables.values().forEach(allTables::addAll);
            return allTables;
        } else
     */

    private List<Table> getAllTables() {
        return this.tables.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Category> getFilterValues() {
        List<Category> categoriesValues = new ArrayList<>(this.tables.keySet());
        Category all = new Category(Constants.ALL, 0);
        if (categoriesValues.contains(all)) {
            categoriesValues.remove(all);
        }
        return categoriesValues;
    }

    public int getFilterIndex(String filter) {
        return getFilterValues().indexOf(new Category(filter, 0));
    }

    public Category getTotalCategory() {
        return new Category(Constants.ALL, this.tables.values().size());
    }

    public void updateTables(String filter) {
        if (this.tables_arrangement_RCV_list != null && this.tablesAdapter != null) {
            this.tablesAdapter.updateTables(filter);
        }
    }

    public void setDataReadyInterface(DataReadyInterface dataReadyInterface) {
        this.dataReadyInterface = dataReadyInterface;
    }

    public void setDataClickedListener(DataClickedListener<Table> dataClickedListener) {
        this.dataClickedListener = dataClickedListener;
    }

    public void setTablesRecyclerView(RecyclerView guests_list_RCV_list, Context context, String filter) {
        this.tables_arrangement_RCV_list = guests_list_RCV_list;
        initRecyclerViewAdapter(context, filter);
    }

}
