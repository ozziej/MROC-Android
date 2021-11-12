package com.surveyfiesta.mroc.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class SavedStateViewModel extends ViewModel {
    private SavedStateHandle stateHandle;
    public static final String CURRENT_USER_ID = "CURRENT_USER_ID";

    public SavedStateViewModel(SavedStateHandle stateHandle) {
        this.stateHandle = stateHandle;
    }

    public LiveData<Integer> getCurrentUserId() {
        return stateHandle.getLiveData(CURRENT_USER_ID);
    }

    public void setCurrentUserId(Integer currentUserId) {
        stateHandle.set(CURRENT_USER_ID, currentUserId);
    }
}