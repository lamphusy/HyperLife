package com.hyperlife.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hyperlife.R;

import java.util.ArrayList;

import javax.sql.DataSource;

public class AnimExerAdapter extends BaseAdapter {
    private ArrayList<String> title, textDetail, videoUri;
    private Context context;
    private View tempView;
    private int resource;
    private Holder holder;
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private String[] exerciseContainer;

    public AnimExerAdapter(Context context, int resource, ArrayList<String> title, ArrayList<String> textDetail, ArrayList<String> videoUri) {
        super();
        this.context = context;
        this.title = title;
        this.textDetail = textDetail;
        this.videoUri = videoUri;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return title.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        tempView = inflater.inflate(resource, viewGroup, false);

        holder = new AnimExerAdapter.Holder();
        holder.exerciseTitle = (TextView) tempView.findViewById(R.id.exercises_workout_title);
        holder.exerciseText = (TextView) tempView.findViewById(R.id.exercises_workout_duration);
        holder.exerciseGif = (ImageView) tempView.findViewById(R.id.exercises_video);
        holder.frameLayout = (FrameLayout) tempView.findViewById(R.id.exercises_video_frame);
        holder.loadingIcon = (ImageView) tempView.findViewById(R.id.loading_image_list_data);

        holder.exerciseTitle.setText(title.get(i));
        holder.exerciseText.setText(textDetail.get(i));

        ViewTarget<ImageView, Drawable> into = Glide.with(context)
                .load(videoUri.get(i))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        holder.exerciseGif.setVisibility(View.VISIBLE);
                        holder.loadingIcon.setVisibility(View.GONE);
                        return false;

                    }


                })
                .into(holder.exerciseGif);


        return view;
    }

    public class Holder {
        TextView exerciseTitle, exerciseText;
        ImageView exerciseGif, loadingIcon;
        FrameLayout frameLayout;
    }
}
