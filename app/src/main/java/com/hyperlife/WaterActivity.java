package com.hyperlife;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.hyperlife.model.AlarmReceiver;
import com.hyperlife.model.ProgressBarAnimation;
import com.hyperlife.model.ReminderWaterReceiver;
import com.john.waveview.WaveView;

import java.time.LocalDate;

public class WaterActivity extends AppCompatActivity {
    private final int MAX_OF_CUP = 12;
    private ImageView imgBackHome;
    private ImageView imgOption;
    private FirebaseFirestore firestore;
    private TextView txtDateToday,
            txtWaterDrank, txtWaterNeed,
            txtRemainCup;
    private AppCompatButton btnDrink;
    private WaveView wvDrink;
    private String userEmail;
    private int numOfCup = 8;
    private double waterDrank=0, waterNeed = 0;
    private int cupNeed = 0, cupGoal=0;
    private DocumentReference docRef, docRef2;
    private CardView cvReminderDrink;
    private SwitchCompat swReminderDrink;

    private LocalDate monday;
    private LocalDate today;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        userEmail = getIntent().getStringExtra("userEmail");

        firestore= FirebaseFirestore.getInstance();

        imgBackHome = (ImageView) findViewById(R.id.button_backtohomefrag);
        imgOption = (ImageView) findViewById(R.id.more_menu_waterfrag);
        txtDateToday = (TextView) findViewById(R.id.date_time_water);
        txtWaterDrank = (TextView) findViewById(R.id.water_count_text);
        txtWaterNeed = (TextView) findViewById(R.id.water_daily_goal_text);
        txtRemainCup = (TextView) findViewById(R.id.water_cup_count_text);
        wvDrink = (WaveView)findViewById(R.id.wave_view_water);
        btnDrink = (AppCompatButton) findViewById(R.id.drink_water_button);
        cvReminderDrink = (CardView) findViewById(R.id.card_view_reminder_drink);
        swReminderDrink = (SwitchCompat) findViewById(R.id.switch_reminder_drink);


        today = LocalDate.now();
        monday = today.with(previousOrSame(MONDAY));

        docRef = firestore.collection("daily").
                document("week-of-" + monday.toString()).
                collection(today.toString()).
                document(userEmail);
        docRef2 = firestore.collection("users")
                .document(userEmail);
        backGroundThread();
        loadData();
        addEvents();
    }

    private void backGroundThread() {
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                LocalDate getToday = LocalDate.now();
                txtDateToday.setText(getToday.getDayOfWeek() +", "+ getToday.getMonth()+" "+getToday.getDayOfMonth());
            }
        });
        thread.start();
    }

    private void loadData() {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot != null){
                        if(documentSnapshot.exists()){
                            String isReminder = documentSnapshot.getString("reminder_water");
                            if(isReminder.equals("false")){
                                swReminderDrink.setChecked(false);
                            }else{
                                swReminderDrink.setChecked(true);
                            }
                        }
                    }
                }
            }
        });
        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot != null){
                        if(documentSnapshot.exists()){
                            if(documentSnapshot.getString("drink_goal").equals("empty")){
                                txtRemainCup.setText("Empty");
                                txtWaterNeed.setText("");
                                txtWaterDrank.setText("Empty");
                                wvDrink.setProgress(0);
                                cvReminderDrink.setVisibility(View.GONE);
                            }else{
                                waterNeed =Double.parseDouble(documentSnapshot.getString("drink_goal"));
                                txtWaterNeed.setText(
                                        "of "+  waterNeed/1000 +"L goal" );
                                cupGoal = (int)(waterNeed/250);
                                docRef.get().addOnCompleteListener (new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if(documentSnapshot != null){
                                                if(documentSnapshot.exists()){
                                                    waterDrank = Double.parseDouble(documentSnapshot.getString("drink"));
                                                    txtWaterDrank.setText((int)waterDrank + "");
                                                    cupNeed = (int)(waterNeed - waterDrank) / 250;
                                                    wvDrink.setProgress((int)((waterDrank / waterNeed)* 100));
                                                    txtRemainCup.setText(cupNeed+"");
                                                    cvReminderDrink.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }
                                    }
                                });
                            }

                        }
                    }
                }
            }
        });
    }

    private void addEvents() {
        swReminderDrink.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        CharSequence name = "Hyperlife";
                        String description = "Remind drink water";
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel channel = new NotificationChannel("notify_drink",
                                name,importance);

                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                        notificationManager.createNotificationChannel(channel);
                    }

                    Intent intent = new Intent(WaterActivity.this, ReminderWaterReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(WaterActivity.this, 1, intent, PendingIntent.FLAG_MUTABLE);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


                    long timeAtButtonClick = System.currentTimeMillis();
                    int interval = 4 * 60 * 60 * 1000;
//                    if(timeAtButtonClick < timeCurrent){
//                        timeAtButtonClick += 1000* 60 * 60 * 24;
//                    }

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,timeAtButtonClick,interval,pendingIntent);

                    docRef.update("reminder_water","true").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
