package com.example.lets_plan.logic.recyclerview.handler;

import android.view.View;

import com.example.lets_plan.data.Guest;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.callback.ItemClickListener;
import com.example.lets_plan.logic.recyclerview.adapter.GuestViewAdapter;
import com.example.lets_plan.logic.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ExtendedGuestslistHandler extends GuestslistHandler {
    private boolean hiddenCheckMark;
    private List<Guest> checkedGuests;

    public ExtendedGuestslistHandler(boolean hiddenCheckMark, List<Guest> checkedGuests) {
        this.hiddenCheckMark = hiddenCheckMark;
        this.checkedGuests = checkedGuests;
    }

    @Override
    public void initAdapter(String category) {
        if (checkedGuests == null) {
            return;
        }
        List<Guest> guests = findItemsByCategoryName(category);
        if (guests == null || guests.isEmpty()) {
            return;
        }
        setGuestslistAdapter(new GuestViewAdapter(guests, hiddenCheckMark, checkedGuests));
        getGuestslistAdapter().setClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getDataClickedListener().dataClicked(getGuestslistAdapter().getItem(position));
            }
        });
        getRcvList().swapAdapter(getGuestslistAdapter(), false);
    }
}
