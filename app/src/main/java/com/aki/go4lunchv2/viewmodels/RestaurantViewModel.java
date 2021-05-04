package com.aki.go4lunchv2.viewmodels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.models.ResultDetailed;
import com.aki.go4lunchv2.models.ResultDetails;
import com.aki.go4lunchv2.repositories.RestaurantRepository;

import java.util.ArrayList;

public class RestaurantViewModel extends ViewModel {

    //REPOSITORY
    private final RestaurantRepository placeRepository;

    //MUTABLE LIVE DATA FOR LOCAL CACHING
    private final MutableLiveData<ResultDetailed> localCachedDetails = new MutableLiveData<>();


    public RestaurantViewModel() {
        placeRepository = new RestaurantRepository();
    }

    //GETTING DATA
    public LiveData<ArrayList<Result>> getRestaurantsAround(String location, Context context) {
        return placeRepository.fetchRestaurantsAround(location, context);
    }

    public LiveData<ResultDetails> getRestaurantDetail(String id, Context context) {
        return placeRepository.getRestaurantDetail(id, context);
    }

    public LiveData<Result> getRestaurantFromName(String id, String location, Context context) {
        return placeRepository.getRestaurantFromName(id, location, context);
    }

    public MutableLiveData<ResultDetailed> getLocalCachedDetails() { return localCachedDetails; }

    //SETTING LOCAL DATA

    public void setLocalCachedDetails(ResultDetailed details) {
        localCachedDetails.setValue(details);
    }
}
