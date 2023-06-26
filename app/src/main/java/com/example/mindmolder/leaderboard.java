package com.example.mindmolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class leaderboard extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    Button retToMenu;
    TextView textView;
    ListView listView;
    ArrayList<user> users;
    userAdapter adapter;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser u;
    FirebaseStorage firebaseStorage;
    ProgressBar progressBar;
    ArrayList<user> usersNoP;
    CollectionReference usersRef;
    int pos;
    int currentPos;
    public static boolean first = true;
    public static boolean firstU = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        getSupportActionBar().hide(); // hide the action bar

        retToMenu = findViewById(R.id.retToMenu);
        retToMenu.setOnClickListener(this);

        textView = findViewById(R.id.yourP);

        listView = findViewById(R.id.main_list);
        listView.setOnItemClickListener(this);

        pos = 1;

        mAuth = FirebaseAuth.getInstance();
        u = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        usersRef = db.collection("users");
        usersRef.orderBy("scoreColorGame",Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for (QueryDocumentSnapshot document : querySnapshot){
                    user item = new user(document.getString("uNameF"),document.getDouble("scoreColorGame"),pos,document.getId());
                    if (item.getUserUid().equals(u.getUid())){
                        currentPos = pos;
                    }
                    pos++;
                    Log.d("USER",item.toString());
                    users.add(item);
                }
                textView.setText("Your place is: " + currentPos);
                adapter = new userAdapter(leaderboard.this,0,0,users);
                Log.e("INFO", String.valueOf(adapter.list.size()));
                listView.setAdapter(adapter);
                //new Thread(thread).start();
                Log.e("INFO", listView.getAdapter().toString());
                // Handle the query results here
                /*
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    // Access the user data
                    String username = document.getString("username");
                    int score = document.getLong("scoreColorGame").intValue();
                    // Process the user data as needed
                    System.out.println("Username: " + username + ", Score: " + score);
                 */
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors
                Log.e("TAG", "Error getting users: " + e.getMessage());
            }
        });


        progressBar = findViewById(R.id.progressBar);
        //downloadThread thread = new downloadThread(this);
        db = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        Log.e("ERROR", "1");
        users = new ArrayList<>();
        usersNoP = new ArrayList<>();
        if(first){
            Toast.makeText(leaderboard.this, "loading...", Toast.LENGTH_SHORT).show();
            first = false;
        }
        //db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        //  @Override
        //public void  onComplete(@NonNull Task<QuerySnapshot> task) {
                /*
                StorageReference pathReference = firebaseStorage.getReference();
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        user item = new user(document.getString("uNameF"),document.getDouble("scoreColorGame"), document.getId());
                        Log.d("USER",item.toString());
                        // Create a reference with an initial file path and name
                        pathReference.child(document.getId()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                //Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                //item.setImageView(bitmap);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("ERROR", e.getMessage());
                                //usersNoP.add(item);
                            }
                        });
                        users.add(item);
                    }


                    adapter = new userAdapter(leaderboard.this,0,0,users);
                    Log.e("INFO", String.valueOf(adapter.list.size()));
                    listView.setAdapter(adapter);
                    //new Thread(thread).start();
                    Log.e("INFO", listView.getAdapter().toString());
                }
                else {
                    Log.d("TAG", "Error getting documents.", task.getException());
                }

                 */
    }
    // });

    //}

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.retToMenu){
            finish();
            Intent menu = new Intent(this, nextPage.class);
            startActivity(menu);
        }
    }

    public void getUsers() {
        Log.e("ERROR", "2");
        Log.e("ERROR", "3");
        Log.e("ERROR", "7");


        for (user u : users) {
            // Create a reference with an initial file path and name
            StorageReference pathReference = firebaseStorage.getReference();
            pathReference.child(u.getUserUid()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    //u.setImageView(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("ERROR", e.getMessage());
                }
            });
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this,userInfo.class);
        finish();
        intent.putExtra("user",(user) listView.getAdapter().getItem(i));
        startActivity(intent);
    }
}