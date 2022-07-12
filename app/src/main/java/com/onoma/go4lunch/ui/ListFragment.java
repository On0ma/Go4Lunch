package com.onoma.go4lunch.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.onoma.go4lunch.R;
import com.onoma.go4lunch.ViewModelFactory;
import com.onoma.go4lunch.databinding.FragmentListBinding;
import com.onoma.go4lunch.model.RestaurantResponse;
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

        mRestaurantsViewModel = new ViewModelProvider(requireActivity()).get(RestaurantsViewModel.class);
        // List<RestaurantResponse> restaurantResponseList = mRestaurantsViewModel.getRestaurants();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
