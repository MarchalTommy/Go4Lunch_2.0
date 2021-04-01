package com.aki.go4lunchv2.events;

public class MapReadyEvent {
    public Boolean mapReady = false;

    public MapReadyEvent(Boolean b) {
        this.mapReady = b;
    }
}
