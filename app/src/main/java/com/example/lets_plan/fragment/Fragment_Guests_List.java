package com.example.lets_plan.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lets_plan.R;
import com.example.lets_plan.data.Category;
import com.example.lets_plan.data.Guest;
import com.example.lets_plan.logic.PermissionHandlerSingleton;
import com.example.lets_plan.logic.recyclerview.handler.GuestslistHandler;
import com.example.lets_plan.logic.recyclerview.handler.ItemsHandler;
import com.example.lets_plan.logic.utils.Constants;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.callback.DataReadyInterface;
import com.example.lets_plan.logic.callback.DataClickedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Fragment_Guests_List extends Fragment_Base {
    private AutoCompleteTextView guests_list_DDM_categories;
    private RecyclerView guests_list_RCV_list;
    private Button guests_list_BTN_send_invites;
    private ImageButton guests_list_IBT_add;
    private final ItemsHandler<Guest> itemsHandler;

    public Fragment_Guests_List() {
        this.itemsHandler = new GuestslistHandler();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_guests_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews(view);
        initViews();
        updateDropdown();
    }

    private void findViews(View view) {
        this.guests_list_DDM_categories = view.findViewById(R.id.guests_list_DDM_categories);
        this.guests_list_RCV_list = view.findViewById(R.id.guests_list_RCV_list);
        this.guests_list_BTN_send_invites = view.findViewById(R.id.guests_list_BTN_send_invites);
        this.guests_list_IBT_add = view.findViewById(R.id.guests_list_IBT_add);
    }

    private void initViews() {
        String category = getFromSharedPreferences(Constants.CURRENT_GUEST_CATEGORY, Constants.ALL);
        itemsHandler.setRecyclerView(this.guests_list_RCV_list, Objects.requireNonNull(getActivity()).getApplicationContext(), category);
        itemsHandler.setDataReadyInterface(new DataReadyInterface() {
            @Override
            public void dataReady() {
                updateDropdown();
                DataHandler.getInstance().getRotateLoading().stop();
            }
        });
        itemsHandler.setDataClickedListener(new DataClickedListener<Guest>() {
            @Override
            public void dataClicked(Guest guest) {
                saveToSharedPreferences(Constants.NEW_GUEST, toJson(false, Boolean.class));
                saveToSharedPreferences(Constants.CURRENT_GUEST, toJson(guest, Guest.class));
                switchParentFragment(R.id.main_FGMT_container, new Fragment_Guest(itemsHandler), true);
            }
        });
        this.guests_list_IBT_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToSharedPreferences(Constants.NEW_GUEST, toJson(true, Boolean.class));
                switchParentFragment(R.id.main_FGMT_container, new Fragment_Guest(itemsHandler), true);
            }
        });
        this.guests_list_BTN_send_invites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChargesMayIncurDialog();
            }
        });
    }

    private void openChargesMayIncurDialog() {
        String message = getActivity().getString(R.string.message_additional_charges_may_incur);
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), 0)
                .setMessage(message)
                .setPositiveButton(getActivity().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendInvites();
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(getActivity().getString(R.string.cancel), null)
                .show();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    private void sendInvites() {
        PermissionHandlerSingleton.getInstance().checkSmsPermission();
        DataHandler.getInstance().getAllGuests().forEach(guest -> {
            if (!guest.getPhoneNumber().equals("0525286732")) {
                return;
            }
            try {
                SmsManager smsManager = SmsManager.getDefault();
                String tmpMessage = "Dear " + guest.getFullname() + ",\n"
                        + getActivity().getString(R.string.message_invite)
                        + "\n Total guests: " + guest.getNumberOfGuests()
                        + ",\nDate: " + DataHandler.getInstance().getEventDateAndTime().toString()
                        + ",\nLocation: " + DataHandler.getInstance().getEventHall().getName()
                        + ",\nAddress: " + DataHandler.getInstance().getEventHall().getAddress()
                        + ",\nWaze navigation link: "
                        + "http://waze.to/?ll=" + DataHandler.getInstance().getEventHall().getLocation().latitude + "," + DataHandler.getInstance().getEventHall().getLocation().longitude + "&navigate=yes";
                ArrayList<String> message = smsManager.divideMessage(tmpMessage);
                smsManager.sendMultipartTextMessage(guest.getPhoneNumber(), null, message, null, null);
                Toast.makeText(getActivity().getApplicationContext(), "Invites Sent", Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(getActivity().getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        });
    }


    private void updateDropdown() {
        List<Category> categories = new ArrayList<>(DataHandler.getInstance().getAllCategories());
        categories.remove(new Category(Constants.OTHER_CATEGORY, 0));
        this.guests_list_DDM_categories.setAdapter(
                new ArrayAdapter<>(
                        getActivity().getApplicationContext(),
                        R.layout.dropdown_menu_list_item,
                        categories
                )
        );
        String currentCategory = getFromSharedPreferences(Constants.CURRENT_GUEST_CATEGORY, "");
        this.guests_list_DDM_categories.setText((currentCategory != null ? currentCategory : Constants.ALL), false);
        this.guests_list_DDM_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) parent.getItemAtPosition(position);
                saveToSharedPreferences(Constants.CURRENT_GUEST_CATEGORY, category.getName());
                guests_list_DDM_categories.setText(category.getName(), false);
                itemsHandler.updateList(category.getName());
            }
        });
    }

}
