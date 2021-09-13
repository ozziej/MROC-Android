package com.surveyfiesta.mroc.interfaces;

import com.surveyfiesta.mroc.entities.UserLoginRequest;
import com.surveyfiesta.mroc.entities.Users;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {
    @POST("login")
    Call<Users> loginUser(@Body UserLoginRequest request);

    @POST("update")
    Call<Users> updateUser(@Body Users users);
}
