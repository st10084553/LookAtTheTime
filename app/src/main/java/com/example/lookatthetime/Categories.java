package com.example.lookatthetime;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Categories extends AppCompatActivity {

    Button buttonHome;
    Button buttonAddCategory;

    TextView textView;

    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

       buttonHome = findViewById(R.id.home);
       buttonAddCategory = findViewById(R.id.addcategory);
       textView = findViewById(R.id.textView1);
       textView.append("Your Categories\n\n\n");
       auth = FirebaseAuth.getInstance();
       user = auth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userEmail = String.valueOf(user.getEmail());

        db.collection(userEmail)
                .whereEqualTo("User", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Doc", document.getId() + " => " + document.getData());

                                String categoryName = document.getString("Category Name");
                                String description = document.getString("Description");
                                int timeSpent = document.getLong("TimeSpent").intValue();
                                int sessions = document.getLong("Sessions").intValue();


                                String CategoryListText = "Category Name: " +
                                        categoryName + "\n" + "Description: "+ description +
                                        "\n" + "Sessions: "+ sessions + "\n" + "Time Spent:" +
                                       timeSpent + "\n\n";

                                textView.append(CategoryListText);

                                Log.d("TextViewText", textView.getText().toString());
                            }
                        } else {
                            Log.d("Doc", "Error getting documents: ", task.getException());
                        }
                    }
                });


       buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

       buttonAddCategory.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getApplicationContext(), AddCategory.class);
               startActivity(intent);
               finish();
           }
       });


    }


}