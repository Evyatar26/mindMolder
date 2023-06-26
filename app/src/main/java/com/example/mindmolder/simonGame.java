package com.example.mindmolder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class simonGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simon_game);
        getSupportActionBar().hide();
    }
}