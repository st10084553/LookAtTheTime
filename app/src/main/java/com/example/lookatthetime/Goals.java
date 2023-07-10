package com.example.lookatthetime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Goals extends AppCompatActivity {

    Button buttonHome;
    Button buttonUpdateMin;

    Button buttonUpdateMax;
    TextInputEditText editTarget, editTarget2;


    TextView textView, textView3, textView4;

    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        buttonHome = findViewById(R.id.home);
        buttonUpdateMin = findViewById(R.id.updateMinGoal);
        buttonUpdateMax = findViewById(R.id.updateMaxGoal);
        editTarget = findViewById(R.id.target);
        editTarget2 = findViewById(R.id.target2);
        textView = findViewById(R.id.textView);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userEmail = String.valueOf(user.getEmail());

        Map<String, Object> goal = new HashMap<>();

        goal.put("Min Goal", 0);
        goal.put("Max Goal", 0);

        db.collection(userEmail).document("Goal").update(goal).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(Goals.this, "Goals Loaded", Toast.LENGTH_SHORT).show();
            }
        });

        db.collection(userEmail).document("Goal").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                int minGoal = documentSnapshot.getLong("Min Goal").intValue();
                textView3.setText("Minimum Daily Work Hours Goal: " + minGoal);
            }
        });

        db.collection(userEmail).document("Goal").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                int maxGoal = documentSnapshot.getLong("Max Goal").intValue();
                textView4.setText("Maximum Daily Work Hours Goal: " + maxGoal);
            }
        });


        buttonUpdateMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userInput = editTarget.getText().toString();
                boolean validInput = isNumeric(userInput);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> goal = new HashMap<>();

                if(validInput == true)
                {
                    int goalNum = Integer.parseInt(userInput);
                    goal.put("Min Goal", goalNum);

                    db.collection(userEmail).document("Goal").update(goal).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            textView3.setText("Minimum Daily Work Hours Goal: " + goalNum);
                            Toast.makeText(Goals.this, "Goal Updated", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
                else
                {
                    Toast.makeText(Goals.this, "Please enter a valid number between 1 and 10.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonUpdateMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userInput = editTarget2.getText().toString();
                boolean validInput = isNumeric(userInput);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> goal = new HashMap<>();

                if(validInput == true)
                {
                    int goalNum = Integer.parseInt(userInput);
                    goal.put("Max Goal", goalNum);

                    db.collection(userEmail).document("Goal").update(goal).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            textView4.setText("Maximum Daily Work Hours Goal: " + goalNum);
                            Toast.makeText(Goals.this, "Goal Updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(Goals.this, "Please enter a valid number between 1 and 10.", Toast.LENGTH_SHORT).show();
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



    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {

            double d = Double.parseDouble(strNum);

            if(d > 10 || d < 1){

                return false;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}