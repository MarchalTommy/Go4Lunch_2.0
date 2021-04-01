package com.aki.go4lunchv2.events;

import com.google.android.libraries.places.api.model.Place;

public class FromSearchToList {

    public Place place;

    public FromSearchToList(Place place) {
        this.place = place;
    }
}
