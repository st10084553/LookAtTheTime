package com.example.lookatthetime;

import static android.view.View.GONE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TimeSheet extends AppCompatActivity {

    private Chronometer chronometer;
    private boolean running;

    private long pauseOffset;
    Button buttonHome;
    Spinner spinner;

    String startTime, stopTime;

    String storedTime;

    int savedTime, totalTime;
    String currentCatID;
    String currentCat;


    Boolean isRunning;
    Button buttonStart;
    Button buttonStop;

    TextView textView, textViewTimer;

    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_sheet);

        buttonHome = findViewById(R.id.home);
        buttonStart = findViewById(R.id.start);
        buttonStop = findViewById(R.id.stop);
        spinner = findViewById(R.id.spinner);
        textView = findViewById(R.id.textView2);
        chronometer = findViewById(R.id.chronometer);

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String t = (h < 10 ? "0"+h: h)+":"+(m < 10 ? "0"+m: m)+":"+ (s < 10 ? "0"+s: s);
                chronometer.setText("Current Time Spent: "+ t);
            }
        });

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setText("Current Time Spent: 00:00:00");

        buttonStop.setBackgroundColor(Color.parseColor("#FF0000"));
       // textViewTimer = findViewById(R.id.textView3);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        ArrayList<String> listOfCat = new ArrayList<>();
        String userEmail = String.valueOf(user.getEmail());


        try{
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

                                    listOfCat.add(categoryName);
                                    Log.d("List", listOfCat.toString());

                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                            TimeSheet.this, android.R.layout.simple_spinner_item, listOfCat);

                                    adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                                    spinner.setAdapter(adapter);

                                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                            String item = adapterView.getItemAtPosition(position).toString();
                                            currentCat = item;

                                            String userEmail = String.valueOf(user.getEmail());

                                            db.collection(userEmail)
                                                    .whereEqualTo("Category Name", item)
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                                    Log.d("Doc", document.getId() + " => " + document.getData());

                                                                    String categoryName = document.getString("Category Name");
                                                                    String description = document.getString("Description");


                                                                    currentCatID = document.getId();

                                                                    String CategoryListText = "Category Name: " +
                                                                            categoryName + "\n" + "Description: "+ description +
                                                                            "\n";
                                                                    textView.setText(CategoryListText);

                                                                    SimpleDateFormat sdf1
                                                                            = new SimpleDateFormat(
                                                                            "dd-MM-yyyy");
                                                                    String currentDate = sdf1.format(new Date());

                                                                    try{
                                                                        db.collection(userEmail).document(currentCatID).collection("Timesheets")
                                                                                .document(currentDate)
                                                                                .get()
                                                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                        savedTime = documentSnapshot.getLong("Total Time Spent (MS) ").intValue();

                                                                                        if(savedTime == 0 || savedTime < 0 ){
                                                                                            long seconds = savedTime / 1000;
                                                                                            long minutes = seconds / 60;
                                                                                            long hours = minutes / 60;
                                                                                            long days = hours / 24;
                                                                                            String time = "\nDays: " + days + "\nHours: " + hours % 24 + "\nMinutes: " +  minutes % 60 + "\nSeconds: " + seconds % 60;
                                                                                            textView.append("Time spent in total: " + time);
                                                                                        }
                                                                                        else{

                                                                                            textView.append("No time spent on this category.");
                                                                                        }

                                                                                    }
                                                                                });
                                                                    }
                                                                    catch (Exception e){

                                                                    }


                                                                }
                                                            } else {



                                                            }
                                                        }
                                                    });
                                        }
                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                }
                            } else {
                                Log.d("Doc", "Error getting documents: ", task.getException());
                            }
                        }
                    });


        }
        catch (Exception e){


        }




        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonStart.setVisibility(GONE);
                buttonStop.setVisibility(View.VISIBLE);


                chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
                chronometer.start();

                buttonStop.setVisibility(View.VISIBLE);

                //get current time
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());

                startTime = currentDateandTime;

                Log.d("currentDateandTime", currentDateandTime);

            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonStart.setVisibility(View.VISIBLE);
                buttonStop.setVisibility(View.INVISIBLE);

                chronometer.stop();
                pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());

                stopTime = currentDateandTime;

                Log.d("stopTime", stopTime);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                auth = FirebaseAuth.getInstance();
                user = auth.getCurrentUser();

                String userEmail = String.valueOf(user.getEmail());

                findDifference(startTime, stopTime, savedTime);

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

                                        listOfCat.add(categoryName);
                                        Log.d("List", listOfCat.toString());

                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                                TimeSheet.this, android.R.layout.simple_spinner_item, listOfCat);

                                        spinner.setAdapter(adapter);

                                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                                String item = adapterView.getItemAtPosition(position).toString();
                                                currentCat = item;

                                                String userEmail = String.valueOf(user.getEmail());

                                                db.collection(userEmail)
                                                        .whereEqualTo("Category Name", item)
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        Log.d("Doc", document.getId() + " => " + document.getData());

                                                                        String categoryName = document.getString("Category Name");
                                                                        String description = document.getString("Description");


                                                                        currentCatID = document.getId();

                                                                        String CategoryListText = "Category Name: " +
                                                                                categoryName + "\n" + "Description: "+ description +
                                                                                "\n";
                                                                        textView.setText(CategoryListText);

                                                                        SimpleDateFormat sdf1
                                                                                = new SimpleDateFormat(
                                                                                "dd-MM-yyyy");
                                                                        String currentDate = sdf1.format(new Date());

                                                                        db.collection(userEmail).document(currentCatID).collection("Timesheets")
                                                                                .document(currentDate)
                                                                                .get()
                                                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                                        savedTime = documentSnapshot.getLong("Total Time Spent (MS) ").intValue();
                                                                                        long seconds = savedTime / 1000;
                                                                                        long minutes = seconds / 60;
                                                                                        long hours = minutes / 60;
                                                                                        long days = hours / 24;
                                                                                        String time = "\nDays: " + days + "\nHours: " + hours % 24 + "\nMinutes: " +  minutes % 60 + "\nSeconds: " + seconds % 60;
                                                                                        textView.append("Time spent in total: " + time);
                                                                                    }
                                                                                });

                                                                    }
                                                                } else {



                                                                }
                                                            }
                                                        });
                                            }
                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });
                                    }
                                } else {
                                    Log.d("Doc", "Error getting documents: ", task.getException());
                                }
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


    public String formatTime (long elapsedTime){
        int hours = (int)(elapsedTime / (1000 * 60 * 60));
        int minutes = (int)((elapsedTime / (1000 * 60)) % 60);
        int seconds = (int)((elapsedTime / 1000) % 60);
        int milliseconds = (int)(elapsedTime % 1000);
                return String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d", hours,
                        minutes, seconds, milliseconds);
    }

    public void findDifference(String start_date,
                               String end_date, int savedTime) {

        String userEmail = String.valueOf(user.getEmail());

        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");

        SimpleDateFormat sdf1
                = new SimpleDateFormat(
                "dd-MM-yyyy");

        try{

            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            String temp = difference_In_Time + "";



            int timeSpent = (int) difference_In_Time;

            timeSpent = timeSpent - 1;

            long seconds = timeSpent / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            String time = days + ":" + hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;

            String currentDateAndTime = sdf.format(new Date());
            String currentDate = sdf1.format(new Date());

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();


            totalTime = savedTime + timeSpent;

            Log.d("totalTime", String.valueOf(totalTime));

            Map<String, Object> timesheet = new HashMap<>();
            timesheet.put("Total Time Spent (MS) ", totalTime);

            db.collection(userEmail)
                    .document(currentCatID)
                    .collection("Timesheets")
                    .document(currentDate)
                    .set(timesheet)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                            }
                        }
                    });

        }
        catch (Exception e){

        }
    }
}