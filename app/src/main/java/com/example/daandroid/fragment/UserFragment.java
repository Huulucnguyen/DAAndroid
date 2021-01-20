package com.example.daandroid.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.daandroid.Adapter.UserAdapter;
import com.example.daandroid.Model.User;
import com.example.daandroid.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UserFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> users;
    private EditText search_user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        users = new ArrayList<>();
        View view =  inflater.inflate(R.layout.fragment_user, container, false);
        recyclerView = view.findViewById(R.id.friend_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager((getContext())));
        search_user = view.findViewById(R.id.search_user);
        search_user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count == 0){
                    readUsers();
                }
                else{
                    searchUser(s.toString());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        readUsers();
        return view;
    }

    private void searchUser(String s){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    users.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        User user1 = dataSnapshot.getValue(User.class);
                        assert user1!= null;
                        assert firebaseUser != null;
                        if(!user1.getId().equals(firebaseUser.getUid())){
                            users.add(user1);
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

    private void readUsers() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (search_user.getText().toString().equals("")) {
                    users.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);

                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            users.add(user);
                        }
                    }
                    userAdapter = new UserAdapter(getContext(), users, true);
                    recyclerView.setAdapter(userAdapter);


                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}