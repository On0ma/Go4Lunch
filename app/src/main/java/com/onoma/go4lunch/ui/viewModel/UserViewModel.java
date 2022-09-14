package com.onoma.go4lunch.ui.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.model.User;
import com.onoma.go4lunch.ui.repository.UserRepositoryImpl;
import com.onoma.go4lunch.ui.utils.StateData;
import com.onoma.go4lunch.ui.utils.StateLiveData;

import java.util.List;

public class UserViewModel extends ViewModel {
    private final UserRepositoryImpl mUserRepository;

    private MutableLiveData<Boolean> userLoggedBoolean = new MutableLiveData<Boolean>();
    private StateLiveData<User> userLiveData;
    private StateLiveData<List<User>> usersListLiveData = new StateLiveData<List<User>>();
    private StateLiveData<List<User>> usersFromRestaurantListLiveData = new StateLiveData<>();
    private MutableLiveData<UserRepositoryImpl.RestaurantSelectionResult> restaurantSelectionLiveData = new MutableLiveData<>();
    private StateLiveData<Restaurant> currentUserChoice = new StateLiveData<>();

    public UserViewModel(UserRepositoryImpl userRepository) {
        mUserRepository = userRepository;
    }

    private Boolean getUserLoggedIn() {
        return (mUserRepository.getIsUserLoggedIn());
    }

    public LiveData<Boolean> isCurrentUserLogged() {
        userLoggedBoolean.setValue(getUserLoggedIn());
        return userLoggedBoolean;
    }

    public void createUser() {
        mUserRepository.createUser();
    }

    public LiveData<StateData<User>> getUserData() {
        if (userLiveData == null) {
            userLiveData = new StateLiveData<>();
            loadUserData();
        }
        return userLiveData;
    }

    private void loadUserData() {
        mUserRepository.getUserData(new UserRepositoryImpl.UserQuery() {
            @Override
            public void getUserSuccess(User user) {
                userLiveData.postSuccess(user);
            }

            @Override
            public void getUserFailure(String error) {
                userLiveData.postError(error);
            }
        });
    }

    public LiveData<StateData<List<User>>> getAllUsers() {
        loadAllUsers();
        return usersListLiveData;
    }

    private void loadAllUsers() {
        mUserRepository.getAllUsers(new UserRepositoryImpl.AllUsersQuery() {
            @Override
            public void getAllUsersSuccess(List<User> results) {
                usersListLiveData.postSuccess(results);
            }

            @Override
            public void getAllUsersFailure(String error) {
                usersListLiveData.postError(error);
            }
        });
    }

    public void initUsersFromRestaurant(Restaurant restaurant) {
        loadUsersFromRestaurant(restaurant);
    }

    public LiveData<StateData<List<User>>> getUsersFromRestaurant() {
        return usersFromRestaurantListLiveData;
    }

    private void loadUsersFromRestaurant(Restaurant restaurant) {
        mUserRepository.getAllUsersFromRestaurant(restaurant, new UserRepositoryImpl.AllUsersQuery() {
            @Override
            public void getAllUsersSuccess(List<User> results) {
                usersFromRestaurantListLiveData.postSuccess(results);
            }

            @Override
            public void getAllUsersFailure(String error) {
                usersFromRestaurantListLiveData.postError(error);
            }
        });
    }

    public void initRestaurantSelection(Restaurant restaurant, Boolean update) {
        loadRestaurantSelection(restaurant, update);
    }

    public LiveData<UserRepositoryImpl.RestaurantSelectionResult> getRestaurantSelection() {
        return restaurantSelectionLiveData;
    }

    private void loadRestaurantSelection(Restaurant restaurant, Boolean update) {
        mUserRepository.updateRestaurantSelection(restaurant, update, new UserRepositoryImpl.RestaurantSelectionQuery() {
            @Override
            public void getRestaurantSelection(UserRepositoryImpl.RestaurantSelectionResult result) {
                restaurantSelectionLiveData.setValue(result);
            }
        });
    }

    public void initUserSelection() {
        loadUserSelection();
    }

    public LiveData<StateData<Restaurant>> getUserSelection() {
        return currentUserChoice;
    }

    private void loadUserSelection() {
        mUserRepository.getCurrentUserSelection(new UserRepositoryImpl.UserSelectionQuery() {
            @Override
            public void getUserSelectionSuccess(Restaurant result) {
                currentUserChoice.postSuccess(result);
            }

            @Override
            public void getUserSelectionFailure(String error) {
                currentUserChoice.postError(error);
            }
        });
    }

    public Task<Void> signOut(Context context){
        return mUserRepository.signOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return mUserRepository.deleteUser(context);
    }
}
