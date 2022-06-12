package com.hyperlife;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hyperlife.adapter.SleepTimePagerAdapter;
import com.hyperlife.adapter.StepViewPagerAdapter;
import com.hyperlife.model.AlarmReceiver;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

public class SleepTime extends AppCompatActivity {

    FirebaseFirestore firestore;
    TextView txtLoading, txtAskTime, txtTimeWake, txtTimeSleep, txtDateTimeSleep;
    AppCompatButton btnSetWakeupTime;
    LinearLayout sleepTimeLinear, sleepTimeLinear2;
    ImageView imgBack, imgMoreOption;
    ViewPager2 viewPagerSleep;
    SleepTimePagerAdapter adapter;


    String userEmail;
    SharedPreferences sharedPreferences;
    DocumentReference docRef;
    String timeWakeUp = "";

    LocalDate today;
    LocalDate monday;

    int sleepHour, sleepMin;

    private int lastSelectedHour = -1;
    private int lastSelectedMinute = -1;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_time);

        today = LocalDate.now();
        monday = today.with(previousOrSame(MONDAY));

        firestore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("tempEmail",MODE_PRIVATE);
        userEmail = sharedPreferences.getString("Email","none");

        txtLoading = (TextView) findViewById(R.id.loading_title_sleep_time);
        txtAskTime = (TextView) findViewById(R.id.text_view_time_wake);
        txtTimeWake = (TextView) findViewById(R.id.time_to_wake);
        txtTimeSleep = (TextView)findViewById(R.id.time_to_sleep);
        btnSetWakeupTime = (AppCompatButton) findViewById(R.id.choose_button_wake_up_time);
        sleepTimeLinear = (LinearLayout)findViewById(R.id.sleep_time_linear);
        sleepTimeLinear2 = (LinearLayout) findViewById(R.id.sleep_time_linear_2);
        imgBack = (ImageView) findViewById(R.id.button_back_sleep_time);
        viewPagerSleep = (ViewPager2)findViewById(R.id.suggest_sleep_time);
        imgMoreOption = (ImageView) findViewById(R.id.more_menu_sleep_time);
        txtDateTimeSleep = (TextView)findViewById(R.id.date_time_sleep);


        backGroundThread();
        updateTimeWakeSleep();
        checkSetTimeWakeAlready();

        addEvents();





    }
    private void backGroundThread() {
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                LocalDate getToday = LocalDate.now();
                txtDateTimeSleep.setText(getToday.getDayOfWeek() +", "+ getToday.getMonth()+" "+getToday.getDayOfMonth());
            }
        });
        thread.start();
    }

    private void updateTimeWakeSleep() {
        docRef = firestore.collection("users").
                document(userEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String wake_up = task.getResult().getString("wake_up");
                String sleep_time = task.getResult().getString("sleep_time");

                firestore.collection("daily").
                        document("week-of-"+ monday.toString()).
                        collection(today.toString()).document(userEmail).
                        update("wake_up",wake_up);

                firestore.collection("daily").
                        document("week-of-"+ monday.toString()).
                        collection(today.toString()).document(userEmail).
                        update("sleep_time",sleep_time);
            }
        });
    }

    private void addEvents() {

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SleepTime.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        txtTimeWake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWakeUpTime();
            }
        });
        btnSetWakeupTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setWakeUpTime();
            }
        });

        imgMoreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View viewDialog = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.layout_bottom_sheet_dialog_sleep_time, null);
                BottomSheetDialog bottomSheet = new BottomSheetDialog(SleepTime.this);

                Button btnSetAlarmWake = viewDialog.findViewById(R.id.btn_alarm_wake_up);

               btnSetAlarmWake.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setAlarmClockToWakeUp();
                    }
                });


                bottomSheet.setContentView(viewDialog);
                bottomSheet.show();
            }
        });

        txtTimeSleep.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                SetUpSleepNotification();

            }
        });
    }

    private void setAlarmClockToWakeUp(){
        String[]time_wake_split = timeWakeUp.split(":");
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(time_wake_split[0]));
        intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(time_wake_split[1]));
        intent.putExtra(AlarmClock.EXTRA_MESSAGE,"Time to wake up. Have a nice day!");
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "There is no app at all", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Hyperlife";
            String description = "Remind sleep";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("notify_sleep",
                    name,importance);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
    private void SetUpSleepNotification() {
        //sleep Alert

//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(SleepTime.this, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(SleepTime.this, 1, intent, 0);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        String time_sleep_love_you = txtTimeSleep.getText().toString();
        String[] time_sleep_love_you_split = time_sleep_love_you.split(":");


        Calendar c = Calendar.getInstance();
        long timeCurrent = System.currentTimeMillis();

        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_sleep_love_you_split[0]));
        c.set(Calendar.MINUTE, Integer.parseInt(time_sleep_love_you_split[1]));
        c.set(Calendar.SECOND,0);


        createNotificationChannel();
        Intent intent = new Intent(SleepTime.this,AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SleepTime.this, 0, intent, PendingIntent.FLAG_MUTABLE);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);


        long timeAtButtonClick = c.getTimeInMillis();
        if(timeAtButtonClick < timeCurrent){
            timeAtButtonClick += 1000* 60 * 60 * 24;
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,timeAtButtonClick, AlarmManager.INTERVAL_DAY,
                pendingIntent);

    }
    private void checkSetTimeWakeAlready(){

        docRef = firestore.collection("users").
              document(userEmail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(!task.getResult().getString("wake_time").equals("empty")){
                    timeWakeUp = task.getResult().getString("wake_time");
                    adapter = new SleepTimePagerAdapter(userEmail, task.getResult().getString("wake_time"));
                    viewPagerSleep.setAdapter(adapter);
                    setupUI(task.getResult().getString("wake_time"));
                }
            }
        });

    }

    private void setupUI(String time){
        if(!userEmail.equals("none") && time.length()> 0){
            txtLoading.setVisibility(View.GONE);
            btnSetWakeupTime.setVisibility(View.GONE);
            txtAskTime.setVisibility(View.VISIBLE);
            sleepTimeLinear.setVisibility(View.VISIBLE);
            sleepTimeLinear2.setVisibility(View.VISIBLE);
            txtTimeWake.setText(time);

            firestore.collection("users").document(userEmail)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            String sleep_time = task.getResult().getString("sleep_time");
                            txtTimeSleep.setText(sleep_time);
                            String []sleep_time_split_2 = sleep_time.split(":");
                            sleepHour = Integer.parseInt(sleep_time_split_2[0]);
                            sleepMin = Integer.parseInt(sleep_time_split_2[1]);
                        }
                    });

        }else if (!userEmail.equals("none")){
            txtLoading.setVisibility(View.GONE);
            txtAskTime.setVisibility(View.GONE);
            sleepTimeLinear.setVisibility(View.GONE);
            sleepTimeLinear2.setVisibility(View.GONE);
            btnSetWakeupTime.setVisibility(View.VISIBLE);
        }else{
            txtLoading.setVisibility(View.VISIBLE);
            txtAskTime.setVisibility(View.GONE);
            sleepTimeLinear.setVisibility(View.GONE);
            btnSetWakeupTime.setVisibility(View.GONE);
            sleepTimeLinear2.setVisibility(View.GONE);
        }
    }
    private void updateSleepTime(String time_wake){
        String[]time_wake_split = time_wake.split(":");
        double time_wake_real_number = Double.parseDouble(time_wake_split[0]) +
                Double.parseDouble(time_wake_split[1])/60;

        firestore.collection("users").document(userEmail)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                double update_time = 0;
                String sleep_goal = task.getResult().getString("sleep_goal");
                String []sleep_goal_2 = sleep_goal.split(" ");
                if(sleep_goal_2[0].equals("empty")){
                    sleep_goal_2[0] = "8";
                }
                sleep_goal = sleep_goal_2[0];
                double sleep_goal_double = Double.parseDouble(sleep_goal);
                if(time_wake_real_number < sleep_goal_double){
                    update_time = time_wake_real_number + 24 - sleep_goal_double;
                }else{
                    update_time = time_wake_real_number - sleep_goal_double;
                }

                String []update_time_split = (update_time + "").split("\\.");
                ArrayList<String>update_time_arr = new ArrayList<>(2);
                String update_time_decimal = Math.round(Double.parseDouble("0."+update_time_split[1])*60) +"";
                update_time_arr.add(update_time_split[0]);
                if(update_time_decimal.length() == 1){
                    update_time_arr.add("0" + update_time_decimal);
                }else{
                    update_time_arr.add(update_time_decimal);
                }
                setupFireBase(update_time_arr.get(0)+":"+update_time_arr.get(1));

            }
        });
    }
    private void setupFireBase(String time_to_sleep){
        firestore.collection("daily").
                document("week-of-" + monday.toString()).
                collection(today.toString()).
                document(userEmail)
                .update("sleep_time",time_to_sleep);

        firestore.collection("users").
                document(userEmail)
                .update("sleep_time",time_to_sleep);
    }
    private void setWakeUpTime(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                SleepTime.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        lastSelectedHour = hourOfDay;
                        lastSelectedMinute = minute;

                        String lastSelectedHourFormat = (hourOfDay <= 9) ? "0"+hourOfDay : hourOfDay+"";
                        String lastSelectedMinuteFormat = (minute <= 9) ? "0"+minute : minute+"";

                        docRef = firestore.collection("daily").
                                document("week-of-"+ monday.toString()).
                                collection(today.toString()).document(userEmail);



                        docRef.update("wake_time",lastSelectedHourFormat+":"+lastSelectedMinuteFormat)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(SleepTime.this, "Cập nhật dữ liệu thành công", Toast.LENGTH_SHORT).show();
                                        firestore.collection("users").document(userEmail)
                                                .update("wake_time",lastSelectedHourFormat+":"+lastSelectedMinuteFormat)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                updateSleepTime(lastSelectedHourFormat+":"+lastSelectedMinuteFormat);
                                                checkSetTimeWakeAlready();
                                            }
                                        });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SleepTime.this, "Cập nhật dữ liệu thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                });


                    }
                }, 12, 0, true
        );
        timePickerDialog.updateTime(lastSelectedHour, lastSelectedMinute);
        timePickerDialog.show();
    }

    private void setSleepTime(){

    }
}