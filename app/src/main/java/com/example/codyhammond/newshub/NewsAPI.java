package com.example.codyhammond.newshub;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by codyhammond on 1/8/18.
 */

public interface NewsAPI {

    @GET("top-headlines")
    Call<GSONCenter>getTopStoriesSource(@Query(value="sources") String endpoint,@Query(value="pageSize")int size, @Query(value = "apikey") String apikey);

    @GET("top-headlines")
    Call<GSONCenter>getTopStoriesALL(@Query(value="country") String endpoint,@Query(value="pageSize")int size, @Query(value = "apikey") String apikey);

    @GET("sources")
    Call<GSONCenter>getSources( @Query(value = "apikey") String apikey);

    @GET("everything")
    Call<GSONCenter>getStoriesBySearch(@Query(value = "q") String endpoint,@Query(value="pageSize")int size, @Query(value = "apikey") String apikey);

}
