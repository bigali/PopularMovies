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
    String API_KEY="";

    @GET("{id}/videos?api_key=" + API_KEY)
    Call<TrailersList> getTrailers(@Path("id") String id);

}
