package com.example.autoshowroom.service;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.example.autoshowroom.service.CarDatabase;

public class CarContentProvider extends ContentProvider {

    private CarDatabase db;
    public static final String CONTENT_AUTHORITY = "fit2081.app.KEE";
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public CarContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deletionCount;

        deletionCount = db.getOpenHelper()
                            .getWritableDatabase()
                            .delete(Car.CAR_TABLE, selection, selectionArgs);

        return deletionCount;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId = db.getOpenHelper()
                        .getWritableDatabase()
                        .insert(Car.CAR_TABLE, 0, values);

        return ContentUris.withAppendedId(CONTENT_URI, rowId);
    }

    @Override
    public boolean onCreate() {
        db = CarDatabase.getDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Car.CAR_TABLE);
        String query = builder.buildQuery(projection, selection, null, null, sortOrder, null);

        final Cursor cursor = db.getOpenHelper()
                                .getReadableDatabase()
                                .query(query, selectionArgs);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int updateCount;

        updateCount = db.getOpenHelper()
                        .getWritableDatabase()
                        .update(Car.CAR_TABLE, 0, values, selection, selectionArgs);

        return updateCount;
    }
}