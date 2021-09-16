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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jsstech.woocommerceapp.Model.CartModel;
import com.jsstech.woocommerceapp.Prevalent.Prevalent;
import com.jsstech.woocommerceapp.ViewHolder.CartViewHolder;

import java.text.NumberFormat;

public class CartActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Button NextBtn;
    TextView textViewTotalPrice,textMsg1;

    private int overTotalPrice=0;

    @Override
    protected void onStart() {
        super.onStart();
        //textViewTotalPrice.setText("Total Price = $" + String.valueOf(overTotalPrice));
        CheckOrderState();

        final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<CartModel> options=new FirebaseRecyclerOptions.Builder<CartModel>()
                .setQuery(cartListRef.child("User View")
                        .child(Prevalent.CurrentonlineUser.getPhone())
                        .child("Products"),CartModel.class)
                        .build();

        FirebaseRecyclerAdapter<CartModel, CartViewHolder> adapter
                =new FirebaseRecyclerAdapter<CartModel, CartViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder,int i,@NonNull CartModel cartModel) {
                cartViewHolder.txtProductQuantity.setText("Quantity = "+ cartModel.getQuantity());
                cartViewHolder.txtProductPrice.setText("Price" +cartModel.getPrice());
                cartViewHolder.txtProductName.setText(cartModel.getPname());

                //getting total price
                int oneTypeProduct=((Integer.valueOf(cartModel.getPrice()))) * Integer.valueOf(cartModel.getQuantity());
                overTotalPrice=overTotalPrice + oneTypeProduct;

                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       CharSequence options[] = new CharSequence[]
                               {
                                       "Edit",
                                       "Delete"

                               };
                       AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                       builder.setTitle("Cart Option");
                       builder.setItems(options,new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface,int i) {
                               //for Edit
                               if (i == 0)
                               {
                                   Intent intent=new Intent(CartActivity.this,ProductDetailsActivity.class);
                                   intent.putExtra("pid",cartModel.getPid());
                                   startActivity(intent);
                               }
                               if (i == 1){
                                   //not working

//                                   cartListRef.child("User View")
//                                           .child(Prevalent.CurrentonlineUser.getPhone())
//                                           .child("products")
//                                           .child(cartModel.getPid())
//                                           .removeValue()
//                                           .addOnCompleteListener(task -> {
//                                                   if (task.isSuccessful())
//                                                   {
//                                                       Toast.makeText(CartActivity.this,"Item remove sucessfully",Toast.LENGTH_SHORT).show();
//                                                       Intent intent=new Intent(CartActivity.this,CartActivity.class);
//                                                       startActivity(intent);
//                                                   }
//
//
//                                           });
                                   //for remove cartList Item
                                   cartListRef.child("User View")
                                           .child(Prevalent.CurrentonlineUser.getPhone())
                                           .child("Products")
                                           .child(cartModel.getPid())
                                           .removeValue()
                                           .addOnCompleteListener(task -> {
                                               if (task.isSuccessful())
                                               {
                                                   Toast.makeText(CartActivity.this, "Item removed successfully", Toast.LENGTH_SHORT).show();

                                                   Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                                   startActivity(intent);
                                               }

                                           });

                               }
                           }
                       });
                       builder.show();
                   }
               });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType)
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder=new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }
    private void CheckOrderState(){

        DatabaseReference ordeRef;
        ordeRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.CurrentonlineUser.getPhone());
        ordeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String shippingState=snapshot.child("state").getValue().toString();
                    String username=snapshot.child("name").getValue().toString();

                    if (shippingState.equals("shipped"))
                    {
                       textViewTotalPrice.setText("Dear "+ username + "\n order is shipped sucessfully");
                       recyclerView.setVisibility(View.GONE);
                       textMsg1.setVisibility(View.VISIBLE);
                       textMsg1.setText("Congrulation your final order has been shipped at you door steps\"\n");
                       NextBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this,"You can purches more product",Toast.LENGTH_SHORT).show();

                    }
                        else if (shippingState.equals("not shipped"))
                    {
                        textViewTotalPrice.setText("Shipping state = Not Shipped");
                        recyclerView.setVisibility(View.GONE);
                        textMsg1.setVisibility(View.VISIBLE);
                        NextBtn.setVisibility(View.GONE);
                        Toast.makeText(CartActivity.this,"You can purches more product",Toast.LENGTH_SHORT).show();


                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        NextBtn=findViewById(R.id.next_procee_bt);
        textViewTotalPrice=findViewById(R.id.totall_price);
        textMsg1=findViewById(R.id.msg_one);

        recyclerView=findViewById(R.id.cart_ListRv);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textViewTotalPrice.setText("Total Price = $" + String.valueOf(overTotalPrice));

               Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(intent);
               finish();

            }
        });



    }
}