package com.surveyfiesta.mroc.ui.grouplist;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.surveyfiesta.mroc.constants.DefaultValues;
import com.surveyfiesta.mroc.entities.GroupChat;
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
    MutableLiveData <GroupChat> selectedChatData = new MutableLiveData<>();

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

    public MutableLiveData<List<GroupChat>> getGroupChatData() {
        return groupChatData;
    }

    public GroupChat getSelectedChatData() {
        return selectedChatData.getValue();
    }

    public void setSelectedChatData(GroupChat selectedChat) {
        this.selectedChatData.setValue(selectedChat);
    }
}