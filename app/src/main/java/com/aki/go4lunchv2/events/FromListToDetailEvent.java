package com.aki.go4lunchv2.events;


import com.aki.go4lunchv2.models.Result;

public class FromListToDetailEvent {

    public Result result;

    public FromListToDetailEvent(Result result) {
        this.result = result;
    }
}
