package com.aki.go4lunchv2.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.FragmentMapBinding;
import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.viewmodels.RestaurantViewModel;
import com.aki.go4lunchv2.viewmodels.SharedViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MapFragment extends Fragment {

    RestaurantViewModel restaurantViewModel;
    SharedViewModel sharedViewModel;

    FragmentMapBinding mapBinding;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    LatLng latLng;
    String stringLocation = new String();
    GoogleMap gMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restaurantViewModel = new ViewModelProvider(getActivity()).get(RestaurantViewModel.class);
        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Init view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //Init map fragment
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_frag);

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapBinding = FragmentMapBinding.bind(view);
        getPermissions();
        initMap();
    }

    public void initMap() {
        //Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //When map is loaded
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_marker);
                googleMap.clear();
                //Animating to zoom the marker
                gMap = googleMap;

                restaurantViewModel.getRestaurantsAround(stringLocation, getContext()).observe(getViewLifecycleOwner(), new Observer<ArrayList<Result>>() {
                    @Override
                    public void onChanged(ArrayList<Result> results) {
                        googleMap.clear();
                        for (Result r : results) {
                            LatLng restaurantLocation = new LatLng(r.getGeometry().getLocation().getLat(), r.getGeometry().getLocation().getLng());

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.icon(icon);
                            markerOptions.title(r.getName());
                            markerOptions.position(restaurantLocation);

                            googleMap.addMarker(markerOptions);
                        }
                    }
                });
            }
        });
    }

    private void locationVariableUpdate(Location location) {
        Log.d(TAG, "onComplete: LOCATION FOR DEVELOPPEMENT PURPOSE => " + location.getLatitude() + " : " + location.getLongitude());
        //For the LatLng var
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //To be accessed from the list view, update on the sharedViewModel
        sharedViewModel.setUserLocation(latLng);
        //To be easily usable for my google places API call
        stringLocation = latLng.latitude + "," + latLng.longitude;
        //For the map
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    //-----------------------------------------
    //LOCATION PERMISSION
    //LOCATION ACCESS
    //-----------------------------------------

    public void getPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                explainPermissions();
            } else {
                askPermissions();
            }
        } else {
            Log.d(TAG, "getPermissions: PERMISSIONS ALREADY GRANTED ");
            //INIT LOCATION MANAGER
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                //When location service is enabled, get last location
                client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    /**
                     * SuppressLint is okay because this is called ONLY if the permissions have already been granted
                     */
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            locationVariableUpdate(location);
                        } else {
                            LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(10000)
                                    .setFastestInterval(1000)
                                    .setNumUpdates(1);

                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    Location location1 = locationResult.getLastLocation();
                                    locationVariableUpdate(location1);
                                }
                            };
                            //REQUEST LOCATION UPDATES
                            client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    }
                });
            } else {
                //IF LOCATION SERVICE IS NOT ENABLED
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }
    }

    private void explainPermissions() {
        Snackbar.make(mapBinding.layoutMapFragment,
                "Location permissions are required to use the map view, and for the list to be optimal.",
                BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setAction("Authorize", view -> askPermissions())
                .show();
    }

    private void askPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
    }

    private void displayOptions() {
        Snackbar.make(mapBinding.layoutMapFragment, "You have refused the permission", BaseTransientBottomBar.LENGTH_LONG)
                .setAction("Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        final Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                })
                .show();
    }

    /**
     * SuppressLint("MissingPermission")
     * It's because we aren't gonna check permissions in the case where they just got granted.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                //ACCESS LOCATION, IT MEANS THAT PERMISSIONS ARE GOOD
                Log.d(TAG, "onRequestPermissionsResult: PERMISSIONS GRANTED");
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    //When location service is enabled, get last location
                    client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location != null) {
                                locationVariableUpdate(location);
                            }
                        }
                    });
                }
            } else if (!shouldShowRequestPermissionRationale(permissions[0]) && !shouldShowRequestPermissionRationale(permissions[1])) {
                displayOptions();
            } else {
                explainPermissions();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
