package com.aki.go4lunchv2.UI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.FragmentMapBinding;
import com.aki.go4lunchv2.events.FromListToDetailEvent;
import com.aki.go4lunchv2.events.FromMapToDetailEvent;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Init view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //Init map fragment
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_frag);

        client = LocationServices.getFusedLocationProviderClient(requireActivity());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        mapBinding = FragmentMapBinding.bind(view);
        getPermissions();
        initMap();
    }

    public void initMap() {
        supportMapFragment.getMapAsync(googleMap -> {
            //When map is loaded
            BitmapDescriptor iconBasic = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
            Bitmap bitmap;
            BitmapDescriptor iconLunchHere = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            googleMap.clear();
            gMap = googleMap;

            EventBus.getDefault().post(new MapReadyEvent(true));

            getLocalRestaurantsData();
            locationUpdates();

            googleMap.setOnMarkerClickListener(marker -> {
                restaurantViewModel.getRestaurantFromName(marker.getTitle(), localUser.getLocation(), requireContext()).observe(getViewLifecycleOwner(), result -> {
                    if (result != null) {
                        EventBus.getDefault().postSticky(new FromMapToDetailEvent(result));
                        navController.navigate(R.id.detailFragment);
                    }
                });
                return false;
            });

        });
    }

    private void getLocalRestaurantsData() {
        BitmapDescriptor iconBasic = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
        Bitmap bitmap;
        BitmapDescriptor iconLunchHere = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);

        restaurantViewModel.getLocalRestaurantsData().observe(getViewLifecycleOwner(), results -> {
            if (results != null) {
                for (Result r : results) {
                    LatLng restaurantLocation = new LatLng(
                            r.getGeometry().getLocation().getLat(),
                            r.getGeometry().getLocation().getLng());

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.icon(iconBasic);
                    markerOptions.title(r.getName());
                    markerOptions.position(restaurantLocation);

                    gMap.addMarker(markerOptions);
                }
            }
        });
    }

    private void locationVariableUpdate(Location location) {
        Log.d(TAG, "onComplete: LOCATION FOR DEVELOPPEMENT PURPOSE => " + location.getLatitude() + " : " + location.getLongitude());
        //For the LatLng var
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //To be easily usable for my google places API call
        stringLocation = latLng.latitude + "," + latLng.longitude;
        //To be accessed from the list view, update on the sharedViewModel
        localUser.setLocation(stringLocation);
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

        gMap.addMarker(new MarkerOptions()
                .position(searchLocation)
                .title(searchResult.getResult().getName()));

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
        gMap.setMyLocationEnabled(true);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 150000, 5, new LocationListener() {
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
            //INIT LOCATION MANAGER
            locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
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
