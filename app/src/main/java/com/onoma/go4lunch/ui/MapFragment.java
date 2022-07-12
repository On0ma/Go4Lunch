package com.onoma.go4lunch.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.locationcomponent.LocationProvider;
import com.mapbox.search.CategorySearchOptions;
import com.mapbox.search.MapboxSearchSdk;
import com.mapbox.search.ResponseInfo;
import com.mapbox.search.SearchCallback;
import com.mapbox.search.SearchEngine;
import com.mapbox.search.SearchRequestTask;
import com.mapbox.search.result.SearchResult;
import com.onoma.go4lunch.R;
import com.onoma.go4lunch.databinding.FragmentListBinding;
import com.onoma.go4lunch.databinding.FragmentMapBinding;
import com.onoma.go4lunch.ui.viewModel.RestaurantsViewModel;

import java.util.List;

public class MapFragment extends Fragment {

    private PermissionsManager permissionsManager;
    private MapView mapview;
    private MapboxMap mapboxMap;

    FragmentMapBinding binding;

    private RestaurantsViewModel mRestaurantsViewModel;

    public MapFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        mRestaurantsViewModel = new ViewModelProvider(requireActivity()).get(RestaurantsViewModel.class);

        mapview = binding.mapView;
        mapview.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);

        mRestaurantsViewModel.getRestaurants();

        return view;
    }
}
