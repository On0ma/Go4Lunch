package com.onoma.go4lunch.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.MenuItemCompat;
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
import com.onoma.go4lunch.ui.utils.StateData;
import com.onoma.go4lunch.ui.viewModel.UserViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    FusedLocationProviderClient mFusedLocationProviderClient;
    int PERMISSION_ID = 44;

    private UserViewModel mUserViewModel;

    Fragment currentFragment;
    FragmentTransaction ft;

    private ActivityMainBinding binding;

    View headerView;
    HeaderNavigationDrawerBinding headerBinding;

    private final double LONGITUDE = 2.356526;
    private final double LATITUDE = 48.831351;
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

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();
        Log.i("LONGITUDE BEFORE ASYNC", String.valueOf(longitude));
        Log.i("LATITUDE BEFORE ASYNC", String.valueOf(latitude));

        // Put Location in bundle
        Bundle bundle = new Bundle();
        bundle.putDouble("longitude", LONGITUDE);
        bundle.putDouble("latitude", LATITUDE);

        // Set Default Fragment
        currentFragment = new MapFragment();
        handleFragmentLoading(currentFragment, bundle);

        // Bind the navigation header
        headerView = binding.drawerNavigation.getHeaderView(0);
        headerBinding = HeaderNavigationDrawerBinding.bind(headerView);

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        handleDrawerNav();
        handleBottomNav(bundle);
        // Set a different tab by default on launch
        // binding.bottomNavigation.setSelectedItemId(R.id.nav_list);

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
                       /* Intent intent = new Intent(getApplicationContext(), RestaurantActivity.class);
                        intent.putExtra("restaurant", stringStateData.getData());
                        startActivity(intent);*/
                        mUserViewModel.initUsersFromRestaurant(stringStateData.getData());
                        currentRestaurant = stringStateData.getData();
                        Log.i("RESTAURANT ACTUEL", String.valueOf(currentRestaurant));
                        break;
                    case ERROR:
                        Toast.makeText(getApplicationContext(), stringStateData.getError(), Toast.LENGTH_SHORT).show();
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
                            Log.i("USERS FROM REST", names);
                        }
                        setNotification(currentRestaurant, names);
                        break;
                    case ERROR:
                        Log.i(null, listStateData.getError());
                }
            }
        };
        mUserViewModel.getUsersFromRestaurant().observe(this, usersFromRestaurantObserver);

        createNotificationChannel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_menu, menu);

        final String[] COUNTRIES = new String[] {
                "Belgium", "France", "Italy", "Germany", "Spain"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        AutoCompleteTextView textView = (AutoCompleteTextView) menu.findItem(R.id.nav_search);
        textView.setAdapter(adapter);

        return true;
    }

    private void setNotification(Restaurant restaurant, String names) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_local_dining_24)
                .setContentTitle(getString(R.string.notification_content_title))
                .setContentText(getString(R.string.notification_content_description, restaurant.getName(), restaurant.getAdress(), names))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.notification_content_description, restaurant.getName(), restaurant.getAdress(), names)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(100, builder.build());

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
        Log.i(null, "LUNCH");
        mUserViewModel.initUserSelection();
    }

    // Show settings activity
    private void displaySettings() {
        Log.i(null, "SETTINGS");
    }

    // Logout user and send back to the login page
    private void logout() {
        mUserViewModel.signOut(this).addOnSuccessListener(aVoid -> {
            Log.i(null, "LOGOUT");
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

                        handleFragmentLoading(currentFragment, bundle);
                        return true;
                    // Load the list view
                    case R.id.nav_list:
                        currentFragment = new ListFragment();
                        handleFragmentLoading(currentFragment, bundle);
                        return true;
                    // Load the workers view
                    case R.id.nav_workmates:
                        currentFragment = new WorkmatesFragment();
                        handleFragmentLoading(currentFragment, bundle);
                        return true;
                }
                return false;
            }
        });
    }

    private void handleFragmentLoading(Fragment currFrag, Bundle bundle) {
        currentFragment.setArguments(bundle);
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container_view, currFrag);
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
            Log.i("SET USER DATA ERROR", "user is null");
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
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            Log.i("LONGITUDE AFTER ASYNC", String.valueOf(longitude));
                            Log.i("LATITUDE AFTER ASYNC", String.valueOf(latitude));
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