//                            Toast.makeText(WaterActivity.this, "Turn on reminder mode", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    Intent intent = new Intent(WaterActivity.this, ReminderWaterReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(WaterActivity.this, 1, intent, PendingIntent.FLAG_MUTABLE);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    alarmManager.cancel(pendingIntent);

                    docRef.update("reminder_water","false").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
//                            Toast.makeText(WaterActivity.this, "Turn off reminder mode", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        btnDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(waterDrank < waterNeed){
                    waterDrank += 250;
                    cupNeed -=1;
                    txtWaterDrank.setText((int)waterDrank+"");
                    txtRemainCup.setText(cupNeed+"");
                    wvDrink.setProgress((int)((waterDrank / waterNeed)* 100));

                    docRef.update("drink",waterDrank+"");
                }
            }
        });

        imgBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivity = new Intent(WaterActivity.this,MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        });

        imgOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewDialog = LayoutInflater.from(getApplicationContext())
                                    .inflate(R.layout.bottom_sheet_dialog_waterfrag,null);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(WaterActivity.this);

                ProgressBar progressBar = (ProgressBar) viewDialog.findViewById(R.id.progressbar_water);
                ImageView img_buttonPlus = (ImageView) viewDialog.findViewById(R.id.plus_button_water_menu);
                ImageView img_buttonMinus = (ImageView) viewDialog.findViewById(R.id.minus_button_water_menu);
                TextView txtNumOfCup = (TextView)viewDialog.findViewById(R.id.num_of_cup_progress);
                TextView txt_btnDone = (TextView)viewDialog.findViewById(R.id.close_button_water);
                TextView txtLiters = (TextView)viewDialog.findViewById(R.id.text_view_liters);

                numOfCup = cupGoal;
                txtNumOfCup.setText(numOfCup+"");
                progressBar.setProgress(numOfCup *1000);

                img_buttonPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(numOfCup < MAX_OF_CUP){
                            numOfCup +=1;
                            txtNumOfCup.setText(numOfCup+"");
                            txtLiters.setText(numOfCup* 0.25 + " liters");
                            ProgressBarAnimation progressBarAnimation = new ProgressBarAnimation(progressBar,
                                    numOfCup*1000-1000,numOfCup*1000);
                            progressBarAnimation.setDuration(700);
                            progressBar.startAnimation(progressBarAnimation);
                        }
                    }
                });

                img_buttonMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(numOfCup > 0){
                            numOfCup -=1;
                            txtNumOfCup.setText(numOfCup+"");
                            txtLiters.setText(numOfCup* 0.25 + " liters");
                            ProgressBarAnimation progressBarAnimation = new ProgressBarAnimation(progressBar,
                                    numOfCup*1000+1000,numOfCup*1000);
                            progressBarAnimation.setDuration(700);
                            progressBar.startAnimation(progressBarAnimation);
                        }
                    }
                });

                txt_btnDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        firestore.collection("users")
                                .document(userEmail)
                                .update("drink_goal",numOfCup*250 + "")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(WaterActivity.this, "Set new goal successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(WaterActivity.this, "Set new goal failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        docRef = firestore.collection("daily").
                                document("week-of-" + monday.toString()).
                                collection(today.toString()).
                                document(userEmail);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    if(task.getResult() != null){
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if(documentSnapshot.exists()){
                                            String result = documentSnapshot.getString("drink");
                                            if(result.equals("empty")){
                                                docRef.update("drink","0");
                                            }
                                        }
                                    }
                                }
                            }
                        });
                        loadData();
                        bottomSheetDialog.hide();
                    }
                });


                bottomSheetDialog.setContentView(viewDialog);
                bottomSheetDialog.show();
            }
        });

    }

}