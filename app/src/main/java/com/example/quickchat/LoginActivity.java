package com.example.quickchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextView txt_signup;
    EditText login_email,login_password;
    Button signin_btn;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(true);

        login_email=findViewById(R.id.login_email);
        login_password=findViewById(R.id.login_password);
        signin_btn=findViewById(R.id.signin_btn);

        txt_signup=findViewById(R.id.txt_signup);
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });

        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                //progressDialog.dismiss();
                String email = login_email.getText().toString();
                String password = login_password.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Enter valid data",Toast.LENGTH_SHORT).show();
                } else if (!email.matches(emailPattern)){
                    progressDialog.dismiss();
                    login_email.setError("Enter valid email");
                    Toast.makeText(LoginActivity.this,"Enter valid email",Toast.LENGTH_SHORT).show();
                } else if (password.length()<6) {
                    progressDialog.dismiss();
                    login_password.setError("Enter valid password");
                    Toast.makeText(LoginActivity.this,"Enter valid password",Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Error in Login", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}