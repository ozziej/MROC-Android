package com.surveyfiesta.mroc.ui.grouplist;

import android.app.AlertDialog;
import android.app.Dialog;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.entities.UserGroupChatEntity;
import com.surveyfiesta.mroc.interfaces.EditGroupDialogListener;

public class EditGroupDialogFragment extends DialogFragment implements TextWatcher {

    EditGroupDialogListener listener;
    private SwitchMaterial editGroupEnabledSwitch;
    private EditText editGroupTitleText;
    private EditText editGroupDescriptionText;

    private UserGroupChatEntity chatEntity;

    public EditGroupDialogFragment(UserGroupChatEntity chatEntity) {
        this.chatEntity = chatEntity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            listener = (EditGroupDialogListener) getParentFragment();
        } catch (ClassCastException e){
            throw new ClassCastException(e.getLocalizedMessage()+" Should be a fragment implementing "+EditGroupDialogListener.class.getSimpleName());
        }
    }

    @Override
    public void onResume() {
        AlertDialog dialog = (AlertDialog) this.getDialog();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        super.onResume();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        int positiveButtonString = R.string.create;
        View view = inflater.inflate(R.layout.edit_group_dialog, null);
        editGroupTitleText = view.findViewById(R.id.editGroupTitle);
        editGroupTitleText.requestFocus();
        editGroupTitleText.addTextChangedListener(this);
        editGroupEnabledSwitch = view.findViewById(R.id.editGroupEnabledSwitch);
        editGroupDescriptionText = view.findViewById(R.id.editGroupDescription);
        editGroupDescriptionText.addTextChangedListener(this);

        if (chatEntity.getGroupChat().getGroupId() > 0) {
            positiveButtonString = R.string.save;
        }

        editGroupTitleText.setText(chatEntity.getGroupChat().getGroupName());
        editGroupDescriptionText.setText(chatEntity.getGroupChat().getGroupDescription());
        editGroupEnabledSwitch.setChecked(chatEntity.getGroupChat().isGroupEnabled());
        editGroupEnabledSwitch.setOnClickListener(l -> groupEnabledSwitchChanged());
        builder.setView(view)
                .setPositiveButton(positiveButtonString, (dialogInterface, i) -> {
                    listener.onDialogPositiveClick(EditGroupDialogFragment.this,
                            editGroupTitleText.getText().toString(),
                            editGroupDescriptionText.getText().toString(),
                            editGroupEnabledSwitch.isChecked());
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    listener.onDialogNegativeClick(EditGroupDialogFragment.this);
                });

        return builder.create();
    }

    private void groupEnabledSwitchChanged() {
        AlertDialog dialog = (AlertDialog) this.getDialog();
        boolean currentStatus = chatEntity.getGroupChat().isGroupEnabled();
        boolean switchStatus = editGroupEnabledSwitch.isChecked();
        if (dialog != null && switchStatus != currentStatus) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        AlertDialog dialog = (AlertDialog) this.getDialog();
        boolean enabled = this.editGroupDescriptionText.getText().length() > 0
                && this.editGroupTitleText.getText().length() > 0;
        if (dialog != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(enabled);
        }
    }

    public UserGroupChatEntity getChatEntity() {
        return chatEntity;
    }
}
