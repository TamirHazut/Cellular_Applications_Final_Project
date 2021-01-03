package com.example.lets_plan.logic;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lets_plan.logic.recyclerview.handler.GuestslistHandler;
import com.example.lets_plan.logic.recyclerview.handler.TablesArrangementHandler;
import com.victor.loading.rotate.RotateLoading;


public class DataHandler {
    private static DataHandler instance;
    private Context context;
    private TablesArrangementHandler tablesArrangementHandler;
    private GuestslistHandler guestslistHandler;
    private String ownerID;
    private RotateLoading rotateLoading;

    private DataHandler(AppCompatActivity context) {
        this.context = context;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public Context getContext() {
        return context;
    }

    public void initTablesArrangementHandler() {
        if (this.tablesArrangementHandler == null) {
            this.tablesArrangementHandler = new TablesArrangementHandler();
        }
    }

    public TablesArrangementHandler getTablesArrangementHandler() {
        return tablesArrangementHandler;
    }

    public void initGuestslistHandler() {
        if (this.guestslistHandler == null) {
            this.guestslistHandler = new GuestslistHandler();
        }
    }

    public void setRotateLoading(RotateLoading rotateLoading) {
        this.rotateLoading = rotateLoading;
    }

    public RotateLoading getRotateLoading() {
        return rotateLoading;
    }

    public GuestslistHandler getGuestslistHandler() {
        return guestslistHandler;
    }

    public static void init(AppCompatActivity activity) {
        if (instance == null) {
            instance = new DataHandler(activity);
        }
    }

    public <T> boolean isDataModified(T o1, T o2) {
        return !o1.equals(o2);
    }

    public static DataHandler getInstance() {
        return instance;
    }
}
