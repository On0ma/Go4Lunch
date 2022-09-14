package com.onoma.go4lunch.ui.viewModel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.ui.repository.RestaurantRepositoryImpl;
import com.onoma.go4lunch.ui.utils.StateData;
import com.onoma.go4lunch.ui.utils.StateLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RestaurantsViewModel extends ViewModel {
    private final RestaurantRepositoryImpl mRestaurantRepository;

    private StateLiveData<List<Restaurant>> restaurantsLiveData;
    private MutableLiveData<RestaurantRepositoryImpl.RestaurantFavoriteResult> restaurantFavoriteLiveData = new MutableLiveData<>();
    private List<Restaurant> restaurantApiList = new ArrayList<>();

    public RestaurantsViewModel(RestaurantRepositoryImpl restaurantRepository) {
        mRestaurantRepository = restaurantRepository;
    }

    public void getRestaurantsFromSearch(String query, List<Restaurant> restaurants) {
        if (restaurantApiList.isEmpty()) {
            return;
        }
        List<Restaurant> restaurantFiltered = new ArrayList<>();
        for (Restaurant restaurant : restaurantApiList) {
            if (restaurant.getName().contains(query)) {
                restaurantFiltered.add(restaurant);
            }
        }
        restaurantsLiveData.postSuccess(restaurantFiltered);
    }

    public void sortRestaurantsByName(List<Restaurant> restaurants) {
        Collections.sort(restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant r1, Restaurant r2) {
                return r1.getName().compareTo(r2.getName());
            }
        });
        restaurantsLiveData.postSuccess(restaurants);
    }

    public void sortRestaurantsByRating(List<Restaurant> restaurants) {
        Collections.sort(restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant r1, Restaurant r2) {
                return r1.getNbFavorite() - r2.getNbFavorite();
            }
        });
        restaurantsLiveData.postSuccess(restaurants);
    }

    public void sortRestaurantsByDistance(List<Restaurant> restaurants, double longitude, double latitude) {
        Collections.sort(restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant r1, Restaurant r2) {
                int r1Dist = Integer.parseInt(r1.getDistance(longitude, latitude).replace("m", ""));
                int r2Dist = Integer.parseInt(r2.getDistance(longitude, latitude).replace("m", ""));
                return r1Dist - r2Dist;
            }
        });
        restaurantsLiveData.postSuccess(restaurants);
    }

    public LiveData<StateData<List<Restaurant>>> initRestaurants() {
        return restaurantsLiveData;
    }

    public LiveData<StateData<List<Restaurant>>> getRestaurants(double longitude, double latitude) {
        if (restaurantsLiveData == null) {
            restaurantsLiveData = new StateLiveData<>();
            loadRestaurants(longitude, latitude);
        }
        return  restaurantsLiveData;
    }

    private void loadRestaurants(double longitude, double latitude) {
        mRestaurantRepository.getRestaurants(longitude, latitude, new RestaurantRepositoryImpl.RestaurantQuery() {
            @Override
            public void restaurantApiResult(List<Restaurant> restaurants) {
                if (!restaurantApiList.isEmpty()) {
                    restaurantApiList.clear();
                }
                restaurantApiList.addAll(restaurants);
                restaurantsLiveData.postSuccess(restaurants);
            }

            @Override
            public void restaurantApiFailure(String error) {
                restaurantsLiveData.postError(error);
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
}
