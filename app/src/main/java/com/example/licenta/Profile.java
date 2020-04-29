package com.example.licenta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private EditText Name,Description;
    private Button btnBack,btnConfirm;
    private ImageView ProfilePicture;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;

    private String userID, nume, descriere, profilImageUrl;

    private Uri resultUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Name = findViewById(R.id.name);
        Description = findViewById(R.id.description);

        ProfilePicture=findViewById(R.id.profile_Image);

        btnBack = findViewById(R.id.back);
        btnConfirm = findViewById(R.id.confirm);

        mAuth= FirebaseAuth.getInstance();
        userID=mAuth.getCurrentUser().getUid();
        mCustomerDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(userID);//trebuie sa pun aici sportul pe care l practica

        getUserInfo();

        ProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);//unu ne zice ca rezultatul din orice intent e acesta
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
    }

    private  void getUserInfo(){
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map= (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        nume=map.get("name").toString();
                        Name.setText(nume);
                    }
                    if(map.get("description")!=null){
                        descriere=map.get("description").toString();
                        Description.setText(descriere);
                    }
                    if(map.get("profil image")!=null){
                        profilImageUrl=map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(profilImageUrl).into(ProfilePicture);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation(){
        nume=Name.getText().toString();
        descriere=Description.getText().toString();

        Map userInfo= new HashMap();
        userInfo.put("name",nume);
        userInfo.put("description",descriere);
        mCustomerDatabase.updateChildren(userInfo);
        if(resultUri != null) {
            StorageReference filepath= FirebaseStorage.getInstance().getReference().child("profileImages").child(userID);
            Bitmap bitmap=null;
            try {  //e mai sigur cu try...catch
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            }catch (IOException e){
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask=filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl=taskSnapshot.getMetadata().getReference().getDownloadUrl();

                    Map userInfo=new HashMap();
                  userInfo.put("profileImageUrl",downloadUrl.toString());
                    mCustomerDatabase.updateChildren(userInfo);

                    finish();
                    return;
                }
            });

        }else{
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && requestCode== Activity.RESULT_OK){
            final Uri imageUri=data.getData();
            resultUri=imageUri;
            ProfilePicture.setImageURI(resultUri);
        }
    }


}
