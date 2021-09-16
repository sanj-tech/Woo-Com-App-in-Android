package com.jsstech.woocommerceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jsstech.woocommerceapp.Model.Products;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintaiProductAct extends AppCompatActivity {
    EditText name, price, description;
    Button applyChanges, deletebtnP;
    ImageView imageViewP;

    private String productId = "";
    DatabaseReference prodRefere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintai_product);


        productId = getIntent().getStringExtra("pid");

        prodRefere = FirebaseDatabase.getInstance().getReference().child("Products").child(productId);


        name = findViewById(R.id.maintain_productName);
        price = findViewById(R.id.maintain_product_price);
        description = findViewById(R.id.maintain_Descriptin);
        applyChanges = findViewById(R.id.apply_changes_bt);
        imageViewP = findViewById(R.id.maintain_image);
        deletebtnP = findViewById(R.id.delete_cbt);

        displayProductInfoD();

        applyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyChang();
            }
        });

        deletebtnP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteThisProduct();

            }
        });
    }

    //delete product by admin
    private void deleteThisProduct() {

        prodRefere.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminMaintaiProductAct.this,"Product deleted sucessfully",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(AdminMaintaiProductAct.this,AdminCategoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void applyChang() {

        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDes = description.getText().toString();

        if (pName.equals("")) {
            Toast.makeText(this,"please write name",Toast.LENGTH_SHORT).show();
        } else if (pPrice.equals("")) {
            Toast.makeText(this,"please write price",Toast.LENGTH_SHORT).show();
        } else if (pDes.equals("")) {
            Toast.makeText(this,"please write Description",Toast.LENGTH_SHORT).show();

        } else {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid",productId);
            productMap.put("des",pDes);
            productMap.put("price",pPrice);
            productMap.put("pname",pName);
            prodRefere.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AdminMaintaiProductAct.this,"change applied sucessfully",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminMaintaiProductAct.this,AdminCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displayProductInfoD() {


        prodRefere.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String pname = snapshot.child("pname").getValue().toString();
                    String pprice = snapshot.child("price").getValue().toString();
                    String pdescription = snapshot.child("des").getValue().toString();
                    String pimage = snapshot.child("image").getValue().toString();

                    name.setText(pname);
                    price.setText(pprice);
                    description.setText(pdescription);
                    Picasso.get().load(pimage).into(imageViewP);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}