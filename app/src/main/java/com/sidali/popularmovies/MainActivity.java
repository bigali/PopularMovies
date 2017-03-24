package com.sidali.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

import static android.R.attr.data;
import static android.R.id.list;
import static com.sidali.popularmovies.R.id.tv_title;

public class MainActivity extends AppCompatActivity implements Callback<MoviesList> {


    @BindView(R.id.lv_popular_movies)
    RecyclerView recyclerView;
    private List<Movie> movies = new ArrayList<>();
    private MovieAdapter movieAdapter;
    private SQLiteDatabase mDb;
    ProgressDialog mProgressDialog;
    String order = "0";
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        movieAdapter = new MovieAdapter(MainActivity.this, movies);
        recyclerView.setAdapter(movieAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) layoutManager) {
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
        if (order.equals("0")) {
            loadMorePopularMovies(page);
        } else if (order.equals("1")) {
            loadMoreTopRatedMovies(page);
        }
    }


    void callApi(String v) {
        if (v.equals("0")) {
            callPopularMovies();
        } else if (order.equals("1")) {
            callTopRatedMovies();
        } else {

            getFavourites();
        }
    }

    private void getFavourites() {
        List<Movie> favourites = new ArrayList<>();



        Cursor cursor = getContentResolver().query(FavouritesContract.FavouritesEntry.CONTENT_URI, null, null, null, null);

        try{
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID));
                    String title = cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_TITLE));
                    String posterPath = cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_POSTER_PATH));

                    Movie movie= new Movie();
                    movie.setId(id);
                    movie.setTitle(title);
                    movie.setPosterPath(posterPath);

                    favourites.add(movie);
                } while (cursor.moveToNext());
            }



            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID));
                String title = cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_TITLE));
                String posterPath = cursor.getString(cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_POSTER_PATH));

                Movie movie= new Movie();
                movie.setId(id);
                movie.setTitle(title);
                movie.setPosterPath(posterPath);

                favourites.add(movie);
                cursor.moveToNext();
            }
        }finally {
            // make sure to close the cursor
            cursor.close();
        }

        movies.clear();

        movies.addAll(favourites);

        movieAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
        String v = sharedPref.getString("order_preference", "");

        if (!order.equals(v)) {
            callApi(v);
            order = v;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void loadMorePopularMovies(int page) {
        Call<MoviesList> call = moviesApi.loadMorePopularMovies(page);
        call.enqueue(this);
    }

    private void loadMoreTopRatedMovies(int page) {
        Call<MoviesList> call = moviesApi.loadMoreTopRatedMovies(page);
        call.enqueue(this);
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
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));

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



}
