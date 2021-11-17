package com.surveyfiesta.mroc.interfaces;

import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.GroupUsers;
import com.surveyfiesta.mroc.entities.InstantNotification;
import com.surveyfiesta.mroc.entities.UserGroupChatRequest;
import com.surveyfiesta.mroc.entities.Users;

import java.util.List;
import java.util.Set;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GroupChatService {
    @POST("findGroupList")
    Call<List<GroupChat>> findGroupList(@Body Users user);

    @POST("getGroupMessages")
    Call<List<InstantNotification>> getGroupMessages(@Body GroupChat group);

    @POST("createGroup")
    Call<GenericResponse> createGroup(@Body UserGroupChatRequest chatRequest);

    @POST("addToGroup")
    Call<GenericResponse> addToGroup(@Body GroupUsers groupUsers);

    @POST("findGroupMembers")
    Call<Set<GroupUsers>> findGroupMembers(@Body GroupChat groupChat);

    @POST("updateNickname")
    Call<GenericResponse> updateNickname(@Body GroupUsers groupUsers);

    @POST("leaveGroup")
    Call<GenericResponse> leaveGroup(@Body UserGroupChatRequest chatRequest);

}
