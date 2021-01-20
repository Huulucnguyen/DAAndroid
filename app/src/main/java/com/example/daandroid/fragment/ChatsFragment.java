package com.example.daandroid.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.daandroid.Adapter.UserAdapter;
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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class ChatsFragment extends Fragment {

    private FirebaseUser firebaseUser;

    private RecyclerView recyclerView;
    private List<User> users;
    private List<String> userid_lst;
    private DatabaseReference reference;
    private UserAdapter userAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.chat_recycleview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager((getContext())));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        userid_lst = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userid_lst.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chats chat = dataSnapshot.getValue(Chats.class);
                    if(firebaseUser.getUid().equals(chat.getSender())){
                        userid_lst.add(chat.getReceiver());
                    }
                    if(firebaseUser.getUid().equals(chat.getReceiver())){
                        userid_lst.add(chat.getSender());
                    }
                }
                LinkedHashSet<String> hashSet = new LinkedHashSet<>(userid_lst);
                userid_lst = new ArrayList<>(hashSet);
                readChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }
    private void readChat(){
        users = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User firebase_user = dataSnapshot.getValue(User.class);
                   for(String id : userid_lst){
                       if(id.equals(firebase_user.getId())){

                                users.add(firebase_user);

                       }
                   }
                }
                userAdapter = new UserAdapter(getContext(),users,false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}