package com.onoma.go4lunch.data;

import com.onoma.go4lunch.model.RestaurantResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestaurantApi {
    @GET("mapbox.places/restaurant.json")
    Call<RestaurantResponse> getRestaurantList(
            @Query("access_token") String access_token,
            @Query("country") String country,
            @Query("language") String language,
            @Query("limit") int limit,
            @Query("proximity") String proximity,
            @Query("types") String types
    );
}
