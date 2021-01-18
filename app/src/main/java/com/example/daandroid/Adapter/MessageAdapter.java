package com.example.daandroid.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.daandroid.MessageActivity;
import com.example.daandroid.Model.Chats;
import com.example.daandroid.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context context;
    private List<Chats> aChats;
    private  String imageUrl;
    private  static final int left = 0;
    private  static final int right = 1;

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==1){
            View view = LayoutInflater.from(context).inflate(R.layout.chatitem_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.chatitem_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chats chat = aChats.get(position);
        holder.message.setText(chat.getMessage());
        if(imageUrl.equals("default")){
            holder.profile_image.setImageResource(R.drawable.resource_default);
        }
        else{
            Glide.with(context).load(imageUrl).into(holder.profile_image);
        }

    }

    @Override
    public int getItemCount() {
        return aChats.size();
    }

    public MessageAdapter(Context context, List<Chats> aUsers, String image) {
        this.context = context;
        this.aChats= aUsers;
        this.imageUrl = image;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView message;
        private CircleImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.message = itemView.findViewById(R.id.chat_view);
            this.profile_image = itemView.findViewById(R.id.friend_profile_picture);

        }
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser.getUid().equals(aChats.get(position).getSender())){
            return 1;
        }
        else
            return 0;
    }
}
