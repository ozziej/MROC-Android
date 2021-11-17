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

import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.interfaces.EditGroupDialogListener;

public class EditGroupDialogFragment extends DialogFragment implements TextWatcher {

    EditGroupDialogListener listener;
    private EditText editGroupTitleText;
    private EditText editGroupDescriptionText;

    public EditGroupDialogFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            listener = (EditGroupDialogListener) getParentFragment();
        } catch (ClassCastException e){
            throw new ClassCastException(e.getLocalizedMessage()+" Should be a fragment implementing EditGroupDialogListener");
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
        View view = inflater.inflate(R.layout.new_group_dialog, null);
        editGroupTitleText = view.findViewById(R.id.editGroupTitle);
        editGroupTitleText.requestFocus();
        editGroupTitleText.addTextChangedListener(this);
        editGroupDescriptionText = view.findViewById(R.id.editGroupDescription);
        builder.setView(view)
                .setPositiveButton(R.string.create, (dialogInterface, i) -> {
                    listener.onDialogPositiveClick(EditGroupDialogFragment.this, editGroupTitleText.getText().toString(), editGroupDescriptionText.getText().toString());
                })
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    listener.onDialogNegativeClick(EditGroupDialogFragment.this);
                });
        return builder.create();
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
        if (editable.length() > 0) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
        } else {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }
    }
}
