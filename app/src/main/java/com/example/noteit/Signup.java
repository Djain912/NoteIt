package com.example.noteit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signup extends AppCompatActivity {
    EditText signupemail,signpass;
    Button signupbtn,already;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupemail = findViewById(R.id.signupemail);
        signpass = findViewById(R.id.signpass);
        signupbtn = findViewById(R.id.signupbtn);
        already = findViewById(R.id.already);

        firebaseAuth= FirebaseAuth.getInstance();

        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signupemail.getText().toString().trim();
                String pass = signpass.getText().toString().trim();

                if(email.isEmpty() || pass.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Pls fill all Fields properly",Toast.LENGTH_SHORT).show();
                } else if (pass.length()<8) {
                    Toast.makeText(getApplicationContext(),"Password must be atleast 8 characters",Toast.LENGTH_SHORT).show();
                }
                else{
                    //regestring user to fb

                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Regestration Succuessful",Toast.LENGTH_SHORT).show();
                                sendEmailVerification();
                            }else{
                                Toast.makeText(getApplicationContext(),"Regestration Unsuccuessful",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });

    }
    private  void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(),"Verification email send",Toast.LENGTH_SHORT).show();
                    finish();
//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"Fail to send Verification email ",Toast.LENGTH_SHORT).show();

        }
    }
}