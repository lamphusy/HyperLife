package com.hyperlife;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hyperlife.model.ProgressBarAnimation;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements SensorEventListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ProgressBar progressBarStep;
    SensorManager sensorManager;
    Sensor mStepCounter, mStepDetector;
    private boolean isCounterSensorPresent, isSetectorSensorPresent;

    private LinearLayout cardWater, cardStep, cardCalo,
            cardSleep, cardTraining, cardTime;

    private ProgressBar progressBar;
    private ConstraintLayout setupStepGoal, setupWaterGoal;
    private String userEmail, theTempEmail;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;

    private String step_goal, drink_goal, sleepTime, stepGoal;
    private TextView statusOfProgressBar, txtNumOfWater, txtSleepTime, txtTimeOnScreen, txtCalo;
    private FirebaseAuth mAuth;
    private static final String tempEmail = "tempEmail";
    private TextView userName, txtNumOfExercises;


    private static final String TEXT_NUM_STEPS = "";
    private int numStepsHomeFrag;
    private TextView txtStepCount, txtGreeting;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public HomeFragment() {
        // Required empty public constructor
    }
    public HomeFragment(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(HomeFragment.this,mStepCounter,SensorManager.SENSOR_DELAY_FASTEST);
            isCounterSensorPresent = true;
        }else{
            isCounterSensorPresent = false;
        }

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null){
            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(HomeFragment.this,mStepDetector,SensorManager.SENSOR_DELAY_FASTEST);
            isSetectorSensorPresent = true;
        }else{
            isSetectorSensorPresent = false;
        }



        //add control
        txtStepCount = (TextView) view.findViewById(R.id.home_step_count);
        cardWater = (LinearLayout) view.findViewById(R.id.water_card_view_linear);
        cardStep = (LinearLayout) view.findViewById(R.id.step_count_cardview_linear);
        cardCalo = (LinearLayout) view.findViewById(R.id.calo_card_view_linear);
        cardSleep = (LinearLayout) view.findViewById(R.id.sleep_card_view_linear);
        cardTraining = (LinearLayout) view.findViewById(R.id.training_card_view_linear);
        cardTime = (LinearLayout) view.findViewById(R.id.time_on_screen_card_view_linear);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_homefrag);
        setupStepGoal = (ConstraintLayout) view.findViewById(R.id.setup_steps_constraint);
        statusOfProgressBar = (TextView) view.findViewById(R.id.status_of_progressbar_homefrag);
        setupWaterGoal = (ConstraintLayout) view.findViewById(R.id.setup_water_constraint);
        txtNumOfWater = (TextView) view.findViewById(R.id.num_of_water);
        txtSleepTime = (TextView) view.findViewById(R.id.sleep_time_text_view);
        txtTimeOnScreen = (TextView) view.findViewById(R.id.time_on_screen_text_view);
        txtCalo = (TextView) view.findViewById(R.id.calo_text_view);
        userName = (TextView) view.findViewById(R.id.username_text_view);
        txtNumOfExercises = (TextView) view.findViewById(R.id.num_of_exercise_home_frag);
        txtGreeting = (TextView) view.findViewById(R.id.txtGreeting);

        cardWater.setClickable(false);
        cardStep.setClickable(false);
        cardSleep.setClickable(false);
        cardCalo.setClickable(false);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(tempEmail, Context.MODE_PRIVATE);
        theTempEmail = sharedPreferences.getString("Email","");


        //thread chay ngam cap nhat du lieu
        Thread backgroundThread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try{
                    Calendar cal = Calendar.getInstance();
                    int hoursOfDay = cal.get(Calendar.HOUR_OF_DAY);

                    if(hoursOfDay >= 0 && hoursOfDay < 12){
                        txtGreeting.setText("Good morning");
                    }else if(hoursOfDay >=12 && hoursOfDay< 18){
                        txtGreeting.setText("Good afternoon");
                    }else if(hoursOfDay >=18 && hoursOfDay< 21){
                        txtGreeting.setText("Good evening");
                    }else if(hoursOfDay >=21 && hoursOfDay< 24){
                        txtGreeting.setText("Good night");
                    }
                    UpdateFirebase(theTempEmail);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        backgroundThread.start();

        //update step step step chuyện quna trọng phải nói 3 lần
        Thread StepThread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                LocalDate today = LocalDate.now();
                LocalDate monday = today.with(previousOrSame(MONDAY));

                firestore = FirebaseFirestore.getInstance();
                docRef = firestore.collection("daily").
                        document("week-of-" + monday.toString()).
                        collection(today.toString()).
                        document(theTempEmail);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                String temp = task.getResult().getString("steps");
                                if (temp != null) {
                                    if (!"empty".equals(temp)) {
                                        txtStepCount.setText(String.valueOf(temp));
                                        numStepsHomeFrag = Integer.parseInt(temp);
                                    }
                                }
                            } else {
                                Log.d("LOGGER", "No such document");
                            }
                        } else {
                            Log.d("LOGGER", "get failed with ", task.getException());
                        }
                    }
                });
            }
        });
        StepThread.start();

        cardWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(),WaterActivity.class);
