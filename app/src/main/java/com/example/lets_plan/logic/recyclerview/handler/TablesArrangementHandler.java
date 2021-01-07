package com.example.lets_plan.logic.recyclerview.handler;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_plan.data.Category;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.data.Table;
import com.example.lets_plan.logic.callback.DataReadyInterface;
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.callback.ItemClickListener;
import com.example.lets_plan.logic.recyclerview.adapter.TableViewAdapter;
import com.example.lets_plan.logic.utils.Converter;
import com.example.lets_plan.logic.utils.DataType;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TablesArrangementHandler extends ItemsHandler<Table> {
    // Recyclerview + Adapter
    private TableViewAdapter tablesAdapter;

    public TablesArrangementHandler() {
        loadList();
    }

    @Override
    public void loadList() {
        DataHandler.getInstance().setTablesDataReady(new DataReadyInterface() {
            @Override
            public void dataReady() {
                if (getDataReadyInterface() != null) {
                    getDataReadyInterface().dataReady();
                    initAdapter(Constants.ALL);
                }
            }
        });
    }

    @Override
    public void saveItem(Table table) {
        FirebaseFirestore.getInstance()
                .collection(Constants.USERS_COLLECTION)
                .document(DataHandler.getInstance().getOwnerID())
                .collection(Constants.TABLES_COLLECTION)
                .document(table.getName())
                .set(table);
        table.getGuests().forEach(phone -> {
            Guest guest = DataHandler.getInstance().findGuestByPhone(phone);
            if (guest != null) {
                FirebaseFirestore.getInstance()
                        .collection(Constants.USERS_COLLECTION)
                        .document(DataHandler.getInstance().getOwnerID())
                        .collection(Constants.GUESTS_COLLECTION)
                        .document(phone)
                        .update(Converter.objectToMap(guest));
            }
        });
    }

    @Override
    public void updateItem(Table oldTable, Table newTable) {
        Set<Table> filteredTables = new TreeSet<>();
        Category category;
        // Change category
        boolean isCategoryChanges = !oldTable.getCategory().equals(newTable.getCategory());
        if (isCategoryChanges) {
            int oldFilterIndex = findCategoryIndexByName(oldTable.getCategory());
            if (oldFilterIndex != -1) {
                category = getAllCategories().get(oldFilterIndex);
                filteredTables = DataHandler.getInstance().findTablesByCategory(category.getName());
                category.substractCount(1);
                filteredTables.remove(oldTable);
                if (filteredTables.isEmpty()) {
                    DataHandler.getInstance().removeCategory(category.getName());
                }
            }
        }
        int filterIndex = findCategoryIndexByName(newTable.getCategory());
        if (filterIndex != -1) {
            category = getAllCategories().get(filterIndex);
            filteredTables = DataHandler.getInstance().findTablesByCategory(category.getName());
        }
        if (!isCategoryChanges) {
            Table tableToUpdate = filteredTables.stream().filter(e -> e.compareTo(newTable) == 0).findAny().get();
            tableToUpdate.copyData(newTable);
        } else {
            Category newCategory = createNewCategory(newTable.getCategory());
            DataHandler.getInstance().addItem(DataType.TABLE, newCategory.getName(), newTable);
        }
        FirebaseFirestore.getInstance()
                .collection(Constants.USERS_COLLECTION)
                .document(DataHandler.getInstance().getOwnerID())
                .collection(Constants.TABLES_COLLECTION)
                .document(newTable.getName())
                .update(Converter.objectToMap(newTable));
        updateGuests(oldTable, newTable);
    }

    private void updateGuests(Table oldTable, Table newTable) {
        // Create a collection of guests whose been changed
        List<Guest> oldTableGuests = new ArrayList<>();
        oldTable.getGuests().forEach(phone -> {
            Guest guest = DataHandler.getInstance().findGuestByPhone(phone);
            oldTableGuests.add(guest);
        });
        List<Guest> newTableGuests = new ArrayList<>();
        newTable.getGuests().forEach(phone -> {
            Guest guest = DataHandler.getInstance().findGuestByPhone(phone);
            newTableGuests.add(guest);
        });
        Set<Guest> diff = new HashSet<>();

        Set<Guest> diffOldToNew = new HashSet<>(oldTableGuests);
        diffOldToNew.removeAll(newTableGuests);
        diff.addAll(diffOldToNew);

        Set<Guest> diffNewToOld = new HashSet<>(newTableGuests);
        diffNewToOld.removeAll(oldTableGuests);
        diff.addAll(diffNewToOld);

        diff.forEach(guest -> FirebaseFirestore.getInstance()
                        .collection(Constants.USERS_COLLECTION)
                        .document(DataHandler.getInstance().getOwnerID())
                        .collection(Constants.GUESTS_COLLECTION)
                        .document(guest.getPhoneNumber())
                        .update(Converter.objectToMap(guest)));
    }

    @Override
    public void initRecyclerView(Context context, String category) {
        GridLayoutManager layoutManager = new GridLayoutManager(context, Constants.TABLES_IN_ROW);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        getRcvList().setLayoutManager(layoutManager);
        initAdapter(category);
    }

    @Override
    public void setRecyclerView(RecyclerView rcv_list, Context context, String category) {
        setRcvList(rcv_list);
        initRecyclerView(context, category);
    }

    @Override
    public void initAdapter(String category) {
        List<Table> tables = findItemsByCategoryName(category);
        if (tables == null || tables.isEmpty()) {
            return;
        }
        this.tablesAdapter = new TableViewAdapter(tables);
//        updateList(category);
        tablesAdapter.setClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getDataClickedListener().dataClicked(tablesAdapter.getItem(position));
            }
        });
        getRcvList().swapAdapter(this.tablesAdapter, false);
    }

    @Override
    public void updateList(String category) {
        if (getRcvList() != null && this.tablesAdapter != null) {
            this.tablesAdapter.updateList(category);
        }
    }

    @Override
    public void addNewItem(Table table) {
        if (table == null){
            return;
        }
        Category category = DataHandler.getInstance().findCategoryByName(table.getCategory());
        if (category == null) {
            category = createNewCategory(table.getCategory());
        }
        DataHandler.getInstance().addItem(DataType.TABLE, category.getName(), table);
    }

    @Override
    public List<Table> findItemsByCategoryName(String category) {
        if (category.equals(Constants.ALL)) {
            return DataHandler.getInstance().getAllTables();
        }
        Set<Table> result = DataHandler.getInstance().findTablesByCategory(category);
        if (result == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(result);
    }

    @Override
    public Category getSummedCategory() {
        return new Category(Constants.ALL, DataHandler.getInstance().getAllTables().size());
    }

}
