package com.example.lookatthetime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddCategory extends AppCompatActivity {

    TextInputEditText editTextName, editTextLocation, editTextDescription, editTextNotes;
    Button submitCategory;
    Button buttonHome;

    String currentCatID;
    String catID;

    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        submitCategory = findViewById(R.id.submitCategory);
        buttonHome = findViewById(R.id.home);

        editTextName = findViewById(R.id.name);
        editTextLocation = findViewById(R.id.location);
        editTextDescription = findViewById(R.id.description);
        editTextNotes = findViewById(R.id.notes);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        submitCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> category = new HashMap<>();

                category.put("Category Name", String.valueOf(editTextName.getText()));
                category.put("Location", String.valueOf(editTextLocation.getText()));
                category.put("Description", String.valueOf(editTextDescription.getText()));
                category.put("Notes", String.valueOf(editTextNotes.getText()));
                category.put("TimeSpent", 0);
                category.put("Sessions", 0);
                category.put("User", String.valueOf(user.getEmail()));

                String userEmail = String.valueOf(user.getEmail());

                db.collection(userEmail).add(category).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String temp = documentReference.getId();
                        currentCatID = temp;

                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");

                        String currentDate = sdf1.format(new Date());

                        Map<String, Object> timesheet = new HashMap<>();
                        timesheet.put("Total Time Spent (MS) ", 0);

                        db.collection(userEmail)
                                .document(currentCatID)
                                .collection("Timesheets")
                                .document(currentDate)
                                .set(timesheet)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AddCategory.this, "Pogg", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), Categories.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


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
    }


}