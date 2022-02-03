package com.surveyfiesta.mroc.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.surveyfiesta.mroc.constants.DefaultValues;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.InstantNotification;
import com.surveyfiesta.mroc.entities.UserGroupChatEntity;
import com.surveyfiesta.mroc.interfaces.GroupChatService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class GroupChatViewModel extends ViewModel {
    private MutableLiveData <List<InstantNotification>> notificationLiveDate = new MutableLiveData<>();
    private MutableLiveData <UserGroupChatEntity> groupChatData ;

    public GroupChatViewModel() {
    }

    public void findGroupChatMessages(UserGroupChatEntity chatEntity) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DefaultValues.BASE_CHAT_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        GroupChatService service = retrofit.create(GroupChatService.class);

        if (chatEntity != null) {
            Call<List<InstantNotification>> call = service.getGroupMessages(chatEntity);
            call.enqueue(new Callback<List<InstantNotification>>() {
                @Override
                public void onResponse(Call<List<InstantNotification>> call, Response<List<InstantNotification>> response) {
                    if (response.isSuccessful()){
                       notificationLiveDate.setValue(response.body());
                    } else {
                        notificationLiveDate.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<List<InstantNotification>> call, Throwable t) {
                    notificationLiveDate.setValue(null);
                }
            });
        }
    }

    public void findGroupChat(UserGroupChatEntity chatRequest) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DefaultValues.BASE_CHAT_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        GroupChatService service = retrofit.create(GroupChatService.class);
        Call<UserGroupChatEntity> call = service.findGroupByUuid(chatRequest);
        call.enqueue(new Callback<UserGroupChatEntity>() {
            @Override
            public void onResponse(Call<UserGroupChatEntity> call, Response<UserGroupChatEntity> response) {
                if (response.isSuccessful()) {
                    groupChatData.setValue(response.body());
                } else {
                    groupChatData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<UserGroupChatEntity> call, Throwable t) {
                groupChatData.setValue(null);
            }
        });
    }

    public MutableLiveData<List<InstantNotification>> getNotificationLiveDate() {
        return notificationLiveDate;
    }

    public LiveData<UserGroupChatEntity> getGroupChatData() {
        if (groupChatData == null) {
            groupChatData = new MutableLiveData<>();
        }
        return groupChatData;
    }
}