package com.surveyfiesta.mroc.ui.grouplist;

import static com.surveyfiesta.mroc.constants.DefaultValues.BASE_SHARE_URL;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.adapters.GroupListAdapter;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.GroupChatRecyclerEntity;
import com.surveyfiesta.mroc.entities.GroupUsers;
import com.surveyfiesta.mroc.entities.UserGroupChatEntity;
import com.surveyfiesta.mroc.entities.UserResponse;
import com.surveyfiesta.mroc.entities.Users;
import com.surveyfiesta.mroc.helpers.GroupEditMenuCallback;
import com.surveyfiesta.mroc.interfaces.ChatGroupListener;
import com.surveyfiesta.mroc.interfaces.EditGroupDialogListener;
import com.surveyfiesta.mroc.interfaces.EditMenuActionItemListener;
import com.surveyfiesta.mroc.interfaces.JoinGroupDialogListener;
import com.surveyfiesta.mroc.ui.login.UserViewModel;
import com.surveyfiesta.mroc.viewmodels.SavedStateViewModel;

import java.util.ArrayList;
import java.util.List;

public class GroupListFragment extends Fragment implements ChatGroupListener, EditGroupDialogListener, EditMenuActionItemListener, JoinGroupDialogListener {
    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipeGroupContainer;
    private GroupListViewModel groupListViewModel;
    private GroupListAdapter groupListAdapter;

    private EditGroupDialogFragment editGroupDialogFragment;
    private JoinGroupDialogFragment joinGroupDialogFragment;

