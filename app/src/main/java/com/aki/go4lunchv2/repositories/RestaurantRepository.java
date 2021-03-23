package com.aki.go4lunchv2.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.helpers.RestaurantCalls;
import com.aki.go4lunchv2.models.Restaurant;
import com.aki.go4lunchv2.models.Result;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class RestaurantRepository {

    private ArrayList<Result> restaurantsAround = new ArrayList<>();
    private MutableLiveData<ArrayList<Result>> resultLiveData = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Result>> fetchRestaurantsAround(String location, Context context) {
        RestaurantCalls.fetchRestaurantsAround(new RestaurantCalls.Callbacks() {
            @Override
            public void onResponse(@Nullable JsonObject jsonObject) {
                Log.d(TAG, "onResponse: RESTAURANTS HAS BEEN FOUND");
                restaurantsAround.clear();
                if (jsonObject != null) {
                    Gson gson = new Gson();
                    Restaurant restaurant;
                    restaurant = gson.fromJson(jsonObject, Restaurant.class);

                    for (Result r : restaurant.getResults()) {
                        if (r.getTypes().get(0).equals("restaurant") && !r.isPermanentlyClosed()) {
                            restaurantsAround.add(r);
                        }
                    }
                    resultLiveData.postValue(restaurantsAround);
                }
            }

            @Override
            public void onFailure() {
                Log.d(TAG, "onFailure: RESTAURANTS NOT FOUND");
                resultLiveData.postValue(null);
            }
        }, location, context);

        return resultLiveData;
    }
}
