package com.onoma.go4lunch.ui.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.ui.repository.RestaurantRepositoryImpl;
import com.onoma.go4lunch.ui.utils.StateData;
import com.onoma.go4lunch.ui.utils.StateLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantsViewModel extends ViewModel {
    private final RestaurantRepositoryImpl mRestaurantRepository;

    private MutableLiveData<List<Restaurant>> restaurantsLiveData;
    private MutableLiveData<RestaurantRepositoryImpl.RestaurantFavoriteResult> restaurantFavoriteLiveData = new MutableLiveData<>();
    private StateLiveData<List<Map<String, Object>>> restaurantListenerLiveData = new StateLiveData<>();
    public Map<String, Double> RestaurantListenerData = new HashMap<>();

    public RestaurantsViewModel() {
        mRestaurantRepository = RestaurantRepositoryImpl.getInstance();
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
        mRestaurantRepository.getRestaurants(longitude, latitude, new RestaurantRepositoryImpl.RestaurantQuery() {
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

    public LiveData<RestaurantRepositoryImpl.RestaurantFavoriteResult> getRestaurantFavorite() {
        return restaurantFavoriteLiveData;
    }

    private void loadRestaurantFavorite(Restaurant restaurant, Boolean update) {
        mRestaurantRepository.updateRestaurantFavorite(restaurant, update, new RestaurantRepositoryImpl.RestaurantFavoriteQuery() {
            @Override
            public void getRestaurantFavorite(RestaurantRepositoryImpl.RestaurantFavoriteResult result) {
                restaurantFavoriteLiveData.setValue(result);
            }
        });
    }

    public Map<String, Double> getRestaurantTestListener(Restaurant restaurant) {
        return mRestaurantRepository.restaurantListenerTest(restaurant);
    }

    public LiveData<StateData<List<Map<String, Object>>>> getRestaurantListener() {
        loadRestaurantListener();
        return restaurantListenerLiveData;
    }

    private void loadRestaurantListener() {
        mRestaurantRepository.restaurantListener(new RestaurantRepositoryImpl.RestaurantListenerQuery() {
            @Override
            public void restaurantListenerResult(List<Map<String, Object>> result) {
                restaurantListenerLiveData.postSuccess(result);
            }

            @Override
            public void restaurantListenerFailure(String error) {
                restaurantListenerLiveData.postError(error);
            }
        });
    }
}
