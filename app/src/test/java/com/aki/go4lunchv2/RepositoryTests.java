package com.aki.go4lunchv2;

import com.aki.go4lunchv2.viewmodels.RestaurantViewModel;
import com.aki.go4lunchv2.viewmodels.UserViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryTests {

    @Mock
    UserViewModel userViewModel;
    @Mock
    RestaurantViewModel restaurantViewModel;


    @Before
    public void setup() {
    }
}
