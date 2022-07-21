package com.onoma.go4lunch.ui.viewModel;

import android.util.Log;

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

    private MutableLiveData<List<Restaurant>> restaurantsLiveData;

    public RestaurantsViewModel() {
        mRestaurantRepository = RestaurantRepository.getInstance();
    }

    /*public LiveData<List<Restaurant>> getRestaurants(double longitude, double latitude) {
        List<Restaurant> result = new ArrayList<>();
        List<Feature> restaurantsData = mRestaurantRepository.getRestaurants(longitude, latitude);
        for (Feature restaurant : restaurantsData) {
            Restaurant item = new Restaurant(
                    restaurantsData.indexOf(restaurant),
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
    }*/

    public LiveData<List<Restaurant>> getRestaurants(double longitude, double latitude) {
        if (restaurantsLiveData == null) {
            restaurantsLiveData = new MutableLiveData<>();
            loadRestaurants(longitude, latitude);
        }
        return  restaurantsLiveData;
    }

    private void loadRestaurants(double longitude, double latitude) {
        List<Restaurant> result = new ArrayList<>();
        mRestaurantRepository.getRestaurants(longitude, latitude, new RestaurantRepository.RestaurantQuery() {
            @Override
            public void restaurantApiResult(List<Feature> restaurants) {
                for (Feature restaurant : restaurants) {
                    Restaurant item = new Restaurant(
                            restaurants.indexOf(restaurant),
                            restaurant.getTextFr(),
                            restaurant.getProperties().getAddress(),
                            restaurant.getProperties().getCategory(),
                            restaurant.getCenter().get(0),
                            restaurant.getCenter().get(1)
                    );
                    result.add(item);
                }
                restaurantsLiveData.setValue(result);
            }

            @Override
            public void restaurantApiFailure(String error, Throwable t) {
                Log.i("TAG", error, t);
            }
        });
    }
}
