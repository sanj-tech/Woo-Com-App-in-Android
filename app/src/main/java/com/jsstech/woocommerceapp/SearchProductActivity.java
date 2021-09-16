package com.jsstech.woocommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jsstech.woocommerceapp.Model.Products;
import com.jsstech.woocommerceapp.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;

public class SearchProductActivity extends AppCompatActivity {
    Button search_bt;
    RecyclerView Search_recyclerView;
    EditText productName;
    private String SearchInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        search_bt = findViewById(R.id.search_bt);
        Search_recyclerView = findViewById(R.id.searchRv);
        Search_recyclerView.setLayoutManager(new LinearLayoutManager(SearchProductActivity.this));
        productName = findViewById(R.id.search_product_name);

        search_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchInput = productName.getText().toString();
               //every time call onstart method when user search product
                onStart();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Products> options=
                new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(reference.orderByChild("pname").startAt(SearchInput),Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter=
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder,int i,@NonNull Products products)
                    {
                        holder.txtProductName.setText(products.getPname());
                        holder.txtProductDescription.setText(products.getDes());
                        holder.  txtProductPrice.setText("Price= "+products.getPrice());
                        Picasso.get().load(products.getImage()).into(holder.pImageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(SearchProductActivity.this,ProductDetailsActivity.class);
                                intent.putExtra("pid",products.getPid());
                                startActivity(intent);

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout,parent,false);
                        ProductViewHolder holder=new ProductViewHolder(view);
                        return holder;
                    }
                };
        Search_recyclerView.setAdapter(adapter);
        adapter.startListening();


    }
}
