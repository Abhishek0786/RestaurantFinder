package com.android.hackerearth.restaurantfinder.db;

import android.os.Parcel;
import android.os.Parcelable;

public class Restaurant implements Parcelable, Comparable<Restaurant> {

    private String name;
    private Res R;
    private int id;
    private Location location;
    private String cuisines;
    private long average_cost_for_two;
    private long price_range;
    private String thumb;
    private UserRating user_rating;

    protected Restaurant(Parcel in) {
        name = in.readString();
        id = in.readInt();
        cuisines = in.readString();
        average_cost_for_two = in.readLong();
        price_range = in.readLong();
        thumb = in.readString();
        location = (Location) in.readValue(Location.class.getClassLoader());
        user_rating = (UserRating) in.readValue(UserRating.class.getClassLoader());
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public Res getR() {
        return R;
    }

    public void setR(Res r) {
        R = r;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCuisines() {
        return cuisines;
    }

    public void setCuisines(String cuisines) {
        this.cuisines = cuisines;
    }

    public long getAverage_cost_for_two() {
        return average_cost_for_two;
    }

    public void setAverage_cost_for_two(long average_cost_for_two) {
        this.average_cost_for_two = average_cost_for_two;
    }

    public long getPrice_range() {
        return price_range;
    }

    public void setPrice_range(long price_range) {
        this.price_range = price_range;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public UserRating getUser_rating() {
        return user_rating;
    }

    public void setUser_rating(UserRating user_rating) {
        this.user_rating = user_rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(id);
        parcel.writeString(cuisines);
        parcel.writeLong(average_cost_for_two);
        parcel.writeLong(price_range);
        parcel.writeString(thumb);
        parcel.writeValue(location);
        parcel.writeValue(user_rating);
    }

    @Override
    public int compareTo(Restaurant restaurant) {
        double rating_1 = Double.parseDouble(this.getUser_rating().getAggregate_rating());
        double rating_2 = Double.parseDouble(restaurant.getUser_rating().getAggregate_rating());

        if (rating_1 == rating_2)
            return 0;
        else if (rating_1 > rating_2)
            return -1;
        else
            return 1;
    }

}
