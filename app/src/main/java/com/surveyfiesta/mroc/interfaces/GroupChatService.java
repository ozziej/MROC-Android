package com.surveyfiesta.mroc.interfaces;

import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.GroupChatItemRequest;
import com.surveyfiesta.mroc.entities.InstantNotification;
import com.surveyfiesta.mroc.entities.UserGroupChatEntity;
import com.surveyfiesta.mroc.entities.UserResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GroupChatService {
    @POST("findGroupList")
    Call<List<UserGroupChatEntity>> findGroupList(@Body UserResponse userResponse);

    @POST("findGroupByUuid")
    Call<UserGroupChatEntity> findGroupByUuid(@Body UserGroupChatEntity chatRequest);

    @POST("joinGroupByUuid")
    Call<GenericResponse> joinGroupByUuid(@Body UserGroupChatEntity chatRequest);

    @POST("getGroupMessages")
    Call<List<InstantNotification>> getGroupMessages(@Body GroupChatItemRequest itemRequest);

    @POST("createGroup")
    Call<GenericResponse> createGroup(@Body UserGroupChatEntity chatRequest);

    @POST("editGroup")
    Call<GenericResponse> editGroup(@Body UserGroupChatEntity chatRequest);

    @POST("leaveGroup")
    Call<GenericResponse> leaveGroup(@Body UserGroupChatEntity chatRequest);

    @POST("editGroupUsers")
    Call<GenericResponse> editGroupUsers(@Body UserGroupChatEntity chatRequest);

}
