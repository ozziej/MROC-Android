package com.surveyfiesta.mroc.ui.login;

import static android.content.Context.INPUT_METHOD_SERVICE;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.viewmodels.SavedStateViewModel;

public class LoginFragment extends Fragment {

    private UserViewModel userViewModel;
    private SavedStateViewModel stateViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(SavedStateViewModel.class);

        EditText emailAddressText = view.findViewById(R.id.editEmailAddress);
        EditText passwordText = view.findViewById(R.id.editPassword);
        Button loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(l -> {
            String emailAddress = emailAddressText.getText().toString();
            String password = passwordText.getText().toString();
            hideKeyboard(view);
            userViewModel.login(emailAddress, password);
        });

        passwordText.setOnEditorActionListener((textView, i, keyEvent) -> {
            boolean handled = false;
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String emailAddress = emailAddressText.getText().toString();
                String password = passwordText.getText().toString();
                hideKeyboard(view);
                userViewModel.login(emailAddress, password);
                handled = true;
            }
            return handled;
        });

        userViewModel.getLoginResult().observe(getViewLifecycleOwner(), result ->{
            if (result.getResponseCode().equals(GenericResponse.ResponseCode.SUCCESSFUL)) {
                stateViewModel.setCurrentUserId(result.getUser().getUserId());
                Snackbar.make(view, result.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
            } else {
                Snackbar.make(view, result.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void hideKeyboard(View v) {
        Context context = v.getContext();
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }
}