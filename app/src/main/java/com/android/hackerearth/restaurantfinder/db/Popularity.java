package com.android.hackerearth.restaurantfinder.db;

import java.util.ArrayList;


public class Popularity {

    ArrayList<Integer> nearby_res = new ArrayList<Integer>();
    ArrayList<String> top_cuisines = new ArrayList<String>();
    double popularity;
    double nightlife_index;

    public ArrayList<Integer> getNearby_res() {
        return nearby_res;
    }

    public void setNearby_res(ArrayList<Integer> nearby_res) {
        this.nearby_res = nearby_res;
    }


    public ArrayList<String> getTop_cuisines() {
        return top_cuisines;
    }

    public void setTop_cuisines(ArrayList<String> top_cuisines) {
        this.top_cuisines = top_cuisines;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getNightlife_index() {
        return nightlife_index;
    }

    public void setNightlife_index(double nightlife_index) {
        this.nightlife_index = nightlife_index;
    }
}
