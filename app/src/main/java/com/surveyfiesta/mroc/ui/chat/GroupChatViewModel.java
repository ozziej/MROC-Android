package com.surveyfiesta.mroc.ui.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.surveyfiesta.mroc.constants.DefaultValues;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.InstantNotification;
import com.surveyfiesta.mroc.interfaces.GroupChatService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class GroupChatViewModel extends ViewModel {
    MutableLiveData <List<InstantNotification>> notificationLiveDate = new MutableLiveData<>();

    public GroupChatViewModel() {
    }

    public void findGroupChatMessages(GroupChat selectedGroup) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DefaultValues.BASE_CHAT_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        GroupChatService service = retrofit.create(GroupChatService.class);

        if (selectedGroup != null) {
            Call<List<InstantNotification>> call = service.getGroupMessages(selectedGroup);
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

    public MutableLiveData<List<InstantNotification>> getNotificationLiveDate() {
        return notificationLiveDate;
    }
}