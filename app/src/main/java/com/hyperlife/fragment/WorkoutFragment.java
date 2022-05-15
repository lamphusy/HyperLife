package com.hyperlife.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hyperlife.R;
import com.hyperlife.WorkoutDataActivity;
import com.hyperlife.WorkoutHistoryActivity;
import com.hyperlife.adapter.GymListViewAdapter;
import com.hyperlife.model.NonScrollListView;

import org.w3c.dom.Text;

public class WorkoutFragment extends Fragment {

    private FloatingActionButton viewButtonExercises;
    private TextView workoutHeadline, workoutTitle,workoutTime,workoutRecommended,viewAll;
    private Boolean gotPlan = false;
    private ImageView clockIcon;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private static final String tempEmail = "tempEmail";
    private String workoutLabel,workoutDuration,workoutImage;
    private LinearLayout timeLinear;
    private NonScrollListView listView,listView_intermediate,listView_advanced;
    private String[] gymListTitle = {"Abs - Beginner",
            "Chest - Beginner",
            "Arm - Beginner",
            "Leg - Beginner",
            "Shoulder & Back - Beginner"};
    private String[] gymListTitleIntermediate = {"Abs - Intermediate",
            "Chest - Intermediate",
            "Arm - Intermediate",
            "Leg - Intermediate",
            "Shoulder & Back - Intermediate"};
    private String[] gymListTitleAdvanced = {"Abs - Advanced",
            "Chest - Advanced",
            "Arm - Advanced",
            "Leg - Advanced",
            "Shoulder & Back - Advanced"};
    private String[] gymListTime = {"15 min", "6 min", "16 min", "21 min", "14 min"};
    private String[] focusBodyPart = {"Abs","Chest","Arm","Leg","Shoulder & Back"};
    private int[] gymListImage = {R.drawable.abs_workout_image,
            R.drawable.chest_workout_image,
            R.drawable.arm_workout_image,
            R.drawable.leg_workout_image,
            R.drawable.shoulder_workout_image};
    public WorkoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workout, container, false);
        viewButtonExercises = (FloatingActionButton) view.findViewById(R.id.fab_exercises);
        workoutHeadline = (TextView) view.findViewById(R.id.workout_headline);
        workoutTitle = (TextView) view.findViewById(R.id.workout_title);
        clockIcon = (ImageView) view.findViewById(R.id.clockIcon);
        workoutTime = (TextView) view.findViewById(R.id.workout_time);
        timeLinear =(LinearLayout) view.findViewById(R.id.time_linear);
        workoutRecommended =(TextView) view.findViewById(R.id.workout_recommended);
        viewAll =(TextView) view.findViewById(R.id.view_all_button);
        SharedPreferences sharedPreferences = this.getActivity().
                getSharedPreferences(tempEmail, MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");
        firestore = FirebaseFirestore.getInstance();
        Runnable workoutRunnable = new Runnable() {
            @Override
            public void run() {
                docRef = firestore.collection("workoutHistory").document(theTempEmail);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                workoutLabel = document.getString("workoutTitle");
                                if(workoutLabel != null){
                                    workoutDuration = document.getString("workoutDuration");
                                    workoutImage = document.getString("workoutImage");
                                    workoutTitle.setText(workoutLabel);
                                    workoutTime.setText(workoutDuration);

                                    workoutHeadline.setText("Recent Workout");
                                    timeLinear.setVisibility(View.VISIBLE);
                                    workoutRecommended.setVisibility(View.INVISIBLE);
                                    viewButtonExercises.setVisibility(View.VISIBLE);

                                    viewButtonExercises.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getContext(), WorkoutDataActivity.class);
                                            intent.putExtra("workoutTitle", workoutLabel);
                                            intent.putExtra("workoutImage", Integer.parseInt(workoutImage));
                                            intent.putExtra("workoutTime",workoutDuration);

                                            String[] splitString = workoutLabel.split("-");
                                            intent.putExtra("focusBodyPart",splitString[0].trim());

                                            startActivity(intent);
                                            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold_position);
                                        }
                                    });
                                } else {
                                    workoutHeadline.setText("No Recent Workout");
                                    workoutTitle.setText("Let's workout and keep fit!");
                                    viewButtonExercises.setVisibility(View.INVISIBLE);
                                    timeLinear.setVisibility(View.GONE);
                                    workoutRecommended.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
            }
        };

        Thread workoutThread = new Thread(workoutRunnable);
        workoutThread.start();


        listView = (NonScrollListView) view.findViewById(R.id.classic_workout_listview);
        GymListViewAdapter listViewAdapter = new GymListViewAdapter(getActivity(),
                R.layout.item_list_workout,
                gymListImage,
                gymListTitle,
                gymListTime);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), WorkoutDataActivity.class);
                intent.putExtra("workoutTitle", gymListTitle[position]);
                intent.putExtra("workoutImage", gymListImage[position]);
                intent.putExtra("workoutTime",gymListTime[position]);
                intent.putExtra("focusBodyPart",focusBodyPart[position]);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold_position);
            }
        });

        listView_intermediate = (NonScrollListView) view.findViewById(R.id.classic_workout_listview_intermediate);
        GymListViewAdapter listViewAdapter_intermediate = new GymListViewAdapter(getActivity(),
                R.layout.item_list_workout,
                gymListImage,
                gymListTitleIntermediate,
                gymListTime);
        listView_intermediate.setAdapter(listViewAdapter_intermediate);
        listView_intermediate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), WorkoutDataActivity.class);
                intent.putExtra("workoutTitle", gymListTitleIntermediate[position]);
                intent.putExtra("workoutImage", gymListImage[position]);
                intent.putExtra("workoutTime",gymListTime[position]);
                intent.putExtra("focusBodyPart",focusBodyPart[position]);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold_position);
            }
        });

        listView_advanced = (NonScrollListView) view.findViewById(R.id.classic_workout_listview_advanced);
        GymListViewAdapter listViewAdapter_advanced = new GymListViewAdapter(getActivity(),
                R.layout.item_list_workout,
                gymListImage,
                gymListTitleAdvanced,
                gymListTime);
        listView_advanced.setAdapter(listViewAdapter_advanced);
        listView_advanced.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), WorkoutDataActivity.class);
                intent.putExtra("workoutTitle", gymListTitleAdvanced[position]);
                intent.putExtra("workoutImage", gymListImage[position]);
                intent.putExtra("workoutTime",gymListTime[position]);
                intent.putExtra("focusBodyPart",focusBodyPart[position]);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold_position);
            }
        });


        clockIcon.setColorFilter(Color.WHITE);
        viewButtonExercises.setColorFilter(getResources().getColor(R.color.lime_200));

        viewButtonExercises.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotPlan = true;
            }
        });

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toHistory = new Intent(getContext(), WorkoutHistoryActivity.class);
                startActivity(toHistory);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold_position);
            }
        });

        return view;

    }
}