package com.aki.go4lunchv2.models;

import androidx.annotation.Nullable;

public class User {

    private String uid, username;
    private Boolean hasBooked = false;;
    @Nullable private String urlPicture;
    private String placeBooked = "null";

    public User() {}

    public User(String uid, String username, @Nullable String urlPicture, Boolean hasBooked, String placeBooked) {
        this.uid = uid;
        this.urlPicture = urlPicture;
        this.username = username;
        this.hasBooked = hasBooked;
        this.placeBooked = placeBooked;
    }

    //TODO : vérifier si meilleur temps de renvoyer un "faux" result avec le nom, ou un string du nom
    public String getPlaceBooked() {
        return placeBooked;
    }

    public void setPlaceBooked(String resultBooked) {
        this.placeBooked = resultBooked;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getHasBooked() {
        return hasBooked;
    }

    public void setHasBooked(Boolean hasBooked) {
        this.hasBooked = hasBooked;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }
}
