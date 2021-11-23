package com.surveyfiesta.mroc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.surveyfiesta.mroc.constants.ChatGroupButtonType;
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
    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;

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

            groupViewHolder.groupNameText.setText(selectedGroup.getGroupName());
            groupViewHolder.groupDescriptionText.setText(selectedGroup.getGroupDescription());

            groupViewHolder.itemView.setOnClickListener(view -> listener.chatGroupListener(holder.getAbsoluteAdapterPosition()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (groupList.get(position).isMenuVisible()){
            return SHOW_MENU;
        } else {
            return HIDE_MENU;
        }
    }

    public void showMenu(int position) {
        groupList.forEach(i -> i.setMenuVisible(false));
        groupList.get(position).setMenuVisible(true);
        notifyDataSetChanged();
    }

    public boolean isMenuShown() {
        return groupList.stream().anyMatch(i -> i.isMenuVisible());
    }

    public void closeMenu() {
        groupList.forEach(i -> i.setMenuVisible(false));
        notifyDataSetChanged();
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

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            this.groupImage = itemView.findViewById(R.id.groupImage);
            this.groupDescriptionText = itemView.findViewById(R.id.groupDescriptionText);
            this.groupNameText = itemView.findViewById(R.id.groupNameText);
            itemView.setClickable(true);
        }
    }

    public class GroupViewMenuHolder extends RecyclerView.ViewHolder {
        Button editButton;
        Button shareButton;
        Button deleteButton;

        public GroupViewMenuHolder(@NonNull View itemView) {
            super(itemView);
            this.deleteButton = itemView.findViewById(R.id.buttonDeleteGroup);
            this.editButton = itemView.findViewById(R.id.buttonEditGroup);
            this.shareButton = itemView.findViewById(R.id.buttonShareGroup);
            itemView.setClickable(true);
        }
    }

}


