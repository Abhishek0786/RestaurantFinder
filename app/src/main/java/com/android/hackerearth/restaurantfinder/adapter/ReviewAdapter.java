package com.android.hackerearth.restaurantfinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.hackerearth.restaurantfinder.R;
import com.android.hackerearth.restaurantfinder.beans.Reviews;
import com.android.hackerearth.restaurantfinder.beans.User;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ReviewAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Reviews> mReviewsArrayList;

    public ReviewAdapter(Context context, ArrayList<Reviews> list) {
        mContext = context;
        mReviewsArrayList = list;

    }

    @Override
    public int getCount() {
        return mReviewsArrayList.size();
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
            view = LayoutInflater.from(mContext).inflate(R.layout.review_view, null, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Reviews review = mReviewsArrayList.get(i);
        User user = review.getUser();
        String uri = user.getProfile_image();
        Picasso.with(mContext).load(uri).into(holder.mUserThumbnail);

        holder.mReviewName.setText(user.getName());
        holder.mReview.setText(review.getReview_text());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(review.getTimestamp());
        String format = simpleDateFormat.format(calendar.getTime());
        System.out.println("date : " + format);
        holder.mReviewTime.setText(format);

        return view;
    }


    class ViewHolder {

        private ImageView mUserThumbnail;
        private TextView mReviewName;
        private TextView mReviewTime;
        private TextView mReview;

        public ViewHolder(View view) {
            mUserThumbnail = (ImageView) view.findViewById(R.id.reviewrs_photo);
            mReviewName = (TextView) view.findViewById(R.id.reviewrs_name);
            mReviewTime = (TextView) view.findViewById(R.id.review_time);
            mReview = (TextView) view.findViewById(R.id.details);
        }
    }
}
