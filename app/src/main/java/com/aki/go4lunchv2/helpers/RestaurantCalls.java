package com.aki.go4lunchv2.helpers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.aki.go4lunchv2.R;
import com.google.gson.JsonObject;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class RestaurantCalls {


    public interface Callbacks {
        void onResponse(@Nullable JsonObject jsonObject);
        void onFailure();
    }

    public static void fetchRestaurantsAround(Callbacks callbacks, String coordinates, Context context) {

        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<Callbacks>(callbacks);

        PlacesService placesService = PlacesService.setRetrofit();

        Call<JsonObject> call = placesService.getRestaurantsAround(
                context.getResources().getString(R.string.GOOGLE_MAPS_API_KEY),
                coordinates,
                "restaurant",
                "distance");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (callbacksWeakReference.get() != null) {
                    callbacksWeakReference.get().onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Log.d(TAG, "onFailure: " + t.getMessage());

                if (callbacksWeakReference.get() != null) {
                    callbacksWeakReference.get().onFailure();
                }
            }
        });

    }

    public static void getRestaurantDetailsByID(Callbacks callbacks, String id, Context context) {
        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<>(callbacks);

        PlacesService placesService = PlacesService.setRetrofit();

        Call<JsonObject> call = placesService.getRestaurantDetails(
                context.getResources().getString(R.string.GOOGLE_MAPS_API_KEY),
                id,
                "fr",
                "formatted_address,name,geometry,photo,place_id,url,international_phone_number,opening_hours,rating");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (callbacksWeakReference.get() != null) {
                    callbacksWeakReference.get().onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                if (callbacksWeakReference.get() != null) {
                    callbacksWeakReference.get().onFailure();
                }
            }
        });
    }

    public static void fetchRestaurantFromName(Callbacks callbacks, String name, String coordinates, Context context) {
        final WeakReference<Callbacks> callbacksWeakReference = new WeakReference<>(callbacks);

        PlacesService placesService = PlacesService.setRetrofit();

        Call<JsonObject> call = placesService.getRestaurantFromName(
                context.getResources().getString(R.string.GOOGLE_MAPS_API_KEY),
                name,
                "textquery",
                "circle:10000@" + coordinates,
                "photos,place_id,formatted_address,name,rating,geometry,photos");

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (callbacksWeakReference.get() != null) {
                    callbacksWeakReference.get().onResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                if (callbacksWeakReference.get() != null) {
                    callbacksWeakReference.get().onFailure();
                }
            }
        });

    }
}
