package com.example.lets_plan.data;

import java.util.Objects;

public class Guest implements Comparable<Guest> {
    private String fullname;
    private String phoneNumber;
    private Long numberOfGuests;
    private String category;
    private String table;

    public Guest() {
        this.numberOfGuests = new Long(0);
    }

    public Guest(String fullname, String phoneNumber, Long numberOfGuests, String category, String table) {
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
        this.numberOfGuests = numberOfGuests;
        this.category = category;
        this.table = table;
    }

    public Guest(Guest guest) {
        this(guest.getFullname(), guest.getPhoneNumber(), guest.getNumberOfGuests(), guest.getCategory(), guest.getTable());
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getNumberOfGuests() { return numberOfGuests; }

    public void setNumberOfGuests(Long numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTable() { return table; }

    public void setTable(String table) { this.table = table; }

    @Override
    public int compareTo(Guest o) {
        return this.getPhoneNumber().compareTo(o.getPhoneNumber());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guest guest = (Guest) o;
        return Objects.equals(phoneNumber, guest.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber);
    }

    @Override
    public String toString() {
        return this.fullname + " (" + this.numberOfGuests + ")";
    }

    public void copyData(Guest guest) {
        this.setFullname(guest.getFullname());
        this.setPhoneNumber(guest.getPhoneNumber());
        this.setNumberOfGuests(guest.getNumberOfGuests());
        this.setCategory(guest.getCategory());
        this.setTable(guest.getTable());
    }
}
