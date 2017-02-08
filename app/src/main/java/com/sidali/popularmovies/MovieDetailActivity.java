package com.sidali.popularmovies;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sidali.popularmovies.api.MoviesApi;
import com.sidali.popularmovies.model.MovieDetail;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.sidali.popularmovies.R.id.img;

public class MovieDetailActivity extends AppCompatActivity implements Callback<MovieDetail> {
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
    ProgressDialog mProgressDialog;

    Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("MovieDetail");
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();
        id=getIntent().getIntExtra("id",0);
        callMovieDetail(id.toString());




    }

    private void callMovieDetail(String id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/movie/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // prepare call in Retrofit 2.0
        MoviesApi moviesApi = retrofit.create(MoviesApi.class);
        Call<MovieDetail> call = moviesApi.getMovieDetail(id);
        //asynchronous call
        call.enqueue(this);




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




}
