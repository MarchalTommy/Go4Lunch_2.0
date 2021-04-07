package com.aki.go4lunchv2.events;

import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.models.ResultDetails;
import com.google.android.libraries.places.api.model.Place;

public class FromSearchToFragment {

    public ResultDetails result = new ResultDetails();

    public FromSearchToFragment(ResultDetails place) {
        result = place;
    }
}
