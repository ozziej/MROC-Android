package com.surveyfiesta.mroc.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.surveyfiesta.mroc.constants.DefaultValues;
import com.surveyfiesta.mroc.entities.GroupChat;
import com.surveyfiesta.mroc.R;
import com.surveyfiesta.mroc.interfaces.ChatGroupListener;

import java.util.ArrayList;
import java.util.List;

public class GroupListAdapater extends RecyclerView.Adapter<GroupListAdapater.GroupViewHolder>{

    private static ChatGroupListener listener;
    private List<GroupChat> groupList = new ArrayList<>();
    private Context context;

    public GroupListAdapater(List<GroupChat> groupList, Context context, ChatGroupListener listener) {
        this.groupList = groupList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_list_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        GroupChat selectedGroup = groupList.get(position);
        Integer groupId = selectedGroup.getGroupId();
        ImageView i = holder.groupImage;
        String imageUrl = selectedGroup.getGroupImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            i.setImageResource(R.drawable.ic_baseline_chat_24);
            i.setContentDescription("Group Image");
            i.setAdjustViewBounds(true);
        } else {
            Picasso.get().load(DefaultValues.BASE_IMAGE_URL+"/chatgroup/"+groupId+"/"+imageUrl)
                    .placeholder(R.drawable.ic_baseline_access_time_24)
                    .fit()
                    .error(R.drawable.ic_baseline_chat_24)
                    .into(i);
        }

        holder.groupNameText.setText(selectedGroup.getGroupName());
        holder.groupDescriptionText.setText(selectedGroup.getGroupDescription());

        holder.groupImage.setOnClickListener(view -> {
            listener.chatGroupListener(view, holder.getAbsoluteAdapterPosition());
            Log.i("action","image clicked");
        });
    }

    public List<GroupChat> getGroupList() {
        return groupList;
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView groupNameText;
        TextView groupDescriptionText;
        ImageView groupImage;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            this.groupImage = itemView.findViewById(R.id.groupImage);
            this.groupDescriptionText = itemView.findViewById(R.id.groupDescriptionText);
            this.groupNameText = itemView.findViewById(R.id.groupNameText);
            itemView.setClickable(true);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            Log.i("View Holder Hit","Position :"+position+" Group "+groupList.get(position).getGroupName());
        }
    }
}


