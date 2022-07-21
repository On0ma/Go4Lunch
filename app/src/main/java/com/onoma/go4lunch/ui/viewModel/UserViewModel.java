package com.onoma.go4lunch.ui.viewModel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.onoma.go4lunch.model.User;
import com.onoma.go4lunch.ui.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository mUserRepository;

    private MutableLiveData<User> user = new MutableLiveData<>();
    private MutableLiveData<Boolean> userLoggedBoolean = new MutableLiveData<Boolean>();
    private MutableLiveData<Boolean> signOutListener = new MutableLiveData<>();

    public UserViewModel() {
        mUserRepository = UserRepository.getInstance();
    }

    // TODO Useless method, check if I can remove
    /*private FirebaseUser getCurrentUser() {
        return mUserRepository.getCurrentUser();
    }*/

    private Boolean getUserLoggedIn() {
        return (mUserRepository.getCurrentUser() != null);
    }

    public LiveData<Boolean> isCurrentUserLogged() {
        userLoggedBoolean.setValue(getUserLoggedIn());
        return userLoggedBoolean;
    }


    // Create a user from the Firebase User
    private User createUser() {
        String name = mUserRepository.getCurrentUser().getDisplayName();
        String email = mUserRepository.getCurrentUser().getEmail();
        Uri photo = mUserRepository.getCurrentUser().getPhotoUrl();
        return new User(name, email, photo);
    }

    public LiveData<User> getUser() {
        user.setValue(createUser());
        return user;
    }


    // Handle Sign Out
    public LiveData<Boolean> observeSignOut() {
        return signOutListener;
    }

    public void getSignOut(Context context) {
        mUserRepository.SignOut(context, new UserRepository.UserQuery() {
            @Override
            public void signOutResult(boolean signOutResultBool) {
                signOutListener.setValue(signOutResultBool);
            }
        });
    }
}
