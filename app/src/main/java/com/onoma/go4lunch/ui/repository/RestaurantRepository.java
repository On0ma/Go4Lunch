package com.onoma.go4lunch.ui.repository;

import com.onoma.go4lunch.model.Restaurant;

import java.util.List;
import java.util.Map;

public interface RestaurantRepository {
    void getRestaurants(double longitude, double latitude, RestaurantQuery callback);
    void updateRestaurantFavorite(Restaurant restaurant, Boolean update, RestaurantFavoriteQuery callback);

    interface RestaurantQuery {
        void restaurantApiResult(List<Restaurant> restaurants);
        void restaurantApiFailure(String error);
    }

    interface RestaurantFavoriteQuery {
        void getRestaurantFavorite(RestaurantFavoriteResult result);
    }

    enum RestaurantFavoriteResult {
        ADD,
        CHECKED,
        DELETE
    }
}
