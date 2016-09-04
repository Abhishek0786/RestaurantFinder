package com.android.hackerearth.restaurantfinder.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.hackerearth.restaurantfinder.Common;
import com.android.hackerearth.restaurantfinder.Constants;
import com.android.hackerearth.restaurantfinder.LocationDetails;
import com.android.hackerearth.restaurantfinder.NetworkUtils;
import com.android.hackerearth.restaurantfinder.R;
import com.android.hackerearth.restaurantfinder.adapter.RestaurantAdapter;
import com.android.hackerearth.restaurantfinder.db.DbHandler;
import com.android.hackerearth.restaurantfinder.db.Popularity;
import com.android.hackerearth.restaurantfinder.db.Restaurant;
import com.android.hackerearth.restaurantfinder.db.TableColumns;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StartActivity extends AppCompatActivity implements LocationListener, AdapterView.OnItemClickListener {

    private static final String TAG = StartActivity.class.getSimpleName();
    private Button mRetryButton;
    private LocationManager mLocationManager;
    private ProgressDialog mDialog;
    private boolean isNwkEnabled;
    private LocationDetails mLocationDetails;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRetryButton = (Button) findViewById(R.id.retry);
        mListView = (ListView) findViewById(R.id.rest_listview);
        mListView.setOnItemClickListener(this);

        mDialog = new ProgressDialog(this);
        mDialog.setTitle(getResources().getString(R.string.please_wait));
        mDialog.setCancelable(false);
        mDialog.show();

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNwkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsEnabled) {
            Common.showAlert(this);
        }

        if (!isNwkEnabled) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
            mRetryButton.setVisibility(View.VISIBLE);
        }

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!NetworkUtils.getInstance(getApplicationContext()).isNwkAvailable()) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
                    boolean isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (!isGpsEnabled) {
                        Common.showAlert(StartActivity.this);
                    }
                    return;
                }

                if (Common.checkPermission(StartActivity.this)) {
                    mDialog.show();
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, StartActivity.this);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_permission), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (Common.checkPermission(this)) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.i(TAG, "Longitude : " + longitude + " Latitude : " + latitude);
            Request request = createRequest(latitude, longitude, Constants.list_rest);
            NetworkUtils.getInstance(this).addRequest(request);

            if (Common.checkPermission(this)) {
                mLocationManager.removeUpdates(this);
            }
        }

    }


    private Request createRequest(double latitude, double longitude, String uri) {

        uri = uri + "?" + "lat" + "=" + latitude + "&" + "lon" + "=" + longitude;
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
                Log.i(TAG, "Json Data " + jsonObject);
                fetchJsonData(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(TAG, "Error MSG :  " + volleyError.getMessage());
                Toast.makeText(getApplicationContext(), "Network Issue, Please try again", Toast.LENGTH_SHORT).show();
                mRetryButton.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
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
        new DbInsertTask(this, jsonObject).execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Common.MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && Common.checkPermission(this)) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_permission), Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

        Restaurant restaurant = mLocationDetails.getResArrayList().get(pos);

        Intent intent = new Intent(getApplicationContext(), RestDetailActivity.class);
        intent.putExtra(Constants.RESTAURANT_KEY, restaurant);
        startActivity(intent);


    }


    class DbInsertTask extends AsyncTask<Object, Void, Boolean> {

        private Context mContext;
        private ArrayList<Restaurant> mList;
        private JSONObject jsonObject;


        public DbInsertTask(Context context, JSONObject jsonObject) {
            mContext = context;
            this.jsonObject = jsonObject;
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            try {
                JSONObject object = jsonObject.getJSONObject(Constants.LOCATION);
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                mLocationDetails = gson.fromJson(object.toString(), LocationDetails.class);
                object = jsonObject.getJSONObject(Constants.POPULARITY);
                Popularity popularity = gson.fromJson(object.toString(), Popularity.class);
                mLocationDetails.setPopularity(popularity);


                jsonObject = jsonObject.getJSONObject(Constants.NEAR_BY_REST);

                Log.i(TAG, "popularityStart :  " + popularity.getNearby_res().size());
                for (int i = 1; i <= popularity.getNearby_res().size(); i++) {
                    object = jsonObject.getJSONObject(String.valueOf(i));
                    object = object.getJSONObject(Constants.RESTAURANT);
                    Restaurant restaurant = gson.fromJson(object.toString(), Restaurant.class);
                    Log.i(TAG, "Rating = " + restaurant.getUser_rating().getAggregate_rating());
                    mLocationDetails.addRestaurant(restaurant);
                }

                insertIntoDb(mContext, mLocationDetails.getResArrayList());
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (!aBoolean) { //add rety button
                mRetryButton.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Unexpected error , please try again", Toast.LENGTH_SHORT).show();
            }

            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }

            if (aBoolean) {
                inflateView(false);
            }

            super.onPostExecute(aBoolean);
        }
    }

    private void inflateView(boolean ispop) {

        Log.i(TAG, "InflateView");

        mRetryButton.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        updateActionBar(mLocationDetails.getCity_name());
        mListView = (ListView) findViewById(R.id.rest_listview);
        RestaurantAdapter adapter = new RestaurantAdapter(getApplicationContext(), mLocationDetails, ispop);
        mListView.setAdapter(adapter);
    }

    private void insertIntoDb(Context mContext, ArrayList<Restaurant> list) {

        DbHandler handler = DbHandler.getInstance(mContext);
        Cursor cursor = handler.query(TableColumns.RESTA_TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null) {
            handler.delete(TableColumns.RESTA_TABLE_NAME);
        }

        for (Restaurant restaurant : list) {
            ContentValues cv = new ContentValues();
            cv.put(TableColumns.RES_ID, restaurant.getId());
            cv.put(TableColumns.NAME, restaurant.getName());
            cv.put(TableColumns.CUISINES, restaurant.getCuisines());
            cv.put(TableColumns.COST_OF_TWO_PEOPLE, restaurant.getAverage_cost_for_two());
            cv.put(TableColumns.PRICE_RANGE, restaurant.getPrice_range());
            cv.put(TableColumns.THUMBNAIL, restaurant.getThumb());

            handler.insert(TableColumns.RESTA_TABLE_NAME, cv);
        }

    }

    private void updateActionBar(String obj) {
        getSupportActionBar().setTitle(obj);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.filter) {
            String title = (String) item.getTitle();

            if (title.equals(getString(R.string.popular))) {
                inflateView(true);
                item.setTitle(getResources().getString(R.string.rating));
            } else {
                inflateView(false);
                item.setTitle(getResources().getString(R.string.popular));
            }

        }
        return super.onOptionsItemSelected(item);
    }
}
