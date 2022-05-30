package com.hyperlife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import at.favre.lib.crypto.bcrypt.BCrypt;

public class ChangePassWordActivity extends AppCompatActivity {
    TextInputEditText txtOldPassword, txtNewPassword, txtConfirmPassword;
    TextView txtBackToHome;
    Button btnChangePassword;
    String oldPassword, newPassword, confirmPassword;
    FirebaseFirestore firestore;
    private static final String tempEmail = "tempEmail";
    private String password;
    SharedPreferences sharedPreferences;
    String theTempEmail;
    private DocumentReference docRef;
    private Log Tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass_word);
        addControls();
        addEvents();
    }

    private void addEvents() {
        ChangePassword();
    }

    private void ChangePassword() {
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!oldPassword.isEmpty() && !newPassword.isEmpty() && !confirmPassword.isEmpty()) {
                    DocumentReference docRef = firestore.collection("Users").document(theTempEmail);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    password = document.getString("password");
                                    if (BCrypt.verifyer().verify(oldPassword.toCharArray(), password).verified) {
                                        if (newPassword.equals(confirmPassword)) {
                                            docRef.update("password", BCrypt.withDefaults().hashToString(12, newPassword.toCharArray()));
                                        }
                                    }
                                } else {
//                        Log.d(TAG, "No such document");
                                }
                            } else {
//                    Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });


                }
            }
        });

    }

    private void addControls() {


        sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        theTempEmail = sharedPreferences.getString("Email", "");
        firestore = FirebaseFirestore.getInstance();
        txtOldPassword = (TextInputEditText) findViewById(R.id.oldPassword);
        txtNewPassword = (TextInputEditText) findViewById(R.id.newPassword);
        txtConfirmPassword = (TextInputEditText) findViewById(R.id.confirmPassword);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        txtBackToHome = (TextView) findViewById(R.id.txtBackToHome);

        oldPassword = txtOldPassword.getText().toString();
        newPassword = txtNewPassword.getText().toString();
        confirmPassword = txtConfirmPassword.getText().toString();
    }
}