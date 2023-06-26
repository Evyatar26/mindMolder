package com.example.mindmolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Map;

public class colorsGame extends AppCompatActivity {

    TextView color;
    TextView scoreView;
    ProgressBar progressBar;

    ImageButton[] btnArr = new ImageButton[6];
    int[] colorList = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.BLACK,
            Color.GRAY, Color.WHITE, Color.rgb(255, 165, 0)};
    String[] colorNames = {"Red", "Green", "Blue", "Yellow", "Magenta", "Cyan", "Black",
            "Gray", "White", "Orange"};

    int score;
    double highestScore;

    int[] tempArray = new int[6];

    public static int tempIndex;

    Button menu, restart;

    boolean firstTouch,isGameOver,isProgressOver;

    Vibrator vibe;

    MediaPlayer gameMusic,gameOverMusic;

    long startTime;

    CountDownTimer countDown;

    FirebaseUser user;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    DocumentReference userDocRef;
    CollectionReference usersRef;

    MusicService service;
    ServiceConnection serviceConnection;

    MediaPlayer mpGame, mpOver;

    boolean isMuOn, isVibOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

            }

        }, 2500;
        */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colors_game);
        getSupportActionBar().hide(); // hide the action bar

        firstTouch = true;

        color = findViewById(R.id.color);

        progressBar = findViewById(R.id.progress);
        progressBar.setProgress(0);

        btnArr[0] = findViewById(R.id.button1);
        btnArr[0].setOnClickListener(this::runGame);

        btnArr[1] = findViewById(R.id.button2);
        btnArr[1].setOnClickListener(this::runGame);

        btnArr[2] = findViewById(R.id.button3);
        btnArr[2].setOnClickListener(this::runGame);

        btnArr[3] = findViewById(R.id.button4);
        btnArr[3].setOnClickListener(this::runGame);

        btnArr[4] = findViewById(R.id.button5);
        btnArr[4].setOnClickListener(this::runGame);

        btnArr[5] = findViewById(R.id.button6);
        btnArr[5].setOnClickListener(this::runGame);

        score = 0;
        scoreView = findViewById(R.id.score);

        menu = findViewById(R.id.menu);
        menu.setOnClickListener(this::OnLoseGame);

        restart = findViewById(R.id.restart);
        restart.setOnClickListener(this::OnLoseGame);

        menu.setVisibility(View.INVISIBLE);
        restart.setVisibility(View.INVISIBLE);

        //gameMusic = MediaPlayer.create(this,R.r);
        //gameOverMusic = MediaPlayer.create(this,R.raw.gameovermusic);

        isGameOver = false;
        isProgressOver = false;

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
                        highestScore = document.getDouble("scoreColorGame");
                        if (document.getDouble("isMusicOn") == 1.0){
                            isMuOn = false;
                        }
                        else {
                            isMuOn = true;
                        }
                        if (document.getDouble("isVibrationOn") == 1.0){
                            isVibOn = false;
                        }
                        else{
                            isVibOn = true;
                        }
                    }
                }
                //else
                //document does not exist

                else{
                    Toast.makeText(colorsGame.this, "There was an error during the receiving your highest score" ,Toast.LENGTH_SHORT).show();
                }

            }
        });

        progressBar.setMax(100); // Set the maximum progress value to 150
        progressBar.setProgress(100); // Set the initial progress to max (e.g., 150)
        countDown = new CountDownTimer(1500, 30) {
            @Override
            public void onTick(long millisUntilFinished) {
                // On every tick the value of the seconds is changing
                progressBar.setProgress(progressBar.getProgress()-2);

            }

            @Override
            public void onFinish() {
                isProgressOver = true;
                if (!isGameOver) {
                    gameOver();
                }
            }
        };

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                MusicService.LocalBinder binder = (MusicService.LocalBinder) iBinder;
                service = binder.getService();

            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                service = null;
            }
        };

        mpGame = MediaPlayer.create(this,R.raw.color_game_music);
        mpOver = MediaPlayer.create(this,R.raw.gameovermusic);
    }


    protected void startMusic() {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void stopMusic() {
        if (service != null && serviceConnection!=null) {
            service.stopMusic();
            //unbindService(serviceConnection);
        }
    }


/*
    @Override
    protected void onStop() {
        super.onStop();
        if (service != null) {
            unbindService(serviceConnection);
            service = null;
        }
    }
 */


    public void OnLoseGame(View view){
        if (view.getId() == R.id.restart) {
            finish();
            Intent colorGame = new Intent(this, colorsGame.class);
            startActivity(colorGame);
        }

        if (view.getId() == R.id.menu) {
            finish();
            loginPage.mediaPlayer.start();
            Intent menu = new Intent(this, nextPage.class);
            startActivity(menu);
        }
    }

    private void startTimer() {
        // create a countdown timer for 1.5 seconds
        countDown.cancel();
        progressBar.setMax(100); // Set the maximum progress value to 100
        progressBar.setProgress(100); // Set the initial progress to max (e.g., 100)

        // start the counting
        isProgressOver = false;
        countDown.start();
    }


    public void runGame(View view) {
        //gameMusic.start();
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //long startTime = System.currentTimeMillis(); // get current time in milliseconds

        if (firstTouch) {
            tempIndex = (int) (Math.random() * (10));
            case1(view);
            firstTouch = false;
            startTimer();
            return;
        }
/*
        if (isProgressOver) {
            isGameOver = true;
            gameOver();
            return;
        }
*/

        if (view.getId() == R.id.button1) {
            if (isVibOn){
                vibe.vibrate(100);
            }
            ColorDrawable viewColor = (ColorDrawable) (btnArr[0]).getBackground();
            int colorId = viewColor.getColor();
            if (colorId == colorList[tempIndex]) {
                score++;
                updateScore();
                startTimer();
            } else {
                isGameOver = true;
                gameOver();
            }
        }

        if (view.getId() == R.id.button2) {
            if (isVibOn) {
                vibe.vibrate(100);
            }
            ColorDrawable viewColor = (ColorDrawable) (btnArr[1]).getBackground();
            int colorId = viewColor.getColor();
            if (colorId == colorList[tempIndex]) {
                score++;
                updateScore();
                startTimer();
            } else {
                isGameOver = true;
                gameOver();
            }
        }


        if (view.getId() == R.id.button3) {
            if (isVibOn){
                vibe.vibrate(100);
            }
            ColorDrawable viewColor = (ColorDrawable) (btnArr[2]).getBackground();
            int colorId = viewColor.getColor();
            if (colorId == colorList[tempIndex]) {
                score++;
                updateScore();
                startTimer();
            } else {
                isGameOver = true;
                gameOver();
            }
        }


        if (view.getId() == R.id.button4) {
            if (isVibOn) {
                vibe.vibrate(100);
            }
            ColorDrawable viewColor = (ColorDrawable) (btnArr[3]).getBackground();
            int colorId = viewColor.getColor();
            if (colorId == colorList[tempIndex]) {
                score++;
                updateScore();
                startTimer();
            } else {
                isGameOver = true;
                gameOver();
            }
        }


        if (view.getId() == R.id.button5) {
            if (isVibOn) {
                vibe.vibrate(100);
            }
            ColorDrawable viewColor = (ColorDrawable) (btnArr[4]).getBackground();
            int colorId = viewColor.getColor();
            if (colorId == colorList[tempIndex]) {
                score++;
                updateScore();
                startTimer();
            } else {
                isGameOver = true;
                gameOver();
            }
        }


        if (view.getId() == R.id.button6) {
            if (isVibOn) {
                vibe.vibrate(100);
            }
            ColorDrawable viewColor = (ColorDrawable) (btnArr[5]).getBackground();
            int colorId = viewColor.getColor();
            if (colorId == colorList[tempIndex]) {
                score++;
                updateScore();
                startTimer();
            } else {
                isGameOver = true;
                gameOver();
            }
        }

        if (!isGameOver && !isProgressOver) {
            startTimer();
            tempIndex = (int) (Math.random() * (10));
            loop();
        }
    }


    public void setColorBackground() {
        // make sure that temp array has random values and that it has the string color position in it,
        // then it puts in every image button color from the colorList in the tempArray position.

        //Log.d("SUCCESS", "Success");
        makeSureExist();
        for(int i = 0; i<btnArr.length;i++){
            btnArr[i].setBackgroundColor(colorList[tempArray[i]]);
        }
    }


    public void loop(){
        String s = colorNames[tempIndex];
        color.setText(s);
        int index = (int) (Math.random() * (10));
        color.setTextColor(colorList[index]);
        setColorBackground();
    }


    public boolean containsDuplicate(int[] nums) {
        for(int i = 0; i < nums.length; i++) {
            for(int j = i + 1; j < nums.length; j++) {
                if(nums[i] == nums[j]) {
                    return true;
                }
            }
        }
        return false;
    }


    public void getTempArray(){
        // checks if the array from getArray() (arr) returns an array with no duplicates (so the color won't appear more than once),
        // if it doesn't then its sets a new array (temp array) with the arr values
        tempArray = new int[6];
        int[] arr = getArray();
        while (containsDuplicate(arr)){
            arr = getArray();
        }
        for (int i = 0;i<tempArray.length;i++){
            tempArray[i] = arr[i];
        }
    }


    public int[] getArray(){
        //returns an array that has 6 cells with random numbers between 0-9
        int[] arr = new int[6];
        for (int i = 0; i < arr.length; i++){
            int colorIndex = (int) (Math.random() * (10));
            arr[i] = colorIndex;
        }
        return arr;
    }


    public boolean isExist() {
        // it sets the random values with no duplicates in tempArray, then it checks if the tempIndex (the position of the string) is in temp array,
        // so the color string must be in one of the colored buttons.
        getTempArray();
        for (int i = 0; i < tempArray.length; i++) {
            if (tempIndex == tempArray[i]) {
                return true;
            }
        }
        return false;
    }

    public void makeSureExist(){
        // make sure that the color string is in one of the buttons color, by calling the isExist() until it must be exist;
        while (!isExist()){
            isExist();
        }
    }

    public void case1(View view){
        if(view.getId() == R.id.button6 || view.getId() == R.id.button5|| view.getId() == R.id.button4|| view.getId() == R.id.button3||view.getId() == R.id.button2||view.getId() == R.id.button1){
            if (isMuOn) {
                if (serviceConnection != null|| service != null){
                    // Bind to LocalService.
                    startMusic();
                }
                else {
                    mpGame.start();
                }
            }
            if (isVibOn) {
                vibe.vibrate(100);
            }
            if (color.getText().toString().equals("COLOR")){
                score++;
                updateScore();
                loop();
            }
        }
    }

    public void gameOver(){
        //gameMusic.stop();
        //gameOverMusic.start();
        progressBar.setVisibility(View.INVISIBLE);
/*
        Intent intent = new Intent(this, MusicService.class);
        unbindService(serviceConnection);
        stopService(intent);
 */
        if (service != null || serviceConnection != null) {
            stopMusic();
        }

        else{
            mpGame.stop();
        }

        mpOver.start();

        //onDestroy();
        if (score > highestScore){
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            Map<String, Object> user = new HashMap<>();
            user.put("scoreColorGame", score);
            db.collection("users").document(mAuth.getCurrentUser().getUid()).update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(colorsGame.this, "Highest score has been saved", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("TAG", "Error adding document", e);
                }
            });
        }

        color.setText(R.string.gameOver);
        if (isVibOn) {
            vibe.vibrate(500);
        }
        menu.setVisibility(View.VISIBLE);
        restart.setVisibility(View.VISIBLE);
        for (int i = 0; i<btnArr.length; i++){
            btnArr[i].setVisibility(View.INVISIBLE);
        }
    }

    public void updateScore() {
        scoreView.setText("Score: " + score);
    }
}