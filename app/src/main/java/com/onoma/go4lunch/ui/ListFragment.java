package com.onoma.go4lunch.ui;

import android.content.Intent;
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

import com.onoma.go4lunch.databinding.FragmentListBinding;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.ui.utils.StateData;
import com.onoma.go4lunch.ui.viewModel.RestaurantsViewModel;
import com.onoma.go4lunch.ui.viewModel.UserViewModel;

import java.util.List;

public class ListFragment extends Fragment implements RestaurantAdapter.RestaurantDisplayCallback {

    private FragmentListBinding binding;

    private RestaurantsViewModel mRestaurantsViewModel;
    private UserViewModel mUserViewModel;

    public ListFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        double longitude = getArguments().getDouble("longitude");
        double latitude = getArguments().getDouble("latitude");

        RecyclerView recyclerView = binding.fragmentListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RestaurantAdapter adapter = new RestaurantAdapter(longitude, latitude, this);
        recyclerView.setAdapter(adapter);

        mRestaurantsViewModel = new ViewModelProvider(requireActivity()).get(RestaurantsViewModel.class);
        mUserViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        mRestaurantsViewModel.getRestaurants(longitude, latitude).observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                Log.i(null, String.valueOf(restaurants));
                adapter.submitList(restaurants);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRestaurantClick(Restaurant restaurant) {
        Intent intent = new Intent(getActivity(), RestaurantActivity.class);
        intent.putExtra("restaurant", restaurant);
        startActivity(intent);
    }

    @Override
    public void onRestaurantUpdate(Restaurant restaurant) {
        mUserViewModel.getUsersChoice(restaurant).observe(getViewLifecycleOwner(), new Observer<StateData<Integer>>() {
            @Override
            public void onChanged(StateData<Integer> integerStateData) {
                switch (integerStateData.getStatus()) {
                    case SUCCESS:
                        restaurant.setUsersChoice(integerStateData.getData());
                        Log.i("RESTAURANT CHOICES", String.valueOf(integerStateData.getData()));
                        break;
                    case ERROR:
                        Log.i("ERROR", integerStateData.getError());
                        break;
                }
            }
        });
    }
}
