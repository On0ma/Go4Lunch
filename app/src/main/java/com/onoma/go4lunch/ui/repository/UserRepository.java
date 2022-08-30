package com.onoma.go4lunch.ui.repository;

import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.model.User;

import java.util.List;

public interface UserRepository {
    void createUser();
    void getUserData(UserQuery callback);
    void getAllUsers(AllUsersQuery callback);
    void getAllUsersFromRestaurant(Restaurant restaurant, AllUsersQuery callback);
    void getCurrentUserSelection(UserSelectionQuery callback);
    Boolean getIsUserLoggedIn();


    interface UserQuery {
        void getUserSuccess(User user);
        void getUserFailure(String error);
    }

    interface AllUsersQuery {
        void getAllUsersSuccess(List<User> results);
        void getAllUsersFailure(String error);
    }

    interface RestaurantSelectionQuery {
        void getRestaurantSelection(RestaurantSelectionResult result);
    }

    interface UserSelectionQuery {
        void getUserSelectionSuccess(Restaurant result);
        void getUserSelectionFailure(String error);
    }

    enum RestaurantSelectionResult {
        ADD,
        DELETE,
        CHECKED
    }
}
