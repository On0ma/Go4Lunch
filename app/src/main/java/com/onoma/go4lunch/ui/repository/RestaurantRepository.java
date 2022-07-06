package com.onoma.go4lunch.ui.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.onoma.go4lunch.data.RestaurantApi;
import com.onoma.go4lunch.data.RetroFitService;
import com.onoma.go4lunch.model.RestaurantResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private static volatile RestaurantRepository instance;

    private final RestaurantApi mRestaurantApi;

    public RestaurantRepository() {
        mRestaurantApi = RetroFitService.getRestaurantApi();
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

    public List<RestaurantResponse> getRestaurantsData() {

        List<RestaurantResponse> restaurantsList = new ArrayList<>();
        mRestaurantApi.getRestaurantsList().enqueue(new Callback<List<RestaurantResponse>>() {
            @Override
            public void onResponse(Call<List<RestaurantResponse>> call, Response<List<RestaurantResponse>> response) {
                restaurantsList.addAll(response.body());
            }

            @Override
            public void onFailure(Call<List<RestaurantResponse>> call, Throwable t) {
                Log.i("API CALL", "FAILURE", t);
            }
        });
        return restaurantsList;
    }
}
