package com.jsstech.woocommerceapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.jsstech.woocommerceapp.Prevalent.Prevalent;
import com.jsstech.woocommerceapp.ViewHolder.ProductViewHolder;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {
    EditText editTextPhone,editTextFullName,editTextAddress;
    TextView profile_change_Txtbt,closeTxtBt,saveTxtBt;
    CircleImageView profile_setting_image;
    Button securityBt;
    //allow user to changes
    private Uri imageUri;
    private String myUrl="";
    private StorageTask uploadTask;
    private StorageReference storageProfilePicRef;
    private String checker="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        storageProfilePicRef= FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        editTextPhone=findViewById(R.id.setting_phone_No);
        editTextFullName=findViewById(R.id.setting_full_name);
        editTextAddress=findViewById(R.id.setting_address);
        closeTxtBt=findViewById(R.id.close_setting);
        saveTxtBt=findViewById(R.id.update_setting);
        profile_change_Txtbt=findViewById(R.id.profile_change_image);
        profile_setting_image=findViewById(R.id.setting_profileImage);
        securityBt=findViewById(R.id.security_ques_bt);

        userInfoDisplay(profile_setting_image,editTextFullName,editTextPhone,editTextAddress);

        closeTxtBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        securityBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SettingActivity.this,ResetPassActivity.class);
               intent.putExtra("check","settings");
                startActivity(intent);
            }
        });

        saveTxtBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked")){
                    userInfoSave();

                }else {
                    updateOnlyUserInfo();
                }
            }
        });
        profile_change_Txtbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker="clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(SettingActivity.this);


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {

        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){

            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            //for setting of image selection
            profile_setting_image.setImageURI(imageUri);

        }else {
            Toast.makeText(this,"Error Try again",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SettingActivity.this,SettingActivity.class));
            finish();
        }
    }

    private void userInfoSave(){
        if (TextUtils.isEmpty(editTextFullName.getText().toString())){
            Toast.makeText(this,"Name is mandatory",Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(editTextAddress.getText().toString())){
            Toast.makeText(this,"Name is address",Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(editTextPhone.getText().toString())){
            Toast.makeText(this,"Name is address",Toast.LENGTH_SHORT).show();

        }else if (checker.equals("clicked")) {
            uploadImage();
        }

    }

    private void uploadImage() {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("update Profile");
        progressDialog.setMessage("Please update We are updating your profile info");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (imageUri!=null){
            final StorageReference fileRef=storageProfilePicRef
                    .child(Prevalent.CurrentonlineUser.getPhone() +".jpg");
            uploadTask=fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {

                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUrl=task.getResult();
                        myUrl=downloadUrl.toString();

                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
                        HashMap<String,Object> userMap=new HashMap<>();
                        userMap.put("name",editTextFullName.getText().toString());
                        userMap.put("address",editTextAddress.getText().toString());
                        userMap.put("phoneOrder",editTextPhone.getText().toString());
                        userMap.put("image",myUrl);

                        ref.child(Prevalent.CurrentonlineUser.getPhone()).updateChildren(userMap);
                        progressDialog.dismiss();


                        startActivity(new Intent(SettingActivity.this,HomeActivity.class));
                        Toast.makeText(SettingActivity.this,"Profile info added sucessfully",Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(this,"image not selected",Toast.LENGTH_SHORT).show();
        }

    }

    private void updateOnlyUserInfo(){

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String,Object> userMap=new HashMap<>();
        userMap.put("name",editTextFullName.getText().toString());
        userMap.put("address",editTextAddress.getText().toString());
        userMap.put("phoneOrder",editTextPhone.getText().toString());

        ref.child(Prevalent.CurrentonlineUser.getPhone()).updateChildren(userMap);


        startActivity(new Intent(SettingActivity.this,HomeActivity.class));
        Toast.makeText(SettingActivity.this,"Profile info added sucessfully",Toast.LENGTH_SHORT).show();
        finish();





    }

    private void userInfoDisplay(CircleImageView profile_setting_image,EditText editTextFullName,EditText editTextPhone,EditText editTextAddress) {

        DatabaseReference UsersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.CurrentonlineUser.getPhone());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("image").exists()){
                        String image=snapshot.child("image").getValue().toString();
                        String name=snapshot.child("name").getValue().toString();
                        String phone=snapshot.child("phone").getValue().toString();
                        String address=snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profile_setting_image);
                        editTextFullName.setText(name);
                        editTextPhone.setText(phone);
                        editTextAddress.setText(address);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}