package com.jsstech.woocommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
EditText registerNamebt,regisPhoneNo,regisPassword;
Button register_Button;
private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerNamebt=findViewById(R.id.regs_userName);
        regisPhoneNo=findViewById(R.id.regis_PhoneNo);
        regisPassword=findViewById(R.id.edtPassword);
        progressDialog=new ProgressDialog(this);

        register_Button=findViewById(R.id.register_buttn);

        register_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

    }

    private void createAccount() {


        String name=registerNamebt.getText().toString().trim();
        String phone=regisPhoneNo.getText().toString().trim();
        String password=regisPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please write yoyr name",Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please enter your phone",Toast.LENGTH_SHORT).show();
        }
       else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password",Toast.LENGTH_SHORT).show();
        }
       else {
           progressDialog.setTitle("Create account");
           progressDialog.setMessage("Please wait,while checking credentials");
           progressDialog.setCanceledOnTouchOutside(false);
           progressDialog.show();

           ValidatephoneNo(name,phone,password);
       }
    }

    private void ValidatephoneNo(String name,String phone,String password) {

        final DatabaseReference Rootref;
        Rootref= FirebaseDatabase.getInstance().getReference();
        Rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((!snapshot.child("Users").child(phone).exists())){

                    HashMap<String,Object> userdatamap=new HashMap<>();
                    userdatamap.put("phone",phone);
                    userdatamap.put("password",password);
                    userdatamap.put("name",name);
                    Rootref.child("Users").child(phone).updateChildren(userdatamap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this,"congratulation,your account is created",Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                                Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this,"Network error please try again",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

        else {
                    Toast.makeText(RegisterActivity.this,"This" +phone+ "already exists",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this,"Please try with another number",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}