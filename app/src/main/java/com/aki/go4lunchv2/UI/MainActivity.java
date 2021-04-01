package com.aki.go4lunchv2.UI;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.ActivityMainBinding;
import com.aki.go4lunchv2.databinding.NavHeaderBinding;
import com.aki.go4lunchv2.databinding.SettingsDialogBinding;
import com.aki.go4lunchv2.events.FromSearchToFragment;
import com.aki.go4lunchv2.events.MapReadyEvent;
import com.aki.go4lunchv2.events.YourLunchEvent;
import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.models.User;
import com.aki.go4lunchv2.viewmodels.RestaurantViewModel;
import com.aki.go4lunchv2.viewmodels.UserViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    //TODO : Gérer YourLunch et les favoris
    //TODO : Gérer searchbar with autocomplete
    //TODO : Optimiser map éventuellement


    UserViewModel userViewModel;
    RestaurantViewModel restaurantViewModel;
    User localUser = User.getInstance();
    Result chosenRestaurant;
    // BINDINGS
    ActivityMainBinding mainBinding;
    NavHeaderBinding headerBinding;
    SettingsDialogBinding settingsBinding;
    NavController navController;
    // UI
    private DrawerLayout drawer;
    // Listeners
    @SuppressLint("NonConstantResourceId")
    private final NavigationView.OnNavigationItemSelectedListener drawerListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.your_lunch:
                        // TODO : RECUPERER RESTAURANT CHOISIT  : selectedFragment = new DetailFragment();
                        if (localUser != null) {
                            getRestaurantFromName();
                            if (chosenRestaurant != null) {
                                EventBus.getDefault().postSticky(new YourLunchEvent(chosenRestaurant));
                                navController.navigate(R.id.detailFragment);
                            } else {
                                Snackbar.make(mainBinding.getRoot(), "You don't have any place selected yet !", BaseTransientBottomBar.LENGTH_LONG).show();
                            }
                        }
                        //toolbar.setVisibility(View.GONE);
                        break;
                    case R.id.settings:
                        showSettings();
                        break;
                    case R.id.logout:
                        userViewModel.logout(this);
                        navController.navigate(R.id.loginFragment);
                        break;
                }
                return true;
            };
    //TODO : comment savoir si liste ou map ?
    private final MenuItem.OnMenuItemClickListener searchListener =
            menuItem -> {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setCountry("FR")
                        .setLocationBias(RectangularBounds.newInstance(
                                new LatLng(48.638732, 2.056947),
                                new LatLng(49.031723, 2.694153)))
                        .build(this);

                startActivityForResult(intent, 100);
                return true;
            };

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMapReadyEvent(MapReadyEvent event) {
        if (event.mapReady) {
            getRestaurantsAround();
            mainBinding.mainProgressBar.hide();
            mainBinding.bottomNavView.setVisibility(View.VISIBLE);
            mainBinding.toolbar.setVisibility(View.VISIBLE);
            updateUi();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        settingsBinding = SettingsDialogBinding.inflate(getLayoutInflater());
        NavigationSetup();
        places();

        if (userViewModel.getCurrentFirebaseUser() != null) {
            mainBinding.mainProgressBar.show();
            userViewModel.getCurrentUser().observe(this, user -> {

                localUser.setUsername(user.getUsername());
                localUser.setPlaceBooked(user.getPlaceBooked());
                localUser.setHasBooked(user.getHasBooked());
                localUser.setLocation(user.getLocation());

                mainBinding.mainProgressBar.hide();
                mainBinding.bottomNavView.setVisibility(View.VISIBLE);
                mainBinding.toolbar.setVisibility(View.VISIBLE);
                updateUi();
            });
        } else {
            mainBinding.bottomNavView.setVisibility(View.GONE);
            mainBinding.toolbar.setVisibility(View.GONE);
            navController.navigate(R.id.loginFragment);
        }

        NavigationView navView = mainBinding.navView;
        navView.setNavigationItemSelectedListener(drawerListener);

        setContentView(mainBinding.getRoot());
    }

    private void getRestaurantsAround() {
        restaurantViewModel.getRestaurantsAround(localUser.getLocation(), this).observe(this, new Observer<ArrayList<Result>>() {
            @Override
            public void onChanged(ArrayList<Result> results) {
                restaurantViewModel.setRestaurantsAround(results);
            }
        });
    }

    private void places() {
        Places.initialize(getApplicationContext(), getResources().getString(R.string.GOOGLE_MAPS_API_KEY));
    }

    private void NavigationSetup() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavigationUI.setupWithNavController(mainBinding.bottomNavView, navHostFragment.getNavController());
            navController = navHostFragment.getNavController();
        }
    }

    public void updateUi() {
        Toolbar toolbar = mainBinding.toolbar;
        toolbar.setTitle("I'm Hungry !");
        toolbar.getMenu().getItem(0).setOnMenuItemClickListener(searchListener);

        drawer = mainBinding.drawerLayout;
        NavigationView navView = mainBinding.navView;

        headerBinding = NavHeaderBinding.bind(navView.getHeaderView(0));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Glide.with(this)
                .load(userViewModel.getCurrentFirebaseUser().getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(headerBinding.profilePic);

        headerBinding.username.setText(localUser.getUsername());
        headerBinding.usermail.setText(userViewModel.getCurrentFirebaseUser().getEmail());
    }

    public void showSettings() {
        drawer.closeDrawer(GravityCompat.START);
        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.show(getSupportFragmentManager(), "settings Dialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Place place = Autocomplete.getPlaceFromIntent(data);

            EventBus.getDefault().post(new FromSearchToFragment(place));

            Log.d(TAG, "onActivityResult: " + place.getName());
        }
    }

    public void getRestaurantFromName() {
        restaurantViewModel.getRestaurantFromName(localUser.getPlaceBooked(), localUser.getLocation(), this.getApplicationContext()).observe(this, new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                chosenRestaurant.setPhotos(result.getPhotos());
                chosenRestaurant.setRating(result.getRating());
                chosenRestaurant.setVicinity(result.getVicinity());
                chosenRestaurant.setName(result.getName());
            }
        });
    }

    public static class SettingsDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.settings_dialog, null, false);
            SettingsDialogBinding binding = SettingsDialogBinding.bind(view);

            builder.setView(view)
                    .setCancelable(true)
                    .setNeutralButton("Confirm settings", (dialogInterface, i) -> {
                        if (binding.notificationSwitch.isEnabled()) {
                            //TODO : activer notifications
                        } else {
                            //TODO : désactiver notifications
                        }
                    });

            return builder.create();
        }
    }
}