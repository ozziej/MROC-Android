package com.surveyfiesta.mroc.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.Users;
import com.surveyfiesta.mroc.ui.login.UserViewModel;
import com.surveyfiesta.mroc.viewmodels.SavedStateViewModel;

public class MainFragment extends Fragment {

    private UserViewModel userViewModel;
    private SavedStateViewModel stateViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(view);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(SavedStateViewModel.class);

        Integer userId = stateViewModel.getCurrentUserId().getValue();
        if (userId == null) {
            navController.navigate(R.id.loginFragment);
        } else {
            Users user = userViewModel.getCurrentUserData().getValue();
            if (user == null) {
                loginUser(userId, view);
            }
        }
    }

    private void loginUser(Integer userId, @NonNull View view) {
        userViewModel.login(userId);
        userViewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (!result.getResponseCode().equals(GenericResponse.ResponseCode.SUCCESSFUL)) {
                Snackbar.make(view, result.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}