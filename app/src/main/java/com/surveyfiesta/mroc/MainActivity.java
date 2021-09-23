package com.surveyfiesta.mroc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.surveyfiesta.mroc.ui.grouplist.GroupListViewModel;
import com.surveyfiesta.mroc.ui.login.LoginFragment;
import com.surveyfiesta.mroc.ui.login.UserViewModel;

public class MainActivity extends AppCompatActivity {
    private GroupListViewModel groupListViewModel;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.profileFragment,
                R.id.groupListFragment
        ).build();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        groupListViewModel = new ViewModelProvider(this).get(GroupListViewModel.class);

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation_view);
        NavHostFragment navHostFragment = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        NavBackStackEntry navBackStackEntry = navController.getCurrentBackStackEntry();
        SavedStateHandle savedStateHandle = navBackStackEntry.getSavedStateHandle();

        savedStateHandle.getLiveData(LoginFragment.LOGIN_SUCCESSFUL)
                .observe(navBackStackEntry, success -> {
                    if (!(Boolean)success){
                        int startDestination = navController.getGraph().getStartDestination();
                        NavOptions navOptions = new NavOptions.Builder()
                                .setPopUpTo(startDestination, true)
                                .build();
                        navController.navigate(startDestination, null, navOptions);
                    }
                });
    }
}