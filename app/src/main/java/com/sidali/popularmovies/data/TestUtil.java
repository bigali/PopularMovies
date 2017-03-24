package com.sidali.popularmovies.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static void insertFakeData(SQLiteDatabase db){
        if(db == null){
            return;
        }
        List<ContentValues> list = new ArrayList<ContentValues>();

        ContentValues cv = new ContentValues();
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID, 376867);
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_TITLE, "Moonlight");
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_POSTER_PATH,"/45Y1G5FEgttPAwjTYic6czC9xCn.jpg");
        list.add(cv);

        cv = new ContentValues();
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID, 369885);
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_TITLE, "Allied");
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_POSTER_PATH,"/45Y1G5FEgttPAwjTYic6czC9xCn.jpg");
        list.add(cv);

        cv = new ContentValues();
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID, 671);
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_TITLE, "Harry Potter and the Philosopher's Stone");
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_POSTER_PATH,"/45Y1G5FEgttPAwjTYic6czC9xCn.jpg");
        list.add(cv);

        cv = new ContentValues();
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID, 209112);
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_TITLE, "Batman v Superman: Dawn of Justice");
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_POSTER_PATH,"/45Y1G5FEgttPAwjTYic6czC9xCn.jpg");

        list.add(cv);

        cv = new ContentValues();
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID, 281957);
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_TITLE, "The Revenant");
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_POSTER_PATH,"/45Y1G5FEgttPAwjTYic6czC9xCn.jpg");

        list.add(cv);

        try
        {
            db.beginTransaction();
            //clear the table first
            db.delete (FavouritesContract.FavouritesEntry.TABLE_NAME,null,null);
            //go through the list and add one by one
            for(ContentValues c:list){
                db.insert(FavouritesContract.FavouritesEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
            //too bad :(
        }
        finally
        {
            db.endTransaction();
        }

    }
}