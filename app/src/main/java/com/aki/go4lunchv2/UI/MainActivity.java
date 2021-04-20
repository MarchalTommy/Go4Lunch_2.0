package com.aki.go4lunchv2.UI;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.ActivityMainBinding;
import com.aki.go4lunchv2.databinding.NavHeaderBinding;
import com.aki.go4lunchv2.databinding.SettingsDialogBinding;
import com.aki.go4lunchv2.events.FromSearchToFragment;
import com.aki.go4lunchv2.events.MapReadyEvent;
import com.aki.go4lunchv2.events.SettingDialogClosed;
import com.aki.go4lunchv2.events.YourLunchEvent;
import com.aki.go4lunchv2.models.Result;
import com.aki.go4lunchv2.models.ResultDetailed;
import com.aki.go4lunchv2.models.ResultDetails;
import com.aki.go4lunchv2.models.User;
import com.aki.go4lunchv2.notifications.NotificationWorker;
import com.aki.go4lunchv2.viewmodels.RestaurantViewModel;
import com.aki.go4lunchv2.viewmodels.UserViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.aki.go4lunchv2.UI.MainActivity.Convert.rgbToHsl;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BONJOUR ! ";

    //TODO : Optimiser map éventuellement
    //TODO : finir switch réglage (sharedPrefs)

    // VAR
    UserViewModel userViewModel;
    RestaurantViewModel restaurantViewModel;
    NavController navController;
    User localUser = User.getInstance();
    ResultDetailed restaurantDetail;
    String userListString = "";
    public static boolean notification_state = true;

    // CONST
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String NOTIFICATIONS = "notificationState";

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
                restaurantViewModel.getRestaurantDetail(localUser.getPlaceBooked(), this).observe(this, result -> {
                    if (result != null) {
                        EventBus.getDefault().postSticky(new YourLunchEvent(result));
                        mainBinding.toolbar.setVisibility(View.GONE);
                        mainBinding.drawerLayout.closeDrawer(GravityCompat.START);
                        navController.navigate(R.id.detailFragment);
                    }
                });
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
        NavigationSetup();
        places();

        // If user is logged in
        if (userViewModel.getCurrentFirebaseUser() != null) {
            mainBinding.mainProgressBar.show();
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

        getFirebaseFCM();
        Convert.main();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        if(localUser.getHasBooked()){
            setWorkRequest();
        }
        updateCloud();
        Log.d(TAG, "onStop: ON STOP EXECUTED");
    }

    // Places initialization
    private void places() {
        Places.initialize(getApplicationContext(), getResources().getString(R.string.GOOGLE_MAPS_API_KEY));
    }

    // WorkRequest (notification)
    private void setWorkRequest() {
        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();
        StringBuilder userStringBuilder = new StringBuilder();

        Log.d(TAG, "setWorkRequest: WORK QUEUED ASKED !");

        userViewModel.getUsersOnPlace(restaurantDetail.getPlaceId()).observe(this, users -> {
            if(users!=null) {
                for (User u : users) {
                    if (u.getPlaceBooked().equals(restaurantDetail.getName())) {
                        userStringBuilder.append(u.getUsername()).append(", ");
                    }
                }
            }
        });

        // Set Execution around 12:00:00 AM
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
                        .putString("RESTAURANT_NAME", localUser.getPlaceBooked())
                        .putString("RESTAURANT_ADDRESS", restaurantDetail.getFormattedAddress())
                        .putString("WORKMATES", userStringBuilder.toString())
                        .build())
                .setBackoffCriteria(BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(this).enqueueUniqueWork(
                "sendNotification",
                ExistingWorkPolicy.REPLACE,
                notificationRequest);

        Log.d(TAG, "setWorkRequest: WORK QUEUED SUCCESSFULLY ! " + userStringBuilder.toString());
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

                if(!user.getPlaceBooked().equals("")) {
                    restaurantViewModel.getRestaurantFromName(user.getPlaceBooked(), user.getLocation(), this).observe(this, new Observer<Result>() {
                        @Override
                        public void onChanged(Result result) {
                            if(result != null)
                            getDetails(result);
                        }
                    });
                }

                updateUi();


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
    public void updateUi() {
        Toolbar toolbar = mainBinding.toolbar;
        toolbar.setTitle("I'm Hungry !");
        toolbar.getMenu().getItem(0).setOnMenuItemClickListener(searchListener);

        mainBinding.mainProgressBar.hide();
        mainBinding.bottomNavView.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);

        drawer = mainBinding.drawerLayout;
        NavigationView navView = mainBinding.navView;

        headerBinding = NavHeaderBinding.bind(navView.getHeaderView(0));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(userViewModel.getCurrentFirebaseUser().getPhotoUrl() != null) {
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

    // Method to make a Place Detail call when we have a place id
    public void getDetails(Result result) {
        restaurantViewModel.getRestaurantDetail(result.getPlaceId(), getApplicationContext()).observe(MainActivity.this, resultDetails -> {
            if(resultDetails != null) {
                restaurantDetail = resultDetails.getResult();
            }
        });
    }

    // Event called when the map fragment is ready
    @Subscribe
    public void onMapReadyEvent(MapReadyEvent event) {
        if (event.mapReady) {
            restaurantViewModel.getRestaurantsAround(localUser.getLocation(), this).observe(this, results -> {
                if (results != null) {
                    restaurantViewModel.setRestaurantsAround(results);
                    mainBinding.mainProgressBar.hide();
                    mainBinding.bottomNavView.setVisibility(View.VISIBLE);
                    mainBinding.toolbar.setVisibility(View.VISIBLE);
                    updateUi();
                }
            });
        }
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
        loadData();
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
                        EventBus.getDefault().post(new SettingDialogClosed(notification_state));
                    });

            return builder.create();
        }
    }

    // Forced to use EventBus because I can't call my SharedPrefs in the static
    // anonymous class for the dialog.
    @Subscribe
    public void onDialogClosed(SettingDialogClosed event){
        Log.d(TAG, "onDialogClosed: DIALOG CLOSED !! " + notification_state);
        saveData();
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATIONS, notification_state).apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        notification_state = sharedPreferences.getBoolean(NOTIFICATIONS, true);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------
    //TO DELETE ! ONLY FOR DEVELOPMENT :
    public void getFirebaseFCM() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        String msg = token;
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //TO DELETE TOO !
    public static class Convert {

        public static class Hsl {
            public double h, s, l;

            public Hsl(double h, double s, double l) {
                this.h = h;
                this.s = s;
                this.l = l;
            }
        }

        public static void main() {
            String color = "#FF8A50"; // A nice shade of green.
            int r = Integer.parseInt(color.substring(1, 3), 16); // Grab the hex representation of red (chars 1-2) and convert to decimal (base 10).
            int g = Integer.parseInt(color.substring(3, 5), 16);
            int b = Integer.parseInt(color.substring(5, 7), 16);

            double hue = rgbToHsl(r, g, b).h * 360;

            System.out.println("The hue value is " + hue);
        }

        public static Hsl rgbToHsl(double r, double g, double b) {
            r /= 255d; g /= 255d; b /= 255d;

            double max = Math.max(Math.max(r, g), b), min = Math.min(Math.min(r, g), b);
            double h, s, l = (max + min) / 2;

            if (max == min) {
                h = s = 0; // achromatic
            } else {
                double d = max - min;
                s = l > 0.5 ? d / (2 - max - min) : d / (max + min);

                if (max == r) h = (g - b) / d + (g < b ? 6 : 0);
                else if (max == g) h = (b - r) / d + 2;
                else h = (r - g) / d + 4; // if (max == b)

                h /= 6;
            }

            return new Hsl(h, s, l);
        }



    }


}