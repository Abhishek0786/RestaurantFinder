package com.android.hackerearth.restaurantfinder;

import com.android.hackerearth.restaurantfinder.db.Popularity;
import com.android.hackerearth.restaurantfinder.db.Restaurant;

import java.util.ArrayList;

/**
 * Created by abhishek.kum on 8/31/2016.
 */
public class LocationDetails {


    private double latitude;
    private double longitude;
    private String city_name;
    private String title;
    private Popularity popularity;
    private ArrayList<Restaurant> resArrayList = new ArrayList<Restaurant>();
    private Restaurant restaurant;

    public void addRestaurant(Restaurant restaurant) {
        resArrayList.add(restaurant);
    }

    public ArrayList<Restaurant> getResArrayList() {
        return resArrayList;
    }

    public Popularity getPopularity() {
        return popularity;
    }

    public void setPopularity(Popularity popularity) {
        this.popularity = popularity;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    //ArrayList<Restaurant> restaurent_List = new ArrayList<Restaurant>();



}
