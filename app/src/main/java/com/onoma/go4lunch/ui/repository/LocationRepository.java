package com.onoma.go4lunch.ui.repository;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;

public class LocationRepository {

    private static volatile LocationRepository instance;

    private LocationRepository() {
    }

    public static LocationRepository getInstance() {
        LocationRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (LocationRepository.class) {
            if (instance == null) {
                instance = new LocationRepository();
            }
            return instance;
        }
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation(FusedLocationProviderClient locationProvider, LocationQuery callback) {
        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        locationProvider.getCurrentLocation(priority, new CancellationToken() {
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }

            @Override
            public boolean isCancellationRequested() {
                return false;
            }
        }).addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    callback.onLocationQuerySuccess(location.getLongitude(), location.getLatitude());
                }
            }
        });
    }

    public interface LocationQuery {
        void onLocationQuerySuccess(double longitude, double latitude);
    }
}
