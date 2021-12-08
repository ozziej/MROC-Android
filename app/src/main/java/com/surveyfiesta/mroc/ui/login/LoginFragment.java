package com.surveyfiesta.mroc.ui.login;

import static android.content.Context.INPUT_METHOD_SERVICE;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
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
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.material.snackbar.Snackbar;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.viewmodels.SavedStateViewModel;

public class LoginFragment extends Fragment {

    private UserViewModel userViewModel;
    private SavedStateViewModel stateViewModel;
    private ProgressBar progressBar;
    private GoogleSignInClient googleSignInClient;
    private Intent signInIntent;
    ActivityResultLauncher<Intent> startActivityForResult;

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
        progressBar = getView().findViewById(R.id.loginProgressBar);

        EditText emailAddressText = view.findViewById(R.id.editEmailAddress);
        EditText passwordText = view.findViewById(R.id.editPassword);
        Button loginButton = view.findViewById(R.id.loginButton);
        Button registerButton = view.findViewById(R.id.registerButton);
        SignInButton signInButton = view.findViewById(R.id.signInButton);
        Button termsConditionsButton = view.findViewById(R.id.loginTermsConditionsButton);
        final NavController navController = Navigation.findNavController(view);

        loginButton.setOnClickListener(l -> {
            String emailAddress = emailAddressText.getText().toString();
            String password = passwordText.getText().toString();
            hideKeyboard(view);
            progressBar.setVisibility(View.VISIBLE);
            userViewModel.login(emailAddress, password);
        });

        registerButton.setOnClickListener(l -> {
            stateViewModel.setCurrentUserToken(null);
            navController.navigate(R.id.profileFragment);
        });

        signInButton.setOnClickListener(l -> {
            progressBar.setVisibility(View.VISIBLE);
            startActivityForResult.launch(signInIntent);
        });

        passwordText.setOnEditorActionListener((textView, i, keyEvent) -> {
            boolean handled = false;
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                String emailAddress = emailAddressText.getText().toString();
                String password = passwordText.getText().toString();
                hideKeyboard(view);
                progressBar.setVisibility(View.VISIBLE);
                userViewModel.login(emailAddress, password);
                handled = true;
            }
            return handled;
        });

        userViewModel.getLoginResult().observe(getViewLifecycleOwner(), result ->{
            progressBar.setVisibility(View.INVISIBLE);
            if (result != null) {
                if (result.getResponseCode().equals(GenericResponse.ResponseCode.SUCCESSFUL)) {
                    stateViewModel.setCurrentUserToken(result.getToken());
                    Snackbar.make(view, result.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(this).popBackStack();
                } else {
                    Snackbar.make(view, result.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_app_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    switch (result.getResultCode()) {
                        case Activity.RESULT_OK:
                            signInUser();
                            break;
                        case Activity.RESULT_CANCELED:
                            googleSignInClient.signOut();
                            Snackbar.make(getView(), "Cancelled",Snackbar.LENGTH_SHORT).show();
                            break;
                        default:
                            Log.d("GOOGLE_LOGIN","Something Went Wrong here");
                            break;
                    }
                }
        );
        signInUser();

        termsConditionsButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.terms_uri)));
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

    private void signInUser() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            userViewModel.signInWithGoogle(account.getEmail(), account.getGivenName(), account.getFamilyName(), account.getIdToken());
        }
    }

    private void hideKeyboard(View v) {
        Context context = v.getContext();
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }
}