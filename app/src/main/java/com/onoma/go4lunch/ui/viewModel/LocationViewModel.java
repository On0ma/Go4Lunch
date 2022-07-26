package com.onoma.go4lunch.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.onoma.go4lunch.model.UserLocation;
import com.onoma.go4lunch.ui.repository.LocationRepository;
import com.onoma.go4lunch.ui.repository.UserRepository;

public class LocationViewModel extends ViewModel {
    private final LocationRepository mLocationRepository;

    private MutableLiveData<UserLocation> userLocation = new MutableLiveData<>();

    public LocationViewModel() {
        mLocationRepository = LocationRepository.getInstance();
    }

    public LiveData<UserLocation> getPosition(FusedLocationProviderClient locationProvider) {
        mLocationRepository.getLastLocation(locationProvider, new LocationRepository.LocationQuery() {
            @Override
            public void onLocationQuerySuccess(double longitude, double latitude) {
                UserLocation newLoc = new UserLocation(
                        longitude,
                        latitude
                );
                userLocation.setValue(newLoc);
            }
        });
        return userLocation;
    }
}
