package com.android.hackerearth.restaurantfinder.db;

import android.os.Parcel;
import android.os.Parcelable;

public class UserRating implements Parcelable{

    private String aggregate_rating;
    private String rating_text;
    private String rating_color;
    private String votes;

    protected UserRating(Parcel in) {
        aggregate_rating = in.readString();
        rating_text = in.readString();
        rating_color = in.readString();
        votes = in.readString();
    }

    public static final Creator<UserRating> CREATOR = new Creator<UserRating>() {
        @Override
        public UserRating createFromParcel(Parcel in) {
            return new UserRating(in);
        }

        @Override
        public UserRating[] newArray(int size) {
            return new UserRating[size];
        }
    };

    public String getAggregate_rating() {
        return aggregate_rating;
    }

    public void setAggregate_rating(String aggregate_rating) {
        this.aggregate_rating = aggregate_rating;
    }

    public String getRating_text() {
        return rating_text;
    }

    public void setRating_text(String rating_text) {
        this.rating_text = rating_text;
    }

    public String getRating_color() {
        return rating_color;
    }

    public void setRating_color(String rating_color) {
        this.rating_color = rating_color;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(aggregate_rating);
        parcel.writeString(rating_text);
        parcel.writeString(rating_color);
        parcel.writeString(votes);
    }
}
