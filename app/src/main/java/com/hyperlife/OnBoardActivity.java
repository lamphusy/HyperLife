package com.hyperlife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.hyperlife.adapter.OnBoardingAdapter;
import com.hyperlife.model.OnBoardingItem;

import java.util.ArrayList;
import java.util.List;

public class OnBoardActivity extends AppCompatActivity {
    private OnBoardingAdapter onBoardingAdapter;
    private LinearLayout layoutOnBoardingIndicators;
    private MaterialButton buttonOnBoardingAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_board); layoutOnBoardingIndicators = findViewById(R.id.layoutOnBoardIndicator);
        buttonOnBoardingAction = findViewById(R.id.buttonOnBoard);
        setupOnboardingItems();
        ViewPager2 onBoardingViewPager = findViewById(R.id.onBoardViewPager);
        onBoardingViewPager.setAdapter(onBoardingAdapter);
        setupOnboardingIndicators();
        setCurrentOnboardingIndicator(0);
        onBoardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });
        buttonOnBoardingAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBoardingViewPager.getCurrentItem()+1 < onBoardingAdapter.getItemCount()){
                    onBoardingViewPager.setCurrentItem(onBoardingViewPager.getCurrentItem()+1);
                }else{
                    startActivity(new Intent(getApplicationContext(), SplashScreenActivity2.class));
                    finish();
                }
            }
        });
    }
    private void setupOnboardingItems(){
        List<OnBoardingItem> onBoardingItems = new ArrayList<>();
        //1
        OnBoardingItem itemFeature1 = new OnBoardingItem();
        itemFeature1.setImage(R.drawable.onboard1);
        //2
        OnBoardingItem itemFeature2 = new OnBoardingItem();
        itemFeature2.setImage(R.drawable.onboard2);
        //3
        OnBoardingItem itemFeature3 = new OnBoardingItem();
        itemFeature3.setImage(R.drawable.onboard3);
        //
        onBoardingItems.add(itemFeature1);
        onBoardingItems.add(itemFeature2);
        onBoardingItems.add(itemFeature3);

        onBoardingAdapter = new OnBoardingAdapter(onBoardingItems);

    }
    private void setupOnboardingIndicators(){
        ImageView[] indicators = new ImageView[onBoardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0 ,8,0);
        for(int i = 0;i < indicators.length;i++){
            indicators[i]=new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboard_indicate_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnBoardingIndicators.addView(indicators[i]);
        }
    }
    private  void  setCurrentOnboardingIndicator( int index){
        int childCount = layoutOnBoardingIndicators.getChildCount();
        for ( int i=0 ; i<childCount ; i++){
            ImageView imageView = (ImageView) layoutOnBoardingIndicators.getChildAt(i);
            if (i==index){
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboard_indicate_active)
                );
            }else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboard_indicate_inactive)
                );
            }
        }
        if (index==onBoardingAdapter.getItemCount()-1){
            buttonOnBoardingAction.setText("Start");
        }else {
            buttonOnBoardingAction.setText("Next");
        }
    }

}