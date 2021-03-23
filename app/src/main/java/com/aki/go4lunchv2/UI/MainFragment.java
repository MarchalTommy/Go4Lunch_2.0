package com.aki.go4lunchv2.UI;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.FragmentMainBinding;
import com.aki.go4lunchv2.databinding.NavHeaderBinding;
import com.aki.go4lunchv2.databinding.SettingsDialogBinding;
import com.aki.go4lunchv2.viewmodels.SharedViewModel;
import com.aki.go4lunchv2.viewmodels.UserViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class MainFragment extends Fragment {

    //TODO : Gérer la map et places
    //TODO : Gérer YourLunch et les favoris
    //TODO : Gérer searchbar with autocomplete
    //TODO : Enquêter sur le bug de déconnexion

    NavController navController;
    UserViewModel userViewModel;
    SharedViewModel sharedViewModel;

    // BINDINGS
    FragmentMainBinding mainBinding;
    NavHeaderBinding headerBinding;
    SettingsDialogBinding settingsBinding;

    // UI
    private DrawerLayout drawer;
    private Toolbar toolbar;
    public static int FRAGMENT_SELECTED = 1;


    // Listeners
    private final BottomNavigationView.OnNavigationItemSelectedListener bottomNavListener =
            item -> {
                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.nav_map:
                        FRAGMENT_SELECTED = 1;
                        selectedFragment = new MapFragment();
                        break;
                    case R.id.nav_list:
                        FRAGMENT_SELECTED = 2;
                        selectedFragment = new ListFragment();
                        break;
                    case R.id.nav_coworkers:
                        FRAGMENT_SELECTED = 3;
                        selectedFragment = new WorkmatesFragment();
                        break;
                }
                MainFragment.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main,
                        selectedFragment).commit();

                return true;
            };
    private final OnMenuItemClickListener searchListener = menuItem -> {
        switch (FRAGMENT_SELECTED) {
            //MAPS VIEW
            case 1:
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setCountry("FR")
                        .setLocationBias(RectangularBounds.newInstance(
                                new LatLng(48.638732, 2.056947),
                                new LatLng(49.031723, 2.694153)))
                        .build(getContext());

                startActivityForResult(intent, 100);
                break;
            //LIST VIEW
            case 2:
                List<Place.Field> fieldList2 = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME);
                Intent intent2 = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList2)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)
                        .setCountry("FR")
                        .setLocationBias(RectangularBounds.newInstance(
                                new LatLng(48.638732, 2.056947),
                                new LatLng(49.031723, 2.694153)))
                        .build(getContext());

                startActivityForResult(intent2, 200);
                break;
            //WORKMATES
            case 3:

                break;
        }
        return true;
    };
    private final NavigationView.OnNavigationItemSelectedListener drawerListener =
            item -> {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.your_lunch:
                        selectedFragment = new DetailFragment();
                        toolbar.setVisibility(View.GONE);
                        break;
                    case R.id.settings:
                        showSettings();
                        break;
                    case R.id.logout:
                        AuthUI.getInstance().signOut(getContext());
                        navController.navigate(R.id.action_mainFragment_to_loginFragment);
                        break;
                }
                return true;
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainBinding = FragmentMainBinding.bind(view);
        navController = Navigation.findNavController(view);

        updateUi();

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_main, new MapFragment()).commit();
    }

    public void updateUi() {
        toolbar = mainBinding.toolbar;
        toolbar.setTitle("I'm Hungry !");
        toolbar.getMenu().getItem(0).setOnMenuItemClickListener(searchListener);

        drawer = mainBinding.drawerLayout;
        NavigationView navView = mainBinding.navView;
        navView.setNavigationItemSelectedListener(drawerListener);
        headerBinding = NavHeaderBinding.bind(navView.getHeaderView(0));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this.getActivity(), drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        BottomNavigationView bottomNav = mainBinding.bottomNavView;
        bottomNav.setOnNavigationItemSelectedListener(bottomNavListener);

        Glide.with(this)
                .load(userViewModel.getCurrentFirebaseUser().getPhotoUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(headerBinding.profilePic);

        headerBinding.username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        headerBinding.usermail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    public void showSettings() {
        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.show(getChildFragmentManager(), "settings Dialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);

            sharedViewModel.setSearchLocation(place.getLatLng());

            Log.d(TAG, "onActivityResult: " + place.getName());
        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);

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
