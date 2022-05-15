package com.hyperlife.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hyperlife.R;

public class StepViewPagerAdapter extends RecyclerView.Adapter<StepViewPagerAdapter.ViewHolder> {
    private String step_goal;
    private String userEmail;
    public int step_goal_selected = 0;
    private FirebaseFirestore firestore;

    public StepViewPagerAdapter(String step_goal, String userEmail) {
        this.step_goal = step_goal;
        this.userEmail = userEmail;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step_count_setupgoal_viewpager,
                parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position == 0){
            holder.numberPicker.setVisibility(View.GONE);
            holder.linearPicker.setVisibility(View.VISIBLE);

            holder.baConstaint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.baImage.setVisibility(View.VISIBLE);
                    holder.kfImage.setVisibility(View.GONE);
                    holder.bmImage.setVisibility(View.GONE);
                    holder.lwImage.setVisibility(View.GONE);
                    step_goal_selected = 2500;

                }
            });
            holder.kfConstraint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.baImage.setVisibility(View.GONE);
                    holder.kfImage.setVisibility(View.VISIBLE);
                    holder.bmImage.setVisibility(View.GONE);
                    holder.lwImage.setVisibility(View.GONE);
                    step_goal_selected = 5000;

                }
            });
            holder.bmConstaint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.baImage.setVisibility(View.GONE);
                    holder.kfImage.setVisibility(View.GONE);
                    holder.bmImage.setVisibility(View.VISIBLE);
                    holder.lwImage.setVisibility(View.GONE);
                    step_goal_selected = 8000;

                }
            });
            holder.lwConstaint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.baImage.setVisibility(View.GONE);
                    holder.kfImage.setVisibility(View.GONE);
                    holder.bmImage.setVisibility(View.GONE);
                    holder.lwImage.setVisibility(View.VISIBLE);
                    step_goal_selected = 15000;

                }
            });

        }else{
            holder.numberPicker.setVisibility(View.VISIBLE);
            holder.linearPicker.setVisibility(View.GONE);

            String[] steps = new String[50];
            for(int i =1;i<=50;i++){
                steps[i-1] = i*500+"";
            }
            holder.numberPicker.setMinValue(0);
            holder.numberPicker.setMaxValue(49);
            holder.numberPicker.setDisplayedValues(steps);
            holder.numberPicker.setValue(500);
            holder.numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    int index = i1;
                    if(i1> 49) index = i1%50;
                    else if(i1 < 0) index = (-i1)%50;
                    step_goal_selected = Integer.parseInt(numberPicker.getDisplayedValues()[i1]);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        NumberPicker numberPicker;
        LinearLayout linearPicker;
        ConstraintLayout baConstaint,kfConstraint,bmConstaint,lwConstaint;
        TextView baTitle,kfTitle,bmTitle,lwTitle
                ,baNum,kfNum,bmNum,lwNum
                ,baText,kfText,bmText,lwText;
        ImageView baImage,kfImage,bmImage,lwImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearPicker = (LinearLayout) itemView.findViewById(R.id.linear_picker_stepcount);
            numberPicker = (NumberPicker) itemView.findViewById(R.id.number_picker_stepcount);

            baConstaint = (ConstraintLayout) itemView.findViewById(R.id.ba_cons_background);
            kfConstraint = (ConstraintLayout) itemView.findViewById(R.id.kf_cons_background);
            bmConstaint = (ConstraintLayout) itemView.findViewById(R.id.bm_cons_background);
            lwConstaint = (ConstraintLayout) itemView.findViewById(R.id.lw_cons_background);

            baTitle = (TextView) itemView.findViewById(R.id.ba_title);
            baNum = (TextView) itemView.findViewById(R.id.ba_number);
            baText = (TextView) itemView.findViewById(R.id.ba_text);

            kfTitle = (TextView) itemView.findViewById(R.id.kf_title);
            kfNum = (TextView) itemView.findViewById(R.id.kf_number);
            kfText = (TextView) itemView.findViewById(R.id.kf_text);

            bmTitle = (TextView) itemView.findViewById(R.id.bm_title);
            bmNum = (TextView) itemView.findViewById(R.id.bm_number);
            bmText = (TextView) itemView.findViewById(R.id.bm_text);

            lwTitle = (TextView) itemView.findViewById(R.id.lw_title);
            lwNum = (TextView) itemView.findViewById(R.id.lw_number);
            lwText   = (TextView) itemView.findViewById(R.id.lw_text);

            baImage = (ImageView) itemView.findViewById(R.id.ba_image);
            kfImage = (ImageView) itemView.findViewById(R.id.kf_image);
            bmImage = (ImageView) itemView.findViewById(R.id.bm_image);
            lwImage = (ImageView) itemView.findViewById(R.id.lw_image);
        }

    }
}
