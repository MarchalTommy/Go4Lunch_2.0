package com.aki.go4lunchv2.repositories;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aki.go4lunchv2.helpers.UserHelper;
import com.aki.go4lunchv2.models.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class UserRepository {

    private User user = new User();
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private ArrayList<User> userList = new ArrayList<User>();
    private MutableLiveData<List<User>> userListLiveData = new MutableLiveData<>();
    private MutableLiveData<List<User>> onPlaceUsers = new MutableLiveData<>();

    public LiveData<User> getCurrentUser() {
        UserHelper.getCurrentUser().addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                User user = value.toObject(User.class);
                        currentUser.setValue(user);
            }
        });
        return currentUser;
    }

    public void logout(Context context) {
        UserHelper.logout(context);
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return UserHelper.getCurrentUserFirebase();
    }

    public void createUser(String uid, String username, String urlPicture) {
        UserHelper.createUser(uid, username, urlPicture, false, "", "");
    }

    public void createUserInFirestore() {
        if(getCurrentUser().equals(null)){

            String urlPicture = (UserHelper.getCurrentUserFirebase().getPhotoUrl() != null) ? UserHelper.getCurrentUserFirebase().getPhotoUrl().toString() : null;
            String username = UserHelper.getCurrentUserFirebase().getDisplayName();
            String uid = UserHelper.getCurrentUserFirebase().getUid();

            createUser(uid, username, urlPicture);
        }

    }

    public void deleteUser(String uid) {
        UserHelper.deleteUser(uid);
    }

    public void updateUsername(String username, String uid) {
        UserHelper.updateUsername(username, uid);
    }

    public void updateHasBooked(Boolean hasBooked, String uid) {
        UserHelper.updateHasBooked(hasBooked, uid);
    }

    public void updatePlaceBooked(String placeBooked, String uid) {
        UserHelper.updatePlaceBooked(placeBooked, uid);
    }

    public MutableLiveData<List<User>> getAllUsers() {
        UserHelper.getAllUsers().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                userListLiveData.postValue(queryDocumentSnapshots.toObjects(User.class));
            }
        });
        return userListLiveData;
    }

    public User getUser(String uid) {
        UserHelper.getUser(uid).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
            }
        });
        return user;
    }

    public MutableLiveData<List<User>> getUsersOnPlace(String placeId) {
        userList.clear();
        onPlaceUsers.setValue(userList);

        UserHelper.getUserCollection().whereEqualTo("placeBooked", placeId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<User> userList = new ArrayList<>();
                for(DocumentSnapshot ds : value.getDocuments()){
                    userList.add(ds.toObject(User.class));
                }
                onPlaceUsers.setValue(userList);
            }
        });
        return onPlaceUsers;
    }

    public void setLocation(LatLng location) {
        String locationString = location.latitude + "," + location.longitude;
        UserHelper.updateLocation(locationString);
    }
}
