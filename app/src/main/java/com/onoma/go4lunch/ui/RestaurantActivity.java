package com.onoma.go4lunch.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.onoma.go4lunch.R;
import com.onoma.go4lunch.databinding.ActivityRestaurantBinding;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.model.User;
import com.onoma.go4lunch.ui.repository.RestaurantRepository;
import com.onoma.go4lunch.ui.repository.UserRepository;
import com.onoma.go4lunch.ui.utils.StateData;
import com.onoma.go4lunch.ui.viewModel.RestaurantsViewModel;
import com.onoma.go4lunch.ui.viewModel.UserViewModel;

import java.util.List;

public class RestaurantActivity extends AppCompatActivity {

    private ActivityRestaurantBinding binding;

    private UserViewModel mUserViewModel;
    private RestaurantsViewModel mRestaurantsViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.restaurantActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mRestaurantsViewModel = new ViewModelProvider(this).get(RestaurantsViewModel.class);

        Restaurant restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");

        mUserViewModel.initRestaurantSelection(restaurant, false);
        mRestaurantsViewModel.initRestaurantFavorite(restaurant, false);

        RecyclerView recyclerView = binding.restaurantRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RestaurantWorkersAdapter adapter = new RestaurantWorkersAdapter();
        recyclerView.setAdapter(adapter);

        final Observer<StateData<List<User>>> usersFromRestaurantObserver = new Observer<StateData<List<User>>>() {
            @Override
            public void onChanged(StateData<List<User>> listStateData) {
                switch (listStateData.getStatus()) {
                    case SUCCESS:
                        adapter.submitList(listStateData.getData());
                        break;
                    case ERROR:
                        Log.i(null, listStateData.getError());
                }
            }
        };

        mUserViewModel.getUsersFromRestaurant(restaurant).observe(this, usersFromRestaurantObserver);

        binding.restaurantLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("FAVORITE CLICK", "réussi");
                mRestaurantsViewModel.initRestaurantFavorite(restaurant, true);
            }
        });

        binding.restaurantActivityCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUserViewModel.initRestaurantSelection(restaurant, true);
            }
        });

        final Observer<UserRepository.RestaurantSelectionResult> updateRestaurantSelectionObserver = new Observer<UserRepository.RestaurantSelectionResult>() {
            @Override
            public void onChanged(UserRepository.RestaurantSelectionResult restaurantSelectionResult) {
                switch (restaurantSelectionResult) {
                    case ADD:
                    case CHECKED:
                        binding.restaurantActivityCheckButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                        break;
                    case DELETE:
                        binding.restaurantActivityCheckButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                        break;
                }
            }
        };
        mUserViewModel.getRestaurantSelection().observe(this, updateRestaurantSelectionObserver);

        final Observer<RestaurantRepository.RestaurantFavoriteResult> updateRestaurantFavoriteObserver = new Observer<RestaurantRepository.RestaurantFavoriteResult>() {
            @Override
            public void onChanged(RestaurantRepository.RestaurantFavoriteResult restaurantFavoriteResult) {
                switch (restaurantFavoriteResult) {
                    case ADD:
                    case CHECKED:
                        Drawable Filled = getResources().getDrawable(R.drawable.ic_baseline_star_24);
                        binding.restaurantLikeButton.setCompoundDrawablesWithIntrinsicBounds(null, Filled, null, null);
                        binding.restaurantName.setCompoundDrawablesWithIntrinsicBounds(null, null, Filled, null);
                        break;
                    case DELETE:
                        Drawable Outline = getResources().getDrawable(R.drawable.ic_baseline_star_outline_24);
                        binding.restaurantLikeButton.setCompoundDrawablesWithIntrinsicBounds(null, Outline, null, null);
                        binding.restaurantName.setCompoundDrawablesWithIntrinsicBounds(null, null, Outline, null);
                        break;
                }
            }
        };
        mRestaurantsViewModel.getRestaurantFavorite().observe(this, updateRestaurantFavoriteObserver);
    }

    private void init() {
        Restaurant restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        binding.restaurantName.setText(restaurant.getName());
        binding.restaurantAdress.setText(restaurant.getAdress());
    }
}
