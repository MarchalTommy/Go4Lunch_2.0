package com.aki.go4lunchv2.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.FragmentLoginBinding;
import com.aki.go4lunchv2.models.User;
import com.aki.go4lunchv2.viewmodels.UserViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class LoginFragment extends Fragment {

    private static final int AUTH_REQUEST_CODE = 123;
    public static String LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL";
    NavController navController;
    FragmentLoginBinding binding;
    UserViewModel userViewModel;
    private SavedStateHandle savedStateHandle;

    private final User localUser = User.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentLoginBinding.bind(view);

        navController = Navigation.findNavController(view);

        savedStateHandle = Objects.requireNonNull(Navigation.findNavController(view)
                .getPreviousBackStackEntry())
                .getSavedStateHandle();
        savedStateHandle.set(LOGIN_SUCCESSFUL, false);

        startSignInActivity();

    }

    private void startSignInActivity() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build()/*,
                                new AuthUI.IdpConfig.TwitterBuilder().build()*/))
                        .setIsSmartLockEnabled(false, true)
                        .setTheme(R.style.Startup_theme)
                        .setLogo(R.drawable.logo_title)
                        .build(), AUTH_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == AUTH_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                userViewModel.createCurrentUserInFirestore();
                savedStateHandle.set(LOGIN_SUCCESSFUL, true);
                NavHostFragment.findNavController(this).navigate(R.id.mapFragment);
            } else {
                if (response == null) {
                    showSnackBar(this.getView(), getString(R.string.auth_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.getView(), getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.getView(), getString(R.string.error_unknown_error));
                }
            }
        }
    }

    private void showSnackBar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}
