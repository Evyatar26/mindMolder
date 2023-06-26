package com.example.mindmolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class userInfo extends AppCompatActivity implements View.OnClickListener {

    user u;
    ImageView iv;
    TextView name,score,position;
    Button retToLeaderboard, retToMenu;
    FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        getSupportActionBar().hide(); // hide the action bar

        if (leaderboard.firstU) {
            Toast.makeText(this, "loading...", Toast.LENGTH_SHORT).show();
            leaderboard.firstU = false;
        }

        iv = findViewById(R.id.imageUser);
        name = findViewById(R.id.userName);
        score = findViewById(R.id.score);
        position = findViewById(R.id.place);

        retToLeaderboard = findViewById(R.id.backToLeaderboard);
        retToLeaderboard.setOnClickListener(this);

        retToMenu = findViewById(R.id.backToMenu);
        retToMenu.setOnClickListener(this);

        Intent i = getIntent();
        u = (user) i.getExtras().get("user");

        name.setText("Name: " + u.getName());
        score.setText("Score: " + (int) u.getScore());
        position.setText("Position: " + u.getPosition());

        firebaseStorage = FirebaseStorage.getInstance();
        StorageReference pathReference = firebaseStorage.getReference();

        pathReference.child(u.getUserUid()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                iv.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ERROR", e.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.backToLeaderboard){
            Intent Leaderboard = new Intent(this,leaderboard.class);
            finish();
            startActivity(Leaderboard);
        }

        if (view.getId() == R.id.backToMenu){
            Intent Menu = new Intent(this,nextPage.class);
            finish();
            startActivity(Menu);
        }
    }
}