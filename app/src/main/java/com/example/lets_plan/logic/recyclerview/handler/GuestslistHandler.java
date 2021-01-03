package com.example.lets_plan.logic.recyclerview.handler;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_plan.data.Category;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.callback.DataReadyInterface;
import com.example.lets_plan.logic.callback.DataClickedListener;
import com.example.lets_plan.logic.callback.ItemClickListener;
import com.example.lets_plan.logic.recyclerview.adapter.GuestslistRecyclerViewAdapter;
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

public class GuestslistHandler {
    // Recyclerview + Adapter
    private RecyclerView guests_list_RCV_list;
    private GuestslistRecyclerViewAdapter guestslistAdapter;
    private boolean changeItemBackgroundOnClick;
    // Data
    private Map<Category, Set<Guest>> guestslist;
    private int totalGuests;
    // Callbacks
    private DataReadyInterface dataReadyInterface;
    private DataClickedListener<Guest> dataClickedListener;

    public GuestslistHandler() {
        this.guestslist = new TreeMap<>();
        loadGuestsList();
    }

    public void loadGuestsList() {
        if (DataHandler.getInstance().getOwnerID() != null && !DataHandler.getInstance().getOwnerID().isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection(Constants.USERS_COLLECTION)
                    .document(DataHandler.getInstance().getOwnerID())
                    .collection(Constants.GUESTS_COLLECTION)
                    .get()
                    .addOnSuccessListener(task -> {
                        task.forEach(snapshot -> {
                            addGuestToList(Converter.mapToObject(snapshot.getData(), Guest.class));
                            if (dataReadyInterface != null) {
                                dataReadyInterface.dataReady();
                                initRecyclerViewAdapter(DataHandler.getInstance().getContext(), Constants.ALL, null);
                            }
                        });
                    });
        }
    }
    public void initRecyclerViewAdapter(Context context, String filter, List<Guest> guests) {
        setAdapter(getAllGuests(), filter, guests);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        this.guests_list_RCV_list.setLayoutManager(layoutManager);
        this.guests_list_RCV_list.swapAdapter(this.guestslistAdapter, false);
    }

    public void setAdapter(List<Guest> filteredList, String filter, List<Guest> guests) {
        this.guestslistAdapter = new GuestslistRecyclerViewAdapter(filteredList, changeItemBackgroundOnClick, guests);
        updateGuestsList(filter);
        guestslistAdapter.setClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dataClickedListener.dataClicked(guestslistAdapter.getItem(position));
            }
        });
    }

    public void setChangeItemBackgroundOnClick(boolean answer) {
        this.changeItemBackgroundOnClick = answer;
    }

    private void addGuestToList(Guest guest) {
        if (guest == null){
            return;
        }
        Category tempCategory = new Category(guest.getCategory(), 0);
        if (this.guestslist.containsKey(tempCategory)) {
            Category category = this.guestslist.keySet().stream().filter(tempCategory::equals).findAny().orElseThrow(() -> new RuntimeException("Filter not found"));
            category.addCount(guest.getNumberOfGuests().intValue());
            this.guestslist.get(category).add(guest);
        } else {
            addGuestToNewList(tempCategory, guest);
        }
    }

    private void addGuestToNewList(Category category, Guest guest) {
        Set<Guest> newFilteredSet = new TreeSet<>();
        newFilteredSet.add(guest);
        category.addCount(guest.getNumberOfGuests().intValue());
        this.guestslist.put(category, newFilteredSet);
    }


    public void addNewGuest(Guest guest) {
        addGuestToList(guest);
        FirebaseFirestore.getInstance()
                .collection(Constants.USERS_COLLECTION)
                .document(DataHandler.getInstance().getOwnerID())
                .collection(Constants.GUESTS_COLLECTION)
                .document(guest.getPhoneNumber())
                .set(guest);
    }

    public void updateGuest(Guest oldGuest, Guest guest) {
        FirebaseFirestore.getInstance()
                .collection(Constants.USERS_COLLECTION)
                .document(DataHandler.getInstance().getOwnerID())
                .collection(Constants.GUESTS_COLLECTION)
                .document(guest.getPhoneNumber())
                .update(Converter.objectToMap(guest));
        Set<Guest> filteredGuests;
        Category category;
        // Change category
        boolean isCategoryChanges = !oldGuest.getCategory().equals(guest.getCategory());
        if (isCategoryChanges) {
            int oldFilterIndex = getFilterIndex(oldGuest.getCategory());
            if (oldFilterIndex != -1) {
                category = getFilterValues().get(oldFilterIndex);
                filteredGuests = getFilteredList(category.getName());
                filteredGuests.remove(oldGuest);
                category.substractCount(oldGuest.getNumberOfGuests().intValue());
                if (filteredGuests.isEmpty()) {
                    this.guestslist.remove(category);
                }
            }
        }
        int filterIndex = getFilterIndex(guest.getCategory());
        if (filterIndex != -1) {
            category = getFilterValues().get(filterIndex);
            filteredGuests = getFilteredList(category.getName());
            if (!isCategoryChanges) {
                Guest guestToUpdate = filteredGuests.stream().filter(e -> e.compareTo(guest) == 0).findAny().get();
                guestToUpdate.copyData(guest);
                category.substractCount(oldGuest.getNumberOfGuests().intValue());
                category.addCount(guestToUpdate.getNumberOfGuests().intValue());
            } else {
                addGuestToList(guest);
            }
        } else {
            addGuestToNewList(new Category(guest.getCategory(), 0), guest);
        }
    }

    public Set<Guest> getFilteredList(String category) {
        Category temp = new Category(category, 0);
        if (category.equals(Constants.ALL)) {
            Set<Guest> allGuests = new TreeSet<>();
            guestslist.values().forEach(allGuests::addAll);
            return allGuests;
        } else if (guestslist.containsKey(temp)) {
            return guestslist.get(temp);
        } else {
            return null;
        }
    }

    public List<Category> getFilterValues() {
        return new ArrayList<>(this.guestslist.keySet());
    }

    public int getFilterIndex(String filter) {
        return getFilterValues().indexOf(new Category(filter, 0));
    }

    public Category getTotalFilter() {
        totalGuests = 0;
        this.guestslist.values().forEach(list -> list.forEach(guest -> totalGuests += guest.getNumberOfGuests()));
        return new Category(Constants.ALL, totalGuests);
    }

    private List<Guest> getAllGuests() {
        return this.guestslist.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public void updateGuestsList(String filter) {
        if (this.guests_list_RCV_list != null && this.guestslistAdapter != null) {
            this.guestslistAdapter.updateGuestList(filter);
        }
    }

    public void setDataReadyInterface(DataReadyInterface dataReadyInterface) {
        this.dataReadyInterface = dataReadyInterface;
    }

    public void setDataClickedListener(DataClickedListener dataClickedListener) {
        this.dataClickedListener = dataClickedListener;
    }

    public void setGuestslistRecyclerView(RecyclerView guests_list_RCV_list, Context context, String category) {
        setGuestslistRecyclerView(guests_list_RCV_list, context, category, null);
    }

    public void setGuestslistRecyclerView(RecyclerView guests_list_RCV_list, Context context, String category, List<Guest> guests) {
        this.guests_list_RCV_list = guests_list_RCV_list;
        initRecyclerViewAdapter(context, category, guests);
    }
}
