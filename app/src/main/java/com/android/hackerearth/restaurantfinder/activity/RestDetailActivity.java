package com.android.hackerearth.restaurantfinder.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hackerearth.restaurantfinder.Constants;
import com.android.hackerearth.restaurantfinder.NetworkUtils;
import com.android.hackerearth.restaurantfinder.R;
import com.android.hackerearth.restaurantfinder.adapter.ReviewAdapter;
import com.android.hackerearth.restaurantfinder.beans.Reviews;
import com.android.hackerearth.restaurantfinder.db.Restaurant;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class RestDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = RestDetailActivity.class.getSimpleName();
    private Intent mIntent;
    private Restaurant mRestaurant;
    private ImageView mDetailView;
    private TextView mRestName;
    private TextView mCityName;
    private TextView mRating;
    private TextView mCusinesName;
    private TextView mAverage;
    private TextView mAddress;
    private LinearLayout mParentLayout;
    private Reviews mReview;
    private ArrayList<Reviews> mReviewsList = new ArrayList<Reviews>();
    private boolean isReviewHeaderToggle;
    private ListView mReviewListView;
    private ImageButton mLocationButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurent_detailview);

        mIntent = getIntent();
        mRestaurant = (Restaurant) mIntent.getParcelableExtra(Constants.RESTAURANT_KEY);


        if (mRestaurant == null) {
            finish();
        }

        mDetailView = (ImageView) findViewById(R.id.restaurent_photo);
        mRestName = (TextView) findViewById(R.id.rest_name);
        mCityName = (TextView) findViewById(R.id.city_name);
        mRating = (TextView) findViewById(R.id.rating);
        mCusinesName = (TextView) findViewById(R.id.cuisine_name);
        mAverage = (TextView) findViewById(R.id.average_cost);
        mAddress = (TextView) findViewById(R.id.address);
        mReviewListView = (ListView) findViewById(R.id.review_list);
        mLocationButton = (ImageButton) findViewById(R.id.location);
        mLocationButton.setOnClickListener(this);

        if (NetworkUtils.getInstance(this).isNwkAvailable()) {
            addView();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
        }

        Log.i(TAG, "Object = " + mRestaurant);
    }

    private void addView() {

        String uri = mRestaurant.getThumb();

        Log.i(TAG, "Uri = " + uri + " Name = " + mRestaurant.getName());


        if(uri != null && !uri.isEmpty()) {
            Picasso.with(this).load(uri).resize(getResources().getDisplayMetrics().widthPixels, getResources().getDimensionPixelSize(R.dimen.detail_thumbnail_height)).into(mDetailView);

        }
        mRestName.setText(mRestaurant.getName());
        mCityName.setText(mRestaurant.getLocation().getCity());
        mRating.setText(mRestaurant.getUser_rating().getAggregate_rating());
        mCusinesName.setText(mRestaurant.getCuisines());

        String cost = String.format(getString(R.string.detail_average_cost),
                mRestaurant.getAverage_cost_for_two(), mRestaurant.getPrice_range());
        mAverage.setText(cost);

        mAddress.setText(mRestaurant.getLocation().getAddress());

        Request request = createRequest(Constants.REVIEWS, mRestaurant.getId());
        NetworkUtils.getInstance(this).addRequest(request);
    }

    private Request createRequest(String uri, int id) {

        uri = uri + "?" + "res_id" + "=" + id + "&" +
                "start" + "=" + Constants.START + "&" + "count" + "=" + Constants.COUNT;

        String final_uri = Constants.common_uri + uri;

        Log.i(TAG, "final uri = " + final_uri);

        JsonObjectRequest objectRequest = new JsonObjectRequest(final_uri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.i(TAG, "onResponse " + jsonObject);
                if (jsonObject == null) {
                    Log.i(TAG, "Json object is null");
                    return;
                }
                fetchJsonData(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG, "Error MSG :  " + volleyError.getMessage());
                Toast.makeText(getApplicationContext(), "Network Issue, Please try again", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.ACCEPT, Constants.CONTENT_TYPE);
                params.put(Constants.USER_KEY, Constants.AUTH_KEY);
                return params;
            }
        };

        return objectRequest;
    }

    private void fetchJsonData(JSONObject jsonObject) {
        new DetailTask(getApplicationContext(), jsonObject).execute();

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.review_header: {
                Log.i(TAG, "isShown = " + mReviewListView.isShown());
                if (mReviewListView.isShown()) {
                    mReviewListView.setVisibility(View.GONE);
                } else {
                    mReviewListView.setVisibility(View.VISIBLE);
                }
                break;
            }

            case R.id.location: {
                double lat = Double.parseDouble(mRestaurant.getLocation().getLatitude());
                double lon = Double.parseDouble(mRestaurant.getLocation().getLongitude());
                Uri gmmIntentUri = Uri.parse("geo:" + lat + ", " + lon + "?q= " + mRestaurant.getLocation().getAddress());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        }
    }

    class DetailTask extends AsyncTask<Object, Void, Boolean> {

        private Context mContext;
        private JSONObject jsonObject;


        public DetailTask(Context context, JSONObject jsonObject) {
            mContext = context;
            this.jsonObject = jsonObject;
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            try {

                JSONArray objectArr = jsonObject.getJSONArray(Constants.USER_REVIEWS);

                for (int i = 0; i < objectArr.length(); i++) {
                    JSONObject object = objectArr.getJSONObject(i);
                    object = object.getJSONObject(Constants.REVIEW);
                    Log.i(TAG, "User = " + object);
                    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                    Reviews review = gson.fromJson(object.toString(), Reviews.class);
                    if (review != null) {
                        mReviewsList.add(review);
                    }
                }

                Log.i(TAG, "Review Count :  " + mReviewsList.size());
                //insertIntoDb(mContext, mLocationDetails.getResArrayList());
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (!aBoolean || mReviewsList.size() == 0) {
                Log.i(TAG, " No review List ");
                return;
            }

            onReviewInflate();
            super.onPostExecute(aBoolean);
        }
    }

    private void onReviewInflate() {

        Button button = (Button) findViewById(R.id.review_header);
        button.setText(getResources().getString(R.string.reviews));
        button.setOnClickListener(this);

        isReviewHeaderToggle = true;
        mReviewListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mReviewListView.setVisibility(View.VISIBLE);
        mReviewListView.setAdapter(new ReviewAdapter(getApplicationContext(), mReviewsList));

    }
}
