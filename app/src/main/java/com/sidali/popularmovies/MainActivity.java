package com.sidali.popularmovies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.stetho.common.StringUtil;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.sidali.popularmovies.adapters.MovieAdapter;
import com.sidali.popularmovies.api.MoviesApi;
import com.sidali.popularmovies.data.FavouritesContract;
import com.sidali.popularmovies.data.FavouritesDbHelper;
import com.sidali.popularmovies.data.TestUtil;
import com.sidali.popularmovies.model.Movie;
import com.sidali.popularmovies.model.MovieDetail;
import com.sidali.popularmovies.model.MoviesList;
import com.sidali.popularmovies.utils.EndlessRecyclerViewScrollListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.data;
import static android.R.id.list;
import static android.R.id.switch_widget;

public class MainActivity extends AppCompatActivity implements Callback<MoviesList>, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String POPULAR = "0";
    public static final String MOST_RATED = "1";
    public static final String FAVOURITE = "2";
    public static final String LIST_STATE_KEY = "position";
    public static final String ORDER_STATE_KEY = "position";
    private static final int LOADER_ID = 0x01;


    @BindView(R.id.lv_popular_movies)
    RecyclerView recyclerView;

    RecyclerView.LayoutManager mLayoutManager;
    private List<Movie> movies = new ArrayList<>();
    private MovieAdapter movieAdapter;
    private SQLiteDatabase mDb;
    ProgressDialog mProgressDialog;
    String order = POPULAR;
    SharedPreferences sharedPref;
    private EndlessRecyclerViewScrollListener scrollListener;


    FavouritesDbHelper dbHelper;
    OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .client(client)
            .baseUrl("http://api.themoviedb.org/3/movie/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    MoviesApi moviesApi = retrofit.create(MoviesApi.class);

    Parcelable mListState;
    String mOrderState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getApplicationContext(), calculateNoOfColumns(this));
        recyclerView.setLayoutManager(mLayoutManager);
        movieAdapter = new MovieAdapter(MainActivity.this, movies);
        recyclerView.setAdapter(movieAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page + 1);
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");

        dbHelper = new FavouritesDbHelper(this);
        mDb = dbHelper.getWritableDatabase();


        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        order = sharedPref.getString("order_preference", "");
        callApi(order);


    }

    private void loadNextDataFromApi(int page) {
        if (order.equals(POPULAR)) {
            Call<MoviesList> call = moviesApi.loadMorePopularMovies(page);
            call.enqueue(this);
        } else if (order.equals(MOST_RATED)) {
            Call<MoviesList> call = moviesApi.loadMoreTopRatedMovies(page);
            call.enqueue(this);
        }
    }


    void callApi(String v) {
        switch (v) {
            case POPULAR:
                callPopularMovies();
                break;
            case MOST_RATED:
                callTopRatedMovies();
                break;
            case FAVOURITE:
                getFavourites();
                break;
        }

    }

    private void getFavourites() {

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        String v = order;
        if(mOrderState!=null){
            v=mOrderState;

        }

        if (!order.equals(v)) {
            callApi(v);
            order = v;
        }
        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    public void callPopularMovies() {
        Call<MoviesList> call = moviesApi.getPopularMovies();
        call.enqueue(this);
        mProgressDialog.show();

    }

    private void callTopRatedMovies() {
        Call<MoviesList> call = moviesApi.getTopRatedMovies();
        call.enqueue(this);
        mProgressDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSortFavourites:
                order = FAVOURITE;
                callApi(order);
                break;
            case R.id.menuSortNewest:
                order = POPULAR;
                callApi(order);
                break;
            case R.id.menuSortRating:
                order = MOST_RATED;
                callApi(order);
                break;

        }

        return true;
    }


    @Override
    public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
        if (response.isSuccessful()) {
            if (response.body().page == 1) {
                movies.clear();
            }
            movies.addAll(response.body().results);

            movieAdapter.notifyDataSetChanged();

            if (mListState != null) {
                recyclerView.getLayoutManager().onRestoreInstanceState(mListState);
            }



        } else {
            Toast.makeText(MainActivity.this, "Error code " + response.code(), Toast.LENGTH_SHORT).show();
        }

        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void onFailure(Call<MoviesList> call, Throwable t) {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }


    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save list state
        mListState = mLayoutManager.onSaveInstanceState();
        state.putParcelable(LIST_STATE_KEY, mListState);

        state.putString(ORDER_STATE_KEY,order);

    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if (state != null ) {
            mListState = state.getParcelable(LIST_STATE_KEY);
            mOrderState = state.getString(ORDER_STATE_KEY);
        }
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        return noOfColumns;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, FavouritesContract.FavouritesEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<Movie> favourites = new ArrayList<>();


        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID));
                String title = cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_TITLE));
                String posterPath = cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_POSTER_PATH));

                Movie movie = new Movie();
                movie.setId(id);
                movie.setTitle(title);
                movie.setPosterPath(posterPath);

                favourites.add(movie);
            } while (cursor.moveToNext());
        }


        movies.clear();

        movies.addAll(favourites);

        movieAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
