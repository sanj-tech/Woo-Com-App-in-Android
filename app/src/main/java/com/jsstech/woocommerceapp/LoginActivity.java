package com.jsstech.woocommerceapp;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jsstech.woocommerceapp.Model.Users;
import com.jsstech.woocommerceapp.Prevalent.Prevalent;

public class LoginActivity extends AppCompatActivity {
    EditText LoginPhone,LoginPassword;
    CheckBox checkBoxRemembeMe;
    TextView forgetPass,imAdmin,notAdminLink;
    Button Loginalresdy_Button;
    ProgressDialog progressDialog;
    private  String parentDbName="Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginPhone=findViewById(R.id.login_phoneno_input);
        LoginPassword=findViewById(R.id.login_password_input);

        imAdmin=findViewById(R.id.adminpanel_link);
        notAdminLink=findViewById(R.id.not_adminpanel_link);

        checkBoxRemembeMe=findViewById(R.id.rememberme_chkbox);

        //for remember me functionality used Paper Library
        Paper.init(this);

        forgetPass=findViewById(R.id.forgetPass);
        Loginalresdy_Button=findViewById(R.id.alreadylogin);
        progressDialog=new ProgressDialog(this);

//For Login
        Loginalresdy_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginUser();
            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ResetPassActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);

            }
        });


        //for Admin
        imAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Loginalresdy_Button.setText("Login Admin");
                imAdmin.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);
                parentDbName="Admins";
            }
        });
        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Loginalresdy_Button.setText("Login");
                imAdmin.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.INVISIBLE);
                parentDbName="Users";

            }
        });







    }

    private void LoginUser() {
        String phone=LoginPhone.getText().toString().trim();
        String password=LoginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please Enter phone No",Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please Enter Password",Toast.LENGTH_SHORT).show();
        }
        else{
            progressDialog.setTitle("LoginAccount");
            progressDialog.setMessage("Please wait ,while checking credentials..");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            AllowAccessToAccount(phone,password);
        }
    }

    private void AllowAccessToAccount(String phone,String password) {
        //To Store userphone and password inside a variable which is in prevalent class
        if (checkBoxRemembeMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }

        //paper is close.

        final DatabaseReference RootRef;
        RootRef= FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(phone).exists()){

                    Users userData=snapshot.child(parentDbName).child(phone).getValue(Users.class);
                    if (userData.getPhone().equals(phone))
                    {
                        if (userData.getPassword().equals(password)){
//                            Toast.makeText(LoginActivity.this,"logged in Sucessfully",Toast.LENGTH_SHORT).show();
//                            progressDialog.dismiss();
//
//                            Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
//                            startActivity(intent);

                            if (parentDbName.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this,"Welcome Admin,You logged in Sucessfully",Toast.LENGTH_SHORT).show();
                                 progressDialog.dismiss();
                                 Intent intent=new Intent(LoginActivity.this,AdminCategoryActivity.class);
                                 startActivity(intent);
                            }
                            else if (parentDbName.equals("Users")){

                                Toast.makeText(LoginActivity.this,"logged in Sucessfully",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                               Prevalent.CurrentonlineUser=userData;
                                startActivity(intent);
                            }


                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,"password is incorrect",Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else {
                    Toast.makeText(LoginActivity.this,"Account with this" +phone+ "number not exists",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}