package com.jsstech.woocommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jsstech.woocommerceapp.Model.AdminOrders;

public class NewOrderActivityByAdmin extends AppCompatActivity {
    RecyclerView orderRecyclerV;
    private DatabaseReference orderRefer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order_by_admin);
        orderRecyclerV=findViewById(R.id.order_recV);

        orderRefer= FirebaseDatabase.getInstance().getReference().child("Orders");
        orderRecyclerV.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options=
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderRefer,AdminOrders.class)
                .build();

        FirebaseRecyclerAdapter<AdminOrders,AdminOrderViewHolder> adapter=
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrderViewHolder adminOrderViewHolder,int i,@NonNull AdminOrders adminOrders) {
                      adminOrderViewHolder.userName.setText("Name : "+adminOrders.getName());
                        adminOrderViewHolder.userPhoneNo.setText("Phone : "+adminOrders.getPhone());
                        adminOrderViewHolder.userTotalPrice.setText("Total Amount : "+adminOrders.getTotalAmt());
                        adminOrderViewHolder.userDateTime.setText("Orders at : "+adminOrders.getDate() +" "+adminOrders.getTime());
                        adminOrderViewHolder.usershippingAddress.setText("Shipping Address : "+adminOrders.getAddress() +" "+adminOrders.getCity());

                        adminOrderViewHolder.showOrderbt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String uID=getRef(i).getKey();
                                Intent intent=new Intent(NewOrderActivityByAdmin.this,AdminUserProductActivity.class);
                                intent.putExtra("uid",uID);
                                startActivity(intent);
                            }
                        });
                        //when order dispatched then remove details from admin side
                        adminOrderViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence options[]=new CharSequence[]

                                        {
                                            "yes",
                                                "No"

                                         };
                                AlertDialog.Builder builder=new AlertDialog.Builder(NewOrderActivityByAdmin.this);
                                builder.setTitle("Have you shipped product ?");

                                builder.setItems(options,new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface,int i)
                                    {
                                        if (i==0)
                                        {
                                            String uID=getRef(i).getKey();
                                            removeOrder(uID);

                                        }else
                                            {
                                                finish();
                                         }
                                    }
                                });
                                builder.show();

                            }
                        });

                    }


                    @NonNull
                    @Override
                    public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_items,parent,false);
                        return new AdminOrderViewHolder(view);
                    }
                };
        orderRecyclerV.setAdapter(adapter);
        adapter.startListening();
    }



    public static class AdminOrderViewHolder extends RecyclerView.ViewHolder {
        TextView userName,userPhoneNo,userTotalPrice,usershippingAddress,userDateTime;
        Button showOrderbt;
        public AdminOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.order_user_Name);
            userPhoneNo=itemView.findViewById(R.id.order_phone_number);
            userTotalPrice=itemView.findViewById(R.id.order_price);
            usershippingAddress=itemView.findViewById(R.id.order_AddressCity);
            userDateTime=itemView.findViewById(R.id.order_date_time);

            showOrderbt=itemView.findViewById(R.id.show_order_btn);

        }
    }
    private void removeOrder(String uID)
    {

        orderRefer.child(uID).removeValue();

    }
}