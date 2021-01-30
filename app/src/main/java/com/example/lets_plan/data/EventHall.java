package com.example.lets_plan.data;

import com.example.lets_plan.logic.utils.Converter;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;
import java.util.Map;

public class EventHall {
    private String placeId;
    private String name;
    private LatLng location;
    private String address;
    private String phoneNumber;
    private Double rating;
    private Integer userRatingsTotal;
    private List<String> openingHours;
    private String websiteUri;

    public EventHall() {
    }

    public EventHall(String placeId, String name, LatLng location) {
        this.placeId = placeId;
        this.name = name;
        this.location = location;
    }

    public String getPlaceId() {
        return placeId;
    }

    public EventHall setPlaceId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    public String getName() {
        return name;
    }

    public EventHall setName(String name) {
        this.name = name;
        return this;
    }

    public LatLng getLocation() {
        return location;
    }

    public EventHall setLocation(Map<String, Object> mapLocation) {
        Location location = Converter.mapToObject(mapLocation, Location.class);
        return setLocation(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    public EventHall setLocation(LatLng location) {
        this.location = location;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public EventHall setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public EventHall setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public Double getRating() {
        return rating;
    }

    public EventHall setRating(Double rating) {
        this.rating = rating;
        return this;
    }

    public Integer getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public EventHall setUserRatingsTotal(Long userRatingsTotal) {
        return setUserRatingsTotal(new Integer(userRatingsTotal.intValue()));
    }

    public EventHall setUserRatingsTotal(Integer userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
        return this;
    }

    public List<String> getOpeningHours() {
        return openingHours;
    }

    public EventHall setOpeningHours(List<String> openingHours) {
        this.openingHours = openingHours;
        return this;
    }

    public String getWebsiteUri() {
        return websiteUri;
    }

    public EventHall setWebsiteUri(String websiteUri) {
        this.websiteUri = websiteUri;
        return this;
    }

    @Override
    public String toString() {
        return "EventHall{" +
                "placeId='" + placeId +'\'' +
                ", name='" + name + '\'' +
                ", location=" + location +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", rating=" + rating +
                ", userRatingsTotal=" + userRatingsTotal +
                ", openingHours=" + openingHours +
                ", websiteUri=" + websiteUri +
                '}';
    }
}
