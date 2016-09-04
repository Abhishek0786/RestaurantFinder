package com.android.hackerearth.restaurantfinder.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "zomato.db";
    private String TAG = DbHelper.class.getSimpleName();
    private DbHelper mDbHelper;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createRestaurantTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void createRestaurantTable(SQLiteDatabase db) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ").append(TableColumns.RESTA_TABLE_NAME).append(" (").
                append(TableColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ").
                append(TableColumns.RES_ID).append(" INTEGER, ").
                append(TableColumns.NAME).append(" TEXT, ").
                append(TableColumns.Location_ID).append(" INTEGER, ").
                append(TableColumns.CUISINES).append(" TEXT, ").
                append(TableColumns.COST_OF_TWO_PEOPLE).append(" INTEGER, ").
                append(TableColumns.PRICE_RANGE).append(" INTEGER, ").
                append(TableColumns.THUMBNAIL).append(" TEXT, ").
                append(TableColumns.USER_RATING_ID).append(" INTEGER").
                append(")");

        Log.i(TAG, "Create Query  : " + builder);

        db.execSQL(builder.toString());
    }
}
