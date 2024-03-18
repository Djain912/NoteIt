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
import com.google.firebase.auth.FirebaseAuth;

public class Forgetpass extends AppCompatActivity {
    Button forgetback,forgetbtn;
    EditText forgetemail;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpass);

        forgetemail = findViewById(R.id.forgetemail);
        forgetback = findViewById(R.id.forgetback);
        forgetbtn = findViewById(R.id.forgetbtn);

        firebaseAuth = FirebaseAuth.getInstance();

        forgetback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

        forgetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = forgetemail.getText().toString().trim();

                if(mail.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter your mail",Toast.LENGTH_SHORT).show();
                }else {
                    // recover password logic

                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"You can recover your password using mail",Toast.LENGTH_SHORT).show();
finish();
startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }else{
                                Toast.makeText(getApplicationContext(),"Wrong credential",Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });


    }
}