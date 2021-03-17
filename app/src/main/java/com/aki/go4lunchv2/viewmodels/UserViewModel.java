package com.aki.go4lunchv2.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aki.go4lunchv2.models.User;
import com.aki.go4lunchv2.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UserViewModel extends ViewModel {

    //REPOSITORY
    private final UserRepository userRepository = new UserRepository();

    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<User> user = new MutableLiveData<>();

    private User currentUserPOJO = new User();

    //GETTING DATA
    public MutableLiveData<User> getCurrentUser() {
        currentUser.setValue(userRepository.getCurrentUser());
        return currentUser;
    }

    public User getCurrentUserPOJO() {
        return currentUserPOJO = userRepository.getCurrentUser();
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return userRepository.getCurrentFirebaseUser();
    }

    public MutableLiveData<List<User>> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public MutableLiveData<User> getUserById(String uid) {
        user.setValue(userRepository.getUser(uid));
        return user;
    }

    public MutableLiveData<List<User>> getUsersOnPlace(String placeName) {
        return userRepository.getUsersOnPlace(placeName);
    }

    //UPDATE DATA
    public void createCurrentUserInFirestore() {
        userRepository.createUserInFirestore();
    }

    public void createUser(String uid, String userName, String urlPicture) {
        userRepository.createUser(uid, userName, urlPicture);
    }

    public void deleteUser() {
        userRepository.deleteUser(getCurrentFirebaseUser().getUid());
    }

    public void updateUsername(String username) {
        userRepository.updateUsername(username, getCurrentFirebaseUser().getUid());
    }

    public void updateHasBooked(Boolean hasBooked) {
        userRepository.updateHasBooked(hasBooked, getCurrentFirebaseUser().getUid());
    }

    public void updatePlaceBooked(String placeBooked) {
        userRepository.updatePlaceBooked(placeBooked, getCurrentFirebaseUser().getUid());
    }
}
