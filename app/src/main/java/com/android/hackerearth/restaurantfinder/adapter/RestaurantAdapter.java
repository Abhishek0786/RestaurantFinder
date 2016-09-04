package com.android.hackerearth.restaurantfinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.hackerearth.restaurantfinder.db.Location;
import com.android.hackerearth.restaurantfinder.LocationDetails;
import com.android.hackerearth.restaurantfinder.R;
import com.android.hackerearth.restaurantfinder.db.Restaurant;
import com.android.hackerearth.restaurantfinder.db.UserRating;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;



public class RestaurantAdapter extends BaseAdapter {

    private LocationDetails mDetails;
    private Context mContext;
    private ArrayList<Restaurant> mList;
    private boolean isPopularity;

    public RestaurantAdapter(Context context, LocationDetails details, boolean isPop) {
        mContext = context;
        mDetails = details;
        isPopularity = isPop;
        if (!isPopularity) {
            mList = details.getResArrayList();
            Collections.sort(mList);
        } else {
            createPopList();
        }
    }

    private void createPopList() {

        HashMap<Integer, Restaurant> map = new HashMap<Integer, Restaurant>();
        for (Restaurant temp : mDetails.getResArrayList()) {
            map.put(temp.getId(), temp);
        }

        mList = new ArrayList<Restaurant>();
        for (Integer val : mDetails.getPopularity().getNearby_res()) {
            mList.add(map.get(val));
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.restaurant_view, null, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //picture set pending
        Restaurant restaurant = mList.get(i);

        String uri = restaurant.getThumb();
        if (uri != null && !uri.isEmpty()) {
            Picasso.with(mContext).load(uri).
                    resize(mContext.getResources().getDimensionPixelSize(R.dimen.thumbnail_width), mContext.getResources().getDimensionPixelSize(R.dimen.thumbnail_height))
                    .into(holder.mThumbnail);
        }

        holder.mRestName.setText(restaurant.getName());
        holder.mCuisineName.setText(restaurant.getCuisines());
        UserRating rating = restaurant.getUser_rating();
        holder.mRating.setText(rating.getAggregate_rating());

        Location location = restaurant.getLocation();
        holder.mAreaName.setText(location.getLocality());


        String cost = String.format(mContext.getString(R.string.average_cost),
                restaurant.getAverage_cost_for_two(), restaurant.getPrice_range());
        holder.mCost.setText(cost);

        return view;
    }

    class ViewHolder {

        private ImageView mThumbnail;
        private TextView mRestName;
        private TextView mAreaName;
        private TextView mRating;
        private TextView mCuisineName;
        private TextView mCost;

        public ViewHolder(View view) {
            mThumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            mRestName = (TextView) view.findViewById(R.id.rest_name);
            mAreaName = (TextView) view.findViewById(R.id.area_name);
            mRating = (TextView) view.findViewById(R.id.rating);
            mCuisineName = (TextView) view.findViewById(R.id.cuisine);
            mCost = (TextView) view.findViewById(R.id.cost);
        }
    }
}
