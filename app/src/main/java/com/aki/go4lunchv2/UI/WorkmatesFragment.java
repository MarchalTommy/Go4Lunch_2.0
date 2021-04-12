package com.aki.go4lunchv2.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.FragmentWorkmatesBinding;
import com.aki.go4lunchv2.events.FromListToDetailEvent;
import com.aki.go4lunchv2.events.FromWorkmatesListToFragment;
import com.aki.go4lunchv2.models.User;
import com.aki.go4lunchv2.viewmodels.RestaurantViewModel;
import com.aki.go4lunchv2.viewmodels.UserViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class WorkmatesFragment extends Fragment {

    FragmentWorkmatesBinding binding;
    UserViewModel userViewModel;
    RestaurantViewModel restaurantViewModel;
    User localUser = User.getInstance();
    private WorkmatesAdapter adapter;
    NavController navController;

    private User workmateClicked = new User();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        restaurantViewModel = new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workmates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentWorkmatesBinding.bind(view);
        adapter = new WorkmatesAdapter(this.getContext());
        navController = Navigation.findNavController(view);

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        binding.workmatesRecyclerView.setHasFixedSize(true);
        binding.workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.workmatesRecyclerView.setAdapter(adapter);

        //Observing LiveData to populate RV
        userViewModel.getAllUsers().observe(requireActivity(), users -> adapter.updateList(users));
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

    @Subscribe
    public void fromList(FromWorkmatesListToFragment event) {
        workmateClicked = event.user;

        restaurantViewModel.getRestaurantFromName(workmateClicked.getPlaceBooked(), localUser.getLocation(), requireContext())
                .observe(getViewLifecycleOwner(), result -> {
                    if (result != null) {
                        restaurantViewModel.getRestaurantDetail(result.getPlaceId(), getContext())
                                .observe(getViewLifecycleOwner(), resultDetails -> {
                                    if (resultDetails != null) {
                                        EventBus.getDefault().postSticky(new FromListToDetailEvent(resultDetails.getResult()));
                                        navController.navigate(R.id.action_workmatesFragment_to_detailFragment);
                                    }
                                });
                    }
                });
    }
}
