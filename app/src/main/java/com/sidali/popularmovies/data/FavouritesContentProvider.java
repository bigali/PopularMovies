package com.sidali.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.media.tv.TvContract.Programs.Genres.MOVIES;
import static com.sidali.popularmovies.data.FavouritesContract.FavouritesEntry.TABLE_NAME;

/**
 * Created by shallak on 24/03/2017.
 */

public class FavouritesContentProvider extends ContentProvider {


    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavouritesDbHelper dbHelper;


    public static final int FAVOUTITES = 100;
    public static final int FAVOURITES_WITH_ID = 101;

    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavouritesContract.CONTENT_AUTHORITY, FavouritesContract.PATH_FAVOURITE, FAVOUTITES);
        uriMatcher.addURI(FavouritesContract.CONTENT_AUTHORITY, FavouritesContract.PATH_FAVOURITE + "/#", FAVOURITES_WITH_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new FavouritesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case FAVOUTITES:
                retCursor =  db.query(TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
// Get access to the task database (to write new data to)
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the tasks directory
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case FAVOUTITES:
                // Insert new values into the database
                // Inserting values into tasks table
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(FavouritesContract.FavouritesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int favouriteDeleted;

        switch (match) {
            case FAVOUTITES:
                favouriteDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case FAVOURITES_WITH_ID:
                favouriteDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (favouriteDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of tasks deleted
        return favouriteDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
