package com.onoma.go4lunch.data;

import com.onoma.go4lunch.model.RestaurantResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RestaurantApi {
    @GET("[out:json]; nwr[amenity=restaurant](around:1000,48.8348256,2.3681344); out center;")
    Call<List<RestaurantResponse>> getRestaurantsList();
}
