package com.android.hackerearth.restaurantfinder.beans;

public class User {

    private String name;
    private String foodie_level;
    private String profile_url;
    private String profile_image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoodie_level() {
        return foodie_level;
    }

    public void setFoodie_level(String foodie_level) {
        this.foodie_level = foodie_level;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
