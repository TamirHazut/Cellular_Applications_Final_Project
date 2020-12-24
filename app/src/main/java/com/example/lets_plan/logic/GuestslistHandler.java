package com.example.lets_plan.logic;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_plan.data.Filter;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.logic.callback.CallbackInterface;
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
    private FirebaseFirestore db;
    private Map<Filter, List<Guest>> guestslist;
    private Converter<Guest> converter;
    private String ownerID;
    private int totalGuests;
    private CallbackInterface callbackInterface;
    private RecyclerView guestslist_RCV_list;
    private GuestslistRecyclerViewAdapter guestslistAdapter;

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
                            if (callbackInterface != null) {
                                callbackInterface.onCall();
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
                Log.d("GuestslistRecyclerView", guestslistAdapter.getItem(position).toString());
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
            List<Guest> newFilteredList = new ArrayList<>();
            newFilteredList.add(guest);
            tempFilter.addCount(guest.getNumberOfGuests().intValue());
            this.guestslist.put(tempFilter, newFilteredList);
        }
    }

    public void addNewGuest(Guest guest) {
        addGuestToList(guest);
        db.collection(ownerID).document(guest.getPhoneNumber()).set(guest);
    }

    public List<Guest> getFilteredList(String category) {
        Filter temp = new Filter(category, 0);
        if (category.equals(Constants.ALL)) {
            List<Guest> allGuests = new ArrayList<>();
            guestslist.values().stream().forEach(list -> allGuests.addAll(list));
            Collections.sort(allGuests);
            return allGuests;
        } else if (guestslist.containsKey(temp)) {
            return guestslist.get(temp);
        } else {
            return null;
        }
    }

    public List<Filter> getFilterValues() {
        return this.guestslist.keySet().stream().collect(Collectors.toList());
    }

    public Filter getTotalFilter() {
        totalGuests = 0;
        this.guestslist.values().stream().forEach(list -> list.forEach(guest -> totalGuests += guest.getNumberOfGuests()));
        return new Filter(Constants.ALL, totalGuests);
    }

    public void setCallbackInterface(CallbackInterface callbackInterface) {
        this.callbackInterface = callbackInterface;
    }

    public void updateGuestsList(String filter) {
        if (this.guestslist_RCV_list != null && this.guestslistAdapter != null) {
            this.guestslist_RCV_list.removeAllViewsInLayout();
            this.guestslistAdapter.updateGuestList(getFilteredList(filter));
        }
    }

}
