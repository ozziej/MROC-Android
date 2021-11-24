package com.surveyfiesta.mroc.interfaces;

import android.view.View;

import com.surveyfiesta.mroc.constants.ChatGroupButtonType;

public interface ChatGroupListener {
    void onRowClickListener(View view, int position);
    void onButtonClickListener(View view, int position);
}
