package com.aki.go4lunchv2.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.models.User;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<User> currentUser = new MutableLiveData<>();

    private MutableLiveData<Result> restaurant = new MutableLiveData<>();

    public void setRestaurant(Result input) {
        restaurant.setValue(input);
    }

    public LiveData<Result> getRestaurant() {
        return restaurant;
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser.setValue(user);
    }
}
