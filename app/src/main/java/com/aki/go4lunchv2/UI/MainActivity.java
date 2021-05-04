package com.aki.go4lunchv2.UI;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.ActivityMainBinding;
import com.aki.go4lunchv2.databinding.NavHeaderBinding;
import com.aki.go4lunchv2.databinding.SettingsDialogBinding;
import com.aki.go4lunchv2.events.FromSearchToFragment;
import com.aki.go4lunchv2.events.LunchSelectedEvent;
import com.aki.go4lunchv2.events.MapReadyEvent;
import com.aki.go4lunchv2.events.SettingDialogClosed;
import com.aki.go4lunchv2.models.ResultDetails;
import com.aki.go4lunchv2.models.User;
import com.aki.go4lunchv2.notifications.NotificationWorker;
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
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAIN ACTIVITY : ";

    // VAR
    UserViewModel userViewModel;
    RestaurantViewModel restaurantViewModel;
    NavController navController;
    User localUser = User.getInstance();
    ResultDetails restaurantDetail;
    public static boolean notification_state = true;
    SharedPreferences.Editor editor;

    // BINDINGS
    ActivityMainBinding mainBinding;
    NavHeaderBinding headerBinding;
    SettingsDialogBinding settingsBinding;

    // UI
    private DrawerLayout drawer;

    // Listeners
    //Drawer Listener
    @SuppressLint("NonConstantResourceId")
    private final NavigationView.OnNavigationItemSelectedListener drawerListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.your_lunch:
                        lunchClick();
                        break;
                    case R.id.settings:
                        showSettings();
                        break;
                    case R.id.logout:
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                        userViewModel.logout(this);
                        break;
                }
                return true;
            };
    //Menu Listener (Places Autocomplete)
    private final MenuItem.OnMenuItemClickListener searchListener =
            menuItem -> {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.NAME, Place.Field.ID);
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
    //When click on the "Your Lunch" button in the drawer
    public void lunchClick() {
        if (localUser != null) {
            if (localUser.getHasBooked()) {
                if (localUser.getPlaceBooked().equals(restaurantViewModel.getLocalCachedDetails().getValue().getName())) {
                    mainBinding.toolbar.setVisibility(View.GONE);
                    mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                    navController.navigate(R.id.detailFragment);
                } else {
                    restaurantViewModel.getRestaurantFromName(localUser.getPlaceBooked(),
                            localUser.getLocation(), this)
                            .observe(this, result -> {
                                if (result != null && restaurantDetail == null)
                                    restaurantViewModel.getRestaurantDetail(result.getPlaceId(),
                                            getApplicationContext())
                                            .observe(MainActivity.this, resultDetails -> {
                                                if (resultDetails != null) {
                                                    restaurantDetail = resultDetails;
                                                    restaurantViewModel.setLocalCachedDetails(restaurantDetail.getResult());
                                                    mainBinding.toolbar.setVisibility(View.GONE);
                                                    mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                                                    navController.navigate(R.id.detailFragment);
                                                }
                                            });
                            });
                }
            } else {
                Snackbar.make(mainBinding.getRoot(), getString(R.string.no_lunch_yet), BaseTransientBottomBar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        settingsBinding = SettingsDialogBinding.inflate(getLayoutInflater());

        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        notification_state = sharedPreferences.getBoolean("notification", true);

        NavigationSetup();
        places();

        // If user is logged in
        if (userViewModel.getCurrentFirebaseUser() != null) {
            getFromCloud();
        } else {
            // Else, login screen
            //navController.navigate(R.id.loginFragment);
            mainBinding.bottomNavView.setVisibility(View.GONE);
            mainBinding.toolbar.setVisibility(View.GONE);
        }

        NavigationView navView = mainBinding.navView;
        navView.setNavigationItemSelectedListener(drawerListener);

        setContentView(mainBinding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        updateCloud();
        EventBus.getDefault().unregister(this);
    }

    // Places initialization
    private void places() {
        Places.initialize(getApplicationContext(), getResources().getString(R.string.GOOGLE_MAPS_API_KEY));
    }

    // WorkRequest on subscribe to be launched if the user set his lunch to a restaurant
    @Subscribe
    public void setWorkRequest(LunchSelectedEvent event) {
        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();
        StringBuilder userStringBuilder = new StringBuilder();

        ArrayList<User> userArray = event.userList;
        for (User u : userArray) {
            if (u.getUsername().equals(localUser.getUsername())) {
                userArray.remove(u);
            }
        }

        switch (userArray.size()) {
            case 0:
                userStringBuilder.append(getString(R.string.nobody));
                break;
            case 1:
                userStringBuilder.append(getString(R.string.the_one)).append(event.userList.get(0).getUsername()).append(" !");
                break;
            default:
                if (userArray.size() == 2) {
                    userStringBuilder.append(userArray.get(0).getUsername()).append(getString(R.string._and_)).append(userArray.get(1).getUsername());
                } else if (userArray.size() == 3) {
                    userStringBuilder.append(userArray.get(0).getUsername()).append(", ").append(userArray.get(1).getUsername()).append(getString(R.string._and_)).append(userArray.get(2).getUsername());
                } else {
                    userStringBuilder.append(userArray.get(0).getUsername()).append(", ").append(userArray.get(1).getUsername()).append(getString(R.string._and_more));
                }
                break;
        }

        // Set Execution around 12:00:00 PM
        dueDate.set(Calendar.HOUR_OF_DAY, 12);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);
        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }
        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

        OneTimeWorkRequest notificationRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .setInputData(new Data.Builder()
                        .putString("RESTAURANT_NAME", event.name)
                        .putString("RESTAURANT_ADDRESS", event.formattedAddress)
                        .putString("WORKMATES", userStringBuilder.toString())
                        .build())
                .setBackoffCriteria(BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .build();

        if (notification_state) {
            WorkManager.getInstance(this).enqueueUniqueWork(
                    "sendNotification",
                    ExistingWorkPolicy.REPLACE,
                    notificationRequest);
        }
    }

    // Updating cloud data
    public void updateCloud() {
        userViewModel.updatePlaceLiked(localUser.getPlaceLiked());
        userViewModel.updatePlaceBooked(localUser.getPlaceBooked());
        userViewModel.updateHasBooked(localUser.getHasBooked());
        userViewModel.updateNotificationPreference(localUser.getNotificationPreference());
    }

    // Getting cloud data
    public void getFromCloud() {
        userViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                if (user.getUsername() != null) {
                    localUser.setUsername(user.getUsername());
                } else {
                    localUser.setUsername(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
                }
                localUser.setPlaceBooked(user.getPlaceBooked());
                localUser.setHasBooked(user.getHasBooked());
                localUser.setLocation(user.getLocation());
                localUser.setPlaceLiked(user.getPlaceLiked());
                localUser.setNotificationPreference(user.getNotificationPreference());

                updateUserUi();
            }
        });
    }

    // Navigation Setup
    private void NavigationSetup() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavigationUI.setupWithNavController(mainBinding.bottomNavView, navHostFragment.getNavController());
            navController = navHostFragment.getNavController();
        }
    }

    // Updating the UI with all the information
    public void updateUserUi() {
        Toolbar toolbar = mainBinding.toolbar;
        toolbar.setTitle("I'm Hungry !");
        toolbar.getMenu().getItem(0).setOnMenuItemClickListener(searchListener);

        mainBinding.bottomNavView.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);

        drawer = mainBinding.drawerLayout;
        NavigationView navView = mainBinding.navView;

        headerBinding = NavHeaderBinding.bind(navView.getHeaderView(0));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (userViewModel.getCurrentFirebaseUser().getPhotoUrl() != null) {
            Glide.with(this)
                    .load(userViewModel.getCurrentFirebaseUser().getPhotoUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(headerBinding.profilePic);
        } else {
            Glide.with(this)
                    .load(R.drawable.fui_ic_anonymous_white_24dp)
                    .apply(RequestOptions.circleCropTransform())
                    .into(headerBinding.profilePic);
        }

        headerBinding.username.setText(userViewModel.getCurrentFirebaseUser().getDisplayName());
        headerBinding.usermail.setText(userViewModel.getCurrentFirebaseUser().getEmail());
    }

    // Event called when the map fragment is ready
    @Subscribe
    public void onMapReadyEvent(MapReadyEvent event) {
        if (event.mapReady) {

            restaurantViewModel.getRestaurantsAround(localUser.getLocation(), this).observe(this, results -> {
                if (results != null) {
                    //restaurantViewModel.setLocalRestaurantsData(results);
                    mainBinding.bottomNavView.setVisibility(View.VISIBLE);
                    mainBinding.toolbar.setVisibility(View.VISIBLE);
                    updateUserUi();
                }
            });

            userViewModel.getAllUsers().observe(this, users -> {
                if (users != null) {
                    userViewModel.setLocalUsersData((ArrayList<User>) users);
                }
            });
        }
    }

    // Event to be able to keep the notification switch state in the sharedPrefs
    @Subscribe
    public void onSettingsSaved(SettingDialogClosed event) {
        editor.putBoolean("notification", event.notification_state)
                .apply();
    }

    // Activity Result for Places Autocomplete
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Place place = Autocomplete.getPlaceFromIntent(data);

            ResultDetails restaurantWithDetail = new ResultDetails();
            restaurantViewModel.getRestaurantDetail(place.getId(), getApplicationContext()).observe(this, resultDetails -> {
                if (resultDetails != null) {
                    restaurantWithDetail.setResult(resultDetails.getResult());
                    EventBus.getDefault().post(new FromSearchToFragment(restaurantWithDetail));
                }
            });
        }
    }

    // Showing the setting dialog
    public void showSettings() {
        drawer.closeDrawer(GravityCompat.START);
        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.show(getSupportFragmentManager(), "settings Dialog");
    }

    // Anonymous Class for the settings Dialog
    public static class SettingsDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.settings_dialog, null, false);
            SettingsDialogBinding binding = SettingsDialogBinding.bind(view);

            binding.notificationSwitch.setChecked(notification_state);

            builder.setView(view)
                    .setCancelable(true)
                    .setNeutralButton(getString(R.string.confirm_settings), (dialogInterface, i) -> {
                        notification_state = (binding.notificationSwitch.isChecked());
                        EventBus.getDefault().post(new SettingDialogClosed(binding.notificationSwitch.isChecked()));
                    });

            return builder.create();
        }
    }



}