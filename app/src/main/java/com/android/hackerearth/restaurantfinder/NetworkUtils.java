package com.android.hackerearth.restaurantfinder;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static NetworkUtils mNetworkUtils;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private LruCache<String, Bitmap> mCache;

    private NetworkUtils(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
        mCache=new LruCache<String, Bitmap>(20);

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    @Override
                    public Bitmap getBitmap(String url) {
                        return mCache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        mCache.put(url, bitmap);
                    }
                });
    }

    public static NetworkUtils getInstance(Context context) {

        if (mNetworkUtils == null) {
            mNetworkUtils = new NetworkUtils(context);
        }

        return mNetworkUtils;

    }

    public void addRequest(Request request) {
        mRequestQueue.add(request);
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public LruCache<String, Bitmap> getCache() {
        return mCache;
    }

    public boolean isNwkAvailable() {

        boolean isNwrk = false;
        if (mContext == null) {
            return isNwrk;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null
                && connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null) {
            isNwrk = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnected();

            if (!isNwrk) {
                isNwrk = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                        .isConnected();
            }
        }

        return isNwrk;
    }
}
