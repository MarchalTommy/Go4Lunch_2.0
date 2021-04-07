package com.aki.go4lunchv2.events;

import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.models.ResultDetailed;
import com.aki.go4lunchv2.models.ResultDetails;

public class YourLunchEvent {

    public ResultDetailed result;

    public YourLunchEvent(ResultDetails resultDetails) {
        this.result = resultDetails.getResult();
    }
}
