package com.surveyfiesta.mroc.ui.home;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.entities.Users;
import com.surveyfiesta.mroc.ui.login.LoginFragment;
import com.surveyfiesta.mroc.ui.login.UserViewModel;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private UserViewModel userViewModel;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        final NavController navController = Navigation.findNavController(view);

        userViewModel.getCurrentUserData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                displayUserDetails(user);
            } else {
                navController.navigate(R.id.loginFragment);
            }
        });
    }

    private void displayUserDetails(Users user) {
        TextView firstNameView = this.getView().findViewById(R.id.firstNameText);
        TextView surnameView = this.getView().findViewById(R.id.surnameTextView);
        TextView emailAddress = this.getView().findViewById(R.id.profileEmailTextView);

        firstNameView.setText(user.getFirstName());
        surnameView.setText(user.getSurname());
        emailAddress.setText(user.getEmailAddress());
    }
}