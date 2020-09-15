package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasedemo.Domain.User;
import com.example.firebasedemo.fragments.ProfileFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView close;
    private CircleImageView imageProfile;
    private TextView save;
    private TextView changePhoto;
    private MaterialEditText fullName;
    private MaterialEditText userName;
    private MaterialEditText bio;

    private FirebaseUser fuser;

    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        close=findViewById(R.id.close);
        imageProfile=findViewById(R.id.image_profile);
        save=findViewById(R.id.save);
        changePhoto=findViewById(R.id.change_photo);
        fullName=findViewById(R.id.fullName);
        userName=findViewById(R.id.username);
        bio=findViewById(R.id.bio);

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference().child("Uploads");

        FirebaseDatabase.getInstance().getReference().child("Users").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                fullName.setText(user.getName());
                userName.setText(user.getUserName());
                bio.setText(user.getBio());

                Picasso.get().load(user.getImageurl()).into(imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
                startActivity(new Intent(EditProfileActivity.this, ProfileFragment.class));
                finish();
            }
        });
    }

    private void updateProfile() {
        HashMap<String,Object> map=new HashMap<>();
        map.put("name",fullName.getText().toString());
        map.put("userName",userName.getText().toString());
        map.put("bio",bio.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("Users").child(fuser.getUid()).updateChildren(map);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            uploadImage();
        }
        else {
            Toast.makeText(EditProfileActivity.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Uploading..");
        pd.show();

        if (imageUri!=null){
            final StorageReference fileRef=storageReference.child(System.currentTimeMillis()+".jpeg");

            uploadTask=fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (task.isSuccessful()){
                        return fileRef.getDownloadUrl();
                    }
                    else {
                        throw task.getException();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        String url=downloadUri.toString();

                        FirebaseDatabase.getInstance().getReference().child("Users").child(fuser.getUid()).child("imageurl").
                                setValue(url);
                        pd.dismiss();
                    }
                    else {
                        Toast.makeText(EditProfileActivity.this,"Upload Failed",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(EditProfileActivity.this,"No Image Selected",Toast.LENGTH_SHORT).show();
        }
    }
}