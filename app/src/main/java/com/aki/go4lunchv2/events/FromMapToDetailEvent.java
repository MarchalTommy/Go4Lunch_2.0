package com.aki.go4lunchv2.events;

import com.aki.go4lunchv2.models.Result;

public class FromMapToDetailEvent {

    public Result result;

    public FromMapToDetailEvent(Result result) {
        this.result = result;
    }
}
