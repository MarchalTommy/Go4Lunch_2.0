package com.aki.go4lunchv2.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.models.User;
import com.google.android.gms.maps.model.LatLng;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<User> currentUser = new MutableLiveData<>();

    private MutableLiveData<LatLng> userLocation = new MutableLiveData<LatLng>();

    private MutableLiveData<Result> restaurant = new MutableLiveData<>();

    private MutableLiveData<LatLng> searchLocation = new MutableLiveData<>();

    //RESTAURANT
    public void setRestaurant(Result input) {
        restaurant.setValue(input);
    }

    public LiveData<Result> getRestaurant() {
        return restaurant;
    }

    //CURRENT USER
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser.setValue(user);
    }

    //CURRENT USER LOCATION
    public void setUserLocation(LatLng location) {
        userLocation.setValue(location);
    }

    public LiveData<LatLng> getUserLocation() {
        return userLocation;
    }

    //Search MAPS LOCATION
    public void setSearchLocation(LatLng location) {
        searchLocation.setValue(location);
    }

    public LiveData<LatLng> getSearchLocation() {
        return searchLocation;
    }
}
