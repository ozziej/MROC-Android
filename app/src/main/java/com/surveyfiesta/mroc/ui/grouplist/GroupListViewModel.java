package com.surveyfiesta.mroc.ui.grouplist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.surveyfiesta.mroc.constants.DefaultValues;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.UserGroupChatEntity;
import com.surveyfiesta.mroc.entities.UserResponse;
import com.surveyfiesta.mroc.interfaces.GroupChatService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class GroupListViewModel extends ViewModel {
    private final MutableLiveData <List<UserGroupChatEntity>> groupChatListData = new MutableLiveData<>();
    private final MutableLiveData <UserGroupChatEntity> groupChatData = new MutableLiveData<>();
    private MutableLiveData <GroupChat> selectedChatData = new MutableLiveData<>();
    private MutableLiveData <GenericResponse> genericResponseData = new MutableLiveData<>();

    public GroupListViewModel() {
    }

    public void findUserChats(UserResponse userResponse) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DefaultValues.BASE_CHAT_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        GroupChatService service = retrofit.create(GroupChatService.class);

        Call<List<UserGroupChatEntity>> call = service.findGroupList(userResponse);
        call.enqueue(new Callback<List<UserGroupChatEntity>>() {
            @Override
            public void onResponse(Call<List<UserGroupChatEntity>> call, Response<List<UserGroupChatEntity>> response) {
                if (response.isSuccessful()) {
                    groupChatListData.setValue(response.body());
                } else {
                    groupChatListData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<UserGroupChatEntity>> call, Throwable t) {
                groupChatListData.setValue(null);
            }
        });
    }

    public void joinGroupByUuid(UserGroupChatEntity chatRequest) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DefaultValues.BASE_CHAT_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        GroupChatService service = retrofit.create(GroupChatService.class);
        Call<GenericResponse> call = service.joinGroupByUuid(chatRequest);
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

    public void sendGroupChatRequest(UserGroupChatEntity chatEntity, GenericResponse.RequestTypes requestType){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DefaultValues.BASE_CHAT_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        GroupChatService service = retrofit.create(GroupChatService.class);
        Call<GenericResponse> call = null;
        switch (requestType) {
            case NEW_GROUP:
                call = service.createGroup(chatEntity);
                break;
            case EDIT_GROUP:
                call = service.editGroup(chatEntity);
                break;
            case LEAVE_GROUP:
                call = service.leaveGroup(chatEntity);
                break;
            case EDIT_GROUP_USERS:
                call = service.editGroupUsers(chatEntity);
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

    public LiveData<List<UserGroupChatEntity>> getGroupChatListData() {
        return groupChatListData;
    }

    public LiveData<UserGroupChatEntity> getGroupChatData() {
        return groupChatData;
    }

    public void setSelectedChatData(MutableLiveData<GroupChat> selectedChatData) {
        this.selectedChatData = selectedChatData;
    }

    public GroupChat getSelectedChatData() {
        return selectedChatData.getValue();
    }

    public void setSelectedChatData(GroupChat selectedChat) {
        this.selectedChatData.postValue(selectedChat);
    }

    public MutableLiveData<GenericResponse> getGenericResponseData() {
        return genericResponseData;
    }

    public void setGenericResponseData(GenericResponse genericResponse) {
        this.genericResponseData.postValue(genericResponse);
    }
}