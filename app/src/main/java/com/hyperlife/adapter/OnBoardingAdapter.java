package com.hyperlife.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyperlife.R;
import com.hyperlife.model.OnBoardingItem;

import java.util.List;

public class OnBoardingAdapter extends RecyclerView.Adapter<OnBoardingAdapter.OnBoardingViewHolder> {
    private List<OnBoardingItem> onBoardingItems;

    public OnBoardingAdapter(List<OnBoardingItem> onBoardingItems) {
        this.onBoardingItems = onBoardingItems;
    }

    @NonNull
    @Override
    public OnBoardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnBoardingViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_onboarding,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnBoardingViewHolder holder, int position) {
        holder.setOnBoardingData(onBoardingItems.get(position));
    }

    @Override
    public int getItemCount() {
        return onBoardingItems.size();
    }

    class OnBoardingViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageOnBoarding;

        OnBoardingViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageOnBoarding = imageOnBoarding;
            imageOnBoarding = itemView.findViewById(R.id.imageOnBoarding);
        }


        void setOnBoardingData(OnBoardingItem onBoardingItem)
        {
            imageOnBoarding.setImageResource(onBoardingItem.getImage());
        }
    }
}
