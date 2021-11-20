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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.adapters.GroupListAdapter;
import com.surveyfiesta.mroc.constants.ChatGroupButtonType;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.GroupChatRecyclerEntity;
import com.surveyfiesta.mroc.entities.GroupUsers;
import com.surveyfiesta.mroc.entities.UserGroupChatEntity;
import com.surveyfiesta.mroc.entities.Users;
import com.surveyfiesta.mroc.interfaces.ChatGroupListener;
import com.surveyfiesta.mroc.interfaces.EditGroupDialogListener;
import com.surveyfiesta.mroc.ui.login.UserViewModel;
import com.surveyfiesta.mroc.viewmodels.SavedStateViewModel;

import java.util.ArrayList;
import java.util.List;

public class GroupListFragment extends Fragment implements ChatGroupListener, EditGroupDialogListener {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeGroupContainer;
    private GroupListViewModel groupListViewModel;
    private GroupListAdapter groupListAdapter;

    private EditGroupDialogFragment dialogFragment;

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
                List<GroupChatRecyclerEntity> list = new ArrayList<>();
                groupChatList.forEach(i -> list.add(new GroupChatRecyclerEntity(i)));
                if (groupListAdapter == null) {
                    groupListAdapter = new GroupListAdapter(list, getContext(), this::chatGroupListener);
                } else {
                    groupListAdapter.setGroupList(list);
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                ItemTouchHelper helper = new ItemTouchHelper(new DeleteGroupCallback(groupListAdapter));
                helper.attachToRecyclerView(recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(groupListAdapter);
                swipeGroupContainer.setRefreshing(false);
            }
        });

        swipeGroupContainer = view.findViewById(R.id.swipeGroupContainer);
        swipeGroupContainer.setOnRefreshListener(() -> {
            groupListAdapter.clear();
            groupListViewModel.findUserChats(currentUser);
        });

        swipeGroupContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
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
        UserGroupChatEntity chatEntity = new UserGroupChatEntity();
        List<GroupUsers> usersList = new ArrayList<>();
        GroupUsers groupUsers = new GroupUsers(currentUser.getUserId(), currentUser.getFirstName(), true);
        usersList.add(groupUsers);
        chatEntity.setGroupChat(new GroupChat());
        chatEntity.setGroupUsers(usersList);
        dialogFragment = new EditGroupDialogFragment(chatEntity);
        dialogFragment.show(getChildFragmentManager(),"newGroupDialog");
    }

    private void showEditGroupDialog(UserGroupChatEntity chatEntity) {
        boolean adminUser = chatEntity.getGroupUsers().stream()
                .filter(i-> i.getUserId().equals(currentUser.getUserId()))
                .anyMatch(i-> i.isAdminUser());
        if (adminUser) {
            dialogFragment = new EditGroupDialogFragment(chatEntity);
            dialogFragment.show(getChildFragmentManager(), "editGroupDialog");
        } else {
            Snackbar.make(getView(), "You cannot edit this group!", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void chatGroupListener(View view, int position, ChatGroupButtonType buttonType) {
        UserGroupChatEntity chatEntity = groupListAdapter.getGroupList().get(position).getChatEntity();
        GroupChat groupChat = chatEntity.getGroupChat();
        groupListViewModel.setSelectedChatData(groupChat);
        switch (buttonType) {
            case CLICK:
                if (groupChat != null) {
                    Navigation.findNavController(view).navigate(R.id.action_groupListFragment_to_groupChatFragment);
                } else {
                    Snackbar.make(view, "Nothing Selected!", Snackbar.LENGTH_SHORT).show();
                }
                break;
            case DELETE:
                groupListAdapter.deleteItem(position);
                leaveChat(chatEntity);
                break;
            case EDIT:
                groupListAdapter.closeMenu();
                showEditGroupDialog(chatEntity);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String titleText, String descriptionText) {
        if (titleText.isEmpty() || descriptionText.isEmpty()) {
            Snackbar.make(getView(),getString(R.string.title_description_needed), Snackbar.LENGTH_SHORT).show();
        } else {
            updateGroupChat(titleText, descriptionText);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    private void leaveChat(UserGroupChatEntity chatEntity) {
        groupListViewModel.sendGroupChatRequest(chatEntity, GenericResponse.RequestTypes.LEAVE_GROUP);
        groupListViewModel.getGenericResponseData().observe(getViewLifecycleOwner(), response -> {
            Snackbar.make(getView() , response.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
        });
    }

    private void updateGroupChat(String titleText, String descriptionText) {
        UserGroupChatEntity chatEntity = dialogFragment.getChatEntity();
        chatEntity.getGroupChat().setGroupName(titleText);
        chatEntity.getGroupChat().setGroupDescription(descriptionText);
        GenericResponse.RequestTypes type;
        if (chatEntity.getGroupChat().getGroupId().equals(0)) {
            type = GenericResponse.RequestTypes.NEW_GROUP;
        } else {
            type = GenericResponse.RequestTypes.EDIT_GROUP;
        }
        groupListViewModel.sendGroupChatRequest(chatEntity, type);
        groupListViewModel.getGenericResponseData().observe(getViewLifecycleOwner(), response -> {
            Snackbar.make(getView() , response.getResponseMessage(), Snackbar.LENGTH_SHORT).show();
        });
    }

}