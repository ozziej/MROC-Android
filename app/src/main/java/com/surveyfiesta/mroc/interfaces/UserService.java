package com.surveyfiesta.mroc.interfaces;

import com.surveyfiesta.mroc.entities.UserLoginRequest;
import com.surveyfiesta.mroc.entities.UserResponse;
import com.surveyfiesta.mroc.entities.Users;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("login")
    Call<UserResponse> loginUser(@Body UserLoginRequest request);

    @POST("login")
    Call<UserResponse> loginUser(@Body RequestBody body);

    @POST("update")
    Call<UserResponse> updateUser(@Body Users users);
}
