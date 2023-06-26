package com.example.mindmolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class loginPage extends AppCompatActivity implements View.OnClickListener {

    Button submit_btn, register_btn;
    String Email, password;
    EditText email, p_assword;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    DocumentReference userDocRef;
    CollectionReference usersRef;
    public static MediaPlayer mediaPlayer;
    boolean isMuOn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // onCreate must haves
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // hide the action bar
        setContentView(R.layout.activity_login);

        submit_btn = findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(this);

        register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(this);

        email = findViewById(R.id.email);

        p_assword = findViewById(R.id.password);

        mediaPlayer = MediaPlayer.create(this ,R.raw.backgroundmusic);

        password = "";
        Email = "";

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        usersRef = db.collection("users");
        userDocRef = usersRef.document(user.getUid());

        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getDouble("isMusicOn") == 1.0){
                            isMuOn = false;
                        }
                    }
    }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.submit_btn){
            password = p_assword.getText().toString().trim();
            Email = email.getText().toString().trim();
            if (password.equals("") || Email.equals("")){
                Toast.makeText(loginPage.this, "You didn't enter password or email",Toast.LENGTH_SHORT).show();
            }
            else{
                mAuth.signInWithEmailAndPassword(Email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //backMusic.start();
                                Toast.makeText(loginPage.this, "Logged in Succesfully", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent nextPage = new Intent(loginPage.this, nextPage.class);
                                if (isMuOn) {
                                    mediaPlayer.start();
                                }
                                startActivity(nextPage);
                            } else {
                                Toast.makeText(loginPage.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    );
        }
        }

        if (view.getId() == R.id.register_btn){
            Intent register = new Intent(this, registerPage.class);
            startActivity(register);
        }
    }
}