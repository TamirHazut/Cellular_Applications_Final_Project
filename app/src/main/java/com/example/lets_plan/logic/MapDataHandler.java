package com.example.lets_plan.logic;

import android.util.Log;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lets_plan.data.EventHall;
import com.example.lets_plan.logic.callback.DataClickedListener;
import com.example.lets_plan.logic.utils.Constants;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapDataHandler {
    private static MapDataHandler instance;
    private GoogleMap mGoogleMap;
    private PlacesClient placesClient;
    private final BiMap<String, Marker> markers;
    private final Map<String, EventHall> eventHalls;
    private String apiKey;
    private boolean currentLocationReady;
    private boolean mapReady;
    private boolean eventHallMethodCalled;
    private DataClickedListener dataClickedListener;

    public MapDataHandler() {
        this.markers = HashBiMap.create();
        this.eventHalls = new HashMap<>();
    }

    public static MapDataHandler getInstance() {
        if (instance == null) {
            instance = new MapDataHandler();
        }
        return instance;
    }

    public void setCurrentLocationReady(boolean currentLocationReady) {
        this.currentLocationReady = currentLocationReady;
    }

    public void setDataClickedListener(DataClickedListener dataClickedListener) {
        this.dataClickedListener = dataClickedListener;
    }

    public void setSettingsAfterMapAndDataAreReady() {
        if (this.currentLocationReady && this.mapReady) {
            this.mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(PermissionHandlerSingleton.getInstance().getCurrentLocation(), Constants.MAP_ZOOM_LEVEL));
            getAllEventHalls();
        }
    }

    public void setAPIKey(String apiKey) {
        this.apiKey = (apiKey != null ? apiKey : "");
        if (!this.apiKey.isEmpty()) {
            initializePlaces();
        }
    }

    private void initializePlaces () {
        Places.initialize(DataHandler.getInstance().getContext(), this.apiKey);
        placesClient = Places.createClient(DataHandler.getInstance().getContext());
    }

    public void mapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        this.mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        this.mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String placeId = markers.inverse().get(marker);
                if (placeId == null || placeId.isEmpty() || !eventHalls.containsKey(placeId)) {
                    return false;
                }
                final List<Place.Field> placeFields = Arrays.asList(Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.USER_RATINGS_TOTAL, Place.Field.OPENING_HOURS, Place.Field.WEBSITE_URI);
                final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place place = response.getPlace();
                    dataClickedListener.dataClicked(eventHalls.get(placeId)
                            .setAddress(place.getAddress())
                            .setPhoneNumber(place.getPhoneNumber())
                            .setRating(place.getRating())
                            .setUserRatingsTotal(place.getUserRatingsTotal())
                            .setOpeningHours(place.getOpeningHours().getWeekdayText())
                            .setWebsiteUri(place.getWebsiteUri().toString()));
                }).addOnFailureListener((e) -> {
                    if (e instanceof ApiException) {
                        final ApiException apiException = (ApiException) e;
                        Log.e("fetchPlace", "Place not found: " + e.getMessage());
                    }
                });
                return true;
            }
        });
        this.mapReady = true;
        setSettingsAfterMapAndDataAreReady();
    }

    private void getAllEventHalls() {
        if (this.eventHallMethodCalled) {
            return;
        }
        this.eventHallMethodCalled = true;
        String currentLocation = PermissionHandlerSingleton.getInstance().getCurrentLocation().latitude + "," + PermissionHandlerSingleton.getInstance().getCurrentLocation().longitude;
        final String API_LINK =  "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + Constants.SEARCH_QUERY + "&location=" + currentLocation + "&radius=" + Constants.SEARCH_RADIUS_IN_METERS + "&key=" + apiKey;
        Log.d("EventHall", API_LINK);
        RequestQueue requestQueue = Volley.newRequestQueue(DataHandler.getInstance().getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_LINK, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String placeId = jsonObject.getString("place_id");
                        String name = jsonObject.getString("name");
                        JSONObject locationJSON = jsonObject.getJSONObject("geometry").getJSONObject("location");
                        LatLng location = new LatLng(Double.parseDouble(locationJSON.getString("lat")), Double.parseDouble(locationJSON.getString("lng")));
                        eventHalls.put(placeId, new EventHall(placeId, name, location));
                        markers.put(placeId, mGoogleMap.addMarker(new MarkerOptions().position(location).title(name)));
                    }
                } catch (JSONException | NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}
