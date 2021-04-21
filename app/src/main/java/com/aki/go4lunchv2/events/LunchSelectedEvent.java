package com.aki.go4lunchv2.events;

import com.aki.go4lunchv2.models.User;

import java.util.ArrayList;
import java.util.List;

public class LunchSelectedEvent {

    public ArrayList<User> userList;
    public String name, formattedAddress;

    public LunchSelectedEvent(ArrayList<User> userList, String name, String formattedAddress) {
        this.userList = userList;
        this.name = name;
        this.formattedAddress = formattedAddress;
    }
}
