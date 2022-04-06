package com.hyperlife;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {
    private EditText txtEmail, txtPassword;
    private Button btnSignIn, btnSignUp, btnForgot;
    private Button btnGoogle, btnFacebook;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String tempEmail = "tempEmail";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        addControls();
        addEvents();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addEvents() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                             SignIn();
                                             progressBar.setVisibility(View.VISIBLE);
                                             new Handler().postDelayed(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     progressBar.setVisibility(View.INVISIBLE);
                                                 }
                                             }, 4000);
                                         }
                                     }
        );
        SignUp();
    }

    private void SignUp() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createUserOnFirebase(String userEmail, String userName) {
        //Set up firestore
        db = FirebaseFirestore.getInstance();
        LocalDate today = LocalDate.now();

        // Save user data to firestore
        Map<String, Object> user = new HashMap<>();
        user.put("name", userName);
        user.put("email", userEmail);
        user.put("gender", "empty");
        user.put("date_of_birth", "empty");
        user.put("join_date", today.toString());
        user.put("weight", "empty");
        user.put("height", "empty");
        user.put("step_goal", "empty");
        user.put("drink_goal", "empty");
        user.put("calories_burn_goal", "empty");
        user.put("sleep_goal", "empty");
        user.put("on_screen_goal", "empty");
        user.put("recent_workout", "empty");
        user.put("time_to_sleep", "empty");
        db.collection("users").document(userEmail)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignInActivity.this, "Fail to save data to Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void SignIn() {
        String email = txtEmail.getText().toString();
        String pass = txtPassword.getText().toString();

        if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (!pass.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                SharedPreferences sharedPreferences = getSharedPreferences(
                                        tempEmail, MODE_PRIVATE);

                                Toast.makeText(SignInActivity.this, "Login Successfully !!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);

                                SharedPreferences.Editor editor;
                                editor = sharedPreferences.edit();
                                editor.putString("Email", email);
                                editor.apply();
                                startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignInActivity.this, "Login Fail ! Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                txtPassword.setError("Your Password must not empty");
            }
        } else if (email.isEmpty()) {
            txtEmail.setError("Your email must not empty");
        } else {
            txtEmail.setError("Please enter correct email");
        }
    }

    private void addControls() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        txtEmail = (EditText) findViewById(R.id.et_email_signin);
        txtPassword = (EditText) findViewById(R.id.et_password_signin);
        btnSignIn = (Button) findViewById(R.id.btnSignin);
        btnSignUp = (Button) findViewById(R.id.jumptosignup);
        btnForgot = (Button) findViewById(R.id.forgotpass);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
    }
}