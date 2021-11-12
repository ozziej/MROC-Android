package com.surveyfiesta.mroc.ui.home;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.Users;
import com.surveyfiesta.mroc.ui.login.UserViewModel;
import com.surveyfiesta.mroc.viewmodels.SavedStateViewModel;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private UserViewModel userViewModel;
    private SavedStateViewModel stateViewModel;

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
        stateViewModel = new ViewModelProvider(requireActivity()).get(SavedStateViewModel.class);
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
            } else {
                displayUserDetails(user);
            }
        }
    }

    private void loginUser(Integer userId, @NonNull View view) {
        userViewModel.login(userId);
        userViewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (!result.getResponseCode().equals(GenericResponse.ResponseCode.SUCCESSFUL)) {
                Snackbar.make(view, result.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
            } else {
                displayUserDetails(result.getUser());
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
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        stateViewModel.setCurrentUserId(null);
        userViewModel.setCurrentUserData(null);
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