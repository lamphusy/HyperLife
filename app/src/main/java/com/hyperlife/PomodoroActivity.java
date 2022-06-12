package com.hyperlife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hyperlife.adapter.StepViewPagerAdapter;

public class PomodoroActivity extends AppCompatActivity {

    TextView timerTextView;
    SeekBar timerSeekBar;
    Boolean counterIsActive = false;
    Button goButton,btnSkipTimerCount,btnResetTime;
    CountDownTimer countDownTimer;
    ImageView button_backtohomefrag_pomodoro;

    TextView pomodoroTitle;
    TextView relaxTitle;
    ConstraintLayout tabAnimationView;
    ProgressBar timerProgressBar;

    int selectTab=0;

    public void resetTimer(){
        timerTextView.setText("30:00");
        timerSeekBar.setProgress(30*60);
        timerSeekBar.setEnabled(true);
        countDownTimer.cancel();
        goButton.setText("START");
        counterIsActive = false;
        timerProgressBar.setMax(30*60);
        timerProgressBar.setProgress(30*60);
        btnSkipTimerCount.setVisibility(View.GONE);

    }
    public void relaxResetTimer(){
        timerTextView.setText("5:00");
        timerSeekBar.setProgress(5*60);
        timerSeekBar.setEnabled(true);
        countDownTimer.cancel();
        goButton.setText("START");
        counterIsActive = false;
        timerProgressBar.setMax(5*60);
        timerProgressBar.setProgress(5*60);
        btnSkipTimerCount.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        button_backtohomefrag_pomodoro = findViewById(R.id.button_backtohomefrag_pomodoro);

        timerSeekBar = findViewById(R.id.timerSeekBar);
        timerTextView = findViewById(R.id.countdownTextView);
        goButton = findViewById(R.id.goButton);
        btnSkipTimerCount = findViewById(R.id.btnSkipTimerCount);
        btnResetTime = findViewById(R.id.btnResetTime);

        tabAnimationView = (ConstraintLayout) findViewById(R.id.tab_animation_view_pomodoro);
        pomodoroTitle = (TextView) findViewById(R.id.pomodoro_title);
        relaxTitle = (TextView) findViewById(R.id.relax_title);
        timerProgressBar = findViewById(R.id.progressbar_timer_count);


        addEvents();
        timerSeekBar.setMax(60*60);
        timerSeekBar.setProgress(30*60);

        timerProgressBar.setMax(30*60);
        timerProgressBar.setProgress(30*60);

        timerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setTimer(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void addEvents() {

        pomodoroTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relaxTitle.setTextColor(Color.parseColor("#7E7E7E"));
                pomodoroTitle.setTextColor(getResources().getColor(R.color.black));
                tabAnimationView.animate().x(0).setDuration(200);
                selectTab = 0;
                resetTimer();
            }
        });

        relaxTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = relaxTitle.getWidth();
                relaxTitle.setTextColor(getResources().getColor(R.color.black));
                pomodoroTitle.setTextColor(Color.parseColor("#7E7E7E") );
                tabAnimationView.animate().x(size).setDuration(200);
                selectTab = 1;
                relaxResetTimer();

            }
        });

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countDownTimer != null)
                    countDownTimer.cancel();
                buttonClicked();
            }
        });


        btnSkipTimerCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.onFinish();
            }
        });

        button_backtohomefrag_pomodoro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivity = new Intent(PomodoroActivity.this,MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        });

        btnResetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectTab ==0){


                    resetTimer();
                }else{


                    relaxResetTimer();
                }

            }
        });

        countDownTimer = new CountDownTimer(timerProgressBar.getProgress() * 1000 + 100, 1000) {
            @Override
            public void onTick(long l) {
                updateTimer((int)l/1000);
                if(counterIsActive== false){
                    countDownTimer.cancel();
                }
            }

            @Override
            public void onFinish() {
                MediaPlayer mplayer = MediaPlayer.create(getApplicationContext(), R.raw.ringbreak);
                mplayer.start();

                if(selectTab == 0){
                    int size = relaxTitle.getWidth();
                    relaxTitle.setTextColor(getResources().getColor(R.color.black));
                    pomodoroTitle.setTextColor(Color.parseColor("#7E7E7E") );
                    tabAnimationView.animate().x(size).setDuration(200);
                    selectTab = 1;
                    relaxResetTimer();
                }else{
                    relaxTitle.setTextColor(Color.parseColor("#7E7E7E"));
                    pomodoroTitle.setTextColor(getResources().getColor(R.color.black));
                    tabAnimationView.animate().x(0).setDuration(200);
                    selectTab = 0;
                    resetTimer();
                }
            }
        };
    }

    public void setTimer(int secondsLeft){
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - (minutes * 60);
        String secondString = Integer.toString(seconds);
        if(seconds <=9){
            secondString = "0" + secondString;
        }
        timerTextView.setText(Integer.toString(minutes) + ":"+secondString);
        timerProgressBar.setMax(secondsLeft);
        timerProgressBar.setProgress(secondsLeft);
    }


    public void updateTimer(int secondsLeft){
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - (minutes * 60);
        String secondString = Integer.toString(seconds);
        if(seconds <=9){
            secondString = "0" + secondString;
        }
        timerTextView.setText(Integer.toString(minutes) + ":"+secondString);
        timerProgressBar.setProgress(secondsLeft);
    }

    public void buttonClicked() {
        if(counterIsActive){
            counterIsActive = false;
            goButton.setText("START");
            btnSkipTimerCount.setVisibility(View.GONE);

        }
        else{
            btnSkipTimerCount.setVisibility(View.VISIBLE);

            counterIsActive = true;
            timerSeekBar.setEnabled(false);

            goButton.setText("STOP");
            countDownTimer = new CountDownTimer(timerProgressBar.getProgress() * 1000 + 100, 1000) {
                @Override
                public void onTick(long l) {
                    updateTimer((int)l/1000);
                    if(counterIsActive== false){
                        countDownTimer.cancel();
                    }
                }

                @Override
                public void onFinish() {
                    MediaPlayer mplayer = MediaPlayer.create(getApplicationContext(), R.raw.ringbreak);
                    mplayer.start();

                    if(selectTab == 0){
                        int size = relaxTitle.getWidth();
                        relaxTitle.setTextColor(getResources().getColor(R.color.black));
                        pomodoroTitle.setTextColor(Color.parseColor("#7E7E7E") );
                        tabAnimationView.animate().x(size).setDuration(200);
                        selectTab = 1;
                        relaxResetTimer();
                    }else{
                        relaxTitle.setTextColor(Color.parseColor("#7E7E7E"));
                        pomodoroTitle.setTextColor(getResources().getColor(R.color.black));
                        tabAnimationView.animate().x(0).setDuration(200);
                        selectTab = 0;
                        resetTimer();
                    }
                }
            }.start();
        }
    }
}