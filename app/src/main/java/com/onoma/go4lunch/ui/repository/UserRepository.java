package com.onoma.go4lunch.ui.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    private static volatile UserRepository instance;

    private static final String COLLECTION_NAME = "users";

    private UserRepository() { }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public void createUser() {
        FirebaseUser user = getCurrentUser();
        Log.i("CREATE USER", String.valueOf(user));
        if (user != null) {
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String email = user.getEmail();
            String uid = user.getUid();

            User userToCreate = new User(uid, username, email, urlPicture);

            // Task<DocumentSnapshot> userData = getUserData();

            getUsersCollection().document(uid).get().addOnSuccessListener(documentSnapshot -> {
                this.getUsersCollection().document(uid).set(userToCreate);
            });
        }
    }

    public void getUserData(UserQuery callback) {
        String uid = getCurrentUser().getUid();
        Log.i("USER UID", uid);
        if (uid != null) {
            // return this.getUsersCollection().document(uid).get();
            this.getUsersCollection().document(uid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.i("SUCCESS USER CALLBACK", "user data");
                            callback.getUserSuccess(documentSnapshot);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("ERROR USER CALLBACK", "error getting user", e);
                            callback.getUserFailure("error getting user data");
                        }
                    });
        } else {
            callback.getUserFailure("error getting user data");
            // return null;
        }
    }

    public void getAllUsers(AllUsersQuery callback) {
        getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    callback.getAllUsersSuccess(task.getResult());

                } else {
                    callback.getAllUsersFailure("Error getting documents: " + task.getException());
                }
            }
        });
    }

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
        getUsersCollection().document(getCurrentUser().getUid()).set(data, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                callback.getRestaurantSelection(RestaurantSelectionResult.ADD);
            }
        });
    }

    private void deleteRestaurantSelection(Restaurant restaurant, RestaurantSelectionQuery callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("restaurantSelection", FieldValue.delete());
        getUsersCollection().document(getCurrentUser().getUid()).update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                callback.getRestaurantSelection(RestaurantSelectionResult.DELETE);
            }
        });
    }

    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public interface UserQuery {
        void getUserSuccess(DocumentSnapshot userDocument);
        void getUserFailure(String error);
    }

    public interface AllUsersQuery {
        void getAllUsersSuccess(QuerySnapshot results);
        void getAllUsersFailure(String error);
    }

    public interface RestaurantSelectionQuery {
        void getRestaurantSelection(RestaurantSelectionResult result);
    }

    public enum RestaurantSelectionResult {
        ADD,
        DELETE,
        CHECKED
    }
}
