package com.surveyfiesta.mroc.ui.login;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.surveyfiesta.mroc.constants.DefaultValues;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.UserLoginRequest;
import com.surveyfiesta.mroc.entities.UserResponse;
import com.surveyfiesta.mroc.entities.Users;
import com.surveyfiesta.mroc.interfaces.UserService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
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

    public void login (String userToken) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DefaultValues.BASE_USERS_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        Map<String, String> userMap = new HashMap<>();
        userMap.put("userToken", userToken);
        RequestBody body = RequestBody.create(new JSONObject(userMap).toString(), MediaType.parse("application/json; charset=utf-8"));
        UserService service = retrofit.create(UserService.class);
        loginUser(service.loginUser(body));
    }

    public void signInWithGoogle (String emailAddress, String firstName, String surname) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DefaultValues.BASE_USERS_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        Map<String, String> userMap = new HashMap<>();
        userMap.put("emailAddress", emailAddress);
        userMap.put("firstName", firstName);
        userMap.put("surname", surname);
        RequestBody body = RequestBody.create(new JSONObject(userMap).toString(), MediaType.parse("application/json; charset=utf-8"));
        UserService service = retrofit.create(UserService.class);
        loginUser(service.signInWithGoogle(body));
    }

    public void login (String emailAddress, String password) {
        UserLoginRequest request = new UserLoginRequest(emailAddress, password);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DefaultValues.BASE_USERS_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        UserService service = retrofit.create(UserService.class);
        loginUser(service.loginUser(request));
    }

    private void loginUser(Call<UserResponse> call) {
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
                    loginResult.setValue(new UserResponse(GenericResponse.ResponseCode.ERROR, "Something Went Wrong : "+response.message(), GenericResponse.RequestCode.USER, null, "null"));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                currentUserData.setValue(null);
                loginResult.setValue(new UserResponse(GenericResponse.ResponseCode.ERROR, "Something Went Wrong", GenericResponse.RequestCode.USER, null, "null"));
            }
        });
    }

    public void updateUserDetails(String userToken) {
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
        UserResponse userResponse = new UserResponse(user, userToken);
        Call<UserResponse> call = service.updateUser(userResponse);
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
                        updateResult.setValue(new UserResponse(GenericResponse.ResponseCode.ERROR, "Something Went Wrong : "+response.message(), GenericResponse.RequestCode.USER, user,null));
                    }
                } else {
                    updateResult.setValue(new UserResponse(GenericResponse.ResponseCode.ERROR, "Something Went Wrong : "+response.message(), GenericResponse.RequestCode.USER, user, null));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                updateResult.setValue(new UserResponse(GenericResponse.ResponseCode.ERROR, "Something Went Wrong : "+t.getLocalizedMessage(), GenericResponse.RequestCode.USER, user, null));
            }
        });
    }

    public MutableLiveData<Users> getCurrentUserData() {
        return currentUserData;
    }

    public void setCurrentUserData(Users user){
        currentUserData.postValue(user);
    }

    public MutableLiveData<UserResponse> getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(UserResponse loginResult) {
        this.loginResult.setValue(loginResult);
    }

    public MutableLiveData<UserResponse> getUpdateResult() {
        return updateResult;
    }
}
