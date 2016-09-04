package com.android.hackerearth.restaurantfinder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DbHandler {

    private static DbHandler mDbHandler;
    private DbHelper mHelper;
    private Context mContext;

    public static DbHandler getInstance(Context context) {

        if (mDbHandler == null) {
            mDbHandler = new DbHandler(context);
        }

        return mDbHandler;
    }

    private DbHandler(Context context) {
        mContext = context;
        mHelper = new DbHelper(context);
    }

    public void insert(String table, ContentValues cv) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        database.insert(table, null, cv);
    }

    public Cursor query(String table,
                        String[] columns,
                        String selection,
                        String[] selectionArgs,
                        String groupBy,
                        String having,
                        String orderBy) {
        SQLiteDatabase database = mHelper.getReadableDatabase();
        return database.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public void delete(String table) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        database.delete(table, null, null);
    }
}
