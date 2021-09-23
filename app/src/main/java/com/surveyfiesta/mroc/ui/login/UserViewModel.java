package com.surveyfiesta.mroc.ui.login;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.surveyfiesta.mroc.constants.DefaultValues;
import com.surveyfiesta.mroc.entities.UserLoginRequest;
import com.surveyfiesta.mroc.entities.Users;
import com.surveyfiesta.mroc.interfaces.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class UserViewModel extends ViewModel {
    MutableLiveData<Users> currentUserData = new MutableLiveData<>();
    MutableLiveData<String> loginResult = new MutableLiveData<>();

    public UserViewModel() {
        this.currentUserData.setValue(null);
    }

    public void login (String emailAddress, String password){
        UserLoginRequest request = new UserLoginRequest(emailAddress, password);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DefaultValues.BASE_USERS_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        UserService service = retrofit.create(UserService.class);
        Call<Users> call = service.loginUser(request);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful()) {
                    currentUserData.setValue(response.body());
                    loginResult.setValue("SUCCESS");
                } else {
                    currentUserData.setValue(null);
                    loginResult.setValue("FAILED");
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                currentUserData.setValue(null);
                loginResult.setValue("FAILED");
            }
        });
    }

    public MutableLiveData<Users> getCurrentUserData() {
        return currentUserData;
    }

    public MutableLiveData<String> getLoginResult() {
        return loginResult;
    }
}
