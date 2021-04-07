package com.aki.go4lunchv2.events;


import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.models.ResultDetailed;

public class FromListToDetailEvent {

    public ResultDetailed result;

    public FromListToDetailEvent(ResultDetailed result) {
        this.result = result;
    }
}
