package com.jsstech.woocommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jsstech.woocommerceapp.Model.CartModel;
import com.jsstech.woocommerceapp.ViewHolder.CartViewHolder;

public class AdminUserProductActivity extends AppCompatActivity {
RecyclerView productLisRecv;
RecyclerView.LayoutManager layoutManager;
DatabaseReference cartListRefe;

private String userId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_product);

        userId=getIntent().getStringExtra("uid");

        productLisRecv=findViewById(R.id.product_listRv);
        productLisRecv.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        productLisRecv.setLayoutManager(layoutManager);


        cartListRefe=FirebaseDatabase.getInstance().getReference()
                .child("Cart List").child("Admin View").child(userId).child("Products");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<CartModel> options=
                new FirebaseRecyclerOptions.Builder<CartModel>()
                .setQuery(cartListRefe,CartModel.class)
                .build();

        FirebaseRecyclerAdapter<CartModel, CartViewHolder> adapter=
                new FirebaseRecyclerAdapter<CartModel, CartViewHolder>(options)
                {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder,int i,@NonNull CartModel cartModel) {
                cartViewHolder.txtProductQuantity.setText("Quantity = "+ cartModel.getQuantity());
                cartViewHolder.txtProductPrice.setText("Price" +cartModel.getPrice());
                cartViewHolder.txtProductName.setText(cartModel.getPname());
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder=new CartViewHolder(view);
                return holder;
            }
        };
        productLisRecv.setAdapter(adapter);
        adapter.startListening();

    }
}