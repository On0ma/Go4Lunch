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
import com.onoma.go4lunch.model.User;
import com.onoma.go4lunch.ui.repository.UserRepository;
import com.onoma.go4lunch.ui.utils.StateData;
import com.onoma.go4lunch.ui.utils.StateLiveData;

public class UserViewModel extends ViewModel {
    private final UserRepository mUserRepository;

    private MutableLiveData<User> user = new MutableLiveData<>();
    private MutableLiveData<String> userError = new MutableLiveData<>();
    private MutableLiveData<Boolean> userLoggedBoolean = new MutableLiveData<Boolean>();
    private StateLiveData<User> userLiveData = new StateLiveData<>();

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

    public void getAllUsers() {
        mUserRepository.getAllUsers();
    }

    public Task<Void> signOut(Context context){
        return mUserRepository.signOut(context);
    }
}
