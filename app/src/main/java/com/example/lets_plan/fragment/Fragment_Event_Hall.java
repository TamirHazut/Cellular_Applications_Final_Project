package com.example.lets_plan.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;

import com.example.lets_plan.R;
import com.example.lets_plan.data.DateAndTime;
import com.example.lets_plan.data.EventHall;
import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.utils.Constants;

import java.util.Calendar;
import java.util.Locale;


public class Fragment_Event_Hall extends Fragment_Base {
    private TextView event_hall_LBL_name;
    private TextView event_hall_LBL_address;
    private TextView event_hall_LBL_phoneNumber;
    private AppCompatRatingBar event_hall_WGT_rating;
    private TextView event_hall_LBL_rating;
    private TextView event_hall_LBL_user_rating_total;
    private ListView event_hall_LSTV_opening_time;
    private TextView event_hall_LNK_website;
    private Button event_hall_BTN_select;
    private Button event_hall_BTN_close;

    public Fragment_Event_Hall() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_event_hall, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventHall eventHall = fromJson(getFromSharedPreferences(Constants.CURRENT_EVENT_HALL, ""), EventHall.class);
        findViews(view);
        initViews(eventHall);
    }

    private void findViews(View view) {
        this.event_hall_LBL_name = view.findViewById(R.id.event_hall_LBL_name);
        this.event_hall_LBL_address = view.findViewById(R.id.event_hall_LBL_address);
        this.event_hall_LBL_phoneNumber = view.findViewById(R.id.event_hall_LBL_phoneNumber);
        this.event_hall_WGT_rating = view.findViewById(R.id.event_hall_WGT_rating);
        this.event_hall_LBL_rating = view.findViewById(R.id.event_hall_LBL_rating);
        this.event_hall_LBL_user_rating_total = view.findViewById(R.id.event_hall_LBL_user_rating_total);
        this.event_hall_LSTV_opening_time = view.findViewById(R.id.event_hall_LSTV_opening_time);
        this.event_hall_LNK_website = view.findViewById(R.id.event_hall_LNK_website);
        this.event_hall_BTN_select = view.findViewById(R.id.event_hall_BTN_select);
        this.event_hall_BTN_close = view.findViewById(R.id.event_hall_BTN_close);
    }

    private void initViews(EventHall eventHall) {
        this.event_hall_LBL_name.setText(eventHall.getName());
        this.event_hall_LBL_address.setText(eventHall.getAddress());
        this.event_hall_LBL_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", eventHall.getLocation().latitude, eventHall.getLocation().longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                getActivity().startActivity(intent);
            }
        });
        this.event_hall_LBL_phoneNumber.setText(eventHall.getPhoneNumber());
        this.event_hall_LBL_phoneNumber.setPaintFlags(0);

        this.event_hall_WGT_rating.setRating(eventHall.getRating().floatValue());
        this.event_hall_LBL_rating.setText(String.format(Locale.getDefault(), "%.1f", eventHall.getRating()));
        this.event_hall_LBL_user_rating_total.setText(String.format(Locale.getDefault(), "(%d)", eventHall.getUserRatingsTotal()));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.event_hall_listview_item, eventHall.getOpeningHours());
        this.event_hall_LSTV_opening_time.setAdapter(adapter);

        this.event_hall_LNK_website.setText(eventHall.getWebsiteUri());
        this.event_hall_LNK_website.setMovementMethod(LinkMovementMethod.getInstance());

        this.event_hall_BTN_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDateAndTime();
                DataHandler.getInstance().saveEventHall(eventHall);
            }
        });

        this.event_hall_BTN_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });
    }

    private DateAndTime getDateAndTime() {
        DateAndTime eventDateAndTime = new DateAndTime();
        Calendar calendar = Calendar.getInstance();
        eventDateAndTime.setYear(calendar.get(Calendar.YEAR))
                    .setMonth(calendar.get(Calendar.MONTH))
                    .setDay(calendar.get(Calendar.DATE));
        DatePickerDialog datePickerDialog = getDatePickerDialog(eventDateAndTime);
        datePickerDialog.show();
        return eventDateAndTime;
    }

    private DatePickerDialog getDatePickerDialog(DateAndTime eventDateAndTime) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                eventDateAndTime.setYear(year)
                        .setMonth(month+1)
                        .setDay(dayOfMonth)
                        .setHour(calendar.get(Calendar.HOUR))
                        .setMinute(calendar.get(Calendar.MINUTE));
                TimePickerDialog timePickerDialog = getTimePickerDialog(eventDateAndTime);
                timePickerDialog.show();
            }
        }, eventDateAndTime.getYear(), eventDateAndTime.getMonth(), eventDateAndTime.getDay());
        datePickerDialog.setButton(android.content.DialogInterface.BUTTON_NEGATIVE, "", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        datePickerDialog.setCanceledOnTouchOutside(false);
        return datePickerDialog;
    }

    private TimePickerDialog getTimePickerDialog(DateAndTime eventDateAndTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                eventDateAndTime.setHour(hourOfDay)
                        .setMinute(minute);
                DataHandler.getInstance().saveEventDateAndTime(eventDateAndTime);
                closeFragment();
                Log.d("EventHall", "#2 DateAndTime: " + DataHandler.getInstance().getEventDateAndTime());
            }
        }, eventDateAndTime.getHour(), eventDateAndTime.getMinute(), DateFormat.is24HourFormat(getActivity()));
        timePickerDialog.setButton(android.content.DialogInterface.BUTTON_NEGATIVE, "", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                   }
               });
        timePickerDialog.setCanceledOnTouchOutside(false);
        return timePickerDialog;
    }

}
