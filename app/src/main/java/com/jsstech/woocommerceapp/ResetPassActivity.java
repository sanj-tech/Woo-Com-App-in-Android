package com.jsstech.woocommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jsstech.woocommerceapp.Model.Users;
import com.jsstech.woocommerceapp.Prevalent.Prevalent;

import java.util.HashMap;

public class ResetPassActivity extends AppCompatActivity {
    private String check = "";
    TextView reset_title, quesTitle;
    EditText resetPhNo, etQues1, etQues2, etQues3;
    Button verifyBt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        check = getIntent().getStringExtra("check");

        reset_title = findViewById(R.id.reset_page_title);
        quesTitle = findViewById(R.id.title_ques);
        resetPhNo = findViewById(R.id.ph_no);
        etQues1 = findViewById(R.id.ques1_no);
        etQues2 = findViewById(R.id.ques2_no);
        etQues3 = findViewById(R.id.ques3_no);
        verifyBt = findViewById(R.id.verify_bt);


    }

    //to change the visiblity of buttons
    @Override
    protected void onStart() {
        super.onStart();

        resetPhNo.setVisibility(View.GONE);


        if (check.equals("settings")) {
            reset_title.setText("Set Questions");
            quesTitle.setText("Please set Answer for the follwing question");
            verifyBt.setText("Set");

            displayPreviousAns();


            verifyBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAns();

                }
            });


        } else if (check.equals("login")) {

            resetPhNo.setVisibility(View.VISIBLE);
            verifyBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    verifyUser();
                }
            });


        }
    }

    private void verifyUser()
    {
        String phoneno=resetPhNo.getText().toString();
        String Ques1 = etQues1.getText().toString().toLowerCase();
        String Ques2 = etQues2.getText().toString().toLowerCase();
        String Ques3 = etQues3.getText().toString().toLowerCase();

        if (!phoneno.equals("")&& !Ques1.equals("")&& !Ques2.equals("")&& !Ques3.equals(""))
        {
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(phoneno);


            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    if (snapshot.exists()){
                        String mphone=snapshot.child("phone").getValue().toString();
                        if (snapshot.hasChild("Security Questions"))
                        {

                            String ans1=snapshot.child("Security Questions").child("answer1").getValue().toString();
                            String ans2=snapshot.child("Security Questions").child("answer2").getValue().toString();
                            String ans3=snapshot.child("Security Questions").child("answer3").getValue().toString();

                            if (!ans1.equals(Ques1)){
                                Toast.makeText(ResetPassActivity.this,"Your first ans is worng",Toast.LENGTH_SHORT).show();
                            }else if (!ans2.equals(Ques2))
                            {
                                Toast.makeText(ResetPassActivity.this,"Your second ans is worng",Toast.LENGTH_SHORT).show();

                            }else if (!ans3.equals(Ques3))
                            {
                                Toast.makeText(ResetPassActivity.this,"Your second ans is worng",Toast.LENGTH_SHORT).show();

                            }
                            else {
                                AlertDialog.Builder builder=new AlertDialog.Builder(ResetPassActivity.this);
                                builder.setTitle("New Password");
                                final  EditText newpass=new EditText(ResetPassActivity.this);
                                newpass.setHint("Write new password.....");
                                builder.setView(newpass);

                                builder.setPositiveButton("change",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface,int i) {
                                        if (!newpass.getText().toString().equals(""))
                                        {
                                            reference.child("password").setValue(newpass.getText()
                                                    .toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(ResetPassActivity.this,"password change sucessfully",Toast.LENGTH_SHORT).show();

                                                        Intent intent=new Intent(ResetPassActivity.this,LoginActivity.class);
                                                        startActivity(intent);
                                                    }

                                                }
                                            });

                                        }
                                    }
                                });
                                builder.setNegativeButton("cancel",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface,int i)
                                    {
                                        dialogInterface.cancel();
                                    }
                                });
                                builder.show();
                            }

                        }


                        else {
                            Toast.makeText(ResetPassActivity.this,"you have not set the security key",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(ResetPassActivity.this,"this phone no. not exists",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        else {
            Toast.makeText(this,"please complete form",Toast.LENGTH_SHORT).show();
        }



    }

    private void setAns() {
        String Ques1 = etQues1.getText().toString().toLowerCase();
        String Ques2 = etQues2.getText().toString().toLowerCase();
        String Ques3 = etQues3.getText().toString().toLowerCase();

        if (etQues1.equals("") && etQues2.equals("") && etQues3.equals("")) {
            Toast.makeText(ResetPassActivity.this,"Please fill all fields",Toast.LENGTH_SHORT).show();

        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(Prevalent.CurrentonlineUser.getPhone());

            HashMap<String, Object> userQuesAns = new HashMap<>();
            userQuesAns.put("answer1",Ques1);
            userQuesAns.put("answer2",Ques2);
            userQuesAns.put("answer3",Ques3);
            reference.child("Security Questions").updateChildren(userQuesAns).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ResetPassActivity.this,"You have answer the security question sucessfully",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ResetPassActivity.this,HomeActivity.class);
                        startActivity(intent);
                    }

                }
            });

        }

    }

    private void displayPreviousAns() {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(Prevalent.CurrentonlineUser.getPhone());
        reference.child("Security Questions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
             if (snapshot.exists()){
                 String ans1=snapshot.child("answer1").getValue().toString();
                 String ans2=snapshot.child("answer2").getValue().toString();
                 String ans3=snapshot.child("answer3").getValue().toString();


                 etQues1.setText(ans1);
                 etQues2.setText(ans2);
                 etQues3.setText(ans3);
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}