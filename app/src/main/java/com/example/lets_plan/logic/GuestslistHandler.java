package com.example.lets_plan.logic;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_plan.data.Filter;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.logic.callback.DataReadyInterface;
import com.example.lets_plan.logic.callback.GuestClickedListener;
import com.example.lets_plan.logic.callback.ItemClickListener;
import com.example.lets_plan.logic.utils.Converter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class GuestslistHandler {
    // Recyclerview + Adapter
    private RecyclerView guestslist_RCV_list;
    private GuestslistRecyclerViewAdapter guestslistAdapter;
    // Firebase + Data
    private FirebaseFirestore db;
    private Map<Filter, List<Guest>> guestslist;
    private String ownerID;
    private int totalGuests;
    // Utils
    private Converter<Guest> converter;
    // Callbacks
    private DataReadyInterface dataReadyInterface;
    private GuestClickedListener guestClickedListener;

    public GuestslistHandler(Context context, String ownerID, RecyclerView guestslist_RCV_list) {
        this.db = FirebaseFirestore.getInstance();
        this.guestslist = new TreeMap<>();
        this.converter = new Converter<>();
        this.ownerID = ownerID;
        this.guestslist_RCV_list = guestslist_RCV_list;
        loadGuestsList(context);
    }

    private void loadGuestsList(Context context) {
        if (ownerID != null && !ownerID.isEmpty()) {
            this.db.collection(ownerID)
                    .get()
                    .addOnSuccessListener(task -> {
                        task.forEach(snapshot -> {
                            addGuestToList(converter.mapToObject(snapshot.getData(), Guest.class));
                            if (dataReadyInterface != null) {
                                dataReadyInterface.dataReady();
                                initGuestsList(context, Constants.ALL);
                            }
                        });
                    });
        }
    }

    public void initGuestsList(Context context, String filter) {
        this.guestslistAdapter = new GuestslistRecyclerViewAdapter(context, getFilteredList(filter));
        guestslistAdapter.setClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                guestClickedListener.guestClicked(guestslistAdapter.getItem(position));
            }
        });
        this.guestslist_RCV_list.setLayoutManager(new LinearLayoutManager(context));
        this.guestslist_RCV_list.setAdapter(guestslistAdapter);
    }

    private void addGuestToList(Guest guest) {
        if (guest == null){
            return;
        }
        Filter tempFilter = new Filter(guest.getCategory(), 0);
        if (this.guestslist.containsKey(tempFilter)) {
            Filter filter = this.guestslist.keySet().stream().filter(tempFilter::equals).findAny().orElseThrow(() -> new RuntimeException("Filter not found"));
            filter.addCount(guest.getNumberOfGuests().intValue());
            this.guestslist.get(filter).add(guest);
        } else {
            addGuestToNewList(tempFilter, guest);
        }
    }

    private void addGuestToNewList(Filter filter, Guest guest) {
        List<Guest> newFilteredList = new ArrayList<>();
        newFilteredList.add(guest);
        filter.addCount(guest.getNumberOfGuests().intValue());
        this.guestslist.put(filter, newFilteredList);
    }


    public void addNewGuest(Guest guest) {
        addGuestToList(guest);
        db.collection(ownerID).document(guest.getPhoneNumber()).set(guest);
    }

    public void updateGuest(Guest oldGuest, Guest guest) {
        db.collection(ownerID).document(guest.getPhoneNumber()).update(converter.objectToMap(guest));
        List<Guest> filteredGuests;
        Filter filter;
        // Change category
        boolean isCategoryChanges = !oldGuest.getCategory().equals(guest.getCategory());
        if (isCategoryChanges) {
            int oldFilterIndex = getFilterIndex(oldGuest.getCategory());
            if (oldFilterIndex != -1) {
                filter = getFilterValues().get(oldFilterIndex);
                filteredGuests = getFilteredList(filter.getName());
                filteredGuests.remove(oldGuest);
                filter.substractCount(oldGuest.getNumberOfGuests().intValue());
            }
        }
        int filterIndex = getFilterIndex(guest.getCategory());
        if (filterIndex != -1) {
            filter = getFilterValues().get(filterIndex);
            filteredGuests = getFilteredList(filter.getName());
            if (!isCategoryChanges) {
                Guest guestToUpdate = filteredGuests.stream().filter(e -> e.compareTo(guest) == 0).findAny().get();
                filter.substractCount(oldGuest.getNumberOfGuests().intValue());
                filter.addCount(guestToUpdate.getNumberOfGuests().intValue());
            } else {
                addGuestToList(guest);
            }
        } else {
            addGuestToNewList(new Filter(guest.getCategory(), 0), guest);
        }
    }

    public List<Guest> getFilteredList(String category) {
        Filter temp = new Filter(category, 0);
        if (category.equals(Constants.ALL)) {
            List<Guest> allGuests = new ArrayList<>();
            guestslist.values().forEach(allGuests::addAll);
            Collections.sort(allGuests);
            return allGuests;
        } else if (guestslist.containsKey(temp)) {
            return guestslist.get(temp);
        } else {
            return null;
        }
    }

    public List<Filter> getFilterValues() {
        return new ArrayList<>(this.guestslist.keySet());
    }

    public int getFilterIndex(String filter) {
        return getFilterValues().indexOf(new Filter(filter, 0));
    }

    public Filter getTotalFilter() {
        totalGuests = 0;
        this.guestslist.values().forEach(list -> list.forEach(guest -> totalGuests += guest.getNumberOfGuests()));
        return new Filter(Constants.ALL, totalGuests);
    }

    public void updateGuestsList(String filter) {
        if (this.guestslist_RCV_list != null && this.guestslistAdapter != null) {
            this.guestslist_RCV_list.removeAllViewsInLayout();
            this.guestslistAdapter.updateGuestList(getFilteredList(filter));
        }
    }

    public void setDataReadyInterface(DataReadyInterface dataReadyInterface) {
        this.dataReadyInterface = dataReadyInterface;
    }

    public void setGuestClickedListener(GuestClickedListener guestClickedListener) {
        this.guestClickedListener = guestClickedListener;
    }
}
