package com.aki.go4lunchv2.events;

import com.aki.go4lunchv2.models.Result;

public class YourLunchEvent {

    public Result result;

    public YourLunchEvent(Result result) {
        this.result = result;
    }
}
