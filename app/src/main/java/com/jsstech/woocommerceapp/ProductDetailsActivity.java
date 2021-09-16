package com.jsstech.woocommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jsstech.woocommerceapp.Model.Products;
import com.jsstech.woocommerceapp.Prevalent.Prevalent;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    ElegantNumberButton numberButton;
    Button detail_prod_add_to_cart;
    ImageView prodImgD;
    TextView prodNameDTxt,prodDesDTxt,prodPriceDTxt;
    private String productId=" ",state="Normal";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        productId=getIntent().getStringExtra("pid");

        detail_prod_add_to_cart=findViewById(R.id.prodD_addto_Cart);
        prodImgD=findViewById(R.id.product_imageDetail);
        prodNameDTxt=findViewById(R.id.detail_prodName);
        prodDesDTxt=findViewById(R.id.detail_prodDesc);
        prodPriceDTxt=findViewById(R.id.detail_prodPrice);
        numberButton=findViewById(R.id.numberBt);



        getProducutDetails(productId);
        //adding image in to cart
        detail_prod_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add  in to cart
                addingCartList();
                //validation for cart List
                if (state.equals("Order Placed")||state.equals("Order Shipped")){
                    Toast.makeText(ProductDetailsActivity.this,"You can purchase more product once your order is shipped or confirmed",Toast.LENGTH_LONG).show();
                }else
                    {
                        addingCartList();

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();
    }

    private void addingCartList() {
        String saveCurrentDate,saveCurrentTime;

        Calendar calForDate=Calendar.getInstance();

        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate=currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calForDate.getTime());

       final DatabaseReference cartListRef=FirebaseDatabase.getInstance().getReference().child("Cart List");

        //store data using HashMap
        final HashMap<String, Object> cartMap=new HashMap<>();
        cartMap.put("pid",productId);
        cartMap.put("pname",prodNameDTxt.getText().toString());
        cartMap.put("price",prodPriceDTxt.getText().toString());
        cartMap.put("date",saveCurrentDate);
        cartMap.put("time",saveCurrentTime);
        cartMap.put("quantity",numberButton.getNumber());
        cartMap.put("discount"," ");

        cartListRef.child("User View").child(Prevalent.CurrentonlineUser.getPhone())
                .child("Products").child(productId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            cartListRef.child("Admin View").child(Prevalent.CurrentonlineUser.getPhone())
                                    .child("Products").child(productId)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(ProductDetailsActivity.this,"Added to cart List",Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(ProductDetailsActivity.this,HomeActivity.class);
                                                startActivity(intent);
                                            }

                                        }
                                    });
                        }
                    }
                });

    }

    private void getProducutDetails(String productId) {
        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Products products=snapshot.getValue(Products.class);
                    prodNameDTxt.setText(products.getPname());
                    //prodPriceDTxt.setText(products.getPrice()+"$");
                    prodPriceDTxt.setText(products.getPrice());
                    prodDesDTxt.setText(products.getDes());

                    Picasso.get().load(products.getImage()).into(prodImgD);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void CheckOrderState(){

        DatabaseReference ordeRef;
        ordeRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.CurrentonlineUser.getPhone());
        ordeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    String shippingState=snapshot.child("state").getValue().toString();


                    if (shippingState.equals("shipped"))
                    {
                       state = "Order Shipped";
                    }
                    else if (shippingState.equals("not shipped"))
                    {

                        state = "Order Placed";
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }






}