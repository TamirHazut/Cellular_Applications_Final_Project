package com.example.lets_plan.data;

public class Guest implements Comparable<Guest> {
    private String fullname;
    private String phoneNumber;
    private Long numberOfGuests;
    private String category;

    public Guest() {
    }

    public Guest(String fullname, String phoneNumber, Long numberOfGuests, String category) {
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
        this.numberOfGuests = numberOfGuests;
        this.category = category;
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

    public Long getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Long numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    @Override
    public int compareTo(Guest o) {
        return this.getPhoneNumber().compareTo(o.getPhoneNumber());
    }
}
