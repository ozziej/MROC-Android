package com.surveyfiesta.mroc.interfaces;

import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.Users;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GroupChatService {
    @POST("groupList")
    Call<List<GroupChat>> findGroupList(@Body Users user);

}
