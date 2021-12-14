package com.surveyfiesta.mroc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.entities.GroupUsers;
import com.surveyfiesta.mroc.interfaces.GroupUserListener;

import java.util.List;

public class GroupUserListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private GroupUserListener listener;
    private List<GroupUsers> groupUsersList;
    private Context context;
    private int selectedPos = RecyclerView.NO_POSITION;

    public GroupUserListAdapter(List<GroupUsers> groupUsersList, Context context, GroupUserListener listener) {
        this.groupUsersList = groupUsersList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_user_list_item, parent, false);
        return new GroupUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GroupUsers groupUsers = groupUsersList.get(position);
        GroupUserViewHolder viewHolder = (GroupUserViewHolder)holder;
        viewHolder.groupUserImageView.setImageResource(R.drawable.ic_twotone_person_24);
        viewHolder.groupUserDisplayName.setText(groupUsers.getUser().getOtherName());
        viewHolder.groupUserStatusText.setText(groupUsers.isAdminUser()?"Admin":"User");
        viewHolder.groupUserStatusImage.setImageResource(groupUsers.isAdminUser()?R.drawable.ic_outline_verified_user_24:R.drawable.ic_baseline_perm_identity_24);
        viewHolder.itemView.setOnClickListener(view -> listener.onUserRowClickListener(view, position));
        viewHolder.groupUserLayout.setBackgroundColor(selectedPos == position?ContextCompat.getColor(getContext(),android.R.color.holo_blue_bright):
                ContextCompat.getColor(getContext(),android.R.color.white));
    }

    @Override
    public int getItemCount() {
        return groupUsersList.size();
    }

    public int getSelectedPos() {
        return selectedPos;
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
    }

    public GroupUserListener getListener() {
        return listener;
    }

    public List<GroupUsers> getGroupUsersList() {
        return groupUsersList;
    }

    public Context getContext() {
        return context;
    }

    private class GroupUserViewHolder extends RecyclerView.ViewHolder {
        TextView groupUserDisplayName;
        TextView groupUserStatusText;
        ConstraintLayout groupUserLayout;
        ImageView groupUserImageView;
        ImageView groupUserStatusImage;

        public GroupUserViewHolder(View view) {
            super(view);
            this.groupUserLayout = view.findViewById(R.id.groupUserLayout);
            this.groupUserDisplayName = view.findViewById(R.id.groupUserDisplayName);
            this.groupUserImageView = view.findViewById(R.id.groupUserImageView);
            this.groupUserStatusText = view.findViewById(R.id.groupUserStatusText);
            this.groupUserStatusImage = view.findViewById(R.id.groupUserStatusImage);
            view.setClickable(true);
        }
    }
}
