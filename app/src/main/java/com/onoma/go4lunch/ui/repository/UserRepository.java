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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.onoma.go4lunch.model.User;

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

    public void getAllUsers() {
        getUsersCollection().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(null, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d(null,"Error getting documents: ", task.getException());
                }
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

}
