package com.example.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText userName;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private Button register;
    private TextView loginUser;
    private FirebaseAuth firebaseAuth;

    private DatabaseReference databaseReference;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userName=findViewById(R.id.username);
        name=findViewById(R.id.fullName);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        confirmPassword=findViewById(R.id.confirm_password);
        register=findViewById(R.id.register);
        loginUser=findViewById(R.id.login_user);
        firebaseAuth=FirebaseAuth.getInstance();

        databaseReference= FirebaseDatabase.getInstance().getReference();

        progressDialog=new ProgressDialog(this);

        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNameText=userName.getText().toString();
                String nameText=name.getText().toString();
                String emailText=email.getText().toString();
                String passwordText=password.getText().toString();
                String confirmPasswordText=confirmPassword.getText().toString();
                if(userNameText.isEmpty()){
                    userName.setError("Enter a UserName");
                }
                if(nameText.isEmpty()){
                    name.setError("Enter Your Name");
                }
                if(emailText.isEmpty()){
                    email.setError("E-mail is Required");
                }
                if(passwordText.isEmpty()){
                    password.setError("Password is Required");
                }
                if(confirmPasswordText.isEmpty()){
                    confirmPassword.setError("Password is Required to Verify");
                }
                if (!passwordText.equals(confirmPasswordText)){
                    confirmPassword.setError("Password does not Match");
                }
                if (userNameText.length()>0 && nameText.length()>0 && emailText.length()>0 && passwordText.length()>0
                        && confirmPasswordText.length()>0 &&passwordText.equals(confirmPasswordText)){
                    registerUser(userNameText,nameText,emailText,passwordText);
                }
            }
        });
    }

    private void registerUser(final String userName, final String name, final String email, final String password) {
        progressDialog.setMessage("Registering user...");
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String,Object> map=new HashMap<>();
                map.put("name",name);
                map.put("email",email);
                map.put("userName",userName);
                map.put("id",firebaseAuth.getCurrentUser().getUid());
                map.put("bio","");
                map.put("imageurl","default");

                databaseReference.child("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this,"User Registered Successfully",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this,LoginActivity.class).addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP
                            ));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}