package com.hyperlife.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hyperlife.MainActivity;
import com.hyperlife.R;
import com.hyperlife.SignInActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {

    private TextView mName, mId, mEmail, mDateOfBirth,mGender;
    ImageView mUserImage;
    private static final String tempEmail = "tempEmail";
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    private LinearLayout logout;
    private FirebaseAuth mAuth;


    public UserProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_user_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        mName = (TextView) rootview.findViewById(R.id.id_fullname);
        mEmail = (TextView) rootview.findViewById(R.id.id_email);
        mUserImage = rootview.findViewById(R.id.id_userimage);
        logout = rootview.findViewById(R.id.linearlogout);
        mDateOfBirth = rootview.findViewById(R.id.id_date_of_birth);
        mGender = rootview.findViewById(R.id.id_gender);



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                //
                /////
                Intent i = new Intent(getActivity(), SignInActivity.class);
                startActivity(i);
                getActivity().finish();
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });

        SharedPreferences sharedPreferences = this.getActivity().
                getSharedPreferences(tempEmail, Context.MODE_PRIVATE);
        String theTempEmail = sharedPreferences.getString("Email", "");

        mEmail.setText(theTempEmail);
        firestore = FirebaseFirestore.getInstance();

//        Runnable getUserInfoFromFirebase = new Runnable() {
//            @Override
//            public void run() {
//                docRef = firestore.collection("users").document(theTempEmail);
//                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            if (document != null) {
//                                String temp = document.getString("name");
//                                String tempBirth = document.getString("date_of_birth");
//                                String tempGender = document.getString("gender");
//                                mName.setText(temp);
//
//                                assert tempBirth != null;
//                                if(!tempBirth.equals("empty")){
//                                    mDateOfBirth.setText(tempBirth);
//                                } else {
//                                    mDateOfBirth.setText("Choose date of birth");
//                                }
//
//                                assert tempGender != null;
//                                if(!tempGender.equals("empty")){
//                                    mGender.setText(tempGender);
//                                }else {
//                                    mGender.setText("Choose Gender");
//                                }
//                            }
//                        }
//                    }
//                });
//            }
//        };
//
//        Thread backgroundThread = new Thread(getUserInfoFromFirebase);
//        backgroundThread.start();
        return rootview;
    }
}