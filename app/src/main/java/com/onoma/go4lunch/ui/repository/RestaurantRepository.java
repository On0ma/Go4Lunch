package com.onoma.go4lunch.ui.repository;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.api.tilequery.MapboxTilequery;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.search.CategorySearchOptions;
import com.mapbox.search.MapboxSearchSdk;
import com.mapbox.search.ResponseInfo;
import com.mapbox.search.SearchCallback;
import com.mapbox.search.SearchEngine;
import com.mapbox.search.SearchRequestTask;
import com.mapbox.search.result.SearchResult;
import com.onoma.go4lunch.R;
import com.onoma.go4lunch.data.RestaurantApi;
import com.onoma.go4lunch.data.RetroFitService;
import com.onoma.go4lunch.model.Feature;
import com.onoma.go4lunch.model.RestaurantResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private static volatile RestaurantRepository instance;

    private final double LONGITUDE = 2.356526;
    private final double LATITUDE = 48.831351;

    private final String queryAcessToken = "pk.eyJ1IjoiZW56by1vIiwiYSI6ImNsNTl3c3N3aDEzcmwzZG5zc2J6eGlsMGIifQ.ukP47h-0lgGwCsWKs2aXsg";
    private final String queryCountry = "fr";
    private final String queryLanguage = "fr";
    private final int queryLimit = 10;
    private final String queryProximity = LONGITUDE + "," + LATITUDE;
    private final String queryTypes = "poi";

    private final RestaurantApi restaurantApi;

    public RestaurantRepository() {
        restaurantApi = RetroFitService.getRestaurantApi();
    }

    public static RestaurantRepository getInstance() {
        RestaurantRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (RestaurantRepository.class) {
            if (instance == null) {
                instance = new RestaurantRepository();
            }
            return instance;
        }
    }

    private List<Feature> RestaurantListData = new ArrayList<>();

    public List<Feature> getRestaurants() {
        restaurantApi.getRestaurantList(queryAcessToken, queryCountry, queryLanguage, queryLimit, queryProximity, queryTypes).enqueue(new Callback<RestaurantResponse>() {
            @Override
            public void onResponse(Call<RestaurantResponse> call, Response<RestaurantResponse> response) {
                if (response.isSuccessful()) {
                    RestaurantListData.addAll(response.body().getFeatures());
                    Log.i("RESULT", String.valueOf(response.body().getFeatures()));
                } else {
                    Log.i("API RESULT", "Something went wrong");
                }
            }

            @Override
            public void onFailure(Call<RestaurantResponse> call, Throwable t) {
                Log.e("TAG", "Error occured", t);
            }
        });
        return RestaurantListData;
    }

    /*public void makeTilequeryApiCall() {
        MapboxTilequery tilequery = MapboxTilequery.builder()
                .accessToken("pk.eyJ1IjoiZW56by1vIiwiYSI6ImNsNTl3c3N3aDEzcmwzZG5zc2J6eGlsMGIifQ.ukP47h-0lgGwCsWKs2aXsg")
                .tilesetIds("mapbox.mapbox-streets-v8")
                .query(Point.fromLngLat(LONGITUDE, LATITUDE))
                .radius(500)
                .limit(20)
                .geometry("polygon")
                .dedupe(true)
                .layers("building")
                .build();

        tilequery.enqueueCall(new Callback<FeatureCollection>() {
            @Override
            public void onResponse(Call<FeatureCollection> call, Response<FeatureCollection> response) {
                responseResult = response.body();
                Log.i("API RESULT", String.valueOf(responseResult));
            }

            @Override
            public void onFailure(Call<FeatureCollection> call, Throwable t) {
                Toast.makeText(RestaurantRepository.this, "Api error", Toast.LENGTH_SHORT).show();
            }
        });
    }*/




}
