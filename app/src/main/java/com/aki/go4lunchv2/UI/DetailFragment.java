package com.aki.go4lunchv2.UI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.FragmentRestaurantDetailBinding;
import com.aki.go4lunchv2.events.FromListToDetailEvent;
import com.aki.go4lunchv2.events.FromMapToDetailEvent;
import com.aki.go4lunchv2.events.YourLunchEvent;
import com.aki.go4lunchv2.models.ResultDetailed;
import com.aki.go4lunchv2.models.User;
import com.aki.go4lunchv2.viewmodels.UserViewModel;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class DetailFragment extends Fragment {

    Context context;
    UserViewModel userViewModel;
    DetailAdapter adapter;
    ResultDetailed restaurantDetail;
    List<User> userList = new ArrayList<>();
    FragmentRestaurantDetailBinding bindings;

    User localUser = User.getInstance();

    //Listener
    private final OnClickListener fabListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!localUser.getHasBooked()) {
                //Updating data for the UI
                userViewModel.updateHasBooked(true);
                userViewModel.updatePlaceBooked(restaurantDetail.getName());
                updateAdapter();
                DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.secondaryColor));
            } else if (localUser.getPlaceBooked().equals(restaurantDetail.getName())) {
                DetailFragment.this.FABAlertDialog(1);
            } else {
                DetailFragment.this.FABAlertDialog(2);
            }
        }
    };
    @SuppressLint("NonConstantResourceId")
    private final OnClickListener callLikeWebsiteListener = view -> {
        switch (view.getId()) {
            case R.id.call_btn:
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + restaurantDetail.getInternationalPhoneNumber())));
                break;
            case R.id.like_btn:
                break;
            case R.id.website_btn:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(restaurantDetail.getUrl())));
                break;
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getContext();
        //sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        restaurantDetail = new ResultDetailed();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindings = FragmentRestaurantDetailBinding.bind(view);

        //Getting data and updating the UI
        getDataFromViewModel();

        initAdapter();

        //FAB Listener
        bindings.detailFab.setOnClickListener(fabListener);

        bindings.callBtn.setOnClickListener(callLikeWebsiteListener);
        bindings.likeBtn.setOnClickListener(callLikeWebsiteListener);
        bindings.websiteBtn.setOnClickListener(callLikeWebsiteListener);

    }

    private void initAdapter() {
        adapter = new DetailAdapter(context);
        bindings.detailRecyclerview.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        bindings.detailRecyclerview.setHasFixedSize(false);
        bindings.detailRecyclerview.setAdapter(adapter);

        //RecyclerView update with users having their lunch set here
        userViewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            userList.clear();
            for (User u : users) {
                if (u.getPlaceBooked().equals(restaurantDetail.getName())) {
                    userList.add(u);
                }
            }
            adapter.updateList(userList);
        });

    }

    private void updateAdapter(){
        userViewModel.getUsersOnPlace(restaurantDetail.getName()).observe(getViewLifecycleOwner(), users -> adapter.updateList(users));
    }

    // I made it this way to prevent the app from updating each time the FAB is clicked.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        localUser.setHasBooked(Objects.requireNonNull(userViewModel.getCurrentUser().getValue()).getHasBooked());
        localUser.setPlaceBooked(userViewModel.getCurrentUser().getValue().getPlaceBooked());
    }

    private void getDataFromViewModel() {
        //Getting current user
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {

            //Putting the info in local var currentUser when data is received
            localUser.setUrlPicture(user.getUrlPicture());
            localUser.setUsername(user.getUsername());
            localUser.setHasBooked(user.getHasBooked());
            localUser.setPlaceBooked(user.getPlaceBooked());
            localUser.setUid(user.getUid());

            //FAB design (green when lunch is here, black otherwise)
            if (localUser.getPlaceBooked().equals(restaurantDetail.getName())) {
                DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.secondaryColor));
            } else {
                DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.black));
            }
        });
    }

    private void FABAlertDialog(int c) {
        switch (c) {
            case 1:
                new MaterialAlertDialogBuilder(context)
                        .setPositiveButton(getString(R.string.dialog_yes), (dialogInterface, i) -> {
                            //Updating data for the UI
                            userViewModel.updateHasBooked(false);
                            userViewModel.updatePlaceBooked("");
                            updateAdapter();
                            DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.black));
                        })
                        .setNegativeButton(getString(R.string.dialog_no), (dialogInterface, i) -> dialogInterface.dismiss())
                        .setMessage(getString(R.string.lunch_already_placed)).setTitle(getString(R.string.cancel_lunch))
                        .show();
                break;
            case 2:
                new MaterialAlertDialogBuilder(context)
                        .setPositiveButton(getString(R.string.dialog_yes_2), (dialogInterface, i) -> {

                            //Updating data for the UI
                            userViewModel.updatePlaceBooked(restaurantDetail.getName());
                            updateAdapter();
                            DrawableCompat.setTint(bindings.detailFab.getDrawable(), getResources().getColor(R.color.secondaryColor));
                        })
                        .setNegativeButton(getString(R.string.dialog_no_2), (dialogInterface, i) -> dialogInterface.dismiss())
                        .setMessage(getString(R.string.change_lunch)).setTitle(getString(R.string.change_lunch_title))
                        .show();
                break;
        }
    }

    public void updateRestaurantUI(ResultDetailed restaurant) {
        //Rating
        bindings.detailRatingBar.setNumStars(3);
        bindings.detailRatingBar.setStepSize(1);
        bindings.detailRatingBar.setIsIndicator(true);
        if (restaurant.getRating() != null) {
            //Rating binding
            if (restaurant.getRating() <= 2 && restaurant.getRating() > 0) {
                bindings.detailRatingBar.setRating(1);
            } else if (restaurant.getRating() <= 4 && restaurant.getRating() > 2) {
                bindings.detailRatingBar.setRating(2);
            } else if (restaurant.getRating() > 4) {
                bindings.detailRatingBar.setRating(3);
            } else {
                bindings.detailRatingBar.setRating(0);
            }
        }

        //Basic info
        bindings.restaurantDetailAddress.setText(restaurant.getFormattedAddress());
        bindings.restaurantDetailName.setText(restaurant.getName());

        //Photo
        if (restaurant.getPhotos() != null) {

            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                    "maxheight=400&photoreference=" +
                    restaurant.getPhotos().get(0).getPhotoReference() +
                    "&key=" + context.getResources().getString(R.string.GOOGLE_MAPS_API_KEY);

            //Photo binding
            Glide.with(context)
                    .load(photoUrl)
                    .centerCrop()
                    .into(bindings.restaurantDetailPic);
        } else {
            Glide.with(context)
                    .load(R.drawable.restaurant_default)
                    .centerCrop()
                    .into(bindings.restaurantDetailPic);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void fromMenu(YourLunchEvent event){
        //Initializing the restaurant to be able to receive the data
        restaurantDetail = event.result;

        updateRestaurantUI(restaurantDetail);

        EventBus.getDefault().removeStickyEvent(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void fromList(FromListToDetailEvent event) {
        //Initializing the restaurant to be able to receive the data
        Log.d(TAG, "fromList: EVENT SUCCESSFUL" + restaurantDetail.getName());
        restaurantDetail = event.result;

        updateRestaurantUI(restaurantDetail);

        EventBus.getDefault().removeStickyEvent(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void fromMap(FromMapToDetailEvent event) {

    }
}

