package com.sidali.popularmovies.api;

import com.sidali.popularmovies.model.MovieDetail;
import com.sidali.popularmovies.model.TrailersList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by shallak on 20/03/2017.
 */

public interface TrailersApi {
    String API_KEY="004d21a7d9f558a098601d09c501ab9d";

    @GET("{id}?api_key=" + API_KEY)
    Call<TrailersList> getTrailers(@Path("id") String id);

}