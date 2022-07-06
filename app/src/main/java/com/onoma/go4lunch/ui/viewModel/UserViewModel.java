package com.onoma.go4lunch.ui.viewModel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.onoma.go4lunch.ui.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository mUserRepository;

    public UserViewModel() {
        mUserRepository = UserRepository.getInstance();
    }


    public Boolean isCurrentUserLogged() {
        return (mUserRepository.getCurrentUser() != null);
    }

    public FirebaseUser getCurrentUser() {
        return mUserRepository.getCurrentUser();
    }

    public Task<Void> signOut(Context context) {
        return mUserRepository.SignOut(context);
    }

    public Task<Void> deleteUser(Context context) {
        return mUserRepository.deleteUser(context);
    }
}
