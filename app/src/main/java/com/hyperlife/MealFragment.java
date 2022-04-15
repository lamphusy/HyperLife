package com.hyperlife;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MealFragment extends Fragment {
    private Button mOpenMealInput;
    private ConstraintLayout expandableView;
    private CardView mealCardview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_meal, container, false);
        mOpenMealInput = rootview.findViewById(R.id.btOpenMealInput);
        expandableView = rootview.findViewById(R.id.inputmeallayout);
        mealCardview = rootview.findViewById(R.id.MealCardView);

        mOpenMealInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (expandableView.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(mealCardview, new AutoTransition());
                    expandableView.setVisibility(View.VISIBLE);

                } else {
                    TransitionManager.beginDelayedTransition(mealCardview, new AutoTransition());
                    expandableView.setVisibility(View.GONE);
                }
            }
        });
        // Inflate the layout for this fragment
        return rootview;
    }
}