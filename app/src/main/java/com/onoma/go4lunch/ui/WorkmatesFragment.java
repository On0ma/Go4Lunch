package com.onoma.go4lunch.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.onoma.go4lunch.R;
import com.onoma.go4lunch.databinding.FragmentWorkmatesBinding;
import com.onoma.go4lunch.model.User;
import com.onoma.go4lunch.ui.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesFragment extends Fragment {

    FragmentWorkmatesBinding binding;

    private UserViewModel mUserViewModel;

    public WorkmatesFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mUserViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // mUserViewModel.getAllUsers();

        List<User> allUsers = new ArrayList<>();

        RecyclerView recyclerView = binding.fragmentWorkmatesRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        WorkmateAdapter adapter= new WorkmateAdapter();
        recyclerView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.i("ALL USERS SUCCESS", document.getId() + " => " + document.getData());
                        User newUser = new User(
                                document.getString("uid"),
                                document.getString("name"),
                                document.getString("email"),
                                document.getString("photoUrl")
                        );
                        allUsers.add(newUser);
                    }
                    // User adapter
                    adapter.submitList(allUsers);
                } else {
                    Log.i("ALL USERS ERROR","Error getting documents: ", task.getException());
                }
            }
        });

        return view;
    }
}
