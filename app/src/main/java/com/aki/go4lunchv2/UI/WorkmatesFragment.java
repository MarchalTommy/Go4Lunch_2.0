package com.aki.go4lunchv2.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aki.go4lunchv2.R;
import com.aki.go4lunchv2.databinding.FragmentWorkmatesBinding;
import com.aki.go4lunchv2.models.User;
import com.aki.go4lunchv2.viewmodels.UserViewModel;

import java.util.List;

public class WorkmatesFragment extends Fragment {

    FragmentWorkmatesBinding binding;
    UserViewModel userViewModel;
    private WorkmatesAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
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

        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        binding.workmatesRecyclerView.setHasFixedSize(true);
        binding.workmatesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.workmatesRecyclerView.setAdapter(adapter);

        //Observing LiveData to populate RV
        userViewModel.getAllUsers().observe(this.getActivity(), users -> adapter.updateList(users));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
