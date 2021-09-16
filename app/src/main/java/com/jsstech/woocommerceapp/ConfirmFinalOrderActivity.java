package com.jsstech.woocommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jsstech.woocommerceapp.Prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    private EditText etConfirmName,etConfirmAddr,etConfirmPhoneNo,etConfirmCity;
    Button confirm_button;
    private String totalAmt="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmt = getIntent().getStringExtra("Total Price");
       Toast.makeText(this, "Total Price = $ " + totalAmt, Toast.LENGTH_SHORT).show();
////

        //totalAmt=getIntent().getStringExtra("Total Price");

        etConfirmName=findViewById(R.id.shipName);
        etConfirmAddr=findViewById(R.id.shipAddress);
        etConfirmPhoneNo=findViewById(R.id.shipPhoneNo);
        etConfirmCity=findViewById(R.id.shipcity);
        confirm_button=findViewById(R.id.confirmBt);

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String CONName=etConfirmName.getText().toString();
//                String CONAddr=etConfirmAddr.getText().toString();
//                String CONPhone=etConfirmPhoneNo.getText().toString();
//                String CONcity=etConfirmCity.getText().toString();

                Check();
            }
        });







    }

    private void Check() {
        if (TextUtils.isEmpty(etConfirmName.getText().toString())){
            Toast.makeText(this,"Please provide your full name",Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(etConfirmPhoneNo.getText().toString())){
            Toast.makeText(this,"Please provide your Phone Number",Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(etConfirmAddr.getText().toString())){
            Toast.makeText(this,"Please provide your Address",Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(etConfirmCity.getText().toString())){
            Toast.makeText(this,"Please provide your City",Toast.LENGTH_SHORT).show();

        }
        else {
            ConfirmOrder();
        }


    }
    private void ConfirmOrder(){

       final String saveCurrentDate,saveCurrentTime;

        Calendar calForDate=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate=currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calForDate.getTime());
        final DatabaseReference ordersRef= FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.CurrentonlineUser.getPhone());

        HashMap<String,Object> orderMap=new HashMap<>();
        orderMap.put("totalAmt",totalAmt);
        orderMap.put("name",etConfirmName.getText().toString());
        orderMap.put("phone",etConfirmPhoneNo.getText().toString());
        orderMap.put("address",etConfirmAddr.getText().toString());
        orderMap.put("name",etConfirmCity.getText().toString());
        orderMap.put("date",saveCurrentDate);
        orderMap.put("time",saveCurrentTime);

        orderMap.put("state","not shipped");

        ordersRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(Prevalent.CurrentonlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrderActivity.this,"Your final order has been placed sucessfully",Toast.LENGTH_SHORT).show();
                                        Intent intent= new Intent(ConfirmFinalOrderActivity.this,HomeActivity.class);
                                       //To remove cartList after order
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }

            }
        });



    }
}