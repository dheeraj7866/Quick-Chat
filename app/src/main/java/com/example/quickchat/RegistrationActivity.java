package com.example.quickchat;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    TextView txt_signin;
    EditText reg_name,reg_email,reg_password,reg_Cpassword;
    CircleImageView profile_image;
    Button btn_signup;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUri;
    FirebaseDatabase  database;
    FirebaseStorage storage;
    String imageURI;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(true);

        btn_signup=findViewById(R.id.btn_signup);
        profile_image=findViewById(R.id.profile_image);
        reg_name=findViewById(R.id.reg_name);
        reg_email=findViewById(R.id.reg_email);
        reg_password=findViewById(R.id.reg_password);
        reg_Cpassword=findViewById(R.id.reg_Cpassword);
        txt_signin=findViewById(R.id.txt_signin);

        txt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                //progressDialog.dismiss();
                String name= reg_name.getText().toString();
                String email= reg_email.getText().toString();
                String password= reg_password.getText().toString();
                String Cpassword= reg_Cpassword.getText().toString();
                String status="Hey there I'm using QuickChat";

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
                        || TextUtils.isEmpty(name) || TextUtils.isEmpty(Cpassword)){
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this,"Enter valid data",Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailPattern)){
                    progressDialog.dismiss();
                    reg_email.setError("Enter valid email");
                    Toast.makeText(RegistrationActivity.this,"Enter valid email",Toast.LENGTH_SHORT).show();
                } else if (!password.equals(Cpassword)){
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this,"Password doesn't matches",Toast.LENGTH_SHORT).show();
                }
                else if (password.length()<6) {
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this,"Enter valid password",Toast.LENGTH_SHORT).show();
                } else {

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();

                                DatabaseReference databaseReference=database.getReference().child("user").child(auth.getUid());
                                StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());

                                if(imageUri!=null){
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){

                                                progressDialog.dismiss();
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI=uri.toString();
                                                        Users users = new Users(auth.getUid(), name,email,imageURI,status);
                                                        databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                                                } else {
                                                                    Toast.makeText(RegistrationActivity.this,"Error in creating user",Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    imageURI="https://firebasestorage.googleapis.com/v0/b/quickchat-74e5c.appspot.com/o/download.png?alt=media&token=eb1f1484-e8a2-49bd-8ac6-fc9c22cb32f1";
                                    Users users = new Users(auth.getUid(), name,email,imageURI,status);
                                    databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                            } else {
                                                Toast.makeText(RegistrationActivity.this,"Error in creating user",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(RegistrationActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                        10);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==10){
            if (data!=null){
                imageUri=data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }

}