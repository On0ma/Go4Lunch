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

import com.onoma.go4lunch.databinding.FragmentListBinding;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.ui.viewModel.RestaurantsViewModel;

import java.util.List;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;

    private RestaurantsViewModel mRestaurantsViewModel;

    public ListFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        RecyclerView recyclerView = binding.fragmentListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RestaurantAdapter adapter = new RestaurantAdapter();
        recyclerView.setAdapter(adapter);

        mRestaurantsViewModel = new ViewModelProvider(requireActivity()).get(RestaurantsViewModel.class);
        mRestaurantsViewModel.getRestaurants().observe(getViewLifecycleOwner(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                adapter.submitList(restaurants);
                Log.i(null, String.valueOf(restaurants));
                Log.i("Restaurants Observer", "CHANGED");
                Log.i(null, String.valueOf(adapter.getItemCount()));
            }
        });
        // List<RestaurantResponse> restaurantResponseList = mRestaurantsViewModel.getRestaurants();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
