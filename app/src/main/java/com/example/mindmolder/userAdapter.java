package com.example.mindmolder;

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

import java.util.List;

class userAdapter extends ArrayAdapter<user> {
    Context context;
    List<user> list;

    public userAdapter(Context context,int resource,int textViewResourceId, List<user> list) {
        super(context,resource,textViewResourceId,list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater=((Activity)context).getLayoutInflater();
        View body = layoutInflater.inflate(R.layout.body,parent,false);
        //ImageView iv = body.findViewById(R.id.imageView);
        TextView userName = body.findViewById(R.id.userName);
        TextView score = body.findViewById(R.id.score);
        TextView place = body.findViewById(R.id.pos);

        user temp = list.get(position);
        userName.setText(temp.getName());
        score.setText(String.valueOf((int) temp.getScore()));
        place.setText(String.valueOf(temp.getPosition()));

        //iv.setImageResource(R.drawable.rpp);
        //if (temp.getImageView() != null)
        //    iv.setImageBitmap(temp.getImageView());

        return body;
    }

    @Override
    public String toString() {
        return "userAdapter{" +
                "context=" + context +
                ", list=" + list +
                '}';
    }
}
