package com.example.noteit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    EditText loginemail,loginpass;
    Button loginbtn,newuserbtn,goforget;

    ProgressBar loader;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginemail = findViewById(R.id.loginemail);
        loginpass = findViewById(R.id.loginpass);
        loginbtn = findViewById(R.id.loginbtn);
         newuserbtn= findViewById(R.id.newuserbtn);
        goforget= findViewById(R.id.goforget);
        loader= findViewById(R.id.loginloader);


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null ){
            finish();
            startActivity(new Intent(getApplicationContext(), Note.class));
        }

        newuserbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Signup.class));
            }
        });
        goforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Forgetpass.class));
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String email = loginemail.getText().toString().trim();
              String pass = loginpass.getText().toString().trim();

                if(email.isEmpty() || pass.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Pls fill all Fields properly",Toast.LENGTH_SHORT).show();
                }
                else{
                    loader.setVisibility(View.VISIBLE);
                    //login  user to fb
                    firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                checkMailVerification();
                            }else{
                                Toast.makeText(getApplicationContext(),"Account doesn't exist or Invalid Credentials",Toast.LENGTH_SHORT).show();
                                loader.setVisibility(View.INVISIBLE);

                            }
                        }
                    });

                }

            }
        });
    }
    private void checkMailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified()){
            Toast.makeText(getApplicationContext(),"Logged in successfully",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(),Note.class));
        }else {
            loader.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(),"Verify your email first",Toast.LENGTH_SHORT).show();
           firebaseAuth.signOut();
        }
    }
    }
