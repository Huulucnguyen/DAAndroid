package com.example.daandroid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, email,password,repassword;
    private Button btnRegister;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseApp.initializeApp(this);

        repassword= findViewById(R.id.repassword);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        btnRegister = findViewById(R.id.register);
        auth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Đăng kí");
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUsername = username.getText().toString();
                String txtEmail = email.getText().toString();
                String txtPassword = password.getText().toString();
                String txtRepassword = repassword.getText().toString();
                status = "offline";
                if(TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtUsername)  || TextUtils.isEmpty(txtPassword)  ){
                    Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin",Toast.LENGTH_SHORT).show();
                }
                else if(txtPassword.length() <6)
                    Toast.makeText(RegisterActivity.this, "Mật khẩu phải có 6 kí tự trở lên",Toast.LENGTH_SHORT).show();
                else if(txtPassword.compareTo(txtRepassword)!=0)
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không trùng khớp. Vui lòng nhập lại",Toast.LENGTH_SHORT).show();
                else
                    register(txtUsername,txtEmail,txtPassword);
            }
        });
    }
    private void register(String username, String email, String password){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) // neu thanh cong, cap nhap ui voi thong tin user
                        {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",userId);
                            hashMap.put("username",username);
                            hashMap.put("imageURL","default");
                            hashMap.put("status",status);
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Bạn không thể đăng kí bằng email này",Toast.LENGTH_SHORT).show();
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

}
