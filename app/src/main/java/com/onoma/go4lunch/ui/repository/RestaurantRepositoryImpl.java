package com.onoma.go4lunch.ui.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.onoma.go4lunch.data.RestaurantApi;
import com.onoma.go4lunch.data.RetroFitService;
import com.onoma.go4lunch.model.Feature;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.model.RestaurantResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepositoryImpl implements RestaurantRepository {

    private static volatile RestaurantRepositoryImpl instance;

    private final String queryAcessToken = "pk.eyJ1IjoiZW56by1vIiwiYSI6ImNsNTl3c3N3aDEzcmwzZG5zc2J6eGlsMGIifQ.ukP47h-0lgGwCsWKs2aXsg";
    private final String queryCountry = "fr";
    private final String queryLanguage = "fr";
    private final int queryLimit = 10;
    private final String queryTypes = "poi";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final RestaurantApi restaurantApi;

    public RestaurantRepositoryImpl() {
        restaurantApi = RetroFitService.getRestaurantApi();
    }

    public static RestaurantRepositoryImpl getInstance() {
        RestaurantRepositoryImpl result = instance;
        if (result != null) {
            return result;
        }
        synchronized (RestaurantRepositoryImpl.class) {
            if (instance == null) {
                instance = new RestaurantRepositoryImpl();
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
                List<Restaurant> result = new ArrayList<>();
                WriteBatch batch = db.batch();

                Log.i("API CALL", "New api call");

                for (Feature restaurant : response.body().getFeatures()) {
                    DocumentReference restaurantRef = db.collection("restaurants").document(restaurant.getId());

                    Restaurant item = new Restaurant(
                            restaurant.getId(),
                            restaurant.getTextFr(),
                            restaurant.getProperties().getAddress(),
                            restaurant.getProperties().getCategory(),
                            restaurant.getCenter().get(0),
                            restaurant.getCenter().get(1),
                            0,
                            0
                    );

                    Map<String, Object> restaurantData = new HashMap<>();
                    restaurantData.put("id", item.getId());
                    restaurantData.put("name", item.getName());
                    restaurantData.put("adress", item.getAdress());
                    restaurantData.put("type", item.getType());
                    restaurantData.put("longitude", item.getLongitude());
                    restaurantData.put("latitude", item.getLatitude());

                    batch.set(restaurantRef, restaurantData, SetOptions.merge());
                    result.add(item);
                }
                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            List<Restaurant> restaurantListUpdate = new ArrayList<>();
                            restaurantListUpdate.addAll(result);

                            for (Restaurant restaurant : restaurantListUpdate) {
                                db.collection("restaurants").document(restaurant.getId())
                                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                if (error != null) {
                                                    return;
                                                }
                                                int restaurantPos = restaurantListUpdate.indexOf(restaurant);
                                                int restaurantSelection = 0;
                                                int restaurantFavorite = 0;
                                                if (value.get("nbSelection") != null) {
                                                    Double restaurantSelectionDouble = value.getDouble("nbSelection");
                                                    restaurantSelection = restaurantSelectionDouble.intValue();
                                                }
                                                if (value.get("nbFavorite") != null) {
                                                    Double restaurantFavoriteDouble = value.getDouble("nbFavorite");
                                                    restaurantFavorite = restaurantFavoriteDouble.intValue();
                                                }
                                                restaurant.setNbFavorite(restaurantFavorite);
                                                restaurant.setNbSelection(restaurantSelection);
                                                restaurantListUpdate.set(restaurantPos, restaurant);
                                                callback.restaurantApiResult(restaurantListUpdate);
                                            }
                                        });
                            }
                        } else {
                            callback.restaurantApiFailure("Error writing restaurant data");
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<RestaurantResponse> call, Throwable t) {
                callback.restaurantApiFailure("Error getting restaurant data");
            }
        });
    }

    // TODO Add to interface
    public void updateRestaurantFavorite(Restaurant restaurant, Boolean update, RestaurantFavoriteQuery callback) {
        Map<String, Object> restaurantData = new HashMap<>();
        restaurantData.put("id", restaurant.getId());
        db.collection("restaurants").document(restaurant.getId()).set(restaurantData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                db.collection("restaurants").document(restaurant.getId()).collection("users").document(currentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            // If favorite is set then we display or remove it
                            if (document.exists()) {
                                // We remove the favorite for the update
                                if (update) {
                                    deleteRestaurantFavorite(restaurant, callback);
                                    // We only display it when we first launch the restaurant activity
                                } else {
                                    callback.getRestaurantFavorite(RestaurantFavoriteResult.CHECKED);
                                }
                                // if user isn't present then we add him to the restaurant favorite
                            } else {
                                if (update) {
                                    addRestaurantFavorite(restaurant, callback);
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    private void addRestaurantFavorite(Restaurant restaurant, RestaurantFavoriteQuery callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", currentUser().getUid());
        db.collection("restaurants").document(restaurant.getId()).collection("users").document(currentUser().getUid()).set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                db.collection("restaurants").document(restaurant.getId()).update("nbFavorite", FieldValue.increment(1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.getRestaurantFavorite(RestaurantFavoriteResult.ADD);
                    }
                });
            }
        });
    }

    private void deleteRestaurantFavorite(Restaurant restaurant, RestaurantFavoriteQuery callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("uid", FieldValue.delete());
        db.collection("restaurants").document(restaurant.getId()).collection("users").document(currentUser().getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                db.collection("restaurants").document(restaurant.getId()).update("nbFavorite", FieldValue.increment(-1)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.getRestaurantFavorite(RestaurantFavoriteResult.DELETE);
                    }
                });
            }
        });
    }
}
