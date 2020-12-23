package com.example.lets_plan.logic;

import com.example.lets_plan.data.Filter;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.logic.callback.CallbackInterface;
import com.example.lets_plan.logic.utils.Converter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class GuestslistHandler {
    private static GuestslistHandler instance;
    private FirebaseFirestore db;
    private Map<Filter, List<Guest>> guestslist;
    private Converter<Guest> converter;
    private String ownerID;
    private int totalGuests;
    private CallbackInterface callbackInterface;

    private GuestslistHandler(String ownerID) {
        this.db = FirebaseFirestore.getInstance();
        this.guestslist = new TreeMap<>();
        this.converter = new Converter<>();
        this.ownerID = ownerID;
        loadGuestsList();
    }

    private void loadGuestsList() {
        if (ownerID != null && !ownerID.isEmpty()) {
            this.db.collection(ownerID)
                    .get()
                    .addOnSuccessListener(task -> {
                        task.forEach(snapshot -> {
                            addGuestToList(converter.mapToObject(snapshot.getData(), Guest.class));
                            if (callbackInterface != null) {
                                callbackInterface.onCall();
                            }
                        });
                    });
        }
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

    public static void init(String ownerID) {
        if (instance == null) {
            instance = new GuestslistHandler(ownerID);
        }
    }

    public static GuestslistHandler getInstance() {
        return instance;
    }

    public Filter getTotalFilter() {
        totalGuests = 0;
        this.guestslist.values().stream().forEach(list -> list.forEach(guest -> totalGuests += guest.getNumberOfGuests()));
        return new Filter(Constants.ALL, totalGuests);
    }

    public void setCallbackInterface(CallbackInterface callbackInterface) {
        this.callbackInterface = callbackInterface;
    }
}
