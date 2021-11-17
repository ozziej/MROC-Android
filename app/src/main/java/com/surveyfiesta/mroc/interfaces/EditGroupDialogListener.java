package com.surveyfiesta.mroc.interfaces;

import androidx.fragment.app.DialogFragment;

public interface EditGroupDialogListener {
    public void onDialogPositiveClick(DialogFragment dialog, String titleText, String descriptionText);
    public void onDialogNegativeClick(DialogFragment dialog);
}
