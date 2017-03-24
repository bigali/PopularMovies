package com.sidali.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.sidali.popularmovies.data.FavouritesContract.*;

/**
 * Created by shallak on 22/03/2017.
 */

public class FavouritesDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "favourites.db";


    private static final int DATABASE_VERSION = 2;
    public FavouritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVOURITES_TABLE = "CREATE TABLE " + FavouritesEntry.TABLE_NAME + " (" +
                FavouritesEntry.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY," +
                FavouritesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavouritesEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL" +
                "); ";

        db.execSQL(SQL_CREATE_FAVOURITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
