package com.sidali.popularmovies;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.sidali.popularmovies.adapters.MovieAdapter;
import com.sidali.popularmovies.adapters.ReviewsAdapter;
import com.sidali.popularmovies.adapters.TrailerAdapter;
import com.sidali.popularmovies.api.MoviesApi;
import com.sidali.popularmovies.api.ReviewsApi;
import com.sidali.popularmovies.api.TrailersApi;
import com.sidali.popularmovies.data.FavouritesContract;
import com.sidali.popularmovies.data.FavouritesDbHelper;
import com.sidali.popularmovies.model.Movie;
import com.sidali.popularmovies.model.MovieDetail;
import com.sidali.popularmovies.model.Review;
import com.sidali.popularmovies.model.ReviewList;
import com.sidali.popularmovies.model.Trailer;
import com.sidali.popularmovies.model.TrailersList;
import com.sidali.popularmovies.utils.CustomLinearLayout;
import com.squareup.picasso.Picasso;

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
import static android.R.attr.name;
import static com.sidali.popularmovies.R.id.img;

public class MovieDetailActivity extends AppCompatActivity {
    MovieDetail movieDetail;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.year)
    TextView tv_year;
    @BindView(R.id.duration)
    TextView tv_duration;
    @BindView(R.id.overview)
    TextView tv_overview;
    @BindView(R.id.mark)
    TextView tv_mark;
    @BindView(img)
    ImageView im_img;
    @BindView(R.id.mark_favorite)
    Button bMarkAsFavourite;

    @BindView(R.id.lv_reviews)
    RecyclerView rvReviews;

    @BindView(R.id.lv_trailers)
    RecyclerView rvTrailers;

    private List<Review> reviews=new ArrayList<>();
    private List<Trailer> trailers=new ArrayList<>();

    private TrailerAdapter trailerAdapter;
    private ReviewsAdapter reviewsAdapter;

    ProgressDialog mProgressDialog;
    FavouritesDbHelper dbHelper;


    public final String MARKED="#80cbc4";
    public final String UNMARKED="#f44336";

    public final String MARKED_TEXT="UNMARK";
    public final String UNMARKED_TEXT="MARK AS FAVOURITE";



    Integer id;
    OkHttpClient client =new OkHttpClient.Builder()
            .addNetworkInterceptor(new StethoInterceptor())
            .build();

    Retrofit retrofit = new Retrofit.Builder()
            .client(client)
            .baseUrl("http://api.themoviedb.org/3/movie/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //client.networkInterceptors().add(new StethoInterceptor());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("MovieDetail");
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();

        rvReviews.setHasFixedSize(true);
        RecyclerView.LayoutManager reviewsLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvReviews.setLayoutManager(reviewsLayoutManager);
        reviewsAdapter = new ReviewsAdapter(MovieDetailActivity.this, reviews);
        rvReviews.setAdapter(reviewsAdapter);
        rvReviews.clearFocus();

        rvTrailers.setHasFixedSize(true);
        RecyclerView.LayoutManager trailerslayoutManager = new LinearLayoutManager(getApplicationContext());
        rvTrailers.setLayoutManager(trailerslayoutManager);
        trailerAdapter = new TrailerAdapter(MovieDetailActivity.this, trailers);
        rvTrailers.setAdapter(trailerAdapter);
        rvTrailers.clearFocus();


        dbHelper= new FavouritesDbHelper(this);
        id=getIntent().getIntExtra("id",0);
        callMovieDetail(id.toString());
        callTrailers(id.toString());
        callReviews(id.toString());


        if(!isExist()){
            bMarkAsFavourite.setBackgroundColor(Color.parseColor(MARKED));
            bMarkAsFavourite.setText(UNMARKED_TEXT);
        }else {
            bMarkAsFavourite.setBackgroundColor(Color.parseColor(UNMARKED));
            bMarkAsFavourite.setText(MARKED_TEXT);
        }


    }

    private void callReviews(String id) {
        ReviewsApi reviewsApi = retrofit.create(ReviewsApi.class);
        Call<ReviewList> call = reviewsApi.getReviews(id);
        call.enqueue(new Callback<ReviewList>() {
            @Override
            public void onResponse(Call<ReviewList> call, Response<ReviewList> response) {
                if(response.isSuccessful()) {

                    reviews.clear();
                    reviews.addAll(response.body().results);
                    if (reviews.size() < 1)
                        rvReviews.setVisibility(View.GONE);
                    else {
                        rvReviews.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams params=rvReviews.getLayoutParams();
                        params.height=ViewGroup.LayoutParams.WRAP_CONTENT;
                        rvReviews.setLayoutParams(params);
                    }
                }else {
                    Toast.makeText(MovieDetailActivity.this, "Error code " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReviewList> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callTrailers(String id) {


        final TrailersApi trailersApi = retrofit.create(TrailersApi.class);
        Call<TrailersList> call = trailersApi.getTrailers(id);
        call.enqueue(new Callback<TrailersList>() {
            @Override
            public void onResponse(Call<TrailersList> call, Response<TrailersList> response) {
                if(response.isSuccessful()) {

                    trailers.clear();
                    trailers.addAll(response.body().results);
                    if (trailers.size() < 1)
                        rvTrailers.setVisibility(View.GONE);
                    else {
                        rvTrailers.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams params=rvTrailers.getLayoutParams();
                        params.height=ViewGroup.LayoutParams.WRAP_CONTENT;
                        rvTrailers.setLayoutParams(params);
                    }
                }else {
                    Toast.makeText(MovieDetailActivity.this, "Error code " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TrailersList> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void callMovieDetail(String id) {



        // prepare call in Retrofit 2.0
        MoviesApi moviesApi = retrofit.create(MoviesApi.class);
        Call<MovieDetail> call = moviesApi.getMovieDetail(id);
        //asynchronous call
        call.enqueue(new Callback<MovieDetail>() {
            @Override
            public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
                if(response.isSuccessful()){
                    movieDetail= response.body();
                    Picasso.with(MovieDetailActivity.this).load("http://image.tmdb.org/t/p/w342/"+movieDetail.getPosterPath()).into(im_img);
                    tv_title.setText(movieDetail.getTitle());
                    tv_year.setText(movieDetail.getReleaseDate().split("-")[0]);
                    tv_duration.setText(movieDetail.getRuntime()+"min");
                    tv_overview.setText(movieDetail.getOverview());
                    tv_mark.setText(movieDetail.getVoteAverage()+"/10");


                }else {
                    Toast.makeText(MovieDetailActivity.this, "Error code " + response.code(), Toast.LENGTH_LONG).show();
                }
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<MovieDetail> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(MovieDetailActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                Log.d("onFailure",t.getLocalizedMessage());
            }
        });
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    public void toggleFavourite(View view){
        if(!isExist()){
            bMarkAsFavourite.setBackgroundColor(Color.parseColor(UNMARKED));
            bMarkAsFavourite.setText(MARKED_TEXT);
            markAsFavourite();


        }else {

            bMarkAsFavourite.setBackgroundColor(Color.parseColor(MARKED));
            bMarkAsFavourite.setText(UNMARKED_TEXT);
            unMark();


        }
    }

    public void markAsFavourite() {
        SQLiteDatabase mDb = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID, movieDetail.getId());
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_TITLE, movieDetail.getTitle());
        cv.put(FavouritesContract.FavouritesEntry.COLUMN_MOVIE_POSTER_PATH,movieDetail.getPosterPath());
        getContentResolver().insert(FavouritesContract.FavouritesEntry.CONTENT_URI, cv);
    }

    private void unMark() {
        String[] selectionArgs=new String[]{String.valueOf(movieDetail.getId())};
        String selection=""+FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID+"=?";
        getContentResolver().delete(FavouritesContract.FavouritesEntry.CONTENT_URI, selection, selectionArgs);
    }

    public boolean isExist() {
        SQLiteDatabase mDb = dbHelper.getReadableDatabase();

        Cursor cur = mDb.rawQuery("SELECT * FROM " + FavouritesContract.FavouritesEntry.TABLE_NAME + " WHERE "+ FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID+" = '" + id + "'", null);
        boolean exist = (cur.getCount() > 0);
        cur.close();
        mDb.close();
        return exist;

    }


}
