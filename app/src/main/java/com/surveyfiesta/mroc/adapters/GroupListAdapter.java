package com.surveyfiesta.mroc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.surveyfiesta.mroc.constants.DefaultValues;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.entities.GroupChatRecyclerEntity;
import com.surveyfiesta.mroc.interfaces.ChatGroupListener;

import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static ChatGroupListener listener;
    private List<GroupChatRecyclerEntity> groupList ;
    private Context context;
    private GroupChatRecyclerEntity deletedGroupChat = null;

    public GroupListAdapter(List<GroupChatRecyclerEntity> groupList, Context context, ChatGroupListener listener) {
        this.groupList = groupList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_list_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GroupChat selectedGroup = groupList.get(position).getChatEntity().getGroupChat();
        if (holder instanceof GroupViewHolder) {
            GroupViewHolder groupViewHolder = (GroupViewHolder)holder;
            Integer groupId = selectedGroup.getGroupId();
            String groupName = selectedGroup.getGroupName();
            groupName = selectedGroup.isGroupEnabled() ? groupName : groupName + " (disabled)";
            ImageView groupImage = groupViewHolder.groupImage;
            String imageUrl = selectedGroup.getGroupImageUrl();
            if (imageUrl == null || imageUrl.isEmpty()) {
                groupImage.setImageResource(R.drawable.ic_baseline_chat_24);
                groupImage.setContentDescription("Group Image");
                groupImage.setAdjustViewBounds(true);
            } else {
                Picasso.get().load(DefaultValues.BASE_IMAGE_URL + "/chatgroup/" + groupId + "/" + imageUrl)
                        .placeholder(R.drawable.ic_baseline_access_time_24)
                        .fit()
                        .error(R.drawable.ic_baseline_chat_24)
                        .into(groupImage);
            }

            groupViewHolder.groupNameText.setText(groupName);
            groupViewHolder.groupDescriptionText.setText(selectedGroup.getGroupDescription());
            groupViewHolder.moreButton.setOnClickListener(view -> listener.onButtonClickListener(view, holder.getAbsoluteAdapterPosition()));
            groupViewHolder.itemView.setOnClickListener(view -> listener.onRowClickListener(view, holder.getAbsoluteAdapterPosition()));
        }
    }

    public List<GroupChatRecyclerEntity> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<GroupChatRecyclerEntity> groupList) {
        this.groupList = groupList;
    }

    public Context getContext() {
        return context;
    }

    public void deleteItem(int position) {
        deletedGroupChat = groupList.get(position);
        groupList.remove(deletedGroupChat);
        notifyItemRemoved(position);
    }

    public void undoDeleteItem(int position) {
        if (deletedGroupChat != null){
            groupList.add(position, deletedGroupChat);
            notifyItemChanged(position);
            deletedGroupChat = null;
        }
    }

    public void clear() {
        groupList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameText;
        TextView groupDescriptionText;
        ImageView groupImage;
        ImageButton moreButton;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            this.groupImage = itemView.findViewById(R.id.groupImage);
            this.groupDescriptionText = itemView.findViewById(R.id.groupDescriptionText);
            this.groupNameText = itemView.findViewById(R.id.groupNameText);
            this.moreButton = itemView.findViewById(R.id.groupMoreButton);

            itemView.setClickable(true);
        }
    }

}


