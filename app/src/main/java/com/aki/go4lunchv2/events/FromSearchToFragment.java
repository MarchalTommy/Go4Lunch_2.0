package com.aki.go4lunchv2.events;

import com.google.android.libraries.places.api.model.Place;

public class FromSearchToFragment {

    public Place place;

    public FromSearchToFragment(Place place) {
        this.place = place;
    }
}
