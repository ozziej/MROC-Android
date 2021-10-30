package com.surveyfiesta.mroc.viewmodels;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.InstantNotification;
import com.surveyfiesta.mroc.entities.Users;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketViewModel extends ViewModel {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private WebSocketListener socketListener;
    private WebSocket webSocket;
    private MutableLiveData<InstantNotification> notificationLiveData = new MutableLiveData<>();

    public void initWebSocket(GroupChat groupChat, Users currentUser) {
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        socketListener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d("Connected :",response.body().toString());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                final InstantNotification notification;
                try {
                    notification = objectMapper.readValue(text, new TypeReference<InstantNotification>() {
                    });
                    Integer groupId = notification.getRecipientId();
                    if (groupChat != null && groupChat.getGroupId().equals(groupId)) {
                        notificationLiveData.postValue(notification);
                    }
                } catch (JsonProcessingException ex) {
                    Log.e("Error in JSON processing", ex.getLocalizedMessage());
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d("Closed:","Closed connection to "+groupChat.getGroupName());
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                Log.e("Error :",t.getLocalizedMessage());
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0,  TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url("ws://localhost:8080/SurveyFiesta/chat/"+currentUser.getFirstName())
                .build();
        webSocket = client.newWebSocket(request, socketListener);
        client.dispatcher().executorService().shutdown();
    }

    public MutableLiveData<InstantNotification> getNotificationLiveData() {
        return notificationLiveData;
    }

    public void sendMessage(String message) {
        webSocket.send(message);
    }

    public void closeSocket() {
        webSocket.close(1000,"Hide Chat");
    }
}
