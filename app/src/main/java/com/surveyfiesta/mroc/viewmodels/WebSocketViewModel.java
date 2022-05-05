package com.surveyfiesta.mroc.viewmodels;

import static com.surveyfiesta.mroc.constants.DefaultValues.BASE_WEB_SOCKET;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.surveyfiesta.mroc.entities.GenericResponse;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.entities.InstantNotification;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketViewModel extends ViewModel {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private WebSocket webSocket;
    private final MutableLiveData<InstantNotification> notificationLiveData = new MutableLiveData<>();
    private final MutableLiveData<GenericResponse> responseNotification = new MutableLiveData<>();

    public void initWebSocket(GroupChat groupChat, String userToken) {
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        WebSocketListener socketListener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                if (response.isSuccessful()) {
                    responseNotification.postValue(new GenericResponse(GenericResponse.ResponseCode.SUCCESSFUL, "Joined Group", GenericResponse.RequestCode.CHAT));
                } else {
                    responseNotification.postValue(new GenericResponse(GenericResponse.ResponseCode.ERROR, "Unable to Join", GenericResponse.RequestCode.CHAT));
                }
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
                responseNotification.postValue(new GenericResponse(GenericResponse.ResponseCode.SUCCESSFUL, "Ended Chat with "+groupChat.getGroupName(), GenericResponse.RequestCode.CHAT));
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
                responseNotification.postValue(new GenericResponse(GenericResponse.ResponseCode.ERROR, "Unable to Join", GenericResponse.RequestCode.CHAT));
                Log.e("Error :",t.getLocalizedMessage());
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0,  TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url(BASE_WEB_SOCKET+ "/" + groupChat.getGroupId() + "/" + userToken)
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
        if (webSocket != null) {
            webSocket.close(1000, "Hide Chat");
        }
    }
}
