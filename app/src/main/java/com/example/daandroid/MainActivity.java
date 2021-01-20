package com.example.daandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.PersistableBundle;
import android.util.Log;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.daandroid.Model.Chats;
import com.example.daandroid.Model.User;
import com.example.daandroid.fragment.ChatsFragment;
import com.example.daandroid.fragment.UserFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private CircleImageView profile_image;
    private TextView username;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference, reference_chat;
    private CircleImageView status_on;
    private CircleImageView status_off;
    private TextView mess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status_on = findViewById(R.id.status_on);
        status_off = findViewById(R.id.status_off);
        username = findViewById(R.id.username);
        profile_image = findViewById(R.id.profile_picture);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = firebaseUser.getUid();
        mess =findViewById(R.id.lastmess);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(Uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.drawable.resource_default);
                }
                else{
                    final Context context = MainActivity.this;
                        if (!MainActivity.this.isFinishing())
                            Glide.with(MainActivity.this).load(user.getImageURL()).into(profile_image);
                }
                if(user.getStatus().equals("online")){
                    status_on.setVisibility(View.VISIBLE);
                    status_off.setVisibility(View.GONE);
                }
                else{
                    status_on.setVisibility(View.GONE);
                    status_off.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,PopupActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager = findViewById(R.id.view_pager);



        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ChatsFragment());
        viewPagerAdapter.addFragment(new UserFragment());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.chat_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.group_chat2);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;



        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public void addFragment(Fragment fragment){
            fragments.add(fragment);

        }

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

    }

    private  void status(String status){

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }


    @Override
    protected void onStart() {
        super.onStart();

            status("online");

    }
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }


    @Override
    protected void onPause() {
        super.onPause();

            status("offline");

    }


}