package com.example.mindmolder;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.List;

public class user implements Serializable {
    private Bitmap imageView;
    private String name;
    private double score;
    private int position;
    private String userUid;

    public user(String name, double score,int position ,String userUid) {
        this.imageView = null;
        this.name = name;
        this.score = score;
        this.position = position;
        this.userUid = userUid;
    }
/*
    public Bitmap getImageView() {
        return imageView;
    }
 */

    public String getName() {
        return name;
    }

    public double getScore() {
        return score;
    }

    public void setImageView(Bitmap imageView) {
        this.imageView = imageView;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "user{" +
                "name='" + name + '\'' +
                ", score=" + score +
                ", position=" + position +
                ", userUid='" + userUid + '\'' +
                '}';
    }
}
