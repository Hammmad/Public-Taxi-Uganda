package com.company.coder.publicTaxi.routes;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

public interface RetrofitMaps {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("api/directions/json?key=AIzaSyDecAA1tPGUFmzl8TaHuIevVJFQw5PFh18")
    Call<Example> getDistanceDuration(@Query("units") String units, @Query("origin") String origin, @Query("destination") String destination, @Query("mode") String mode);
}
