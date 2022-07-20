package com.onoma.go4lunch.ui;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.onoma.go4lunch.R;
import com.onoma.go4lunch.ViewModelFactory;
import com.onoma.go4lunch.databinding.ActivityMainBinding;
import com.onoma.go4lunch.databinding.HeaderNavigationDrawerBinding;
import com.onoma.go4lunch.model.User;
import com.onoma.go4lunch.ui.viewModel.UserViewModel;

public class MainActivity extends AppCompatActivity {

    private UserViewModel mUserViewModel;

    Fragment currentFragment;
    FragmentTransaction ft;

    private ActivityMainBinding binding;

    View headerView;
    HeaderNavigationDrawerBinding headerBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Set Default Fragment
        currentFragment = new MapFragment();
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container_view, currentFragment);
        ft.commit();

        // Bind the navigation header
        headerView = binding.drawerNavigation.getHeaderView(0);
        headerBinding = HeaderNavigationDrawerBinding.bind(headerView);

        mUserViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserViewModel.class);
        handleDrawerNav();
        handleBottomNav();

        // Observe the liveData of the User
        final Observer<User> userObserver = new Observer<User>() {
            @Override
            public void onChanged(User user) {
                setUserData(user);
            }
        };
        mUserViewModel.getUser().observe(this, userObserver);
    }

    // Open the drawer on click on the menu button
    private void handleDrawerNav() {
        binding.topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawerLayout.open();
            }
        });

        binding.drawerNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.logout_button) {
                    logout();
                } else if (item.getItemId() == R.id.lunch_button) {
                    displayLunch();
                } else if (item.getItemId() == R.id.settings_button) {
                    displaySettings();
                }
                return true;
            }
        });
    }

    // Show Selected Lunch activity
    private void displayLunch() {
        Log.i(null, "LUNCH");
    }

    // Show settings activity
    private void displaySettings() {
        Log.i(null, "SETTINGS");
    }

    // Logout user and send back to the login page
    private void logout() {
        Log.i(null, "LOGOUT");
        mUserViewModel.signOut(this).addOnSuccessListener(aVoid -> {
            finish();
        });
    }

    // Load a different fragment for each bottom tab
    private void handleBottomNav() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    // Load the map view
                    case R.id.nav_map:
                        currentFragment = new MapFragment();
                        handleFragmentLoading(currentFragment);
                        return true;
                    // Load the list view
                    case R.id.nav_list:
                        currentFragment = new ListFragment();
                        handleFragmentLoading(currentFragment);
                        return true;
                    // Load the workers view
                    case R.id.nav_workmates:
                        currentFragment = new WorkmatesFragment();
                        handleFragmentLoading(currentFragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void handleFragmentLoading(Fragment currFrag) {
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container_view, currFrag);
        ft.commit();
    }

    private void setUserData (User user){
        // Get & Set the user picture
        Uri profilePictureUrl = user.getPhotoUrl();
        if (profilePictureUrl != null) {
            Glide.with(this)
                    .load(profilePictureUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(headerBinding.drawerProfile);
        }

        //Get email & username from User
        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getName()) ? getString(R.string.info_no_username_found) : user.getName();

        // Set header text
        headerBinding.drawerName.setText(username);
        headerBinding.drawerMail.setText(email);
    }
}
