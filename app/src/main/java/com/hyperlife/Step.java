package com.hyperlife;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hyperlife.adapter.StepViewPagerAdapter;

import java.time.LocalDate;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Step extends AppCompatActivity {

    ImageView backPress, imgSettingStepGoal;
    TextView txtCountStep, txtLongStep,txtKcalStep, txtDateTimeStep;
    Intent intent;
    StepViewPagerAdapter adapter;
    String userEmail, step_goal;
    Thread thread;

    LocalDate today;
    LocalDate monday;
    FirebaseFirestore firestore;
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        today = LocalDate.now();
        monday = today.with(previousOrSame(MONDAY));

        firestore = FirebaseFirestore.getInstance();
        addControls();
        addEvents();
    }

    private void addEvents() {
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Step.this,MainActivity.class);
                startActivity(intent);
            }
        });

        imgSettingStepGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewDialog = LayoutInflater.from(getApplicationContext())
                  .inflate(R.layout.layout_bottom_sheet_dialog_step_count, null);

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Step.this);

                TextView btnDone = (TextView) viewDialog.findViewById(R.id.close_button_step_goal_set_up);
                TextView recommendTitle = (TextView) viewDialog.findViewById(R.id.recommend_title);
                TextView customTitle = (TextView) viewDialog.findViewById(R.id.custom_title);
                ConstraintLayout tabAnimationView = (ConstraintLayout) viewDialog.findViewById(R.id.tab_animation_view_step_count);

                ViewPager2 viewpage = (ViewPager2) viewDialog
                        .findViewById(R.id.step_count_set_goal_viewPager);
                adapter = new StepViewPagerAdapter(step_goal,userEmail);
                viewpage.setAdapter(adapter);
                viewpage.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        if(position == 0){
                            customTitle.setTextColor(Color.parseColor("#7E7E7E"));
                            recommendTitle.setTextColor(getResources().getColor(R.color.black));
                            tabAnimationView.animate().x(0).setDuration(200);
                        }else{
                            int size = customTitle.getWidth();
                            customTitle.setTextColor(getResources().getColor(R.color.black));
                            recommendTitle.setTextColor(Color.parseColor("#7E7E7E") );
                            tabAnimationView.animate().x(size).setDuration(200);
                        }
                    }
                });
                customTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewpage.setCurrentItem(1);
                    }
                });

                recommendTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewpage.setCurrentItem(0);
                    }
                });

                btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.hide();
                        firestore.collection("users").document(userEmail).update("step_goal",adapter.step_goal_selected + "");
                        docRef = firestore.collection("daily").
                                document("week-of-"+ monday.toString()).
                                collection(today.toString()).document(userEmail);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                String steps = task.getResult().getString("steps");
                                if(steps.equals("empty")){
                                    docRef.update("steps","0");
                                }
                            }
                        });

                    }
                });

                bottomSheetDialog.setContentView(viewDialog);
                bottomSheetDialog.show();

            }
        });
    }



    private void addControls() {
        backPress = findViewById(R.id.button_backtohomefrag);
        txtCountStep = findViewById(R.id.step_count_text);
        txtLongStep = findViewById(R.id.km_step_count_text);
        txtKcalStep = findViewById(R.id.kcal_step_count_text);
        imgSettingStepGoal = findViewById(R.id.more_menu_stepcount);
        txtDateTimeStep = findViewById(R.id.date_time_step);

        intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        step_goal = intent.getStringExtra("step_goal");

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                LocalDate getToday = LocalDate.now();
                txtDateTimeStep.setText(getToday.getDayOfWeek() +", "+ getToday.getMonth()+" "+getToday.getDayOfMonth());
                if("empty".equals(step_goal)){
                    txtCountStep.setText("0");
                    txtKcalStep.setText("0");
                    txtLongStep.setText("0");
                }else{
                    docRef =firestore.collection("daily").
                            document("week-of-" + monday.toString()).
                            collection(today.toString()).
                            document(userEmail);

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            String step_count = "0";
                            step_count = task.getResult().getString("steps");
                            if(!step_count.equals("empty")){
                                txtCountStep.setText(step_count);
                                double to_km = Double.parseDouble(step_count);
                                double to_cal = Double.parseDouble(step_count);

                                to_km = to_km * 0.000762;
                                to_cal = to_cal * 0.0447;

                                float f = (float) to_km;
                                String s = String.format("%.3f", f);

                                txtLongStep.setText(s);
                                txtKcalStep.setText(String.valueOf(to_cal));

                                docRef = firestore.collection("daily").
                                        document("week-of-" + monday.toString()).
                                        collection(today.toString()).
                                        document(userEmail);


                                docRef.update("cal_step", txtLongStep.getText());
                                docRef.update("km_step", txtKcalStep.getText());
                            }else{
                                txtCountStep.setText("Empty");
                                txtKcalStep.setText("0");
                                txtLongStep.setText("0");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Step.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        thread.run();


    }
}