    private UserViewModel userViewModel;
    private SavedStateViewModel stateViewModel;
    private GroupEditMenuCallback callback;
    private ActionMode actionMode;
    private Users currentUser;
    private String userToken;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.group_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        groupListViewModel = new ViewModelProvider(this).get(GroupListViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(SavedStateViewModel.class);
        final NavController navController = Navigation.findNavController(view);
        currentUser = userViewModel.getCurrentUserData().getValue();

        userToken = stateViewModel.getCurrentUserToken();
        if (userToken == null || userToken.isEmpty()) {
            navController.navigate(R.id.loginFragment);
        } else {
            if (currentUser == null) {
                userViewModel.login(userToken);
            }
        }

        userViewModel.getLoginResult().observe(getViewLifecycleOwner(), result -> {
            if (!result.getResponseCode().equals(GenericResponse.ResponseCode.SUCCESSFUL)) {
                Snackbar.make(view, result.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
            } else {
                currentUser = result.getUser();
                UserResponse userResponse = new UserResponse(currentUser, userToken);
                if (groupListViewModel.getGroupChatListData().getValue() == null || groupListViewModel.getGroupChatListData().getValue().isEmpty()) {
                    groupListViewModel.findUserChats(userResponse);
                }
            }
        });

        groupListViewModel.getGroupChatListData().observe(getViewLifecycleOwner(), groupChatList -> {
            if (groupChatList != null) {
                recyclerView = view.findViewById(R.id.groupListRecycler);
                List<GroupChatRecyclerEntity> list = new ArrayList<>();
                groupChatList.forEach(i -> list.add(new GroupChatRecyclerEntity(i)));
                if (groupListAdapter == null) {
                    groupListAdapter = new GroupListAdapter(list, getContext(), this);
                } else {
                    groupListAdapter.setGroupList(list);
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(groupListAdapter);
            } else {
                Snackbar.make(getView(), "There seems to be a problem, please try again.", Snackbar.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).popBackStack();
            }
            swipeGroupContainer.setRefreshing(false);
        });

        groupListViewModel.getGenericResponseData().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                Snackbar.make(getView(), response.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
                UserResponse userResponse = new UserResponse(currentUser, userToken);
                groupListAdapter.clear();
                groupListViewModel.findUserChats(userResponse);
                groupListViewModel.setGenericResponseData(null);
            }
        });

        swipeGroupContainer = view.findViewById(R.id.swipeGroupContainer);
        swipeGroupContainer.setOnRefreshListener(() -> {
            UserResponse userResponse = new UserResponse(currentUser, userToken);
            groupListAdapter.clear();
            groupListViewModel.findUserChats(userResponse);
        });

        swipeGroupContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeGroupContainer.setRefreshing(true);
        callback = new GroupEditMenuCallback(this::onActionItemClicked);
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
            case R.id.button_join_group:
                showJoinGroupDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showJoinGroupDialog() {
        joinGroupDialogFragment = new JoinGroupDialogFragment();
        joinGroupDialogFragment.show(getChildFragmentManager(), "joinGroupDialog");
    }

    private void showNewGroupDialog() {
        UserGroupChatEntity chatEntity = new UserGroupChatEntity();
        List<GroupUsers> usersList = new ArrayList<>();
        GroupUsers groupUsers = new GroupUsers(currentUser, true);
        usersList.add(groupUsers);
        chatEntity.setGroupChat(new GroupChat());
        chatEntity.setGroupUsers(usersList);
        editGroupDialogFragment = new EditGroupDialogFragment(chatEntity);
        editGroupDialogFragment.show(getChildFragmentManager(),"newGroupDialog");
    }

    private void showEditGroupDialog(int position) {
        UserGroupChatEntity chatEntity = groupListAdapter.getGroupList().get(position).getChatEntity();
        boolean adminUser = chatEntity.getGroupUsers().stream()
                .filter(i-> i.getUser().getUserId().equals(currentUser.getUserId()))
                .anyMatch(i-> i.isAdminUser());
        if (adminUser) {
            editGroupDialogFragment = new EditGroupDialogFragment(chatEntity);
            editGroupDialogFragment.show(getChildFragmentManager(), "editGroupDialog");
        } else {
            Snackbar.make(getView(), "You cannot edit this group!", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRowClickListener(View view, int position) {
        UserGroupChatEntity chatEntity = groupListAdapter.getGroupList().get(position).getChatEntity();
        GroupChat groupChat = chatEntity.getGroupChat();
        stateViewModel.setCurrentChatUuid(groupChat.getGroupUuid());
        if (actionMode != null) {
            actionMode.finish();
        }
        Bundle bundle = new Bundle();
        bundle.putString("fragmentTitle",chatEntity.getGroupChat().getGroupName());
        Navigation.findNavController(getView()).navigate(R.id.action_groupListFragment_to_groupChatFragment, bundle);
    }

    @Override
    public void onButtonClickListener(View view, int position) {
        callback.setRowPosition(position);
        actionMode = view.startActionMode(callback);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String titleText, String descriptionText, boolean isEnabled) {
        if (titleText.isEmpty() || descriptionText.isEmpty()) {
            Snackbar.make(getView(),getString(R.string.title_description_needed), Snackbar.LENGTH_SHORT).show();
        } else {
            updateGroupChat(titleText, descriptionText, isEnabled);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    @Override
    public void onJoinDialogPositiveClick(DialogFragment dialog, String joinUrl) {
        UserGroupChatEntity chatEntity = new UserGroupChatEntity();
        String groupUuid = joinUrl.replace(BASE_SHARE_URL,"").toUpperCase();
        if (!groupUuid.isEmpty() && groupUuid.length() < 37) {
            List<GroupUsers> usersList = new ArrayList<>();
            GroupUsers groupUsers = new GroupUsers(currentUser, false);
            usersList.add(groupUsers);

            GroupChat chat = new GroupChat();
            chat.setGroupUuid(groupUuid);

            chatEntity.setUserToken(userToken);
            chatEntity.setGroupChat(chat);
            chatEntity.setGroupUsers(usersList);
            groupListViewModel.joinGroupByUuid(chatEntity);
        } else {
            Snackbar.make(getView(), "This doesn't seem to be a link",Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onJoinDialogNegativeClick(DialogFragment dialog) {
    }

    private void confirmLeaveChat(int position) {
        UserGroupChatEntity chatEntity = groupListAdapter.getGroupList().get(position).getChatEntity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure you want to exit the group " + chatEntity.getGroupChat().getGroupName())
                .setPositiveButton(R.string.leave, (dialog, which) -> leaveChat(position))
                .setNegativeButton(R.string.cancel,null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void leaveChat(int position) {
        UserGroupChatEntity chatEntity = groupListAdapter.getGroupList().get(position).getChatEntity();
        chatEntity.setUserToken(userToken);
        UserResponse userResponse = new UserResponse(currentUser, userToken);
        groupListViewModel.sendGroupChatRequest(chatEntity, GenericResponse.RequestTypes.LEAVE_GROUP);
        groupListAdapter.clear();
        groupListViewModel.findUserChats(userResponse);
    }

    private void updateGroupChat(String titleText, String descriptionText, boolean isEnabled) {
        UserGroupChatEntity chatEntity = editGroupDialogFragment.getChatEntity();
        chatEntity.getGroupChat().setGroupName(titleText);
        chatEntity.getGroupChat().setGroupDescription(descriptionText);
        chatEntity.getGroupChat().setGroupEnabled(isEnabled);
        GenericResponse.RequestTypes type;
        if (chatEntity.getGroupChat().getGroupId().equals(0)) {
            type = GenericResponse.RequestTypes.NEW_GROUP;
        } else {
            type = GenericResponse.RequestTypes.EDIT_GROUP;
        }
        groupListViewModel.sendGroupChatRequest(chatEntity, type);
    }

    private void shareItem(int position) {
        UserGroupChatEntity chatEntity = groupListAdapter.getGroupList().get(position).getChatEntity();
        if (chatEntity.getGroupChat().isGroupEnabled()) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, BASE_SHARE_URL + chatEntity.getGroupChat().getGroupUuid());
            sendIntent.putExtra(Intent.EXTRA_TITLE, "Share the link to this chat.");
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        } else {
            Snackbar.make(getView(), "This group is closed / disabled.", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onActionItemClicked(MenuItem item) {
        int position = callback.getRowPosition();
        switch (item.getItemId()) {
            case R.id.button_edit_group:
                showEditGroupDialog(position);
                break;
            case R.id.button_leave_group:
                confirmLeaveChat(position);
                break;
            case R.id.button_share_group:
                shareItem(position);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        if (actionMode != null) {
            actionMode.finish();
        }
        super.onDestroy();
    }
}