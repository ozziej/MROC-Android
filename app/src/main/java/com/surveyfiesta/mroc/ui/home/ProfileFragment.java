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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.Users;
import com.surveyfiesta.mroc.ui.login.LoginFragment;
import com.surveyfiesta.mroc.ui.login.UserViewModel;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
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
        setHasOptionsMenu(true);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_save:
                updateWithResult();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateWithResult() {
        Users user = userViewModel.getCurrentUserData().getValue();
        if (user != null) {
            updateUserModel(user);
            userViewModel.updateUserDetails();
            userViewModel.getUpdateResult().observe(getViewLifecycleOwner(), result ->{
                Snackbar.make(this.getView(), result.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
            });
        }
    }

    private void updateUserModel(Users user){
        EditText firstNameView = this.getView().findViewById(R.id.firstNameEditText);
        EditText surnameView = this.getView().findViewById(R.id.surnameEditText);
        EditText emailAddress = this.getView().findViewById(R.id.emailAddressEditText);
        EditText cellNumber = this.getView().findViewById(R.id.cellNumberEditText);

        user.setFirstName(firstNameView.getText().toString());
        user.setSurname(surnameView.getText().toString());
        user.setEmailAddress(emailAddress.getText().toString());
        user.setCellNumber(cellNumber.getText().toString());
    }

    private void displayUserDetails(Users user) {
        EditText firstNameView = this.getView().findViewById(R.id.firstNameEditText);
        EditText surnameView = this.getView().findViewById(R.id.surnameEditText);
        EditText emailAddress = this.getView().findViewById(R.id.emailAddressEditText);
        EditText cellNumber = this.getView().findViewById(R.id.cellNumberEditText);

        firstNameView.setText(user.getFirstName());
        surnameView.setText(user.getSurname());
        emailAddress.setText(user.getEmailAddress());
        cellNumber.setText(user.getCellNumber());
    }
}