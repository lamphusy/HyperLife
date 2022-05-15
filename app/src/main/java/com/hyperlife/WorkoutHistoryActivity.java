package com.hyperlife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hyperlife.adapter.WorkoutHistoryRecyclerViewAdapter;
import com.hyperlife.model.ExerciseHistoryItem;

import java.util.ArrayList;

public class WorkoutHistoryActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private static final String tempEmail = "tempEmail";
    private ArrayList<ExerciseHistoryItem> exerciseHistoryItems;
    private WorkoutHistoryRecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private ImageView backbutton;
    private TextView noHistoryLabel;
    private SharedPreferences sharedPreferences;
    private String theTempEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);
        addControls();
        addEvents();
    }

    private void addEvents() {

    }

    private void addControls() {
        firestore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        theTempEmail = sharedPreferences.getString("Email", "");

        recyclerView = findViewById(R.id.history_recycler_view);
        backbutton = findViewById(R.id.button_backtohomefrag_history);
        noHistoryLabel = findViewById(R.id.no_history_label);

        exerciseHistoryItems = new ArrayList<>();
    }
}