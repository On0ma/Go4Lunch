package com.onoma.go4lunch.ui.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepositoryImpl implements UserRepository {

    private static volatile UserRepositoryImpl instance;

    private static final String COLLECTION_NAME = "users";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private UserRepositoryImpl() { }

    public static UserRepositoryImpl getInstance() {
        UserRepositoryImpl result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepositoryImpl.class) {
            if (instance == null) {
                instance = new UserRepositoryImpl();
            }
            return instance;
        }
    }

    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public void createUser() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String email = user.getEmail();
            String uid = user.getUid();

            User userToCreate = new User(uid, username, email, urlPicture);

            getUsersCollection().document(uid).get().addOnSuccessListener(documentSnapshot -> {
                this.getUsersCollection().document(uid).set(userToCreate);
            });
        }
    }

    public void getUserData(UserQuery callback) {
        String uid = getCurrentUser().getUid();
        if (uid != null) {
            this.getUsersCollection().document(uid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User userData = new User(
                                    documentSnapshot.getString("uid"),
                                    documentSnapshot.getString("name"),
                                    documentSnapshot.getString("email"),
                                    documentSnapshot.getString("photoUrl")
                            );
                            callback.getUserSuccess(userData);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.getUserFailure("error getting user data");
                        }
                    });
        } else {
            callback.getUserFailure("error getting user data");
        }
    }

    public void getAllUsers(AllUsersQuery callback) {
        getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User newUser = new User(
                                document.getString("uid"),
                                document.getString("name"),
                                document.getString("email"),
                                document.getString("photoUrl")
                        );
                        userList.add(newUser);
                    }
                    callback.getAllUsersSuccess(userList);

                } else {
                    callback.getAllUsersFailure("Error getting documents: " + task.getException());
                }
            }
        });
    }

    public void getAllUsersFromRestaurant(Restaurant restaurant, AllUsersQuery callback) {
        getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if ((document.getString("restaurantSelection") != null) && (document.getString("restaurantSelection").equals(restaurant.getId()))) {
                            User newUser = new User(
                                    document.getString("uid"),
                                    document.getString("name"),
                                    document.getString("email"),
                                    document.getString("photoUrl")
                            );
                            userList.add(newUser);
                        }
                    }
                    callback.getAllUsersSuccess(userList);

                } else {
                    callback.getAllUsersFailure("Error getting documents: " + task.getException());
                }
            }
        });
    }

    // TODO remove boolean and update methods
    // TODO Add method to interface
    public void updateRestaurantSelection(Restaurant restaurant, Boolean update, RestaurantSelectionQuery callback) {
        getUsersCollection().document(getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if ((document.getString("restaurantSelection") != null) && (document.getString("restaurantSelection").equals(restaurant.getId()))) {
                            if (update) {
                                deleteRestaurantSelection(restaurant, callback);
                            } else {
                                callback.getRestaurantSelection(RestaurantSelectionResult.CHECKED);
                            }
                        } else {
                            if (update) {
                                addRestaurantSelection(restaurant, callback);
                            }
                        }
                    }
                }
            }
        });
    }

    private void addRestaurantSelection(Restaurant restaurant, RestaurantSelectionQuery callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("restaurantSelection", restaurant.getId());
        db.collection("users").document(getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                WriteBatch batch = db.batch();
                // If we already have a selection we need to remove it from the restaurant collection
                if (documentSnapshot.contains("restaurantSelection")) {
                    // Decrement old Selection from the Restaurant document
                    Map<String, Object> restaurantDelete = new HashMap<>();
                    batch.update(
                            db.collection("restaurants").document(documentSnapshot.getString("restaurantSelection")),
                            "nbSelection",
                            FieldValue.increment(-1)
                    );
                }
                // Update user field for restaurant selection
                batch.set(
                        getUsersCollection().document(getCurrentUser().getUid()),
                        data,
                        SetOptions.merge()
                );
                // increment restaurant field for selection number
                batch.update(
                        db.collection("restaurants").document(restaurant.getId()),
                        "nbSelection",
                        FieldValue.increment(1)
                );
                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callback.getRestaurantSelection(RestaurantSelectionResult.ADD);
                        }
                    }
                });
            }
        });
    }

    private void deleteRestaurantSelection(Restaurant restaurant, RestaurantSelectionQuery callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("restaurantSelection", FieldValue.delete());
        getUsersCollection().document(getCurrentUser().getUid()).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    db.collection("restaurants").document(restaurant.getId()).update("nbSelection", FieldValue.increment(-1)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                callback.getRestaurantSelection(RestaurantSelectionResult.DELETE);
                            }
                        }
                    });
                }
            }
        });
    }

    public void getCurrentUserSelection(UserSelectionQuery callback) {
        getUsersCollection().document(getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String restaurantSelection = task.getResult().getString("restaurantSelection");
                    if (restaurantSelection != null) {
                        db.collection("restaurants").document(restaurantSelection).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot result = task.getResult();
                                    int resultSelection = 0;
                                    int resultFavorite = 0;
                                    if (result.getDouble("restaurantSelection") != null) {
                                        resultSelection = result.getDouble("restaurantSelection").intValue();
                                    }
                                    if (result.getDouble("restaurantFavorite") != null) {
                                        resultFavorite = result.getDouble("restaurantFavorite").intValue();
                                    }
                                    Restaurant restaurantSelected = new Restaurant(
                                            restaurantSelection,
                                            result.getString("name"),
                                            result.getString("adress"),
                                            result.getString("type"),
                                            result.getDouble("longitude"),
                                            result.getDouble("latitude"),
                                            resultSelection,
                                            resultFavorite
                                    );
                                    callback.getUserSelectionSuccess(restaurantSelected);
                                } else {
                                    callback.getUserSelectionFailure("Error getting restaurant data");
                                }
                            }
                        });
                    } else {
                        callback.getUserSelectionFailure("No restaurant selected");
                    }
                }
            }
        });
    }

    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    @Nullable
    private FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Boolean getIsUserLoggedIn() {
        return getCurrentUser() != null;
    }
}
