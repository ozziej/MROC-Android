package com.surveyfiesta.mroc.interfaces;

import android.view.View;

import com.surveyfiesta.mroc.constants.ChatGroupButtonType;

public interface ChatGroupListener {
    void chatGroupListener(View view, int position, ChatGroupButtonType buttonType);
}
