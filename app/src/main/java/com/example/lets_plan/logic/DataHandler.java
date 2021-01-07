package com.example.lets_plan.logic;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lets_plan.data.Category;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.data.Table;
import com.example.lets_plan.logic.callback.DataReadyInterface;
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.utils.Converter;
import com.example.lets_plan.logic.utils.DataType;
import com.google.firebase.firestore.FirebaseFirestore;
import com.victor.loading.rotate.RotateLoading;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class DataHandler {
    private static DataHandler instance;
    private final Context context;
    private final Map<String, Set<Guest>> allGuests;
    private DataReadyInterface guestsDataReady;
    private final Map<String, Set<Table>> allTables;
    private DataReadyInterface tablesDataReady;
    private final Map<String, Category> allCategories;
    private String ownerID;
    private RotateLoading rotateLoading;

    private DataHandler(AppCompatActivity context) {
        this.context = context;
        this.allGuests = new TreeMap<>();
        this.allTables = new TreeMap<>();
        this.allCategories = new TreeMap<>();
        this.allCategories.put(Constants.ALL, new Category(Constants.ALL, 0));
        this.allCategories.put(Constants.OTHER_CATEGORY, new Category(Constants.OTHER_CATEGORY, 0));
    }

    public void setOwnerID(String ownerID) {
        if (ownerID != null && !ownerID.isEmpty()) {
            this.ownerID = ownerID;
            loadData();
        }
    }

    private void loadData() {
        FirebaseFirestore.getInstance()
                .collection(Constants.USERS_COLLECTION)
                .document(DataHandler.getInstance().getOwnerID())
                .collection(Constants.GUESTS_COLLECTION)
                .get()
                .addOnSuccessListener(task -> {
                    task.forEach(snapshot -> {
                        Guest guest = Converter.mapToObject(snapshot.getData(), Guest.class);
                        addItem(DataType.GUEST, guest.getCategory(), guest);
                        Category category = allCategories.get(guest.getCategory());
                        if (category == null) {
                            category = new Category(guest.getCategory(), 0);
                        }
                        category.addCount(guest.getNumberOfGuests().intValue());
                        Objects.requireNonNull(allCategories.get(Constants.ALL)).addCount(guest.getNumberOfGuests().intValue());
                        allCategories.put(guest.getCategory(), category);
                    });
                    if (guestsDataReady != null) {
                        guestsDataReady.dataReady();
                    }
                });
        FirebaseFirestore.getInstance()
                .collection(Constants.USERS_COLLECTION)
                .document(DataHandler.getInstance().getOwnerID())
                .collection(Constants.TABLES_COLLECTION)
                .get()
                .addOnSuccessListener(task -> {
                    task.forEach(snapshot -> {
                        Table table = Converter.mapToObject(snapshot.getData(), Table.class);
                        addItem(DataType.TABLE, table.getCategory(), table);
                    });
                    if (tablesDataReady != null) {
                        tablesDataReady.dataReady();
                    }
                });
    }

    public <T extends Comparable<T>> void addItem(DataType dataType, String category, T item) {
        if (item == null) {
            return;
        }
        try {
            if (dataType == DataType.GUEST) {
                Guest guest = (Guest) item;
                addToMap(allGuests, category, guest);
            } else if (dataType == DataType.TABLE) {
                Table table = (Table) item;
                addToMap(allTables, category, table);
            }
        } catch (ClassCastException ignored) {
        }
    }

    private <T extends Comparable<T>> void addToMap(Map<String, Set<T>> allItems, String category, T item) {
        if (allItems.containsKey(category)) {
            Objects.requireNonNull(allItems.get(category)).add(item);
        } else {
            Set<T> newSet = new TreeSet<>();
            newSet.add(item);
            allItems.put(category, newSet);
        }
    }

    public String getOwnerID() {
        return ownerID;
    }

    public Context getContext() {
        return context;
    }

    public void setRotateLoading(RotateLoading rotateLoading) {
        this.rotateLoading = rotateLoading;
    }

    public RotateLoading getRotateLoading() {
        return rotateLoading;
    }

    public static void init(AppCompatActivity activity) {
        if (instance == null) {
            instance = new DataHandler(activity);
        }
    }

    public <T> boolean isDataModified(T o1, T o2) {
        return !o1.equals(o2);
    }

    public static DataHandler getInstance() {
        return instance;
    }

    public List<Guest> getAllGuests() {
        return this.allGuests.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Table> getAllTables() {
        return this.allTables.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public Guest findGuestByPhone(String phone) {
        return getAllGuests().stream().filter(guest -> guest.getPhoneNumber().equals(phone)).findAny().orElse(null);
    }

    public Set<String> getAllCategoriesNames() {
        return this.allCategories.keySet();
    }

    public Set<Category> getAllCategories() {
        return new TreeSet<>(allCategories.values());
    }

    public Map<String, Category> getAllCategoryMap() { return this.allCategories; }

    public Category findCategoryByName(String currentFilter) {
        return allCategories.get(currentFilter);
    }

    public Set<Guest> findGuestsByCategory(String category) {
        if (this.allGuests.isEmpty() || this.allGuests.get(category) == null) {
            return new TreeSet<>();
        }
        return this.allGuests.get(category);
    }

    public Set<Table> findTablesByCategory(String category) {
        if (this.allTables.isEmpty() || this.allTables.get(category) == null) {
            return new TreeSet<>();
        }
        return this.allTables.get(category);
    }

    public void setGuestsDataReady(DataReadyInterface guestsDataReady) {
        this.guestsDataReady = guestsDataReady;
    }

    public void setTablesDataReady(DataReadyInterface tablesDataReady) {
        this.tablesDataReady = tablesDataReady;
    }

    public void removeCategory(String category) {
        allCategories.remove(category);
        allGuests.remove(category);
        allTables.remove(category);
    }

    public Set<String> getTablesCategories() {
        return this.allTables.keySet();
    }

    public Table findTableByCategoryAndName(String category, String name) {
        return this.allTables.get(category).stream().filter(table -> table.getName().equals(name)).findAny().orElse(null);
    }
}