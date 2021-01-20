package com.example.daandroid.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
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
import com.example.daandroid.Model.User;
import com.example.daandroid.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> aUsers;
    private boolean isOnl;
    private String lastMess;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = aUsers.get(position);
        holder.username.setText(user.getUsername());
        if(user.getImageURL().equals("default")){
            holder.profile_image.setImageResource(R.drawable.resource_default);
        }
        else{
            Glide.with(context).load(user.getImageURL()).into(holder.profile_image);
        }
            lassMessage(user.getId(),holder.lastmessage);
        if(user!=null){
            if(isOnl){
                if(user.getStatus().equals("online")){
                    holder.status_on.setVisibility(View.VISIBLE);
                    holder.status_off.setVisibility(View.GONE);
                }
                else{
                    holder.status_on.setVisibility(View.GONE);
                    holder.status_off.setVisibility(View.VISIBLE);

                }
            }
            else{
                holder.status_on.setVisibility(View.GONE);
                holder.status_off.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return aUsers.size();
    }

    public UserAdapter(Context context, List<User> aUsers, boolean isOnl) {
        this.context = context;
        this.aUsers = aUsers;
        this.isOnl = isOnl;

    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView username;
        private ImageView profile_image;
        private ImageView status_on;
        private ImageView status_off;
        private TextView lastmessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.username = itemView.findViewById(R.id.friend_username);
            this.profile_image = itemView.findViewById(R.id.friend_image);
            this.status_on = itemView.findViewById(R.id.status_on);
            this.lastmessage = itemView.findViewById(R.id.lastmess);
            this.status_off = itemView.findViewById(R.id.status_off);
        }
    }
    public void lassMessage(String userid, TextView lastmessage){
        lastMess = "default";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chats chat = dataSnapshot.getValue(Chats.class);
                    if(firebaseUser!=null){
                        if(chat.getSender().equals(userid) && chat.getReceiver().equals(firebaseUser.getUid())
                                || chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(userid)){

                            lastMess = chat.getMessage();

                        }
                    }

                }
                switch (lastMess){
                    case "default":
                        lastmessage.setText("Không có tin nhắn");
                        break;
                    default:

                        lastmessage.setText(lastMess);
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
