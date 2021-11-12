package com.surveyfiesta.mroc.ui.grouplist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.adapters.GroupListAdapater;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.Users;
import com.surveyfiesta.mroc.interfaces.ChatGroupListener;
import com.surveyfiesta.mroc.ui.login.UserViewModel;
import com.surveyfiesta.mroc.viewmodels.SavedStateViewModel;

import java.util.ArrayList;

public class GroupListFragment extends Fragment implements ChatGroupListener {
    private RecyclerView recyclerView;

    private GroupListViewModel groupListViewModel;
    private GroupListAdapater groupListAdapater;

    private UserViewModel userViewModel;
    private SavedStateViewModel stateViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.group_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        groupListViewModel = new ViewModelProvider(requireActivity()).get(GroupListViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(SavedStateViewModel.class);
        final NavController navController = Navigation.findNavController(view);
        Users currentUser = userViewModel.getCurrentUserData().getValue();

        Integer userId = stateViewModel.getCurrentUserId().getValue();
        if (userId == null) {
            navController.navigate(R.id.loginFragment);
        } else {
            if (currentUser == null) {
                loginUser(userId, view);
            } else {
                drawList(currentUser, view);
            }
        }
    }

    private void loginUser(Integer userId, @NonNull View view) {
        userViewModel.login(userId);
        userViewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (!result.getResponseCode().equals(GenericResponse.ResponseCode.SUCCESSFUL)) {
                Snackbar.make(view, result.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
            } else {
                drawList(result.getUser(), view);
            }
        });
    }

    private void drawList(Users currentUser, @NonNull View view){
        recyclerView = view.findViewById(R.id.groupListRecycler);
        groupListAdapater = new GroupListAdapater(new ArrayList<>(), getContext(), this::chatGroupListener);
        recyclerView.setAdapter(groupListAdapater);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        groupListViewModel.findUserChats(currentUser);

        groupListViewModel.getGroupChatData().observe(getViewLifecycleOwner(), groupChatList -> {
            if (groupChatList != null) {
                groupListAdapater = new GroupListAdapater(groupChatList, getContext(), this::chatGroupListener);
                recyclerView.setAdapter(groupListAdapater);
            }
        });
    }

    @Override
    public void chatGroupListener(View view, int position) {
        GroupChat groupChat = groupListAdapater.getGroupList().get(position);
        groupListViewModel.setSelectedChatData(groupChat);

        if (groupChat != null) {
            Navigation.findNavController(view).navigate(R.id.action_groupListFragment_to_groupChatFragment);
        } else {
            Snackbar.make(view, "Nothing Selected!", Snackbar.LENGTH_SHORT).show();
        }
    }
}