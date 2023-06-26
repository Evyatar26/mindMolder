package com.example.mindmolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class registerPage extends AppCompatActivity implements View.OnClickListener {

    EditText u_name, e_mail, p_assword, c_password;
    Button rSubmit_btn;
    String userName, email, password, confirmPassword;
    int scoreColor, simonGame ,isMusicOn , isVibOn = 0;
    ImageView profilePic;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    StorageReference storageRef;
    Bitmap imageBitmap;
    FirebaseStorage storage;
    Bundle saved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // hide the action bar
        setContentView(R.layout.activity_register_page);

        saved = savedInstanceState;

        p_assword = findViewById(R.id.password);
        c_password = findViewById(R.id.confirm_password);
        e_mail = findViewById(R.id.email);
        u_name = findViewById(R.id.userName);

        profilePic = findViewById(R.id.profilePic);
        profilePic.setOnClickListener(this);

        rSubmit_btn = findViewById(R.id.rSubmit_btn);
        rSubmit_btn.setOnClickListener(this);

        imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rpp);

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.profilePic) {
            AlertDialog.Builder a = new AlertDialog.Builder(this)
                    .setMessage("Get photo from")
                    .setPositiveButton("Camera", (dialog, which) -> {
                        Intent picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        if (ContextCompat.checkSelfPermission(this , Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
                        }

                        startActivityForResult(picture, 1);

                    } ).setNegativeButton("Gallery", (dialog, which) -> {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");

                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_DENIED){
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                        }
                        startActivityForResult(photoPickerIntent, 0);
                    });
            a.create().show();
        }

        if (view.getId() == R.id.rSubmit_btn) {
            Toast.makeText(this, "loading...", Toast.LENGTH_SHORT).show();
            if (checkNameInput() && checkMail() && checkPassInput() && checkCPass()) {
                storage = FirebaseStorage.getInstance();
                storageRef = storage.getReference();
                db = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Create a new user with username email and photo
                            Map<String, Object> user = new HashMap<>();
                            user.put("uNameF", userName);
                            user.put("emailF", email);
                            user.put("scoreColorGame", scoreColor);
                            user.put("scoreSimonGame", simonGame);
                            user.put("isMusicOn", isMusicOn);
                            user.put("isVibrationOn", isVibOn);
                            //user.put(String.valueOf(R.string.bitmap_field),bitMapToByteArr(imageBitmap));

                            // Add a new document with a generated ID
                            db.collection("users").document(mAuth.getCurrentUser().getUid())
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //Toast.makeText(registerPage.this, "success!\n,you'll be transformed into the login page in 3 seconds", Toast.LENGTH_SHORT).show();
                                            long startTime = System.currentTimeMillis(); // get current time in milliseconds
                                            StorageReference mountainRef = storageRef.child(mAuth.getCurrentUser().getUid());
                                            UploadTask uploadTask = mountainRef.putBytes(bitMapToByteArr(imageBitmap));
                                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    Log.e("error", exception.getMessage());
                                                }
                                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                                    // ...
                                                    Log.d("success", "");

                                                    Toast.makeText(registerPage.this, "Success! you will be registered in about 3 seconds", Toast.LENGTH_SHORT).show();
                                                    while (System.currentTimeMillis() < startTime + 3000) {
                                                        // wait for 3 seconds
                                                    }

                                                    Intent login = new Intent(registerPage.this, loginPage.class);
                                                    startActivity(login);
                                                }

                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("TAG", "Error adding document", e);
                                                }
                                            });
                                        }
                                    });
                        } else {
                            Toast.makeText(registerPage.this, "we have a problem with our servers now, please try again later", Toast.LENGTH_SHORT).show();
                            Log.e("ERROR", task.getResult().toString());
                        }
                    }
                });
            }
        }
    }


    public boolean checkNameInput() {
        userName = u_name.getText().toString().trim();
        boolean nameHasValidLength = userName.length() >= 2 && userName.length() <= 9;

        if (!nameHasValidLength) {
            u_name.setError("Username must be between 2 to 9 characters");
            userName = "";
            u_name.setText("");
            Toast.makeText(this, "please change your username according to the error on it's right", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean checkMail() {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        email = e_mail.getText().toString().trim();

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null || !pat.matcher(email).matches()) {
            e_mail.setError("The mail you entered isn't correct or you didn't enter a mail, please check and retry");
            email = "";
            e_mail.setText("");
            Toast.makeText(this, "Please change your mail according to the error on it's right", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean checkPassInput() {
        password = p_assword.getText().toString().trim();

        boolean hasChr = password.matches(".*[a-zA-Z]+.*");
        boolean hasNum = password.matches(".*[0-9]+.*");
        boolean passHasValidLength = password.length() >= 6 && password.length() <= 9;

        if (!passHasValidLength) {
            p_assword.setError("Password must be between 6 and 9 characters");
            password = "";
            p_assword.setText("");
            Toast.makeText(this, "please change your password according to the error on it's right", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!hasChr) {
            p_assword.setError("Password must contain at least one letter");
            password = "";
            p_assword.setText("");
            Toast.makeText(this, "please change your password according to the error on it's right", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!hasNum) {
            p_assword.setError("Password must contain at least one number");
            password = "";
            p_assword.setText("");
            Toast.makeText(this, "please change your password according to the error on it's right", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public boolean checkCPass() {
        password = p_assword.getText().toString().trim();
        confirmPassword = c_password.getText().toString().trim();

        if (!(password.equals(confirmPassword))) {
            c_password.setError("Different from original password");
            confirmPassword = "";
            c_password.setText("");
            Toast.makeText(this, "please change the confirm password according to the error on it's right", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public byte[] bitMapToByteArr(Bitmap map) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        map.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                profilePic.setImageBitmap(imageBitmap);
            }

            else if(requestCode == 0){
                Uri image = data.getData();
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image);
                    profilePic.setImageBitmap(imageBitmap);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}