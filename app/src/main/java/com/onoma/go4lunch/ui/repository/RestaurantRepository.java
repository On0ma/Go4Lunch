package com.onoma.go4lunch.ui.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.onoma.go4lunch.data.RestaurantApi;
import com.onoma.go4lunch.data.RetroFitService;
import com.onoma.go4lunch.model.Feature;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.model.RestaurantResponse;
import com.onoma.go4lunch.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private static volatile RestaurantRepository instance;

    private final String queryAcessToken = "pk.eyJ1IjoiZW56by1vIiwiYSI6ImNsNTl3c3N3aDEzcmwzZG5zc2J6eGlsMGIifQ.ukP47h-0lgGwCsWKs2aXsg";
    private final String queryCountry = "fr";
    private final String queryLanguage = "fr";
    private final int queryLimit = 10;
    private final String queryTypes = "poi";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final RestaurantApi restaurantApi;

    public RestaurantRepository() {
        restaurantApi = RetroFitService.getRestaurantApi();
    }

    public static RestaurantRepository getInstance() {
        RestaurantRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (RestaurantRepository.class) {
            if (instance == null) {
                instance = new RestaurantRepository();
            }
            return instance;
        }
    }

    private FirebaseUser currentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public void getRestaurants(double longitude, double latitude, RestaurantQuery callback) {
        String queryProximity = longitude + "," + latitude;
        restaurantApi.getRestaurantList(queryAcessToken, queryCountry, queryLanguage, queryLimit, queryProximity, queryTypes).enqueue(new Callback<RestaurantResponse>() {
            @Override
            public void onResponse(Call<RestaurantResponse> call, Response<RestaurantResponse> response) {
                callback.restaurantApiResult(response.body().getFeatures());
            }
            @Override
            public void onFailure(Call<RestaurantResponse> call, Throwable t) {
                callback.restaurantApiFailure("Error occured", t);
            }
        });
    }

    public void updateRestaurantFavorite(Restaurant restaurant, Boolean update, RestaurantFavoriteQuery callback) {
        db.collection("restaurants").document(restaurant.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        document.getReference().collection("restaurants").document(currentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSecond = task.getResult();
                                    if (documentSecond.exists()) {
                                        if ((documentSecond.getString("uid") != null) && (Objects.equals(documentSecond.getString("uid"), currentUser().getUid()))) {
                                            if (update) {
                                                deleteRestaurantFavorite(restaurant, callback);
                                            } else {
                                                callback.getRestaurantFavorite(RestaurantFavoriteResult.CHECKED);
                                            }
                                        } else {
                                            if (update) {
                                                addRestaurantFavorite(restaurant, callback);
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void addRestaurantFavorite(Restaurant restaurant, RestaurantFavoriteQuery callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", currentUser().getUid());
        db.collection("restaurants").document(restaurant.getId()).collection("users").document().set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                callback.getRestaurantFavorite(RestaurantFavoriteResult.ADD);
            }
        });
    }

    private void deleteRestaurantFavorite(Restaurant restaurant, RestaurantFavoriteQuery callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("uid", FieldValue.delete());
        db.collection("restaurants").document(restaurant.getId()).collection("users").document(currentUser().getUid()).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.getRestaurantFavorite(RestaurantFavoriteResult.DELETE);
            }
        });
    }

    public interface RestaurantQuery {
        void restaurantApiResult(List<Feature> restaurants);
        void restaurantApiFailure(String error, Throwable t);
    }

    public interface RestaurantFavoriteQuery {
        void getRestaurantFavorite(RestaurantFavoriteResult result);
    }

    public enum RestaurantFavoriteResult {
        ADD,
        CHECKED,
        DELETE
    }
}
