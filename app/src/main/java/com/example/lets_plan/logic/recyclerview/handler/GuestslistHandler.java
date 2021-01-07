package com.example.lets_plan.logic.recyclerview.handler;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_plan.data.Category;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.logic.callback.DataReadyInterface;
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.callback.ItemClickListener;
import com.example.lets_plan.logic.recyclerview.adapter.GuestViewAdapter;
import com.example.lets_plan.logic.utils.Converter;
import com.example.lets_plan.logic.utils.DataType;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GuestslistHandler extends ItemsHandler<Guest> {
    // Recyclerview + Adapter
    private GuestViewAdapter guestslistAdapter;

    public GuestslistHandler() {
        loadList();
    }

    @Override
    public void loadList() {
        DataHandler.getInstance().setGuestsDataReady(new DataReadyInterface() {
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
    public void saveItem(Guest item) {
        FirebaseFirestore.getInstance()
                .collection(Constants.USERS_COLLECTION)
                .document(DataHandler.getInstance().getOwnerID())
                .collection(Constants.GUESTS_COLLECTION)
                .document(item.getPhoneNumber())
                .set(item);
    }

    @Override
    public void updateItem(Guest oldGuest, Guest newGuest) {
        Set<Guest> filteredGuests;
        Category category;
        // Change category
        boolean isCategoryChanges = !oldGuest.getCategory().equals(newGuest.getCategory());
        if (isCategoryChanges) {
            int oldFilterIndex = findCategoryIndexByName(oldGuest.getCategory());
            if (oldFilterIndex != -1) {
                category = getAllCategories().get(oldFilterIndex);
                filteredGuests = DataHandler.getInstance().findGuestsByCategory(category.getName());
                filteredGuests.remove(oldGuest);
                category.substractCount(oldGuest.getNumberOfGuests().intValue());
                if (filteredGuests.isEmpty()) {
                    DataHandler.getInstance().removeCategory(category.getName());
                }
            }
        }
        int filterIndex = findCategoryIndexByName(newGuest.getCategory());
        if (filterIndex != -1) {
            category = getAllCategories().get(filterIndex);
            filteredGuests = DataHandler.getInstance().findGuestsByCategory(category.getName());
            if (!isCategoryChanges) {
                Guest guestToUpdate = filteredGuests.stream().filter(e -> e.compareTo(newGuest) == 0).findAny().orElse(null);
                if (guestToUpdate != null) {
                    guestToUpdate.copyData(newGuest);
                    category.substractCount(oldGuest.getNumberOfGuests().intValue());
                    category.addCount(guestToUpdate.getNumberOfGuests().intValue());
                }
            } else {
                addNewItem(newGuest);
            }
        } else {
            Category newCategory = createNewCategory(newGuest.getCategory());
            DataHandler.getInstance().addItem(DataType.GUEST, newCategory.getName(), newGuest);
        }
        FirebaseFirestore.getInstance()
                .collection(Constants.USERS_COLLECTION)
                .document(DataHandler.getInstance().getOwnerID())
                .collection(Constants.GUESTS_COLLECTION)
                .document(newGuest.getPhoneNumber())
                .update(Converter.objectToMap(newGuest));
    }

    @Override
    public void initRecyclerView(Context context, String category) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
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
        List<Guest> guests = findItemsByCategoryName(category);
        if (guests == null || guests.isEmpty()) {
            return;
        }
        this.guestslistAdapter = new GuestViewAdapter(new ArrayList<>(guests));
        guestslistAdapter.setClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getDataClickedListener().dataClicked(guestslistAdapter.getItem(position));
            }
        });
        getRcvList().swapAdapter(this.guestslistAdapter, false);
    }

    @Override
    public void updateList(String filter) {
        if (getRcvList() != null && this.guestslistAdapter != null) {
            this.guestslistAdapter.updateList(filter);
        }
    }

    @Override
    public void addNewItem(Guest guest) {
        if (guest == null){
            return;
        }
        Category category = DataHandler.getInstance().findCategoryByName(guest.getCategory());
        if (category == null) {
            category = createNewCategory(guest.getCategory());
        }
        category.addCount(guest.getNumberOfGuests().intValue());
        DataHandler.getInstance().addItem(DataType.GUEST, category.getName(), guest);
    }

    @Override
    public List<Guest> findItemsByCategoryName(String category) {
        if (category.equals(Constants.ALL)) {
            return DataHandler.getInstance().getAllGuests();
        }
        return new ArrayList<>(DataHandler.getInstance().findGuestsByCategory(category));
    }

    @Override
    public Category getSummedCategory() {
        setTotalItems(0);
        DataHandler.getInstance().getAllGuests().forEach(guest -> setTotalItems(getTotalItems() + guest.getNumberOfGuests().intValue()));
        return new Category(Constants.ALL, getTotalItems());
    }

    protected GuestViewAdapter getGuestslistAdapter() {
        return guestslistAdapter;
    }

    protected void setGuestslistAdapter(GuestViewAdapter guestslistAdapter) {
        this.guestslistAdapter = guestslistAdapter;
    }

}
