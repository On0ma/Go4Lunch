package com.onoma.go4lunch.ui.viewModel;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.model.User;
import com.onoma.go4lunch.ui.repository.UserRepository;
import com.onoma.go4lunch.ui.utils.StateData;
import com.onoma.go4lunch.ui.utils.StateLiveData;

import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends ViewModel {
    private final UserRepository mUserRepository;

    // private MutableLiveData<User> user = new MutableLiveData<>();
    // private MutableLiveData<String> userError = new MutableLiveData<>();
    private MutableLiveData<Boolean> userLoggedBoolean = new MutableLiveData<Boolean>();
    private StateLiveData<User> userLiveData = new StateLiveData<>();
    private StateLiveData<List<User>> usersListLiveData = new StateLiveData<List<User>>();
    private StateLiveData<List<User>> usersFromRestaurantListLiveData = new StateLiveData<>();
    private MutableLiveData<UserRepository.RestaurantSelectionResult> restaurantSelectionLiveData = new MutableLiveData<>();

    public UserViewModel() {
        mUserRepository = UserRepository.getInstance();
    }

    private Boolean getUserLoggedIn() {
        return (mUserRepository.getCurrentUser() != null);
    }

    public LiveData<Boolean> isCurrentUserLogged() {
        userLoggedBoolean.setValue(getUserLoggedIn());
        return userLoggedBoolean;
    }

    // Create a user from the Firebase User
//    private User createUser() {
//        String uid = mUserRepository.getCurrentUser().getUid();
//        String name = mUserRepository.getCurrentUser().getDisplayName();
//        String email = mUserRepository.getCurrentUser().getEmail();
//        String photo = mUserRepository.getCurrentUser().getPhotoUrl().toString();
//        return new User(uid, name, email, photo);
//    }
//
//    public LiveData<User> getUser() {
//        user.setValue(createUser());
//        return user;
//    }

    public void createUser() {
        mUserRepository.createUser();
    }

//    public LiveData<User> getUserData() {
//        if (user == null) {
//            loadUserData();
//        }
//        return user;
//    }

    public LiveData<StateData<User>> getUserData() {
        loadUserData();
        return userLiveData;
    }

    private void loadUserData() {
        mUserRepository.getUserData(new UserRepository.UserQuery() {
            @Override
            public void getUserSuccess(DocumentSnapshot userDocument) {
                //user.setValue(userDocument.toObject(User.class));
                User userData = new User(
                        userDocument.getString("uid"),
                        userDocument.getString("name"),
                        userDocument.getString("email"),
                        userDocument.getString("photoUrl")
                );
                userLiveData.postSuccess(userData);
            }

            @Override
            public void getUserFailure(String error) {
                // userError.setValue(error);
                // Log.i("USER FAILURE", error);
                userLiveData.postError(error);
            }
        });
    }

    public LiveData<StateData<List<User>>> getAllUsers() {
        loadAllUsers();
        return usersListLiveData;
    }

    private void loadAllUsers() {
        mUserRepository.getAllUsers(new UserRepository.AllUsersQuery() {
            @Override
            public void getAllUsersSuccess(QuerySnapshot results) {
                List<User> userList = new ArrayList<>();
                for (QueryDocumentSnapshot document : results) {
                    User newUser = new User(
                            document.getString("uid"),
                            document.getString("name"),
                            document.getString("email"),
                            document.getString("photoUrl")
                    );
                    userList.add(newUser);
                }
                usersListLiveData.postSuccess(userList);
            }

            @Override
            public void getAllUsersFailure(String error) {
                usersListLiveData.postError(error);
            }
        });
    }

    public LiveData<StateData<List<User>>> getUsersFromRestaurant(Restaurant restaurant) {
        loadUsersFromRestaurant(restaurant);
        return usersFromRestaurantListLiveData;
    }

    private void loadUsersFromRestaurant(Restaurant restaurant) {
        mUserRepository.getAllUsers(new UserRepository.AllUsersQuery() {
            @Override
            public void getAllUsersSuccess(QuerySnapshot results) {
                List<User> userList = new ArrayList<>();
                for (QueryDocumentSnapshot document : results) {
                    if ((document.getString("restaurantSelection") != null) && (document.getString("restaurantSelection").equals(restaurant.getId()))) {
                        User newUser = new User(
                                document.getString("uid"),
                                document.getString("name"),
                                document.getString("email"),
                                document.getString("photoUrl")
                        );
                        userList.add(newUser);
                    }
                }
                usersFromRestaurantListLiveData.postSuccess(userList);
            }

            @Override
            public void getAllUsersFailure(String error) {
                usersFromRestaurantListLiveData.postError(error);
            }
        });
    }

    public Task<Void> signOut(Context context){
        return mUserRepository.signOut(context);
    }

    public void initRestaurantSelection(Restaurant restaurant, Boolean update) {
        loadRestaurantSelection(restaurant, update);
    }

    public LiveData<UserRepository.RestaurantSelectionResult> getRestaurantSelection() {
        return restaurantSelectionLiveData;
    }

    private void loadRestaurantSelection(Restaurant restaurant, Boolean update) {
        mUserRepository.updateRestaurantSelection(restaurant, update, new UserRepository.RestaurantSelectionQuery() {
            @Override
            public void getRestaurantSelection(UserRepository.RestaurantSelectionResult result) {
                restaurantSelectionLiveData.setValue(result);
            }
        });
    }
}
