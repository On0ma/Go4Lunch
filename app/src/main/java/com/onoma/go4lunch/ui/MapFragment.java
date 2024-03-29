package com.onoma.go4lunch.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.onoma.go4lunch.R;
import com.onoma.go4lunch.databinding.FragmentMapBinding;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.ui.utils.StateData;
import com.onoma.go4lunch.ui.viewModel.RestaurantsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment {

    private MapView mapview;

    FragmentMapBinding binding;

    private RestaurantsViewModel mRestaurantsViewModel;

    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setHasOptionsMenu(true);

        double longitude = getArguments().getDouble("longitude");
        double latitude = getArguments().getDouble("latitude");

        mRestaurantsViewModel = new ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(RestaurantsViewModel.class);
        mapview = binding.mapView;

        final Observer<StateData<List<Restaurant>>> restaurantListObserver = new Observer<StateData<List<Restaurant>>>() {
            @Override
            public void onChanged(StateData<List<Restaurant>> listStateData) {
                switch(listStateData.getStatus()) {
                    case SUCCESS:
                        List<Restaurant> newList = new ArrayList<>();
                        newList.addAll(listStateData.getData());
                        onMapReady(newList, longitude, latitude);
                        break;
                    case ERROR:
                        Log.e("Restaurant list error", listStateData.getError());
                        break;
                }
            }
        };
        mRestaurantsViewModel.getRestaurants(longitude, latitude).observe(getViewLifecycleOwner(), restaurantListObserver);

        binding.mapResetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapview.getMapboxMap().setCamera(new CameraOptions.Builder().center(Point.fromLngLat(longitude,latitude)).build());
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.nav_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem searchIcon = menu.findItem(R.id.nav_search);
        MenuItem sortIcon = menu.findItem(R.id.nav_sort);
        sortIcon.setVisible(false);

        SearchView searchView = (SearchView) searchIcon.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.menu_search_hint));

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

    private void onMapReady(List<Restaurant> restaurants, double longitude, double latitude) {
        CameraOptions cameraOptions = new CameraOptions.Builder().zoom(13.5).center(Point.fromLngLat(longitude,latitude)).build();
        mapview.getMapboxMap().setCamera(cameraOptions);
        mapview.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                addMapAnnotations(restaurants);
            }
        });
    }

    private void addMapAnnotations(List<Restaurant> restaurants) {
        Bitmap icon = drawableToBitmap(AppCompatResources.getDrawable(getContext(), R.drawable.location_pin));
        Bitmap iconSelected = drawableToBitmap(AppCompatResources.getDrawable(getContext(), R.drawable.location_pin_selected));
        AnnotationPlugin annotationApi = AnnotationPluginImplKt.getAnnotations(mapview);
        PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationApi, new AnnotationConfig());
        pointAnnotationManager.deleteAll();
        List<Double> textOffset = new ArrayList<>();
        textOffset.add(0.00);
        textOffset.add(1.00);

        for (Restaurant restaurant : restaurants) {
            if (restaurant.getNbSelection() > 0) {
                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                        .withIconImage(iconSelected)
                        .withTextSize(12.00)
                        .withTextOffset(textOffset)
                        .withPoint(Point.fromLngLat(restaurant.getLongitude(), restaurant.getLatitude()));
                pointAnnotationManager.create(pointAnnotationOptions);
            } else {
                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                        .withIconImage(icon)
                        .withTextSize(12.00)
                        .withTextOffset(textOffset)
                        .withPoint(Point.fromLngLat(restaurant.getLongitude(), restaurant.getLatitude()));
                pointAnnotationManager.create(pointAnnotationOptions);
            }

        }

        pointAnnotationManager.addClickListener(new OnPointAnnotationClickListener() {
            @Override
            public boolean onAnnotationClick(@NonNull PointAnnotation pointAnnotation) {
                Restaurant clickedRestaurant = Restaurant.getRestaurantFromLocation(restaurants, pointAnnotation.getPoint().longitude(), pointAnnotation.getPoint().latitude());
                Intent intent = new Intent(getActivity(), RestaurantActivity.class);
                intent.putExtra("restaurant", clickedRestaurant);
                startActivity(intent);
                return false;
            }
        });
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
