package com.onoma.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.onoma.go4lunch.R;
import com.onoma.go4lunch.databinding.FragmentListBinding;
import com.onoma.go4lunch.databinding.FragmentMapBinding;

public class MapFragment extends Fragment {

    MapView mapview;
    FragmentMapBinding binding;

    public MapFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mapview = binding.mapView;
        mapview.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);

        return view;
    }
}
