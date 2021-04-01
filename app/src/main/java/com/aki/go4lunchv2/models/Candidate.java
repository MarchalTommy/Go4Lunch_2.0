package com.aki.go4lunchv2.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Candidate {

    @SerializedName("formatted_address")
    @Expose
    private String formattedAddress;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos = null;
    @SerializedName("rating")
    @Expose
    private Double rating;

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Candidate withFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Candidate withName(String name) {
        this.name = name;
        return this;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public Candidate withPhotos(List<Photo> photos) {
        this.photos = photos;
        return this;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Candidate withRating(Double rating) {
        this.rating = rating;
        return this;
    }

}
