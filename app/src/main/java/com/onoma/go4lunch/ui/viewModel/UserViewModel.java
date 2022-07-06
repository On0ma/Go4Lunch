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

    public UserViewModel() {
        mUserRepository = UserRepository.getInstance();
    }


    // If the user is logged in or not
    private FirebaseUser getCurrentUser() {
        return mUserRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }


    // Create a user from the Firebase User
    private User createUser() {
        String name = getCurrentUser().getDisplayName();
        String email = getCurrentUser().getEmail();
        Uri photo = getCurrentUser().getPhotoUrl();
        return new User(name, email, photo);
    }

    public LiveData<User> getUser() {
        this.user.setValue(createUser());
        return user;
    }


    // Handle Sign Out
    public Task<Void> signOut(Context context) {
        return mUserRepository.SignOut(context);
    }


    // Handle deleting account
    public Task<Void> deleteUser(Context context) {
        return mUserRepository.deleteUser(context);
    }
}
