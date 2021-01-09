package com.example.lets_plan.logic.recyclerview.handler;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_plan.data.Category;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.callback.DataClickedListener;
import com.example.lets_plan.logic.callback.DataReadyInterface;
import com.example.lets_plan.logic.callback.ItemClickListener;
import com.example.lets_plan.logic.recyclerview.adapter.GuestViewAdapter;
import com.example.lets_plan.logic.utils.Constants;
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

public abstract class ItemsHandler<T> {
    // Recyclerview + Adapter
    private RecyclerView rcv_list;
    // Data
//    private Map<Category, Set<T>> items;
    private int totalItems;
    // Callbacks
    private DataReadyInterface dataReadyInterface;
    private DataClickedListener dataClickedListener;

    public ItemsHandler() {
    }

    // FirebaseFirestore
    public abstract void loadList();

    public abstract void saveItem(T item);

    public abstract void updateItem(T oldItem, T newItem);

    public abstract void removeItem(T item);

    // RecyclerView
    public abstract void initRecyclerView(Context context, String category);

    public abstract void setRecyclerView(RecyclerView rcv_list, Context context, String category);

    public abstract void initAdapter(String category);

    public abstract void updateList(String category);

    // Creation
    public abstract void addNewItem(T item);

    protected Category createNewCategory(String name) {
        Category newCategory = new Category(name, 0);
        DataHandler.getInstance().getAllCategoryMap().put(name, newCategory);
        return newCategory;
    }

    // Find
    public int findCategoryIndexByName(String name) {
        return getAllCategories().indexOf(new Category(name, 0));
    }

    public abstract List<T> findItemsByCategoryName(String category);

    // Getters & Setters
    public List<Category> getAllCategories() {
        return new ArrayList<>(new ArrayList<>(DataHandler.getInstance().getAllCategories()));
    }

    public abstract Category getSummedCategory();

    protected RecyclerView getRcvList() {
        return rcv_list;
    }

    protected void setRcvList(RecyclerView rcv_list) {
        this.rcv_list = rcv_list;
    }

    protected int getTotalItems() {
        return totalItems;
    }

    protected void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    // Callbacks
    public void setDataReadyInterface(DataReadyInterface dataReadyInterface) {
        this.dataReadyInterface = dataReadyInterface;
    }

    protected DataReadyInterface getDataReadyInterface() {
        return this.dataReadyInterface;
    }

    public void setDataClickedListener(DataClickedListener dataClickedListener) {
        this.dataClickedListener = dataClickedListener;
    }

    protected DataClickedListener<T> getDataClickedListener() {
        return dataClickedListener;
    }
}
