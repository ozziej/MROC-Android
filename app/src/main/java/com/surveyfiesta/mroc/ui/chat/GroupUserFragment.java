package com.surveyfiesta.mroc.ui.chat;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.adapters.GroupUserListAdapter;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.GroupUsers;
import com.surveyfiesta.mroc.entities.UserGroupChatEntity;
import com.surveyfiesta.mroc.entities.Users;
import com.surveyfiesta.mroc.helpers.UserEditMenuCallback;
import com.surveyfiesta.mroc.interfaces.EditMenuActionItemListener;
import com.surveyfiesta.mroc.interfaces.GroupUserListener;
import com.surveyfiesta.mroc.ui.grouplist.GroupListViewModel;
import com.surveyfiesta.mroc.ui.login.UserViewModel;
import com.surveyfiesta.mroc.viewmodels.SavedStateViewModel;

import java.util.ArrayList;
import java.util.List;

public class GroupUserFragment extends Fragment implements GroupUserListener, EditMenuActionItemListener {
    private UserViewModel userViewModel;
    private GroupChat groupChat;
    private List<GroupUsers> groupUsersList;
    private GroupUserListAdapter userListAdapter;
    private GroupListViewModel groupListViewModel;
    private SwipeRefreshLayout swipeGroupUserContainer;
    private RecyclerView recyclerView;
    private ActionMode actionMode;
    private UserEditMenuCallback callback;
    private Users currentUser;

    private SavedStateViewModel stateViewModel;
    private GroupChatViewModel groupChatViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.group_user_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController navController = Navigation.findNavController(getView());

        groupListViewModel = new ViewModelProvider(this).get(GroupListViewModel.class);
        groupChatViewModel = new ViewModelProvider(this).get(GroupChatViewModel.class);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(SavedStateViewModel.class);

        recyclerView = view.findViewById(R.id.groupUserListRecycler);
        callback = new UserEditMenuCallback(this::onActionItemClicked);

        currentUser = userViewModel.getCurrentUserData().getValue();

        String groupChatUuid = stateViewModel.getCurrentChatUuid();

        if (groupChatUuid == null || currentUser == null) {
            navController.popBackStack();
        }

        swipeGroupUserContainer = view.findViewById(R.id.swipeGroupUserContainer);
        swipeGroupUserContainer.setOnRefreshListener(() -> {

        });

        swipeGroupUserContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        groupListViewModel.getGenericResponseData().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                Snackbar.make(getView(), response.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });

        groupChatViewModel.findGroupChat(groupChatUuid);
        groupChatViewModel.getGroupChatData().observe(getViewLifecycleOwner(), l -> {
            if (l != null) {
                groupChat = l.getGroupChat();
                groupUsersList = l.getGroupUsers();
                userListAdapter = new GroupUserListAdapter(groupUsersList, getContext(), this);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(userListAdapter);
            }
        });
    }

    @Override
    public void onUserRowClickListener(View view, int position) {
        callback.setRowPosition(position);
        actionMode = view.startActionMode(callback);
        int previousItem = userListAdapter.getSelectedPos();
        userListAdapter.notifyItemChanged(previousItem);
        userListAdapter.setSelectedPos(position);
        userListAdapter.notifyItemChanged(position);
    }

    @Override
    public boolean onActionItemClicked(MenuItem item) {
        int position = callback.getRowPosition();

        switch (item.getItemId()) {
            case R.id.button_user_leave_group:
                editGroupUsers(position, false, true);
                break;
            case R.id.button_set_as_admin:
                editGroupUsers(position, true, false);
                break;
            case R.id.button_set_as_user:
                editGroupUsers(position, false, false);
                break;
            default:
                break;
        }
        return false;
    }

    private void editGroupUsers(int position, boolean setAsAdmin, boolean removeUser) {
        List<GroupUsers> modifiedUsers = new ArrayList<>();
        GroupUsers modifiedUser = groupUsersList.get(position);
        modifiedUser.setAdminUser(setAsAdmin);
        modifiedUsers.add(modifiedUser);
        UserGroupChatEntity chatEntity = new UserGroupChatEntity(groupChat, modifiedUsers);

        if (currentUser.getUserId().equals(modifiedUser.getUserId())){
            Snackbar.make(getView(), "You cannot modify yourself.",Snackbar.LENGTH_SHORT).show();
        } else {
            if (removeUser) {
                groupUsersList.remove(position);
                groupListViewModel.sendGroupChatRequest(chatEntity, GenericResponse.RequestTypes.LEAVE_GROUP);
            } else {
                groupUsersList.set(position, modifiedUser);
                groupListViewModel.sendGroupChatRequest(chatEntity, GenericResponse.RequestTypes.EDIT_GROUP_USERS);
            }
            userListAdapter.setSelectedPos(RecyclerView.NO_POSITION);
            userListAdapter.notifyItemChanged(position);
        }
    }
}
