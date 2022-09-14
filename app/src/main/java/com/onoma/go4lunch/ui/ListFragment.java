package com.onoma.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onoma.go4lunch.R;
import com.onoma.go4lunch.databinding.FragmentListBinding;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.ui.utils.StateData;
import com.onoma.go4lunch.ui.viewModel.RestaurantsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListFragment extends Fragment implements RestaurantAdapter.RestaurantDisplayCallback {

    private FragmentListBinding binding;

    private RestaurantsViewModel mRestaurantsViewModel;

    double longitude;
    double latitude;

    RecyclerView recyclerView;

    public ListFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setHasOptionsMenu(true);

        longitude = getArguments().getDouble("longitude");
        latitude = getArguments().getDouble("latitude");

        recyclerView = binding.fragmentListRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RestaurantAdapter adapter = new RestaurantAdapter(longitude, latitude, this);
        recyclerView.setAdapter(adapter);

        // Move the recycler view to the top after the animation (when sorting the list)
        recyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public void onAnimationFinished(RecyclerView.ViewHolder viewHolder) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        mRestaurantsViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(RestaurantsViewModel.class);

        final Observer<StateData<List<Restaurant>>> restaurantListObserver = new Observer<StateData<List<Restaurant>>>() {
            @Override
            public void onChanged(StateData<List<Restaurant>> listStateData) {
                switch (listStateData.getStatus()) {
                    case SUCCESS:
                        List<Restaurant> newList = new ArrayList<>();
                        newList.addAll(listStateData.getData());
                        // TODO Find a better solution
                        // Temporary solution to fix the bug where the recycler view doesn't update while giving an identical list
                        adapter.submitList(null);
                        adapter.submitList(newList);
                        break;
                    case ERROR:
                        Log.e("Restaurant list error", listStateData.getError());
                }
            }
        };
        mRestaurantsViewModel.getRestaurants(longitude, latitude).observe(getViewLifecycleOwner(), restaurantListObserver);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.nav_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchIcon = menu.findItem(R.id.nav_search);
        SearchView searchView = (SearchView) searchIcon.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.menu_search_hint));

        SubMenu subMenu = menu.findItem(R.id.nav_sort).getSubMenu();
        MenuItem sortNewest = subMenu.findItem(R.id.menu_sort_newest);
        MenuItem sortRating = subMenu.findItem(R.id.menu_sort_rating);
        MenuItem sortDistance = subMenu.findItem(R.id.menu_sort_distance);

        // Sort restaurants by name
        sortNewest.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                List<Restaurant> restaurants = new ArrayList<>();
                restaurants.addAll(Objects.requireNonNull(mRestaurantsViewModel.initRestaurants().getValue().getData()));
                mRestaurantsViewModel.sortRestaurantsByName(restaurants);
                return false;
            }
        });

        // Sort restaurants by rating
        sortRating.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                List<Restaurant> restaurants = new ArrayList<>();
                restaurants.addAll(Objects.requireNonNull(mRestaurantsViewModel.initRestaurants().getValue().getData()));
                mRestaurantsViewModel.sortRestaurantsByRating(restaurants);
                return false;
            }
        });

        // Sort restaurants by distance
        sortDistance.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                List<Restaurant> restaurants = new ArrayList<>();
                restaurants.addAll(Objects.requireNonNull(mRestaurantsViewModel.initRestaurants().getValue().getData()));
                mRestaurantsViewModel.sortRestaurantsByDistance(restaurants, longitude, latitude);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                List<Restaurant> restaurants = new ArrayList<>();
                restaurants.addAll(Objects.requireNonNull(mRestaurantsViewModel.initRestaurants().getValue().getData()));
                mRestaurantsViewModel.getRestaurantsFromSearch(s, restaurants);
                return false;
            }
        });
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
}
