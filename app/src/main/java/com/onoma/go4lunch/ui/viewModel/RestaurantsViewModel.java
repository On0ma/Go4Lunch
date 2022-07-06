package com.onoma.go4lunch.ui.viewModel;

import androidx.lifecycle.ViewModel;

import com.onoma.go4lunch.model.RestaurantResponse;
import com.onoma.go4lunch.ui.repository.RestaurantRepository;

import java.util.List;

public class RestaurantsViewModel extends ViewModel {
    private final RestaurantRepository mRestaurantRepository;

    public RestaurantsViewModel() {
        mRestaurantRepository = RestaurantRepository.getInstance();
    }


    public List<RestaurantResponse> getRestaurants() {
        return mRestaurantRepository.getRestaurantsData();
    }
}
