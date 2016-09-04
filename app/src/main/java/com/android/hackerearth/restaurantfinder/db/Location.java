package com.android.hackerearth.restaurantfinder.db;

import android.os.Parcel;
import android.os.Parcelable;


public class Location implements Parcelable{

    private String address;
    private String locality;
    private String city;
    private String city_id;
    private String latitude;
    private String longitude;
    private String zipcode;

    protected Location(Parcel in) {
        address = in.readString();
        locality = in.readString();
        city = in.readString();
        city_id = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        zipcode = in.readString();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(address);
        parcel.writeString(locality);
        parcel.writeString(city);
        parcel.writeString(city_id);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(zipcode);
    }
}
