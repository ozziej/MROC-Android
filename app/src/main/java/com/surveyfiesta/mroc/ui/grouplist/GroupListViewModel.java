package com.surveyfiesta.mroc.ui.grouplist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.surveyfiesta.mroc.constants.DefaultValues;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.GroupUsers;
import com.surveyfiesta.mroc.entities.UserGroupChatRequest;
import com.surveyfiesta.mroc.entities.Users;
import com.surveyfiesta.mroc.interfaces.GroupChatService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class GroupListViewModel extends ViewModel {
    private final MutableLiveData<List<GroupChat>> groupChatData = new MutableLiveData<>();
    private MutableLiveData <GroupChat> selectedChatData = new MutableLiveData<>();
    private MutableLiveData<GenericResponse> genericResponseData = new MutableLiveData<>();

    public GroupListViewModel() {
    }

    public void findUserChats(Users selectedUser) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DefaultValues.BASE_CHAT_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        GroupChatService service = retrofit.create(GroupChatService.class);

        Call<List<GroupChat>> call = service.findGroupList(selectedUser);
        call.enqueue(new Callback<List<GroupChat>>() {
            @Override
            public void onResponse(Call<List<GroupChat>> call, Response<List<GroupChat>> response) {
                if (response.isSuccessful()) {
                    groupChatData.setValue(response.body());
                } else {
                    groupChatData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<GroupChat>> call, Throwable t) {
                groupChatData.setValue(null);
            }
        });
    }

    public void createNewUserChat(GroupChat groupChat, GroupUsers groupUsers) {
        sendGroupChatRequest(groupChat, groupUsers, GenericResponse.RequestTypes.CREATE_GROUP);
    }

    public void leaveGroup(GroupChat groupChat, GroupUsers groupUsers) {
        sendGroupChatRequest(groupChat, groupUsers, GenericResponse.RequestTypes.LEAVE_GROUP);
    }

    private void sendGroupChatRequest(GroupChat groupChat, GroupUsers groupUsers, GenericResponse.RequestTypes requestType){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DefaultValues.BASE_CHAT_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        UserGroupChatRequest chatRequest = new UserGroupChatRequest(groupUsers, groupChat);
        GroupChatService service = retrofit.create(GroupChatService.class);
        Call<GenericResponse> call = null;
        switch (requestType) {
            case CREATE_GROUP :
                call = service.createGroup(chatRequest);
            break;
            case LEAVE_GROUP:
                call = service.leaveGroup(chatRequest);
                break;
            default:
                break;
        }

        if (call != null) {
            call.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    genericResponseData.setValue(response.body());
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    GenericResponse response = new GenericResponse();
                    response.setResponseMessage(t.getLocalizedMessage());
                    response.setResponseCode(GenericResponse.ResponseCode.ERROR);
                    response.setRequestCode(GenericResponse.RequestCode.CHAT);
                    genericResponseData.setValue(response);
                }
            });
        }
    }


    public MutableLiveData<List<GroupChat>> getGroupChatData() {
        return groupChatData;
    }

    public void setSelectedChatData(MutableLiveData<GroupChat> selectedChatData) {
        this.selectedChatData = selectedChatData;
    }

    public GroupChat getSelectedChatData() {
        return selectedChatData.getValue();
    }

    public void setSelectedChatData(GroupChat selectedChat) {
        this.selectedChatData.setValue(selectedChat);
    }

    public MutableLiveData<GenericResponse> getGenericResponseData() {
        return genericResponseData;
    }
}