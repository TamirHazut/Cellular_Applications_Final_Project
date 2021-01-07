package com.example.lets_plan.data;

import android.util.Log;

import com.example.lets_plan.logic.DataHandler;
import com.example.lets_plan.logic.utils.Converter;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Table implements Comparable<Table> {
    private String name;
    private Long maxCapacity;
    private String category;
    private List<String> guests;

    public Table() {
        this.guests = new ArrayList<>();
        this.maxCapacity = new Long(0);
    }

    public Table(Table other) {
        this.name = other.getName();
        this.maxCapacity = other.getMaxCapacity();
        this.category = other.getCategory();
        this.guests = new ArrayList<>(other.getGuests());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMaxCapacity() { return maxCapacity; }

    public void setMaxCapacity(Long maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getGuests() {
        return guests;
    }

    public void setGuests(List<String> guests) {
        this.guests = guests;
    }

    @Override
    public int compareTo(Table o) {
        return this.name.compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(name, table.name) &&
                Objects.equals(category, table.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category);
    }

    public void copyData(Table table) {
        this.setName(table.getName());
        this.setMaxCapacity(table.getMaxCapacity());
        this.setCategory(table.getCategory());
        this.setGuests(table.getGuests());
    }

    public static long sumGuests(Table table) {
        final long[] sum = {0};
        List<Guest> allGuests = DataHandler.getInstance().getAllGuests();
        if (allGuests != null && !allGuests.isEmpty()) {
            table.getGuests().stream().forEach(phone -> {
                Guest guest = DataHandler.getInstance().findGuestByPhone(phone);
                if (guest != null && guest.getTable() != null && guest.getTable().equals(table.getName())) {
                    sum[0] += guest.getNumberOfGuests().longValue();
                }
            });

        }
        return sum[0];
    }
}
