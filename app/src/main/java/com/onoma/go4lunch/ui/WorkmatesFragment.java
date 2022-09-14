package com.onoma.go4lunch.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onoma.go4lunch.databinding.FragmentWorkmatesBinding;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.model.User;
import com.onoma.go4lunch.ui.utils.StateData;
import com.onoma.go4lunch.ui.viewModel.RestaurantsViewModel;
import com.onoma.go4lunch.ui.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesFragment extends Fragment {

    FragmentWorkmatesBinding binding;

    private UserViewModel mUserViewModel;
    private RestaurantsViewModel mRestaurantsViewModel;

    private List<Restaurant> restaurantList = new ArrayList<>();

    public WorkmatesFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setHasOptionsMenu(true);

        double longitude = getArguments().getDouble("longitude");
        double latitude = getArguments().getDouble("latitude");

        mUserViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(UserViewModel.class);
        mRestaurantsViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(RestaurantsViewModel.class);

        RecyclerView recyclerView = binding.fragmentWorkmatesRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.addAll(mRestaurantsViewModel.initRestaurants().getValue().getData());

        WorkmateAdapter adapter= new WorkmateAdapter(restaurantList);
        recyclerView.setAdapter(adapter);

        final Observer<StateData<List<User>>> usersListObserver = new Observer<StateData<List<User>>>() {
            @Override
            public void onChanged(StateData<List<User>> listStateData) {
                switch (listStateData.getStatus()) {
                    case SUCCESS:
                        adapter.submitList(listStateData.getData());
                        break;
                    case ERROR:
                        Log.e(null, listStateData.getError());
                }
            }
        };
        mUserViewModel.getAllUsers().observe(getViewLifecycleOwner(), usersListObserver);

        final Observer<StateData<List<Restaurant>>> restaurantListObserver = new Observer<StateData<List<Restaurant>>>() {
            @Override
            public void onChanged(StateData<List<Restaurant>> listStateData) {
                switch(listStateData.getStatus()) {
                    case SUCCESS:

                        break;
                    case ERROR:
                        Log.e("Restaurant list error", listStateData.getError());
                        break;
                }
            }
        };
        mRestaurantsViewModel.getRestaurants(longitude, latitude).observe(getViewLifecycleOwner(), restaurantListObserver);

        return view;
    }
}
