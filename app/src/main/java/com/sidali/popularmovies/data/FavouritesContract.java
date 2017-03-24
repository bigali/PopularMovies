package com.sidali.popularmovies.data;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by shallak on 22/03/2017.
 */

public class FavouritesContract {
    public static final String CONTENT_AUTHORITY = "com.sidali.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVOURITE = "favourite";

    public static final class FavouritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVOURITE)
                .build();
        public static final String TABLE_NAME = "favourites";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_POSTER_PATH="moviePosterPath";

        public static Uri buildFavouriteMoviesUri() {
            return CONTENT_URI.buildUpon()
                    .build();
        }

    }
}
