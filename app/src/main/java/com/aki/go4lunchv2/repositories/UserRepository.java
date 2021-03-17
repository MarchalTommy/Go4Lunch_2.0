package com.aki.go4lunchv2.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.aki.go4lunchv2.helpers.UserHelper;
import com.aki.go4lunchv2.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class UserRepository {

    private User user = new User();
    private ArrayList<User> userList = new ArrayList<User>();
    private MutableLiveData<List<User>> userListLiveData = new MutableLiveData<>();

    public User getCurrentUser() {
        UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot ->
                user = documentSnapshot.toObject(User.class));
        Log.d(TAG, "getCurrentUser: ");
        return user;
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return UserHelper.getCurrentUser();
    }

    public void createUser(String uid, String username, String urlPicture) {
        UserHelper.createUser(uid, username, urlPicture, false, "");
    }

    public void createUserInFirestore() {
        if (UserHelper.getCurrentUser() != null) {
            String urlPicture = (UserHelper.getCurrentUser().getPhotoUrl() != null) ? UserHelper.getCurrentUser().getPhotoUrl().toString() : null;
            String username = UserHelper.getCurrentUser().getDisplayName();
            String uid = UserHelper.getCurrentUser().getUid();

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

    public MutableLiveData<List<User>> getUsersOnPlace(String placeName) {
        userList.clear();
        UserHelper.getUsersOnPlace(placeName).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                task.addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        userList.addAll(queryDocumentSnapshots.toObjects(User.class));
                        Log.d(TAG, "onSuccess: Userlist fetched with success !\n" + queryDocumentSnapshots.getDocuments().size());
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error while retrieving users from a restaurant ->" + e);
                    }
                });
            }
        });
        userListLiveData.postValue(userList);
        return userListLiveData;
    }
}
