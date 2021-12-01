package com.surveyfiesta.mroc.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class SavedStateViewModel extends ViewModel {
    private SavedStateHandle stateHandle;
    public static final String CURRENT_USER_ID = "CURRENT_USER_ID";
    public static final String CURRENT_CHAT_UUID = "CURRENT_CHAT_UUID";

    public SavedStateViewModel(SavedStateHandle stateHandle) {
        this.stateHandle = stateHandle;
    }

    public String getCurrentUserToken() {
        LiveData<String> currentUserToken = stateHandle.getLiveData(CURRENT_USER_ID);
        if (currentUserToken == null || currentUserToken.getValue() == null){
            return null;
        }
        return currentUserToken.getValue();
    }

    public void setCurrentUserToken(String currentUserToken) {
        stateHandle.set(CURRENT_USER_ID, currentUserToken);
    }

    public String getCurrentChatUuid() {
        LiveData<String> currentChatUuid = stateHandle.getLiveData(CURRENT_CHAT_UUID);
        if (currentChatUuid == null || currentChatUuid.getValue() == null){
            return null;
        }
        return currentChatUuid.getValue();
    }

    public void setCurrentChatUuid(String currentChatUuid) {
        stateHandle.set(CURRENT_CHAT_UUID,currentChatUuid);
    }
}