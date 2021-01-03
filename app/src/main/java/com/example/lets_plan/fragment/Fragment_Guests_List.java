package com.example.lets_plan.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_plan.R;
import com.example.lets_plan.data.Category;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.callback.DataReadyInterface;
import com.example.lets_plan.logic.callback.DataClickedListener;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Guests_List extends Fragment_Base {
    private AutoCompleteTextView guests_list_DDM_filters;
    private RecyclerView guests_list_RCV_list;
    private Button guests_list_BTN_send_invites;
    private ImageButton guests_list_IBT_add;

    public Fragment_Guests_List() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_guests_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initViews();
        updateDropdown();
    }

    private void findViews(View view) {
        this.guests_list_DDM_filters = view.findViewById(R.id.guests_list_DDM_filters);
        this.guests_list_RCV_list = view.findViewById(R.id.guests_list_RCV_list);
        this.guests_list_BTN_send_invites = view.findViewById(R.id.guests_list_BTN_send_invites);
        this.guests_list_IBT_add = view.findViewById(R.id.guests_list_IBT_add);
    }

    private void initViews() {
        DataHandler.getInstance().initGuestslistHandler();
        String filter = getFromSharedPreferences(Constants.CURRENT_GUEST_FILTER, Constants.ALL);
        DataHandler.getInstance().getGuestslistHandler().setGuestslistRecyclerView(this.guests_list_RCV_list, getActivity().getApplicationContext(), filter);
        DataHandler.getInstance().getGuestslistHandler().setDataReadyInterface(new DataReadyInterface() {
            @Override
            public void dataReady() {
                updateDropdown();
                DataHandler.getInstance().getRotateLoading().stop();
            }
        });
        DataHandler.getInstance().getGuestslistHandler().setDataClickedListener(new DataClickedListener<Guest>() {
            @Override
            public void dataClicked(Guest guest) {
                saveToSharedPreferences(Constants.NEW_GUEST, toJson(false, Boolean.class));
                saveToSharedPreferences(Constants.CURRENT_GUEST, toJson(guest, Guest.class));
                switchParentFragment(R.id.main_FGMT_container, new Fragment_Guest(), false);
            }
        });
        this.guests_list_IBT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToSharedPreferences(Constants.NEW_GUEST, toJson(true, Boolean.class));
                switchParentFragment(R.id.main_FGMT_container, new Fragment_Guest(), false);
            }
        });
        this.guests_list_BTN_send_invites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void updateDropdown() {
        List<Category> categories = new ArrayList<>();
        categories.add(DataHandler.getInstance().getGuestslistHandler().getTotalFilter());
        categories.addAll(DataHandler.getInstance().getGuestslistHandler().getFilterValues());
        this.guests_list_DDM_filters.setAdapter(
                new ArrayAdapter<Category>(
                        getActivity().getApplicationContext(),
                        R.layout.dropdown_menu_list_item,
                        categories
                )
        );
        String currentFilter = getFromSharedPreferences(Constants.CURRENT_GUEST_FILTER, "");
        String displayFilter = categories.stream().map(Category::getName).filter(currentFilter::equals).findAny().orElse(categories.get(0).getName());
        this.guests_list_DDM_filters.setText(displayFilter, false);
        this.guests_list_DDM_filters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) parent.getItemAtPosition(position);
                saveToSharedPreferences(Constants.CURRENT_GUEST_FILTER, category.getName());
                guests_list_DDM_filters.setText(category.getName(), false);
                DataHandler.getInstance().getGuestslistHandler().updateGuestsList(category.getName());
            }
        });
    }

}
