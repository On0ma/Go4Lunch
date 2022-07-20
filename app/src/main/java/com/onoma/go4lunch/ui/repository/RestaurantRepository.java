package com.onoma.go4lunch.ui.repository;

import android.util.Log;

import com.onoma.go4lunch.data.RestaurantApi;
import com.onoma.go4lunch.data.RetroFitService;
import com.onoma.go4lunch.model.Feature;
import com.onoma.go4lunch.model.RestaurantResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private static volatile RestaurantRepository instance;

    private final double LONGITUDE = 2.356526;
    private final double LATITUDE = 48.831351;

    private final String queryAcessToken = "pk.eyJ1IjoiZW56by1vIiwiYSI6ImNsNTl3c3N3aDEzcmwzZG5zc2J6eGlsMGIifQ.ukP47h-0lgGwCsWKs2aXsg";
    private final String queryCountry = "fr";
    private final String queryLanguage = "fr";
    private final int queryLimit = 10;
    private final String queryProximity = LONGITUDE + "," + LATITUDE;
    private final String queryTypes = "poi";

    private final RestaurantApi restaurantApi;

    public RestaurantRepository() {
        restaurantApi = RetroFitService.getRestaurantApi();
    }

    public static RestaurantRepository getInstance() {
        RestaurantRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (RestaurantRepository.class) {
            if (instance == null) {
                instance = new RestaurantRepository();
            }
            return instance;
        }
    }

    private List<Feature> RestaurantListData = new ArrayList<>();

    public List<Feature> getRestaurants() {
        restaurantApi.getRestaurantList(queryAcessToken, queryCountry, queryLanguage, queryLimit, queryProximity, queryTypes).enqueue(new Callback<RestaurantResponse>() {
            @Override
            public void onResponse(Call<RestaurantResponse> call, Response<RestaurantResponse> response) {
                if (response.isSuccessful()) {
                    RestaurantListData.addAll(response.body().getFeatures());
                    Log.i("RESULT", String.valueOf(response.body().getFeatures()));
                } else {
                    Log.i("API RESULT", "Something went wrong");
                }
            }

            @Override
            public void onFailure(Call<RestaurantResponse> call, Throwable t) {
                Log.e("TAG", "Error occured", t);
            }
        });
        return RestaurantListData;
    }
}
