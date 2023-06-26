package com.example.mindmolder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class nextPage extends AppCompatActivity implements View.OnClickListener {


    Button colorButton, leaderboard, settings, exitGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // hide the action bar
        setContentView(R.layout.activity_next_page);

        colorButton = findViewById(R.id.colorBtn);
        colorButton.setOnClickListener(this);

        leaderboard = findViewById(R.id.leaderboard);
        leaderboard.setOnClickListener(this);

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(this);

        exitGame = findViewById(R.id.exitGame);
        exitGame.setOnClickListener(this);

    }

    public void onClick(View view){
        if (view.getId() == R.id.colorBtn){
            loginPage.mediaPlayer.pause();
            finish();
            Intent colorsGame = new Intent(nextPage.this, colorsGame.class);
            startActivity(colorsGame);
        }

        if (view.getId() == R.id.leaderboard){
            finish();
            Intent leaderboard = new Intent(nextPage.this, leaderboard.class);
            startActivity(leaderboard);
        }

        if (view.getId() == R.id.settings){
            finish();
            Intent settings = new Intent(nextPage.this, settings.class);
            startActivity(settings);
        }

        if (view.getId() == R.id.exitGame){
            finishAffinity();
            System.exit(0);
        }
    }
}
