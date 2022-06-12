package com.hyperlife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import at.favre.lib.crypto.bcrypt.BCrypt;

public class ChangePassWordActivity extends AppCompatActivity {
    TextInputEditText txtOldPassword, txtNewPassword, txtConfirmPassword;
    private FirebaseAuth mAuth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
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
    FirebaseUser user;

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
                oldPassword = txtOldPassword.getText().toString();
                newPassword = txtNewPassword.getText().toString();
                confirmPassword = txtConfirmPassword.getText().toString();
                if (!oldPassword.isEmpty() && !newPassword.isEmpty() && !confirmPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), theTempEmail, Toast.LENGTH_SHORT).show();

                    DocumentReference docRef = firestore.collection("users").document(theTempEmail);
                    Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();

                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

                                if (document.exists()) {
                                    password = document.getString("password");
                                    Toast.makeText(getApplicationContext(), password, Toast.LENGTH_SHORT).show();

                                    BCrypt.Result result = BCrypt.verifyer().verify(oldPassword.toCharArray(), password);
                                    if (result.verified) {
                                        Log.d("LOGGER", "BCrypt verified");

                                        if (newPassword.equals(confirmPassword)) {
                                            AuthCredential credential = EmailAuthProvider
                                                    .getCredential(theTempEmail, oldPassword);
                                            user.reauthenticate(credential)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d("LOGGER", "User re-authenticated.");
                                                                user.updatePassword(confirmPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(getApplicationContext(), "Password authen changed", Toast.LENGTH_SHORT).show();
                                                                            docRef.update("password", BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Toast.makeText(getApplicationContext(), "Password firestore changed", Toast.LENGTH_SHORT).show();
                                                                                        logOut();
                                                                                    }
                                                                                }
                                                                            });
                                                                        } else {
                                                                            Log.d("LOGGER", "Error when update password.");
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                Log.d("LOGGER", "User re-authenticate failed.");

                                                            }
                                                        }
                                                    });

//                                            String email = user.getEmail();
//                                            Log.d("LOGGER", email);
//                                            if (email != null) {
//                                                docRef.update("password", BCrypt.withDefaults().hashToString(12, newPassword.toCharArray()));
                                            Toast.makeText(getApplicationContext(), password, Toast.LENGTH_SHORT).show();
//                                            }
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

    private void logOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
        mGoogleSignInClient.signOut();

        Intent i = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(i);
    }

    private void addControls() {
        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .build();
        sharedPreferences = getSharedPreferences(tempEmail, MODE_PRIVATE);
        theTempEmail = sharedPreferences.getString("Email", "");
        firestore = FirebaseFirestore.getInstance();
        txtOldPassword = (TextInputEditText) findViewById(R.id.oldPassword);
        txtNewPassword = (TextInputEditText) findViewById(R.id.newPassword);
        txtConfirmPassword = (TextInputEditText) findViewById(R.id.confirmPassword);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        txtBackToHome = (TextView) findViewById(R.id.txtBackToHome);


        user = FirebaseAuth.getInstance().getCurrentUser();
    }
}