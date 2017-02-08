package com.sidali.popularmovies.api;

import com.sidali.popularmovies.model.Movie;
import com.sidali.popularmovies.model.MovieDetail;
import com.sidali.popularmovies.model.MoviesList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.sidali.popularmovies.api.MoviesApi.API_KEY;

/**
 * Created by shallak on 05/02/2017.
 */

public interface MoviesApi {
    String API_KEY="";

    @GET("popular?api_key=" + API_KEY)
    Call<MoviesList> getPopularMovies();

    @GET("top_rated?api_key=" + API_KEY)
    Call<MoviesList> getTopRatedMovies();

    @GET("popular?api_key=" + API_KEY )
    Call<MoviesList> loadMorePopularMovies(@Query("page") int page);

    @GET("top_rated?api_key=" + API_KEY)
    Call<MoviesList> loadMoreTopRatedMovies(@Query("page") int page);

    @GET("{id}?api_key=" + API_KEY)
    Call<MovieDetail> getMovieDetail(@Path("id") String id);

}
