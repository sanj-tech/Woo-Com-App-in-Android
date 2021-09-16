package com.jsstech.woocommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jsstech.woocommerceapp.Model.Users;
import com.jsstech.woocommerceapp.Prevalent.Prevalent;

public class MainActivity extends AppCompatActivity {
    Button buttonJoin, buttonLogin;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonJoin = findViewById(R.id.btjoin);
        buttonLogin = findViewById(R.id.btlogin);
        progressDialog=new ProgressDialog(this);

        //initialize paper
        Paper.init(this);


        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        buttonJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);

            }
        });

        //Retrive users data
        String UserPhoneKey=Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey=Paper.book().read(Prevalent.UserPasswordKey);

        if (UserPhoneKey != "" && UserPasswordKey !=""){

            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)){

                AllowAccess(UserPhoneKey,UserPasswordKey);

                progressDialog.setTitle("Alread Logged In");
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

            }
        }

    }

    private void AllowAccess( final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(phone).exists()){

                    Users userData=snapshot.child("Users").child(phone).getValue(Users.class);
                    if (userData.getPhone().equals(phone))
                    {
                        if (userData.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this,"logged in Sucessfully",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                            Intent intent=new Intent(MainActivity.this,HomeActivity.class);
                            Prevalent.CurrentonlineUser=userData;
                            startActivity(intent);
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this,"password is incorrect",Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else {
                    Toast.makeText(MainActivity.this,"Account with this" +phone+ "number not exists",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}