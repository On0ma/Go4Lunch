package com.onoma.go4lunch.ui;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.onoma.go4lunch.databinding.ActivityRestaurantBinding;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.ui.repository.UserRepository;
import com.onoma.go4lunch.ui.viewModel.UserViewModel;

import java.util.HashMap;
import java.util.Map;

public class RestaurantActivity extends AppCompatActivity {

    private ActivityRestaurantBinding binding;

    private UserViewModel mUserViewModel;

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

        Restaurant restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");

        mUserViewModel.initRestaurantSelection(restaurant, false);

        Log.i("SELECTED RESTAURANT", String.valueOf(restaurant));

//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        binding.restaurantLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

//        binding.restaurantActivityCheckButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//                                if ((document.getDouble("restaurantSelection") != null) && (document.getDouble("restaurantSelection") == restaurant.getId())) {
//                                    Map<String, Object> updates = new HashMap<>();
//                                    updates.put("restaurantSelection", FieldValue.delete());
//                                    db.collection("users").document(user.getUid()).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            Log.i("RESTAURANT DELETE", "Field successfully deleted");
//                                            binding.restaurantActivityCheckButton.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
//                                        }
//                                    });
//                                } else {
//                                    Map<String, Object> data = new HashMap<>();
//                                    String restaurantName = binding.restaurantName.getText().toString();
//                                    data.put("restaurantSelection", restaurant.getId());
//                                    db.collection("users").document(user.getUid()).set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            Log.i("RESTAURANT SELECTION", "Document successfully written !");
//                                            binding.restaurantActivityCheckButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
//                                        }
//                                    });
//                                }
//                            }
//                        }
//                    }
//                });
//
//            }
//        });
    }

    private void init() {
        Restaurant restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        binding.restaurantName.setText(restaurant.getName());
        binding.restaurantAdress.setText(restaurant.getAdress());
    }
}
