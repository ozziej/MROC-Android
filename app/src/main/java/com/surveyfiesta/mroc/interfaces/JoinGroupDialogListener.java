package com.surveyfiesta.mroc.interfaces;

import androidx.fragment.app.DialogFragment;

public interface JoinGroupDialogListener {
    public void onJoinDialogPositiveClick(DialogFragment dialog, String joinUrl);
    public void onJoinDialogNegativeClick(DialogFragment dialog);
}
