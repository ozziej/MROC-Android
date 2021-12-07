package com.surveyfiesta.mroc.ui.main;

import androidx.lifecycle.ViewModelProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        Button termsConditionsButton = view.findViewById(R.id.termsConditionsButton);

        String userToken = stateViewModel.getCurrentUserToken();
        if (userToken == null || userToken.isEmpty()) {
            navController.navigate(R.id.loginFragment);
        } else {
            Users user = userViewModel.getCurrentUserData().getValue();
            if (user == null) {
                loginUser(userToken);
            }
        }
        termsConditionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.surveyfiesta.com/terms-and-conditions/"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome");
            try{
                startActivity(intent);
            }catch (ActivityNotFoundException ex) {
                intent.setPackage(null);
                startActivity(Intent.createChooser(intent, "Select Browser"));
            }
        });
    }

    private void loginUser(String userToken) {
        userViewModel.login(userToken);
        userViewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (!result.getResponseCode().equals(GenericResponse.ResponseCode.SUCCESSFUL)) {
                Snackbar.make(getView(), result.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}