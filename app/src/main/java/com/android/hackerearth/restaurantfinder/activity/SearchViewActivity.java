
package com.android.hackerearth.restaurantfinder.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.android.hackerearth.restaurantfinder.db.Restaurant;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SearchViewActivity extends AppCompatActivity implements LocationListener, AdapterView.OnItemClickListener {

    private static final String TAG = SearchViewActivity.class.getSimpleName();
    private String searchQuery;
    private LocationManager mLocationManager;
    private ProgressDialog mDialog;
    private boolean isNwkEnabled;
    private LocationDetails mLocationDetails;
    private ListView mListView;
    private Button mRetryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Intent intent = getIntent();

        mLocationDetails = new LocationDetails();

        mRetryButton = (Button) findViewById(R.id.retry);
        mListView = (ListView) findViewById(R.id.rest_listview);
        mListView.setOnItemClickListener(this);


        mDialog = new ProgressDialog(this);
        mDialog.setIndeterminate(true);
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
                        Common.showAlert(SearchViewActivity.this);
                    }
                    return;
                }

                if (Common.checkPermission(SearchViewActivity.this)) {
                    mDialog.show();
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, SearchViewActivity.this);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_permission), Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (Common.checkPermission(this)) {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }

        if (intent != null) {
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                String query = intent.getStringExtra(SearchManager.QUERY);
                searchQuery = query;
                query = query.toLowerCase();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.i(TAG, "Longitude : " + longitude + " Latitude : " + latitude);
            Request request = createRequest(latitude, longitude, Constants.SEARCH);
            NetworkUtils.getInstance(this).addRequest(request);

            if (Common.checkPermission(this)) {
                mLocationManager.removeUpdates(this);
            }
        }
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

    private Request createRequest(double latitude, double longitude, String uri) {

        if (searchQuery != null) {
            uri = uri + "?" + "q=" + searchQuery + "&" + "lat" + "=" + latitude + "&" + "lon" + "=" + longitude;
        } else {
            uri = uri + "?" + "lat" + "=" + latitude + "&" + "lon" + "=" + longitude;
        }

        final String final_uri = Constants.common_uri + uri;

        Log.i(TAG, "final uri = " + final_uri);

        JsonObjectRequest objectRequest = new JsonObjectRequest(final_uri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                Log.i(TAG, "onResponse " + jsonObject);
                if (jsonObject == null) {
                    Log.i(TAG, "Json object is null");
                    return;
                }

                try {
                    int found = jsonObject.getInt(Constants.RESULT_FOUND);
                    if (found == 0) {
                        Toast.makeText(getApplicationContext(), "No data found. Try with different words", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Json Data " + jsonObject);
                new FetchJsonData(getApplicationContext(), jsonObject).execute();
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
                finish();
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        Restaurant restaurant = mLocationDetails.getResArrayList().get(pos);

        Intent intent = new Intent(getApplicationContext(), RestDetailActivity.class);
        intent.putExtra(Constants.RESTAURANT_KEY, restaurant);
        startActivity(intent);
    }

    class FetchJsonData extends AsyncTask<Object, Void, Boolean> {

        private Context mContext;
        private ArrayList<Restaurant> mList;
        private JSONObject jsonObject;


        public FetchJsonData(Context context, JSONObject jsonObject) {
            mContext = context;
            this.jsonObject = jsonObject;
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            try {


                JSONArray array = jsonObject.getJSONArray(Constants.RESTAURANTS);

                if (array == null) {
                    Log.i(TAG, "Json array is null");
                    return false;
                }
                Log.i(TAG, "JsonObject :  " + jsonObject);


                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    object = object.getJSONObject(Constants.RESTAURANT);
                    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                    Restaurant restaurant = gson.fromJson(object.toString(), Restaurant.class);
                    Log.i(TAG, "Rating = " + restaurant.getThumb());
                    mLocationDetails.addRestaurant(restaurant);
                }

                Log.i(TAG, "popularityStart :  " + array.length());

                //insertIntoDb(mContext, mLocationDetails.getResArrayList());
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
                finish();
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
        mListView = (ListView) findViewById(R.id.rest_listview);
        RestaurantAdapter adapter = new RestaurantAdapter(getApplicationContext(), mLocationDetails, ispop);
        mListView.setAdapter(adapter);
    }

}
