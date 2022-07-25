package com.onoma.go4lunch.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
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
import androidx.lifecycle.Observer;
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
import com.mapbox.maps.plugin.annotation.Annotation;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationManager;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.AnnotationType;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions;
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.onoma.go4lunch.R;
import com.onoma.go4lunch.databinding.FragmentMapBinding;
import com.onoma.go4lunch.model.Restaurant;
import com.onoma.go4lunch.ui.viewModel.RestaurantsViewModel;

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

        double longitude = getArguments().getDouble("longitude");
        double latitude = getArguments().getDouble("latitude");

        mRestaurantsViewModel = new ViewModelProvider(requireActivity()).get(RestaurantsViewModel.class);
        mapview = binding.mapView;

        final Observer<List<Restaurant>> restaurantListObserver = new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                Log.i("BEFORE", String.valueOf(restaurants));
                onMapReady(restaurants, longitude, latitude);
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
        Bitmap icon = drawableToBitmap(AppCompatResources.getDrawable(getContext(), R.drawable.ic_baseline_location_on_24));
        AnnotationPlugin annotationApi = AnnotationPluginImplKt.getAnnotations(mapview);
        PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationApi, new AnnotationConfig());

        for (Restaurant restaurant : restaurants) {
            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                    .withIconImage(icon)
                    .withPoint(Point.fromLngLat(restaurant.getLongitude(), restaurant.getLatitude()));
            pointAnnotationManager.create(pointAnnotationOptions);
        }

        pointAnnotationManager.addClickListener(new OnPointAnnotationClickListener() {
            @Override
            public boolean onAnnotationClick(@NonNull PointAnnotation pointAnnotation) {
                Log.i("ANNOTATION CLICK", String.valueOf(pointAnnotation.getPoint().longitude()));
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
