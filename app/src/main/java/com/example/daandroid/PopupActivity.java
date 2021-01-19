package com.example.daandroid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.IntentCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.daandroid.Model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class PopupActivity extends Activity {

    private Uri imageUri;
    private CircleImageView profile_image, logout_image;
    private TextView username, back, txtpopup;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private Button btnChose;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private StorageTask uploadTask;
    private CircleImageView status_on;
    private CircleImageView status_off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);



        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width),(int)(height*0.8));
        WindowManager.LayoutParams params = getWindow().getAttributes();

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        params.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(params);

        txtpopup = findViewById(R.id.txtpopup);
        logout_image = findViewById(R.id.logout_image);
        back =findViewById(R.id.back);
        username = findViewById(R.id.username);
        profile_image = findViewById(R.id.profile_picture);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String Uid = firebaseUser.getUid();

        logout_image.setImageResource(R.drawable.logout);

        logout_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(PopupActivity.this,LoginActivity.class);

                startActivity(intent);

            }
        });
        txtpopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(PopupActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

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

                    if (!PopupActivity.this.isFinishing())
                    Glide.with(PopupActivity.this).load(user.getImageURL()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PopupActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }



    private void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData()!=null ){
            imageUri =data.getData();
            profile_image.setImageURI(imageUri);
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Đang tải....");
        pd.show();
        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/*"+randomKey);
        //Lay image ur
        uploadTask = riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        pd.dismiss();
                        Toast.makeText(PopupActivity.this,"Tải ảnh thành công",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        pd.dismiss();
                        Toast.makeText(PopupActivity.this,"Tải ảnh thất bại",Toast.LENGTH_SHORT).show();
                    }
                });


        Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful())
                    throw task.getException();
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    String downloadUri = task.getResult().toString();
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                    HashMap<String,Object>  map = new HashMap<>();
                    map.put("imageURL",downloadUri);
                    reference.updateChildren(map);
                }
            }
        });
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


}