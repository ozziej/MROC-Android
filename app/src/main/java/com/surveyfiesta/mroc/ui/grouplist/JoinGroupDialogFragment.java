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
import com.surveyfiesta.mroc.interfaces.JoinGroupDialogListener;

public class JoinGroupDialogFragment extends DialogFragment implements TextWatcher {
    private EditText editLinkText;
    private JoinGroupDialogListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            listener = (JoinGroupDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getLocalizedMessage()+" Should be a fragment implementing "+JoinGroupDialogListener.class.getSimpleName());
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
        View view = inflater.inflate(R.layout.join_group_dialog, null);
        editLinkText = view.findViewById(R.id.editLinkText);
        editLinkText.requestFocus();
        editLinkText.addTextChangedListener(this);
        builder.setView(view)
                .setPositiveButton(R.string.join, ((dialog, which) ->
                        listener.onJoinDialogPositiveClick(JoinGroupDialogFragment.this, editLinkText.getText().toString())))
                .setNegativeButton(R.string.cancel, ((dialog, which) ->
                        listener.onJoinDialogNegativeClick(JoinGroupDialogFragment.this)));
        return builder.create();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        AlertDialog dialog = (AlertDialog) this.getDialog();
        boolean enabled = editLinkText.getText().length() > 0;
        if (dialog != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(enabled);
        }
    }
}
