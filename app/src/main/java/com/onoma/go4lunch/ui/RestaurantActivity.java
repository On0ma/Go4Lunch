package com.onoma.go4lunch.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.onoma.go4lunch.databinding.ActivityRestaurantBinding;
import com.onoma.go4lunch.model.Restaurant;

public class RestaurantActivity extends AppCompatActivity {

    private ActivityRestaurantBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestaurantBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.restaurantActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        Restaurant restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        binding.restaurantName.setText(restaurant.getName());
        binding.restaurantAdress.setText(restaurant.getAdress());
    }
}
