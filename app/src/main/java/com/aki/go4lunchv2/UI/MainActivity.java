package com.aki.go4lunchv2.UI;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.ActivityMainBinding;
import com.aki.go4lunchv2.databinding.NavHeaderBinding;
import com.aki.go4lunchv2.databinding.SettingsDialogBinding;
import com.aki.go4lunchv2.events.FromSearchToMap;
import com.aki.go4lunchv2.viewmodels.SharedViewModel;
import com.aki.go4lunchv2.viewmodels.UserViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    //TODO : Gérer YourLunch et les favoris
    //TODO : Gérer searchbar with autocomplete
    //TODO : Enquêter sur le bug de déconnexion

    UserViewModel userViewModel;
    SharedViewModel sharedViewModel;
    // BINDINGS
    ActivityMainBinding mainBinding;
    NavHeaderBinding headerBinding;
    SettingsDialogBinding settingsBinding;
    NavController navController;
    // UI
    private DrawerLayout drawer;
    private Toolbar toolbar;

    // Listeners
    private final NavigationView.OnNavigationItemSelectedListener drawerListener =
            item -> {
                switch (item.getItemId()) {
                    case R.id.your_lunch:
                        // TODO : RECUPERER RESTAURANT CHOISIT  : selectedFragment = new DetailFragment();
                        //toolbar.setVisibility(View.GONE);
                        break;
                    case R.id.settings:
                        showSettings();
                        break;
                    case R.id.logout:
                        AuthUI.getInstance().signOut(this);
                        navController.navigate(R.id.action_mapFragment_to_loginFragment);
                        break;
                }
                return true;
            };
    private final MenuItem.OnMenuItemClickListener searchListener = menuItem -> {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        settingsBinding = SettingsDialogBinding.inflate(getLayoutInflater());

        NavigationSetup();
        places();

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            updateUi();
            mainBinding.bottomNavView.setVisibility(View.VISIBLE);
            mainBinding.toolbar.setVisibility(View.VISIBLE);
        } else {
            mainBinding.bottomNavView.setVisibility(View.GONE);
            mainBinding.toolbar.setVisibility(View.GONE);
        }

        NavigationView navView = mainBinding.navView;
        navView.setNavigationItemSelectedListener(drawerListener);

        setContentView(mainBinding.getRoot());
    }

    private void places() {
        Places.initialize(getApplicationContext(), getResources().getString(R.string.GOOGLE_MAPS_API_KEY));
    }

    private void NavigationSetup() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(mainBinding.bottomNavView, navHostFragment.getNavController());
        navController = navHostFragment.getNavController();
    }

    public void updateUi() {
        toolbar = mainBinding.toolbar;
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

        headerBinding.username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        headerBinding.usermail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    public void showSettings() {
        drawer.closeDrawer(Gravity.LEFT);
        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.show(getSupportFragmentManager(), "settings Dialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);

            EventBus.getDefault().post(new FromSearchToMap(place));

            Log.d(TAG, "onActivityResult: " + place.getName());
        }
    }

    public static class SettingsDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.settings_dialog, null, false);
            SettingsDialogBinding binding = SettingsDialogBinding.bind(view);

            builder.setView(view)
                    .setCancelable(true)
                    .setNeutralButton("Confirm settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (binding.notificationSwitch.isEnabled()) {
                                //TODO : activer notifications
                            } else {
                                //TODO : désactiver notifications
                            }
                        }
                    });

            return builder.create();
        }
    }
}