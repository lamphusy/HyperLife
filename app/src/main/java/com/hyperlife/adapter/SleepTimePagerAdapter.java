package com.hyperlife.adapter;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hyperlife.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SleepTimePagerAdapter extends RecyclerView.Adapter<SleepTimePagerAdapter.ViewHolder> {
    private FirebaseFirestore firestore;
    private String userEmail;
    private String time_wake;
    public double period_sleep_time = 0;
    String []time_wake_split;
    double time_wake_real_number;
    LocalDate today;
    LocalDate monday;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public SleepTimePagerAdapter(String userEmail, String time_wake) {
        this.userEmail = userEmail;
        this.time_wake = time_wake;


        today = LocalDate.now();
        monday = today.with(previousOrSame(MONDAY));
        firestore = FirebaseFirestore.getInstance();
    }

    public void setTimeWake(String timeWake){
        this.time_wake = timeWake;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sleep_suggest_viewpaper,
                parent,false);
        return new SleepTimePagerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        double child_time =0, teen_time=0, adult_time=0;

        time_wake_split = time_wake.split(":");
        time_wake_real_number = Double.parseDouble(time_wake_split[0]) +
                Double.parseDouble(time_wake_split[1])/60;

//        firestore.collection("users").document(userEmail)
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                double update_time = 0;
//                String sleep_goal = task.getResult().getString("sleep_goal");
//                String []sleep_goal_2 = sleep_goal.split(" ");
//                sleep_goal = sleep_goal_2[0];
//                double sleep_goal_double = Double.parseDouble(sleep_goal);
//                if(time_wake_real_number < sleep_goal_double){
//                    update_time = time_wake_real_number + 24 - sleep_goal_double;
//                }else{
//                    update_time = time_wake_real_number - sleep_goal_double;
//                }
//
//                String []update_time_split = (update_time + "").split("\\.");
//                ArrayList<String>update_time_arr = new ArrayList<>(2);
//                String update_time_decimal = Math.round(Double.parseDouble("0."+update_time_split[1])*60) +"";
//                update_time_arr.add(update_time_split[0]);
//                if(update_time_decimal.length() == 1){
//                    update_time_arr.add("0" + update_time_decimal);
//                }else{
//                    update_time_arr.add(update_time_decimal);
//                }
//                setupFireBase(update_time_arr.get(0)+":"+update_time_arr.get(1));
//            }
//        });

        if(time_wake_real_number < 10.5 && time_wake_real_number >= 9){
            child_time = time_wake_real_number + 24 - 10.5;
            teen_time = time_wake_real_number - 9;
            adult_time = time_wake_real_number - 7.5;
        }else if (time_wake_real_number < 9 && time_wake_real_number >= 7.5){
            child_time = time_wake_real_number + 24 - 10.5;
            teen_time = time_wake_real_number + 24 - 9;
            adult_time = time_wake_real_number - 7.5;
        }else if(time_wake_real_number < 7.5){
            child_time = time_wake_real_number + 24 - 10.5;
            teen_time = time_wake_real_number + 24 - 9;
            adult_time = time_wake_real_number + 24 - 7.5;
        }else if (time_wake_real_number >= 10.5){
            child_time = time_wake_real_number - 10.5;
            teen_time = time_wake_real_number - 9;
            adult_time = time_wake_real_number - 7.5;
        }

        String []child_time_split = (child_time + "").split("\\.");
        String []teen_time_split = (teen_time + "").split("\\.");
        String []adult_time_split = (adult_time + "").split("\\.");

        ArrayList<String>child_time_arr = new ArrayList<>(2);
        ArrayList<String>teen_time_arr = new ArrayList<>(2);
        ArrayList<String>adult_time_arr = new ArrayList<>(2);

        child_time_arr.add(child_time_split[0]);
        teen_time_arr.add(teen_time_split[0]);
        adult_time_arr.add(adult_time_split[0]);

        String child_time_decimal = Math.round(Double.parseDouble("0."+child_time_split[1])*60) +"";
        String teen_time_decimal = Math.round(Double.parseDouble("0."+teen_time_split[1])*60) +"";
        String adult_time_decimal = Math.round(Double.parseDouble("0."+adult_time_split[1])*60) +"";

        if(child_time_decimal.length() == 1){
            child_time_arr.add("0" + child_time_decimal);
        }else{
            child_time_arr.add(child_time_decimal);
        }

        if(teen_time_decimal.length() == 1){
            teen_time_arr.add("0" + teen_time_decimal);
        }else{
            teen_time_arr.add(teen_time_decimal);
        }

        if(adult_time_decimal.length() == 1){
            adult_time_arr.add("0" + adult_time_decimal);
        }else{
            adult_time_arr.add(adult_time_decimal);
        }

        holder.childTime.setText("You should sleep at: "+ child_time_arr.get(0) +":"+ child_time_arr.get(1));
        holder.teenTime.setText("You should sleep at: "+ teen_time_arr.get(0) +":"+ teen_time_arr.get(1));
        holder.adultTime.setText("You should sleep at: "+ adult_time_arr.get(0) +":"+ adult_time_arr.get(1));

        holder.childConstaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.childImage.setVisibility(View.VISIBLE);
                holder.teenImage.setVisibility(View.GONE);
                holder.adultImage.setVisibility(View.GONE);
                holder.customImage.setVisibility(View.GONE);
                holder.customPicker.setVisibility(View.GONE);
                holder.customTime.setVisibility(View.GONE);
                period_sleep_time = 10.5;

                setupFireBase(holder.childNum.getText().toString()
                        ,holder.childTime.getText().toString().substring(21));

            }
        });
        holder.teenConstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.childImage.setVisibility(View.GONE);
                holder.teenImage.setVisibility(View.VISIBLE);
                holder.adultImage.setVisibility(View.GONE);
                holder.customImage.setVisibility(View.GONE);
                holder.customPicker.setVisibility(View.GONE);
                holder.customTime.setVisibility(View.GONE);
                period_sleep_time = 9;

                setupFireBase(holder.teenNum.getText().toString()
                        ,holder.teenTime.getText().toString().substring(21));
            }
        });
        holder.adultConstaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.childImage.setVisibility(View.GONE);
                holder.teenImage.setVisibility(View.GONE);
                holder.adultImage.setVisibility(View.VISIBLE);
                holder.customImage.setVisibility(View.GONE);
                holder.customPicker.setVisibility(View.GONE);
                holder.customTime.setVisibility(View.GONE);
                period_sleep_time = 7.5;

                setupFireBase(holder.adultNum.getText().toString()
                        ,holder.adultTime.getText().toString().substring(21));
            }
        });
        holder.customConstaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.childImage.setVisibility(View.GONE);
                holder.teenImage.setVisibility(View.GONE);
                holder.adultImage.setVisibility(View.GONE);
                holder.customImage.setVisibility(View.VISIBLE);
                holder.customPicker.setVisibility(View.VISIBLE);
                holder.customTime.setVisibility(View.VISIBLE);

                String[] sleep = new String[15];
                for(int i =1;i<=15;i++){
                    sleep[i-1] = i*1.5+"";
                }
                holder.customPicker.setMinValue(0);
                holder.customPicker.setMaxValue(14);
                holder.customPicker.setDisplayedValues(sleep);
                holder.customPicker.setValue(6);
                holder.customPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        int index = i1;
                        if(i1>= 15) index = i1%15;
                        else if(i1 < 0) index = (-i1)%15;
                        period_sleep_time = Double.parseDouble(numberPicker.getDisplayedValues()[i1]);
                        double custom_time = 0;
                        if(time_wake_real_number < period_sleep_time){
                            custom_time = time_wake_real_number + 24 - period_sleep_time;
                        }else{
                            custom_time = time_wake_real_number - period_sleep_time;
                        }
                        String []custom_time_split = (custom_time + "").split("\\.");

                        ArrayList<String> custom_time_arr = new ArrayList<>(2);
                        String custom_time_decimal = Math.round(Double.parseDouble("0."+custom_time_split[1])*60) +"";

                        custom_time_arr.add(custom_time_split[0]);
                        if(custom_time_decimal.length() == 1){
                            custom_time_arr.add("0"+custom_time_decimal);
                        }else{
                            custom_time_arr.add(custom_time_decimal);
                        }

                        holder.customTime.setText("You should sleep at: "+ custom_time_arr.get(0) + ":" + custom_time_arr.get(1));

                        setupFireBase(period_sleep_time+""
                                ,holder.customTime.getText().toString().substring(21));
                    }
                });

            }
        });
    }
    private void setupFireBase(String sleep_time_period, String time_to_sleep){
        firestore.collection("daily").
                document("week-of-" + monday.toString()).
                collection(today.toString()).
                document(userEmail)
                .update("sleep_time",time_to_sleep);

        firestore.collection("users").
                document(userEmail)
                .update("sleep_goal",sleep_time_period);

        firestore.collection("users").
                document(userEmail)
                .update("sleep_time",time_to_sleep);
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
    @Override
    public int getItemCount() {
        return 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        NumberPicker customPicker;

        ConstraintLayout childConstaint,teenConstraint,adultConstaint;
        LinearLayout customConstaint;
        TextView childTitle,teenTitle,adultTitle,customTitle
                ,childNum,teenNum,adultNum,customNum
                ,childText,teenText,adultText,customText
                ,childTime,teenTime,adultTime,customTime;
        ImageView childImage,teenImage,adultImage,customImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            childTime = (TextView) itemView.findViewById(R.id.txtChildSleepTime);
            teenTime = (TextView) itemView.findViewById(R.id.txtTeenSleepTime);
            adultTime = (TextView) itemView.findViewById(R.id.txtAdultSleepTime);
            customTime = (TextView) itemView.findViewById(R.id.txtCustomSleepTime);

            customPicker = (NumberPicker) itemView.findViewById(R.id.number_picker_sleep_time);

            childConstaint = (ConstraintLayout) itemView.findViewById(R.id.child_cons);
            teenConstraint = (ConstraintLayout) itemView.findViewById(R.id.teen_cons);
            adultConstaint = (ConstraintLayout) itemView.findViewById(R.id.adult_cons);
            customConstaint = (LinearLayout) itemView.findViewById(R.id.custom_container);

            childTitle = (TextView) itemView.findViewById(R.id.child_title);
            childNum = (TextView) itemView.findViewById(R.id.child_number);
            childText = (TextView) itemView.findViewById(R.id.child_text);

            teenTitle = (TextView) itemView.findViewById(R.id.teen_title);
            teenNum = (TextView) itemView.findViewById(R.id.teen_number);
            teenText = (TextView) itemView.findViewById(R.id.teen_text);

            adultTitle = (TextView) itemView.findViewById(R.id.adult_title);
            adultNum = (TextView) itemView.findViewById(R.id.adult_number);
            adultText = (TextView) itemView.findViewById(R.id.adult_text);

            customTitle = (TextView) itemView.findViewById(R.id.custom_title);

            childImage = (ImageView) itemView.findViewById(R.id.child_image);
            teenImage = (ImageView) itemView.findViewById(R.id.teen_image);
            adultImage = (ImageView) itemView.findViewById(R.id.adult_image);
            customImage = (ImageView) itemView.findViewById(R.id.custom_image);
        }
    }
}
