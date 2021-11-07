package com.surveyfiesta.mroc.ui.login;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.surveyfiesta.mroc.constants.DefaultValues;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.UserLoginRequest;
import com.surveyfiesta.mroc.entities.UserResponse;
import com.surveyfiesta.mroc.entities.Users;
import com.surveyfiesta.mroc.interfaces.UserService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class UserViewModel extends ViewModel {
    MutableLiveData<Users> currentUserData = new MutableLiveData<>();
    MutableLiveData<UserResponse> loginResult = new MutableLiveData<>();
    MutableLiveData<UserResponse> updateResult = new MutableLiveData<>();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public UserViewModel() {
        this.currentUserData.setValue(null);
    }

    public void login (String emailAddress, String password){
        UserLoginRequest request = new UserLoginRequest(emailAddress, password);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DefaultValues.BASE_USERS_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        UserService service = retrofit.create(UserService.class);
        Call<UserResponse> call = service.loginUser(request);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    loginResult.setValue(userResponse);
                    if (response.isSuccessful()) {
                        Users user = userResponse.getUser();
                        currentUserData.setValue(user);
                    } else {
                        currentUserData.setValue(null);
                    }
                } else {
                    currentUserData.setValue(null);
                    loginResult.setValue(new UserResponse(GenericResponse.ResponseCode.ERROR, "Something Went Wrong : "+response.message(), GenericResponse.RequestCode.USER, null));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                currentUserData.setValue(null);
                loginResult.setValue(new UserResponse(GenericResponse.ResponseCode.ERROR, "Something Went Wrong", GenericResponse.RequestCode.USER, null));
            }
        });
    }

    public void updateUserDetails() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DefaultValues.BASE_USERS_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        UserService service = retrofit.create(UserService.class);
        Users user = currentUserData.getValue();
        Call<UserResponse> call = service.updateUser(user);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                UserResponse userResponse = response.body();
                if (userResponse != null) {
                    updateResult.setValue(userResponse);
                    if (response.isSuccessful()) {
                        Users user = userResponse.getUser();
                        currentUserData.setValue(user);
                    } else {
                        updateResult.setValue(new UserResponse(GenericResponse.ResponseCode.ERROR, "Something Went Wrong : "+response.message(), GenericResponse.RequestCode.USER, user));
                    }
                } else {
                    updateResult.setValue(new UserResponse(GenericResponse.ResponseCode.ERROR, "Something Went Wrong : "+response.message(), GenericResponse.RequestCode.USER, user));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                updateResult.setValue(new UserResponse(GenericResponse.ResponseCode.ERROR, "Something Went Wrong : "+t.getLocalizedMessage(), GenericResponse.RequestCode.USER, user));
            }
        });
    }

    public MutableLiveData<Users> getCurrentUserData() {
        return currentUserData;
    }

    public MutableLiveData<UserResponse> getLoginResult() {
        return loginResult;
    }

    public MutableLiveData<UserResponse> getUpdateResult() {
        return updateResult;
    }
}
