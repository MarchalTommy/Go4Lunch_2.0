package com.aki.go4lunchv2.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import android.content.ContentValues;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.FragmentMainBinding;
import com.aki.go4lunchv2.databinding.NavHeaderBinding;
import com.aki.go4lunchv2.databinding.SettingsDialogBinding;
import com.aki.go4lunchv2.viewmodels.SharedViewModel;
import com.aki.go4lunchv2.viewmodels.UserViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        setContentView(R.layout.activity_main);
    }

}