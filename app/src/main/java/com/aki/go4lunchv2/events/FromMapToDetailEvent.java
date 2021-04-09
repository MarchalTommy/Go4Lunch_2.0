package com.aki.go4lunchv2.events;

import com.aki.go4lunchv2.models.ResultDetailed;

public class FromMapToDetailEvent {

    public ResultDetailed result;

    public FromMapToDetailEvent(ResultDetailed result) {
        this.result = result;
    }
}
