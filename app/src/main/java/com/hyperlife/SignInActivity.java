package com.hyperlife;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 120;
    private EditText txtEmail, txtPassword;
    private Button btnSignIn, btnSignUp, btnForgot;
    private Button btnGoogle, btnFacebook;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String tempEmail = "tempEmail";
    private FirebaseFirestore firestore;
    private DocumentReference docRef;
    //    CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        addControls();
        addEvents();
    }


    @Override
    public void onBackPressed() {
        Intent INTENT = new Intent(Intent.ACTION_MAIN);
        INTENT.addCategory(Intent.CATEGORY_HOME);
        INTENT.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(INTENT);
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
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
                progressBar.setVisibility(View.VISIBLE);

            }
        });
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void signInWithGoogle() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnForgot = (Button) findViewById(R.id.btnForgot);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnFacebook = (Button) findViewById(R.id.btnFacebook);
        btnGoogle = (Button) findViewById(R.id.btGoogle);
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
         gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Exception exception = task.getException();
            if (task.isSuccessful()) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d("SigninActivity", "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                    SharedPreferences sharedPreferences = getSharedPreferences(
                            tempEmail, MODE_PRIVATE);

                    GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
                    if (acct != null) {
                        String googleEmail = acct.getEmail();
                        String userName = acct.getDisplayName();

                        firestore = FirebaseFirestore.getInstance();
                        docRef = firestore.collection("users").document(googleEmail);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Toast.makeText(SignInActivity.this, "Login Successfully !!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    } else {
                                        CreateUserOnFirebase(googleEmail, userName);
                                        Toast.makeText(SignInActivity.this, "Login Successfully !!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignInActivity.this, CompleteUserInfoActivity.class);
                                        startActivity(intent);
                                    }
                                    finish();
                                }
                            }
                        });


                        //set up shareRef
                        SharedPreferences.Editor editor;
                        editor = sharedPreferences.edit();
                        editor.putString("Email", googleEmail);
                        editor.apply();
                    }
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("SigninActivity", "Google sign in failed", e);
                }
            } else {
                Log.w("SigninActivity", exception.toString());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void CreateUserOnFirebase(String googleEmail, String userName) {
        firestore = FirebaseFirestore.getInstance();
        LocalDate today = LocalDate.now();

        // Save user data to firestore
        Map<String, Object> user = new HashMap<>();
        user.put("name", userName);
        user.put("email", googleEmail);
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
        firestore.collection("users").document(googleEmail)
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


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SigninActivity", "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SigninActivity", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
}