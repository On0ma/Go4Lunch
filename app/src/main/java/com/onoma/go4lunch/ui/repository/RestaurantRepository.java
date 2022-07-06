package com.onoma.go4lunch.ui.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.onoma.go4lunch.data.RestaurantApi;
import com.onoma.go4lunch.model.RestaurantResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private final RestaurantApi mRestaurantApi;

    public RestaurantRepository(RestaurantApi restaurantApi) {
        this.mRestaurantApi = restaurantApi;
    }

    public LiveData<List<RestaurantResponse>> getRestaurantsLiveData() {
        MutableLiveData<List<RestaurantResponse>> restaurantsMutableLiveData = new MutableLiveData<>();

        mRestaurantApi.getRestaurantsList().enqueue(new Callback<List<RestaurantResponse>>() {
            @Override
            public void onResponse(Call<List<RestaurantResponse>> call, Response<List<RestaurantResponse>> response) {

            }

            @Override
            public void onFailure(Call<List<RestaurantResponse>> call, Throwable t) {
                restaurantsMutableLiveData.setValue(null);
            }
        });
        return restaurantsMutableLiveData;
    }
}
