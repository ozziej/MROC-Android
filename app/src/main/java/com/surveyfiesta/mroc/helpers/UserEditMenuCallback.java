package com.surveyfiesta.mroc.helpers;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.interfaces.EditMenuActionItemListener;

public class UserEditMenuCallback implements ActionMode.Callback {

    private EditMenuActionItemListener itemListener;
    private int rowPosition;

    public UserEditMenuCallback(EditMenuActionItemListener itemListener) {
        this.itemListener = itemListener;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.edit_group_users_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        itemListener.onActionItemClicked(item);
        mode.finish();
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

    public int getRowPosition() {
        return rowPosition;
    }

    public void setRowPosition(int rowPosition) {
        this.rowPosition = rowPosition;
    }
}
