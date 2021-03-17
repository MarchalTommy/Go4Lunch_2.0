package com.aki.go4lunchv2.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.repositories.RestaurantRepository;

import java.util.ArrayList;

public class RestaurantViewModel extends ViewModel {

    //REPOSITORY
    private RestaurantRepository placeRepository;


    public RestaurantViewModel() {
        placeRepository = new RestaurantRepository();
    }

    //INITIALIZING
    public void initPlace(Context context) {
        placeRepository.initPlaces(context);
    }

    //GETTING DATA
    public LiveData<ArrayList<Result>> getRestaurantsAround(String location, Context context) {
        return placeRepository.fetchRestaurantsAround(location, context);
    }
}
