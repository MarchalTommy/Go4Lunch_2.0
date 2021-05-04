package com.aki.go4lunchv2.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.FragmentMapBinding;
import com.aki.go4lunchv2.events.FromSearchToFragment;
import com.aki.go4lunchv2.events.MapReadyEvent;
import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.models.ResultDetails;
import com.aki.go4lunchv2.models.User;
import com.aki.go4lunchv2.viewmodels.RestaurantViewModel;
import com.aki.go4lunchv2.viewmodels.UserViewModel;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MapFragment extends Fragment {

    RestaurantViewModel restaurantViewModel;
    UserViewModel userViewModel;

    ArrayList<User> allUsers = new ArrayList<>();
    User localUser = User.getInstance();

    FragmentMapBinding mapBinding;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    NavController navController;
    LocationManager locationManager;

    LatLng latLng;
    String stringLocation = "";
    GoogleMap gMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        //INIT LOCATION MANAGER
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Init view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //Init map fragment
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_frag);
        client = LocationServices.getFusedLocationProviderClient(requireActivity());

        getPermissions();
        initMap();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        mapBinding = FragmentMapBinding.bind(view);
    }

    public void initMap() {
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;

                //Setting MapStyle to get a clean and clear map
                try {
                    boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle));
                if(!success) {
                    Log.d(TAG, "onMapReady: style parsing failed.");
                }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "onMapReady: Can't find style. Error :", e);
                }

                //Telling the Activity that the map is ready for the api call and UI Update (only if no local data can be found)
                EventBus.getDefault().post(new MapReadyEvent(true));

                //Getting restaurant data
                getLocalRestaurantsData();

                gMap.setOnMarkerClickListener(marker -> {
                    restaurantViewModel.getRestaurantDetail(marker.getSnippet(), requireContext())
                            .observe(getViewLifecycleOwner(), resultDetails -> {
                                if (resultDetails != null) {
                                    restaurantViewModel.setLocalCachedDetails(resultDetails.getResult());
                                    navController.navigate(R.id.detailFragment);
                                }
                            });
                    return false;
                });

                // If permissions are granted, activate the blue marker for the user position
                if(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED) {
                    gMap.setMyLocationEnabled(true);
                }


            }
        });
    }

    // Getting the restaurant data and adding markers on location
    private void getLocalRestaurantsData() {

        //Primary color
        float huePrimary = 19.885714285714283f;
        //Secondary color
        float hueSecondary = 150.66666666666666f;
        BitmapDescriptor iconBasic = BitmapDescriptorFactory.defaultMarker(huePrimary);
        BitmapDescriptor iconReserved = BitmapDescriptorFactory.defaultMarker(hueSecondary);

        //Getting all the users
        userViewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            if (users != null) {
                allUsers.addAll(users);
            }

            //Placing markers on restaurant location.
            //If user has set his lunch to one, marker's green, otherwise, primary color.
            restaurantViewModel.getRestaurantsAround(localUser.getLocation(), requireContext()).observe(getViewLifecycleOwner(), results -> {
                if (results != null) {
                    for (Result r : results) {
                        LatLng restaurantLocation = new LatLng(
                                r.getGeometry().getLocation().getLat(),
                                r.getGeometry().getLocation().getLng());

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.title(r.getName());
                        markerOptions.snippet(r.getPlaceId());
                        markerOptions.position(restaurantLocation);
                        markerOptions.icon(iconBasic);

                        for (User u : allUsers) {
                            if (u.getPlaceBooked().equals(r.getName())) {
                                markerOptions.icon(iconReserved);
                            }

                            gMap.addMarker(markerOptions);
                        }
                    }
                }
            });
        });
    }

    //Updating all the location variables needed throughout the app
    private void locationVariableUpdate(Location location) {
        //For the LatLng var
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //A String to be easily usable for my google places API call
        stringLocation = latLng.latitude + "," + latLng.longitude;
        //To be accessed from the list view
        localUser.setLocation(stringLocation);
        if (userViewModel.getCurrentFirebaseUser() != null)
            userViewModel.setLocation(latLng);
        //For the map
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    //EVENT TO GET THE SEARCHED RESTAURANT FROM THE AUTOCOMPLETE IN THE ACTIVITY
    @Subscribe
    public void onRestaurantSearch(FromSearchToFragment event) {
        ResultDetails searchResult = event.result;

        LatLng searchLocation = new LatLng(searchResult.getResult().getGeometry().getLocation().getLat(), searchResult.getResult().getGeometry().getLocation().getLng());

        //Primary color
        float huePrimary = 19.885714285714283f;
        BitmapDescriptor iconBasic = BitmapDescriptorFactory.defaultMarker(huePrimary);

        gMap.addMarker(new MarkerOptions()
                .position(searchLocation)
                .icon(iconBasic)
                .title(searchResult.getResult().getName())
                .snippet(searchResult.getResult().getPlaceId()));

        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchLocation, 19));
    }

    //-----------------------------------------
    //LOCATION PERMISSION
    //LOCATION ACCESS
    //-----------------------------------------

    /**
     * SuppressLint because this method is called only AFTER permissions have been acquired
     */
    @SuppressLint("MissingPermission")
    private void locationUpdates() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 150000, 8, new LocationListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationChanged(@NonNull Location location) {
                locationVariableUpdate(location);
            }
        });
    }

    public void getPermissions() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                explainPermissions();
            } else {
                askPermissions();
            }
        } else {
            Log.d(TAG, "getPermissions: PERMISSIONS ALREADY GRANTED ");
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                //To start the location update listener
                locationUpdates();
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
                                    .setFastestInterval(5000)
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
                getActivity().getResources().getString(R.string.location_required),
                BaseTransientBottomBar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.authorize), view -> askPermissions())
                .show();
    }

    private void askPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
    }

    private void displayOptions() {
        Snackbar.make(mapBinding.layoutMapFragment, getString(R.string.permission_denied), BaseTransientBottomBar.LENGTH_LONG)
                .setAction(getString(R.string.settings_menu), view -> {
                    final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    final Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
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
                LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    //When location service is enabled, get last location
                    client.getLastLocation().addOnCompleteListener(task -> {
                        Location location = task.getResult();
                        if (location != null) {
                            locationVariableUpdate(location);
                        }
                    });
                    //To start the location update listener
                    locationUpdates();
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
