package com.example.mindmolder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class settings extends AppCompatActivity implements View.OnClickListener {

    Button retToMenu, deleteAccount;
    SwitchCompat music, vibrate;
    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    double isOnMu, isOnVib;
    CollectionReference usersRef;
    DocumentReference userDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide(); // hide the action bar

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        retToMenu = findViewById(R.id.retToMenu);
        retToMenu.setOnClickListener(this);

        deleteAccount = findViewById(R.id.delete);
        deleteAccount.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        userDocRef = usersRef.document(user.getUid());

        music = findViewById(R.id.music);
        vibrate = findViewById(R.id.vibrate);

        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve a specific field from the document
                        isOnMu = document.getDouble("isMusicOn");
                        music.setChecked(isOnMu == 0);
                        isOnVib = document.getDouble("isVibrationOn");
                        vibrate.setChecked(isOnVib == 0);
                    }
                    //else
                        //document does not exist
                }
                else{
                    Toast.makeText(settings.this, "There was an error during the receiving of music state" ,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (!music.isChecked()){
            isOnMu = 1.0;
        }
        else
            isOnMu = 0.0;

        if (!vibrate.isChecked()){
            isOnVib = 1.0;
        }
        else
            isOnVib = 0.0;

        if (view.getId() == R.id.retToMenu){
            userDocRef.update("isMusicOn",isOnMu).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("ERROR",e.getMessage());
                    Toast.makeText(settings.this, "There was an error during the change of music state" ,Toast.LENGTH_SHORT).show();
                }
            });

            userDocRef.update("isVibrationOn",isOnVib).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("ERROR",e.getMessage());
                    Toast.makeText(settings.this, "There was an error during the change of vibration state" ,Toast.LENGTH_SHORT).show();
                }
            });

            finish();
            Intent menu = new Intent(settings.this,nextPage.class);
            if (isOnMu == 0 && loginPage.mediaPlayer != null){
                loginPage.mediaPlayer.start();
            }

            else if(isOnMu == 1){
                loginPage.mediaPlayer.stop();
            }

            startActivity(menu);
        }

        if (view.getId() == R.id.delete){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Account");
            builder.setMessage("Are you sure you want to delete your account? \n\n(if you will delete your account the app will go to the register page so you can register again)");

            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User clicked Delete button, so delete the account
                    deleteAccount();
                    finish();
                    Intent register = new Intent(settings.this,registerPage.class);
                    startActivity(register);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // User clicked Cancel button, so do nothing
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

    private void deleteAccount(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = user.getUid();
        DocumentReference userRef = db.collection("users").document(userId);
        StorageReference userDocRef = FirebaseStorage.getInstance().getReference().child(user.getUid());

        userDocRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // User data deleted successfully from storage
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete user data from storage
                        Toast.makeText(settings.this, "There was an error deleting your account", Toast.LENGTH_SHORT).show();
                    }
                });

        userRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Document deleted successfully
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to delete document
                Toast.makeText(settings.this, "There was an error deleting your account", Toast.LENGTH_SHORT).show();
            }
        });

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(settings.this, "User account deleted successfully",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(settings.this, "There was an error deleting your account", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}