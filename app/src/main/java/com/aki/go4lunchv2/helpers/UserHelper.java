package com.aki.go4lunchv2.helpers;

import android.content.Context;

import com.aki.go4lunchv2.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class UserHelper {

    public static final String COLLECTION_NAME = "users";

    public static CollectionReference getUserCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public static void logout(Context context) {
        AuthUI.getInstance().signOut(context);
        FirebaseAuth.getInstance().signOut();
    }

    public static DocumentReference getCurrentUser() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public static FirebaseUser getCurrentUserFirebase() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static Task<Void> createUser(String uid, String username, String urlPicture, Boolean hasBooked, String placeBooked, String location) {
        User userToCreate = new User(uid, username, urlPicture, hasBooked, placeBooked, location);
        return UserHelper.getUserCollection()
                .document(uid)
                .set(userToCreate);
    }

    public static Task<QuerySnapshot> getUsersOnPlace(String place) {
        return UserHelper.getUserCollection()
                .whereEqualTo("placeBooked", place)
                .get();
    }

    public static Task<DocumentSnapshot> getUser(String uid) {
        return UserHelper.getUserCollection()
                .document(uid)
                .get();
    }

    public static Task<QuerySnapshot> getAllUsers() {
        return UserHelper.getUserCollection()
                .orderBy("username")
                .get();
    }

    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUserCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateHasBooked(Boolean hasBooked, String uid) {
        return UserHelper.getUserCollection().document(uid).update("hasBooked", hasBooked);
    }

    public static Task<Void> updatePlaceBooked(String placeBooked, String uid) {
        return UserHelper.getUserCollection().document(uid).update("placeBooked", placeBooked);
    }

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUserCollection().document(uid).delete();
    }

    public static Task<Void> updateLocation(String locationString) {
        return UserHelper.getCurrentUser().update("location", locationString);
    }
}
