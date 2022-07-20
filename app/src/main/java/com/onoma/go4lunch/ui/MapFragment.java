package com.onoma.go4lunch.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.onoma.go4lunch.R;
import com.onoma.go4lunch.databinding.FragmentMapBinding;
import com.onoma.go4lunch.ui.viewModel.RestaurantsViewModel;

import java.util.List;

public class MapFragment extends Fragment {

    private MapView mapview;
    private PermissionsManager permissionsManager;

    FragmentMapBinding binding;

    private RestaurantsViewModel mRestaurantsViewModel;

    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mRestaurantsViewModel = new ViewModelProvider(requireActivity()).get(RestaurantsViewModel.class);

        mapview = binding.mapView;

        onMapReady();

        mRestaurantsViewModel.getRestaurants();

        return view;
    }

    private void onMapReady() {
        CameraOptions cameraOptions = new CameraOptions.Builder().zoom(13.5).center(Point.fromLngLat(2.356526,48.831351)).build();
        mapview.getMapboxMap().setCamera(cameraOptions);
        mapview.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);
    }

    /*private Style.OnStyleLoaded initLocationComponent() {
        LocationComponentPlugin locationComponentPlugin = mapview.getPlugin("location");
        locationComponentPlugin.updateSettings(
                locationComponentPlugin.setEnabled(true),
                locationComponentPlugin.setLocationPuck(
                        new LocationPuck2D(
                                null,
                                AppCompatResources.getDrawable(getActivity(), R.drawable.ic_baseline_my_location_24),
                                null,
                                null

                        )
                )
        );
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
