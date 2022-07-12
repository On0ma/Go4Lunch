package com.onoma.go4lunch.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mapbox.geojson.FeatureCollection;
import com.onoma.go4lunch.model.Feature;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.model.RestaurantResponse;
import com.onoma.go4lunch.ui.repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsViewModel extends ViewModel {
    private final RestaurantRepository mRestaurantRepository;

    private MutableLiveData<List<Restaurant>> restaurants = new MutableLiveData<>();

    public RestaurantsViewModel() {
        mRestaurantRepository = RestaurantRepository.getInstance();
    }


   /* public void getRestaurantsList() {
        mRestaurantRepository.performSearch();
    }*/

    public LiveData<List<Restaurant>> getRestaurants() {
        List<Restaurant> result = new ArrayList<>();
        List<Feature> restaurantsData = mRestaurantRepository.getRestaurants();
        for (Feature restaurant : restaurantsData) {
            Restaurant item = new Restaurant(
                    restaurant.getTextFr(),
                    restaurant.getProperties().getAddress(),
                    restaurant.getProperties().getCategory(),
                    restaurant.getCenter().get(0),
                    restaurant.getCenter().get(1)
            );
            result.add(item);
        }
        restaurants.setValue(result);
        return restaurants;
    }
}