//                intent.putExtra("userEmail",userEmail);
//                startActivity(intent);
            }
        });

        cardStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(),StepCountActivity.class);
//                intent.putExtra("userEmail",userEmail);
//                intent.putExtra("step_goal",step_goal);
//                startActivity(intent);
            }
        });

        cardCalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).handleChangeFragment(R.id.nav_meal);
                ((MainActivity)getActivity()).btmNav.setSelectedItemId(R.id.nav_meal);
            }
        });

        cardTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).handleChangeFragment(R.id.nav_workout);
                ((MainActivity)getActivity()).btmNav.setSelectedItemId(R.id.nav_workout);
            }
        });

        cardTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getActivity(), UsageStatisticActivity.class);
//                intent.putExtra("userEmail",userEmail);
//                startActivity(intent);
            }
        });

        cardSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(),SleepTimeActivity.class);
//                intent.putExtra("sleepTime",sleepTime);
//                startActivity(sleepTime);
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void UpdateFirebase(String email) {
            SharedPreferences sharedPreferences = this.getActivity().
                    getSharedPreferences(tempEmail,Context.MODE_PRIVATE);
            LocalDate today = LocalDate.now();
            LocalDate monday = today.with(previousOrSame(MONDAY));

            firestore = FirebaseFirestore.getInstance();
            firestore.collection("daily").
                document("week-of-" + monday.toString()).
                collection(today.toString()).
                document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if(documentSnapshot != null){
                            if(documentSnapshot.exists() == true){

                                SharedPreferences.Editor editor;
                                editor = sharedPreferences.edit();
                                editor.putString("Email",email);
                                editor.apply();

                                int numOfExercise = Integer.parseInt(documentSnapshot.getString("num_of_exercise"));
                                if(numOfExercise == 0){
                                    txtNumOfExercises.setText("No workout");
                                }else if(numOfExercise == 1){
                                    txtNumOfExercises.setText(numOfExercise+ " workout");
                                }else{
                                    txtNumOfExercises.setText(numOfExercise+ " workouts");
                                }

                                SetupStepCard(email);
                                SetupWaterCard(email);
                                SetupSleepCard(email);
                                SetupTimeCard(email);
                                SetupCaloCard(email);
                            }
                            else{

                            docRef = firestore.collection("users")
                                    .document(email);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot document = task.getResult();
                                        if(document != null){
                                            String water = document.getString("drink_goal");
                                            String time = document.getString("sleep_goal");
                                            String weight = document.getString("weight");
                                            String height = document.getString("height");
                                            String step = document.getString("step_goal");
                                            String uName = document.getString("name");

                                            userName.setText(uName);
                                            txtNumOfExercises.setText("No workout");

                                            docRef = firestore.collection("daily").
                                                    document("week-of-"+ monday.toString()).
                                                    collection(today.toString()).document(email);
                                            Map<String,Object> Value = new HashMap<>();

                                            if(water.equals("empty")){
                                                Value.put("drink","empty");
                                            }else{
                                                Value.put("drink","0");
                                            }

                                            if(time.equals("empty")){
                                                Value.put("sleep_time","empty");
                                            }else{
                                                Value.put("sleep_time","0");
                                            }

                                            if (weight.equals("empty")) {
                                                Value.put("weight", "empty");
                                            } else {
                                                Value.put("weight", weight);
                                            }

                                            if (height.equals("empty")) {
                                                Value.put("height", "empty");
                                            } else {
                                                Value.put("height", height);
                                            }

                                            if (step.equals("empty")) {
                                                Value.put("steps", "empty");
                                            } else {
                                                Value.put("steps", step);
                                            }

                                            Value.put("time_on_screen", "0");
                                            Value.put("cal_step","0");
                                            Value.put("km_step","0");

                                            Value.put("diet", "0");

                                            Value.put("num_of_exercise", "0");
                                            Value.put("kcal_workout","0");
                                            Value.put("userEmail", email);
                                            Value.put("datetime",today.toString());

                                            firestore = FirebaseFirestore.getInstance();
                                            firestore.collection("daily")
                                                    .document("week-of-"+ monday.toString())
                                                    .collection(today.toString())
                                                    .document(email)
                                                    .set(Value);

                                            SharedPreferences.Editor editor;
                                            editor = sharedPreferences.edit();
                                            editor.putString("Email",email);
                                            editor.apply();
                                            SetupStepCard(email);
                                            SetupWaterCard(email);
                                            SetupCaloCard(email);
                                        } else {
                                            Log.d("LOGGER", "No such document");

                                        }
                                    } else {
                                        Log.d("LOGGER", "get failed with ", task.getException());
                                    }
                                }
                            });
                        }}
                    }
                }
            });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetupCaloCard(String userEmail) {

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));
        docRef = firestore.collection("daily")
                .document("week-of-"+ monday.toString())
                .collection(today.toString())
                .document(userEmail);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        String calo = document.getString("diet");
                        if(!"0".equals(calo)){
                            float calories = Float.parseFloat(calo);
                            txtCalo.setText(calories + "");
                        }
                    }else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetupTimeCard(String userEmail) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));
        docRef = firestore.collection("daily")
                .document("week-of-"+monday.toString())
                .collection(today.toString())
                .document(userEmail);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        String temp = document.getString("time_on_screen");
                        if(!"0".equals(temp)){
                            String[] splitHourOnScreen = temp.split(" ");
                            txtTimeOnScreen.setText(splitHourOnScreen[0]);
                        }
                    }else{
                        Log.d("LOGGER","No such document");
                    }
                }else{
                    Log.d("LOGGER","get failed with ", task.getException());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetupSleepCard(String userEmail) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));
        docRef = firestore.collection("daily").
                document("week-of-" + monday.toString()).
                collection(today.toString()).
                document(userEmail);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String temp = document.getString("sleep_time");
                        if (!"empty".equals(temp)) {
                            String[] splitString = temp.split(":");
                            txtSleepTime.setText(splitString[0] + "h");
                            sleepTime = temp;
                            cardSleep.setClickable(true);
                        }
                    } else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetupWaterCard(String userEmail) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(previousOrSame(MONDAY));
        docRef = firestore.collection("daily").
                document("week-of-" + monday.toString()).
                collection(today.toString()).
                document(theTempEmail);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document!= null){
                        String temp = document.getString("drink");
                        if(!"empty".equals(temp) && temp != null){
                            float waterHadDrink = Float.parseFloat(temp);
                            if(!temp.equals("0")){
                                waterHadDrink = Float.parseFloat(temp)/1000;
                            }
                            txtNumOfWater.setText(waterHadDrink + "");
                            cardWater.setClickable(true);
                        }else {
                            Log.d("LOGGER", "No such document");
                        }
                    } else {
                        Log.d("LOGGER", "get failed with ", task.getException());
                    }
                }
            }
        });
    }

    private void SetupStepCard(String userEmail) {
        firestore = FirebaseFirestore.getInstance();
        docRef = firestore.collection("users")
                    .document(userEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        String uName = document.getString("name");
                        userName.setText(uName);

                        step_goal = document.getString("step_goal");

                        if("empty".equals(step_goal)){
                            statusOfProgressBar.setText("steps");
                            setupStepGoal.setVisibility(View.VISIBLE);
                        }else{
                            setupStepGoal.setVisibility(View.GONE);
                            statusOfProgressBar.setText("/" + step_goal);
                            txtStepCount.setText(TEXT_NUM_STEPS+ numStepsHomeFrag);

                            String tempStepGoal = statusOfProgressBar.getText().
                                    toString().substring(1);
                            progressBar.setMax(Integer.parseInt(tempStepGoal));

                            ProgressBarAnimation progressBarAnimation = new ProgressBarAnimation(progressBar,
                                    numStepsHomeFrag-1,numStepsHomeFrag);
                            progressBarAnimation.setDuration(100);
                            progressBar.startAnimation(progressBarAnimation);
                        }

                        drink_goal = document.getString("drink_goal");
                        if("empty".equals(drink_goal)){
                            setupWaterGoal.setVisibility(View.VISIBLE);
                        }else{
                            setupWaterGoal.setVisibility(View.GONE);
                        }

                        cardStep.setClickable(true);
                    }else {
                        Log.d("LOGGER", "No such document");
                    }
                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (!statusOfProgressBar.getText().equals("steps")) {
            numStepsHomeFrag += (int)sensorEvent.values[0];
            txtStepCount.setText(TEXT_NUM_STEPS + numStepsHomeFrag);
            String tempStepGoal = statusOfProgressBar.getText().toString().substring(1);
            progressBar.setMax(Integer.parseInt(tempStepGoal));
            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar,
                    numStepsHomeFrag - 1,
                    numStepsHomeFrag);
            anim.setDuration(100);
            progressBar.startAnimation(anim);

            Runnable stepCountRunnable = new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    LocalDate today = LocalDate.now();
                    LocalDate monday = today.with(previousOrSame(MONDAY));
                    if (userEmail == null) {
                        docRef = firestore.collection("users").document(theTempEmail);
                    } else {
                        docRef = firestore.collection("users").document(userEmail);
                    }
                    docRef = firestore.collection("daily").

                            document("week-of-" + monday.toString()).

                            collection(today.toString()).

                            document(theTempEmail);
                    docRef.update("steps", String.valueOf(numStepsHomeFrag));

                }
            };
            Thread backgroundThread = new Thread(stepCountRunnable);
            backgroundThread.start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}