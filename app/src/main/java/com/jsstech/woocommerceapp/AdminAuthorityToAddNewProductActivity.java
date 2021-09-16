package com.jsstech.woocommerceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAuthorityToAddNewProductActivity extends AppCompatActivity {

    private String CategoryName,pDescription,pPrice,pName,saveCurrentDate,saveCurrentTime;
    private Button AddNewProductbt;
    private EditText prodName,prodPrice,prod_Desc;
    private ImageView productImage;
    private static final int picImgcode=1;
    private Uri imageUri;
    private String prodRandomKey,downloadUrl;
    private StorageReference pImagestorageRef;
    private DatabaseReference prodRefer;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_authority_to_add_new_product);
        progressDialog=new ProgressDialog(this);

        prodRefer=FirebaseDatabase.getInstance().getReference().child("Products");

        CategoryName=getIntent().getExtras().get("category").toString();
        pImagestorageRef= FirebaseStorage.getInstance().getReference().child("Product Images");

        AddNewProductbt=findViewById(R.id.add_new_product);

        productImage=findViewById(R.id.picImage);
        prodName=findViewById(R.id.product_name);
        prodPrice=findViewById(R.id.product_price);
        prod_Desc=findViewById(R.id.product_descr);


       // Toast.makeText(this,CategoryName,Toast.LENGTH_SHORT).show();
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //for pickup image from gallery
                PicImageFromGallery();
            }
        });

        AddNewProductbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatProductData();
            }
        });
    }

    private void ValidatProductData() {
        pDescription=prod_Desc.getText().toString();
        pPrice=prodPrice.getText().toString();
        pName=prodName.getText().toString();

        if (imageUri==null){
            Toast.makeText(this,"product image required",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pDescription)){
            Toast.makeText(this,"please write product description",Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(pPrice)){
            Toast.makeText(this,"please write product Price",Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(pName)){
            Toast.makeText(this,"please write product name",Toast.LENGTH_SHORT).show();

        }
        else{
            //display a time ,at a time admin add a product

            storeProductInfo();
        }





    }

    private void storeProductInfo() {

        progressDialog.setTitle("Add new Product");
        progressDialog.setMessage("Dear Admin please wait ,while we are adding product..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate=currentdate.format(calendar.getTime());

        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());

        //
        prodRandomKey=saveCurrentDate + saveCurrentTime;

        StorageReference filepath=pImagestorageRef.child(imageUri.getLastPathSegment() +prodRandomKey +".jpg");
        final UploadTask uploadTask=filepath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message=e.toString();
                Toast.makeText(AdminAuthorityToAddNewProductActivity.this,"Error"+message,Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAuthorityToAddNewProductActivity.this,"Image uploaded sucessfully",Toast.LENGTH_SHORT).show();

                //display imagelink from firebase
                Task<Uri> uritask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();

                        }
                        downloadUrl=filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadUrl=task.getResult().toString();
                            Toast.makeText(AdminAuthorityToAddNewProductActivity.this,"getting product image url sucessfully",Toast.LENGTH_SHORT).show();

                            saveProductInformationToDatabase();
                        }

                    }
                });

            }
        });


    }

    private void saveProductInformationToDatabase() {

        HashMap<String,Object> productMap=new HashMap<>();
        productMap.put("pid",prodRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("des",pDescription);
        productMap.put("image",downloadUrl);
        productMap.put("category",CategoryName);
        productMap.put("price",pPrice);
        productMap.put("pname",pName);

        prodRefer.child(prodRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Intent intent=new Intent(AdminAuthorityToAddNewProductActivity.this,AdminCategoryActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                    Toast.makeText(AdminAuthorityToAddNewProductActivity.this,"product is added sucessfully",Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(AdminAuthorityToAddNewProductActivity.this,"Error"+message,Toast.LENGTH_SHORT).show();
                }

            }
        });




    }

    private void PicImageFromGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,picImgcode);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {

        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode==picImgcode && resultCode==RESULT_OK && data!=null){
           imageUri=data.getData();

           productImage.setImageURI(imageUri);
        }
    }
}
