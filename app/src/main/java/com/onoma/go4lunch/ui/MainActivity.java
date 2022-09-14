package com.onoma.go4lunch.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.onoma.go4lunch.R;
import com.onoma.go4lunch.databinding.ActivityMainBinding;
import com.onoma.go4lunch.databinding.HeaderNavigationDrawerBinding;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.model.User;
import com.onoma.go4lunch.ui.repository.UserRepository;
import com.onoma.go4lunch.ui.utils.StateData;
import com.onoma.go4lunch.ui.viewModel.RestaurantsViewModel;
import com.onoma.go4lunch.ui.viewModel.UserViewModel;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    FusedLocationProviderClient mFusedLocationProviderClient;
    int PERMISSION_ID = 44;

    private UserViewModel mUserViewModel;
    private RestaurantsViewModel mRestaurantsViewModel;

    Fragment currentFragment;
    FragmentTransaction ft;

    private ActivityMainBinding binding;

    View headerView;
    HeaderNavigationDrawerBinding headerBinding;

    private final String DEFAULT_CHANNEL_ID = "0";

    private double longitude;
    private double latitude;

    private Restaurant currentRestaurant;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = (Toolbar) binding.topAppBar;
        setSupportActionBar(toolbar);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();

        // Bind the navigation header
        headerView = binding.drawerNavigation.getHeaderView(0);
        headerBinding = HeaderNavigationDrawerBinding.bind(headerView);

        mUserViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserViewModel.class);
        mRestaurantsViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(RestaurantsViewModel.class);

        handleDrawerNav();
        createNotificationChannel();

        mUserViewModel.initUserSelection();

        final Observer<StateData<User>> userObserver = new Observer<StateData<User>>() {
            @Override
            public void onChanged(StateData<User> user) {
                switch (user.getStatus()) {
                    case ERROR:
                        String error = user.getError();
                        Log.e(null, error);
                        break;
                    case SUCCESS:
                        User userData = user.getData();
                        setUserData(userData);
                        break;
                }
            }
        };
        mUserViewModel.getUserData().observe(this, userObserver);

        final Observer<StateData<Restaurant>> userSelectionObserver = new Observer<StateData<Restaurant>>() {
            @Override
            public void onChanged(StateData<Restaurant> stringStateData) {
                switch (stringStateData.getStatus()) {
                    case SUCCESS:
                        mUserViewModel.initUsersFromRestaurant(stringStateData.getData());
                        currentRestaurant = stringStateData.getData();
                        break;
                    case ERROR:
                        currentRestaurant = null;
                        Log.e("Error user selection", stringStateData.getError());
                }
            }
        };
        mUserViewModel.getUserSelection().observe(this, userSelectionObserver);

        final Observer<StateData<List<User>>> usersFromRestaurantObserver = new Observer<StateData<List<User>>>() {
            @Override
            public void onChanged(StateData<List<User>> listStateData) {
                switch (listStateData.getStatus()) {
                    case SUCCESS:
                        List<User> users = listStateData.getData();

                        String names = "";
                        for (User user : users) {
                            if (users.indexOf(user)+1 == users.size()) {
                                names = names + user.getName();
                            } else {
                                names = names + user.getName() + ", ";
                            }
                        }
                        setNotification(currentRestaurant, names);
                        break;
                    case ERROR:
                        Log.e(null, listStateData.getError());
                }
            }
        };
        mUserViewModel.getUsersFromRestaurant().observe(this, usersFromRestaurantObserver);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserViewModel.initUserSelection();
        binding.drawerLayout.close();
    }

    private void setNotification(Restaurant restaurant, String names) {
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        boolean notificationChoice = sharedPreferences.getBoolean(getString(R.string.notifcations_choice_key), true);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent alarmShowIntent = new Intent(this, BroadcastManager.class);
        Bundle bundle = new Bundle();
        bundle.putString("names", names);
        bundle.putSerializable("restaurant", restaurant);
        alarmShowIntent.putExtra("bundle", bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 110, alarmShowIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (!notificationChoice) {
            alarmManager.cancel(pendingIntent);
        } else {
            Calendar calendar = Calendar.getInstance();
            long currentTime = calendar.getTimeInMillis();
            calendar.setTimeInMillis(System.currentTimeMillis());
            // Remove 2 hours from current time to get to UTC
            calendar.set(Calendar.HOUR_OF_DAY, 10);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            long notificationTime = calendar.getTimeInMillis();
            // Add a day to the notification if you launched the app after 12 PM
            if (notificationTime-currentTime < 0) {
                Log.i("ALARM", "alarm is set fot next day");
                calendar.add(Calendar.DATE, 1);
            }
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.default_notification_channel_name);
            String description = getString(R.string.default_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(DEFAULT_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
        if (currentRestaurant != null) {
            Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
            intent.putExtra("restaurant", currentRestaurant);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "No restaurant selected", Toast.LENGTH_SHORT).show();
        }
        // mUserViewModel.initUserSelection();
    }

    // Show settings activity
    private void displaySettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    // Logout user and send back to the login page
    private void logout() {
        mUserViewModel.signOut(this).addOnSuccessListener(aVoid -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Load a different fragment for each bottom tab
    private void handleBottomNav(Bundle bundle) {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    // Load the map view
                    case R.id.nav_map:
                        currentFragment = new MapFragment();
                        handleFragmentLoading(currentFragment, bundle, "MAP");
                        return true;
                    // Load the list view
                    case R.id.nav_list:
                        currentFragment = new ListFragment();
                        handleFragmentLoading(currentFragment, bundle, "LIST");
                        return true;
                    // Load the workers view
                    case R.id.nav_workmates:
                        currentFragment = new WorkmatesFragment();
                        handleFragmentLoading(currentFragment, bundle, "WORKMATES");
                        return true;
                }
                return false;
            }
        });
    }

    private void handleFragmentLoading(Fragment currFrag, Bundle bundle, String fragTag) {
        currentFragment.setArguments(bundle);
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container_view, currFrag, fragTag);
        ft.commit();
    }

    private void setUserData (User user){
        if (user != null) {
            // Get & Set the user picture
            String profilePictureUrl = user.getPhotoUrl();
            if (profilePictureUrl != null) {
                Glide.with(this)
                        .load(profilePictureUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .error(R.drawable.ic_baseline_person_24)
                        .into(headerBinding.drawerProfile);
            }
            //Get email & username from User
            String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
            String username = TextUtils.isEmpty(user.getName()) ? getString(R.string.info_no_username_found) : user.getName();

            // Set header text
            headerBinding.drawerName.setText(username);
            headerBinding.drawerMail.setText(email);
        }
        else {
            Log.e("SET USER DATA ERROR", "user is null");
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
                mFusedLocationProviderClient.getCurrentLocation(priority, new CancellationToken() {
                    @NonNull
                    @Override
                    public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                        return null;
                    }

                    @Override
                    public boolean isCancellationRequested() {
                        return false;
                    }
                }).addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            // Get user location
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            // Put Location in bundle
                            Bundle bundle = new Bundle();
                            bundle.putDouble("longitude", longitude);
                            bundle.putDouble("latitude", latitude);

                            // Set Default Fragment
                            currentFragment = new MapFragment();
                            handleFragmentLoading(currentFragment, bundle, "MAP");

                            handleBottomNav(bundle);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on your location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }
}
