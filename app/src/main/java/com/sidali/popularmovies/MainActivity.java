package com.sidali.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sidali.popularmovies.adapters.MovieAdapter;
import com.sidali.popularmovies.api.MoviesApi;
import com.sidali.popularmovies.model.Movie;
import com.sidali.popularmovies.model.MoviesList;
import com.sidali.popularmovies.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements Callback<MoviesList>  {


    @BindView(R.id.lv_popular_movies) RecyclerView recyclerView;
    private List<Movie> movies=new ArrayList<>();
    private MovieAdapter movieAdapter;
    ProgressDialog mProgressDialog;
    String order="0";
    SharedPreferences sharedPref;
    private EndlessRecyclerViewScrollListener scrollListener;

    Retrofit retrofit = new Retrofit.Builder()
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
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        movieAdapter = new MovieAdapter(MainActivity.this, movies);
        recyclerView.setAdapter(movieAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener((GridLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page+1);
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");


        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        order = sharedPref.getString("order_preference","");
        callApi(order);

    }

    private void loadNextDataFromApi(int page) {
        if(order.equals("0")){
            loadMorePopularMovies(page);
        }else {
            loadMoreTopRatedMovies(page);
        }
    }


    void callApi(String v){
        if(v.equals("0")){
            callPopularMovies();
        }else {
            callTopRatedMovies();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String v = sharedPref.getString("order_preference","");

        if(!order.equals(v)){
            callApi(v);
            order=v;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void loadMorePopularMovies(int page){
        Call<MoviesList> call = moviesApi.loadMorePopularMovies(page);
        call.enqueue(this);
    }

    private void loadMoreTopRatedMovies(int page) {
        Call<MoviesList> call = moviesApi.loadMoreTopRatedMovies(page);
        call.enqueue(this);
    }

    public void callPopularMovies(){
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
        switch (item.getItemId()){
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));

        }
        return true;
    }


    @Override
    public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
        if(response.isSuccessful()){
            if(response.body().page==1){
                movies.clear();
            }
            movies.addAll(response.body().results);

            movieAdapter.notifyDataSetChanged();

        }else {
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
