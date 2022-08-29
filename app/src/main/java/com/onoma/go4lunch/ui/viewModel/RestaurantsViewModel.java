package com.onoma.go4lunch.ui.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.onoma.go4lunch.model.Feature;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.model.User;
import com.onoma.go4lunch.ui.repository.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsViewModel extends ViewModel {
    private final RestaurantRepository mRestaurantRepository;

    private MutableLiveData<List<Restaurant>> restaurantsLiveData;
    private MutableLiveData<RestaurantRepository.RestaurantFavoriteResult> restaurantFavoriteLiveData = new MutableLiveData<>();

    public RestaurantsViewModel() {
        mRestaurantRepository = RestaurantRepository.getInstance();
    }

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
            /*@Override
            public void restaurantApiResult(List<Feature> restaurants) {
                for (Feature restaurant : restaurants) {
                    Restaurant item = new Restaurant(
                            restaurant.getId(),
                            restaurant.getTextFr(),
                            restaurant.getProperties().getAddress(),
                            restaurant.getProperties().getCategory(),
                            restaurant.getCenter().get(0),
                            restaurant.getCenter().get(1)
                    );
                    result.add(item);
                }
                restaurantsLiveData.setValue(result);
            }*/

            @Override
            public void restaurantApiResult(List<Restaurant> restaurants) {
                restaurantsLiveData.setValue(restaurants);
            }

            @Override
            public void restaurantApiFailure(String error) {
                Log.i("TAG", error);
            }
        });
    }

    public void initRestaurantFavorite(Restaurant restaurant, Boolean update) {
        loadRestaurantFavorite(restaurant, update);
    }

    public LiveData<RestaurantRepository.RestaurantFavoriteResult> getRestaurantFavorite() {
        return restaurantFavoriteLiveData;
    }

    private void loadRestaurantFavorite(Restaurant restaurant, Boolean update) {
        mRestaurantRepository.updateRestaurantFavorite(restaurant, update, new RestaurantRepository.RestaurantFavoriteQuery() {
            @Override
            public void getRestaurantFavorite(RestaurantRepository.RestaurantFavoriteResult result) {
                restaurantFavoriteLiveData.setValue(result);
            }
        });
    }
}
