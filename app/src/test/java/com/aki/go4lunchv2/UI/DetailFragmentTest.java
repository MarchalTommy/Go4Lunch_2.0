package com.aki.go4lunchv2.UI;

import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.models.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DetailFragmentTest {

    @Mock User localUserMock;

    @Captor private ArgumentCaptor<String> captor;

    @Test
    public void likeARestaurantTest() {
        ArrayList<String> placeLikedIDs = new ArrayList<>();

        when(localUserMock.getPlaceLiked()).thenReturn(placeLikedIDs);

        assertTrue(localUserMock.getPlaceLiked().isEmpty());

        //Creating an array of places's ID
        placeLikedIDs.add("abcd1234");
        placeLikedIDs.add("efgh5678");
        placeLikedIDs.add("ijkl91011");


        //Adding this array to our "PlaceLiked" (as if the button got clicked)
        localUserMock.setPlaceLiked(placeLikedIDs);


        assertFalse(localUserMock.getPlaceLiked().isEmpty());

        assertEquals(3, localUserMock.getPlaceLiked().size());
        assertTrue(localUserMock.getPlaceLiked().contains("efgh5678"));
        assertEquals("ijkl91011", localUserMock.getPlaceLiked().get(2));
    }

    @Test
    public void setRestaurantForLunch() {
        Result imaginaryRestaurant = new Result();
        imaginaryRestaurant.setName("ImaginaryLand");

        boolean hasBooked = false;

        when(localUserMock.getHasBooked()).thenReturn(hasBooked);
        assertFalse(localUserMock.getHasBooked());

        localUserMock.setHasBooked(true);
        localUserMock.setPlaceBooked(imaginaryRestaurant.getName());
        hasBooked = true;

        when(localUserMock.getHasBooked()).thenReturn(hasBooked);
        assertTrue(localUserMock.getHasBooked());

        verify(localUserMock).setPlaceBooked(captor.capture());
        String capturedArgument = captor.getValue();
        assertTrue(capturedArgument.contains("ImaginaryLand"));



        if(localUserMock.getHasBooked())
            when(localUserMock.getPlaceBooked()).thenReturn(imaginaryRestaurant.getName());

        assertEquals("ImaginaryLand", localUserMock.getPlaceBooked());

    }

}