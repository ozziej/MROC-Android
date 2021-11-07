package com.surveyfiesta.mroc.ui.login;

import static android.content.Context.INPUT_METHOD_SERVICE;

import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.entities.GenericResponse;

public class LoginFragment extends Fragment {

    private UserViewModel userViewModel;
    private SavedStateHandle savedStateHandle;
    public static String LOGIN_SUCCESSFUL = "LOGIN_SUCCESSFUL";

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        savedStateHandle = Navigation.findNavController(view)
                .getPreviousBackStackEntry()
                .getSavedStateHandle();
        savedStateHandle.set(LOGIN_SUCCESSFUL, false);

        EditText emailAddressText = view.findViewById(R.id.editEmailAddress);
        EditText passwordText = view.findViewById(R.id.editPassword);
        Button loginButton = view.findViewById(R.id.loginButton);

        loginButton.setOnClickListener(l -> {
            String emailAddress = emailAddressText.getText().toString();
            String password = passwordText.getText().toString();
            hideKeyboard(view);
            login(emailAddress, password, view);
        });

        passwordText.setOnEditorActionListener((textView, i, keyEvent) -> {
            boolean handled = false;
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String emailAddress = emailAddressText.getText().toString();
                String password = passwordText.getText().toString();
                hideKeyboard(view);
                login(emailAddress, password, view);
                return true;
            }
            return handled;
        });
    }

    private void login(String emailAddress, String password, View view){
        userViewModel.login(emailAddress, password);
        userViewModel.getLoginResult().observe(getViewLifecycleOwner(), result ->{
            if (result.getResponseCode().equals(GenericResponse.ResponseCode.SUCCESSFUL)) {
                savedStateHandle.set(LOGIN_SUCCESSFUL, true);
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