package com.onoma.go4lunch.ui.viewModel;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.ui.repository.RestaurantRepository;
import com.onoma.go4lunch.ui.repository.RestaurantRepositoryImpl;
import com.onoma.go4lunch.ui.utils.StateData;
import com.onoma.go4lunch.ui.utils.StateLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantsViewModel extends ViewModel {
    private final RestaurantRepositoryImpl mRestaurantRepository;

    private MutableLiveData<List<Restaurant>> restaurantsLiveData;
    private MutableLiveData<RestaurantRepositoryImpl.RestaurantFavoriteResult> restaurantFavoriteLiveData = new MutableLiveData<>();
    private StateLiveData<List<Map<String, Object>>> restaurantListenerLiveData = new StateLiveData<>();
    public Map<String, Double> RestaurantListenerData = new HashMap<>();

    private List<Restaurant> restaurantApiList = new ArrayList<>();

    public RestaurantsViewModel() {
        mRestaurantRepository = RestaurantRepositoryImpl.getInstance();
    }

    public LiveData<List<Restaurant>> initRestaurants() {
        return restaurantsLiveData;
    }

    public LiveData<List<Restaurant>> getRestaurants(double longitude, double latitude) {
        if (restaurantsLiveData == null) {
            restaurantsLiveData = new MutableLiveData<>();
            loadRestaurants(longitude, latitude);
        }
        return  restaurantsLiveData;
    }

    public void getRestaurantsFromSearch(String query, List<Restaurant> restaurants) {
        /*mRestaurantRepository.getRestaurantsFromQuery(query, new RestaurantRepository.RestaurantQuery() {
            @Override
            public void restaurantApiResult(List<Restaurant> restaurants) {
                restaurantsLiveData.setValue(restaurants);
            }

            @Override
            public void restaurantApiFailure(String error) {
                // TODO Handle error
            }
        });*/

        if (restaurantApiList.isEmpty()) {
            return;
        }
        Log.i("LISTE INITIALE", String.valueOf(restaurants));
        List<Restaurant> restaurantFiltered = new ArrayList<>();
        for (Restaurant restaurant : restaurantApiList) {
            if (restaurant.getName().contains(query)) {
                restaurantFiltered.add(restaurant);
            }
        }
        Log.i("RESTAURANT FILTRE", String.valueOf(restaurantFiltered));
        restaurantsLiveData.setValue(restaurantFiltered);
    }

    private void loadRestaurants(double longitude, double latitude) {
        mRestaurantRepository.getRestaurants(longitude, latitude, new RestaurantRepositoryImpl.RestaurantQuery() {
            @Override
            public void restaurantApiResult(List<Restaurant> restaurants) {
                restaurantApiList.addAll(restaurants);
                List<Restaurant> restaurantListUpdate = new ArrayList<>();
                restaurantListUpdate.addAll(restaurants);

                for (Restaurant restaurant : restaurantListUpdate) {
                    FirebaseFirestore.getInstance().collection("restaurants").document(restaurant.getId())
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Log.i("ERROR", "Error getting data", error);
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
                                    restaurantsLiveData.setValue(restaurantListUpdate);
                                }
                            });
                }
                restaurantsLiveData.setValue(restaurantListUpdate);
            }

            @Override
            public void restaurantApiFailure(String error) {
                Log.i("TAG", error);
            }
        });
    }

    public void initRestaurantFavorite(Restaurant restaurant, Boolean update) {
        loadRestaurantFavorite(restaurant, update);
    }

    public LiveData<RestaurantRepositoryImpl.RestaurantFavoriteResult> getRestaurantFavorite() {
        return restaurantFavoriteLiveData;
    }

    private void loadRestaurantFavorite(Restaurant restaurant, Boolean update) {
        mRestaurantRepository.updateRestaurantFavorite(restaurant, update, new RestaurantRepositoryImpl.RestaurantFavoriteQuery() {
            @Override
            public void getRestaurantFavorite(RestaurantRepositoryImpl.RestaurantFavoriteResult result) {
                restaurantFavoriteLiveData.setValue(result);
            }
        });
    }

    public Map<String, Double> getRestaurantTestListener(Restaurant restaurant) {
        return mRestaurantRepository.restaurantListenerTest(restaurant);
    }

    public LiveData<StateData<List<Map<String, Object>>>> getRestaurantListener() {
        loadRestaurantListener();
        return restaurantListenerLiveData;
    }

    private void loadRestaurantListener() {
        mRestaurantRepository.restaurantListener(new RestaurantRepositoryImpl.RestaurantListenerQuery() {
            @Override
            public void restaurantListenerResult(List<Map<String, Object>> result) {
                restaurantListenerLiveData.postSuccess(result);
            }

            @Override
            public void restaurantListenerFailure(String error) {
                restaurantListenerLiveData.postError(error);
            }
        });
    }
}
