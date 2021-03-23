package com.aki.go4lunchv2.events;

import com.google.android.libraries.places.api.model.Place;

public class FromSearchToMap {

    public Place place;

    public FromSearchToMap(Place place) {
        this.place = place;
    }
}
