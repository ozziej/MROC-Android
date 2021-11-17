package com.surveyfiesta.mroc.ui.grouplist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.adapters.GroupListAdapater;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.GroupUsers;
import com.surveyfiesta.mroc.entities.Users;
import com.surveyfiesta.mroc.interfaces.ChatGroupListener;
import com.surveyfiesta.mroc.interfaces.EditGroupDialogListener;
import com.surveyfiesta.mroc.ui.login.UserViewModel;
import com.surveyfiesta.mroc.viewmodels.SavedStateViewModel;

import java.util.ArrayList;
import java.util.List;

public class GroupListFragment extends Fragment implements ChatGroupListener, EditGroupDialogListener {
    private RecyclerView recyclerView;

    private GroupListViewModel groupListViewModel;
    private GroupListAdapater groupListAdapater;

    private UserViewModel userViewModel;
    private SavedStateViewModel stateViewModel;

    private Users currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.group_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        groupListViewModel = new ViewModelProvider(requireActivity()).get(GroupListViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(SavedStateViewModel.class);
        final NavController navController = Navigation.findNavController(view);
        currentUser = userViewModel.getCurrentUserData().getValue();

        Integer userId = stateViewModel.getCurrentUserId().getValue();
        if (userId == null) {
            navController.navigate(R.id.loginFragment);
        } else {
            if (currentUser == null) {
                userViewModel.login(userId);
            }
        }

        userViewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (!result.getResponseCode().equals(GenericResponse.ResponseCode.SUCCESSFUL)) {
                Snackbar.make(view, result.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
            } else {
                currentUser = result.getUser();
                if (groupListViewModel.getGroupChatData().getValue() == null || groupListViewModel.getGroupChatData().getValue().isEmpty()) {
                    groupListViewModel.findUserChats(currentUser);
                }
            }
        });

        groupListViewModel.getGroupChatData().observe(getViewLifecycleOwner(), groupChatList -> {
            if (groupChatList != null) {
                recyclerView = view.findViewById(R.id.groupListRecycler);
                groupListAdapater = new GroupListAdapater(groupChatList, getContext(), this::chatGroupListener);

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                ItemTouchHelper helper = new ItemTouchHelper(new DeleteGroupCallback(groupListAdapater));
                helper.attachToRecyclerView(recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(groupListAdapater);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.group_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button_new_group:
                showNewGroupDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNewGroupDialog() {
        EditGroupDialogFragment dialogFragment = new EditGroupDialogFragment();
        dialogFragment.show(getChildFragmentManager(),"newGroupDialog");
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

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String titleText, String descriptionText) {
        if (titleText.isEmpty() || descriptionText.isEmpty()){
            Snackbar.make(getView(),getString(R.string.title_description_needed), Snackbar.LENGTH_SHORT).show();
        } else {
            createNewGroup(this.getView(), titleText, descriptionText);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    private void createNewGroup(View view, String titleText, String descriptionText) {
        GroupChat groupChat = new GroupChat();
        groupChat.setGroupName(titleText);
        groupChat.setGroupDescription(descriptionText);
        groupChat.setGroupImageUrl("NONE");
        GroupUsers groupUsers = new GroupUsers(currentUser.getUserId(), currentUser.getFirstName(), true);
        groupListViewModel.createNewUserChat(groupChat, groupUsers);
        groupListViewModel.getGenericResponseData().observe(getViewLifecycleOwner(), response -> {
            Snackbar.make(view , response.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
        });
    }

}