package com.aki.go4lunchv2.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.viewmodels.UserViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    private static final int AUTH_REQUEST_CODE = 123;
    UserViewModel userViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        if(userViewModel.getCurrentFirebaseUser() != null) {
            // INTENT TO MAIN ACTIVITY
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            startSignInActivity();
        }

        setContentView(R.layout.login_activity);
    }

    private void startSignInActivity() {
        Log.d(TAG, "startSignInActivity: SIGN IN STARTED");
        System.out.println("METHOD CALLED");
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                new AuthUI.IdpConfig.TwitterBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setTheme(R.style.Startup_theme)
                        .setLogo(R.drawable.logo_title)
                        .build(), AUTH_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: SIGN IN COMPLETE");
        handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);
        Log.d(TAG, "handleResponseAfterSignIn: SIGN IN AFTER CHECK : LOGIN OR ERROR");
        if (requestCode == AUTH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if(response != null && response.isNewUser()){
                    userViewModel.createCurrentUserInFirestore();
                }
                // INTENT TO MAIN ACTIVITY
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (response == null) {
                    showSnackBar(getCurrentFocus(), getString(R.string.auth_canceled));
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(getCurrentFocus(), getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(getCurrentFocus(), getString(R.string.error_unknown_error));
                }
            }
        }
    }

    private void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }


}
